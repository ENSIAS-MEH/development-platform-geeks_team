-- ═══════════════════════════════════════════════════════════════════════
--  data.sql – Seed data for development/demo
--  Only runs when spring.sql.init.mode=always
-- ═══════════════════════════════════════════════════════════════════════

-- Sample groups
INSERT INTO community_groups (id, name, description, topic, is_public, owner_id, member_count)
VALUES
    ('a1111111-1111-1111-1111-111111111111', 'Web Dev Hub',
     'Everything about modern web development – React, Angular, Vue, and more.',
     'WEB', TRUE, '00000000-0000-0000-0000-000000000001', 3),

    ('a2222222-2222-2222-2222-222222222222', 'AI & ML Community',
     'Discuss latest papers, share projects, and collaborate on AI/ML topics.',
     'AI_ML', TRUE, '00000000-0000-0000-0000-000000000002', 2),

    ('a3333333-3333-3333-3333-333333333333', 'DevOps Engineers',
     'CI/CD, Kubernetes, Docker, monitoring, and infrastructure as code.',
     'DEVOPS', TRUE, '00000000-0000-0000-0000-000000000001', 1)
ON CONFLICT (id) DO NOTHING;

-- Sample members
INSERT INTO group_members (group_id, user_id, role)
VALUES
    ('a1111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000001', 'OWNER'),
    ('a1111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000002', 'MODERATOR'),
    ('a1111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000003', 'MEMBER'),
    ('a2222222-2222-2222-2222-222222222222', '00000000-0000-0000-0000-000000000002', 'OWNER'),
    ('a2222222-2222-2222-2222-222222222222', '00000000-0000-0000-0000-000000000003', 'MEMBER'),
    ('a3333333-3333-3333-3333-333333333333', '00000000-0000-0000-0000-000000000001', 'OWNER')
ON CONFLICT DO NOTHING;

-- Sample posts
INSERT INTO posts (id, group_id, author_id, title, content, type, upvotes, comment_count)
VALUES
    ('b1111111-1111-1111-1111-111111111111',
     'a1111111-1111-1111-1111-111111111111',
     '00000000-0000-0000-0000-000000000001',
     'Welcome to Web Dev Hub!',
     'This is the official welcome post. Introduce yourselves and share what you are working on!',
     'ANNOUNCEMENT', 5, 2),

    ('b2222222-2222-2222-2222-222222222222',
     'a1111111-1111-1111-1111-111111111111',
     '00000000-0000-0000-0000-000000000002',
     'Best resources for learning React in 2025',
     'Here are my top picks for learning React: official docs, Egghead.io, and Frontend Masters.',
     'RESOURCE', 12, 1),

    ('b3333333-3333-3333-3333-333333333333',
     'a2222222-2222-2222-2222-222222222222',
     '00000000-0000-0000-0000-000000000002',
     'Transformer architectures – discussion thread',
     'Let us discuss the latest advances in transformer architectures and their applications.',
     'DISCUSSION', 8, 0)
ON CONFLICT (id) DO NOTHING;

-- Sample comments
INSERT INTO comments (id, post_id, author_id, content, parent_comment_id, upvotes)
VALUES
    ('c1111111-1111-1111-1111-111111111111',
     'b1111111-1111-1111-1111-111111111111',
     '00000000-0000-0000-0000-000000000002',
     'Welcome everyone! Excited to be here.',
     NULL, 3),

    ('c2222222-2222-2222-2222-222222222222',
     'b1111111-1111-1111-1111-111111111111',
     '00000000-0000-0000-0000-000000000003',
     'Thanks for setting this up!',
     'c1111111-1111-1111-1111-111111111111', 1),

    ('c3333333-3333-3333-3333-333333333333',
     'b2222222-2222-2222-2222-222222222222',
     '00000000-0000-0000-0000-000000000003',
     'Frontend Masters has an amazing React course, I can confirm.',
     NULL, 2)
ON CONFLICT (id) DO NOTHING;
