-- ═══════════════════════════════════════════════════════════════════════
--  schema.sql – Community Service schema (for JPA ddl-auto=none fallback)
--  This is maintained alongside Flyway migrations for documentation
-- ═══════════════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS community_groups (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(120) NOT NULL,
    description   TEXT,
    topic         VARCHAR(40)  NOT NULL,
    is_public     BOOLEAN      NOT NULL DEFAULT TRUE,
    owner_id      UUID         NOT NULL,
    member_count  INTEGER      NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS group_members (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id   UUID         NOT NULL REFERENCES community_groups(id) ON DELETE CASCADE,
    user_id    UUID         NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'MEMBER',
    joined_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_group_user UNIQUE (group_id, user_id)
);

CREATE TABLE IF NOT EXISTS posts (
    id             UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id       UUID          NOT NULL REFERENCES community_groups(id) ON DELETE CASCADE,
    author_id      UUID          NOT NULL,
    title          VARCHAR(200)  NOT NULL,
    content        TEXT          NOT NULL,
    type           VARCHAR(30)   NOT NULL,
    upvotes        INTEGER       NOT NULL DEFAULT 0,
    comment_count  INTEGER       NOT NULL DEFAULT 0,
    is_pinned      BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS comments (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id            UUID         NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    author_id          UUID         NOT NULL,
    content            TEXT         NOT NULL,
    parent_comment_id  UUID         REFERENCES comments(id) ON DELETE SET NULL,
    upvotes            INTEGER      NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_group_topic_public ON community_groups (topic, is_public);
CREATE INDEX IF NOT EXISTS idx_group_owner ON community_groups (owner_id);
CREATE INDEX IF NOT EXISTS idx_member_user ON group_members (user_id);
CREATE INDEX IF NOT EXISTS idx_post_group ON posts (group_id);
CREATE INDEX IF NOT EXISTS idx_post_author ON posts (author_id);
CREATE INDEX IF NOT EXISTS idx_comment_post ON comments (post_id);
CREATE INDEX IF NOT EXISTS idx_comment_parent ON comments (parent_comment_id);
