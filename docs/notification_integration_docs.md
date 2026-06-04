# Tài liệu Tích hợp API: Thông báo (Push Notification & History)
Tài liệu này cung cấp hướng dẫn tích hợp hệ thống thông báo đẩy (FCM) và quản lý lịch sử thông báo cho ứng dụng Travery.

> [!IMPORTANT]
> **Tích hợp FCM Token:** 
> Hệ thống quản lý FCM Token đã được tích hợp trực tiếp vào luồng Xác thực (Auth). Frontend **không cần** gọi API riêng để lưu Token. Thay vào đó, hãy gửi `fcmToken` kèm theo các request Đăng ký, Đăng nhập và Đăng xuất.

---

## 1. Quản lý FCM Token (Client-side)
Việc đồng bộ Token diễn ra tự động thông qua các cổng xác thực chính.

| Luồng | Endpoint | Body JSON (Bổ sung) | Mô tả |
| :--- | :--- | :--- | :--- |
| **Đăng ký** | `POST /api/v1/auth/register` | `fcmToken: string` | Tự động liên kết thiết bị ngay khi tạo tài khoản. |
| **Đăng nhập** | `POST /api/v1/auth/verify-otp` | `fcmToken: string` | Cập nhật token mới nhất cho người dùng sau khi xác thực OTP thành công. |
| **Đăng xuất** | `POST /api/v1/auth/logout` | `fcmToken: string` | **Bắt buộc:** Gửi token hiện tại để Backend hủy đăng ký, tránh việc gửi thông báo nhầm sau khi thoát. |

> [!TIP]
> **Xử lý Quyền thông báo:** Nếu người dùng từ chối cấp quyền thông báo trên thiết bị, Frontend chỉ cần gửi `fcmToken: null` hoặc bỏ trống trường này. Hệ thống sẽ xử lý mượt mà mà không gây lỗi.

---

## 2. Quản lý Lịch sử Thông báo
**Base URL:** `/api/v1/notifications`
Yêu cầu Header `Authorization: Bearer <Token>`.

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/` | Lấy danh sách thông báo phân trang. Trả về object chứa `notifications` (dạng Page) và `unreadCount` (tổng số chưa đọc). |
| `GET` | `/unread-count` | Chỉ lấy số lượng thông báo chưa đọc (Dùng cho Badge trên App). |
| `PUT` | `/{id}/read` | Đánh dấu một thông báo cụ thể là đã đọc. |
| `PUT` | `/read-all` | Đánh dấu tất cả thông báo của mình là đã đọc. |
| `DELETE` | `/{id}` | Xóa vĩnh viễn một thông báo khỏi lịch sử. |

---

## 3. Cấu trúc dữ liệu Thông báo (Payload)
Khi nhận được thông báo (hoặc lấy từ lịch sử), Frontend nên dựa vào trường `type` và `dataId` để thực hiện điều hướng (Deep Linking).

### 3.1. Các loại thông báo (`type`)
| Type | Ý nghĩa | Gợi ý Điều hướng |
| :--- | :--- | :--- |
| `BOOKING_CONFIRMED` | Thanh toán/Đặt chỗ thành công | Chi tiết đơn hàng (`dataId` là Booking ID) |
| `NEW_REVIEW` | Có đánh giá mới cho dịch vụ | Màn hình danh sách Review |
| `SYSTEM_ALERT` | Thông báo chung từ hệ thống | Màn hình chi tiết thông báo |

### 3.2. Ví dụ dữ liệu trả về
```json
{
  "id": "uuid-string",
  "title": "Thanh toán thành công",
  "content": "Đơn hàng #123 của bạn đã được xác nhận.",
  "type": "BOOKING_CONFIRMED",
  "dataId": "booking-uuid-123",
  "isRead": false,
  "createdAt": "2026-06-02 21:00:00"
}
```

---

## 4. Lưu ý cho việc Điều hướng (Deep Linking)
Khi người dùng nhấn vào một Push Notification:
1. Đọc trường `type`.
2. Nếu có `dataId`, hãy sử dụng nó để gọi API lấy chi tiết và chuyển hướng người dùng đến màn hình tương ứng.
3. Đồng thời gọi API `PUT /api/v1/notifications/{id}/read` để đồng bộ trạng thái "đã đọc" trên mọi thiết bị.
