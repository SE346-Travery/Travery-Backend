# Tổng hợp Thiết kế Database Travery (Phase 1)

Dưới đây là danh sách các bảng và các mối quan hệ đã chốt cho toàn bộ hệ thống Travery, bao gồm các phân hệ chính: **User**, **Khách sạn (Hotel)**, **Xe khách & Đội xe (Coach)**, **Tour** và **Phân hệ Phụ trợ**.

## 1. Luồng User & Phân quyền (Shared)
Sử dụng mô hình Table-per-Type để tách biệt thông tin đăng nhập và thông tin nghiệp vụ.

**Các bảng:** `User`, `Tourist`, `Receptionist`, `Coordinator`, `Admin`, `Guide`

**Mối quan hệ:**
- **Kế thừa định danh:**
  - `User 1 - 1 Tourist` (Có vai trò là Khách hàng)
  - `User 1 - 1 Receptionist` (Có vai trò là Lễ tân)
  - `User 1 - 1 Coordinator` (Có vai trò là Điều phối viên)
  - `User 1 - 1 Admin` (Có vai trò là Quản trị viên)
  - `User 1 - 1 Guide` (Có vai trò là Hướng dẫn viên)
- **Gán quyền & Trách nhiệm (Nội bộ):**
  - `Hotel 1 - n Receptionist` (Lễ tân làm việc tại một khách sạn cụ thể)

---

## 2. Quản lý Đội xe & Tài xế (Fleet Management - Shared)
Phân hệ dùng chung, lưu trữ tài nguyên vật lý của công ty để Điều phối viên "rút" ra sử dụng cho cả Xe khách và Tour.

**Các bảng:** `Coach`, `CoachSeat`, `Driver` (Bảng thông tin độc lập, không kế thừa từ User)

**Mối quan hệ:**
- `Coach 1 - n CoachSeat` (Một xe vật lý có sơ đồ gồm nhiều chỗ ngồi cố định)
- `Driver` và `Coach` không có quan hệ trực tiếp. Tài xế và Xe chỉ được ghép đôi với nhau khi bắt đầu một chuyến đi thực tế (`CoachTrip` hoặc `TourInstance`).

---

## 3. Luồng Đặt Xe Khách Tuyến Cố Định (Fixed Route)
Quản lý các chuyến xe liên tỉnh, hành khách mua vé và chọn ghế cụ thể.

**Các bảng:** `Route`, `CoachTrip`, `CoachBooking`, `CoachTicket`, `Station`

**Mối quan hệ:**
- **Lịch trình & Phân công:**
  - `Station 1 - n Route` (Mỗi tuyến đường nối 2 trạm xe vật lý cố định: `origin_station_id` và `destination_station_id`)
  - `Route 1 - n CoachTrip` (Một tuyến đường có nhiều chuyến xe xuất phát)
  - `RefundPolicy 1 - n Route` (Tuyến đường áp dụng một chính sách hoàn/hủy cụ thể)
  - `Coach 1 - n CoachTrip` (Một xe chạy nhiều chuyến theo thời gian)
  - `Driver 1 - n CoachTrip` (Một tài xế lái nhiều chuyến theo thời gian)
  - `Coordinator 1 - n CoachTrip` (Điều phối viên chịu trách nhiệm tạo chuyến và gán xe, tài xế)
- **Quy trình đặt vé:**
  - `User 1 - n CoachBooking` (Khách hàng tạo nhiều đơn đặt vé)
  - `CoachTrip 1 - n CoachBooking` (Một chuyến xe thực tế có nhiều đơn đặt. **Lưu ý:** Điểm đón/trả khách mặc định theo 2 trạm gốc của Route, không có nhập liệu văn bản tự do).
  - `CoachBooking 1 - n CoachTicket` (Một đơn đặt xuất ra nhiều vé cho nhiều người đi cùng)
  - `CoachSeat 1 - n CoachTicket` (Một ghế vật lý trên xe được xuất thành nhiều vé khác nhau qua các chuyến đi)

*(Lưu ý: Luồng Đánh giá Review đã được gộp chung vào Phân hệ Phụ trợ)*

---

## 4. Luồng Khách sạn (Hotel) & Dịch vụ tiện ích (Add-on)
Quản lý quỹ phòng, đặt phòng, gán phòng vật lý và các dịch vụ phát sinh.

**Các bảng:** 
- Core: `Hotel`, `RoomType`, `Room`, `Amenity`
- Booking: `HotelBooking`, `HotelBookingDetail`, `RoomAssignment`, `BookingMember`
- Add-on: `HotelService`, `AddOnOrder`

