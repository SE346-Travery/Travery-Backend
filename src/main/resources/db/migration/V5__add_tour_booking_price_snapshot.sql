-- Add price snapshot columns to tour_bookings table
ALTER TABLE tour_bookings 
ADD COLUMN price_per_adult_at_booking NUMERIC(12, 2),
ADD COLUMN price_per_child_at_booking NUMERIC(12, 2);

-- Set default values for existing bookings (optional, but good for consistency)
UPDATE tour_bookings tb
SET price_per_adult_at_booking = t.price_per_adult,
    price_per_child_at_booking = t.price_per_child
FROM tour_instances ti
JOIN tours t ON ti.tour_id = t.id
WHERE tb.tour_instance_id = ti.id;

-- Make them NOT NULL after seeding (all existing rows were backfilled above)
ALTER TABLE tour_bookings
ALTER COLUMN price_per_adult_at_booking SET NOT NULL,
ALTER COLUMN price_per_child_at_booking SET NOT NULL;
