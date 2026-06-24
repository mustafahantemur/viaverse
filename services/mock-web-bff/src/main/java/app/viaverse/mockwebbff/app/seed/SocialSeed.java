package app.viaverse.mockwebbff.app.seed;

import app.viaverse.mockwebbff.app.AppDtos.FeedItemView;
import app.viaverse.mockwebbff.app.AppDtos.AnnouncementIncidentView;
import app.viaverse.mockwebbff.app.AppDtos.IncidentUpdateView;
import app.viaverse.mockwebbff.app.AppDtos.MockPhotoView;
import app.viaverse.mockwebbff.app.AppDtos.PostCommentView;
import app.viaverse.mockwebbff.app.AppDtos.SponsoredAdView;
import java.util.ArrayList;
import java.util.List;

public final class SocialSeed {

    private SocialSeed() {
    }

    public static ArrayList<FeedItemView> feedItems() {
        return new ArrayList<>(List.of(
            feedItem(
                "feed-traffic-evening",
                "TRAFFIC",
                "Trafik",
                "Sahil yolunda akşam yoğunluğu",
                "Moda sahil çıkışında 18:30 sonrası araç akışı yavaşladı. Alternatif olarak Boğa yönü daha rahat görünüyor. #kadikoy #trafik #ulasim",
                "Ulaşım Gözlem",
                "Bilgi paylaşımı",
                "Kadıköy · 3 km",
                "local-info",
                SeedClock.minutesAgo(14),
                34,
                false,
                6,
                5,
                false,
                "Ulaşım",
                null,
                List.of("kadikoy", "trafik", "ulasim"),
                pexels(17674165),
                "IMAGE"
            ),
            feedItem(
                "feed-power-moda",
                "UTILITY",
                "Kesinti",
                "Moda’da planlı elektrik çalışması",
                "Caferağa çevresinde 13:00-15:00 arası kısa süreli kesinti bildirimi var. Asansör ve modemleri kontrol etmek iyi olabilir. #moda #elektrikkesintisi",
                "Enerji Bilgilendirme",
                "Duyuru",
                "Moda · 1 km",
                "local-info",
                SeedClock.minutesAgo(38),
                28,
                false,
                4,
                9,
                false,
                "Faydalı bilgi",
                null,
                List.of("moda", "elektrikkesintisi"),
                pexels(15908181),
                "IMAGE"
            ),
            feedItem(
                "feed-market-event",
                "EVENT",
                "Etkinlik",
                "Hafta sonu üretici pazarı kuruluyor",
                "Cumartesi 10:00-16:00 arası küçük üreticiler, kahve standları ve çocuk atölyeleri meydanda olacak. #etkinlik #uretipazari #kadikoy",
                "Kültür Merkezi",
                "Etkinlik",
                "Kadıköy · 2 km",
                "local-info",
                SeedClock.minutesAgo(66),
                64,
                true,
                11,
                18,
                true,
                "Etkinlik",
                null,
                List.of("etkinlik", "uretipazari", "kadikoy"),
                sampleVideo(),
                "VIDEO"
            ),
            feedItem(
                "feed-cafe-opening",
                "ANNOUNCEMENT",
                "Duyuru",
                "Köşe Fırın Kafe ilk haftasını duyurdu",
                "Sabah 08:00’den itibaren açık olacaklarını ve hafta içi kahve yanında küçük ikram verdiklerini paylaştılar. #yeldegirmeni #kafe #duyuru",
                "Köşe Fırın Kafe",
                "İşletme",
                "Yeldeğirmeni · 2 km",
                "local-info",
                SeedClock.minutesAgo(124),
                57,
                false,
                13,
                16,
                false,
                "İşletme duyurusu",
                null,
                List.of("yeldegirmeni", "kafe", "duyuru"),
                pexels(16979212),
                "IMAGE"
            ),
            feedItem(
                "feed-keys",
                "INFO",
                "Bilgi",
                "Apartman girişinde kayıp anahtar bulundu",
                "Siyah anahtarlık üzerinde küçük metal etiket var. Girişteki panoya bırakıldı, görenler panoya bakabilir. #rasimpasa #kayip #bilgi",
                "Ayşe T.",
                "Hizmet alan",
                "Rasimpaşa · 850 m",
                "local-info",
                SeedClock.minutesAgo(182),
                18,
                false,
                2,
                4,
                false,
                "Faydalı bilgi",
                null,
                List.of("rasimpasa", "kayip", "bilgi"),
                pexels(16979212),
                "IMAGE"
            ),
            feedItem(
                "feed-open-air-cinema",
                "EVENT",
                "Etkinlik",
                "Açık hava film gösterimi cumartesi akşamı",
                "Yoğurtçu Parkı tarafında 21:00'de ücretsiz kısa film seçkisi gösterilecek. Matını getirenler erken yerleşebilir. #kadikoy #etkinlik #sinemagecesi",
                "Kadıköy Kültür Ekibi",
                "Etkinlik",
                "Yoğurtçu Parkı · 2 km",
                "local-info",
                SeedClock.minutesAgo(230),
                42,
                false,
                8,
                12,
                false,
                "Etkinlik",
                null,
                List.of("kadikoy", "etkinlik", "sinemagecesi"),
                pexels(34215002),
                "IMAGE"
            ),
            feedItem(
                "feed-road-maintenance",
                "ANNOUNCEMENT",
                "Duyuru",
                "Rıhtım tarafında gece yol bakım çalışması var",
                "23:30 sonrası şerit daraltması uygulanacak. Gece araçla geçecek olanlar alternatif güzergahı kontrol edebilir. #rihtim #duyuru #ulasim",
                "İBB Yol Bilgilendirme",
                "Duyuru",
                "Rıhtım · 2 km",
                "local-info",
                SeedClock.minutesAgo(312),
                31,
                false,
                5,
                7,
                false,
                "Ulaşım duyurusu",
                null,
                List.of("rihtim", "duyuru", "ulasim"),
                pexels(17674165),
                "IMAGE"
            ),
            feedItem(
                "feed-ferry-delay",
                "INFO",
                "Bilgi",
                "Akşam vapurlarında kısa gecikme var",
                "18:00 sonrası iskelede yoğunluk arttı, ek sefer anonsu bekleniyor. Yürüyerek geçecekler için üst geçit daha hızlı. #vapur #ulasim #kadikoy",
                "İskele Gözlem",
                "Bilgi paylaşımı",
                "Kadıköy İskele · 1 km",
                "local-info",
                SeedClock.minutesAgo(378),
                26,
                false,
                3,
                5,
                false,
                "Ulaşım",
                null,
                List.of("vapur", "ulasim", "kadikoy"),
                pexels(15908181),
                "IMAGE"
            ),
            feedItem(
                "feed-book-swap",
                "POST",
                "Paylaşım",
                "Bu akşam küçük kitap takası yapıyoruz",
                "Kahve eşliğinde 19:30'da kısa bir buluşma olacak. Elinde roman ya da çizgi roman getirmek isteyenler yazabilir. #moda #kitap #aksambulusmasi",
                "Selin K.",
                "Hizmet alan",
                "Moda · 1 km",
                "local-info",
                SeedClock.minutesAgo(452),
                47,
                true,
                9,
                14,
                false,
                "Genel paylaşım",
                null,
                List.of("moda", "kitap", "aksambulusmasi"),
                pexels(16979212),
                "IMAGE"
            ),
            feedItem(
                "feed-bazaar-announcement",
                "ANNOUNCEMENT",
                "Duyuru",
                "Pazar kurulum saati erkene çekildi",
                "Sabah yoğunluğu azaltmak için tezgah kurulumları 07:00 yerine 06:00'da başlayacak. Çevrede park edecekler bunu dikkate alabilir. #pazar #duyuru #ulasim",
                "Pazar Düzenleme Ekibi",
                "Duyuru",
                "Hasanpaşa · 3 km",
                "local-info",
                SeedClock.minutesAgo(520),
                21,
                false,
                2,
                3,
                false,
                "Yerel duyuru",
                null,
                List.of("pazar", "duyuru", "ulasim"),
                pexels(34215002),
                "IMAGE"
            ),
            feedItem(
                "feed-rain-alert",
                "UTILITY",
                "Kesinti",
                "Akşam yağışında internet dalgalanması olabilir",
                "Kısa süreli yağış geçişi bekleniyor. Modem ve prizleri korumaya almak isteyenler için küçük hatırlatma. #yagis #internet #moda",
                "Semt Bilgilendirme",
                "Duyuru",
                "Caferağa · 1 km",
                "local-info",
                SeedClock.minutesAgo(602),
                19,
                false,
                2,
                4,
                false,
                "Altyapı",
                null,
                List.of("yagis", "internet", "moda"),
                sampleVideo(),
                "VIDEO"
            )
        ));
    }

