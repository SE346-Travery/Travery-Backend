-- =============================================
-- Travery Backend - Seed Coaches & Drivers (V5)
-- =============================================

-- 1. COACHES (20 xe)
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000001', '51B-10000', 'SEAT', 29, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000002', '51B-10001', 'SEAT', 29, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000003', '51B-10002', 'SEAT', 29, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000004', '51B-10003', 'SEAT', 29, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000005', '51B-10004', 'SEAT', 29, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000006', '51B-10005', 'SEAT', 29, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000007', '51B-10006', 'SEAT', 29, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000008', '51B-10007', 'SEAT', 29, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000009', '51B-10008', 'BED', 20, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000010', '51B-10009', 'BED', 20, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000011', '51B-10010', 'BED', 20, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000012', '51B-10011', 'BED', 20, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000013', '51B-10012', 'BED', 20, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000014', '51B-10013', 'BED', 20, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000015', '51B-10014', 'BED', 20, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000016', '51B-10015', 'LIMOUSINE', 16, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000017', '51B-10016', 'LIMOUSINE', 16, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000018', '51B-10017', 'LIMOUSINE', 16, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000019', '51B-10018', 'LIMOUSINE', 16, 'ACTIVE', NOW(), NOW());
INSERT INTO coaches (id, license_plate, coach_type, capacity, status, created_at, updated_at) VALUES ('cc000000-0000-0000-0000-000000000020', '51B-10019', 'LIMOUSINE', 16, 'ACTIVE', NOW(), NOW());

-- 2. DRIVERS (20 tài xế, all AVAILABLE)
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000001', 'Tài xế Dương', '0901234500', 'DL-2024-000', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000002', 'Tài xế Toàn', '0901234501', 'DL-2024-001', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000003', 'Tài xế Phát', '0901234502', 'DL-2024-002', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000004', 'Tài xế Hùng', '0901234503', 'DL-2024-003', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000005', 'Tài xế Khoa', '0901234504', 'DL-2024-004', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000006', 'Tài xế Lâm', '0901234505', 'DL-2024-005', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000007', 'Tài xế Đạt', '0901234506', 'DL-2024-006', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000008', 'Tài xế Bình', '0901234507', 'DL-2024-007', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000009', 'Tài xế Châu', '0901234508', 'DL-2024-008', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000010', 'Tài xế Anh', '0901234509', 'DL-2024-009', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000011', 'Tài xế Kiệt', '0901234510', 'DL-2024-010', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000012', 'Tài xế Sang', '0901234511', 'DL-2024-011', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000013', 'Tài xế Hải', '0901234512', 'DL-2024-012', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000014', 'Tài xế Sơn', '0901234513', 'DL-2024-013', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000015', 'Tài xế Hoàng', '0901234514', 'DL-2024-014', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000016', 'Tài xế Nam', '0901234515', 'DL-2024-015', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000017', 'Tài xế Việt', '0901234516', 'DL-2024-016', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000018', 'Tài xế Quân', '0901234517', 'DL-2024-017', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000019', 'Tài xế Long', '0901234518', 'DL-2024-018', 'AVAILABLE', NOW(), NOW());
INSERT INTO drivers (id, full_name, phone_number, license_number, status, created_at, updated_at) VALUES ('dd100000-0000-0000-0000-000000000020', 'Tài xế Tuấn', '0901234519', 'DL-2024-019', 'AVAILABLE', NOW(), NOW());
