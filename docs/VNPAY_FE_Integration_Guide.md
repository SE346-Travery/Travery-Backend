# VNPAY Payment Integration Guide (Flutter)

> Tài liệu này hướng dẫn Flutter FE tích hợp luồng thanh toán VNPAY thông qua Travery Backend API.

## 2. Packages cần thiết

```yaml
# pubspec.yaml
dependencies:
  url_launcher: ^6.3.0 # Mở VNPAY trên browser ngoài
  app_links: ^6.3.0 # Nhận deeplink từ OS
  dio: ^5.7.0 # HTTP client
```

---

## 3. Bước 1 — Tạo Booking và lấy Payment URL

### Endpoint

```
POST /api/v1/tour-instances/{instanceId}/bookings
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Body

```json
{
  "adultCount": 2,
  "childCount": 1,
  "specialRequests": "Yêu cầu đặc biệt (tùy chọn)",
  "paymentMethod": "VNPAY",
  "members": [
    {
      "fullName": "Nguyễn Văn A",
      "identityNumber": "012345678901",
      "dateOfBirth": "1990-01-15",
      "memberType": "ADULT"
    },
    {
      "fullName": "Nguyễn Văn C",
      "identityNumber": "",
      "dateOfBirth": "2015-03-10",
      "memberType": "CHILD"
    }
  ]
}
```

> **Lưu ý `ipAddress`**: FE **không cần gửi** IP. Backend tự detect từ request headers (`X-Forwarded-For`) qua `RequestUtil`.

### Response (201 Created)

```json
{
  "httpStatus": 201,
  "message": "Tour booking created successfully",
  "data": {
    "id": "58a669d9-0c0d-4bf8-9793-f37354358673",
    "status": "PENDING",
    "totalPrice": 5000000.0,
    "paymentDeadline": "2026-05-22T16:34:23",
    "tourName": "Sắc Màu Tây Bắc: Khám Phá Sapa Mù Sương",
    "payment": {
      "transactionId": "43b4fa1c-493f-4e55-bf76-e188441e00a8",
      "amount": 5000000.0,
      "paymentUrl": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?...",
      "expiresAt": "2026-05-22T16:34:23"
    }
  }
}
```

### Dữ liệu FE cần lưu lại

| Field                        | Mô tả     | Dùng để            |
| ---------------------------- | --------- | ------------------ |
| `data.id`                    | bookingId | Poll trạng thái    |
| `data.payment.transactionId` | txnRef    | Đối chiếu deeplink |
| `data.payment.paymentUrl`    | URL VNPAY | Mở browser         |
| `data.payment.expiresAt`     | Hết hạn   | Đếm ngược          |

---

## 4. Bước 2 — Mở VNPAY Payment URL

```dart
import 'package:url_launcher/url_launcher.dart';

Future<void> openVnPayPayment(String paymentUrl) async {
  final uri = Uri.parse(paymentUrl);
  if (await canLaunchUrl(uri)) {
    await launchUrl(
      uri,
      mode: LaunchMode.externalApplication, // Mở browser ngoài
    );
  } else {
    throw Exception('Không thể mở trang thanh toán');
  }
}
```

> ⚠️ **Quan trọng**: `paymentUrl` có thời hạn **15 phút**. Sau đó phải tạo lại qua `POST /api/v1/bookings/{bookingId}/payments`.

---

## 5. Bước 3 — Cấu hình và xử lý Deep Link

### Luồng Return URL (quan trọng)

```
VNPAY Gateway
    │
    │  ① Redirect về Return URL của BE (không phải app)
    ▼
https://{ngrok}/api/v1/payments/vnpay-return?vnp_TxnRef=xxx&vnp_ResponseCode=00&...
    │
    │  ② BE verify checksum, build deeplink, trả về HTTP 302
    ▼
travery://payment-result?txnRef=xxx&status=success&responseCode=00
    │
    │  ③ Mobile OS bắt deeplink, mở Flutter app
    ▼
App xử lý (xem Bước 4)
```

> FE **không cần** và **không nên** gọi thẳng `/vnpay-return`. BE tự xử lý và redirect về deeplink.

### Cấu hình Deep Link — Android

**`android/app/src/main/AndroidManifest.xml`**:

```xml
<activity ...>
  <!-- Deep link scheme cho VNPAY return -->
  <intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data
      android:scheme="travery"
      android:host="payment-result" />
  </intent-filter>
