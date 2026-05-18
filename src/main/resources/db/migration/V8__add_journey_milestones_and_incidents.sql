-- =============================================
-- Travery Backend - Add Tour Incidents (V8)
-- =============================================

-- Create tour_incidents table
CREATE TABLE tour_incidents (
    id UUID PRIMARY KEY,
    tour_instance_id UUID NOT NULL,
    reporter_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    severity VARCHAR(20) NOT NULL CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PROCESSING', 'RESOLVED', 'CLOSED')),
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_tour_incidents_instance FOREIGN KEY (tour_instance_id) REFERENCES tour_instances(id),
    CONSTRAINT fk_tour_incidents_reporter FOREIGN KEY (reporter_id) REFERENCES users(id)
);
