# Tài liệu Thiết kế Database Chi tiết (Detailed Schema)

Tài liệu này định nghĩa chi tiết các trường dữ liệu (columns), kiểu dữ liệu (data types) dành cho PostgreSQL, và các ràng buộc (constraints) của toàn bộ hệ thống Travery.

## Quy ước chung (Conventions)
- **Primary Key (PK):** Sử dụng `UUID` (Tự động sinh bằng `gen_random_uuid()`).
- **Audit Columns:** Tất cả các bảng đều mặc định có 3 cột:
  - `created_at`: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
  - `updated_at`: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
  - `is_deleted`: `BOOLEAN DEFAULT FALSE` (Phục vụ Soft Delete).
- Tên bảng: Số nhiều (Plural) theo chuẩn PostgreSQL/Spring Boot.

---

## Phase 1: Core Base & Auth (Dữ liệu nền tảng & Phân quyền)

### 1. Bảng `users` (Bảng định danh gốc)
Lưu trữ thông tin đăng nhập và thông tin cá nhân cơ bản dùng chung cho toàn hệ thống.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `email` | VARCHAR(255) | UNIQUE, NULL | Email đăng nhập (Có thể Null nếu auth qua SĐT) |
| `password_hashed` | VARCHAR(255) | NULL | Mật khẩu (Null nếu login qua Google) |
| `full_name` | VARCHAR(100) | NOT NULL | Họ và tên |
| `phone_number` | VARCHAR(20) | UNIQUE, NULL | Số điện thoại liên hệ |
| `avatar_url` | VARCHAR(500) | NULL | Link ảnh đại diện |
| `role` | VARCHAR(50) | NOT NULL | Enum: `TOURIST`, `RECEPTIONIST`, `COORDINATOR`, `ADMIN`, `GUIDE` |
| `auth_provider` | VARCHAR(20) | NOT NULL | Enum: `LOCAL`, `GOOGLE` |
| `cometchat_uid` | VARCHAR(100) | UNIQUE, NULL | ID định danh user bên hệ thống CometChat |
| `status` | VARCHAR(20) | DEFAULT 'PENDING'| Trạng thái: `PENDING` (Chờ OTP), `ACTIVE`, `BANNED` |

### 2. Các bảng VAI TRÒ (Table-per-Type)
Mỗi bảng dưới đây lấy `user_id` làm Khóa chính (PK) đồng thời là Khóa ngoại (FK) trỏ về bảng `users`.

#### 2.1. Bảng `tourists` (Khách hàng)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `user_id` | UUID | PK, FK(users.id) | Khóa chính & Khóa ngoại |
| `passport_number` | VARCHAR(50) | UNIQUE, NULL | Số CCCD / Hộ chiếu |
| `date_of_birth` | DATE | NULL | Ngày sinh |
| `gender` | VARCHAR(10) | NULL | MALE, FEMALE, OTHER |

#### 2.2. Bảng `receptionists` (Lễ tân)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `user_id` | UUID | PK, FK(users.id) | Khóa chính & Khóa ngoại |
| `hotel_id` | UUID | FK(hotels.id) NOT NULL | Thuộc biên chế khách sạn nào |
| `shift_type` | VARCHAR(50) | NULL | Ca làm việc (MORNING, EVENING, NIGHT) |

#### 2.3. Bảng `coordinators` (Điều phối viên)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `user_id` | UUID | PK, FK(users.id) | Khóa chính & Khóa ngoại |
| `employee_code` | VARCHAR(50) | UNIQUE, NOT NULL | Mã nhân viên nội bộ |
| `department` | VARCHAR(50) | NULL | Enum: `TOUR`, `HOTEL`, `COACH` |

#### 2.4. Bảng `guides` (Hướng dẫn viên)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `user_id` | UUID | PK, FK(users.id) | Khóa chính & Khóa ngoại |
| `employee_code` | VARCHAR(50) | UNIQUE, NOT NULL | Mã nhân viên (Tự động sinh) |
| `guide_license` | VARCHAR(100) | UNIQUE, NOT NULL | Số thẻ hướng dẫn viên |
| `languages` | JSONB | DEFAULT '[]' | Mảng ngôn ngữ (VD: `["vi", "en"]`) |
| `years_experience`| INT | DEFAULT 0 | Số năm kinh nghiệm |