</activity>
```

### Cấu hình Deep Link — iOS

**`ios/Runner/Info.plist`**:

```xml
<key>CFBundleURLTypes</key>
<array>
  <dict>
    <key>CFBundleURLSchemes</key>
    <array>
      <string>travery</string>
    </array>
    <key>CFBundleURLName</key>
    <string>com.travery.app</string>
  </dict>
</array>
```

### Lắng nghe Deep Link trong Flutter

```dart
import 'package:app_links/app_links.dart';

class DeepLinkService {
  final _appLinks = AppLinks();
  StreamSubscription? _linkSubscription;

  void init() {
    // Lắng nghe deeplink khi app đang chạy (foreground/background)
    _linkSubscription = _appLinks.uriLinkStream.listen((uri) {
      if (uri.scheme == 'travery' && uri.host == 'payment-result') {
        _handlePaymentDeepLink(uri);
      }
    });
  }

  void _handlePaymentDeepLink(Uri uri) {
    final txnRef = uri.queryParameters['txnRef'];
    final status = uri.queryParameters['status'];
    final responseCode = uri.queryParameters['responseCode'];

    // Điều hướng đến màn hình xử lý kết quả
    // (xem Bước 4 — xử lý race condition)
    NavigationService.navigateTo(
      PaymentResultScreen(
        txnRef: txnRef!,
        deeplinkStatus: status!,
        responseCode: responseCode!,
      ),
    );
  }

  void dispose() => _linkSubscription?.cancel();
}
```

---

## 6. Bước 4 — Xử lý Race Condition: Deeplink thành công nhưng Booking vẫn PENDING

### Vấn đề

```
Timeline thực tế:
  t=0s   VNPAY xử lý xong, redirect về BE /vnpay-return
  t=0s   BE build deeplink → travery://payment-result?status=success
  t=0s   Flutter nhận deeplink status=success ← FE thấy "thành công"
  t=0-5s VNPAY gửi IPN đến BE /vnpay-ipn (có thể delay)
  t=1-5s BE xử lý IPN → update booking PENDING → PAID

  ❌ Nếu FE gọi GET /bookings/{id} ngay lập tức → có thể vẫn là PENDING
```

### Giải pháp: Optimistic UI + Polling với Exponential Backoff

**Nguyên tắc**:

- `status=success` trong deeplink = **tạm thời tin tưởng** → hiện màn hình "Đang xác nhận..."
- **Luôn poll** để lấy trạng thái cuối từ DB
- Nếu poll timeout → hiện trạng thái "Đang xử lý" với nút làm mới

```dart
// payment_result_screen.dart

class PaymentResultScreen extends StatefulWidget {
  final String txnRef;
  final String deeplinkStatus; // 'success' | 'failed'
  final String responseCode;

  const PaymentResultScreen({
    required this.txnRef,
    required this.deeplinkStatus,
    required this.responseCode,
    super.key,
  });

  @override
  State<PaymentResultScreen> createState() => _PaymentResultScreenState();
}

class _PaymentResultScreenState extends State<PaymentResultScreen> {
  PaymentConfirmState _state = PaymentConfirmState.confirming;
  String? _bookingId;

  @override
  void initState() {
    super.initState();
    // Lấy bookingId đã lưu trước đó
    _bookingId = PaymentStorage.getBookingId(widget.txnRef);

    if (widget.deeplinkStatus == 'success') {
      // Deeplink báo thành công → poll để xác nhận
      _pollBookingStatus();
    } else {
      // Deeplink báo thất bại → không cần poll
      setState(() => _state = PaymentConfirmState.failed);
    }
  }

  Future<void> _pollBookingStatus() async {
    if (_bookingId == null) {
      setState(() => _state = PaymentConfirmState.processingTimeout);
      return;
    }

    const maxAttempts = 10;
    const delays = [2, 2, 3, 3, 5, 5, 5, 10, 10, 10]; // Exponential backoff (giây)

    for (int i = 0; i < maxAttempts; i++) {
      // Chờ trước khi gọi (lần đầu cũng chờ 2s để IPN kịp xử lý)
      await Future.delayed(Duration(seconds: delays[i]));

      if (!mounted) return;

      try {
        final booking = await BookingApi.getBookingDetail(_bookingId!);

        switch (booking.status) {
          case 'PAID':
            setState(() => _state = PaymentConfirmState.confirmed);
            return;
          case 'CANCELLED':
            setState(() => _state = PaymentConfirmState.failed);
            return;
          case 'PENDING':
            // Tiếp tục poll
            continue;
        }
      } catch (e) {
        // Network error → tiếp tục thử
        continue;
      }
    }

    // Hết 10 lần thử (~45 giây) vẫn PENDING
    setState(() => _state = PaymentConfirmState.processingTimeout);
  }

