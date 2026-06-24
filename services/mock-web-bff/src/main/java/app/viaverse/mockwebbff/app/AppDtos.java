package app.viaverse.mockwebbff.app;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;
import java.util.Map;

/**
 * Product-shaped mock Web BFF contracts consumed by the post-login web app.
 */
public final class AppDtos {

    private AppDtos() {
    }

    public record SessionView(
        UserView currentUser,
        List<UserView> personas
    ) {
    }

    public record SwitchPersonaRequest(
        String personaId
    ) {
    }

    public record UserView(
        String id,
        String displayName,
        String firstName,
        String lastName,
        String initials,
        String activeCapability,
        String activeCapabilityLabel,
        String locationLabel,
        List<CapabilityView> capabilities
    ) {
    }

    public record CapabilityView(
        String key,
        String label,
        boolean enabled,
        String status,
        String summary
    ) {
    }

    public record ProfileView(
        String accountId,
        String displayName,
        String headline,
        String bio,
        String locationLabel,
        String activeCapability,
        List<CapabilityView> capabilities,
        ProviderProfileView individualProviderProfile,
        BusinessProfileView businessProfile,
        int trustScore,
        int completionScore
    ) {
    }

    public record ProviderProfileView(
        String providerType,
        String serviceBlurb,
        String availabilitySummary,
        List<String> serviceCategoryIds,
        boolean acceptsRemote,
        @JsonAlias("dynamicEnvironmentScope")
        String locationScope
    ) {
    }

    public record BusinessProfileView(
        String providerType,
        String tradeName,
        String legalName,
        String sector,
        String addressLine,
        String publicPhone,
        String publicEmail,
        List<String> serviceCategoryIds,
        String verificationStatus
    ) {
    }

    public record PatchProfileRequest(
        String displayName,
        String headline,
        String bio,
        String locationLabel,
        String activeCapability
    ) {
    }

    public record SettingsView(
        boolean pushNotifications,
        boolean emailDigest,
        boolean requestUpdates,
        String privacyLevel,
        String language,
        String theme
    ) {
    }

    public record PatchSettingsRequest(
        Boolean pushNotifications,
        Boolean emailDigest,
        Boolean requestUpdates,
        String privacyLevel,
        String language,
        String theme
    ) {
    }

    public record ServiceCategoryView(
        String id,
        String label,
        String description,
        String lane,
        String icon,
        String preferredProviderType
    ) {
    }

    public record FeedItemView(
        String id,
        String type,
        String typeLabel,
        String title,
        String body,
        String authorName,
        String authorType,
        String locationLabel,
        String categoryId,
        String createdAt,
        int likeCount,
        boolean liked,
        int commentCount,
        int shareCount,
        boolean saved,
        String highlight,
        String relatedRequestId,
        List<String> hashtags,
        String mediaUrl,
        String mediaType
    ) {
    }

    public record CreatePostRequest(
        String type,
        String title,
        String body,
        String categoryId,
        String locationScope,
        String eventStartsAt,
        List<String> hashtags,
        String mediaUrl,
        String mediaType
    ) {
    }

    public record UpdatePostRequest(
        String type,
        String title,
        String body,
        String categoryId,
        String locationScope,
        List<String> hashtags,
        String mediaUrl,
        String mediaType
    ) {
    }

    public record IncidentUpdateView(
        String id,
        String authorName,
        String body,
        String createdAt,
        String mediaUrl,
        String mediaType
    ) {
    }

    public record AnnouncementIncidentView(
        String id,
        String kind,
        String title,
        String summary,
        String locationLabel,
        double latitude,
        double longitude,
        String createdAt,
        List<String> relatedPostIds,
        List<IncidentUpdateView> updates
    ) {
    }

    public record HashtagSuggestionView(
        String tag,
        int usageCount,
        String sampleTitle
    ) {
    }

    public record MockPhotoView(
        String id,
        String url,
        String alt,
        String sourceLabel,
        String sourceUrl,
        List<String> tags
    ) {
    }

    public record SponsoredAdView(
        String id,
        String title,
        String body,
        String advertiser,
        String imageUrl,
        String displayUrl,
        String reason
    ) {
    }

    public record SavedSearchView(
        String id,
        String ownerId,
        String surface,
        String name,
        Map<String, String> filters,
        String createdAt
    ) {
    }

    public record CreateSavedSearchRequest(
        String surface,
        String name,
        Map<String, String> filters
    ) {
    }

    public record PostCommentView(
        String id,
        String postId,
        String authorId,
        String authorName,
        String body,
        String createdAt
    ) {
    }

    public record CreateCommentRequest(
        String body
    ) {
    }