#### 2.6. Bảng `receptionists` (Lễ tân)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `user_id` | UUID | PK, FK(users.id) | Khóa chính & Khóa ngoại |
| `employee_code` | VARCHAR(50) | UNIQUE, NOT NULL | Mã nhân viên (Tự động sinh) |
| `hotel_id` | UUID | FK(hotels.id) NOT NULL | Thuộc khách sạn nào |
| `shift_type` | VARCHAR(50) | NULL | Ca làm việc (VD: `DAY`, `NIGHT`) |

#### 2.5. Bảng `admins` (Quản trị viên)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `user_id` | UUID | PK, FK(users.id) | Khóa chính & Khóa ngoại |

---

### 3. Bảng `stations` (Trạm dừng / Bến bãi)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `name` | VARCHAR(255) | NOT NULL | Tên trạm (VD: VP Phạm Ngũ Lão) |
| `address` | VARCHAR(500) | NOT NULL | Địa chỉ chi tiết |
| `city_province` | VARCHAR(100) | NOT NULL | Tỉnh / Thành phố |
| `latitude` | DECIMAL(10,8)| NULL | Tọa độ GPS Vĩ độ |
| `longitude` | DECIMAL(11,8)| NULL | Tọa độ GPS Kinh độ |

---

### 4. Bảng `images` (Hình ảnh đa hình)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `entity_id` | UUID | NOT NULL | ID của đối tượng (Tour, Hotel...) |
| `entity_type` | VARCHAR(50) | NOT NULL | Enum: `HOTEL`, `ROOM_TYPE`, `TOUR`, `TOUR_ITINERARY` |
| `url` | VARCHAR(500) | NOT NULL | Link ảnh S3/Cloudinary |
| `is_thumbnail` | BOOLEAN | DEFAULT FALSE | Xác định ảnh bìa chính |
| `display_order` | INT | DEFAULT 0 | Thứ tự hiển thị trên gallery |

---

## Phase 2: Configuration & Rule Engines (Cấu hình hệ thống)

### 5. Quản lý Đội xe (Fleet)
Tài sản xe và danh sách tài xế nội bộ của Travery.

#### 5.1. Bảng `coaches` (Tài sản Xe)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `license_plate` | VARCHAR(20) | UNIQUE, NOT NULL | Biển số xe |
| `coach_type` | VARCHAR(50) | NOT NULL | Enum: `SEAT`, `BED`, `LIMOUSINE` |
| `capacity` | INT | NOT NULL | Tổng số ghế/giường |
| `status` | VARCHAR(20) | DEFAULT 'ACTIVE' | `ACTIVE`, `MAINTENANCE` (Đang bảo trì) |

#### 5.2. Bảng `coach_seats` (Sơ đồ ghế cố định)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `coach_id` | UUID | FK(coaches.id) NOT NULL| Xe nào |
| `seat_name` | VARCHAR(10) | NOT NULL | Tên ghế (A1, B2) |
| `tier` | VARCHAR(20) | NOT NULL | Tầng: `UPPER`, `LOWER` |
| `position` | VARCHAR(20) | NOT NULL | Vị trí: `FRONT`, `MIDDLE`, `BACK` |

#### 5.3. Bảng `drivers` (Tài nguyên Tài xế)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `full_name` | VARCHAR(100) | NOT NULL | Tên tài xế |
| `phone_number` | VARCHAR(20) | UNIQUE, NOT NULL | Số điện thoại |
| `license_number`| VARCHAR(50) | UNIQUE, NOT NULL | Số bằng lái |
| `status` | VARCHAR(20) | DEFAULT 'AVAILABLE'| `AVAILABLE`, `ON_TRIP`, `ON_LEAVE` |

---

### 6. Danh mục Tiện ích (`amenities`)
Danh mục dùng chung cho toàn bộ Hotel và RoomType.
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `name` | VARCHAR(100) | UNIQUE, NOT NULL | Tên (Hồ bơi, WiFi...) |
| `icon_url` | VARCHAR(255) | NULL | Link icon SVG |
| `type` | VARCHAR(50) | NOT NULL | Phân loại: `HOTEL_AMENITY`, `ROOM_AMENITY` |

