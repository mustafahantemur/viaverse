package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity;

import app.viaverse.web.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@IdClass(ProfilePreferenceJpaId.class)
@Table(name = "profile_preference")
public class ProfilePreferenceJpaEntity extends BaseJpaEntity {

    @Id
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Id
    @Column(name = "preference_key", nullable = false, length = 160)
    private String key;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "value_json", nullable = false, columnDefinition = "jsonb")
    private String valueJson;

    protected ProfilePreferenceJpaEntity() {
    }

    public ProfilePreferenceJpaEntity(
            UUID accountId,
            String key,
            String valueJson,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        super(createdAt, updatedAt, version);
        this.accountId = accountId;
        this.key = key;
        this.valueJson = valueJson;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getKey() {
        return key;
    }

    public String getValueJson() {
        return valueJson;
    }
}