    public static List<AnnouncementIncidentView> announcementIncidents() {
        return List.of(
            new AnnouncementIncidentView(
                "incident-traffic-sahil",
                "TRAFFIC",
                "Sahil yolunda yoğunluk",
                "Moda sahil çıkışındaki akşam yoğunluğu için birden fazla güncelleme var.",
                "Kadıköy Sahil · 3 km",
                40.9872,
                29.0269,
                SeedClock.minutesAgo(14),
                List.of("feed-traffic-evening", "feed-road-maintenance", "feed-ferry-delay"),
                List.of(
                    new IncidentUpdateView("incident-traffic-update-1", "Ulaşım Gözlem", "Ana akış yavaş, sahil dönüşü bekleme artıyor.", SeedClock.minutesAgo(14), pexels(17674165), "IMAGE"),
                    new IncidentUpdateView("incident-traffic-update-2", "Deniz Arslan", "Otobüs hattı da aynı noktada ağır ilerliyor.", SeedClock.minutesAgo(9), null, null),
                    new IncidentUpdateView("incident-traffic-update-3", "İskele Gözlem", "Vapur tarafında ek sefer anonsu bekleniyor.", SeedClock.minutesAgo(6), sampleVideo(), "VIDEO")
                )
            ),
            new AnnouncementIncidentView(
                "incident-power-moda",
                "POWER_OUTAGE",
                "Moda elektrik çalışması",
                "Caferağa çevresinde planlı elektrik çalışması ve kısa kesintiler bildirildi.",
                "Moda / Caferağa · 1 km",
                40.9845,
                29.0245,
                SeedClock.minutesAgo(38),
                List.of("feed-power-moda", "feed-rain-alert"),
                List.of(
                    new IncidentUpdateView("incident-power-update-1", "Enerji Bilgilendirme", "13:00-15:00 arası kısa süreli kesinti bekleniyor.", SeedClock.minutesAgo(38), pexels(15908181), "IMAGE"),
                    new IncidentUpdateView("incident-power-update-2", "Ece Kaya", "Bizim sokakta 5 dakikalık kesinti oldu, modemleri kapattık.", SeedClock.minutesAgo(22), null, null)
                )
            ),
            new AnnouncementIncidentView(
                "incident-lost-keys",
                "LOST",
                "Kayıp anahtar bildirimi",
                "Rasimpaşa tarafında bulunan anahtar için güncellemeler aynı başlıkta toplandı.",
                "Rasimpaşa · 850 m",
                40.9971,
                29.0283,
                SeedClock.minutesAgo(182),
                List.of("feed-keys"),
                List.of(
                    new IncidentUpdateView("incident-lost-update-1", "Ayşe T.", "Anahtar girişteki panoda duruyor.", SeedClock.minutesAgo(182), pexels(16979212), "IMAGE")
                )
            ),
            new AnnouncementIncidentView(
                "incident-road-maintenance",
                "MUNICIPALITY",
                "Gece yol bakım çalışması",
                "Rıhtım tarafındaki şerit daraltma ve pazar kurulumu bilgileri tek yerel çalışma başlığında izleniyor.",
                "Rıhtım / Hasanpaşa · 2 km",
                40.9927,
                29.0221,
                SeedClock.minutesAgo(312),
                List.of("feed-road-maintenance", "feed-bazaar-announcement"),
                List.of(
                    new IncidentUpdateView("incident-road-update-1", "İBB Yol Bilgilendirme", "23:30 sonrası şerit daraltması uygulanacak.", SeedClock.minutesAgo(312), pexels(17674165), "IMAGE"),
                    new IncidentUpdateView("incident-road-update-2", "Pazar Düzenleme Ekibi", "Sabah kurulum saati erkene çekildi.", SeedClock.minutesAgo(120), null, null)
                )
            )
        );
    }

