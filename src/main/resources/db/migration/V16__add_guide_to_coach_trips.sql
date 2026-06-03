ALTER TABLE coach_trips
ADD COLUMN guide_id uuid;

ALTER TABLE coach_trips
ADD CONSTRAINT fk_coach_trips_guide
FOREIGN KEY (guide_id)
REFERENCES guides(id);
