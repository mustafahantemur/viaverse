package app.viaverse.mockwebbff.app.seed;

import app.viaverse.mockwebbff.app.AppDtos.ServiceCategoryView;
import java.util.ArrayList;
import java.util.List;

public final class CategorySeed {

    private CategorySeed() {
    }

    public static ArrayList<ServiceCategoryView> categories() {
        return new ArrayList<>(List.of(
            new ServiceCategoryView("cleaning", "Temizlik", "Ev ve iş yeri temizliği, düzenleme ve kısa süreli destek.", "Hizmet", "cleaning.png", "Bireysel hizmet veren"),
            new ServiceCategoryView("repair", "Tamir / bakım", "Elektrik, su, mobilya ve küçük bakım işleri.", "Hizmet", "home_repair.png", "Bireysel hizmet veren"),
            new ServiceCategoryView("pets", "Evcil hayvan desteği", "Gezdirme, veteriner ulaşımı, kısa süreli bakım.", "Yakın mesafe", "pets.png", "Bireysel hizmet veren"),
            new ServiceCategoryView("delivery", "Taşıma / teslimat", "Küçük taşıma, hızlı teslimat ve gündelik lojistik.", "Yakın mesafe", "logistics.png", "Bireysel hizmet veren"),
            new ServiceCategoryView("education", "Eğitim / özel ders", "Özel ders, sınav desteği ve beceri atölyeleri.", "Hizmet", "education.png", "Bireysel hizmet veren"),
            new ServiceCategoryView("consulting", "Tasarım / danışmanlık", "Tasarım, danışmanlık, dijital ve profesyonel destek.", "Profesyonel", "professional_consulting.png", "Bireysel hizmet veren"),
            new ServiceCategoryView("local-help", "Yakın küçük işler", "Yakındaki kısa süreli yardım, refakat, elden teslim ve pratik işler.", "Yakın mesafe", "local_help.png", "Bireysel hizmet veren")
        ));
    }
}
