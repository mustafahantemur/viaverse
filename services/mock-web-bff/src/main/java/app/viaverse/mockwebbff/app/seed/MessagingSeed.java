package app.viaverse.mockwebbff.app.seed;

import app.viaverse.mockwebbff.app.AppDtos.ConversationView;
import app.viaverse.mockwebbff.app.AppDtos.MessageView;
import java.util.ArrayList;
import java.util.List;

public final class MessagingSeed {

    private MessagingSeed() {
    }

    public static ArrayList<ConversationView> conversations() {
        return new ArrayList<>(List.of(
            new ConversationView("conversation-delivery", "Küçük paket teslimatı", "Kabul edilen teklif", "Ece Kaya", "Bireysel hizmet veren", "Tamam, paketi alacağım noktayı mesajla paylaşabilirsin.", SeedClock.minutesAgo(290), 1, "request-accepted-delivery", "offer-delivery-accepted"),
            new ConversationView("conversation-business", "Köşe Fırın Kafe duyurusu", "İşletme mesajı", "Köşe Fırın Kafe", "İşletme", "Cumartesi etkinliği için rezervasyon gerekmiyor.", SeedClock.minutesAgo(220), 0, null, null)
        ));
    }

    public static ArrayList<MessageView> messages() {
        return new ArrayList<>(List.of(
            new MessageView("message-delivery-system", "conversation-delivery", "system", "Viaverse", "Teklif kabul edildi. Devamı için konuşma açıldı.", true, SeedClock.minutesAgo(310), SeedClock.minutesAgo(310)),
            new MessageView("message-delivery-ece", "conversation-delivery", "user-provider", "Ece Kaya", "Tamam, paketi alacağım noktayı mesajla paylaşabilirsin.", false, SeedClock.minutesAgo(290), SeedClock.minutesAgo(290)),
            new MessageView("message-business-1", "conversation-business", "business-kose-firin", "Köşe Fırın Kafe", "Cumartesi etkinliği için rezervasyon gerekmiyor.", false, SeedClock.minutesAgo(220), SeedClock.minutesAgo(190))
        ));
    }
}