    public static ArrayList<PostCommentView> comments() {
        return new ArrayList<>(List.of(
            new PostCommentView("comment-traffic-1", "feed-traffic-evening", "user-standard", "Deniz Arslan", "Otobüs de aynı yerde yavaşladı, vapur daha mantıklı.", SeedClock.minutesAgo(9)),
            new PostCommentView("comment-market-1", "feed-market-event", "user-standard", "Deniz Arslan", "Saat aralığı çok iyi olmuş.", SeedClock.minutesAgo(40)),
            new PostCommentView("comment-power-1", "feed-power-moda", "user-provider", "Ece Kaya", "Bizim sokakta da kısa kesinti oldu, modemleri kapattık.", SeedClock.minutesAgo(22)),
            new PostCommentView("comment-cinema-1", "feed-open-air-cinema", "user-standard", "Deniz Arslan", "Battaniye getirmek mantıklı olur.", SeedClock.minutesAgo(180)),
            new PostCommentView("comment-book-1", "feed-book-swap", "user-provider", "Ece Kaya", "Bende iki çizgi roman var, getirebilirim.", SeedClock.minutesAgo(320))
        ));
    }

    public static List<MockPhotoView> mockPhotos() {
        return List.of(
            new MockPhotoView("photo-traffic", pexels(17674165), "İstanbul trafiği", "Pexels", "https://www.pexels.com/photo/a-traffic-jam-on-the-city-street-17674165/", List.of("trafik", "ulasim", "sehir")),
            new MockPhotoView("photo-cafe", pexels(16979212), "İstanbul sokak kafesi", "Pexels", "https://www.pexels.com/photo/people-sitting-outside-a-cafe-on-a-street-16979212/", List.of("kafe", "duyuru", "sokak")),
            new MockPhotoView("photo-market", pexels(34215002), "İstanbul pazar kahve standı", "Pexels", "https://www.pexels.com/photo/bustling-coffee-stand-in-istanbul-s-market-34215002/", List.of("etkinlik", "pazar", "kahve")),
            new MockPhotoView("photo-street", pexels(15908181), "İstanbul sokak akışı", "Pexels", "https://www.pexels.com/photo/rush-hour-on-istanbul-street-15908181/", List.of("ulasim", "bilgi", "sokak"))
        );
    }

