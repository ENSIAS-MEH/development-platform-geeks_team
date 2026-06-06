-- ════════════════════════════════════════════════════════════════
--  TechHub — Team Service  |  schema.sql
--  Owner: Member 3  |  DB: team_db  |  Port: 5432
--
--  Rules:
--   • gen_random_uuid() for PK generation (no Hibernate sequence overhead)
--   • Enum types declared as PostgreSQL native ENUM
--   • All idempotent (safe to re-run in dev with create-drop = always)
--   • Hibernate ddl-auto = validate in prod — this script owns the DDL
-- ════════════════════════════════════════════════════════════════

-- ── Enum types ──────────────────────────────────────────────────
DO $$ BEGIN
    CREATE TYPE team_status AS ENUM ('OPEN', 'FULL', 'CLOSED');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE member_role AS ENUM ('OWNER', 'MEMBER');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE invitation_status AS ENUM ('PENDING', 'ACCEPTED', 'DECLINED', 'EXPIRED');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

-- ── teams ────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS teams (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(120)    NOT NULL,
    description     TEXT,
    max_members     INTEGER         NOT NULL    DEFAULT 10
                                    CHECK (max_members BETWEEN 2 AND 100),
    current_members INTEGER         NOT NULL    DEFAULT 1
                                    CHECK (current_members >= 0),
    status          VARCHAR(10)     NOT NULL    DEFAULT 'OPEN',
    owner_id        UUID            NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL    DEFAULT now(),
    updated_at      TIMESTAMPTZ     NOT NULL    DEFAULT now(),

    CONSTRAINT chk_team_status
        CHECK (status IN ('OPEN','FULL','CLOSED')),
    CONSTRAINT chk_members_lte_max
        CHECK (current_members <= max_members)
);

CREATE INDEX IF NOT EXISTS idx_teams_owner_id ON teams (owner_id);
CREATE INDEX IF NOT EXISTS idx_teams_status   ON teams (status);

-- ── team_members ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS team_members (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    team_id     UUID        NOT NULL
                            REFERENCES teams (id) ON DELETE CASCADE,
    user_id     UUID        NOT NULL,
    role        VARCHAR(10) NOT NULL    DEFAULT 'MEMBER',
    joined_at   TIMESTAMPTZ NOT NULL    DEFAULT now(),

    CONSTRAINT chk_member_role
        CHECK (role IN ('OWNER','MEMBER')),
    CONSTRAINT uq_team_user
        UNIQUE (team_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_team_members_team_id ON team_members (team_id);
CREATE INDEX IF NOT EXISTS idx_team_members_user_id ON team_members (user_id);

-- ── team_invitations ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS team_invitations (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    team_id         UUID        NOT NULL
                                REFERENCES teams (id) ON DELETE CASCADE,
    sender_id       UUID        NOT NULL,
    receiver_id     UUID        NOT NULL,
    status          VARCHAR(10) NOT NULL    DEFAULT 'PENDING',
    expiration_time TIMESTAMPTZ NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL    DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL    DEFAULT now(),

    CONSTRAINT chk_invitation_status
        CHECK (status IN ('PENDING','ACCEPTED','DECLINED','EXPIRED'))
);

CREATE INDEX IF NOT EXISTS idx_invitations_receiver_id   ON team_invitations (receiver_id);
CREATE INDEX IF NOT EXISTS idx_invitations_team_id       ON team_invitations (team_id);
CREATE INDEX IF NOT EXISTS idx_invitations_status        ON team_invitations (status);
-- Partial index: scheduler only scans PENDING rows — huge perf gain
CREATE INDEX IF NOT EXISTS idx_invitations_pending_expiry
    ON team_invitations (expiration_time)
    WHERE status = 'PENDING';

-- ── updated_at auto-trigger ──────────────────────────────────────
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$;

DO $$ BEGIN
    CREATE TRIGGER trg_teams_updated_at
        BEFORE UPDATE ON teams
        FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TRIGGER trg_invitations_updated_at
        BEFORE UPDATE ON team_invitations
        FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN null;
END $$;