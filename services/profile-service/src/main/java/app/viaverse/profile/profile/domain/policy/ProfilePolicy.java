package app.viaverse.profile.profile.domain.policy;

import app.viaverse.profile.profile.domain.model.Profile;

/**
 * Pure profile-domain rules that do not belong to transport or persistence layers.
 */
public final class ProfilePolicy {

    private ProfilePolicy() {
    }

    /**
     * Computes the Phase 1 base-profile completeness score.
     * Capability-specific scoring can be layered in once capability aggregates land.
     */
    public static int computeCompleteness(Profile profile, boolean hasVerifiedEmail, boolean hasVerifiedPhone) {
        int score = 0;
        score += hasText(profile.getDisplayName()) ? 20 : 0;
        score += hasText(profile.getFirstName()) ? 10 : 0;
        score += hasText(profile.getLastName()) ? 10 : 0;
        score += profile.getAvatarMediaId() != null ? 15 : 0;
        score += hasText(profile.getHeadline()) ? 10 : 0;
        score += hasText(profile.getBio()) ? 15 : 0;
        score += hasText(profile.getLocale()) ? 5 : 0;
        score += hasText(profile.getTimezone()) ? 5 : 0;
        score += hasVerifiedEmail ? 5 : 0;
        score += hasVerifiedPhone ? 5 : 0;
        return score;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
