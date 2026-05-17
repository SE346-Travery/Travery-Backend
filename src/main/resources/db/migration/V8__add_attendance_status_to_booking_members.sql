-- =============================================
-- Travery Backend - Add Attendance Status to Booking Members (V4)
-- =============================================

ALTER TABLE booking_members 
ADD COLUMN attendance_status varchar(50) NOT NULL DEFAULT 'NOT_CHECKED' 
CHECK (attendance_status IN ('NOT_CHECKED', 'PRESENT', 'NO_SHOW'));
