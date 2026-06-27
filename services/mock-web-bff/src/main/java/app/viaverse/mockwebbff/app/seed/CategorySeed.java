package app.viaverse.mockwebbff.app.seed;

import app.viaverse.mockwebbff.app.AppDtos.ServiceCategoryView;
import java.util.ArrayList;
import java.util.List;

public final class CategorySeed {

    private CategorySeed() {
    }

    public static ArrayList<ServiceCategoryView> categories() {
        return new ArrayList<>(List.of(

            // ── Hizmet ─────────────────────────────────────────────────────────────
            new ServiceCategoryView("repair", "Ev, Tamirat & Tadilat", "Elektrik, tesisat, boyacı, mobilya ve tadilat işleri.", "Hizmet", "home_repair.png", "Serbest Uzman",
                List.of("Boya ve Badana", "Su Tesisatı ve Onarım", "Elektrik Tesisatı ve Arıza",
                        "Mobilya Montajı ve Tamiri", "Kombi ve Radyatör Bakımı", "Klima Montajı ve Servisi",
                        "Mutfak ve Banyo Tadilatı", "Parke ve Zemin Döşeme", "Fayans ve Seramik İşleri",
                        "Çilingir ve Kapı Kilidi Değişimi", "Kapı ve Pencere Tamiri", "Kartonpiyer ve Asma Tavan",
                        "Alçıpan ve Bölme Duvar", "Duvar Kağıdı Uygulama", "Dış Cephe Boya ve Yalıtım",
                        "Çatı Tamiri ve İzolasyon", "Panjur ve Sineklik Montajı", "Bahçe Düzenleme ve Peyzaj",
                        "Havuz Bakımı ve Onarımı", "Akıllı Ev Sistemleri Kurulumu", "Kamera ve Güvenlik Sistemleri",
                        "Mermer ve Doğal Taş İşleme", "Ahşap Doğrama ve Marangozluk", "Metal ve Demir Doğrama İşleri",
                        "Tıkanıklık Açma ve Pimaş Yıkama", "Cam Balkon ve Kış Bahçesi", "Stor Perde ve Korniş Montajı",
                        "Tv ve Uydu Sistemleri Kurulumu", "Beyaz Eşya Tamiri ve Kurulumu", "Ev ve Ofis İlaçlama",
                        "Kalorifer ve Doğalgaz Tesisatı", "Çelik Kapı Montajı ve Onarımı", "Merdiven ve Küpeşte Tasarımı",
                        "Şömine ve Barbekü Yapımı", "Asansör Bakım ve Onarım")),

            new ServiceCategoryView("cleaning", "Temizlik & Düzenleme", "Ev, ofis ve inşaat sonrası temizlik, eşya düzenleme.", "Hizmet", "cleaning.png", "Serbest Uzman",
                List.of("Ev Temizliği", "Ofis ve İş Yeri Temizliği", "İnşaat Sonrası Temizlik",
                        "Dış Cephe ve Cam Temizliği", "Koltuk ve Döşeme Yıkama", "Halı Yıkama",
                        "Perde ve Stor Temizliği", "İlaçlama ve Pest Kontrol", "Dezenfeksiyon Hizmetleri",
                        "Bahçe Temizliği ve Atık Toplama", "Havuz Temizliği", "Apartman ve Site Temizliği",
                        "Kuru Temizleme ve Ütüleme", "Giysi ve Gardırop Düzenleme", "Mutfak ve Kiler Düzenleme",
                        "Depo ve Garaj Düzenleme", "Ev Taşıma Öncesi ve Sonrası Temizlik", "Baca Temizliği",
                        "Su Deposu Temizliği", "Yat ve Tekne Temizliği", "Araç Detaylı Temizlik ve Oto Kuaför",
                        "Endüstriyel Alan Temizliği", "Okul ve Eğitim Alanları Temizliği", "Sağlık Kuruluşları Temizliği",
                        "Restoran ve Mutfak Detaylı Temizliği", "Evsel Atık ve Moloz Kaldırma", "Yangın ve Su Baskını Sonrası Temizlik",
                        "Eşya Paketleme ve İstifleme", "Mobilya ve Yüzey Cilalama", "Zemin Parlatma ve Kristalize Cila",
                        "Çamaşır Yıkama ve Katlama", "Minimalist Yaşam Danışmanlığı")),

            new ServiceCategoryView("beauty", "Kişisel Bakım & Sağlık", "Kuaför, cilt bakımı, masaj, fitness ve wellness hizmetleri.", "Hizmet", "advisory.png", "Serbest Uzman",
                List.of("Berber ve Erkek Kuaförü", "Kadın Kuaförü ve Saç Tasarımı", "Cilt Bakımı ve Estetik",
                        "Makyaj ve Kalıcı Makyaj", "Manikür, Pedikür ve Nail Art", "Masaj ve Spa Hizmetleri",
                        "Lazer Epilasyon ve Ağda", "Kişisel Spor Eğitmeni", "Yoga ve Pilates Eğitimi",
                        "Diyetisyen ve Beslenme Danışmanlığı", "Psikolog ve Terapi Hizmetleri", "Fizyoterapi ve Rehabilitasyon",
                        "Hasta ve Yaşlı Bakım Refakatçiliği", "Çocuk Bakımı ve Oyun Ablası", "Medikal Ayak Bakımı",
                        "İmaj ve Stil Danışmanlığı", "Yaşam Koçluğu ve Mental Sağlık", "Mindfulness ve Meditasyon Rehberliği",
                        "Doğum Koçluğu ve Hamilelik Eğitimi", "Uyku Koçluğu", "Nefes Terapisi ve Teknikleri",
                        "Aromaterapi ve Doğal Ürün Danışmanlığı", "Evde Serum ve Enjeksiyon Hizmetleri", "Protez Saç ve Saç Ekimi Danışmanlığı",
                        "Konuşma Terapisi", "Ergoterapi Hizmetleri", "Bağımlılık Danışmanlığı",
                        "Cinsel Terapi ve Aile Danışmanlığı", "Online Sağlık ve Wellness Danışmanlığı", "Check-up ve Laboratuvar Takibi")),

            new ServiceCategoryView("education", "Eğitim, Ders & Mentorluk", "Özel ders, sınav hazırlık, mentorluk ve yetenek atölyeleri.", "Hizmet", "education.png", "Serbest Uzman",
                List.of("Matematik ve Fen Bilimleri Özel Ders", "Yabancı Dil Eğitimi ve Konuşma Pratiği", "Yazılım ve Programlama Eğitimi",
                        "YKS, LGS ve KPSS Sınav Koçluğu", "TOEFL, IELTS ve Dil Sınavları", "Enstrüman ve Müzik Teorisi Dersleri",
                        "Güzel Sanatlar ve Çizim Teknikleri", "Kariyer Planlama ve Mentorluk", "Kişisel Gelişim ve Yaşam Koçluğu",
                        "İşletme Yönetimi ve Girişimcilik", "Finansal Okuryazarlık ve Borsa Eğitimi", "İleri Seviye Excel ve Veri Analizi",
                        "Dijital Pazarlama ve SEO Mentorluğu", "Hitabet, Diksiyon ve İletişim", "Grafik Tasarım ve Görsel Sanatlar",
                        "Dans, Pilates ve Yoga Eğitimi", "Satranç ve Akıl Oyunları", "Çocuklar İçin Robotik ve STEM",
                        "Akademik Danışmanlık ve Makale Desteği", "Fotoğrafçılık ve Video Kurgu Atölyesi", "Gastronomi ve Yemek Pişirme",
                        "El Sanatları, Örgü ve Hobi Atölyeleri", "Sporcu Beslenmesi ve Performans Koçluğu", "Mindfulness ve Meditasyon Rehberliği",
                        "Ebeveyn Danışmanlığı ve Çocuk Gelişimi", "Yaratıcı Yazarlık ve Editörlük", "Sunum Teknikleri ve Topluluk Önünde Konuşma",
                        "Yapay Zeka Okuryazarlığı ve Prompt Mühendisliği", "Oyun Tasarımı ve Unity/Unreal Mentorluğu", "Teknik Çizim ve 3D Modelleme",
                        "Portfolyo Geliştirme ve Sanat Okulu Hazırlık", "Blockchain ve Web3 Mentorluğu")),

            // ── Yakın Mesafe ───────────────────────────────────────────────────────
            new ServiceCategoryView("delivery", "Lojistik, Paket & Destek", "Kurye, nakliye, market alışverişi ve pratik yardım işleri.", "Yakın Mesafe", "logistics.png", "Serbest Uzman",
                List.of("Şehir İçi Kurye ve Paket Teslimatı", "Market ve Bakkal Alışverişi", "Evden Eve Nakliye ve Taşımacılık",
                        "Parça Eşya Taşıma", "Evrak, Dosya ve Pasaport Teslimatı", "Eczane Alışverişi ve İlaç Temini",
                        "Çiçek ve Hediye Gönderimi", "Mobilya ve Beyaz Eşya Nakliyesi", "Motorlu Kurye Hizmetleri",
                        "Bisikletli Kurye ve Çevreci Teslimat", "Kişisel Alışveriş Asistanlığı", "Kargo Teslim Alımı ve İade",
                        "Havaalanı Transfer ve Karşılama", "Sıra Bekleme Hizmetleri", "Ağır Eşya Taşıma ve Yükleme",
                        "Moloz ve Eski Eşya Atımı", "Su ve Tüp Siparişi Teslimatı", "Şehirler Arası Kargo Koordinasyonu",
                        "Hassas ve Kırılacak Eşya Taşımacılığı", "Soğuk Zincir ve Gıda Taşımacılığı", "Depolama ve Ardiye Hizmetleri",
                        "Günlük veya Saatlik Kurye Kiralama", "Acil Paket ve Ekspres Servis", "Etkinlik ve Fuar Ekipmanları Taşıma",
                        "Valiz ve Bagaj Taşıma Hizmeti", "Kurumsal Dağıtım ve Saha Operasyonları", "Araç Çekici ve Yol Yardım",
                        "Gece Teslimat ve Nöbetçi Kurye", "Hafif Ticari Araçla Nakliye", "Tedarik Zinciri ve Stok Yönetim Desteği")),

            new ServiceCategoryView("pets", "Evcil Hayvan Hizmetleri", "Köpek gezdirme, bakım, veteriner ve eğitim desteği.", "Yakın Mesafe", "pets.png", "Serbest Uzman",
                List.of("Köpek Gezdirme", "Evcil Hayvan Bakıcılığı ve Konaklama", "Evcil Hayvan Kuaförü ve Bakımı",
                        "Veterinerlik ve Sağlık Danışmanlığı", "Temel ve İleri Köpek Eğitimi", "Evcil Hayvan Taşıma ve Pet Taksi",
                        "Akvaryum Kurulumu ve Bakımı", "Evcil Hayvan Fotoğrafçılığı", "Beslenme ve Diyet Danışmanlığı",
                        "Davranış Bozuklukları ve Rehabilitasyon", "Kedi Bakımı ve Oyun Arkadaşlığı", "Kuş Bakımı ve Kafes Temizliği",
                        "Sürüngen ve Egzotik Hayvan Bakımı", "Evcil Hayvan Atık Temizliği", "Mikroçip Kaydı ve Künye",
                        "Kayıp Evcil Hayvan Arama Desteği", "Evcil Hayvan İlk Yardım Eğitimi", "Ameliyat Sonrası Evde Bakım",
                        "Yaşlı Evcil Hayvan Bakımı", "Evcil Hayvan Oteli ve Pansiyonu", "Evde Banyo ve Tırnak Kesimi",
                        "Sahiplendirme Danışmanlığı", "Sosyalleşme ve Oyun Grupları", "Akvaryum ve Teraryum Tasarımı",
                        "Evcil Hayvan Masajı ve Wellness", "Terapi Hayvanı Eğitimi", "Organik Mama Hazırlama",
                        "Tuvalet Eğitimi Danışmanlığı", "Evcil Hayvan Ürünleri ve İlaç Temini", "Irk Seçimi ve Hazırlık Danışmanlığı")),

            new ServiceCategoryView("events", "Etkinlik & Organizasyon", "Düğün, doğum günü, nişan ve kurumsal etkinlik planlama.", "Yakın Mesafe", "cleaning.png", "İşletme",
                List.of("Düğün, Nişan ve Davet Organizasyonu", "Doğum Günü ve Parti Planlama", "Kurumsal Etkinlik ve Toplantı",
                        "Ses, Işık ve Görüntü Sistemleri", "Catering ve Yemek Hizmetleri", "DJ ve Canlı Müzik Performansı",
                        "Sahne Kurulumu ve Dekor Tasarımı", "Davetiye Tasarımı ve Matbaa", "Etkinlik Fotoğrafçılığı ve Video",
                        "Host, Hostes ve Karşılama Ekibi", "Mekan Süsleme ve Çiçek Tasarımı", "Havai Fişek ve Lazer Gösterileri",
                        "Palyaço ve Çocuk Eğlence Hizmetleri", "Fuar Stand Tasarımı ve Kurulumu", "Konser ve Festival Organizasyonu",
                        "Seminer, Konferans ve Workshop", "Kına Gecesi ve Bekarlığa Veda", "Mezuniyet Töreni ve Balo",
                        "Masa ve Sandalye Kiralama", "Çadır ve Portatif Yapı Kurulumu", "Sanatçı Menajerliği ve Booking",
                        "Promosyon Ürünleri ve Etkinlik Kitleri", "Kokteyl ve Bar Servisi", "Dijital Etkinlik ve Webinar Desteği",
                        "Güvenlik ve Giriş Kontrol", "Kostüm ve Ekipman Kiralama", "Tur ve Gezi Organizasyonu",
                        "VIP Transfer ve Protokol Hizmetleri", "Ödül Töreni ve Plaket Hazırlığı", "Etkinlik Danışmanlığı ve Bütçe Planlama")),

            // ── Profesyonel ────────────────────────────────────────────────────────
            new ServiceCategoryView("digital", "Dijital & Yazılım", "Web, mobil, yapay zeka, güvenlik ve teknik altyapı hizmetleri.", "Profesyonel", "home_repair.png", "Serbest Uzman",
                List.of("Web Tasarım ve Geliştirme", "Mobil Uygulama Geliştirme", "E-ticaret Sistemleri Kurulumu",
                        "Masaüstü Yazılım Geliştirme", "Oyun Geliştirme", "Veri Bilimi ve Analitiği",
                        "Yapay Zeka ve Makine Öğrenmesi", "Siber Güvenlik ve Sızma Testi", "Bulut Bilişim ve Sunucu Yönetimi",
                        "Veritabanı Yönetimi ve Tasarımı", "API Geliştirme ve Entegrasyon", "Blokzincir ve Akıllı Kontrat",
                        "Gömülü Sistemler ve IoT Yazılımları", "QA ve Test Otomasyonu", "UI/UX Tasarımı ve Prototipleme",
                        "SEO ve Teknik Web Analizi", "DevSecOps ve Sistem Otomasyonu", "CRM ve ERP Danışmanlığı",
                        "Veri Kurtarma ve Yedekleme", "Yazılım Mimari Danışmanlığı", "No-Code ve Low-Code Geliştirme",
                        "Büyük Veri İşleme ve Yönetimi", "Doğal Dil İşleme Çözümleri", "Bilgisayarlı Görü Sistemleri",
                        "Robotik Süreç Otomasyonu", "Teknik Yazarlık ve Dokümantasyon", "Mobil Oyun Optimizasyonu",
                        "SaaS Ürün Geliştirme", "Mikroservis Mimarisi Dönüşümü", "Donanım Bakımı ve Teknik Destek",
                        "Veri Görselleştirme ve Dashboard Tasarımı")),

            new ServiceCategoryView("creative", "Yaratıcı İşler & Medya", "Logo, grafik, video, fotoğraf, içerik ve marka hizmetleri.", "Profesyonel", "professional_consulting.png", "Serbest Uzman",
                List.of("Logo ve Kurumsal Kimlik Tasarımı", "Sosyal Medya İçerik Tasarımı", "Video Kurgu ve Post Prodüksiyon",
                        "2D ve 3D Animasyon Tasarımı", "Hareketli Grafikler ve Motion Design", "Dijital İllüstrasyon ve Çizim",
                        "Ürün ve Ticari Fotoğrafçılık", "Etkinlik ve Konser Fotoğrafçılığı", "Metin Yazarlığı ve Blog İçeriği",
                        "Sosyal Medya Yönetimi ve Danışmanlığı", "Seslendirme, Dublaj ve Ses Tasarımı", "Ambalaj ve Etiket Tasarımı",
                        "Katalog, Dergi ve Kitap Mizanpajı", "Afiş, Broşür ve İlan Tasarımı", "NFT ve Kripto Sanat",
                        "YouTube Kanal Danışmanlığı ve Kurgusu", "Drone Çekimi ve Hava Fotoğrafçılığı", "Marka Danışmanlığı ve Konumlandırma",
                        "Podcast Prodüksiyonu ve Düzenleme", "Karakter Tasarımı ve Modelleme", "Moda ve Reklam Fotoğrafçılığı",
                        "Kurumsal Tanıtım Filmi Hazırlama", "Storyboard ve Senaryo Yazımı", "Renk Düzenleme ve Color Grading",
                        "Görsel Efekt ve VFX Uygulamaları", "Marka İsimlendirme ve Slogan", "Sunum ve Deck Tasarımı",
                        "Mimari Görselleştirme ve Render", "Dijital Pazarlama Görselleri ve Banner", "Tipografi ve Font Tasarımı",
                        "Müzik Prodüksiyonu ve Jingle Yapımı")),

            new ServiceCategoryView("consulting", "Profesyonel & Danışmanlık", "Hukuk, muhasebe, emlak, mimari ve stratejik iş danışmanlığı.", "Profesyonel", "advisory.png", "İşletme",
                List.of("Avukatlık ve Hukuki Danışmanlık", "Muhasebe ve Mali Müşavirlik", "Çeviri ve Tercümanlık",
                        "Emlak ve Gayrimenkul Danışmanlığı", "Mimari Proje ve Tasarım", "İç Mimari Danışmanlığı",
                        "İnsan Kaynakları ve İşe Alım", "İş Stratejisi ve Yönetim Danışmanlığı", "Pazarlama ve Satış Stratejisi",
                        "Finansal Planlama ve Yatırım", "Hibe ve Teşvik Danışmanlığı", "Marka ve Patent Vekilliği",
                        "Sigorta Danışmanlığı", "İş Sağlığı ve Güvenliği Danışmanlığı", "Çevre Danışmanlığı ve Atık Yönetimi",
                        "BT ve Teknoloji Danışmanlığı", "Halkla İlişkiler ve İtibar Yönetimi", "Etkinlik ve Organizasyon Danışmanlığı",
                        "Dış Ticaret ve Gümrük Danışmanlığı", "Proje Yönetimi ve Planlama", "İstatistiksel Veri Analizi ve Raporlama",
                        "Teknik Yazarlık ve Şartname Hazırlama", "Enerji Verimliliği Danışmanlığı", "Kalite Yönetimi ve ISO Danışmanlığı",
                        "Tedarik Zinciri ve Satın Alma", "Hukuki ve Yeminli Tercüme", "CFO ve Dış Kaynaklı Finans",
                        "Yurt Dışı Eğitim Danışmanlığı", "Kurumsal Sosyal Medya Stratejisi", "Sürdürülebilirlik ve ESG Danışmanlığı",
                        "Sanal Asistanlık ve İdari Destek"))
        ));
    }
}
