package app.viaverse.mockwebbff.app;

import app.viaverse.mockwebbff.app.AppDtos.ConversationView;
import app.viaverse.mockwebbff.app.AppDtos.CreateOfferRequest;
import app.viaverse.mockwebbff.app.AppDtos.CreateServiceRequestRequest;
import app.viaverse.mockwebbff.app.AppDtos.FeedItemView;
import app.viaverse.mockwebbff.app.AppDtos.MessageView;
import app.viaverse.mockwebbff.app.AppDtos.NotificationView;
import app.viaverse.mockwebbff.app.AppDtos.OfferAcceptanceView;
import app.viaverse.mockwebbff.app.AppDtos.OfferView;
import app.viaverse.mockwebbff.app.AppDtos.OpportunityView;
import app.viaverse.mockwebbff.app.AppDtos.ProfileView;
import app.viaverse.mockwebbff.app.AppDtos.ServiceCategoryView;
import app.viaverse.mockwebbff.app.AppDtos.ServiceRequestView;
import app.viaverse.mockwebbff.app.AppDtos.UserView;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MockRequestService extends MockDomainService {

    public MockRequestService(MockAppRepository repository) {
        super(repository);
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
        ServiceCategoryView cat = category(state, fallback(request.categoryId(), "local-help"));
        String now = now();
        ServiceRequestView created = new ServiceRequestView(
            "request-" + UUID.randomUUID(),
            user.id(), user.displayName(),
            request.title().trim(),
            cat.id(), cat.label(),
            request.description().trim(),
            fallback(request.locationScope(), user.locationLabel()),
            fallback(request.timing(), "Zaman esnek"),
            fallback(request.budgetExpectation(), "Bütçe konuşulur"),
            "OPEN", 0, null, null, now, now
        );
        state.serviceRequests().add(created);
        state.feedItems().add(new FeedItemView(
            "feed-" + UUID.randomUUID(),
            "REQUEST", "Talep",
            created.title(), created.description(),
            user.displayName(), user.activeCapabilityLabel(),
            created.locationScope(), created.categoryId(),
            now, 0, false, 0, 0, false, "Talep açık", created.id(),
            normalizeHashtags(null, created.description()),
            mediaForType("REQUEST", created.categoryId()), "IMAGE"
        ));
        state.notifications().add(new NotificationView(
            "notification-" + UUID.randomUUID(),
            "Talebin yayınlandı",
            created.title() + " talebi uygun hizmet verenlere görünür oldu.",
            "REQUEST", false, now
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
            serviceRequest.id(), user.id(), user.displayName(), user.activeCapabilityLabel(),
            fallback(request.amountExpectation(), "Tutar konuşulur"),
            request.message().trim(),
            "SUBMITTED", null, now, now
        );
        state.offers().add(offer);
        replaceRequest(state, withOfferCount(serviceRequest, countOffers(state, serviceRequest.id())));
        state.notifications().add(new NotificationView(
            "notification-" + UUID.randomUUID(),
            "Yeni teklif gönderildi",
            serviceRequest.title() + " talebine teklif verildi.",
            "OFFER", false, now
        ));
        repository.save(state);
        return offer;
    }

    public synchronized List<OfferView> myOffers() {
        MockAppState state = state();
        String currentUserId = state.currentUserId();
        return state.offers().stream()
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
            offer.id(), offer.requestId(), offer.providerId(), offer.providerName(), offer.providerType(),
            offer.amountExpectation(), offer.message(), "ACCEPTED", conversationId, offer.createdAt(), now
        );
        replaceOffer(state, accepted);
        state.offers().stream()
            .filter(other -> other.requestId().equals(request.id()))
            .filter(other -> !other.id().equals(accepted.id()))
            .filter(other -> "SUBMITTED".equals(other.status()))
            .toList()
            .forEach(other -> replaceOffer(state, new OfferView(
                other.id(), other.requestId(), other.providerId(), other.providerName(), other.providerType(),
                other.amountExpectation(), other.message(), "REJECTED", other.conversationId(), other.createdAt(), now
            )));
        ServiceRequestView matched = new ServiceRequestView(
            request.id(), request.requesterId(), request.requesterName(),
            request.title(), request.categoryId(), request.categoryLabel(),
            request.description(), request.locationScope(), request.timing(),
            request.budgetExpectation(), "MATCHED", request.offerCount(),
            accepted.id(), conversationId, request.createdAt(), now
        );
        replaceRequest(state, matched);
        ConversationView conversation = new ConversationView(
            conversationId, request.title(), "Kabul edilen teklif",
            accepted.providerName(), accepted.providerType(),
            "Teklif kabul edildi. Devamı için mesajlaşma açıldı.",
            now, 0, request.id(), accepted.id()
        );
        upsertConversation(state, conversation);
        state.messages().add(new MessageView(
            "message-" + UUID.randomUUID(), conversationId,
            "system", "Viaverse",
            "Teklif kabul edildi. Devamı için mesajlaşma açıldı.",
            true, now, now
        ));
        state.notifications().add(new NotificationView(
            "notification-" + UUID.randomUUID(),
            "Teklif kabul edildi",
            accepted.providerName() + " ile mesajlaşma başlatıldı.",
            "MESSAGE", false, now
        ));
        repository.save(state);
        return new OfferAcceptanceView(accepted, conversation);
    }

    private ServiceCategoryView category(MockAppState state, String categoryId) {
        return state.categories().stream()
            .filter(cat -> cat.id().equals(categoryId))
            .findFirst()
            .orElseGet(() -> state.categories().stream()
                .filter(cat -> "local-help".equals(cat.id()))
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

    private OpportunityView opportunity(ServiceRequestView request, List<String> serviceCategories) {
        boolean categoryMatch = serviceCategories.contains(request.categoryId());
        int fitScore = categoryMatch ? 92 : 64;
        String reason = categoryMatch ? "Hizmet alanlarınla eşleşiyor" : "Yakınında açık talep";
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
            capability.enabled() && ("INDIVIDUAL_PROVIDER".equals(capability.key()) || "BUSINESS".equals(capability.key()))
        );
    }

    private int countOffers(MockAppState state, String requestId) {
        return (int) state.offers().stream().filter(offer -> offer.requestId().equals(requestId)).count();
    }

    private ServiceRequestView withOfferCount(ServiceRequestView request, int offerCount) {
        return new ServiceRequestView(
            request.id(), request.requesterId(), request.requesterName(),
            request.title(), request.categoryId(), request.categoryLabel(),
            request.description(), request.locationScope(), request.timing(),
            request.budgetExpectation(), request.status(), offerCount,
            request.acceptedOfferId(), request.conversationId(), request.createdAt(), now()
        );
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
}
