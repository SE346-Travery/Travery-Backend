## 0. Giới Thiệu Doanh Nghiệp

### 0.1 Travery là gì?

Travery (Travel Discovery) là công ty du lịch tự vận hành, sở hữu và quản lý trực tiếp ba nhóm dịch vụ chính: Tour du lịch trọn gói, Xe khách tuyến cố định và Khách sạn tại các điểm đến. Travery **không phải nền tảng trung gian** — công ty kiểm soát toàn bộ chất lượng dịch vụ và trực tiếp phục vụ khách hàng.

Ứng dụng Travery được xây dựng nhằm số hóa toàn bộ quy trình đặt dịch vụ, điều phối vận hành và chăm sóc khách hàng — từ lúc khách tìm kiếm đến khi hoàn tất chuyến đi và để lại đánh giá.

### 0.2 Ba dịch vụ cốt lõi

| Dịch vụ                    | Mô tả                                                                                                    | Thanh toán                                       |
| -------------------------- | -------------------------------------------------------------------------------------------------------- | ------------------------------------------------ |
| **Tour trọn gói**          | Chương trình du lịch có lịch trình cố định, bao gồm xe đưa đón, lưu trú, ăn uống và hoạt động tham quan. | Toàn bộ 100% ngay khi đặt.                       |
| **Xe khách tuyến cố định** | Vé xe liên tỉnh trên các tuyến Travery vận hành. Khách mua vé theo từng ghế, không thuê nguyên xe.       | Toàn bộ 100% một lần khi đặt. Có sơ đồ chọn ghế. |
| **Khách sạn**              | Phòng lưu trú tại các khách sạn Travery sở hữu ở nhiều địa điểm du lịch.                                 | Toàn bộ 100% tiền phòng khi đặt.                 |

### 0.3 Các bên tham gia

| Bên liên quan             | Vai trò                                | Trách nhiệm chính                                                                                                                                                                                           |
| ------------------------- | -------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Khách hàng**            | Người đặt dịch vụ qua ứng dụng Travery | Tìm kiếm, đặt dịch vụ, thanh toán, theo dõi tiến độ, đánh giá sau khi sử dụng. Có thể nhắn tin trực tiếp với Điều phối viên để được tư vấn.                                                                 |
| **Điều phối viên**        | Nhân viên Văn phòng                    | Xác nhận đơn đặt tour, phân công xe và tài xế, xử lý yêu cầu đặc biệt, hỗ trợ khách hàng qua chat, xử lý hoàn tiền/hủy booking và giám sát tổng thể vận hành.                                               |
| **Hướng dẫn viên**        | Hướng dẫn viên du lịch                 | Trực tiếp dẫn đoàn, cập nhật các mốc tiến độ trên thực địa, báo cáo sự cố khi phát sinh và hỗ trợ khách hàng tại hiện trường.                                                                               |
| **Lễ tân**                | Nhân viên tại quầy khách sạn           | Xác nhận nhận phòng và trả phòng, xử lý dịch vụ tiện ích phát sinh, hỗ trợ khách hàng lưu trú tại khách sạn. Chỉ liên hệ Điều phối viên khi gặp sự cố nghiêm trọng vượt ngoài khả năng xử lý của khách sạn. |
| **Quản trị viên (Admin)** | Quản trị hệ thống                      | Xem báo cáo, thống kê tổng quan về hoạt động kinh doanh và vận hành của nền tảng. Quản lý (tạo mới, phân quyền, xóa/vô hiệu hóa) các tài khoản nội bộ của Travery.                                          |

### 0.4 Tài khoản & Xác thực

| Tính năng                | Mô tả                                                                                                                                                                             |
| ------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Đăng ký                  | Khách hàng tạo tài khoản bằng email + mật khẩu. Xác thực qua email trước khi kích hoạt tài khoản.                                                                                 |
| Đăng nhập                | Đăng nhập bằng email + mật khẩu, hoặc xác thực một lần nếu quên mật khẩu.                                                                                                         |
| Bắt buộc đăng nhập       | Khách hàng phải đăng nhập để đặt dịch vụ, xem lịch sử booking và sử dụng Chat. Khách chưa đăng nhập có thể duyệt danh sách tour/xe/khách sạn nhưng không thể đặt.                 |
| Tài khoản nhân viên      | Quản trị viên, Điều phối viên, Hướng dẫn viên và Lễ tân đăng nhập bằng tài khoản nội bộ. Tài khoản nhân viên không trùng với tài khoản khách hàng.                                |
| Quản lý tài khoản nội bộ | Quản trị viên (Admin) thực hiện việc tạo mới, cấp thông tin đăng nhập và xóa/vô hiệu hóa các tài khoản nội bộ của nhân viên Travery. Tài khoản Quản trị viên được cung cấp trước. |
| Phiên làm việc           | Phiên đăng nhập duy trì trên thiết bị đã xác thực. Tự động hết hạn sau **30 ngày** không hoạt động.                                                                               |

---

# QUY TRÌNH 01 — ĐẶT TOUR TRỌN GÓI

> Tour trọn gói là dịch vụ chủ lực của Travery. Khách hàng thanh toán toàn bộ ngay khi đặt và chờ Điều phối viên xác nhận — vì Điều phối viên cần phân công xe và tài xế phù hợp trước khi chính thức xác nhận đoàn.