    public record ProviderView(
        String id,
        String displayName,
        String providerType,
        String headline,
        String summary,
        List<String> categoryIds,
        String locationScope,
        double rating,
        int completedJobs,
        String responseTime,
        @JsonAlias("servesDynamicEnvironment")
        boolean servesNearby,
        List<String> tags
    ) {
    }

    public record BusinessView(
        String id,
        String tradeName,
        String providerType,
        String sector,
        String summary,
        List<String> categoryIds,
        String locationScope,
        double rating,
        int completedJobs,
        String responseTime,
        String verificationStatus
    ) {
    }

    public record ServiceRequestView(
        String id,
        String requesterId,
        String requesterName,
        String title,
        String categoryId,
        String categoryLabel,
        String description,
        String locationScope,
        String timing,
        String budgetExpectation,
        String status,
        int offerCount,
        String acceptedOfferId,
        String conversationId,
        String createdAt,
        String updatedAt
    ) {
    }

    public record CreateServiceRequestRequest(
        String title,
        String categoryId,
        String description,
        String locationScope,
        String timing,
        String budgetExpectation
    ) {
    }

    public record OpportunityView(
        ServiceRequestView request,
        String matchReason,
        int fitScore
    ) {
    }

    public record OfferView(
        String id,
        String requestId,
        String providerId,
        String providerName,
        String providerType,
        String amountExpectation,
        String message,
        String status,
        String conversationId,
        String createdAt,
        String updatedAt
    ) {
    }

    public record CreateOfferRequest(
        String requestId,
        String amountExpectation,
        String message
    ) {
    }

    public record OfferAcceptanceView(
        OfferView offer,
        ConversationView conversation
    ) {
    }

    public record ConversationView(
        String id,
        String title,
        String contextLabel,
        String participantName,
        String participantType,
        String lastMessage,
        String lastMessageAt,
        int unreadCount,
        String relatedRequestId,
        String relatedOfferId
    ) {
    }

    public record MessageView(
        String id,
        String conversationId,
        String senderId,
        String senderName,
        String body,
        boolean system,
        String createdAt,
        String updatedAt
    ) {
    }

    public record SendMessageRequest(
        String body
    ) {
    }

    public record UpdateMessageRequest(
        String body
    ) {
    }

    public record TransactionView(
        String id,
        String type,
        String title,
        long amountMinor,
        String currency,
        String status,
        String description,
        String relatedRequestId,
        String createdAt,
        String updatedAt
    ) {
    }

    public record CreatePaymentIntentRequest(
        String title,
        long amountMinor,
        String currency,
        String relatedRequestId
    ) {
    }

    public record PaymentStatusRequest(
        String status
    ) {
    }

    public record NotificationView(
        String id,
        String title,
        String body,
        String type,
        boolean read,
        String createdAt
    ) {
    }

    public record IdentityAccountView(
        String id,
        String accountId,
        String email,
        String phone,
        String password,
        String displayName,
        String status
    ) {
    }

    public record RegistrationDraftView(
        String id,
        String email,
        String displayName,
        String firstName,
        String lastName,
        String password,
        String createdAt,
        String otp
    ) {
    }

    public record RequiredConsentsView(
        List<ConsentDocumentView> required,
        ConsentDocumentView marketing
    ) {
    }

    public record CapabilityTermsView(
        List<ConsentDocumentView> capabilityTerms
    ) {
    }

    public record ConsentDocumentView(
        String type,
        String category,
        String version,
        String url
    ) {
    }

    public record PasswordLoginRequest(
        String identifier,
        String password
    ) {
    }

    public record AuthSessionView(
        String nextStep,
        String accessToken,
        String accessTokenExpiresAt,
        String refreshToken,
        String refreshTokenExpiresAt,
        UserView account
    ) {
    }

    public record RegisterStartRequest(
        String email,
        String displayName,
        String firstName,
        String lastName,
        String password,
        List<String> acceptedRequiredConsents,
        boolean marketingConsentAccepted
    ) {
    }

    public record RegisterStartView(
        String draftId,
        String emailFlowId,
        String emailExpiresAt,
        boolean phoneVerificationPending
    ) {
    }

    public record RegisterVerifyEmailRequest(
        String draftId,
        String otp
    ) {
    }

    public record ForgotPasswordStartRequest(
        String identifier
    ) {
    }

    public record ForgotPasswordStartView(
        String flowId,
        String identifierType,
        String expiresAt
    ) {
    }

    public record ForgotPasswordVerifyRequest(
        String flowId,
        String otp
    ) {
    }

    public record ForgotPasswordTokenView(
        String resetToken,
        String expiresAt
    ) {
    }

    public record ForgotPasswordCompleteRequest(
        String resetToken,
        String newPassword
    ) {
    }
}