---

### 7. Hệ thống Chat (`chat_sessions`)
Lưu trữ thông tin liên kết phòng Chat của khách hàng và Điều phối viên trên CometChat.
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `user_id` | UUID | FK(users.id) NOT NULL | Khách hàng mở chat |
| `coordinator_id`| UUID | FK(users.id) NULL | Điều phối viên tiếp nhận (Có thể null lúc mới mở) |
| `cometchat_guid`| VARCHAR(100) | UNIQUE, NOT NULL | Mã Group ID tương ứng trên CometChat |
| `tour_id` | UUID | FK(tours.id) NULL | Nếu phiên tư vấn này sinh ra Custom Tour, lưu ID lại |
| `status` | VARCHAR(20) | DEFAULT 'OPEN' | `OPEN`, `CLOSED` |

---

### 8. Cơ chế Luật Hoàn tiền (Rule Engine)
Bảng cấu hình linh hoạt các mốc % hoàn tiền theo thời gian.

#### 8.1. Bảng `refund_policies` (Tên chính sách)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `name` | VARCHAR(255) | NOT NULL | VD: "Chính sách Lễ Tết", "Tiêu chuẩn" |
| `service_type` | VARCHAR(50) | NOT NULL | Áp dụng cho: `TOUR`, `HOTEL`, `COACH` |

#### 8.2. Bảng `refund_policy_rules` (Chi tiết các mốc)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `refund_policy_id`| UUID | FK(refund_policies.id) | Trỏ về bảng mẹ |
| `hours_before_departure`| INT | NOT NULL | Số giờ tối thiểu trước khởi hành (VD: 168 = 7 ngày) |
| `refund_percentage`| DECIMAL(5,2)| NOT NULL | % hoàn lại (0.00 đến 100.00) |

---

## Phase 3: Bookable Assets (Tài sản có thể đặt trước)
Đây là các dịch vụ cốt lõi mang lại doanh thu cho Travery: Khách sạn, Tuyến xe cố định và Tour.

### 9. Luồng Khách sạn (Hotel)
Danh mục khách sạn, loại phòng và phòng vật lý.

#### 9.1. Bảng `hotels` (Khách sạn)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `name` | VARCHAR(255) | NOT NULL | Tên khách sạn |
| `star_rating` | INT | NOT NULL | Số sao (1-5) |
| `description` | TEXT | NULL | Bài viết giới thiệu |
| `address` | VARCHAR(500) | NOT NULL | Địa chỉ chi tiết |
| `city_province` | VARCHAR(100) | NOT NULL | Tỉnh / Thành phố |
| `latitude` | DECIMAL(10,8)| NOT NULL | Tọa độ GPS Vĩ độ |
| `longitude` | DECIMAL(11,8)| NOT NULL | Tọa độ GPS Kinh độ |
| `check_in_time` | TIME | DEFAULT '14:00' | Giờ nhận phòng mặc định |
| `check_out_time`| TIME | DEFAULT '12:00' | Giờ trả phòng mặc định |
| `refund_policy_id`| UUID | FK(refund_policies.id)| Chính sách hoàn hủy chung |

#### 9.2. Bảng `room_types` (Loại phòng)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `hotel_id` | UUID | FK(hotels.id) NOT NULL| Thuộc khách sạn nào |
| `name` | VARCHAR(255) | NOT NULL | Tên loại phòng (VD: Standard, VIP) |
| `description` | TEXT | NULL | Mô tả phòng |
| `base_price` | DECIMAL(12,2)| NOT NULL | Giá gốc |
| `capacity_adults`| INT | NOT NULL | Số người lớn tối đa |
| `capacity_children`| INT| DEFAULT 0 | Số trẻ em tối đa |
| `bed_type` | VARCHAR(50) | NOT NULL | Loại giường (DOUBLE, SINGLE, TWIN) |

#### 9.3. Bảng `rooms` (Phòng vật lý)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `hotel_id` | UUID | FK(hotels.id) NOT NULL | Thuộc khách sạn nào |
| `room_type_id` | UUID | FK(room_types.id) NOT NULL| Thuộc loại phòng nào |
| `room_number` | VARCHAR(50) | NOT NULL | Số phòng (VD: 101A, 102B) |
| `status` | VARCHAR(20) | DEFAULT 'AVAILABLE'| Trạng thái hiện tại (`AVAILABLE`, `OCCUPIED`, `MAINTENANCE`) |

