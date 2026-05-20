package app.viaverse.mockwebbff.app;

import app.viaverse.mockwebbff.app.AppDtos.BusinessView;
import app.viaverse.mockwebbff.app.AppDtos.AnnouncementIncidentView;
import app.viaverse.mockwebbff.app.AppDtos.AuthSessionView;
import app.viaverse.mockwebbff.app.AppDtos.CapabilityTermsView;
import app.viaverse.mockwebbff.app.AppDtos.ConsentDocumentView;
import app.viaverse.mockwebbff.app.AppDtos.ConversationView;
import app.viaverse.mockwebbff.app.AppDtos.CreateCommentRequest;
import app.viaverse.mockwebbff.app.AppDtos.CreateOfferRequest;
import app.viaverse.mockwebbff.app.AppDtos.CreatePaymentIntentRequest;
import app.viaverse.mockwebbff.app.AppDtos.CreatePostRequest;
import app.viaverse.mockwebbff.app.AppDtos.CreateSavedSearchRequest;
import app.viaverse.mockwebbff.app.AppDtos.CreateServiceRequestRequest;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordCompleteRequest;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordStartRequest;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordStartView;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordTokenView;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordVerifyRequest;
import app.viaverse.mockwebbff.app.AppDtos.FeedItemView;
import app.viaverse.mockwebbff.app.AppDtos.HashtagSuggestionView;
import app.viaverse.mockwebbff.app.AppDtos.IdentityAccountView;
import app.viaverse.mockwebbff.app.AppDtos.MessageView;
import app.viaverse.mockwebbff.app.AppDtos.MockPhotoView;
import app.viaverse.mockwebbff.app.AppDtos.NotificationView;
import app.viaverse.mockwebbff.app.AppDtos.OfferAcceptanceView;
import app.viaverse.mockwebbff.app.AppDtos.OfferView;
import app.viaverse.mockwebbff.app.AppDtos.OpportunityView;
import app.viaverse.mockwebbff.app.AppDtos.PatchProfileRequest;
import app.viaverse.mockwebbff.app.AppDtos.PatchSettingsRequest;
import app.viaverse.mockwebbff.app.AppDtos.PaymentStatusRequest;
import app.viaverse.mockwebbff.app.AppDtos.PostCommentView;
import app.viaverse.mockwebbff.app.AppDtos.ProfileView;
import app.viaverse.mockwebbff.app.AppDtos.ProviderView;
import app.viaverse.mockwebbff.app.AppDtos.RegisterStartRequest;
import app.viaverse.mockwebbff.app.AppDtos.RegisterStartView;
import app.viaverse.mockwebbff.app.AppDtos.RegisterVerifyEmailRequest;
import app.viaverse.mockwebbff.app.AppDtos.RegistrationDraftView;
import app.viaverse.mockwebbff.app.AppDtos.RequiredConsentsView;
import app.viaverse.mockwebbff.app.AppDtos.PasswordLoginRequest;
import app.viaverse.mockwebbff.app.AppDtos.SavedSearchView;
import app.viaverse.mockwebbff.app.AppDtos.SendMessageRequest;
import app.viaverse.mockwebbff.app.AppDtos.ServiceCategoryView;
import app.viaverse.mockwebbff.app.AppDtos.ServiceRequestView;
import app.viaverse.mockwebbff.app.AppDtos.SessionView;
import app.viaverse.mockwebbff.app.AppDtos.SettingsView;
import app.viaverse.mockwebbff.app.AppDtos.SwitchPersonaRequest;
import app.viaverse.mockwebbff.app.AppDtos.SponsoredAdView;
import app.viaverse.mockwebbff.app.AppDtos.TransactionView;
import app.viaverse.mockwebbff.app.AppDtos.UpdateMessageRequest;
import app.viaverse.mockwebbff.app.AppDtos.UpdatePostRequest;
import app.viaverse.mockwebbff.app.AppDtos.UserView;
import app.viaverse.mockwebbff.app.seed.SocialSeed;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MockAppService {

    private final MockAppRepository repository;

    public MockAppService(MockAppRepository repository) {
        this.repository = repository;
    }

    public synchronized RequiredConsentsView requiredConsents() {
        return new RequiredConsentsView(
            List.of(
                new ConsentDocumentView("TERMS", "LEGAL", "mock-2026-05", "/legal/terms"),
                new ConsentDocumentView("PRIVACY", "LEGAL", "mock-2026-05", "/legal/privacy")
            ),
            new ConsentDocumentView("MARKETING", "OPTIONAL", "mock-2026-05", "/legal/marketing")
        );
    }

    public synchronized CapabilityTermsView capabilityTerms() {
        return new CapabilityTermsView(List.of(
            new ConsentDocumentView("PROVIDER_TERMS", "CAPABILITY", "mock-provider-2026-05", "/legal/provider"),
            new ConsentDocumentView("BUSINESS_TERMS", "CAPABILITY", "mock-business-2026-05", "/legal/business")
        ));
    }

    public synchronized AuthSessionView passwordLogin(PasswordLoginRequest request) {
        requireText(request == null ? null : request.identifier(), "identifier");
        requireText(request.password(), "password");
        MockAppState state = state();
        IdentityAccountView account = state.identityAccounts().stream()
            .filter(identity -> matchesIdentifier(identity, request.identifier()))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mock identity account not found"));
        if (!account.password().equals(request.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mock identity password does not match");
        }
        MockAppState updated = withCurrentUser(state, account.accountId());
        repository.save(updated);
        return authSession(updated);
    }

    public synchronized AuthSessionView refreshAuth() {
        return authSession(state());
    }

    public synchronized void logoutAuth() {
        // No-op by design: mock identity does not manage secure cookies.
    }

    public synchronized RegisterStartView registerStart(RegisterStartRequest request) {
        requireText(request == null ? null : request.email(), "email");
        requireText(request.displayName(), "displayName");
        requireText(request.password(), "password");
        MockAppState state = state();
        boolean duplicate = state.identityAccounts().stream()
            .anyMatch(account -> account.email().equalsIgnoreCase(request.email().trim()));
        if (duplicate) {
            throw badRequest("This mock email is already registered");
        }
        String draftId = "draft-" + UUID.randomUUID();
        state.registrationDrafts().add(new RegistrationDraftView(
            draftId,
            request.email().trim().toLowerCase(Locale.ROOT),
            request.displayName().trim(),
            fallback(request.firstName(), ""),
            fallback(request.lastName(), ""),
            request.password(),
            now()
        ));
        repository.save(state);
        return new RegisterStartView(draftId, "email-flow-" + draftId, Instant.now().plusSeconds(900).toString(), false);
    }

    public synchronized AuthSessionView registerVerifyEmail(RegisterVerifyEmailRequest request) {
        requireText(request == null ? null : request.draftId(), "draftId");
        requireText(request.otp(), "otp");
        MockAppState state = state();
        RegistrationDraftView draft = state.registrationDrafts().stream()
            .filter(item -> item.id().equals(request.draftId()))
            .findFirst()
            .orElseThrow(() -> badRequest("Registration draft expired"));
        String accountId = "user-" + UUID.randomUUID();
        UserView user = new UserView(
            accountId,
            draft.displayName(),
            fallback(draft.firstName(), firstNameFromDisplay(draft.displayName())),
            fallback(draft.lastName(), ""),
            initials(draft.displayName()),
            "STANDARD",
            "Hizmet alan",
            "Konum seçilmedi",
            List.of(
                new AppDtos.CapabilityView("STANDARD", "Hizmet alan", true, "ENABLED", "Yakındaki akışı takip eder, paylaşım ve talep oluşturur."),
                new AppDtos.CapabilityView("INDIVIDUAL_PROVIDER", "Bireysel hizmet veren", false, "AVAILABLE", "İsterse hizmet veren görünümünü açabilir."),
                new AppDtos.CapabilityView("BUSINESS", "İşletme", false, "AVAILABLE", "İşletme profili başlatabilir.")
            )
        );
        state.users().add(user);
        state.identityAccounts().add(new IdentityAccountView(
            "identity-" + UUID.randomUUID(),
            accountId,
            draft.email(),
            null,
            draft.password(),
            draft.displayName(),
            "ACTIVE"
        ));
        state.profiles().add(new ProfileView(
            accountId,
            draft.displayName(),
            "Yeni Viaverse kullanıcısı",
            "Yakınındaki akışı ve hizmetleri yeni keşfediyor.",
            "Konum seçilmedi",
            "STANDARD",
            user.capabilities(),
            null,
            null,
            48,
            42
        ));
        state.registrationDrafts().removeIf(item -> item.id().equals(draft.id()));
        MockAppState updated = withCurrentUser(state, accountId);
        repository.save(updated);
        return authSession(updated);
    }

    public synchronized ForgotPasswordStartView forgotPasswordStart(ForgotPasswordStartRequest request) {
        requireText(request == null ? null : request.identifier(), "identifier");
        return new ForgotPasswordStartView("forgot-" + UUID.randomUUID(), request.identifier().contains("@") ? "EMAIL" : "PHONE", Instant.now().plusSeconds(900).toString());
    }

    public synchronized ForgotPasswordTokenView forgotPasswordVerify(ForgotPasswordVerifyRequest request) {
        requireText(request == null ? null : request.flowId(), "flowId");
        requireText(request.otp(), "otp");
        return new ForgotPasswordTokenView("reset-" + UUID.randomUUID(), Instant.now().plusSeconds(900).toString());
    }

    public synchronized void forgotPasswordComplete(ForgotPasswordCompleteRequest request) {
        requireText(request == null ? null : request.resetToken(), "resetToken");
        requireText(request.newPassword(), "newPassword");
    }

    public synchronized SessionView session() {
        MockAppState state = state();
        return new SessionView(currentUser(state), state.users());
    }

    public synchronized SessionView switchPersona(SwitchPersonaRequest request) {
        if (request == null || isBlank(request.personaId())) {
            throw badRequest("personaId is required");
        }
        MockAppState state = state();
        UserView next = findUser(state, request.personaId());
        MockAppState updated = new MockAppState(
            next.id(),
            state.identityAccounts(),
            state.registrationDrafts(),
            state.users(),
            state.profiles(),
            state.settings(),
            state.categories(),
            state.providers(),
            state.businesses(),
            state.feedItems(),
            state.postComments(),
            state.serviceRequests(),
            state.offers(),
            state.conversations(),
            state.messages(),
            state.transactions(),
            state.notifications(),
            state.savedSearches()
        );
        repository.save(updated);
        return new SessionView(next, updated.users());
    }

    public synchronized List<FeedItemView> feed(String type) {
        Predicate<FeedItemView> filter = feedFilter(type);
        return state().feedItems().stream()
            .filter(filter)
            .sorted(Comparator.comparing(FeedItemView::createdAt).reversed())
            .toList();
    }

    public synchronized FeedItemView createPost(CreatePostRequest request) {
        requireText(request == null ? null : request.body(), "body");
        MockAppState state = state();
        UserView user = currentUser(state);
        String type = normalizeContentType(request.type());
        String now = now();
        String id = "feed-" + UUID.randomUUID();
        FeedItemView item = new FeedItemView(
            id,
            type,
            typeLabel(type),
            fallback(request.title(), defaultTitle(type)),
            request.body().trim(),
            user.displayName(),
            user.activeCapabilityLabel(),
            fallback(request.locationScope(), user.locationLabel()),
            fallback(request.categoryId(), "local-help"),
            now,
            0,
            false,
            0,
            0,
            false,
            typeLabel(type),
            null,
            normalizeHashtags(request.hashtags(), request.body()),
            fallback(request.mediaUrl(), null),
            normalizeMediaType(request.mediaType(), request.mediaUrl())
        );
        state.feedItems().add(item);
        repository.save(state);
        return item;
    }

    public synchronized FeedItemView updatePost(String postId, UpdatePostRequest request) {
        requireText(request == null ? null : request.body(), "body");
        MockAppState state = state();
        FeedItemView current = findFeedItem(state, postId);
        UserView user = currentUser(state);
        if (!current.authorName().equals(user.displayName())) {
            throw badRequest("Only the current user's mock posts can be edited");
        }
        String type = normalizeContentType(fallback(request.type(), current.type()));
        FeedItemView updated = new FeedItemView(
            current.id(),
            type,
            typeLabel(type),
            fallback(request.title(), current.title()),
            request.body().trim(),
            current.authorName(),
            current.authorType(),
            fallback(request.locationScope(), current.locationLabel()),
            fallback(request.categoryId(), current.categoryId()),
            current.createdAt(),
            current.likeCount(),
            current.liked(),
            current.commentCount(),
            current.shareCount(),
            current.saved(),
            typeLabel(type),
            current.relatedRequestId(),
            normalizeHashtags(request.hashtags(), request.body()),
            fallback(request.mediaUrl(), current.mediaUrl()),
            normalizeMediaType(fallback(request.mediaType(), current.mediaType()), fallback(request.mediaUrl(), current.mediaUrl()))
        );
        replaceFeedItem(state, updated);
        repository.save(state);
        return updated;
    }

    public synchronized FeedItemView likePost(String postId) {
        MockAppState state = state();
        FeedItemView current = findFeedItem(state, postId);
        boolean liked = !current.liked();
        int likeCount = Math.max(0, current.likeCount() + (liked ? 1 : -1));
        FeedItemView updated = withSocialState(current, likeCount, liked, current.commentCount(), current.shareCount(), current.saved());
        replaceFeedItem(state, updated);
        repository.save(state);
        return updated;
    }

    public synchronized FeedItemView savePost(String postId) {
        MockAppState state = state();
        FeedItemView current = findFeedItem(state, postId);
        FeedItemView updated = withSocialState(current, current.likeCount(), current.liked(), current.commentCount(), current.shareCount(), !current.saved());
        replaceFeedItem(state, updated);
        repository.save(state);
        return updated;
    }

    public synchronized FeedItemView sharePost(String postId) {
        MockAppState state = state();
        FeedItemView current = findFeedItem(state, postId);
        FeedItemView updated = withSocialState(current, current.likeCount(), current.liked(), current.commentCount(), current.shareCount() + 1, current.saved());
        replaceFeedItem(state, updated);
        repository.save(state);
        return updated;
    }

    public synchronized List<PostCommentView> comments(String postId) {
        return state().postComments().stream()
            .filter(comment -> comment.postId().equals(postId))
            .sorted(Comparator.comparing(PostCommentView::createdAt))
            .toList();
    }

    public synchronized PostCommentView createComment(String postId, CreateCommentRequest request) {
        requireText(request == null ? null : request.body(), "body");
        MockAppState state = state();
        FeedItemView current = findFeedItem(state, postId);
        UserView user = currentUser(state);
        PostCommentView comment = new PostCommentView(
            "comment-" + UUID.randomUUID(),
            postId,
            user.id(),
            user.displayName(),
            request.body().trim(),
            now()
        );
        state.postComments().add(comment);
        replaceFeedItem(state, withSocialState(current, current.likeCount(), current.liked(), current.commentCount() + 1, current.shareCount(), current.saved()));
        repository.save(state);
        return comment;
    }

    public synchronized List<HashtagSuggestionView> hashtagSuggestions(String query) {
        String normalizedQuery = cleanHashtag(query);
        Map<String, Integer> counts = new HashMap<>();
        Map<String, String> samples = new HashMap<>();
        for (FeedItemView item : state().feedItems()) {
            for (String tag : item.hashtags()) {
                if (normalizedQuery.isEmpty() || tag.contains(normalizedQuery)) {
                    counts.merge(tag, 1, Integer::sum);
                    samples.putIfAbsent(tag, item.title());
                }
            }
        }
        return counts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
            .limit(10)
            .map(entry -> new HashtagSuggestionView(entry.getKey(), entry.getValue(), samples.get(entry.getKey())))
            .toList();
    }

    public synchronized List<MockPhotoView> mockPhotos(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.forLanguageTag("tr-TR"));
        return SocialSeed.mockPhotos().stream()
            .filter(photo -> normalized.isEmpty()
                || photo.alt().toLowerCase(Locale.forLanguageTag("tr-TR")).contains(normalized)
                || photo.tags().stream().anyMatch(tag -> tag.contains(normalized)))
            .toList();
    }

    public synchronized List<SponsoredAdView> sponsoredAds(String surface) {
        return SocialSeed.sponsoredAds();
    }

    public synchronized List<FeedItemView> events() {
        return feed("EVENT");
    }

    public synchronized List<FeedItemView> announcements() {
        return feed("ANNOUNCEMENT");
    }

    public synchronized List<AnnouncementIncidentView> announcementIncidents() {
        return SocialSeed.announcementIncidents();
    }

    public synchronized List<ServiceCategoryView> categories() {
        return state().categories();
    }

    public synchronized List<ProviderView> providers() {
        return state().providers();
    }

    public synchronized ProviderView provider(String id) {
        return state().providers().stream()
            .filter(provider -> provider.id().equals(id))
            .findFirst()
            .orElseThrow(() -> notFound("Provider not found"));
    }

    public synchronized List<BusinessView> businesses() {
        return state().businesses();
    }

    public synchronized BusinessView business(String id) {
        return state().businesses().stream()
            .filter(business -> business.id().equals(id))
            .findFirst()
            .orElseThrow(() -> notFound("Business not found"));
    }

    public synchronized List<SavedSearchView> savedSearches(String surface) {
        MockAppState state = state();
        String currentUserId = state.currentUserId();
        String normalizedSurface = surface == null ? "" : surface.trim().toLowerCase(Locale.ROOT);
        return state.savedSearches().stream()
            .filter(search -> search.ownerId().equals(currentUserId))
            .filter(search -> normalizedSurface.isEmpty() || search.surface().equalsIgnoreCase(normalizedSurface))
            .sorted(Comparator.comparing(SavedSearchView::createdAt).reversed())
            .toList();
    }

    public synchronized SavedSearchView createSavedSearch(CreateSavedSearchRequest request) {
        requireText(request == null ? null : request.surface(), "surface");
        requireText(request.name(), "name");
        MockAppState state = state();
        SavedSearchView saved = new SavedSearchView(
            "saved-search-" + UUID.randomUUID(),
            state.currentUserId(),
            request.surface().trim().toLowerCase(Locale.ROOT),
            request.name().trim(),
            request.filters() == null ? Map.of() : request.filters(),
            now()
        );
        state.savedSearches().add(saved);
        repository.save(state);
        return saved;
    }

    public synchronized List<ServiceRequestView> myRequests() {
        MockAppState state = state();
        String currentUserId = state.currentUserId();
        return state.serviceRequests().stream()
            .filter(request -> request.requesterId().equals(currentUserId))
            .sorted(Comparator.comparing(ServiceRequestView::createdAt).reversed())
            .toList();
    }

    public synchronized ServiceRequestView createRequest(CreateServiceRequestRequest request) {
        requireText(request == null ? null : request.title(), "title");
        requireText(request.description(), "description");
        MockAppState state = state();
        UserView user = currentUser(state);
        ServiceCategoryView category = category(state, fallback(request.categoryId(), "local-help"));
        String now = now();
        ServiceRequestView created = new ServiceRequestView(
            "request-" + UUID.randomUUID(),
            user.id(),
            user.displayName(),
            request.title().trim(),
            category.id(),
            category.label(),
            request.description().trim(),
            fallback(request.locationScope(), user.locationLabel()),
            fallback(request.timing(), "Zaman esnek"),
            fallback(request.budgetExpectation(), "Bütçe konuşulur"),
            "OPEN",
            0,
            null,
            null,
            now,
            now
        );
        state.serviceRequests().add(created);
        state.feedItems().add(new FeedItemView(
            "feed-" + UUID.randomUUID(),
            "REQUEST",
            "Talep",
            created.title(),
            created.description(),
            user.displayName(),
            user.activeCapabilityLabel(),
            created.locationScope(),
            created.categoryId(),
            now,
            0,
            false,
            0,
            0,
            false,
            "Talep açık",
            created.id(),
            normalizeHashtags(null, created.description()),
            mediaForType("REQUEST", created.categoryId()),
            "IMAGE"
        ));
        state.notifications().add(new NotificationView(
            "notification-" + UUID.randomUUID(),
            "Talebin yayınlandı",
            created.title() + " talebi uygun hizmet verenlere görünür oldu.",
            "REQUEST",
            false,
            now
        ));
        repository.save(state);
        return created;
    }

    public synchronized List<OpportunityView> opportunities() {
        MockAppState state = state();
        UserView user = currentUser(state);
        ProfileView profile = currentProfile(state);
        List<String> serviceCategories = activeServiceCategories(profile, user.activeCapability());
        return state.serviceRequests().stream()
            .filter(request -> "OPEN".equals(request.status()))
            .filter(request -> !request.requesterId().equals(user.id()))
            .map(request -> opportunity(request, serviceCategories))
            .sorted(Comparator.comparing(OpportunityView::fitScore).reversed())
            .toList();
    }

    public synchronized OfferView createOffer(CreateOfferRequest request) {
        requireText(request == null ? null : request.requestId(), "requestId");
        requireText(request.message(), "message");
        MockAppState state = state();
        UserView user = currentUser(state);
        if (!hasProviderCapability(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Current persona has no provider capability");
        }
        ServiceRequestView serviceRequest = findRequest(state, request.requestId());
        if (!"OPEN".equals(serviceRequest.status())) {
            throw badRequest("Only open requests can receive offers");
        }
        String now = now();
        OfferView offer = new OfferView(
            "offer-" + UUID.randomUUID(),
            serviceRequest.id(),
            user.id(),
            user.displayName(),
            user.activeCapabilityLabel(),
            fallback(request.amountExpectation(), "Tutar konuşulur"),
            request.message().trim(),
            "SUBMITTED",
            null,
            now,
            now
        );
        state.offers().add(offer);
        replaceRequest(state, withOfferCount(serviceRequest, countOffers(state, serviceRequest.id())));
        state.notifications().add(new NotificationView(
            "notification-" + UUID.randomUUID(),
            "Yeni teklif gönderildi",
            serviceRequest.title() + " talebine teklif verildi.",
            "OFFER",
            false,
            now
        ));
        repository.save(state);
        return offer;
    }

    public synchronized List<OfferView> myOffers() {
        String currentUserId = state().currentUserId();
        return state().offers().stream()
            .filter(offer -> offer.providerId().equals(currentUserId))
            .sorted(Comparator.comparing(OfferView::createdAt).reversed())
            .toList();
    }

    public synchronized List<OfferView> offersForRequest(String requestId) {
        return state().offers().stream()
            .filter(offer -> offer.requestId().equals(requestId))
            .sorted(Comparator.comparing(OfferView::createdAt).reversed())
            .toList();
    }

    public synchronized OfferAcceptanceView acceptOffer(String offerId) {
        MockAppState state = state();
        OfferView offer = findOffer(state, offerId);
        ServiceRequestView request = findRequest(state, offer.requestId());
        if (!request.requesterId().equals(state.currentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the requester can accept this offer");
        }
        String now = now();
        String conversationId = offer.conversationId() == null ? "conversation-" + UUID.randomUUID() : offer.conversationId();
        OfferView accepted = new OfferView(
            offer.id(),
            offer.requestId(),
            offer.providerId(),
            offer.providerName(),
            offer.providerType(),
            offer.amountExpectation(),
            offer.message(),
            "ACCEPTED",
            conversationId,
            offer.createdAt(),
            now
        );
        replaceOffer(state, accepted);
        state.offers().stream()
            .filter(other -> other.requestId().equals(request.id()))
            .filter(other -> !other.id().equals(accepted.id()))
            .filter(other -> "SUBMITTED".equals(other.status()))
            .toList()
            .forEach(other -> replaceOffer(state, new OfferView(
                other.id(),
                other.requestId(),
                other.providerId(),
                other.providerName(),
                other.providerType(),
                other.amountExpectation(),
                other.message(),
                "REJECTED",
                other.conversationId(),
                other.createdAt(),
                now
            )));
        ServiceRequestView matched = new ServiceRequestView(
            request.id(),
            request.requesterId(),
            request.requesterName(),
            request.title(),
            request.categoryId(),
            request.categoryLabel(),
            request.description(),
            request.locationScope(),
            request.timing(),
            request.budgetExpectation(),
            "MATCHED",
            request.offerCount(),
            accepted.id(),
            conversationId,
            request.createdAt(),
            now
        );
        replaceRequest(state, matched);
        ConversationView conversation = new ConversationView(
            conversationId,
            request.title(),
            "Kabul edilen teklif",
            accepted.providerName(),
            accepted.providerType(),
            "Teklif kabul edildi. Devamı için mesajlaşma açıldı.",
            now,
            0,
            request.id(),
            accepted.id()
        );
        upsertConversation(state, conversation);
        state.messages().add(new MessageView(
            "message-" + UUID.randomUUID(),
            conversationId,
            "system",
            "Viaverse",
            "Teklif kabul edildi. Devamı için mesajlaşma açıldı.",
            true,
            now,
            now
        ));
        state.notifications().add(new NotificationView(
            "notification-" + UUID.randomUUID(),
            "Teklif kabul edildi",
            accepted.providerName() + " ile mesajlaşma başlatıldı.",
            "MESSAGE",
            false,
            now
        ));
        repository.save(state);
        return new OfferAcceptanceView(accepted, conversation);
    }

    public synchronized List<ConversationView> conversations() {
        return state().conversations().stream()
            .sorted(Comparator.comparing(ConversationView::lastMessageAt).reversed())
            .toList();
    }

    public synchronized List<MessageView> messages(String conversationId) {
        return state().messages().stream()
            .filter(message -> message.conversationId().equals(conversationId))
            .sorted(Comparator.comparing(MessageView::createdAt))
            .toList();
    }

    public synchronized MessageView sendMessage(String conversationId, SendMessageRequest request) {
        requireText(request == null ? null : request.body(), "body");
        MockAppState state = state();
        ConversationView conversation = findConversation(state, conversationId);
        UserView user = currentUser(state);
        String now = now();
        MessageView message = new MessageView(
            "message-" + UUID.randomUUID(),
            conversationId,
            user.id(),
            user.displayName(),
            request.body().trim(),
            false,
            now,
            now
        );
        state.messages().add(message);
        replaceConversation(state, new ConversationView(
            conversation.id(),
            conversation.title(),
            conversation.contextLabel(),
            conversation.participantName(),
            conversation.participantType(),
            message.body(),
            now,
            conversation.unreadCount(),
            conversation.relatedRequestId(),
            conversation.relatedOfferId()
        ));
        repository.save(state);
        return message;
    }

    public synchronized MessageView updateMessage(String conversationId, String messageId, UpdateMessageRequest request) {
        requireText(request == null ? null : request.body(), "body");
        MockAppState state = state();
        UserView user = currentUser(state);
        ConversationView conversation = findConversation(state, conversationId);
        MessageView current = findMessage(state, conversationId, messageId);
        if (current.system()) {
            throw badRequest("System messages cannot be edited");
        }
        if (!current.senderId().equals(user.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the sender can edit this message");
        }
        String now = now();
        MessageView updated = new MessageView(
            current.id(),
            current.conversationId(),
            current.senderId(),
            current.senderName(),
            request.body().trim(),
            false,
            current.createdAt(),
            now
        );
        replaceMessage(state, updated);
        MessageView latest = latestMessage(state, conversationId);
        if (latest != null && latest.id().equals(updated.id())) {
            replaceConversation(state, new ConversationView(
                conversation.id(),
                conversation.title(),
                conversation.contextLabel(),
                conversation.participantName(),
                conversation.participantType(),
                updated.body(),
                conversation.lastMessageAt(),
                conversation.unreadCount(),
                conversation.relatedRequestId(),
                conversation.relatedOfferId()
            ));
        }
        repository.save(state);
        return updated;
    }

    public synchronized ProfileView profile() {
        return currentProfile(state());
    }

    public synchronized ProfileView patchProfile(PatchProfileRequest request) {
        MockAppState state = state();
        ProfileView current = currentProfile(state);
        String activeCapability = request.activeCapability() == null
            ? current.activeCapability()
            : request.activeCapability();
        if (!capabilityEnabled(current, activeCapability)) {
            throw badRequest("Selected capability is not enabled for this persona");
        }
        ProfileView updated = new ProfileView(
            state.currentUserId(),
            fallback(request.displayName(), current.displayName()),
            fallback(request.headline(), current.headline()),
            fallback(request.bio(), current.bio()),
            fallback(request.locationLabel(), current.locationLabel()),
            activeCapability,
            current.capabilities(),
            current.individualProviderProfile(),
            current.businessProfile(),
            current.trustScore(),
            current.completionScore()
        );
        replaceProfile(state, updated);
        replaceUserForProfile(state, updated);
        repository.save(state);
        return updated;
    }

    public synchronized SettingsView settings() {
        return state().settings();
    }

    public synchronized SettingsView patchSettings(PatchSettingsRequest request) {
        MockAppState state = state();
        SettingsView current = state.settings();
        SettingsView updated = new SettingsView(
            request.pushNotifications() == null ? current.pushNotifications() : request.pushNotifications(),
            request.emailDigest() == null ? current.emailDigest() : request.emailDigest(),
            request.requestUpdates() == null ? current.requestUpdates() : request.requestUpdates(),
            fallback(request.privacyLevel(), current.privacyLevel()),
            fallback(request.language(), current.language()),
            fallback(request.theme(), current.theme())
        );
        MockAppState next = new MockAppState(
            state.currentUserId(),
            state.identityAccounts(),
            state.registrationDrafts(),
            state.users(),
            state.profiles(),
            updated,
            state.categories(),
            state.providers(),
            state.businesses(),
            state.feedItems(),
            state.postComments(),
            state.serviceRequests(),
            state.offers(),
            state.conversations(),
            state.messages(),
            state.transactions(),
            state.notifications(),
            state.savedSearches()
        );
        repository.save(next);
        return updated;
    }

    public synchronized List<TransactionView> transactions() {
        return state().transactions().stream()
            .sorted(Comparator.comparing(TransactionView::createdAt).reversed())
            .toList();
    }

    public synchronized TransactionView createPaymentIntent(CreatePaymentIntentRequest request) {
        requireText(request == null ? null : request.title(), "title");
        if (request.amountMinor() <= 0) {
            throw badRequest("amountMinor must be positive");
        }
        MockAppState state = state();
        String now = now();
        TransactionView transaction = new TransactionView(
            "txn-" + UUID.randomUUID(),
            "MOCK_INTENT",
            request.title().trim(),
            request.amountMinor(),
            fallback(request.currency(), "TRY"),
            "PENDING",
            "Mock ödeme niyeti oluşturuldu; gerçek ödeme sağlayıcısı yok.",
            request.relatedRequestId(),
            now,
            now
        );
        state.transactions().add(transaction);
        repository.save(state);
        return transaction;
    }

    public synchronized TransactionView updatePaymentStatus(String transactionId, PaymentStatusRequest request) {
        MockAppState state = state();
        TransactionView current = state.transactions().stream()
            .filter(transaction -> transaction.id().equals(transactionId))
            .findFirst()
            .orElseThrow(() -> notFound("Transaction not found"));
        String status = normalizePaymentStatus(request == null ? null : request.status());
        TransactionView updated = new TransactionView(
            current.id(),
            current.type(),
            current.title(),
            current.amountMinor(),
            current.currency(),
            status,
            current.description(),
            current.relatedRequestId(),
            current.createdAt(),
            now()
        );
        replaceTransaction(state, updated);
        repository.save(state);
        return updated;
    }

    public synchronized List<NotificationView> notifications() {
        return state().notifications().stream()
            .sorted(Comparator.comparing(NotificationView::createdAt).reversed())
            .toList();
    }

    public synchronized SessionView reset() {
        MockAppState reset = repository.reset(SeedData::initialState);
        return new SessionView(currentUser(reset), reset.users());
    }

    private MockAppState state() {
        MockAppState loaded = repository.loadOrSeed(SeedData::initialState);
        if (loaded.identityAccounts() == null
            || loaded.registrationDrafts() == null
            || loaded.postComments() == null
            || loaded.savedSearches() == null) {
            return repository.reset(SeedData::initialState);
        }
        migrateFeedMedia(loaded);
        return loaded;
    }

    private void migrateFeedMedia(MockAppState state) {
        boolean changed = false;
        for (int i = 0; i < state.feedItems().size(); i++) {
            FeedItemView item = state.feedItems().get(i);
            String mediaUrl = item.mediaUrl();
            String mediaType = item.mediaType();
            if (List.of("feed-market-event", "feed-rain-alert").contains(item.id())) {
                mediaUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4";
                mediaType = "VIDEO";
            } else if (isBlank(mediaType)) {
                mediaType = normalizeMediaType(null, mediaUrl);
            }
            if (!equalsNullable(mediaUrl, item.mediaUrl()) || !equalsNullable(mediaType, item.mediaType())) {
                state.feedItems().set(i, new FeedItemView(
                    item.id(),
                    item.type(),
                    item.typeLabel(),
                    item.title(),
                    item.body(),
                    item.authorName(),
                    item.authorType(),
                    item.locationLabel(),
                    item.categoryId(),
                    item.createdAt(),
                    item.likeCount(),
                    item.liked(),
                    item.commentCount(),
                    item.shareCount(),
                    item.saved(),
                    item.highlight(),
                    item.relatedRequestId(),
                    item.hashtags(),
                    mediaUrl,
                    mediaType
                ));
                changed = true;
            }
        }
        if (changed) {
            repository.save(state);
        }
    }

    private UserView currentUser(MockAppState state) {
        return findUser(state, state.currentUserId());
    }

    private AuthSessionView authSession(MockAppState state) {
        return new AuthSessionView(
            "AUTHENTICATED",
            "mock-access-" + state.currentUserId(),
            Instant.now().plusSeconds(3600).toString(),
            "mock-refresh-" + state.currentUserId(),
            Instant.now().plusSeconds(2_592_000).toString(),
            currentUser(state)
        );
    }

    private boolean matchesIdentifier(IdentityAccountView identity, String identifier) {
        String normalized = identifier.trim().toLowerCase(Locale.ROOT);
        return identity.email().equalsIgnoreCase(normalized)
            || (identity.phone() != null && identity.phone().replace(" ", "").equals(identifier.replace(" ", "")));
    }

    private MockAppState withCurrentUser(MockAppState state, String currentUserId) {
        return new MockAppState(
            currentUserId,
            state.identityAccounts(),
            state.registrationDrafts(),
            state.users(),
            state.profiles(),
            state.settings(),
            state.categories(),
            state.providers(),
            state.businesses(),
            state.feedItems(),
            state.postComments(),
            state.serviceRequests(),
            state.offers(),
            state.conversations(),
            state.messages(),
            state.transactions(),
            state.notifications(),
            state.savedSearches()
        );
    }

    private UserView findUser(MockAppState state, String userId) {
        return state.users().stream()
            .filter(user -> user.id().equals(userId))
            .findFirst()
            .orElseThrow(() -> notFound("User persona not found"));
    }

    private ProfileView currentProfile(MockAppState state) {
        return state.profiles().stream()
            .filter(profile -> profile.accountId().equals(state.currentUserId()))
            .findFirst()
            .orElseThrow(() -> notFound("Profile not found"));
    }

    private ServiceCategoryView category(MockAppState state, String categoryId) {
        return state.categories().stream()
            .filter(category -> category.id().equals(categoryId))
            .findFirst()
            .orElseGet(() -> state.categories().stream()
                .filter(category -> "local-help".equals(category.id()))
                .findFirst()
                .orElseThrow(() -> notFound("Category not found")));
    }

    private ServiceRequestView findRequest(MockAppState state, String requestId) {
        return state.serviceRequests().stream()
            .filter(request -> request.id().equals(requestId))
            .findFirst()
            .orElseThrow(() -> notFound("Request not found"));
    }

    private OfferView findOffer(MockAppState state, String offerId) {
        return state.offers().stream()
            .filter(offer -> offer.id().equals(offerId))
            .findFirst()
            .orElseThrow(() -> notFound("Offer not found"));
    }

    private ConversationView findConversation(MockAppState state, String conversationId) {
        return state.conversations().stream()
            .filter(conversation -> conversation.id().equals(conversationId))
            .findFirst()
            .orElseThrow(() -> notFound("Conversation not found"));
    }

    private MessageView findMessage(MockAppState state, String conversationId, String messageId) {
        return state.messages().stream()
            .filter(message -> message.conversationId().equals(conversationId))
            .filter(message -> message.id().equals(messageId))
            .findFirst()
            .orElseThrow(() -> notFound("Message not found"));
    }

    private FeedItemView findFeedItem(MockAppState state, String postId) {
        return state.feedItems().stream()
            .filter(item -> item.id().equals(postId))
            .findFirst()
            .orElseThrow(() -> notFound("Post not found"));
    }

    private OpportunityView opportunity(ServiceRequestView request, List<String> serviceCategories) {
        boolean categoryMatch = serviceCategories.contains(request.categoryId());
        int fitScore = categoryMatch ? 92 : 64;
        String reason = categoryMatch
            ? "Hizmet alanlarınla eşleşiyor"
            : "Yakınında açık talep";
        return new OpportunityView(request, reason, fitScore);
    }

    private List<String> activeServiceCategories(ProfileView profile, String activeCapability) {
        if ("BUSINESS".equals(activeCapability) && profile.businessProfile() != null) {
            return profile.businessProfile().serviceCategoryIds();
        }
        if (profile.individualProviderProfile() != null) {
            return profile.individualProviderProfile().serviceCategoryIds();
        }
        return List.of();
    }

    private boolean hasProviderCapability(UserView user) {
        return user.capabilities().stream().anyMatch(capability ->
            capability.enabled()
                && ("INDIVIDUAL_PROVIDER".equals(capability.key()) || "BUSINESS".equals(capability.key()))
        );
    }

    private boolean capabilityEnabled(ProfileView profile, String capabilityKey) {
        return profile.capabilities().stream()
            .anyMatch(capability -> capability.key().equals(capabilityKey) && capability.enabled());
    }

    private int countOffers(MockAppState state, String requestId) {
        return (int) state.offers().stream()
            .filter(offer -> offer.requestId().equals(requestId))
            .count();
    }

    private ServiceRequestView withOfferCount(ServiceRequestView request, int offerCount) {
        return new ServiceRequestView(
            request.id(),
            request.requesterId(),
            request.requesterName(),
            request.title(),
            request.categoryId(),
            request.categoryLabel(),
            request.description(),
            request.locationScope(),
            request.timing(),
            request.budgetExpectation(),
            request.status(),
            offerCount,
            request.acceptedOfferId(),
            request.conversationId(),
            request.createdAt(),
            now()
        );
    }

    private void replaceRequest(MockAppState state, ServiceRequestView updated) {
        for (int i = 0; i < state.serviceRequests().size(); i++) {
            if (state.serviceRequests().get(i).id().equals(updated.id())) {
                state.serviceRequests().set(i, updated);
                return;
            }
        }
        throw notFound("Request not found");
    }

    private void replaceOffer(MockAppState state, OfferView updated) {
        for (int i = 0; i < state.offers().size(); i++) {
            if (state.offers().get(i).id().equals(updated.id())) {
                state.offers().set(i, updated);
                return;
            }
        }
        throw notFound("Offer not found");
    }

    private void upsertConversation(MockAppState state, ConversationView updated) {
        for (int i = 0; i < state.conversations().size(); i++) {
            if (state.conversations().get(i).id().equals(updated.id())) {
                state.conversations().set(i, updated);
                return;
            }
        }
        state.conversations().add(updated);
    }

    private void replaceConversation(MockAppState state, ConversationView updated) {
        for (int i = 0; i < state.conversations().size(); i++) {
            if (state.conversations().get(i).id().equals(updated.id())) {
                state.conversations().set(i, updated);
                return;
            }
        }
        throw notFound("Conversation not found");
    }

    private void replaceMessage(MockAppState state, MessageView updated) {
        for (int i = 0; i < state.messages().size(); i++) {
            if (state.messages().get(i).id().equals(updated.id())) {
                state.messages().set(i, updated);
                return;
            }
        }
        throw notFound("Message not found");
    }

    private void replaceProfile(MockAppState state, ProfileView updated) {
        for (int i = 0; i < state.profiles().size(); i++) {
            if (state.profiles().get(i).accountId().equals(updated.accountId())) {
                state.profiles().set(i, updated);
                return;
            }
        }
        throw notFound("Profile not found");
    }

    private void replaceUserForProfile(MockAppState state, ProfileView profile) {
        for (int i = 0; i < state.users().size(); i++) {
            UserView user = state.users().get(i);
            if (user.id().equals(profile.accountId())) {
                String activeLabel = user.capabilities().stream()
                    .filter(capability -> capability.key().equals(profile.activeCapability()))
                    .findFirst()
                    .map(AppDtos.CapabilityView::label)
                    .orElse(user.activeCapabilityLabel());
                state.users().set(i, new UserView(
                    user.id(),
                    profile.displayName(),
                    user.firstName(),
                    user.lastName(),
                    initials(profile.displayName()),
                    profile.activeCapability(),
                    activeLabel,
                    profile.locationLabel(),
                    user.capabilities()
                ));
                return;
            }
        }
        throw notFound("User not found");
    }

    private void replaceTransaction(MockAppState state, TransactionView updated) {
        for (int i = 0; i < state.transactions().size(); i++) {
            if (state.transactions().get(i).id().equals(updated.id())) {
                state.transactions().set(i, updated);
                return;
            }
        }
        throw notFound("Transaction not found");
    }

    private void replaceFeedItem(MockAppState state, FeedItemView updated) {
        for (int i = 0; i < state.feedItems().size(); i++) {
            if (state.feedItems().get(i).id().equals(updated.id())) {
                state.feedItems().set(i, updated);
                return;
            }
        }
        throw notFound("Post not found");
    }

    private MessageView latestMessage(MockAppState state, String conversationId) {
        return state.messages().stream()
            .filter(message -> message.conversationId().equals(conversationId))
            .max(Comparator.comparing(MessageView::createdAt))
            .orElse(null);
    }

    private FeedItemView withSocialState(FeedItemView item, int likeCount, boolean liked, int commentCount, int shareCount, boolean saved) {
        return new FeedItemView(
            item.id(),
            item.type(),
            item.typeLabel(),
            item.title(),
            item.body(),
            item.authorName(),
            item.authorType(),
            item.locationLabel(),
            item.categoryId(),
            item.createdAt(),
            likeCount,
            liked,
            commentCount,
            shareCount,
            saved,
            item.highlight(),
            item.relatedRequestId(),
            item.hashtags(),
            item.mediaUrl(),
            item.mediaType()
        );
    }

    private Predicate<FeedItemView> feedFilter(String type) {
        if (isBlank(type) || "ALL".equalsIgnoreCase(type)) {
            return item -> true;
        }
        String normalized = type.trim().toUpperCase(Locale.ROOT);
        if ("SOCIAL".equals(normalized)) {
            return item -> !List.of("REQUEST", "OPPORTUNITY").contains(item.type());
        }
        if ("POST".equals(normalized) || "POSTS".equals(normalized)) {
            return item -> "POST".equals(item.type()) || "INFO".equals(item.type());
        }
        if ("HELP".equals(normalized) || "REQUEST".equals(normalized)) {
            return item -> "REQUEST".equals(item.type()) || "OPPORTUNITY".equals(item.type());
        }
        if ("ALERT".equals(normalized)) {
            return item -> "TRAFFIC".equals(item.type()) || "UTILITY".equals(item.type());
        }
        return item -> normalized.equals(item.type());
    }

    private String normalizeContentType(String rawType) {
        if (isBlank(rawType)) {
            return "POST";
        }
        String normalized = rawType.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "ANNOUNCEMENT", "EVENT", "REQUEST", "OPPORTUNITY", "INFO", "TRAFFIC", "UTILITY" -> normalized;
            default -> "POST";
        };
    }

    private List<String> normalizeHashtags(List<String> explicitHashtags, String body) {
        ArrayList<String> tags = new ArrayList<>();
        if (explicitHashtags != null) {
            explicitHashtags.stream()
                .map(this::cleanHashtag)
                .filter(tag -> !tag.isEmpty())
                .forEach(tags::add);
        }
        if (body != null) {
            for (String token : body.split("\\s+")) {
                if (token.startsWith("#")) {
                    String tag = cleanHashtag(token);
                    if (!tag.isEmpty() && !tags.contains(tag)) {
                        tags.add(tag);
                    }
                }
            }
        }
        return tags;
    }

    private String cleanHashtag(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace("#", "")
            .replaceAll("[^\\p{L}\\p{N}_-]", "")
            .toLowerCase(Locale.forLanguageTag("tr-TR"));
    }

    private String mediaForType(String type, String categoryId) {
        String fileName = switch (type) {
            case "ANNOUNCEMENT" -> "announcement.png";
            case "EVENT" -> "events.png";
            case "REQUEST" -> "local_help.png";
            case "OPPORTUNITY" -> "work.png";
            case "TRAFFIC", "UTILITY", "INFO" -> "advisory.png";
            default -> switch (fallback(categoryId, "local-help")) {
                case "cleaning" -> "cleaning.png";
                case "repair" -> "home_repair.png";
                case "pets" -> "pets.png";
                case "delivery" -> "logistics.png";
                case "education" -> "education.png";
                case "consulting" -> "professional_consulting.png";
                default -> "advisory.png";
            };
        };
        return "/brand/assets/categories/" + fileName;
    }

    private String normalizeMediaType(String rawType, String mediaUrl) {
        if (!isBlank(rawType)) {
            String normalized = rawType.trim().toUpperCase(Locale.ROOT);
            if ("VIDEO".equals(normalized)) {
                return "VIDEO";
            }
            return "IMAGE";
        }
        if (!isBlank(mediaUrl)) {
            String normalizedUrl = mediaUrl.trim().toLowerCase(Locale.ROOT);
            if (normalizedUrl.startsWith("data:video")
                || normalizedUrl.endsWith(".mp4")
                || normalizedUrl.endsWith(".webm")
                || normalizedUrl.endsWith(".mov")) {
                return "VIDEO";
            }
        }
        return "IMAGE";
    }

    private String typeLabel(String type) {
        return switch (type) {
            case "ANNOUNCEMENT" -> "Duyuru";
            case "EVENT" -> "Etkinlik";
            case "TRAFFIC" -> "Trafik";
            case "UTILITY" -> "Kesinti";
            case "INFO" -> "Bilgi";
            case "REQUEST" -> "Talep";
            case "OPPORTUNITY" -> "Fırsat";
            default -> "Paylaşım";
        };
    }

    private String defaultTitle(String type) {
        return switch (type) {
            case "ANNOUNCEMENT" -> "Yeni duyuru";
            case "EVENT" -> "Yeni etkinlik";
            case "TRAFFIC" -> "Yeni ulaşım bilgisi";
            case "UTILITY" -> "Yeni kesinti bilgisi";
            case "INFO" -> "Yeni bilgi paylaşımı";
            case "REQUEST" -> "Yeni talep";
            default -> "Yeni paylaşım";
        };
    }

    private String normalizePaymentStatus(String rawStatus) {
        if (isBlank(rawStatus)) {
            throw badRequest("status is required");
        }
        String status = rawStatus.trim().toUpperCase(Locale.ROOT);
        if (!List.of("PENDING", "COMPLETED", "FAILED", "CANCELLED").contains(status)) {
            throw badRequest("Unsupported payment status");
        }
        return status;
    }

    private String initials(String displayName) {
        if (isBlank(displayName)) {
            return "?";
        }
        String[] parts = displayName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase(Locale.ROOT);
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase(Locale.ROOT);
    }

    private String firstNameFromDisplay(String displayName) {
        if (isBlank(displayName)) {
            return "";
        }
        return displayName.trim().split("\\s+")[0];
    }

    private void requireText(String value, String fieldName) {
        if (isBlank(value)) {
            throw badRequest(fieldName + " is required");
        }
    }

    private String fallback(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean equalsNullable(String left, String right) {
        return left == null ? right == null : left.equals(right);
    }

    private String now() {
        return Instant.now().toString();
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
