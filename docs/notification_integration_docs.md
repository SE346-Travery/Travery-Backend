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

---

## 2. Danh mục Loại Thông báo (`type`)
Frontend nên dựa vào trường `type` và `dataId` để thực hiện điều hướng (Deep Linking).

| Type | Vai trò nhận | Ý nghĩa | Gợi ý Điều hướng |
| :--- | :--- | :--- | :--- |
| `BOOKING_CONFIRMED` | Tourist | Thanh toán/Đặt chỗ thành công | Chi tiết đơn hàng (`dataId`: Booking ID) |
| `UPCOMING_TOUR` | Tourist | Nhắc nhở 24h trước khi khởi hành | Chi tiết đơn hàng (`dataId`: Booking ID) |
| `UPCOMING_HOTEL` | Tourist | Nhắc nhở 24h trước khi nhận phòng | Chi tiết đơn hàng (`dataId`: Booking ID) |
| `UPCOMING_COACH` | Tourist | Nhắc nhở 24h trước khi xe chạy | Chi tiết đơn hàng (`dataId`: Booking ID) |
| `POST_TOUR_REVIEW` | Tourist | Mời đánh giá sau khi hoàn thành dịch vụ | Màn hình đánh giá (`dataId`: Booking ID) |
| `SECURITY_ALERT` | Mọi Role | Đổi mật khẩu thành công | Màn hình bảo mật/Cài đặt |
| `NEW_REVIEW` | Staff | Có khách hàng đánh giá mới | Màn hình chi tiết đánh giá (`dataId`: Review ID) |
| `SYSTEM_ALERT` | Mọi Role | Thông báo hệ thống (Khóa/Mở tài khoản) | Chi tiết thông báo |
| `CUSTOM_TOUR_CHAT_ASSIGNED` | Coordinator | Có yêu cầu tư vấn tour mới | Màn hình Chat với khách (`dataId`: Chat Session ID) |
| `GROUP_CHAT_CREATED` | Mọi Role | Nhóm chat đoàn tour vừa được tạo | Màn hình Chat đoàn (`dataId`: Tour Instance ID) |

---

## 3. Các API Quản lý Lịch sử
**Base URL:** `/api/v1/notifications`

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/` | Lấy danh sách thông báo (phân trang). Trả về `{ notifications: Page, unreadCount: int }`. |
| `GET` | `/unread-count` | Chỉ lấy số lượng thông báo chưa đọc. |
| `PUT` | `/{id}/read` | Đánh dấu một thông báo cụ thể là đã đọc. |
| `PUT` | `/read-all` | Đánh dấu tất cả thông báo của mình là đã đọc. |
| `DELETE` | `/{id}` | Xóa một thông báo khỏi lịch sử. |

---

## 4. Lưu ý cho Frontend (Deep Linking)
Khi người dùng nhấn vào một Push Notification:
1. Kiểm tra trường `type` để xác định màn hình đích.
2. Sử dụng `dataId` để gọi API lấy dữ liệu chi tiết của đối tượng tương ứng (Booking, Review, Chat...).
3. Đồng thời gọi API `PUT /api/v1/notifications/{id}/read` để cập nhật trạng thái trên server.
