# Tài khoản Seed Data (Development)

> ⚠️ **Lưu ý:** File này chỉ dành cho môi trường **Development**. Tuyệt đối **KHÔNG** sử dụng các tài khoản này trên Production.

## Thông tin chung

- **Password chung:** `Password@123`
- **Auth Provider:** `LOCAL`
- **Status:** `ACTIVE`
- **Migration file:** `V2__seed_users.sql`

---

## 1. Admin

| Email | Tên | ID |
|:------|:----|:---|
| `admin@travery.com` | Super Admin | `a0000000-...-000000000001` |

---

## 2. Coordinator (Điều phối viên)

| Email | Tên | Mã NV | Phòng ban |
|:------|:----|:------|:----------|
| `coordinator.tour@travery.com` | Nguyễn Văn Hùng | `COO-SEED01` | TOUR |
| `coordinator.hotel@travery.com` | Trần Thị Mai | `COO-SEED02` | HOTEL |
| `coordinator.coach@travery.com` | Lê Minh Tuấn | `COO-SEED03` | COACH |

---

## 3. Guide (Hướng dẫn viên)

| Email | Tên | Mã NV | Bằng HDV | Ngôn ngữ | Kinh nghiệm |
|:------|:----|:------|:---------|:---------|:-------------|
| `guide01@travery.com` | Phạm Quốc Bảo | `GUI-SEED01` | `GL-2024-001` | vi, en | 5 năm |
| `guide02@travery.com` | Hoàng Thị Lan | `GUI-SEED02` | `GL-2024-002` | vi, en, ja | 3 năm |

---

## 4. Receptionist (Lễ tân)

| Email | Tên | Mã NV | Khách sạn | Ca làm |
|:------|:----|:------|:----------|:-------|
| `receptionist01@travery.com` | Võ Thanh Tâm | `REC-SEED01` | Travery Grand Hotel (HCM) | MORNING |
| `receptionist02@travery.com` | Đặng Ngọc Hân | `REC-SEED02` | Travery Beach Resort (Nha Trang) | EVENING |

---

## 5. Tourist (Khách du lịch)

| Email | Tên | Hộ chiếu | Ngày sinh | Giới tính |
|:------|:----|:---------|:----------|:----------|
| `tourist01@travery.com` | Bùi Minh Khôi | `C12345678` | 1995-06-15 | MALE |
| `tourist02@travery.com` | Ngô Thị Hồng | `C98765432` | 1998-11-20 | FEMALE |

---

## 6. Hotels (Khách sạn seed)

| Tên | Địa chỉ | Thành phố | Sao | ID |
|:----|:--------|:----------|:----|:---|
| Travery Grand Hotel | 123 Nguyễn Huệ, Quận 1 | TP. Hồ Chí Minh | ⭐⭐⭐⭐⭐ | `e0000000-...-000000000001` |
| Travery Beach Resort | 456 Trần Phú, Nha Trang | Khánh Hòa | ⭐⭐⭐⭐ | `e0000000-...-000000000002` |