    public static List<SponsoredAdView> sponsoredAds() {
        return List.of(
            new SponsoredAdView(
                "ad-coffee",
                "Yakındaki kahve aboneliği",
                "Hafta içi sabah paketlerinde ilk siparişe özel indirim.",
                "Google Ads · Köşe Fırın",
                pexels(16979212),
                "kosefirin.example",
                "Konum ve ilgi alanı tahmini"
            ),
            new SponsoredAdView(
                "ad-internet",
                "Ev interneti hız testi",
                "Bölgenizdeki altyapı seçeneklerini karşılaştırın.",
                "Google Ads · NetKarşılaştır",
                pexels(15908181),
                "netkarsilastir.example",
                "Tarayıcı çerezlerine göre örnek reklam"
            )
        );
    }

    private static String pexels(long photoId) {
        return "https://images.pexels.com/photos/" + photoId + "/pexels-photo-" + photoId + ".jpeg?auto=compress&cs=tinysrgb&w=1200";
    }

    private static String sampleVideo() {
        return "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4";
    }

    private static FeedItemView feedItem(
        String id,
        String type,
        String typeLabel,
        String title,
        String body,
        String authorName,
        String authorType,
        String locationLabel,
        String categoryId,
        String createdAt,
        int likeCount,
        boolean liked,
        int commentCount,
        int shareCount,
        boolean saved,
        String highlight,
        String relatedRequestId,
        List<String> hashtags,
        String mediaUrl,
        String mediaType
    ) {
        return new FeedItemView(
            id,
            type,
            typeLabel,
            title,
            body,
            authorName,
            authorType,
            locationLabel,
            categoryId,
            createdAt,
            likeCount,
            liked,
            commentCount,
            shareCount,
            saved,
            highlight,
            relatedRequestId,
            hashtags,
            mediaUrl,
            mediaType
        );
    }
}
