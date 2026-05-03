-- =============================================
-- Travery Backend - Seed Users (V2)
-- Seed sample data for all user roles
-- =============================================
-- Password for all seeded users: Password@123
-- BCrypt hash (cost 10): $2a$10$dXJ3SW6G7P50lGmMQoeHhOxht6Mwy/HBt0VbGrAaxOlTzT1FeBv.C

-- =============================================
-- 1. ADMIN
-- =============================================
INSERT INTO users (id, full_name, email, password_hashed, role, status, auth_provider, is_deleted, created_at, updated_at)
VALUES (
    'a0000000-0000-0000-0000-000000000001',
    'Super Admin',
    'admin@travery.com',
    '$2a$10$dXJ3SW6G7P50lGmMQoeHhOxht6Mwy/HBt0VbGrAaxOlTzT1FeBv.C',
    'ADMIN', 'ACTIVE', 'LOCAL', false, NOW(), NOW()
);
INSERT INTO admins (id) VALUES ('a0000000-0000-0000-0000-000000000001');

-- =============================================
-- 2. COORDINATORS (one per department)
-- =============================================

-- Coordinator - Tour Department
INSERT INTO users (id, full_name, email, password_hashed, role, status, auth_provider, is_deleted, created_at, updated_at)
VALUES (
    'c0000000-0000-0000-0000-000000000001',
    'Nguyễn Văn Hùng',
    'coordinator.tour@travery.com',
    '$2a$10$dXJ3SW6G7P50lGmMQoeHhOxht6Mwy/HBt0VbGrAaxOlTzT1FeBv.C',
    'COORDINATOR', 'ACTIVE', 'LOCAL', false, NOW(), NOW()
);
INSERT INTO coordinators (id, employee_code, department) VALUES ('c0000000-0000-0000-0000-000000000001', 'COO-SEED01', 'TOUR');

-- Coordinator - Hotel Department
INSERT INTO users (id, full_name, email, password_hashed, role, status, auth_provider, is_deleted, created_at, updated_at)
VALUES (
    'c0000000-0000-0000-0000-000000000002',
    'Trần Thị Mai',
    'coordinator.hotel@travery.com',
    '$2a$10$dXJ3SW6G7P50lGmMQoeHhOxht6Mwy/HBt0VbGrAaxOlTzT1FeBv.C',
    'COORDINATOR', 'ACTIVE', 'LOCAL', false, NOW(), NOW()
);
INSERT INTO coordinators (id, employee_code, department) VALUES ('c0000000-0000-0000-0000-000000000002', 'COO-SEED02', 'HOTEL');

-- Coordinator - Coach Department
INSERT INTO users (id, full_name, email, password_hashed, role, status, auth_provider, is_deleted, created_at, updated_at)
VALUES (
    'c0000000-0000-0000-0000-000000000003',
    'Lê Minh Tuấn',
    'coordinator.coach@travery.com',
    '$2a$10$dXJ3SW6G7P50lGmMQoeHhOxht6Mwy/HBt0VbGrAaxOlTzT1FeBv.C',
    'COORDINATOR', 'ACTIVE', 'LOCAL', false, NOW(), NOW()
);
INSERT INTO coordinators (id, employee_code, department) VALUES ('c0000000-0000-0000-0000-000000000003', 'COO-SEED03', 'COACH');

-- =============================================
-- 3. GUIDES
-- =============================================

INSERT INTO users (id, full_name, email, password_hashed, role, status, auth_provider, is_deleted, created_at, updated_at)
VALUES (
    'da000000-0000-0000-0000-000000000001',
    'Phạm Quốc Bảo',
    'guide01@travery.com',
    '$2a$10$dXJ3SW6G7P50lGmMQoeHhOxht6Mwy/HBt0VbGrAaxOlTzT1FeBv.C',
    'GUIDE', 'ACTIVE', 'LOCAL', false, NOW(), NOW()
);
INSERT INTO guides (id, employee_code, guide_license, languages, years_experience)
VALUES ('da000000-0000-0000-0000-000000000001', 'GUI-SEED01', 'GL-2024-001', '["vi", "en"]', 5);

