package app.viaverse.mockwebbff.app.seed;

import app.viaverse.mockwebbff.app.AppDtos.OfferView;
import app.viaverse.mockwebbff.app.AppDtos.ServiceRequestView;
import java.util.ArrayList;
import java.util.List;

public final class WorkSeed {

    private WorkSeed() {
    }

    public static ArrayList<ServiceRequestView> requests() {
        return new ArrayList<>(List.of(
            request("request-cat-vet", "user-standard", "Deniz Arslan", "Bu akşam kedimi veterinere götürmem gerekiyor", "pets", "Evcil hayvan desteği", "Taşıma çantam var; 19:00 gibi kısa süreli refakat ve taksi desteği arıyorum.", "Kadıköy içinde", "Bugün 19:00", "300-500 TL", "OPEN", 1, null, null, SeedClock.minutesAgo(45)),
            request("request-electrician", "user-standard", "Deniz Arslan", "Elektrik ustası önerisi arıyorum", "repair", "Tamir / bakım", "Mutfak prizinde temassızlık var. Bugün ya da yarın bakabilecek biriyle görüşmek istiyorum.", "Kadıköy / Rasimpaşa", "Bugün veya yarın", "Keşif sonrası", "OPEN", 0, null, null, SeedClock.minutesAgo(120)),
            request("request-accepted-delivery", "user-standard", "Deniz Arslan", "Küçük paket teslimatı", "delivery", "Taşıma / teslimat", "Kadıköy'den Moda'ya küçük bir paket teslim edilecek.", "Kadıköy - Moda", "Bugün 16:00", "200 TL", "MATCHED", 1, "offer-delivery-accepted", "conversation-delivery", SeedClock.minutesAgo(360))
        ));
    }

    public static ArrayList<OfferView> offers() {
        return new ArrayList<>(List.of(
            new OfferView("offer-cat-vet-ece", "request-cat-vet", "user-provider", "Ece Kaya", "Bireysel hizmet veren", "450 TL", "19:00'da uygun olurum, taşıma ve dönüş için de yardımcı olurum.", "SUBMITTED", null, SeedClock.minutesAgo(30), SeedClock.minutesAgo(30)),
            new OfferView("offer-delivery-accepted", "request-accepted-delivery", "user-provider", "Ece Kaya", "Bireysel hizmet veren", "200 TL", "Paketi bugün 16:00'da teslim edebilirim.", "ACCEPTED", "conversation-delivery", SeedClock.minutesAgo(330), SeedClock.minutesAgo(310))
        ));
    }

    private static ServiceRequestView request(
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
        String createdAt
    ) {
        return new ServiceRequestView(id, requesterId, requesterName, title, categoryId, categoryLabel, description, locationScope, timing, budgetExpectation, status, offerCount, acceptedOfferId, conversationId, createdAt, createdAt);
    }
}