*Giải thích ràng buộc (Constraint):* Phải thiết lập `UNIQUE(hotel_id, room_number)` ở Database. Nếu chỉ Unique `room_number` thì các khách sạn khác nhau không thể cùng có phòng "101". Do đó, ta phải thêm `hotel_id` vào bảng này để ràng buộc cặp khóa.

#### 9.4. Các bảng Trung gian Tiện ích Khách sạn
Sử dụng UUID làm PK thay vì Composite Key để dễ quản lý theo chuẩn Spring Data JPA.

**Bảng `hotel_amenities` (Tiện ích chung của tòa nhà)**
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `hotel_id` | UUID | FK(hotels.id) NOT NULL | Thuộc khách sạn |
| `amenity_id` | UUID | FK(amenities.id) NOT NULL| Tiện ích |
*Ràng buộc:* `UNIQUE(hotel_id, amenity_id)` để chống trùng lặp.

**Bảng `room_type_amenities` (Tiện ích riêng của loại phòng)**
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `room_type_id` | UUID | FK(room_types.id) NOT NULL| Thuộc loại phòng |
| `amenity_id` | UUID | FK(amenities.id) NOT NULL| Tiện ích |
*Ràng buộc:* `UNIQUE(room_type_id, amenity_id)` để chống trùng lặp.

#### 9.5. Bảng `hotel_services` (Dịch vụ Add-on)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `hotel_id` | UUID | FK(hotels.id) NOT NULL| Dịch vụ của khách sạn nào |
| `category` | VARCHAR(50) | NOT NULL | Enum: `FOOD`, `SPA`, `LAUNDRY`, `OTHER` |
| `name` | VARCHAR(100) | NOT NULL | VD: "Combo Giặt Sấy", "Buffet Sáng" |
| `price` | DECIMAL(12,2)| NOT NULL | Giá dịch vụ |
| `unit` | VARCHAR(50) | NOT NULL | Đơn vị: "Kg", "Gói", "Suất" |
| `description` | TEXT | NULL | Mô tả chi tiết |
| `is_active` | BOOLEAN | DEFAULT TRUE | Tạm ngưng dịch vụ nếu quá tải |

---

### 10. Luồng Xe Khách Tuyến Cố Định (Fixed Route)

#### 10.1. Bảng `routes` (Tuyến đường)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `origin_station_id`| UUID | FK(stations.id) NOT NULL| Trạm xuất phát |
| `destination_station_id`| UUID| FK(stations.id) NOT NULL| Trạm đích |
| `distance_km` | DECIMAL(6,2) | NULL | Chiều dài tuyến đường (Km) |
| `estimated_hours`| DECIMAL(4,1) | NULL | Thời gian chạy dự kiến (Giờ) |
| `base_price` | DECIMAL(12,2)| NOT NULL | Giá vé gốc |
| `refund_policy_id`| UUID | FK(refund_policies.id)| Chính sách hoàn vé xe |

---

### 11. Luồng Tour (Tours)
Cấu trúc tạo ra Tour trọn gói hoặc Tour thiết kế riêng (Custom Tour).

#### 11.1. Bảng `tours` (Khuôn mẫu Tour)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `name` | VARCHAR(255) | NOT NULL | Tên Tour |
| `description` | TEXT | NULL | Giới thiệu Tour |
| `coordinator_id`| UUID | FK(users.id) NOT NULL | Điều phối viên tạo ra Tour này |
| `hotel_id` | UUID | FK(hotels.id) NULL | Nơi lưu trú (Có thể Null nếu là Tour trong ngày) |
| `requested_by_user_id`| UUID | FK(users.id) NULL | Khách hàng yêu cầu (Chỉ áp dụng nếu là Custom Tour) |
| `destination_code`| VARCHAR(50) | NOT NULL | Mã điểm đến (Tỉnh thành/Khu vực) |
| `pickup_location` | VARCHAR(500) | NOT NULL | Điểm đón khách mặc định |
| `price_per_adult` | DECIMAL(12,2)| NOT NULL | Giá vé người lớn |
| `price_per_child` | DECIMAL(12,2)| NOT NULL | Giá vé trẻ em |
| `is_custom` | BOOLEAN | DEFAULT FALSE | Xác định đây là Custom Tour do khách đặt riêng |
| `refund_policy_id`| UUID | FK(refund_policies.id)| Chính sách hoàn hủy Tour |

