INSERT INTO projects (id, title, description, type, status, github_url, owner_id) VALUES
('11111111-1111-1111-1111-111111111111', 'DevConnect Mobile App', 'Cross-platform mobile app for developer networking', 'STARTUP_IDEA', 'OPEN', 'https://github.com/devconnect/mobile', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('22222222-2222-2222-2222-222222222222', 'Open Source CLI Tool', 'Developer productivity CLI utilities', 'OPEN_SOURCE', 'OPEN', 'https://github.com/devconnect/cli', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('33333333-3333-3333-3333-333333333333', 'AI Hackathon Project', 'ML-powered code review assistant', 'HACKATHON_PROJECT', 'OPEN', NULL, 'f47ac10b-58cc-4372-a567-0e02b2c3d479');

INSERT INTO project_technologies (project_id, technology) VALUES
('11111111-1111-1111-1111-111111111111', 'React Native'),
('11111111-1111-1111-1111-111111111111', 'TypeScript'),
('22222222-2222-2222-2222-222222222222', 'Go'),
('33333333-3333-3333-3333-333333333333', 'Python');

INSERT INTO project_skills_needed (project_id, skill) VALUES
('11111111-1111-1111-1111-111111111111', 'react'),
('11111111-1111-1111-1111-111111111111', 'typescript'),
('22222222-2222-2222-2222-222222222222', 'go'),
('33333333-3333-3333-3333-333333333333', 'python'),
('33333333-3333-3333-3333-333333333333', 'machine learning');

INSERT INTO project_members (project_id, user_id, role) VALUES
('11111111-1111-1111-1111-111111111111', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'OWNER'),
('22222222-2222-2222-2222-222222222222', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'OWNER'),
('33333333-3333-3333-3333-333333333333', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'OWNER');
