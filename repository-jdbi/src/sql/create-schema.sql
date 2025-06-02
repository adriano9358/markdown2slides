CREATE SCHEMA IF NOT EXISTS m2s;


CREATE TABLE m2s.themes (
    name            VARCHAR(20) PRIMARY KEY
);

CREATE TABLE m2s.users (
    id              UUID PRIMARY key,
    name                VARCHAR(255)        NOT NULL,
    email               VARCHAR(255) UNIQUE NOT NULL		
);

CREATE TABLE m2s.project_info (
    id              UUID PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(255),
    owner_id        UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL,
    theme           VARCHAR(12) NOT NULL DEFAULT 'white',
    visibility      VARCHAR(12) NOT NULL CHECK (visibility IN ('PUBLIC', 'PRIVATE')),
    FOREIGN KEY (owner_id) REFERENCES m2s.users(id) ON DELETE CASCADE,
    FOREIGN KEY (theme) REFERENCES m2s.themes(name) ON DELETE SET NULL
);

INSERT INTO m2s.themes (name) VALUES
  ('beige'),
  ('black'),
  ('blood'),
  ('league'),
  ('moon'),
  ('night'),
  ('serif'),
  ('simple'),
  ('sky'),
  ('solarized'),
  ('white');
