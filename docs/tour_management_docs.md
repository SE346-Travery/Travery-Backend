# Tài liệu Tích hợp API: Quản lý Tour Template và Tour Instance
Tài liệu này tổng hợp các Endpoints liên quan đến việc khởi tạo, cập nhật và quản lý các bản mẫu Tour (Tour Templates) và các chuyến đi thực tế (Tour Instances) dành cho vai trò Điều hành (Coordinator).

> [!IMPORTANT]
> **Quy tắc về Multipart Form Data (Tạo & Cập nhật Tour Template):**
> Do Tour Template bao gồm cả thông tin chữ (JSON) và nhiều loại hình ảnh khác nhau, các API này yêu cầu sử dụng `multipart/form-data`. Thông tin chữ được đóng gói trong một trường có tên là `data` (dạng JSON String). Các tệp tin ảnh được gửi qua các trường `tourImages` và `itineraryImages`.

---

## 1. Quản lý Tour Template (Tour mẫu)
**Base URL:** `/api/v1/tours/templates`
Các API này yêu cầu quyền `ROLE_COORDINATOR`.

| Method | Endpoint | Mô tả | Chú ý |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | Tạo mới một Tour Template. | Yêu cầu `multipart/form-data`. Xem chi tiết ở Mục 3. |
| `PATCH` | `/{id}` | Cập nhật Tour Template hiện có. | Tự động xóa ảnh Itinerary cũ trên Cloudinary nếu gửi ảnh mới thay thế. |
| `DELETE` | `/{id}` | Xóa Tour Template. | Chỉ được xóa nếu Tour chưa có bất kỳ Instance (chuyến đi) nào được khởi tạo. |

**Cấu trúc trường `data` (JSON) khi Tạo/Cập nhật:**
- `name`: Tên tour.
- `description`: Mô tả chi tiết.
- `destinationId`: UUID của điểm đến.
- `hotelId`: (Tùy chọn) UUID của khách sạn.
- `pickupLocation`: Điểm đón khách.
- `pricePerAdult`: Giá người lớn.
- `pricePerChild`: Giá trẻ em.
- `minParticipants`: Số lượng khách tối thiểu (Mới).
- `maxParticipants`: Số lượng khách tối đa (Mới).
- `itineraries`: Danh sách lộ trình từng ngày (mỗi ngày gồm `dayNumber`, `title`, `description`).

---

## 2. Quản lý Tour Instance (Chuyến đi thực tế)
**Base URL:** `/api/v1/staff/coordinator/instances`
Dành cho Coordinator điều phối và vận hành chuyến đi.

| Method | Endpoint | Quyền hạn | Mô tả |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | `COORDINATOR` | Lấy danh sách các chuyến đi. Hỗ trợ filter: `?filter=open`, `in_progress`, `completed`, `waiting_confirmation`, `low_occupancy`. |
| `POST` | `/` | `COORDINATOR` | Khởi tạo chuyến đi từ một Tour Template. |
| `GET` | `/{id}` | `COORDINATOR` | Lấy chi tiết vận hành của chuyến đi (bao gồm Guide, Coach, Driver, Hotel Booking). |
| `PATCH` | `/{id}` | `COORDINATOR` | Cập nhật thông tin vận hành (Gán Guide, Coach, Driver, thay đổi ngày đi). |
| `PATCH` | `/{id}/status` | `COORDINATOR` | Cập nhật trạng thái chuyến đi (Ví dụ: Chuyển từ `OPEN` sang `IN_PROGRESS`). |
| `GET` | `/{id}/incidents` | `COORDINATOR` | Lấy danh sách các sự cố xảy ra trong chuyến đi. |
| `DELETE` | `/{id}` | `COORDINATOR` | Xóa chuyến đi. **Chỉ áp dụng khi Instance ở trạng thái `PLANNING` và chưa có khách đặt (Booking).** |

---

## 3. Hướng dẫn gọi API Tạo/Cập nhật Tour Template (Multipart)
Khi làm việc với Tour Template, bạn cần sử dụng `FormData` để gửi cả JSON và Files cùng lúc.

```javascript
const formData = new FormData();

// 1. Thông tin chữ (JSON) - Phải đặt tên field là 'data'
const tourData = {
  name: "Tour Hà Nội - Hạ Long 2 Ngày 1 Đêm",
  minParticipants: 10,
  maxParticipants: 25,
  itineraries: [
    { dayNumber: 1, title: "Khám phá Vịnh", description: "..." },
    { dayNumber: 2, title: "Mua sắm đặc sản", description: "..." }
  ],
  // ... các trường khác
};
formData.append('data', JSON.stringify(tourData));

// 2. Ảnh chính của Tour (nhiều ảnh)
tourImages.forEach(file => {
  formData.append('tourImages', file);
});

// 3. Ảnh minh họa cho từng ngày lộ trình (gửi theo thứ tự mảng itineraries)
itineraryImages.forEach(file => {
  formData.append('itineraryImages', file);
});

// 4. Gọi API
await axios.post('/api/v1/tours/templates', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
});
```

---

## 4. Các thay đổi quan trọng về Logic
1. **Tự động hóa Duration:** Trường `durationDays` trong Database hiện được Backend tự động tính toán dựa trên số lượng phần tử trong mảng `itineraries` gửi lên.
2. **Ràng buộc Xóa:**
   - Không thể xóa Tour Template nếu đã có ít nhất một Tour Instance tham chiếu đến nó.
   - Không thể xóa Tour Instance nếu trạng thái đã chuyển qua `OPEN` hoặc đã có khách hàng thực hiện đặt chỗ (`Booking`).
3. **Quản lý Hình ảnh:** Khi cập nhật Itinerary của Tour Template, Backend sẽ tự động dọn dẹp các ảnh cũ trên Cloudinary để tối ưu dung lượng lưu trữ.
