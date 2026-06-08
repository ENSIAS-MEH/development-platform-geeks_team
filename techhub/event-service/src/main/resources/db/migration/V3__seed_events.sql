INSERT INTO events (id, title, description, type, start_date, end_date, location, max_participants, organizer_id, status) VALUES
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'HackAI Morocco 2025', 'Build AI-powered solutions in 48h', 'HACKATHON', NOW() + INTERVAL '10 days', NOW() + INTERVAL '12 days', 'Casablanca, Morocco', 200, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'PUBLISHED'),
('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'DevOps Summit Rabat', 'Annual DevOps conference', 'CONFERENCE', NOW() + INTERVAL '20 days', NOW() + INTERVAL '21 days', 'Rabat, Morocco', 500, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'PUBLISHED'),
('c3d4e5f6-a7b8-9012-cdef-123456789012', 'React Workshop Beginner', 'Learn React from scratch', 'WORKSHOP', NOW() + INTERVAL '5 days', NOW() + INTERVAL '6 days', 'Online', 30, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'PUBLISHED'),
('d4e5f6a7-b8c9-0123-defa-234567890123', 'Spring Boot Bootcamp', 'Intensive Spring Boot training', 'WORKSHOP', NOW() + INTERVAL '15 days', NOW() + INTERVAL '17 days', 'Marrakech, Morocco', 50, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'DRAFT'),
('e5f6a7b8-c9d0-1234-efab-345678901234', 'Startup Pitch Night', 'Developer startup competition', 'COMPETITION', NOW() + INTERVAL '30 days', NOW() + INTERVAL '31 days', 'Casablanca, Morocco', 100, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'PUBLISHED')
ON CONFLICT (id) DO NOTHING;
