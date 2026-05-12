-- =============================================
-- Travery Backend - Seed Refund Policies (V3)
-- Seed standard refund policies based on Wiki
-- =============================================

-- 1. TOUR Standard Policy
INSERT INTO refund_policies (id, name, service_type, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Standard Tour Policy',
    'TOUR',
    NOW(), NOW()
);

INSERT INTO refund_policy_rules (id, refund_policy_id, days_before, refund_percentage, created_at, updated_at)
VALUES 
    ('11000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 7, 100.00, NOW(), NOW()),
    ('11000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 3, 50.00, NOW(), NOW()),
    ('11000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 0, 0.00, NOW(), NOW());

-- 2. HOTEL Standard Policy
INSERT INTO refund_policies (id, name, service_type, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000002',
    'Standard Hotel Policy',
    'HOTEL',
    NOW(), NOW()
);

INSERT INTO refund_policy_rules (id, refund_policy_id, days_before, refund_percentage, created_at, updated_at)
VALUES 
    ('11000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000002', 3, 100.00, NOW(), NOW()),
    ('11000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000002', 1, 50.00, NOW(), NOW()),
    ('11000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000002', 0, 0.00, NOW(), NOW());

-- 3. COACH Standard Policy
INSERT INTO refund_policies (id, name, service_type, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000003',
    'Standard Coach Policy',
    'COACH',
    NOW(), NOW()
);

INSERT INTO refund_policy_rules (id, refund_policy_id, days_before, refund_percentage, created_at, updated_at)
VALUES 
    ('11000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000003', 0, 100.00, NOW(), NOW());
