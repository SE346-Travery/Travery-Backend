-- =============================================
-- Travery Backend - Seed Refund Policies (V3)
-- =============================================

-- 1. TOUR Standard Policy
INSERT INTO refund_policies (id, name, service_type, is_deleted, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Standard Tour Policy',
    'TOUR',
    FALSE,
    NOW(), NOW()
);

INSERT INTO refund_policy_rules (id, refund_policy_id, time_before, time_unit, refund_percentage, created_at, updated_at)
VALUES 
    ('11000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 7, 'DAYS', 100.00, NOW(), NOW()),
    ('11000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 3, 'DAYS', 50.00, NOW(), NOW()),
    ('11000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 0, 'DAYS', 0.00, NOW(), NOW());

-- 2. HOTEL Standard Policy
INSERT INTO refund_policies (id, name, service_type, is_deleted, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000002',
    'Standard Hotel Policy',
    'HOTEL',
    FALSE,
    NOW(), NOW()
);

INSERT INTO refund_policy_rules (id, refund_policy_id, time_before, time_unit, refund_percentage, created_at, updated_at)
VALUES 
    ('11000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000002', 3, 'DAYS', 100.00, NOW(), NOW()),
    ('11000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000002', 1, 'DAYS', 50.00, NOW(), NOW()),
    ('11000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000002', 0, 'DAYS', 0.00, NOW(), NOW());

-- 3. COACH Standard Policy
INSERT INTO refund_policies (id, name, service_type, is_deleted, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000003',
    'Standard Coach Policy',
    'COACH',
    FALSE,
    NOW(), NOW()
);

INSERT INTO refund_policy_rules (id, refund_policy_id, time_before, time_unit, refund_percentage, created_at, updated_at)
VALUES 
    ('11000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000003', 24, 'HOURS', 100.00, NOW(), NOW()),
    ('11000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000003', 12, 'HOURS', 50.00, NOW(), NOW()),
    ('11000000-0000-0000-0000-000000000009', '00000000-0000-0000-0000-000000000003', 0, 'HOURS', 0.00, NOW(), NOW());
