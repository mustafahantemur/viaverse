package app.viaverse.identity.account.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Single-use 2FA recovery code. Stored hashed (same HMAC as refresh tokens)
 * so a database leak does not reveal usable codes. {@code usedAt} stamps
 * consumption; rows are not deleted so we can surface "X of N used" in
 * profile settings and audit who burned which code.
 */
public final class BackupCode {

    private final UUID id;
    private final UUID accountId;
    private final String codeHash;
    private final Instant createdAt;
    private Instant usedAt;

    public BackupCode(UUID id, UUID accountId, String codeHash, Instant usedAt, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.codeHash = Objects.requireNonNull(codeHash, "codeHash");
        this.usedAt = usedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static BackupCode issue(UUID id, UUID accountId, String codeHash, Instant now) {
        return new BackupCode(id, accountId, codeHash, null, now);
    }

    public UUID getId() { return id; }
    public UUID getAccountId() { return accountId; }
    public String getCodeHash() { return codeHash; }
    public Instant getUsedAt() { return usedAt; }
    public Instant getCreatedAt() { return createdAt; }

    public boolean isUsed() {
        return usedAt != null;
    }

    public void markUsed(Instant now) {
        if (usedAt != null) {
            throw new IllegalStateException("Backup code already used at " + usedAt);
        }
        this.usedAt = Objects.requireNonNull(now, "now");
    }
}