#### 11.2. Bảng `tour_itineraries` (Lịch trình theo Ngày)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `tour_id` | UUID | FK(tours.id) NOT NULL | Thuộc Tour nào |
| `day_number` | INT | NOT NULL | Ngày thứ mấy (1, 2, 3...) |
| `title` | VARCHAR(255) | NOT NULL | Tiêu đề ngày (VD: Khám phá chợ Đà Lạt) |
| `description` | TEXT | NOT NULL | Mô tả chi tiết các hoạt động trong ngày |

---

## Phase 4: Operational Instances (Chuyến đi thực tế)
Giai đoạn này định nghĩa các thực thể mang tính "sống", tức là các chuyến xe hoặc chuyến tour có ngày giờ khởi hành cụ thể, nơi mà các tài nguyên (Xe, Tài xế, HDV) được mang ra phân bổ.

### 12. Chuyến Xe Khách (`coach_trips`)
Mỗi `Route` (Tuyến đường) có thể sinh ra hàng ngàn `CoachTrip` (Chuyến xe chạy thực tế).

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `route_id` | UUID | FK(routes.id) NOT NULL | Chạy trên tuyến nào |
| `coach_id` | UUID | FK(coaches.id) NOT NULL| Gán cho Xe nào |
| `driver_id` | UUID | FK(drivers.id) NOT NULL| Gán cho Tài xế nào |
| `coordinator_id` | UUID | FK(users.id) NOT NULL | Điều phối viên nào tạo chuyến này |
| `departure_time` | TIMESTAMP | NOT NULL | Thời gian xuất bến chính xác |
| `arrival_time` | TIMESTAMP | NULL | Thời gian cập bến thực tế (Cập nhật sau khi tới) |
| `status` | VARCHAR(50) | DEFAULT 'SCHEDULED' | Enum: `SCHEDULED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED` |

---

