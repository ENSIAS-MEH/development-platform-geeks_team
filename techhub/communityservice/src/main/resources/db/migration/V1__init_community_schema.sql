-- ═══════════════════════════════════════════════════════════════════════
--  V1 – Community Service schema
--  Database: community_db  •  Port: 8085
-- ═══════════════════════════════════════════════════════════════════════

-- Enable UUID generation (PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ── 1. community_groups ─────────────────────────────────────────────

CREATE TABLE community_groups (
    id            UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    name          VARCHAR(120) NOT NULL,
    description   TEXT,
    topic         VARCHAR(40)  NOT NULL
                      CHECK (topic IN ('WEB','MOBILE','AI_ML','DEVOPS','SECURITY','GAME_DEV','DATA','OTHER')),
    is_public     BOOLEAN      NOT NULL DEFAULT TRUE,
    owner_id      UUID         NOT NULL,
    member_count  INTEGER      NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Browse public groups by topic
CREATE INDEX idx_group_topic_public ON community_groups (topic, is_public);

-- Groups I created
CREATE INDEX idx_group_owner ON community_groups (owner_id);


-- ── 2. group_members ────────────────────────────────────────────────

CREATE TABLE group_members (
    id         UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    group_id   UUID         NOT NULL REFERENCES community_groups(id) ON DELETE CASCADE,
    user_id    UUID         NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'MEMBER'
                   CHECK (role IN ('OWNER','MODERATOR','MEMBER')),
    joined_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- One membership per user per group
CREATE UNIQUE INDEX uk_group_user ON group_members (group_id, user_id);

-- Groups I belong to
CREATE INDEX idx_member_user ON group_members (user_id);


-- ── 3. posts ────────────────────────────────────────────────────────

CREATE TABLE posts (
    id             UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    group_id       UUID          NOT NULL REFERENCES community_groups(id) ON DELETE CASCADE,
    author_id      UUID          NOT NULL,
    title          VARCHAR(200)  NOT NULL,
    content        TEXT          NOT NULL,
    type           VARCHAR(30)   NOT NULL
                       CHECK (type IN ('DISCUSSION','ANNOUNCEMENT','RESOURCE')),
    upvotes        INTEGER       NOT NULL DEFAULT 0,
    comment_count  INTEGER       NOT NULL DEFAULT 0,
    is_pinned      BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_post_group  ON posts (group_id);
CREATE INDEX idx_post_author ON posts (author_id);


-- ── 4. comments ─────────────────────────────────────────────────────

CREATE TABLE comments (
    id                 UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    post_id            UUID         NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    author_id          UUID         NOT NULL,
    content            TEXT         NOT NULL,
    parent_comment_id  UUID         REFERENCES comments(id) ON DELETE SET NULL,
    upvotes            INTEGER      NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comment_post   ON comments (post_id);
CREATE INDEX idx_comment_parent ON comments (parent_comment_id);


-- ═══════════════════════════════════════════════════════════════════════
--  Seed data (optional – useful for demo / development)
-- ═══════════════════════════════════════════════════════════════════════

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
     'DEVOPS', TRUE, '00000000-0000-0000-0000-000000000001', 1);

INSERT INTO group_members (group_id, user_id, role)
VALUES
    ('a1111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000001', 'OWNER'),
    ('a1111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000002', 'MODERATOR'),
    ('a1111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000003', 'MEMBER'),
    ('a2222222-2222-2222-2222-222222222222', '00000000-0000-0000-0000-000000000002', 'OWNER'),
    ('a2222222-2222-2222-2222-222222222222', '00000000-0000-0000-0000-000000000003', 'MEMBER'),
    ('a3333333-3333-3333-3333-333333333333', '00000000-0000-0000-0000-000000000001', 'OWNER');

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
     'DISCUSSION', 8, 0);

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
     NULL, 2);
