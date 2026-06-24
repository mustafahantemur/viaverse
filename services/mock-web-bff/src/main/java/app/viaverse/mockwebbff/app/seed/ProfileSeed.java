package app.viaverse.mockwebbff.app.seed;

import app.viaverse.mockwebbff.app.AppDtos.BusinessProfileView;
import app.viaverse.mockwebbff.app.AppDtos.ProfileView;
import app.viaverse.mockwebbff.app.AppDtos.ProviderProfileView;
import app.viaverse.mockwebbff.app.AppDtos.SettingsView;
import app.viaverse.mockwebbff.app.AppDtos.UserView;
import java.util.ArrayList;
import java.util.List;

public final class ProfileSeed {

    private ProfileSeed() {
    }

    public static ArrayList<ProfileView> profiles(List<UserView> users) {
        return new ArrayList<>(List.of(
            new ProfileView(
                "user-standard",
                "Deniz Arslan",
                "Yakınında pratik destek ve güvenilir hizmet arıyor.",
                "Evcil hayvan, teslimat ve küçük tamir işleri için Viaverse'i deniyor.",
                "Kadıköy, İstanbul",
                "STANDARD",
                users.get(0).capabilities(),
                null,
                null,
                64,
                72
            ),
            new ProfileView(
                "user-provider",
                "Ece Kaya",
                "Kısa süreli destek, evcil hayvan ve teslimat işleri.",
                "Öğrenciyim; ders aralarında yakındaki küçük işleri alabiliyorum.",
                "Moda, İstanbul",
                "INDIVIDUAL_PROVIDER",
                users.get(1).capabilities(),
                new ProviderProfileView(
                    "Bireysel hizmet veren",
                    "Evcil hayvan refakati, kısa teslimat ve küçük gündelik destek işleri.",
                    "Hafta içi 18:00 sonrası, hafta sonu esnek.",
                    List.of("pets", "delivery", "local-help"),
                    false,
                    "Moda ve Kadıköy merkezli 25 dakikalık ulaşım yarıçapı"
                ),
                null,
                78,
                86
            ),
            new ProfileView(
                "user-business",
                "Mert Çınar",
                "Butik kafe işletmecisi ve etkinlik duyuruları.",
                "Yeni açılan işletmemiz için çevredeki etkinlik ve hizmet görünürlüğünü yönetiyorum.",
                "Yeldeğirmeni, İstanbul",
                "BUSINESS",
                users.get(2).capabilities(),
                new ProviderProfileView(
                    "Bireysel hizmet veren",
                    "İşletme dışı etkinlik planlama ve küçük organizasyon desteği.",
                    "Randevu ile",
                    List.of("consulting"),
                    true,
                    "Kadıköy ve çevresi"
                ),
                new BusinessProfileView(
                    "İşletme",
                    "Köşe Fırın Kafe",
                    "Köşe Fırın Kafe Gıda Ltd.",
                    "Kafe & fırın",
                    "Yeldeğirmeni, Kadıköy",
                    "+90 216 000 00 00",
                    "merhaba@kosefirin.example",
                    List.of("delivery", "local-help"),
                    "APPROVED"
                ),
                82,
                91
            )
        ));
    }

    public static SettingsView settings() {
        return new SettingsView(true, true, true, "Yakınımda görünür", "tr", "light");
    }
}