  @override
  Widget build(BuildContext context) {
    return switch (_state) {
      PaymentConfirmState.confirming => _buildConfirmingUI(),
      PaymentConfirmState.confirmed => _buildSuccessUI(),
      PaymentConfirmState.failed => _buildFailedUI(),
      PaymentConfirmState.processingTimeout => _buildProcessingUI(),
    };
  }

  // Đang chờ IPN xử lý — hiện spinner
  Widget _buildConfirmingUI() => Scaffold(
    body: Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const CircularProgressIndicator(),
          const SizedBox(height: 16),
          const Text('Đang xác nhận thanh toán...'),
          const SizedBox(height: 8),
          Text(
            'Vui lòng không tắt ứng dụng',
            style: TextStyle(color: Colors.grey[600], fontSize: 13),
          ),
        ],
      ),
    ),
  );

  // IPN đã xử lý, booking = PAID
  Widget _buildSuccessUI() => Scaffold(
    body: Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.check_circle, color: Colors.green, size: 80),
          const SizedBox(height: 16),
          const Text('Thanh toán thành công!', style: TextStyle(fontSize: 20)),
          const SizedBox(height: 8),
          Text('Mã giao dịch: ${widget.txnRef}'),
          const SizedBox(height: 24),
          ElevatedButton(
            onPressed: () => Navigator.pushReplacementNamed(
              context, '/booking-detail',
              arguments: _bookingId,
            ),
            child: const Text('Xem chi tiết đặt tour'),
          ),
        ],
      ),
    ),
  );

  // Deeplink báo thất bại HOẶC booking = CANCELLED
  Widget _buildFailedUI() => Scaffold(
    body: Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.cancel, color: Colors.red, size: 80),
          const SizedBox(height: 16),
          const Text('Thanh toán thất bại', style: TextStyle(fontSize: 20)),
          const SizedBox(height: 8),
          Text(_getErrorMessage(widget.responseCode),
            style: TextStyle(color: Colors.grey[600])),
          const SizedBox(height: 24),
          ElevatedButton(
            onPressed: () => Navigator.pushReplacementNamed(context, '/home'),
            child: const Text('Về trang chủ'),
          ),
        ],
      ),
    ),
  );

  // Timeout — IPN chưa xử lý sau 45 giây
  Widget _buildProcessingUI() => Scaffold(
    body: Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.hourglass_empty, color: Colors.orange, size: 80),
          const SizedBox(height: 16),
          const Text(
            'Thanh toán đang được xử lý',
            style: TextStyle(fontSize: 20),
          ),
          const SizedBox(height: 8),
          const Padding(
            padding: EdgeInsets.symmetric(horizontal: 32),
            child: Text(
              'Hệ thống đang xác nhận thanh toán của bạn. '
              'Vui lòng kiểm tra lại sau vài phút.',
              textAlign: TextAlign.center,
              style: TextStyle(color: Colors.grey),
            ),
          ),
          const SizedBox(height: 24),
          ElevatedButton(
            onPressed: () {
              // Thử poll lại
              setState(() => _state = PaymentConfirmState.confirming);
              _pollBookingStatus();
            },
            child: const Text('Kiểm tra lại'),
          ),
          const SizedBox(height: 12),
          TextButton(
            onPressed: () => Navigator.pushReplacementNamed(
              context, '/bookings',
            ),
            child: const Text('Xem danh sách đặt tour'),
          ),
        ],
      ),
    ),
  );

  String _getErrorMessage(String code) => switch (code) {
    '24' => 'Bạn đã hủy giao dịch',
    '51' => 'Tài khoản không đủ số dư',
    '65' => 'Vượt hạn mức thanh toán trong ngày',
    '75' => 'Ngân hàng đang bảo trì',
    _ => 'Giao dịch không thành công (mã: $code)',
  };
}