- **Note — Add-ons:** Với các tour có bao gồm đêm lưu trú tại khách sạn Travery, khách hàng có thể đặt dịch vụ tiện ích (bữa ăn, spa, giặt ủi…) trong thời gian ở khách sạn thông qua cùng giao diện add-on của Quy trình 03. Tab **"Dịch vụ tiện ích"** được mở khoá sau khi Lễ tân xác nhận nhận phòng. Chi phí add-on được thanh toán riêng khi trả phòng — không gộp vào giá tour. Xem chi tiết tại _[3.3](#33-dịch-vụ-tiện-ích-trong-thời-gian-lưu-trú-add-on)_.---

### 1.1 Quy trình đặt Tour

| Bước  | Bên thực hiện  | Nội dung thực hiện                                                                                                                                                      |
| :---: | -------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1** | Khách hàng     | Xem danh sách tour trên ứng dụng. Lọc theo ngày khởi hành, điểm đến, loại tour và khoảng giá.                                                                           |
| **2** | Khách hàng     | Chọn tour, xem chi tiết lịch trình, hình ảnh, giá và số chỗ còn trống. Nhấn **"Đặt tour"**.                                                                             |
| **3** | Khách hàng     | Điền đầy đủ thông tin đặt tour: _số người lớn_, _số trẻ em_, _danh sách thành viên_ (họ tên + CCCD/Hộ chiếu + chế độ ăn uống), và _các ghi chú đặc biệt_. Xác nhận đặt. |
| **4** | Khách hàng     | Thanh toán toàn bộ 100% giá trị tour qua cổng thanh toán. Chỗ được tạm giữ trong vòng **15 phút** chờ hoàn tất thanh toán.                                              |
| **5** | Điều phối viên | Nhận thông báo thanh toán thành công. Kiểm tra thông tin booking, xem danh sách thành viên và yêu cầu của đoàn.                                                         |
| **6** | Điều phối viên | Phân công xe (loại xe, biển số, sức chứa) và tài xế phù hợp từ danh sách nội bộ. Xác nhận booking.                                                                      |
| **7** | Điều phối viên | Ứng dụng tự động gửi thông báo xác nhận đến khách hàng, kèm thông tin Tour, xe và tài xế được phân công.                                                                |
| **8** | Hướng dẫn viên | Vào ngày khởi hành: xác nhận đoàn đã xuất phát. Trong suốt hành trình, cập nhật các mốc tiến độ (đón khách → nhận phòng → tham quan → trả phòng → kết thúc).            |
| **9** | Khách hàng     | Đánh giá chất lượng tour theo thang 1–5 sao và để lại nhận xét.                                                                                                         |

### 1.2 Thông tin cần cung cấp khi đặt Tour

| Thông tin                                                                     | Bắt buộc | Mục đích                                                                                   |
| ----------------------------------------------------------------------------- | :------: | ------------------------------------------------------------------------------------------ |
| Số người lớn (từ 12 tuổi trở lên)                                             |    ✅    | Tính giá tour và sắp xếp phòng.                                                            |
| Số trẻ em (dưới 12 tuổi)                                                      |    ✅    | Tính giá ưu đãi trẻ em và sắp xếp chỗ ngủ.                                                 |
| Họ tên người đặt & số điện thoại                                              |    ✅    | Đầu mối liên lạc chính giữa Hướng dẫn viên và đoàn.                                        |
| Danh sách thành viên (họ tên + CCCD/Hộ chiếu/Giấy khai sinh + chế độ ăn uống) |    ✅    | Làm thủ tục nhận phòng trong tour và tính giá suất ăn phù hợp. Trẻ em dùng Giấy khai sinh. |
| Ghi chú đặc biệt                                                              |    —     | Hỗ trợ xe lăn, nhu cầu y tế, tổ chức sinh nhật trong chuyến đi…                            |

### 1.3 Quy trình hủy Tour & Chính sách hoàn tiền

> Khách hàng có thể thao tác **hủy toàn bộ booking** hoặc **hủy lẻ từng người** trực tiếp trên ứng dụng bất cứ lúc nào **trước khi đoàn khởi hành**. Hệ thống sẽ tự động tính toán tiền hoàn cho số người bị hủy. Việc thay đổi thông tin người đi (không hủy) chỉ được thực hiện trước khi danh sách bị khóa. Sau khi tour đã bắt đầu, nút Hủy sẽ bị vô hiệu hóa.

| Bước  | Bên thực hiện  | Nội dung thực hiện                                                                                                                   |
| :---: | -------------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| **1** | Khách hàng     | Vào màn hình chi tiết booking, nhấn **"Hủy Tour"**. Ứng dụng hiển thị số tiền được hoàn dựa trên số ngày còn lại đến ngày khởi hành. |
| **2** | Khách hàng     | Xác nhận hủy, ghi lý do (không bắt buộc).                                                                                            |
| **3** | Điều phối viên | Nhận yêu cầu hủy, xử lý hoàn tiền theo chính sách và liên hệ khách nếu cần làm rõ.                                                   |
| **4** | Điều phối viên | Xác nhận hoàn tất hủy booking. Chỗ đã đặt được giải phóng cho khách hàng khác.                                                       |

**Bảng chính sách hoàn tiền — Tour**
|Thời điểm hủy|Hoàn tiền|Ghi chú|
|---|:-:|---|
|Hủy trước 7 ngày trở lên|**100%**|Không mất phí hủy.|
|Hủy trong vòng 3–6 ngày trước ngày khởi hành|**50%**|Phí hủy muộn tương đương 50% giá trị tour.|
|Hủy trong vòng 24 giờ đến dưới 3 ngày trước ngày khởi hành|**0%**|Phí hủy khẩn cấp — giữ toàn bộ tiền đã thanh toán.|

> Nếu Travery là bên chủ động hủy hoặc thay đổi tour, khách hàng sẽ được hoàn **100%** không điều kiện.

### 1.4 Chính sách No Show-up — Tour

> **No show-up** là trường hợp khách hàng có booking đã xác nhận nhưng không xuất hiện tại điểm đón vào ngày và giờ khởi hành đã thỏa thuận.

| Tình huống                               | Xử lý                                                                                                                      |
| ---------------------------------------- | -------------------------------------------------------------------------------------------------------------------------- |
| Khách không có mặt tại điểm đón đúng giờ | Hướng dẫn viên chờ tối đa **15 phút**, sau đó liên hệ số điện thoại đầu mối của đoàn.                                      |
| Không liên hệ được sau 15 phút           | Hướng dẫn viên ghi nhận vắng mặt trên ứng dụng, đoàn khởi hành đúng lịch.                                                  |
| Chính sách hoàn tiền                     | **Không hoàn tiền** — khách đã thanh toán toàn bộ, việc không có mặt không được coi là hủy tour.                           |
| Ghi nhận hệ thống                        | Booking chuyển trạng thái `NO_SHOW`. Lưu vết thời gian và ghi chú của Hướng dẫn viên để phục vụ đối soát nếu có khiếu nại. |

---

### 1.5 Ràng buộc — Tour

| Ràng buộc           | Quy định                                                                                                               | Xử lý hệ thống                                                                                                                                                              |
| ------------------- | ---------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Số lượng tối thiểu  | Mỗi tour cần tối thiểu **10 người** để khởi hành.                                                                      | Ứng dụng hiển thị số chỗ còn lại. Khi tour đã đủ 10 người, trạng thái chuyển sang "Đã đủ số lượng".                                                                         |
| Số lượng tối đa     | Mỗi tour tối đa **30 người**.                                                                                          | Nút **"Đặt tour"** bị vô hiệu hoá khi tour đã đủ 30 người. Không cho phép tạo booking mới.                                                                                  |
| Tour không đủ khách | Nếu **3 ngày trước khởi hành** vẫn dưới 10 người, Travery có thể dời ngày hoặc hoàn **100%** cho toàn bộ khách đã đặt. | Ứng dụng gửi cảnh báo tự động cho Điều phối viên khi còn 3 ngày mà chưa đủ số lượng tối thiểu. Điều phối viên liên hệ khách qua Chat để thông báo phương án.                |
| Chốt danh sách      | Khách hàng phải hoàn tất thông tin danh sách thành viên **trước 5 ngày** so với ngày khởi hành.                        | Ứng dụng gửi nhắc nhở khi còn 6 ngày. Sau mốc 5 ngày, tính năng thêm mới/sửa thông tin bị khoá (chỉ cho phép hủy lẻ thành viên). Mọi thay đổi thông tin khác phải qua Chat. |
| Sức khoẻ & độ tuổi  | Trẻ em dưới 10 tuổi hoặc người cao tuổi cần có người thân đi kèm. Khách tự chịu trách nhiệm về điều kiện sức khoẻ.     | Hiển thị lưu ý khi khách nhập số trẻ em dưới 10 tuổi trong form đặt tour.                                                                                                   |

# QUY TRÌNH 02 — ĐẶT VÉ XE KHÁCH

> Travery vận hành các tuyến xe khách liên tỉnh cố định. Khách hàng chọn chuyến, chọn ghế theo sơ đồ và thanh toán toàn bộ một lần — không cần chờ Điều phối viên xác nhận.

---

### 2.1 Quy trình đặt vé Xe

| Bước  | Bên thực hiện  | Nội dung thực hiện                                                                                                                                                                                                           |
| :---: | -------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1** | Khách hàng     | Nhập điều kiện tìm kiếm: _điểm đi_, _điểm đến_ và _ngày khởi hành_. Ứng dụng hiển thị danh sách các chuyến xe Travery còn chỗ trống trên tuyến đó.                                                                           |
| **2** | Khách hàng     | Lọc chuyến phù hợp theo: **Loại xe** (Ghế / Giường), **Giờ đi** (Sáng sớm 04:00–07:00, Sáng 07:00–12:00, Chiều 12:00–17:00, Tối 17:00–23:00), **Tầng** (Trên / Dưới), **Hàng ghế** (Đầu / Giữa / Cuối). Chọn chuyến phù hợp. |
| **3** | Khách hàng     | Chọn _số lượng vé_ cần mua. Sau đó chọn từng _vị trí ghế_ cụ thể trên sơ đồ (mỗi vé = 1 ghế). Toàn bộ ghế được giữ trong vòng **15 phút** để hoàn tất thanh toán.                                                            |
| **4** | Khách hàng     | Điền thông tin đơn đặt: _họ tên_ và _số điện thoại người đại diện_, _điểm đón/trả_, và _ghi chú_ nếu có. Xác nhận đặt.                                                                                                       |
| **5** | Khách hàng     | Thanh toán toàn bộ tiền vé qua cổng thanh toán. Ngay sau khi thành công, vé điện tử được gửi về ứng dụng.                                                                                                                    |
| **6** | Điều phối viên | Có các chuyến xe được cố định. Phân công lại xe cụ thể và tài xế cho chuyến nếu có vấn đề phát sinh.                                                                                                                         |
| **7** | Hướng dẫn viên | Cập nhật trạng thái hành trình (đã khởi hành → đang trên đường → đã đến nơi).                                                                                                                                                |
| **8** | Khách hàng     | Nhận thông báo các mốc hành trình theo thời gian thực. Đánh giá chất lượng chuyến xe sau khi đến nơi.                                                                                                                        |

### 2.2 Thông tin cần cung cấp khi đặt vé Xe

| Thông tin             | Bắt buộc | Mục đích                                                                |
| --------------------- | :------: | ----------------------------------------------------------------------- |
| Họ tên người đặt      |    ✅    | Người đại diện cho toàn bộ vé trong đơn — là đầu mối liên hệ.           |
| Số điện thoại liên hệ |    ✅    | Nhận thông báo, mã vé và tài xế liên hệ khi cần.                        |
| Số lượng vé           |    ✅    | Tổng số vé cần mua. Mỗi vé gắn với một ghế cụ thể đã chọn trên sơ đồ.   |
| Điểm đón cụ thể       |    ✅    | Chọn từ các điểm đón Travery niêm yết — áp dụng chung cho cả đoàn.      |
| Điểm trả khách cụ thể |    ✅    | Chọn từ các điểm trả Travery niêm yết — áp dụng chung cho cả đoàn.      |
| Ví trí ghế cụ thể     |    ✅    | Chọn vị trí ghế. Vị trí ghế là một mã duy nhất xác định ghế đó.         |
| Ghi chú               |    —     | Hỗ trợ hành lý cồng kềnh, mang theo xe đạp, thú cưng (nếu có quy định)… |

### 2.3 Cách tính giá vé Xe

| Thành phần         | Cách tính                                                                                                  |
| ------------------ | ---------------------------------------------------------------------------------------------------------- |
| Giá tuyến niêm yết | Mỗi tuyến A → B có _mức giá cố định_ do Travery quy định (VD: Sài Gòn – Đà Lạt = 180.000 đ/vé ghế thường). |
| Thanh toán         | Toàn bộ tiền vé thanh toán **một lần** khi đặt.                                                            |

### 2.4 Chính sách hủy vé Xe & Chính sách hoàn tiền

> Khách hàng có thể hủy vé xe bất cứ lúc nào **trước khi xe khởi hành**. Sau khi xe đã lăn bánh, không thể hủy — mọi vấn đề phát sinh cần liên hệ Điều phối viên qua Chat.

| Bước  | Bên thực hiện  | Nội dung thực hiện                                                                                                                  |
| :---: | -------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| **1** | Khách hàng     | Vào màn hình chi tiết booking, nhấn **"Hủy vé"**. Ứng dụng hiển thị số tiền được hoàn dựa trên thời gian còn lại đến giờ khởi hành. |
| **2** | Khách hàng     | Xác nhận hủy, ghi lý do (không bắt buộc).                                                                                           |
| **3** | Điều phối viên | Nhận yêu cầu hủy, xử lý hoàn tiền theo chính sách và liên hệ khách nếu cần làm rõ.                                                  |
| **4** | Điều phối viên | Xác nhận hoàn tất hủy booking. Chỗ đã đặt được giải phóng cho khách hàng khác.                                                      |

**Bảng chính sách hoàn tiền — Vé Xe**

| Thời điểm hủy                        | Hoàn tiền | Ghi chú                                                  |
| ------------------------------------ | :-------: | -------------------------------------------------------- |
| Hủy trước 2 giờ so với giờ khởi hành | **100%**  | Không mất phí hủy.                                       |
| Hủy dưới 2 giờ trước giờ khởi hành   |  **0%**   | Quá gần giờ khởi hành, xe đã được bố trí.                |
| Sau khi xe đã khởi hành              |  **0%**   | Liên hệ Điều phối viên qua Chat để được hỗ trợ ngoại lệ. |

> Nếu Travery là bên chủ động hủy hoặc thay đổi lịch xe, khách hàng sẽ được hoàn **100%** không điều kiện.

### 2.5 Chính sách No Show-up — Xe

> **No show-up** là trường hợp khách hàng có vé đã xác nhận nhưng không có mặt tại điểm đón khi xe đến hoặc khi xe chuẩn bị khởi hành.

| Tình huống                      | Xử lý                                                                                                                                              |
| ------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| Khách không có mặt tại điểm đón | Xe **không chờ thêm** vì khách vắng mặt — đảm bảo đúng giờ cho các hành khách còn lại.                                                             |
| Chính sách hoàn tiền            | **Không hoàn tiền** — vé đã thanh toán, ghế đã được giữ. Khác với trường hợp hủy chủ động trước 2 giờ.                                             |
| Ghi nhận hệ thống               | Hướng dẫn viên đánh dấu vắng mặt cho vé tương ứng. Ghế bỏ trống không ảnh hưởng đến các vé còn lại trong đơn. Booking chuyển trạng thái `NO_SHOW`. |

### 2.6 Ràng buộc — Xe

| Ràng buộc        | Quy định                                                                                                     | Xử lý hệ thống                                                                                                     |
| ---------------- | ------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------ |
| Thời gian có mặt | Khách hàng phải có mặt tại điểm đón trước 15–30 phút. Nếu khách đến trễ, vé mất hiệu lực và không hoàn tiền. | Ứng dụng hiển thị cảnh báo thời gian có mặt trên vé điện tử và gửi thông báo nhắc nhở trước giờ khởi hành 1 tiếng. |
| Quy định hành lý | Tối đa 20 kg hành lý miễn phí/khách. Từ chối hành lý cồng kềnh hoặc động vật.                                | Bổ sung checkbox bắt buộc: "Tôi đồng ý với quy định hành lý và thời gian có mặt" tại bước Thanh toán.              |

---

# QUY TRÌNH 03 — ĐẶT PHÒNG KHÁCH SẠN

> Travery sở hữu khách sạn tại nhiều điểm du lịch trong nước. Khách hàng thanh toán toàn bộ tiền phòng ngay khi đặt, phòng được xác nhận ngay — không cần chờ Điều phối viên duyệt. Dịch vụ tiện ích phát sinh trong kỳ lưu trú được thanh toán riêng khi trả phòng.

---

### 3.1 Quy trình Tìm kiếm & Đặt phòng

| Bước  | Bên thực hiện | Nội dung thực hiện                                                                                                                                                                                                                        |
| :---: | ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1** | Khách hàng    | Nhập điều kiện tìm kiếm: _địa điểm_, _ngày nhận phòng_ và _ngày trả phòng_. Ứng dụng hiển thị danh sách khách sạn Travery còn phòng trống tại địa điểm đó. Hỗ trợ các bộ lọc: khoảng giá, khu vực, đánh giá, tiện nghi, số lượng người ở. |
| **2** | Khách hàng    | Chọn khách sạn, xem chi tiết: hình ảnh, mô tả, vị trí, đánh giá, tiện nghi và danh sách các loại phòng còn trống (Mã phòng, Giá, Mô tả, Hình ảnh, Số giường, Số lượng người tối đa).                                                      |
| **3** | Khách hàng    | Chọn loại phòng phù hợp, nhấn **"Đặt phòng này"**.                                                                                                                                                                                        |
| **4** | Khách hàng    | Điền thông tin đặt phòng: _họ tên_ và _số điện thoại_ người đặt, _danh sách thành viên_ (họ tên + CCCD/Hộ chiếu) và _yêu cầu đặc biệt_ (nếu có — lưu ý có thể không được đáp ứng tùy tình trạng thực tế của khách sạn).                   |
| **5** | Khách hàng    | Thanh toán toàn bộ 100% tiền phòng qua cổng thanh toán. Phòng được tạm giữ **15 phút** chờ thanh toán. Phòng được xác nhận và khóa ngay khi thành công.                                                                                   |

### 3.2 Quy trình Nhận phòng & Trả phòng

| Bước  | Bên thực hiện | Nội dung thực hiện                                                                                                                                                                             |
| :---: | ------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1** | Khách hàng    | Khách đến quầy lễ tân, xuất trình thông tin đặt phòng.                                                                                                                                         |
| **2** | Lễ tân        | Xác minh thông tin đặt phòng và danh sách thành viên. Bấm xác nhận **nhận phòng** trên ứng dụng. Tab "Dịch vụ tiện ích" của khách được mở khóa.                                                |
| **3** | Khách hàng    | Trong thời gian lưu trú, khách có thể đặt thêm dịch vụ tiện ích qua ứng dụng xem mục _[4.3](#43-dịch-vụ-tiện-ích-trong-thời-gian-lưu-trú-add-on)_. Chi phí phát sinh được ghi vào bill add-on. |
| **4** | Khách hàng    | Khi khách trả phòng, khách sẽ gửi yêu cầu check-out.                                                                                                                                           |
| **5** | Lễ tân        | Kiểm tra tình trạng phòng, tổng hợp các dịch vụ add-on và phí phát sinh, gửi bill cuối cùng cho khách.                                                                                         |
| **6** | Khách hàng    | Xem và thanh toán bill (nếu có) qua cổng thanh toán. Nếu không có add-on, booking tự động hoàn tất.                                                                                            |
| **7** | Khách hàng    | Để lại đánh giá khách sạn.                                                                                                                                                                     |

> Check-in chỉ khả dụng từ **12:00 trưa** ngày nhận phòng trở đi. Check-out trước **12:00 trưa** ngày rời đi. Trả phòng trễ sẽ bị tính phí thuê thêm (20.000 VND / giờ) và tự động cộng vào hóa đơn cuối cùng. Check-out do Lễ tân thực hiện sau khi kiểm tra phòng — tránh trường hợp khách tự kết thúc khi chưa hoàn tất thủ tục.

### 3.3 Dịch vụ tiện ích trong thời gian lưu trú (Add-On)

> Chỉ khả dụng khi booking đang ở trạng thái **đã nhận phòng**. Toàn bộ chi phí add-on ghi vào bill và thanh toán chung khi trả phòng. Khách hàng có thể hủy/sửa lịch Add-on trên ứng dụng trước giờ sử dụng tối thiểu **2 giờ**. Nếu hủy quá hạn, ứng dụng chặn thao tác và yêu cầu liên hệ Lễ tân.

| Bước  | Bên thực hiện | Nội dung thực hiện                                                                                          |
| :---: | ------------- | ----------------------------------------------------------------------------------------------------------- |
| **1** | Khách hàng    | Vào tab **"Dịch vụ tiện ích"** trong màn hình booking đang lưu trú.                                         |
| **2** | Khách hàng    | Chọn loại dịch vụ (bữa ăn, spa, giặt ủi…), số lượng, ngày và giờ sử dụng. Ghi chú yêu cầu đặc biệt nếu cần. |
| **3** | Lễ tân        | Nhận thông báo và phối hợp thực hiện dịch vụ cho khách.                                                     |
| **4** | Khách hàng    | Theo dõi hóa đơn tổng bất kỳ lúc nào qua mục **"Hóa đơn hiện tại"**.                                        |

**Các dịch vụ tiện ích thường có**

| Loại           | Ví dụ                                                      | Lưu ý đặt trước                                                                          |
| -------------- | ---------------------------------------------------------- | ---------------------------------------------------------------------------------------- |
| Bữa ăn         | Sáng, trưa, tối theo thực đơn khách sạn.                   | Nên đặt trước ít nhất **2 giờ**.                                                         |
| Spa & thư giãn | Massage, chăm sóc da, ngâm chân…                           | Nên đặt trước **1 ngày** để sắp xếp kỹ thuật viên.                                       |
| Giặt ủi        | Giặt sấy, giặt hấp quần áo.                                | Thời gian hẹn là giờ khách mang đồ gửi Lễ tân. Lễ tân sẽ gửi thông báo khi đồ giặt xong. |
| Dịch vụ khác   | Thuê xe đạp, đặt xe di chuyển, dịch vụ phòng theo yêu cầu… | Tùy từng khách sạn — liên hệ Lễ tân để xác nhận.                                         |

### 3.4 Thông tin cần cung cấp khi đặt phòng

| Thông tin                                     | Bắt buộc | Mục đích                                                                                                                                                      |
| --------------------------------------------- | :------: | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Họ tên người đặt                              |    ✅    | Người đại diện cho toàn bộ booking — là đầu mối liên lạc chính.                                                                                               |
| Số điện thoại người đặt                       |    ✅    | Nhận thông báo xác nhận và liên hệ khi cần.                                                                                                                   |
| Danh sách thành viên (họ tên + CCCD/Hộ chiếu) |    ✅    | Xác minh danh tính khi nhận phòng.                                                                                                                            |
| Yêu cầu đặc biệt                              |    —     | Tầng cao/thấp, hướng nhìn ra biển/vườn, giường đôi/đơn, phòng liền kề… Lưu ý: các yêu cầu này có thể không được đáp ứng tùy tình trạng thực tế của khách sạn. |

### 3.5 Chính sách Hủy phòng & Chính sách hoàn tiền

> Khách hàng có thể hủy booking trực tiếp trên ứng dụng trước ngày nhận phòng. Sau khi đã check-in, không thể hủy — mọi vấn đề phát sinh cần liên hệ Điều phối viên qua Chat.

| Bước  | Bên thực hiện | Nội dung thực hiện                                                                                                                            |
| :---: | ------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| **1** | Khách hàng    | Vào màn hình chi tiết booking, nhấn **"Hủy phòng"**. Ứng dụng hiển thị số tiền được hoàn dựa trên thời gian còn lại đến giờ check-in dự kiến. |
| **2** | Khách hàng    | Xác nhận hủy, ghi lý do (không bắt buộc).                                                                                                     |
| **3** | Lễ tân        | Nhận yêu cầu hủy, xử lý hoàn tiền theo chính sách và liên hệ khách nếu cần làm rõ.                                                            |
| **4** | Lễ tân        | Xác nhận hoàn tất hủy booking. Phòng được giải phóng để nhận đặt mới.                                                                         |

**Bảng chính sách hoàn tiền — Khách Sạn**

| Thời điểm hủy                        | Hoàn tiền | Ghi chú                                                  |
| ------------------------------------ | --------- | -------------------------------------------------------- |
| Trước 72 giờ so với giờ check-in     | 100%      | Không mất phí hủy                                        |
| Từ 24–72 giờ trước giờ check-in      | 50%       | Quá gần giờ check-in, khách sạn đã được bố trí.          |
| Trong vòng 24 giờ trước giờ check-in | 0%        | Liên hệ Điều phối viên qua Chat để được hỗ trợ ngoại lệ. |

> Tiền hoàn sẽ được trả về phương thức thanh toán ban đầu trong vòng **5–7 ngày làm việc** kể từ khi hủy thành công.

> Nếu Travery là bên chủ động hủy hoặc thay đổi booking, khách hàng sẽ được hoàn **100%** không điều kiện.

> Nếu hủy do sự cố bất khả kháng (thiên tai, tai nạn, bệnh cấp cứu…), khách liên hệ Điều phối viên qua Chat để được xem xét theo từng trường hợp cụ thể.

### 3.6 Chính sách No Show-up — Khách Sạn

> **No show-up** là trường hợp khách hàng có booking đã xác nhận nhưng không đến nhận phòng trong ngày check-in đã đặt.

| Tình huống                                         | Xử lý                                                                                                                                                                                 |
| -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Khách không đến trong ngày check-in (trước 23:59)  | Phòng vẫn được giữ nguyên cho khách đến hết **23:59** ngày check-in. Lễ tân chủ động liên hệ số điện thoại đầu mối để xác nhận.                                                       |
| Khách thông báo đến muộn sau 23:59                 | Travery xem xét giữ phòng sang ngày hôm sau theo từng trường hợp cụ thể. Không áp dụng tự động — cần Lễ tân xác nhận.                                                                 |
| Không liên hệ và không đến sau 23:59 ngày check-in | Phòng được giải phóng, booking chuyển trạng thái `NO_SHOW`.                                                                                                                           |
| Chính sách hoàn tiền                               | **Không hoàn tiền** — Phòng đã được giữ riêng và không thể nhận khách khác trong suốt ngày check-in. Trường hợp có lý do bất khả kháng, khách liên hệ Điều phối viên để được xem xét. |
| Ghi nhận hệ thống                                  | Lễ tân đánh dấu `NO_SHOW` trên hệ thống. Phòng được mở khóa để nhận đặt mới.                                                                                                          |

---

# QUY TRÌNH 04 — CHAT TƯ VẤN & CUSTOM TOUR

> Kênh chat trực tiếp là điểm khác biệt của Travery. Không chỉ là kênh hỗ trợ sau sự cố — đây là nơi tư vấn chuyên sâu và là cổng để khách hàng thiết kế chương trình du lịch hoàn toàn theo ý muốn (Custom Tour).

---

### 4.1 Kênh Chat — Tư vấn & Chăm sóc khách hàng

| Tính năng                | Mô tả                                                                                                                                    |
| ------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------- |
| Khởi tạo cuộc trò chuyện | Khách hàng có thể bắt đầu nhắn tin từ trang chủ, trang chi tiết bất kỳ dịch vụ nào, hoặc ngay trong màn hình booking đang diễn ra.       |
| Nhắn tin thời gian thực  | Tin nhắn hiển thị ngay lập tức cho cả hai bên. Trạng thái tin nhắn: đã gửi / đã nhận / đã đọc.                                           |
| Hộp thư Điều phối viên   | Điều phối viên thấy toàn bộ danh sách cuộc trò chuyện, sắp xếp theo tin nhắn mới nhất. Badge số tin chưa đọc hiển thị rõ để không bỏ lỡ. |
| Thông báo đẩy            | Cả khách hàng lẫn Điều phối viên đều nhận thông báo khi có tin nhắn mới, kể cả khi không đang mở ứng dụng.                               |

> Khách hàng được khuyến khích nhắn tin **trước khi đặt** để được tư vấn lộ trình, so sánh các tour, hỏi về chính sách hoặc yêu cầu điều chỉnh đặc biệt.

### 4.2 Custom Tour — Thiết kế chương trình theo yêu cầu

Custom Tour là dịch vụ Travery thiết kế chương trình du lịch riêng theo đúng yêu cầu của khách — điểm đến, lịch trình, ngân sách, sở thích, số người và thời gian hoàn toàn tùy chỉnh. Toàn bộ quá trình tư vấn diễn ra qua Chat với Điều phối viên. Chỉ khi hai bên đã chốt xong, Điều phối viên mới tạo chương trình và gửi link đặt chính thức cho khách.

| Bước  | Bên thực hiện  | Nội dung thực hiện                                                                                                                                                               |
| :---: | -------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1** | Khách hàng     | Nhắn tin cho Điều phối viên, mô tả yêu cầu: điểm muốn đến, thời gian đi, số người, ngân sách dự kiến và sở thích đặc biệt (ưu tiên biển/núi, thời gian tự do, yêu cầu ẩm thực…). |
| **2** | Điều phối viên | Trao đổi qua lại để làm rõ yêu cầu. Đề xuất lộ trình, danh sách điểm đến, các hoạt động, loại phương tiện, loại phòng và mức giá phù hợp.                                        |
| **3** | Cả hai bên     | Thảo luận và điều chỉnh cho đến khi đạt thỏa thuận về chương trình, giá cả và các điều khoản đi kèm.                                                                             |
| **4** | Điều phối viên | Tạo chương trình Custom Tour trong hệ thống với đầy đủ thông tin đã thỏa thuận. Tạo tour riêng dành cho khách.                                                                   |
| **5** | Khách hàng     | Nhận được thông báo xác nhận, xem lại chi tiết Custom Tour trên ứng dụng và tiến hành qua cổng thanh toán.                                                                       |
| **6** | Điều phối viên | Phân công xe và tài xế. Custom Tour được xử lý hoàn toàn giống Tour thường từ bước này.                                                                                          |
| **7** | Hướng dẫn viên | Trực tiếp thực thi trên thực địa: dẫn đoàn, lái xe, cập nhật tiến độ và báo cáo sự cố.                                                                                           |

> Custom Tour phù hợp với: đoàn doanh nghiệp (team building, company trip), gia đình đặt nguyên đoàn, khách muốn lịch trình linh hoạt hoặc điểm đến không có trong tour cố định.

### 4.3 Quy trình hủy Custom Tour & Chính sách hoàn tiền

> Khách hàng có thể yêu cầu hủy Custom Tour bất cứ lúc nào **trước khi đoàn khởi hành**. Sau khi tour đã bắt đầu, không thể hủy — mọi vấn đề phát sinh cần liên hệ Điều phối viên qua Chat.

Do Custom Tour được tư vấn và xác nhận trực tiếp qua Chat, yêu cầu hủy cũng thực hiện qua kênh này thay vì thao tác trực tiếp trên ứng dụng.

| Bước  | Bên thực hiện  | Nội dung thực hiện                                                                                                                                     |
| :---: | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **1** | Khách hàng     | Nhắn yêu cầu hủy cho Điều phối viên qua kênh Chat đã thiết lập. Điều phối viên phản hồi số tiền được hoàn dựa trên số ngày còn lại đến ngày khởi hành. |
| **2** | Khách hàng     | Xác nhận hủy, ghi lý do (không bắt buộc).                                                                                                              |
| **3** | Điều phối viên | Nhận yêu cầu hủy, xử lý hoàn tiền theo chính sách và liên hệ khách nếu cần làm rõ.                                                                     |
| **4** | Điều phối viên | Xác nhận hoàn tất hủy booking. Chỗ đã đặt được giải phóng.                                                                                             |

**Bảng chính sách hoàn tiền — Custom Tour**

Áp dụng hoàn toàn giống Tour thường, xem mục _[1.3](#13-quy-trình-hủy-tour--chính-sách-hoàn-tiền)_.

> Nếu Travery là bên chủ động hủy hoặc thay đổi Custom Tour, khách hàng sẽ được hoàn **100%** không điều kiện.

### 4.4 Chính sách No Show-up — Custom Tour

Custom Tour vận hành giống Tour thường sau khi khách thanh toán. Chính sách no show-up áp dụng **hoàn toàn giống Quy trình 01 — Tour** xem mục _[1.4](#14-chính-sách-no-show-up--tour)_: Hướng dẫn viên chờ 15 phút tại điểm đón, ghi nhận vắng mặt, không hoàn tiền và chuyển trạng thái `NO_SHOW`.

> Do Custom Tour được tư vấn trực tiếp qua Chat, nếu khách có thay đổi kế hoạch vào phút chót, Điều phối viên nên **chủ động liên hệ qua kênh Chat đã thiết lập** để xác nhận trước ngày khởi hành — giúp tránh tình huống no show-up không cần thiết.

### 4.5 Các tình huống Chat thường gặp

| Tình huống                         | Khách hàng cần làm gì                                                                                                  | Điều phối viên xử lý                                                           |
| ---------------------------------- | ---------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------ |
| Hỏi thêm về tour cụ thể            | Nhắn tin từ trang chi tiết tour, đặt câu hỏi.                                                                          | Giải đáp chi tiết lịch trình, điều kiện thời tiết, gợi ý thời điểm đi phù hợp. |
| Yêu cầu thay đổi thông tin booking | Nhắn tin mô tả thay đổi cần thực hiện (thêm người, đổi loại phòng, ghi chú đặc biệt).                                  | Cập nhật booking nếu còn khả dụng, thông báo nếu có phát sinh thêm chi phí.    |
| Tour đang diễn ra có vấn đề        | Nhắn tin mô tả sự cố cần hỗ trợ.                                                                                       | Hỗ trợ từ xa hoặc điều phối Hướng dẫn viên xử lý trực tiếp tại chỗ.            |
| Muốn thiết kế Custom Tour          | Nhắn tin mô tả yêu cầu, bắt đầu quy trình tư vấn xem mục _[4.2](#42-custom-tour--thiết-kế-chương-trình-theo-yêu-cầu)_. | Tư vấn, đề xuất lộ trình và dẫn dắt toàn bộ quy trình Custom Tour.             |
| Khiếu nại sau khi sử dụng dịch vụ  | Nhắn tin mô tả vấn đề và kết quả mong muốn.                                                                            | Ghi nhận phản hồi, làm rõ sự việc và đề xuất phương án giải quyết phù hợp.     |

### 4.6 Ràng buộc — Custom Tour

| Ràng buộc             | Quy định                                                                                                                                                   | Xử lý hệ thống                                                                                                                                                                                                                          |
| --------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Thời gian gửi yêu cầu | Khách hàng nên gửi yêu cầu ít nhất **7 ngày** trước ngày dự định đi để Travery có đủ thời gian thiết kế và đặt trước dịch vụ.                              | Không block đặt — Điều phối viên chủ động cảnh báo qua Chat nếu yêu cầu đến quá gần ngày đi và không đảm bảo kịp tiến độ chuẩn bị.                                                                                                      |
| Hiệu lực bản thiết kế | Bản thiết kế hành trình và bảng giá do Điều phối viên gửi chỉ có hiệu lực **3 ngày**. Quá hạn, giá các dịch vụ có thể đã thay đổi và cần thương lượng lại. | Khi tạo Custom Tour trong hệ thống, Điều phối viên gắn ngày hết hạn. Ứng dụng tự động nhắc khách **1 ngày trước** khi bản thiết kế sắp hết hạn. Sau khi hết hạn, trạng thái booking chuyển sang `EXPIRED` — cần Điều phối viên tạo lại. |

---

# 5. CHÍNH SÁCH & QUY ĐỊNH CHUNG

### 5.1 Thanh toán

| Dịch vụ       | Cách thanh toán                                                                                | Lưu ý                                                                        |
| ------------- | ---------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------- |
| Tour trọn gói | Toàn bộ **100%** ngay khi đặt.                                                                 | Chỗ tạm giữ **15 phút**. Không thanh toán đúng hạn → đặt chỗ bị hủy tự động. |
| Vé Xe khách   | Toàn bộ **100%** một lần khi đặt vé.                                                           | Ghế giữ **15 phút**. Không thanh toán đúng hạn → ghế được giải phóng.        |
| Khách sạn     | Toàn bộ **100%** tiền phòng ngay khi đặt. Bill add-on (nếu có) thanh toán riêng khi trả phòng. | Phòng được xác nhận và khóa ngay sau khi thanh toán thành công.              |

### 5.2 Đánh giá dịch vụ

Sau khi hoàn tất mỗi dịch vụ (tour, chuyến xe, kỳ lưu trú), khách hàng được mời đánh giá theo thang điểm **1–5 sao** kèm nhận xét bằng chữ. Đánh giá giúp Travery cải thiện chất lượng dịch vụ và hỗ trợ khách hàng khác lựa chọn.

> Travery cam kết phản hồi mọi đánh giá tiêu cực trong vòng **48 giờ làm việc**.

### 5.3 Xử lý sự cố trong hành trình

| Bước  | Bên thực hiện               | Nội dung thực hiện                                                                                                                                              |
| :---: | --------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **1** | Hướng dẫn viên / Khách hàng | Báo cáo sự cố ngay lập tức qua tính năng **"Báo cáo sự cố"** trên ứng dụng hoặc liên hệ trực tiếp qua Chat. Mô tả rõ loại sự cố, mức độ và vị trí hiện tại.     |
| **2** | Điều phối viên              | Nhận cảnh báo ngay lập tức. Đánh giá tình huống và triển khai phương án hỗ trợ: điều xe thay thế, liên hệ bảo hiểm, cơ sở y tế hoặc phương án thay thế dịch vụ. |
| **3** | Điều phối viên              | Cập nhật tiến độ xử lý cho khách hàng qua Chat. Toàn bộ sự cố được ghi lại để phục vụ đối soát, bồi thường hoặc giải quyết khiếu nại sau này.                   |

### 5.4 Cam kết dịch vụ

| Cam kết             | Nội dung                                                                                                                                                                   |
| ------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Xác nhận booking    | Travery xác nhận đặt phòng khách sạn và vé xe **ngay sau** khi thanh toán thành công. Riêng tour trọn gói: xác nhận trong vòng **4 giờ làm việc** sau khi nhận thanh toán. |
| Thông tin minh bạch | Giá hiển thị trên ứng dụng là giá cuối — không phát sinh phụ phí ẩn. Mọi điều chỉnh giá phải được Điều phối viên thông báo và khách đồng ý trước khi áp dụng.              |
| Phản hồi chat       | Điều phối viên phản hồi trong giờ hành chính **(8:00–17:30, Thứ Hai – Thứ Bảy)** trong vòng **30 phút**. Ngoài giờ: phản hồi vào đầu giờ làm việc hôm sau.                 |
| Hoàn tiền           | Tiền hoàn được xử lý trong vòng **5–7 ngày làm việc** sau khi Điều phối viên xác nhận hủy dịch vụ.                                                                         |

### 5.5 Xử lý thanh toán thất bại

| Tình huống                                                    | Xử lý hệ thống                                                                                                                                                       |
| ------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Giao dịch bị từ chối (thẻ từ chối, số dư không đủ…)           | Ứng dụng hiển thị thông báo lỗi cụ thể. Chỗ/ghế/phòng vẫn được giữ trong phần thời gian còn lại của 15 phút. Khách có thể thử lại bằng phương thức thanh toán khác.  |
| Hết 15 phút mà chưa thanh toán thành công                     | Chỗ/ghế/phòng được giải phóng tự động. Booking bị huỷ. Ứng dụng thông báo rõ và gợi ý khách quay lại chọn lại từ đầu.                                                |
| Lỗi kết nối trong khi giao dịch                               | Ứng dụng truy vấn lại trạng thái giao dịch với cổng thanh toán trước khi hiển thị kết quả. Không xác nhận booking cho đến khi nhận được phản hồi thành công từ cổng. |
| Thanh toán thành công nhưng hệ thống không nhận được callback | Booking không được xác nhận tự động. Khách hàng liên hệ Điều phối viên qua Chat kèm bằng chứng giao dịch để xác nhận thủ công.                                       |

---

_— Hết tài liệu nghiệp vụ hệ thống Travery —_
