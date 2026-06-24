package app.viaverse.mockwebbff.app.seed;

import app.viaverse.mockwebbff.app.AppDtos.BusinessView;
import app.viaverse.mockwebbff.app.AppDtos.ProviderView;
import java.util.ArrayList;
import java.util.List;

public final class ServiceDiscoverySeed {

    private ServiceDiscoverySeed() {
    }

    public static ArrayList<ProviderView> providers() {
        return new ArrayList<>(List.of(
            new ProviderView(
                "provider-ece",
                "Ece Kaya",
                "Bireysel hizmet veren",
                "Yakında küçük işler ve evcil hayvan desteği",
                "Kısa süreli refakat, veteriner ulaşımı ve küçük teslimat işlerinde hızlı dönüş yapar.",
                List.of("pets", "delivery", "local-help"),
                "Moda + 25 dk ulaşım yarıçapı",
                4.8,
                34,
                "Genelde 12 dk içinde",
                true,
                List.of("öğrenci", "akşam uygun", "hızlı teslimat")
            ),
            new ProviderView(
                "provider-omer",
                "Ömer Yıldız",
                "Bireysel hizmet veren",
                "Elektrik ve küçük bakım işleri",
                "Priz, anahtar, aydınlatma ve küçük bakım işleri için yerinde destek.",
                List.of("repair", "local-help"),
                "Kadıköy, Acıbadem, Hasanpaşa",
                4.9,
                118,
                "Genelde 25 dk içinde",
                true,
                List.of("usta", "aynı gün", "yerinde keşif")
            ),
            new ProviderView(
                "provider-selin",
                "Selin Aydın",
                "Bireysel hizmet veren",
                "Özel ders ve öğrenme koçluğu",
                "Matematik, İngilizce ve sınav planlama desteği.",
                List.of("education"),
                "Kadıköy + uzaktan",
                4.7,
                52,
                "Genelde 1 saat içinde",
                false,
                List.of("uzaktan", "hafta sonu", "özel ders")
            )
        ));
    }

    public static ArrayList<BusinessView> businesses() {
        return new ArrayList<>(List.of(
            new BusinessView(
                "business-kose-firin",
                "Köşe Fırın Kafe",
                "İşletme",
                "Kafe & fırın",
                "Yeni açılan butik kafe; sabah kahvesi, küçük etkinlikler ve çevreye paket destekleri.",
                List.of("delivery", "local-help"),
                "Yeldeğirmeni, Kadıköy",
                4.6,
                86,
                "Genelde 20 dk içinde",
                "APPROVED"
            ),
            new BusinessView(
                "business-temiz-ev",
                "Temiz Ev Atölyesi",
                "İşletme",
                "Temizlik",
                "Düzenli ev ve ofis temizliği için küçük ekipli, randevulu hizmet.",
                List.of("cleaning"),
                "Kadıköy ve Üsküdar",
                4.8,
                203,
                "Genelde aynı gün",
                "APPROVED"
            ),
            new BusinessView(
                "business-tamir-noktasi",
                "Tamir Noktası",
                "İşletme",
                "Tamir / bakım",
                "Ev, ofis ve küçük işletmeler için planlı bakım ve acil tamir ekibi.",
                List.of("repair"),
                "İstanbul Anadolu yakası",
                4.5,
                141,
                "Genelde 40 dk içinde",
                "APPROVED"
            )
        ));
    }
}
