package app.viaverse.mockwebbff.app.seed;

import app.viaverse.mockwebbff.app.AppDtos.CapabilityView;
import app.viaverse.mockwebbff.app.AppDtos.UserView;
import java.util.ArrayList;
import java.util.List;

public final class PersonaSeed {

    private PersonaSeed() {
    }

    public static ArrayList<UserView> users() {
        return new ArrayList<>(List.of(
            new UserView(
                "user-standard",
                "Deniz Arslan",
                "Deniz",
                "Arslan",
                "DA",
                "STANDARD",
                "Bireysel",
                "Kadıköy, İstanbul",
                List.of(
                    new CapabilityView("STANDARD", "Bireysel", true, "ENABLED", "Yakındaki akışı takip eder, paylaşım ve talep oluşturur."),
                    new CapabilityView("INDIVIDUAL_PROVIDER", "Serbest Uzman", false, "AVAILABLE", "İsterse serbest uzman görünümünü açabilir."),
                    new CapabilityView("BUSINESS", "İşletme", false, "AVAILABLE", "İşletme profili başlatabilir.")
                )
            ),
            new UserView(
                "user-provider",
                "Ece Kaya",
                "Ece",
                "Kaya",
                "EK",
                "INDIVIDUAL_PROVIDER",
                "Serbest Uzman",
                "Moda, İstanbul",
                List.of(
                    new CapabilityView("STANDARD", "Bireysel", true, "ENABLED", "Kendi ihtiyaçları için de talep açabilir."),
                    new CapabilityView("INDIVIDUAL_PROVIDER", "Serbest Uzman", true, "ENABLED", "Küçük işler, evcil hayvan ve teslimat işleri alır."),
                    new CapabilityView("BUSINESS", "İşletme", false, "AVAILABLE", "Gerekirse işletme profili başlatabilir.")
                )
            ),
            new UserView(
                "user-business",
                "Mert Çınar",
                "Mert",
                "Çınar",
                "MÇ",
                "BUSINESS",
                "İşletme",
                "Yeldeğirmeni, İstanbul",
                List.of(
                    new CapabilityView("STANDARD", "Bireysel", true, "ENABLED", "Gündelik kullanıcı akışına dönebilir."),
                    new CapabilityView("INDIVIDUAL_PROVIDER", "Serbest Uzman", true, "ENABLED", "İşletme dışı kişisel hizmetlerini ayrıca yönetebilir."),
                    new CapabilityView("BUSINESS", "İşletme", true, "APPROVED", "Onaylı işletme profiliyle hizmet ve duyuru yayınlar.")
                )
            )
        ));
    }
}
