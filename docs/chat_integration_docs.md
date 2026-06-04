# Tài liệu Tích hợp Hệ thống Chat (CometChat & Tour Integration)

Tài liệu này mô tả các logic điều phối Chat đã được triển khai trong Backend để kết nối người dùng, hướng dẫn viên và điều phối viên thông qua nền tảng CometChat.

> [!NOTE]
> **Cơ chế hoạt động:** Backend đóng vai trò "Orchestrator" (điều phối). Khi các sự kiện nghiệp vụ xảy ra (đặt tour, thanh toán, phân công), Backend sẽ tự động gọi API của CometChat để quản lý User, Group và Thành viên.

---

## 1. Các loại Phiên Chat (Chat Sessions)

Backend quản lý vòng đời của hai loại phiên chat chính:

### 1.1. Tư vấn Tour Custom (Custom Tour Consultation)
Dành cho Khách hàng muốn liên hệ để thiết kế tour theo yêu cầu.
- **Khởi tạo:** Khách hàng gọi `POST /api/v1/chats/initiate`.
- **Cơ chế Điều phối:** Hệ thống sử dụng thuật toán **Round Robin** để tự động chọn một Điều phối viên (Coordinator) đang hoạt động và gán vào phiên tư vấn.
- **Thành viên:** 
    - `Admins`: Điều phối viên được chọn.
    - `Participants`: Khách hàng (Tourist).
- **Định danh (GUID):** `consult_{user_id}_{random_suffix}`
- **Kết thúc:** Điều phối viên gọi `POST /api/v1/chats/{id}/request-close` để đóng phiên sau khi hoàn tất tư vấn.

### 1.2. Đoàn Tour (Tour Instance Group Chat)
Nhóm chat dành cho tất cả thành viên tham gia một chuyến đi cụ thể.
- **Khởi tạo:** Hệ thống tự động tạo khi chuyến đi ở trạng thái `OPEN`.
- **Định dạng Tên:** `Nhóm *Mã ngắn* - *Ngày khởi hành*` (Ví dụ: `Nhóm 8A2F1B0C - 15/06/2026`).
- **Thành viên mặc định:** Hướng dẫn viên (Guide) và Điều phối viên quản lý chuyến đi được thêm làm `admins` ngay khi nhóm được tạo.
- **Định danh (GUID):** `tour_instance_{instance_id}`
- **Kết thúc:** Điều phối viên gọi `POST /api/v1/chats/instance/{instanceId}/close` sau khi chuyến đi đã hoàn thành (`COMPLETED`).

---

## 2. Logic Tự động hóa & Đồng bộ dữ liệu

### 2.1. Quản lý Thành viên (Automation)
Hệ thống đảm bảo tính riêng tư và nhất quán của nhóm chat chuyến đi thông qua các trigger:

| Sự kiện | Hành động | Mô tả |
| :--- | :--- | :--- |
| **Thanh toán thành công** | **Thêm vào nhóm** | Khách hàng được tự động thêm vào nhóm chat ngay khi trạng thái Booking chuyển sang `PAID`. |
| **Hủy đặt chỗ** | **Xóa khỏi nhóm** | Khách hàng bị tự động loại bỏ khỏi nhóm chat nếu yêu cầu hủy tour thành công. |
| **Điểm danh vắng mặt** | **Xóa khỏi nhóm** | Nếu khách hàng được Hướng dẫn viên đánh dấu là `NO_SHOW`, tài khoản sẽ bị xóa khỏi nhóm chat của đoàn. |

### 2.2. Đồng bộ hóa Hồ sơ (Profile Sync)
Backend tự động đảm bảo dữ liệu trên CometChat luôn khớp với hệ thống chính:
- **Ảnh đại diện (Avatar):** Khi người dùng cập nhật ảnh đại diện trên App hoặc Admin cập nhật cho Staff, Backend sẽ tự động gọi API CometChat để đồng bộ trường `avatar`.
- **Khởi tạo Lazy:** Tài khoản CometChat được tạo ngay khi người dùng tham gia chat lần đầu, bao gồm cả `name` và `avatarUrl`.

### 2.3. Thông báo đẩy tin nhắn (FCM Push Sync)
- **Tự động đăng ký:** Mỗi khi người dùng Đăng nhập/Đăng ký, Backend sẽ tự động lấy FCM Token của thiết bị và đăng ký với CometChat (`Push Token Registration`).
- **Kết quả:** Người dùng có thể nhận thông báo tin nhắn mới từ CometChat thông qua FCM ngay cả khi ứng dụng không hoạt động.

---

## 3. Quản lý Tài khoản (User Synchronization)

- **Định danh (UID):** Sử dụng định dạng `user_{uuid}`.
- **Đồng bộ hóa:** Dựa trên UUID của User trong hệ thống để quản lý.

---

## 4. Danh mục Endpoints cho Frontend

Tất cả các API này yêu cầu Header `Authorization: Bearer <Token>`.

| Method | Endpoint | Quyền hạn | Mô tả |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/chats/initiate` | `TOURIST` | Khởi tạo phiên tư vấn Tour Custom (Round Robin). |
| `POST` | `/api/v1/chats/initiate-group` | Mọi Role | Lấy thông tin nhóm chat của chuyến đi (`tourInstanceId`). |
| `POST` | `/api/v1/chats/{id}/request-close` | `COORDINATOR` | Đóng phiên tư vấn Tour Custom. |
| `POST` | `/api/v1/chats/instance/{instanceId}/close` | `COORDINATOR` | Đóng nhóm chat của đoàn sau khi tour kết thúc. |

**Dữ liệu trả về (ChatSessionResponse):**
```json
{
  "cometchatGuid": "consult_uuid_xxxxx",
  "status": "OPEN" // Hoặc "CLOSED"
}
```