enum PaymentConfirmState {
  confirming,   // Đang poll — hiện spinner
  confirmed,    // Booking = PAID — thành công
  failed,       // Deeplink failed hoặc booking CANCELLED
  processingTimeout, // Poll timeout — chưa rõ kết quả
}
```

### Luồng state đầy đủ

```
[deeplink arrives]
       │
       ├─ status=failed → state: failed (dừng luôn)
       │
       └─ status=success
              │
              ▼
       state: confirming (spinner)
       Poll GET /bookings/{id} mỗi 2-10s
              │
              ├─ booking.status=PAID → state: confirmed ✅
              ├─ booking.status=CANCELLED → state: failed ❌
              └─ Hết 10 lần (~45s) → state: processingTimeout ⏳
                          │
                          └─ User bấm "Kiểm tra lại" → confirming lại
```

### Tại sao không tin deeplink hoàn toàn?

| Kịch bản     | Deeplink | IPN               | Kết quả thực |
| ------------ | -------- | ----------------- | ------------ |
| Bình thường  | success  | ✅ xử lý xong     | PAID         |
| IPN delay    | success  | ⏳ chưa xử lý     | vẫn PENDING  |
| IPN thất bại | success  | ❌ không gọi được | mãi PENDING  |
| User giả mạo | success  | ❌ không có       | PENDING      |

→ Deeplink chỉ là **UI hint**, trạng thái thật phải lấy từ **database qua API**.

---

## 7. Bước 5 — Tạo lại Payment URL (nếu hết hạn)

```
POST /api/v1/bookings/{bookingId}/payments
Authorization: Bearer {accessToken}
```

> Không cần request body. BE tự detect IP.

```dart
Future<String> retryPayment(String bookingId) async {
  final response = await dio.post('/api/v1/bookings/$bookingId/payments');
  return response.data['data']['payment']['paymentUrl'];
}
```

---

## 8. Toàn bộ Flow

```dart
// payment_flow.dart

class PaymentFlow {
  // B1: Tạo booking → lấy paymentUrl
  Future<BookingResponse> createBooking(
    String instanceId,
    CreateBookingRequest request,
  ) async {
    final response = await dio.post(
      '/api/v1/tour-instances/$instanceId/bookings',
      data: request.toJson(),
    );
    final booking = BookingResponse.fromJson(response.data['data']);

    // Lưu để đối chiếu khi deeplink về
    await PaymentStorage.save(
      txnRef: booking.payment.transactionId,
      bookingId: booking.id,
      expiresAt: booking.payment.expiresAt,
    );

    return booking;
  }

  // B2: Mở VNPAY
  Future<void> openPayment(String paymentUrl) async {
    await launchUrl(
      Uri.parse(paymentUrl),
      mode: LaunchMode.externalApplication,
    );
  }

  // B3: Sau deeplink → poll xác nhận
  // (được xử lý trong PaymentResultScreen — xem Bước 4)
}
```

---

## 9. Xử lý lỗi API

| HTTP Status | Error Code                 | Nguyên nhân           | Xử lý                            |
| ----------- | -------------------------- | --------------------- | -------------------------------- |
| 400         | `BOOKING_NOT_PENDING`      | Booking đã thanh toán | Redirect về booking detail       |
| 400         | `PAYMENT_DEADLINE_EXPIRED` | Hết 15 phút           | Hỏi user có muốn tạo booking mới |
| 404         | `BOOKING_NOT_FOUND`        | bookingId sai         | Về trang chủ                     |
| 401         | -                          | Token hết hạn         | Refresh token → retry            |

---

## 10. Môi trường Test (Sandbox)

### Thẻ test VNPAY Sandbox

| Thông tin      | Giá trị               |
| -------------- | --------------------- |
| Ngân hàng      | NCB                   |
| Số thẻ         | `9704198526191432198` |
| Tên chủ thẻ    | `NGUYEN VAN A`        |
| Ngày phát hành | `07/15`               |
| Mật khẩu OTP   | `123456`              |

### Lưu ý khi test

- URL thanh toán có hiệu lực **15 phút** — tạo booking mới nếu hết hạn
- Mỗi lần test phải tạo **booking mới** (txnRef phải unique)
- Sau khi thanh toán, IPN được gọi trong **vài giây đến 30 giây**
- Nếu IPN chưa cập nhật ngay, UI sẽ hiện spinner và tự cập nhật

---

_Cập nhật: 2026-05-22 | Flutter | Travery Backend v1_
