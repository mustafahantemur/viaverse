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
                "Serbest Uzman",
                "Evcil hayvan bakımı ve hızlı teslimat",
                "Kısa süreli refakat, veteriner ulaşımı ve küçük teslimat işlerinde hızlı dönüş yapar. Moda çevresinde saatler içinde ulaşır.",
                List.of("pets", "delivery"),
                "Moda + 25 dk ulaşım yarıçapı",
                4.8, 34, "Genelde 12 dk içinde", true,
                List.of("öğrenci", "akşam uygun", "hızlı teslimat"),
                "https://picsum.photos/seed/ece-kaya-profile/400/400",
                "₺150–250/iş", 0.8
            ),
            new ProviderView(
                "provider-omer",
                "Ömer Yıldız",
                "Serbest Uzman",
                "Elektrik ve küçük bakım işleri",
                "Priz, anahtar, aydınlatma ve küçük bakım işleri için yerinde destek. Aynı gün keşif yapabilir, ek ücret almaz.",
                List.of("repair"),
                "Kadıköy, Acıbadem, Hasanpaşa",
                4.9, 118, "Genelde 25 dk içinde", true,
                List.of("usta", "aynı gün", "yerinde keşif"),
                "https://picsum.photos/seed/omer-yildiz-profile/400/400",
                "₺200–400/sa", 1.2
            ),
            new ProviderView(
                "provider-selin",
                "Selin Aydın",
                "Serbest Uzman",
                "Özel ders ve öğrenme koçluğu",
                "Matematik, İngilizce ve sınav planlama desteği. Uzaktan veya yüz yüze çalışır. LGS ve YKS odaklı deneyim.",
                List.of("education"),
                "Kadıköy + uzaktan",
                4.7, 52, "Genelde 1 saat içinde", false,
                List.of("uzaktan", "hafta sonu", "özel ders"),
                "https://picsum.photos/seed/selin-aydin-profile/400/400",
                "₺300–450/sa", 2.3
            ),
            new ProviderView(
                "provider-ahmet",
                "Ahmet Demir",
                "Serbest Uzman",
                "Ev ve ofis temizliği",
                "Düzenli veya tek seferlik temizlik. Kendi malzemeleriyle gelir, evcil hayvanlara duyarlı ürünler kullanır. Referans gösterebilir.",
                List.of("cleaning"),
                "Kadıköy, Üsküdar, Ataşehir",
                4.6, 97, "Genelde aynı gün", true,
                List.of("güvenilir", "kendi malzeme", "randevulu"),
                "https://picsum.photos/seed/ahmet-demir-profile/400/400",
                "₺180–280/sa", 0.6
            ),
            new ProviderView(
                "provider-zeynep",
                "Zeynep Arslan",
                "Serbest Uzman",
                "Fotoğrafçı ve görsel içerik üreticisi",
                "Ürün, etkinlik ve portre fotoğrafçılığı. Düzenleme dahil hızlı teslimat. Instagram içerikleri için özel paketler mevcut.",
                List.of("creative"),
                "İstanbul geneli",
                4.8, 41, "Genelde 2 saat içinde", false,
                List.of("fotoğraf", "instagram", "hızlı teslimat"),
                "https://picsum.photos/seed/zeynep-foto-profile/400/400",
                "₺800–1.500/iş", 3.1
            ),
            new ProviderView(
                "provider-mehmet",
                "Mehmet Çelik",
                "Serbest Uzman",
                "Bahçe, peyzaj ve dış mekan bakımı",
                "Çim biçme, budama, toprak düzenleme ve küçük peyzaj işleri. Balkon ve teras düzenlemelerinde de deneyimli.",
                List.of("repair", "cleaning"),
                "Kadıköy, Bostancı, Maltepe",
                4.5, 63, "Genelde 3 saat içinde", true,
                List.of("bahçe", "peyzaj", "hafta sonu"),
                "https://picsum.photos/seed/mehmet-bahce-profile/400/400",
                "₺250–400/sa", 1.7
            ),
            new ProviderView(
                "provider-ayse",
                "Ayşe Yılmaz",
                "Serbest Uzman",
                "Kişisel bakım ve wellness koçu",
                "Evde saç bakımı, manikür ve masaj hizmetleri. Sertifikalı aromaterapi ve refleksoloji uzmanı.",
                List.of("beauty"),
                "Moda, Caddebostan, Fenerbahçe",
                4.9, 78, "Genelde 30 dk içinde", true,
                List.of("wellness", "evde hizmet", "sertifikalı"),
                "https://picsum.photos/seed/ayse-wellness-profile/400/400",
                "₺350–550/seans", 0.9
            ),
            new ProviderView(
                "provider-burak",
                "Burak Özdemir",
                "Serbest Uzman",
                "Nakliye ve taşıma hizmetleri",
                "Küçük ve orta büyüklükte ev/ofis taşımaları. Kiralık araç ve yardımcı personel ile komple hizmet.",
                List.of("delivery"),
                "İstanbul Anadolu yakası",
                4.4, 29, "Genelde 2 saat içinde", false,
                List.of("nakliye", "aracı var", "kiralık taşıt"),
                "https://picsum.photos/seed/burak-nakliye-profile/400/400",
                "₺500–900/taşıma", 2.8
            ),
            new ProviderView(
                "provider-fatma",
                "Fatma Kara",
                "Serbest Uzman",
                "Matematik ve fen bilimleri öğretmeni",
                "Ortaokul ve lise öğrencilerine yönelik özel ders. LGS ve TYT/AYT odaklı, kendi hazırladığı soru bankasıyla çalışır.",
                List.of("education"),
                "Bağcılar + uzaktan",
                4.8, 86, "Genelde 1 saat içinde", false,
                List.of("matematik", "fen", "sınav hazırlık"),
                "https://picsum.photos/seed/fatma-kara-profile/400/400",
                "₺200–350/sa", 1.5
            ),
            new ProviderView(
                "provider-kemal",
                "Kemal Boztaş",
                "Serbest Uzman",
                "Bilgisayar ve teknik destek uzmanı",
                "Yazılım kurulumu, virüs temizleme, ağ sorunları ve donanım bakımı. Ofis ve ev ziyaretleri mümkün.",
                List.of("digital", "repair"),
                "Pendik, Kartal, Maltepe",
                4.7, 144, "Genelde 45 dk içinde", true,
                List.of("teknik", "evde servis", "hızlı çözüm"),
                "https://picsum.photos/seed/kemal-teknik-profile/400/400",
                "₺300–500/sa", 0.7
            ),
            new ProviderView(
                "provider-leyla",
                "Leyla Şahin",
                "Serbest Uzman",
                "Kişisel antrenör ve fitness koçu",
                "Evde veya parkta bireysel antrenman. Beslenme planlama dahil. Kadın koçlama programlarında uzman.",
                List.of("beauty", "education"),
                "Moda Parkı + Kadıköy çevresi",
                4.9, 55, "Genelde 1 saat içinde", true,
                List.of("fitness", "sağlıklı yaşam", "kadın koçu"),
                "https://picsum.photos/seed/leyla-fitness-profile/400/400",
                "₺400–600/sa", 1.1
            ),
            new ProviderView(
                "provider-tolga",
                "Tolga Avcı",
                "Serbest Uzman",
                "Tesisat ve su sistemleri",
                "Su kaçağı, tıkanıklık, kombi bakımı ve doğalgaz tesisatı. Acil durumlara gece dahil müdahale eder.",
                List.of("repair"),
                "Üsküdar, Beykoz, Ümraniye",
                4.6, 201, "Genelde 1 saat içinde", true,
                List.of("tesisatçı", "acil müdahale", "gece hizmet"),
                "https://picsum.photos/seed/tolga-tesisat-profile/400/400",
                "₺300–600/sa", 1.9
            ),
            new ProviderView(
                "provider-hande",
                "Hande Kılıç",
                "Serbest Uzman",
                "Etkinlik organizasyonu ve dekorasyon",
                "Doğum günü, nişan ve küçük toplantılar için konsept tasarımı ve yerinde kurulum. Balon, çiçek ve prop temin eder.",
                List.of("events", "creative"),
                "İstanbul Avrupa ve Anadolu yakası",
                4.7, 38, "Genelde 4 saat içinde", false,
                List.of("etkinlik", "doğum günü", "dekorasyon"),
                "https://picsum.photos/seed/hande-etkinlik-profile/400/400",
                "₺1.000–2.500/etkinlik", 4.2
            ),
            new ProviderView(
                "provider-ibrahim",
                "İbrahim Yurt",
                "Serbest Uzman",
                "Hızlı kurye ve ulaştırma",
                "Motokurye ile belgeler, küçük paketler ve kişisel eşya teslimatı. İstanbul içi ortalama 35 dakika.",
                List.of("delivery"),
                "İstanbul geneli",
                4.5, 312, "Genelde 10 dk içinde", true,
                List.of("motokurye", "hızlı", "aynı gün"),
                "https://picsum.photos/seed/ibrahim-kurye-profile/400/400",
                "₺100–200/teslimat", 0.5
            ),
            new ProviderView(
                "provider-deniz",
                "Deniz Özkan",
                "Serbest Uzman",
                "Grafik tasarım ve marka kimliği",
                "Logo, sosyal medya görselleri, broşür ve kurumsal kimlik çalışmaları. Figma ve Adobe Suite kullanır.",
                List.of("creative", "consulting"),
                "Tüm Türkiye (uzaktan)",
                4.9, 67, "Genelde 3 saat içinde", false,
                List.of("grafik", "logo", "uzaktan"),
                "https://picsum.photos/seed/deniz-grafik-profile/400/400",
                "₺500–900/proje", 5.8
            ),
            new ProviderView(
                "provider-canan",
                "Canan Şimşek",
                "Serbest Uzman",
                "Diyetisyen ve beslenme danışmanı",
                "Kişiye özel beslenme planı, diyet ve sağlıklı yaşam koçluğu. Uzaktan veya yüz yüze görüşme yapılabilir.",
                List.of("beauty", "education"),
                "Beşiktaş + uzaktan",
                4.8, 43, "Genelde 2 saat içinde", false,
                List.of("diyetisyen", "sağlıklı yaşam", "uzaktan"),
                "https://picsum.photos/seed/canan-diyet-profile/400/400",
                "₺300–500/seans", 3.4
            ),
            new ProviderView(
                "provider-serkan",
                "Serkan Doğan",
                "Serbest Uzman",
                "Web geliştirici ve SEO uzmanı",
                "React, Next.js ve WordPress ile web sitesi geliştirme. SEO teknik analiz ve içerik optimizasyonu.",
                List.of("digital", "creative"),
                "Tüm Türkiye (uzaktan)",
                4.7, 59, "Genelde 4 saat içinde", false,
                List.of("web", "SEO", "react", "uzaktan"),
                "https://picsum.photos/seed/serkan-web-profile/400/400",
                "₺400–800/sa", 6.2
            ),
            new ProviderView(
                "provider-neslihan",
                "Neslihan Yıldırım",
                "Serbest Uzman",
                "İngilizce eğitmeni ve çevirmen",
                "IELTS/TOEFL hazırlık, business English ve genel konuşma pratiği. 8 yıllık eğitim deneyimi.",
                List.of("education", "consulting"),
                "Şişli + uzaktan",
                4.9, 91, "Genelde 30 dk içinde", false,
                List.of("ingilizce", "ielts", "çeviri"),
                "https://picsum.photos/seed/neslihan-english-profile/400/400",
                "₺350–500/sa", 4.5
            ),
            // ── Dynamic / user-created categories (not in the predefined tree) ──────
            new ProviderView(
                "provider-emir",
                "Emir Balcı",
                "Serbest Uzman",
                "Drone ile gayrimenkul ve etkinlik çekimi",
                "İnşaat takibi, gayrimenkul tanıtımı ve etkinlikler için lisanslı drone pilotu. 4K hava görüntüsü ve harita çıktısı.",
                List.of("drone-cekim", "creative"),
                "İstanbul geneli",
                4.8, 27, "Genelde 3 saat içinde", false,
                List.of("drone", "hava çekimi", "lisanslı pilot"),
                "https://picsum.photos/seed/emir-drone-profile/400/400",
                "₺1.200–3.000/çekim", 5.4
            ),
            new ProviderView(
                "provider-pelin",
                "Pelin Aksoy",
                "Serbest Uzman",
                "Müzik prodüksiyon ve şarkı aranjmanı",
                "Stüdyo kalitesinde aranjman, mix ve mastering. Jingle, podcast müziği ve sanatçı demoları için prodüksiyon.",
                List.of("muzik-produksiyon"),
                "Kadıköy stüdyo + uzaktan",
                4.9, 44, "Genelde 1 gün içinde", false,
                List.of("müzik", "prodüksiyon", "mix mastering"),
                "https://picsum.photos/seed/pelin-muzik-profile/400/400",
                "₺800–2.500/parça", 2.6
            ),
            new ProviderView(
                "provider-onur-tekne",
                "Onur Kaptan",
                "Serbest Uzman",
                "Tekne bakım, temizlik ve sezon hazırlığı",
                "Yat ve tekneler için polyester onarımı, dip boyası, motor bakımı ve detaylı temizlik. Marina içi yerinde hizmet.",
                List.of("tekne-bakim"),
                "Fenerbahçe & Kalamış Marina",
                4.7, 19, "Randevulu, genelde 2 gün önceden", false,
                List.of("tekne", "yat bakımı", "marina"),
                "https://picsum.photos/seed/onur-tekne-profile/400/400",
                "₺1.500–6.000/iş", 6.1
            ),
            new ProviderView(
                "provider-sevgi",
                "Sevgi Demirtaş",
                "Serbest Uzman",
                "Butik pasta ve özel gün tasarım kek",
                "Doğum günü, nişan ve kurumsal etkinlikler için özel tasarım pastalar. Şeker hamuru figür ve glütensiz seçenekler.",
                List.of("pasta-tasarim", "events"),
                "Bahçeşehir + çevre ilçeler",
                4.9, 61, "Genelde aynı gün", true,
                List.of("pasta", "tasarım kek", "özel gün"),
                "https://picsum.photos/seed/sevgi-pasta-profile/400/400",
                "₺400–1.800/sipariş", 3.3
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
                "Yeni açılan butik kafe; taze ekmek, kahve ve paket servis. Sabah 07:00'den akşam 22:00'ye açık. Etkinlikler için mekan kiralama mevcut.",
                List.of("events", "delivery"),
                "Yeldeğirmeni, Kadıköy",
                4.6, 86, "Genelde 20 dk içinde", "APPROVED",
                "https://picsum.photos/seed/kose-firin-business/800/450",
                "₺50–200/sipariş", 0.3
            ),
            new BusinessView(
                "business-temiz-ev",
                "Temiz Ev Atölyesi",
                "İşletme",
                "Temizlik",
                "Düzenli ev ve ofis temizliği için küçük ekipli, randevulu hizmet. Ekologik ürünler, güvenlik belgeli personel.",
                List.of("cleaning"),
                "Kadıköy ve Üsküdar",
                4.8, 203, "Genelde aynı gün", "APPROVED",
                "https://picsum.photos/seed/temiz-ev-business/800/450",
                "₺400–800/seans", 1.4
            ),
            new BusinessView(
                "business-tamir-noktasi",
                "Tamir Noktası",
                "İşletme",
                "Tamir & bakım",
                "Ev, ofis ve küçük işletmeler için planlı bakım ve acil tamir ekibi. Elektrik, tesisat ve genel bakım.",
                List.of("repair"),
                "İstanbul Anadolu yakası",
                4.5, 141, "Genelde 40 dk içinde", "APPROVED",
                "https://picsum.photos/seed/tamir-noktasi-business/800/450",
                "₺200–800/iş", 2.1
            ),
            new BusinessView(
                "business-studio-lens",
                "Studio Lens Fotoğrafçılık",
                "İşletme",
                "Fotoğraf & video",
                "Ürün çekimi, kurumsal portre, düğün ve etkinlik fotoğrafçılığı. Kendi stüdyosu ve dış mekan seti mevcut.",
                List.of("creative"),
                "Beşiktaş",
                4.9, 74, "Randevulu, genelde 2 gün önceden", "APPROVED",
                "https://picsum.photos/seed/studio-lens-business/800/450",
                "₺1.500–5.000/gün", 3.5
            ),
            new BusinessView(
                "business-nakliye-express",
                "Nakliye Express",
                "İşletme",
                "Nakliye & lojistik",
                "Şehiriçi ve şehirlerarası ev/ofis taşıma. Sigortalı hizmet, ambalaj dahil. Asansörlü araç seçeneği mevcut.",
                List.of("delivery"),
                "İstanbul + Türkiye geneli",
                4.4, 59, "Genelde 1 gün önceden randevu", "APPROVED",
                "https://picsum.photos/seed/nakliye-express-business/800/450",
                "₺600–2.500/taşıma", 4.2
            ),
            new BusinessView(
                "business-yesil-bahce",
                "Yeşil Bahçe Atölyesi",
                "İşletme",
                "Peyzaj & bahçe",
                "Bahçe tasarımı, peyzaj düzenlemesi ve mevsimlik bakım paketleri. Balkon ve teras için özel konsept.",
                List.of("repair", "cleaning"),
                "Kadıköy, Bostancı, Pendik",
                4.6, 48, "Genelde 2 gün içinde", "APPROVED",
                "https://picsum.photos/seed/yesil-bahce-business/800/450",
                "₺300–700/sa", 2.7
            ),
            new BusinessView(
                "business-randevu-wellness",
                "Randevu Wellness Center",
                "İşletme",
                "Sağlık & wellness",
                "Masaj terapisi, cilt bakımı ve aromaterapi. Bireysel ve çift paketleri. İstanbul'un en iyi değerlendirilen wellness merkezlerinden.",
                List.of("beauty"),
                "Nişantaşı",
                4.8, 165, "Genelde aynı gün", "APPROVED",
                "https://picsum.photos/seed/randevu-wellness-business/800/450",
                "₺500–1.500/seans", 1.6
            ),
            new BusinessView(
                "business-kodlama-atolyesi",
                "Kodlama Atölyesi",
                "İşletme",
                "Eğitim & teknoloji",
                "Çocuk ve yetişkinlere yazılım ve veri bilimi kursları. Canlı online ve yüz yüze grup dersleri. Sertifika programları.",
                List.of("education", "digital"),
                "Maslak + uzaktan",
                4.7, 92, "Haftalık programa göre", "APPROVED",
                "https://picsum.photos/seed/kodlama-atolyesi-business/800/450",
                "₺800–2.000/kurs", 3.8
            ),
            new BusinessView(
                "business-hukuk-ofisi",
                "Ak Hukuk Danışmanlık",
                "İşletme",
                "Hukuk bürosu",
                "Sözleşme hazırlama, iş hukuku, icra ve arabuluculuk hizmetleri. Uzaktan danışmanlık seçeneği mevcut.",
                List.of("consulting"),
                "Levent, İstanbul",
                4.7, 38, "Randevulu", "APPROVED",
                "https://picsum.photos/seed/hukuk-ofisi-business/800/450",
                "₺500–2.000/saat", 5.1
            ),
            new BusinessView(
                "business-dijital-ajans",
                "Pivot Dijital Ajans",
                "İşletme",
                "Dijital pazarlama",
                "SEO, sosyal medya yönetimi, içerik stratejisi ve dijital reklamcılık. Aylık paket veya proje bazlı çalışma.",
                List.of("digital", "creative", "consulting"),
                "Tüm Türkiye (uzaktan)",
                4.6, 54, "Genelde 1 iş günü içinde", "APPROVED",
                "https://picsum.photos/seed/pivot-dijital-business/800/450",
                "₺1.500–5.000/ay", 7.3
            )
        ));
    }
}