### 13. Chuyến Tour Thực Tế (`tour_instances`)
Mỗi Khuôn mẫu `Tour` sẽ được nhân bản thành nhiều `TourInstance` với ngày khởi hành và nhân sự khác nhau.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `tour_id` | UUID | FK(tours.id) NOT NULL | Nhân bản từ Khuôn mẫu Tour nào |
| `coordinator_id` | UUID | FK(users.id) NOT NULL | Điều phối viên quản lý chuyến đi này |
| `guide_id` | UUID | FK(users.id) NULL | Hướng dẫn viên dẫn đoàn (Có thể gán sau) |
| `coach_id` | UUID | FK(coaches.id) NULL | Gán Xe nào cho đoàn (Có thể gán sau) |
| `driver_id` | UUID | FK(drivers.id) NULL | Gán Tài xế nào (Có thể gán sau) |
| `hotel_booking_id`| UUID | NULL | ID của Booking Khách sạn ẩn sinh ra tự động (Để map với phân hệ Hotel) |
| `start_date` | DATE | NOT NULL | Ngày đi |
| `end_date` | DATE | NOT NULL | Ngày về |
| `min_participants`| INT | DEFAULT 10 | Số khách tối thiểu để Tour khởi hành |
| `max_participants`| INT | DEFAULT 40 | Số khách tối đa (Sức chứa) |
| `current_participants`| INT| DEFAULT 0 | Tổng số khách hiện tại đã thanh toán |
| `status` | VARCHAR(50) | DEFAULT 'PLANNING' | `PLANNING` (Đang xếp lịch, ẩn trên App), `OPEN` (Bắt đầu mở bán trên App), `FULL`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED` |

---

## Phase 5: Transactional Data (Dữ liệu Giao dịch)
Đây là phase phức tạp nhất, quản lý dòng tiền, vé, đặt chỗ và đánh giá của khách hàng.

### 14. Các bảng Đơn đặt (Bookings)
Mỗi dịch vụ sẽ có một bảng Booking riêng để quản lý vòng đời thanh toán độc lập.

#### 14.1. Bảng `coach_bookings` (Đơn đặt vé xe)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `user_id` | UUID | FK(users.id) NOT NULL | Ai đặt |
| `coach_trip_id`| UUID | FK(coach_trips.id) NOT NULL | Chuyến xe nào |
| `total_price` | DECIMAL(12,2)| NOT NULL | Tổng tiền phải trả |
| `payment_deadline`| TIMESTAMP | NULL | Hạn chót thanh toán |
| `status` | VARCHAR(50) | DEFAULT 'PENDING' | Enum: `PENDING`, `PAID`, `CANCELLED` |

#### 14.2. Bảng `hotel_bookings` (Đơn đặt phòng)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `user_id` | UUID | FK(users.id) NOT NULL | Ai đặt |
| `tour_instance_id`| UUID | FK(tour_instances.id)| NULL nếu khách tự đặt. Có ID nếu là Booking ẩn tự sinh từ Tour |
| `total_price` | DECIMAL(12,2)| NOT NULL | Tổng tiền phải trả |
| `payment_deadline`| TIMESTAMP | NULL | Hạn chót thanh toán |
| `status` | VARCHAR(50) | DEFAULT 'PENDING' | Enum: `PENDING`, `PAID`, `CHECKED_IN`, `CHECKED_OUT`, `CANCELLED` |

#### 14.3. Bảng `tour_bookings` (Đơn đặt Tour)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `user_id` | UUID | FK(users.id) NOT NULL | Ai đặt |
| `tour_instance_id`| UUID | FK(tour_instances.id) NOT NULL| Đi chuyến Tour nào |
| `total_price` | DECIMAL(12,2)| NOT NULL | Tổng tiền |
| `payment_deadline`| TIMESTAMP | NULL | Hạn chót thanh toán |
| `status` | VARCHAR(50) | DEFAULT 'PENDING' | Enum: `PENDING`, `PAID`, `CANCELLED` |

---

### 15. Chi tiết Đơn & Phân bổ (Details & Allocations)

#### 15.1. Bảng `coach_tickets` (Vé xe xuất ra)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `coach_booking_id`| UUID | FK(coach_bookings.id) NOT NULL| Nằm trong đơn nào |
| `coach_seat_id`| UUID | FK(coach_seats.id) NOT NULL| Ngồi ghế nào |
| `passenger_name`| VARCHAR(100) | NULL | Tên người đi (Phòng trường hợp đặt hộ) |
| `passenger_phone`| VARCHAR(20) | NULL | SĐT người đi |
| `price_at_booking`| DECIMAL(12,2)| NOT NULL | Giá vé tại thời điểm mua |

#### 15.2. Bảng `hotel_booking_details` (Yêu cầu loại phòng)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `hotel_booking_id`| UUID | FK(hotel_bookings.id) NOT NULL| Nằm trong đơn nào |
| `room_type_id` | UUID | FK(room_types.id) NOT NULL| Đặt loại phòng nào |
| `quantity` | INT | NOT NULL | Số lượng phòng yêu cầu |
| `price_at_booking`| DECIMAL(12,2)| NOT NULL | Giá tại thời điểm đặt |
| `start_date` | DATE | NOT NULL | Ngày nhận phòng |
| `end_date` | DATE | NOT NULL | Ngày trả phòng |

#### 15.3. Bảng `room_assignments` (Gán phòng thực tế khi Check-in)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `hotel_booking_detail_id`| UUID | FK(hotel_booking_details.id) NOT NULL| Thuộc yêu cầu nào |
| `room_id` | UUID | FK(rooms.id) NOT NULL| Gán phòng vật lý cụ thể nào |

#### 15.4. Bảng `add_on_orders` (Yêu cầu Dịch vụ Khách sạn)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `hotel_booking_id`| UUID | FK(hotel_bookings.id) NOT NULL| Khách của phòng nào gọi dịch vụ |
| `hotel_service_id`| UUID | FK(hotel_services.id) NOT NULL| Đặt dịch vụ gì |
| `quantity` | INT | NOT NULL | Số lượng |
| `total_price` | DECIMAL(12,2)| NOT NULL | Tổng tiền dịch vụ |
| `status` | VARCHAR(50) | DEFAULT 'PENDING' | `PENDING`, `DELIVERED`, `CANCELLED` |

#### 15.5. Bảng `booking_members` (Danh sách hành khách Đa hình)
Lưu thông tin chi tiết của những người đi cùng để nộp cho Bảo hiểm (Tour) hoặc Công an lưu trú (Hotel).
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `booking_id` | UUID | NOT NULL | Poly ID |
| `booking_type` | VARCHAR(50) | NOT NULL | Enum: `TOUR_BOOKING`, `HOTEL_BOOKING` |
| `full_name` | VARCHAR(100) | NOT NULL | Họ và tên |
| `passport_number`| VARCHAR(50) | NOT NULL | Số CCCD/Passport |
| `date_of_birth`| DATE | NULL | Ngày sinh |

---

### 16. Tài chính & Hoàn tiền (Financials)

#### 16.1. Bảng `payment_transactions` (Giao dịch Thanh toán Đa hình)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `user_id` | UUID | FK(users.id) NOT NULL | Ai thanh toán |
| `booking_id` | UUID | NOT NULL | Mã Đơn đặt Poly ID |
| `booking_type` | VARCHAR(50) | NOT NULL | Enum: `TOUR_BOOKING`, `HOTEL_BOOKING`, `COACH_BOOKING`, `ADD_ON_ORDER` |
| `amount` | DECIMAL(12,2)| NOT NULL | Số tiền thanh toán |
| `transaction_type`| VARCHAR(50) | DEFAULT 'PAYMENT' | Enum: `PAYMENT`, `REFUND` |
| `payment_method`| VARCHAR(50) | NOT NULL | Enum: `VNPAY`, `MOMO`, `CASH` |
| `gateway_trans_id`| VARCHAR(255) | NULL | Mã giao dịch do VNPay/Momo trả về |
| `status` | VARCHAR(50) | NOT NULL | Enum: `SUCCESS`, `FAILED`, `PENDING` |
| `paid_at` | TIMESTAMP | NULL | Thời gian thanh toán thực tế |

#### 16.2. Bảng `refund_requests` (Yêu cầu Hoàn tiền)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `payment_transaction_id`| UUID | FK(payment_transactions.id) NOT NULL| Trích xuất từ giao dịch gốc nào |
| `user_id` | UUID | FK(users.id) NOT NULL | Khách yêu cầu hoàn |
| `processed_by_id`| UUID | FK(users.id) NULL | Điều phối viên xử lý |
| `requested_amount`| DECIMAL(12,2)| NOT NULL | Số tiền hệ thống tự động tính |
| `actual_refunded`| DECIMAL(12,2)| NULL | Số tiền kế toán thực tế đã chuyển lại |
| `customer_reason`| TEXT | NULL | Lý do hủy của khách |
| `status` | VARCHAR(50) | DEFAULT 'PENDING' | Enum: `PENDING`, `PROCESSING`, `COMPLETED`, `REJECTED` |

---

### 17. Hệ thống Đánh giá Tập trung (Reviews)

#### 17.1. Bảng `reviews` (Đánh giá Đa hình Kép)
Gộp chung Đánh giá Tour, Khách sạn, Xe khách vào 1 bảng duy nhất.
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK | Khóa chính |
| `user_id` | UUID | FK(users.id) NOT NULL | Khách hàng đánh giá |
| `booking_id` | UUID | NOT NULL | Poly ID (Chống Spam, Verified Purchase) |
| `booking_type` | VARCHAR(50) | NOT NULL | Enum: `TOUR_BOOKING`, `HOTEL_BOOKING`, `COACH_BOOKING` |
| `target_id` | UUID | NOT NULL | Poly ID (Đối tượng bị đánh giá) |
| `target_type` | VARCHAR(50) | NOT NULL | Enum: `TOUR`, `HOTEL`, `ROUTE` |
| `average_rating`| INT | NOT NULL | Số sao (1-5) |
| `content` | TEXT | NULL | Nhận xét chi tiết |

*(Lưu ý Database Constraint: Thiết lập Unique Constraint kết hợp 2 cột `booking_id` và `booking_type` để đảm bảo 1 Đơn hàng chỉ được phép gửi 1 đánh giá duy nhất).*
