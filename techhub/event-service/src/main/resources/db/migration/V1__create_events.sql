CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    location VARCHAR(300),
    max_participants INTEGER,
    organizer_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT chk_dates CHECK (end_date > start_date)
);

CREATE TABLE event_tags (
    event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    tag VARCHAR(50) NOT NULL
);

CREATE INDEX idx_events_status_start ON events(status, start_date);
CREATE INDEX idx_events_organizer ON events(organizer_id);
CREATE INDEX idx_events_type ON events(type);