INSERT INTO users (id, full_name, email, password_hashed, role, status, auth_provider, is_deleted, created_at, updated_at)
VALUES (
    'da000000-0000-0000-0000-000000000002',
    'Hoàng Thị Lan',
    'guide02@travery.com',
    '$2a$10$dXJ3SW6G7P50lGmMQoeHhOxht6Mwy/HBt0VbGrAaxOlTzT1FeBv.C',
    'GUIDE', 'ACTIVE', 'LOCAL', false, NOW(), NOW()
);
INSERT INTO guides (id, employee_code, guide_license, languages, years_experience)
VALUES ('da000000-0000-0000-0000-000000000002', 'GUI-SEED02', 'GL-2024-002', '["vi", "en", "ja"]', 3);

-- =============================================
-- 4. HOTELS (seed before receptionist)
-- =============================================

INSERT INTO hotels (id, name, address, city_province, star_rating, latitude, longitude, is_deleted, created_at, updated_at)
VALUES (
    'e0000000-0000-0000-0000-000000000001',
    'Travery Grand Hotel',
    '123 Nguyễn Huệ, Quận 1',
    'TP. Hồ Chí Minh',
    5, 10.77368900, 106.70072900,
    false, NOW(), NOW()
);

INSERT INTO hotels (id, name, address, city_province, star_rating, latitude, longitude, is_deleted, created_at, updated_at)
VALUES (
    'e0000000-0000-0000-0000-000000000002',
    'Travery Beach Resort',
    '456 Trần Phú, Nha Trang',
    'Khánh Hòa',
    4, 12.24506900, 109.19553700,
    false, NOW(), NOW()
);

-- =============================================
-- 5. RECEPTIONISTS
-- =============================================

INSERT INTO users (id, full_name, email, password_hashed, role, status, auth_provider, is_deleted, created_at, updated_at)
VALUES (
    'eb000000-0000-0000-0000-000000000001',
    'Võ Thanh Tâm',
    'receptionist01@travery.com',
    '$2a$10$dXJ3SW6G7P50lGmMQoeHhOxht6Mwy/HBt0VbGrAaxOlTzT1FeBv.C',
    'RECEPTIONIST', 'ACTIVE', 'LOCAL', false, NOW(), NOW()
);
INSERT INTO receptionists (id, employee_code, hotel_id, shift_type)
VALUES ('eb000000-0000-0000-0000-000000000001', 'REC-SEED01', 'e0000000-0000-0000-0000-000000000001', 'MORNING');

INSERT INTO users (id, full_name, email, password_hashed, role, status, auth_provider, is_deleted, created_at, updated_at)
VALUES (
    'eb000000-0000-0000-0000-000000000002',
    'Đặng Ngọc Hân',
    'receptionist02@travery.com',
    '$2a$10$dXJ3SW6G7P50lGmMQoeHhOxht6Mwy/HBt0VbGrAaxOlTzT1FeBv.C',
    'RECEPTIONIST', 'ACTIVE', 'LOCAL', false, NOW(), NOW()
);
INSERT INTO receptionists (id, employee_code, hotel_id, shift_type)
VALUES ('eb000000-0000-0000-0000-000000000002', 'REC-SEED02', 'e0000000-0000-0000-0000-000000000002', 'EVENING');

-- =============================================
-- 6. TOURISTS
-- =============================================

INSERT INTO users (id, full_name, email, password_hashed, role, status, auth_provider, is_deleted, created_at, updated_at)
VALUES (
    'f0000000-0000-0000-0000-000000000001',
    'Bùi Minh Khôi',
    'tourist01@travery.com',
    '$2a$10$dXJ3SW6G7P50lGmMQoeHhOxht6Mwy/HBt0VbGrAaxOlTzT1FeBv.C',
    'TOURIST', 'ACTIVE', 'LOCAL', false, NOW(), NOW()
);
INSERT INTO tourists (id, passport_number, date_of_birth, gender)
VALUES ('f0000000-0000-0000-0000-000000000001', 'C12345678', '1995-06-15', 'MALE');

INSERT INTO users (id, full_name, email, password_hashed, role, status, auth_provider, is_deleted, created_at, updated_at)
VALUES (
    'f0000000-0000-0000-0000-000000000002',
    'Ngô Thị Hồng',
    'tourist02@travery.com',
    '$2a$10$dXJ3SW6G7P50lGmMQoeHhOxht6Mwy/HBt0VbGrAaxOlTzT1FeBv.C',
    'TOURIST', 'ACTIVE', 'LOCAL', false, NOW(), NOW()
);
INSERT INTO tourists (id, passport_number, date_of_birth, gender)
VALUES ('f0000000-0000-0000-0000-000000000002', 'C98765432', '1998-11-20', 'FEMALE');
