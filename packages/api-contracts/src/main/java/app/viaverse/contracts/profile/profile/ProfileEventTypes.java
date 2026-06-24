package app.viaverse.contracts.profile.profile;

/**
 * Stable event-type header values published on the profile events topic.
 */
public final class ProfileEventTypes {

    public static final String PROFILE_CREATED_V1 = "profile.ProfileCreated.v1";
    public static final String PROFILE_UPDATED_V1 = "profile.ProfileUpdated.v1";
    public static final String PROFILE_CAPABILITY_ENABLED_V1 = "profile.ProfileCapabilityEnabled.v1";
    public static final String PROFILE_CAPABILITY_DISABLED_V1 = "profile.ProfileCapabilityDisabled.v1";
    public static final String PROFILE_BUSINESS_SUBMITTED_V1 = "profile.ProfileBusinessSubmitted.v1";
    public static final String PROFILE_BUSINESS_APPROVED_V1 = "profile.ProfileBusinessApproved.v1";
    public static final String PROFILE_BUSINESS_REJECTED_V1 = "profile.ProfileBusinessRejected.v1";
    public static final String PROFILE_BLOCKED_V1 = "profile.ProfileBlocked.v1";
    public static final String PROFILE_UNBLOCKED_V1 = "profile.ProfileUnblocked.v1";

    private ProfileEventTypes() {
    }
}
