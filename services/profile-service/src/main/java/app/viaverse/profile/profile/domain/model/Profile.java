package app.viaverse.profile.profile.domain.model;

import app.viaverse.profile.profile.domain.enums.ActiveModeEnum;
import app.viaverse.profile.profile.domain.enums.PublicVisibilityEnum;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root for user-facing profile identity.
 */
public final class Profile {

    private final UUID accountId;
    private final String displayName;
    private final String firstName;
    private final String lastName;
    private final UUID avatarMediaId;
    private final String headline;
    private final String bio;
    private final String locale;
    private final String timezone;
    private final ActiveModeEnum activeMode;
    private final int completenessScore;
    private final PublicVisibilityEnum publicVisibility;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final long version;

    public Profile(
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
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.displayName = requireText(displayName, "displayName", 120);
        this.firstName = optionalText(firstName, "firstName", 80);
        this.lastName = optionalText(lastName, "lastName", 80);
        this.avatarMediaId = avatarMediaId;
        this.headline = optionalText(headline, "headline", 80);
        this.bio = optionalText(bio, "bio", 600);
        this.locale = requireText(locale, "locale", 32);
        this.timezone = requireText(timezone, "timezone", 64);
        this.activeMode = Objects.requireNonNull(activeMode, "activeMode");
        this.completenessScore = requireCompletenessScore(completenessScore);
        this.publicVisibility = Objects.requireNonNull(publicVisibility, "publicVisibility");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.version = version;
    }

    /**
     * New accounts start in customer mode and with privacy-preserving limited visibility.
     */
    public static Profile provision(
            UUID accountId,
            String displayName,
            String firstName,
            String lastName,
            String locale,
            String timezone,
            Instant now
    ) {
        return new Profile(
                accountId,
                displayName,
                firstName,
                lastName,
                null,
                null,
                null,
                locale,
                timezone,
                ActiveModeEnum.CUSTOMER,
                0,
                PublicVisibilityEnum.LIMITED,
                now,
                now,
                0
        );
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }

    public Profile withCompletenessScore(int nextCompletenessScore) {
        return new Profile(
                accountId,
                displayName,
                firstName,
                lastName,
                avatarMediaId,
                headline,
                bio,
                locale,
                timezone,
                activeMode,
                nextCompletenessScore,
                publicVisibility,
                createdAt,
                updatedAt,
                version
        );
    }

    public Profile updateSelfView(
            String nextDisplayName,
            String nextFirstName,
            String nextLastName,
            UUID nextAvatarMediaId,
            String nextHeadline,
            String nextBio,
            String nextLocale,
            String nextTimezone,
            PublicVisibilityEnum nextPublicVisibility,
            Instant now
    ) {
        return new Profile(
                accountId,
                nextDisplayName,
                nextFirstName,
                nextLastName,
                nextAvatarMediaId,
                nextHeadline,
                nextBio,
                nextLocale,
                nextTimezone,
                activeMode,
                completenessScore,
                nextPublicVisibility,
                createdAt,
                Objects.requireNonNull(now, "now"),
                version
        );
    }

    public Profile switchActiveMode(ActiveModeEnum nextActiveMode, Instant now) {
        return new Profile(
                accountId,
                displayName,
                firstName,
                lastName,
                avatarMediaId,
                headline,
                bio,
                locale,
                timezone,
                Objects.requireNonNull(nextActiveMode, "nextActiveMode"),
                completenessScore,
                publicVisibility,
                createdAt,
                Objects.requireNonNull(now, "now"),
                version
        );
    }

    private static int requireCompletenessScore(int completenessScore) {
        if (completenessScore < 0 || completenessScore > 100) {
            throw new IllegalArgumentException("completenessScore must be between 0 and 100");
        }
        return completenessScore;
    }

    private static String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must not exceed " + maxLength + " characters");
        }
        return value;
    }

    private static String optionalText(String value, String field, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must not exceed " + maxLength + " characters");
        }
        return value;
    }
}
