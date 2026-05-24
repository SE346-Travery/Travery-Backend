ALTER TABLE chat_sessions ADD COLUMN tour_instance_id UUID;
ALTER TABLE chat_sessions ADD CONSTRAINT fk_chat_sessions_tour_instance FOREIGN KEY (tour_instance_id) REFERENCES tour_instances(id);
