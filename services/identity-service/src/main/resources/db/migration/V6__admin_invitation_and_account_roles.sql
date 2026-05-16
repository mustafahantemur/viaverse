CREATE TABLE identity_account_role (
    account_id UUID NOT NULL REFERENCES identity_account (id),
    role VARCHAR(32) NOT NULL,
    PRIMARY KEY (account_id, role)
);

INSERT INTO identity_account_role (account_id, role)
SELECT id, 'USER'
FROM identity_account;

CREATE TABLE admin_invitation (
    id UUID PRIMARY KEY,
    token_hash VARCHAR(128) NOT NULL,
    issued_by_account_id UUID NOT NULL REFERENCES identity_account (id),
    expires_at TIMESTAMPTZ NOT NULL,
    consumed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_admin_invitation_token_hash UNIQUE (token_hash)
);

CREATE INDEX idx_admin_invitation_expires_at ON admin_invitation (expires_at);
