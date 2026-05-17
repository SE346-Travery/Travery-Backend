-- =============================================
-- V4: Add review unique constraint + booking customer_note
-- =============================================

-- 1. Ensure 1 booking can only have 1 review (prevent spam)
ALTER TABLE reviews
    ADD CONSTRAINT uq_review_booking UNIQUE (booking_id);

-- 2. Add customer_note column to tour_bookings
ALTER TABLE tour_bookings
    ADD COLUMN customer_note TEXT;
