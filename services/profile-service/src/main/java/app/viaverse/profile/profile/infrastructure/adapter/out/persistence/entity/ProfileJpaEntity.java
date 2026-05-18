package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity;

import app.viaverse.profile.profile.domain.enums.ActiveModeEnum;
import app.viaverse.profile.profile.domain.enums.PublicVisibilityEnum;
import app.viaverse.web.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profile")
public class ProfileJpaEntity extends BaseJpaEntity {

    @Id
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(name = "first_name", length = 80)
    private String firstName;

    @Column(name = "last_name", length = 80)
    private String lastName;

    @Column(name = "avatar_media_id")
    private UUID avatarMediaId;

    @Column(name = "headline", length = 80)
    private String headline;

    @Column(name = "bio", length = 600)
    private String bio;

    @Column(name = "locale", nullable = false, length = 32)
    private String locale;

    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(name = "active_mode", nullable = false, length = 32)
    private ActiveModeEnum activeMode;

    @Column(name = "completeness_score", nullable = false)
    private int completenessScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "public_visibility", nullable = false, length = 32)
    private PublicVisibilityEnum publicVisibility;

    protected ProfileJpaEntity() {
    }

    public ProfileJpaEntity(
            UUID accountId,
            String displayName,
            String firstName,
            String lastName,
            UUID avatarMediaId,
            String headline,
            String bio,
            String locale,
            String timezone,
            ActiveModeEnum activeMode,
            int completenessScore,
            PublicVisibilityEnum publicVisibility,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        super(createdAt, updatedAt, version);
        this.accountId = accountId;
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatarMediaId = avatarMediaId;
        this.headline = headline;
        this.bio = bio;
        this.locale = locale;
        this.timezone = timezone;
        this.activeMode = activeMode;
        this.completenessScore = completenessScore;
        this.publicVisibility = publicVisibility;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UUID getAvatarMediaId() {
        return avatarMediaId;
    }

    public String getHeadline() {
        return headline;
    }

    public String getBio() {
        return bio;
    }

    public String getLocale() {
        return locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public ActiveModeEnum getActiveMode() {
        return activeMode;
    }

    public int getCompletenessScore() {
        return completenessScore;
    }

    public PublicVisibilityEnum getPublicVisibility() {
        return publicVisibility;
    }
}