**Mối quan hệ:**
- **Cấu trúc phòng, Tiện nghi & Chính sách:**
  - `Hotel 1 - n RoomType` (Khách sạn cung cấp nhiều loại phòng)
  - `Hotel 1 - n Room` (Khách sạn có nhiều phòng vật lý - Liên kết trực tiếp để ràng buộc Unique)
  - `RoomType 1 - n Room` (Một loại phòng có nhiều phòng vật lý tương ứng)
  - `RefundPolicy 1 - n Hotel` (Khách sạn áp dụng một chính sách hoàn/hủy cụ thể chung cho toàn bộ phòng)
  - `Amenity 1 - n HotelAmenity` & `Hotel 1 - n HotelAmenity` (Quản lý tiện nghi chung của tòa nhà khách sạn)
  - `Amenity 1 - n RoomTypeAmenity` & `RoomType 1 - n RoomTypeAmenity` (Quản lý tiện nghi riêng bên trong loại phòng)

- **Quy trình đặt phòng & Check-in:**
  - `User 1 - n HotelBooking` (Khách hàng tạo nhiều đơn đặt phòng)
  - `TourInstance 1 - n HotelBooking` (Chuyến Tour tự động sinh đơn đặt phòng ẩn để giữ chỗ nội bộ)
  - `HotelBooking 1 - n HotelBookingDetail` (Đơn đặt phòng bao gồm chi tiết yêu cầu các loại phòng)
  - `RoomType 1 - n HotelBookingDetail` (Loại phòng được yêu cầu trong chi tiết đơn đặt)
  - `HotelBookingDetail 1 - n RoomAssignment` (Chi tiết yêu cầu được thỏa mãn bởi việc gán các phòng vật lý)
  - `Room 1 - n RoomAssignment` (Phòng vật lý được gán cho khách lưu trú qua các thời kỳ)
  - `HotelBooking 1 - n BookingMember` (Đơn đặt phòng có danh sách khách lưu trú đi kèm để check-in - Liên kết Đa hình)

- **Dịch vụ tiện ích (Add-on):**
  - `Hotel 1 - n HotelService` (Khách sạn cung cấp các dịch vụ tiện ích như Spa, Giặt ủi)
  - `HotelService 1 - n AddOnOrder` (Dịch vụ được khách đặt nhiều lần)
  - `HotelBooking 1 - n AddOnOrder` (Đơn đặt phòng trả tiền cho các yêu cầu sử dụng dịch vụ tiện ích)

*(Lưu ý: Luồng Đánh giá Review đã được gộp chung vào Phân hệ Phụ trợ)*

---

## 5. Luồng Tour Trọn Gói (Tích hợp Khách sạn & Xe)
Quản lý khuôn mẫu Tour, chuyến đi thực tế, đặt Tour và liên kết với Khách sạn, Kho Xe.

**Các bảng:** `Tour`, `TourItinerary`, `TourInstance`, `TourBooking`, `BookingMember`

**Mối quan hệ:**
- **Cấu trúc Tour & Chính sách:**
  - `Coordinator 1 - n Tour` (Điều phối viên thiết kế khuôn mẫu Tour / Custom Tour)
  - `RefundPolicy 1 - n Tour` (Khuôn mẫu Tour áp dụng một chính sách hoàn/hủy cụ thể)
  - `Hotel 1 - n Tour` (Khách sạn làm điểm lưu trú cho các Tour trong khu vực)
  - `Tour 1 - n TourItinerary` (Tour mẫu có lịch trình chi tiết từng ngày)
  - `Tour 1 - n TourInstance` (Tour mẫu sinh ra nhiều chuyến khởi hành thực tế)
  *(Ghi chú: Custom Tour sử dụng chung bảng `Tour` với cờ `is_custom = true`)*

- **Phân bổ tài nguyên (Xe, Tài xế, HDV):**
  - `Coordinator 1 - n TourInstance` (Điều phối viên chịu trách nhiệm điều hành chuyến Tour)
  - `Coach 1 - n TourInstance` (Một xe chở nhiều đoàn Tour theo thời gian)
  - `Driver 1 - n TourInstance` (Một tài xế lái xe cho nhiều đoàn Tour theo thời gian)
  - `Guide 1 - n TourInstance` (Một HDV được phân công dẫn đoàn)

- **Quy trình đặt Tour:**
  - `TourInstance 1 - n TourBooking` (Một chuyến đi thực tế nhận nhiều đơn đặt Tour)
  - `User 1 - n TourBooking` (Khách hàng tạo nhiều đơn đặt Tour)
  - `TourBooking 1 - n BookingMember` (Đơn đặt Tour có danh sách hành khách đi kèm - Liên kết Đa hình)

> **💡 Lưu ý về Tích hợp Tour - Khách sạn:**
> Khi Tour bắt đầu, hệ thống sẽ tự động sinh ra một `HotelBooking` ẩn dành riêng cho đoàn Tour đó để Lễ tân có thể thao tác gán `RoomAssignment`. Nhờ đó, hành khách đi Tour có thể đặt các dịch vụ Add-on (Spa, Giặt ủi) và sinh ra `AddOnOrder` liên kết trực tiếp vào `HotelBooking` này một cách dễ dàng, tái sử dụng 100% luồng Khách sạn.

