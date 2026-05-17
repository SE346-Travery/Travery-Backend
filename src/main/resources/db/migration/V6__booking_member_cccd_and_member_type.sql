-- Rename passport_number → identity_number
ALTER TABLE booking_members RENAME COLUMN passport_number TO identity_number;

-- Add member_type column (server auto-calculates from date_of_birth)
ALTER TABLE booking_members
ADD COLUMN member_type VARCHAR(20);

-- Backfill existing rows: age <= 11 = CHILD, >= 12 = ADULT
UPDATE booking_members
SET member_type = CASE
    WHEN date_of_birth IS NOT NULL
         AND EXTRACT(YEAR FROM AGE(CURRENT_DATE, date_of_birth)) <= 11
    THEN 'CHILD'
    ELSE 'ADULT'
END;

-- Now make it NOT NULL
ALTER TABLE booking_members ALTER COLUMN member_type SET NOT NULL;
