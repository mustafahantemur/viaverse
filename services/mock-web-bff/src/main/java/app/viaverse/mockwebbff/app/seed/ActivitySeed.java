package app.viaverse.mockwebbff.app.seed;

import app.viaverse.mockwebbff.app.AppDtos.NotificationView;
import java.util.ArrayList;
import java.util.List;

public final class ActivitySeed {

    private ActivitySeed() {
    }

    public static ArrayList<NotificationView> notifications() {
        return new ArrayList<>(List.of(
            new NotificationView("notification-offer", "Yeni teklif geldi", "Ece Kaya, veteriner refakati talebine teklif verdi.", "OFFER", false, SeedClock.minutesAgo(30)),
            new NotificationView("notification-event", "Yakında etkinlik var", "Hafta sonu üretici pazarı etkinliği yakınında öne çıkıyor.", "EVENT", false, SeedClock.minutesAgo(70)),
            new NotificationView("notification-payment", "Mock ödeme tamamlandı", "Paket teslimatı için demo ödeme kaydı tamamlandı.", "PAYMENT", true, SeedClock.minutesAgo(250))
        ));
    }
}