*(Lưu ý: Luồng Đánh giá Review đã được gộp chung vào Phân hệ Phụ trợ)*

---

## 6. Phân hệ Phụ trợ & Vận hành (Auxiliary)
Hỗ trợ các tính năng hệ thống cốt lõi dùng chung cho tất cả các luồng.

**Các bảng:** `PaymentTransaction`, `Image`, `RefundPolicy`, `RefundPolicyRule`, `RefundRequest`, `ChatSession`, `Review`

**Thiết kế Đa hình (Polymorphic) & Luồng Vận hành:**
- **Thanh toán (`PaymentTransaction`):**
  - `User 1 - n PaymentTransaction` (Khách hàng thực hiện nhiều giao dịch thanh toán).
  - Lưu trữ giao dịch của ngân hàng/ví điện tử. Chứa 2 cột `booking_id` và `booking_type` (`TOUR_BOOKING`, `HOTEL_BOOKING`, `COACH_BOOKING`, `ADD_ON_ORDER`).
  - Giao dịch gắn liền với nhiều loại Booking mà không cần khóa ngoại cứng (Foreign Key Constraint). Giúp bảng gọn gàng và dễ mở rộng khi có thêm dịch vụ mới.
- **Danh sách Hành khách (`BookingMember`):**
  - Lưu trữ chi tiết người đi cùng. Chứa 2 cột `booking_id` và `booking_type` (`TOUR_BOOKING`, `HOTEL_BOOKING`).
  - Cho phép tái sử dụng 1 bảng lưu hành khách cho cả Tour và Hotel (phục vụ check-in khách sạn hoặc mua bảo hiểm Tour).
- **Hình ảnh (`Image`):**
  - Quản lý toàn bộ Gallery ảnh trong hệ thống. Chứa 2 cột `entity_id` và `entity_type` (`HOTEL`, `ROOM_TYPE`, `TOUR`, `TOUR_ITINERARY`).
  - Giúp tái sử dụng 1 API Upload ảnh cho tất cả các màn hình quản trị.

**Hệ thống Đánh giá Tập trung (`Review`):**
- Sử dụng bảng duy nhất với thiết kế Đa hình kép (Dual Polymorphism) để dễ dàng query.
- **Mối quan hệ vật lý:**
  - `User 1 - n Review` (Khách hàng tạo nhiều đánh giá).
- **Mối quan hệ Đa hình với Đơn đặt (Cột `booking_id`, `booking_type`):** Khóa Đơn đặt để xác thực "Đã mua hàng" (Verified Purchase) và chặn Spam (1 đơn chỉ được đánh giá 1 lần).
  - `TourBooking 1 - 1 Review`
  - `HotelBooking 1 - 1 Review`
  - `CoachBooking 1 - 1 Review`
- **Mối quan hệ Đa hình với Đối tượng đích (Cột `target_id`, `target_type`):** Giúp truy vấn và tính điểm trung bình (Average Rating) cực nhanh trên trang chi tiết sản phẩm.
  - `Tour 1 - n Review`
  - `Hotel 1 - n Review`
  - `Route 1 - n Review`

**Quản lý Hoàn tiền (Refund Policy & Request):**
- **Cơ chế cấu hình (Rule Engine):**
  - `RefundPolicy 1 - n RefundPolicyRule` (Một chính sách gom nhóm nhiều mốc thời gian và tỷ lệ phần trăm hoàn tiền).
- **Xử lý Yêu cầu Hoàn tiền (Refund Request):**
  - `PaymentTransaction 1 - n RefundRequest` (Yêu cầu hoàn tiền phải trích xuất từ một giao dịch thanh toán gốc đã thành công).
  - `User 1 - n RefundRequest` (Khách hàng tạo yêu cầu hoàn tiền khi hủy).
  - `Coordinator 1 - n RefundRequest` (Điều phối viên thao tác xác nhận và xử lý lệnh hoàn tiền qua ngân hàng).
  *(Lưu ý: Yêu cầu hoàn tiền chỉ cần truy xuất thông qua PaymentTransaction gốc, không cần nối trực tiếp với các bảng Booking để đảm bảo chuẩn hóa và giảm dư thừa dữ liệu).*

**Tư vấn Custom Tour (Chat System):**
- Áp dụng kiến trúc BaaS (Backend-as-a-Service) tích hợp bên thứ 3 (CometChat/Firebase).
- **Mối quan hệ:**
  - `User 1 - n ChatSession` (Khách hàng khởi tạo yêu cầu tư vấn).
  - `Coordinator 1 - n ChatSession` (Điều phối viên tiếp nhận tư vấn qua Chat).
  - `ChatSession 1 - 1 Tour` (Nếu tư vấn thành công, phiên chat có thể liên kết trực tiếp với 1 Custom Tour được tạo ra).
  *(Lưu ý: Không lưu nội dung tin nhắn trong DB nội bộ, bảng ChatSession chỉ lưu `third_party_channel_id` để đồng bộ UI).*
