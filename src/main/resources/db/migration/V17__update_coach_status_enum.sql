-- Update coaches status check constraint to include INACTIVE
ALTER TABLE coaches DROP CONSTRAINT IF EXISTS coaches_status_check;
ALTER TABLE coaches ADD CONSTRAINT coaches_status_check CHECK (status IN ('ACTIVE', 'MAINTENANCE', 'INACTIVE'));

-- Update booking status check constraints to include NOT_CHECKED
ALTER TABLE coach_bookings DROP CONSTRAINT IF EXISTS coach_bookings_status_check;
ALTER TABLE coach_bookings ADD CONSTRAINT coach_bookings_status_check CHECK (status IN ('PENDING', 'PAID', 'NOT_CHECKED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED', 'NO_SHOW'));

ALTER TABLE tour_bookings DROP CONSTRAINT IF EXISTS tour_bookings_status_check;
ALTER TABLE tour_bookings ADD CONSTRAINT tour_bookings_status_check CHECK (status IN ('PENDING', 'PAID', 'NOT_CHECKED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED', 'NO_SHOW'));

ALTER TABLE hotel_bookings DROP CONSTRAINT IF EXISTS hotel_bookings_status_check;
ALTER TABLE hotel_bookings ADD CONSTRAINT hotel_bookings_status_check CHECK (status IN ('PENDING', 'PAID', 'NOT_CHECKED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED', 'NO_SHOW'));
