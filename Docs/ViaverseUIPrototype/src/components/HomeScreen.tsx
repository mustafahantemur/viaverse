import { motion, AnimatePresence } from "motion/react";
import { useState, useEffect } from "react";
import { Compass, Briefcase, Plus, MessageCircle, User as UserIcon, ChevronRight, MonitorSmartphone, Code, Wrench, Home, GraduationCap, Heart, Dumbbell, SlidersHorizontal, LifeBuoy, Megaphone, ClipboardList, PenTool, Sun, Moon, Sparkles, Truck, PawPrint, CalendarDays, Palette, Star, MapPin, MessageSquare, ExternalLink, Tag, X, Bell, Info, LayoutGrid, MessageCircleQuestion } from "lucide-react";
import CreatePostScreen, { POST_TYPES } from "./CreatePostScreen";
import BottomNavigation from "./BottomNavigation";

// Image imports - using placeholders since local files are missing
import ulakImg from "../icons/ulak.png";
import ustaImg from "../icons/Usta.png";
import megaphoneImg from "../icons/megaphone.png";
import danismaIcon from "../icons/danısma1.png";
import duyuruIcon from "../icons/duyuru1.png";
import isIcon from "../icons/is1.png";
import yardimIcon from "../icons/yardım1.png";
import tamiratImg from "../icons/tamirat1.png";
import yazilimImg from "../icons/yazilim1.png";
import yaraticiImg from "../icons/yaratici1.png";
import dersImg from "../icons/ders1.png";
import temizlikImg from "../icons/temizlik1.png";
import lojistikImg from "../icons/lojistik1.png";
import bakimImg from "../icons/bakım1.png";
import danismanlikImg from "../icons/Danışmanlık1.png";
import hayvanImg from "../icons/hayvan1.png";
import etkinlikImg from "../icons/etkinlik1.png";

type Mode = "ulak" | "uzman";

export const mainCategories = [
  { 
    icon: Wrench, 
    image: tamiratImg,
    color: "text-amber-500 dark:text-amber-400",
    bg: "bg-amber-500/10 dark:bg-amber-500/20",
    name: "Ev, Tamirat & Tadilat", 
    providers: "45 Hizmet Sağlayıcı",
    subCats: [
      "Boya ve Badana", "Su Tesisatı ve Onarım", "Elektrik Tesisatı ve Arıza",
      "Mobilya Montajı ve Tamiri", "Kombi ve Radyatör Bakımı", "Klima Montajı ve Servisi",
      "Mutfak ve Banyo Tadilatı", "Parke ve Zemin Döşeme", "Fayans ve Seramik İşleri",
      "Çilingir ve Kapı Kilidi Değişimi", "Kapı ve Pencere Tamiri", "Kartonpiyer ve Asma Tavan",
      "Alçıpan ve Bölme Duvar", "Duvar Kağıdı Uygulama", "Dış Cephe Boya ve Yalıtım",
      "Çatı Tamiri ve İzolasyon", "Panjur ve Sineklik Montajı", "Bahçe Düzenleme ve Peyzaj",
      "Havuz Bakımı ve Onarımı", "Akıllı Ev Sistemleri Kurulumu", "Kamera ve Güvenlik Sistemleri",
      "Mermer ve Doğal Taş İşleme", "Ahşap Doğrama ve Marangozluk", "Metal ve Demir Doğrama İşleri",
      "Tıkanıklık Açma ve Pimaş Yıkama", "Cam Balkon ve Kış Bahçesi Sistemleri", "Stor Perde ve Korniş Montajı",
      "Tv ve Uydu Sistemleri Kurulumu", "Beyaz Eşya Tamiri ve Kurulumu", "Ev ve Ofis İlaçlama",
      "Kalorifer ve Doğalgaz Tesisatı", "Çelik Kapı Montajı ve Onarımı", "Merdiven ve Küpeşte Tasarımı",
      "Şömine ve Barbekü Yapımı", "Asansör Bakım ve Onarım"
    ]
  },
  { 
    icon: Code, 
    image: yazilimImg,
    color: "text-blue-500 dark:text-blue-400",
    bg: "bg-blue-500/10 dark:bg-blue-500/20",
    name: "Dijital & Yazılım Hizmetleri", 
    providers: "90 Hizmet Sağlayıcı",
    subCats: [
      "Web Tasarım ve Geliştirme", "Mobil Uygulama Geliştirme", "E-ticaret Sistemleri Kurulumu ve Yönetimi",
      "Masaüstü Yazılım Geliştirme", "Oyun Geliştirme", "Veri Bilimi ve Analitiği",
      "Yapay Zeka ve Makine Öğrenmesi Çözümleri", "Siber Güvenlik ve Sızma Testi", "Bulut Bilişim ve Sunucu Yönetimi",
      "Veritabanı Yönetimi ve Tasarımı", "API Geliştirme ve Entegrasyon", "Blokzincir ve Akıllı Kontrat Geliştirme",
      "Gömülü Sistemler ve IoT Yazılımları", "QA ve Yazılım Test Otomasyonu", "UI/UX Tasarımı ve Prototipleme",
      "SEO ve Teknik Web Analizi", "DevSecOps ve Sistem Otomasyonu", "CRM ve ERP Sistemleri Danışmanlığı",
      "Veri Kurtarma ve Yedekleme Çözümleri", "Yazılım Mimari Danışmanlığı", "No-Code ve Low-Code Uygulama Geliştirme",
      "Büyük Veri İşleme ve Yönetimi", "Doğal Dil İşleme Çözümleri", "Bilgisayarlı Görü Sistemleri",
      "Robotik Süreç Otomasyonu", "Yazılım Dokümantasyonu ve Teknik Yazarlık", "Dijital Erişilebilirlik Danışmanlığı",
      "E-posta Sunucusu ve Altyapı Yönetimi", "Mobil Oyun Optimizasyonu", "SaaS Ürün Geliştirme ve Yönetimi",
      "Mikroservis Mimarisi Dönüşümü", "Yazılım Modernizasyonu ve Legacy Kod Güncelleme", "Açık Kaynak Kodlu Yazılım Özelleştirme",
      "Sanal Gerçeklik ve Artırılmış Gerçeklik Yazılımları", "Veri Görselleştirme ve Dashboard Tasarımı"
    ]
  },
  { 
    icon: Palette, 
    image: yaraticiImg,
    color: "text-purple-500 dark:text-purple-400",
    bg: "bg-purple-500/10 dark:bg-purple-500/20",
    name: "Yaratıcı İşler & Medya", 
    providers: "65 Hizmet Sağlayıcı",
    subCats: [
      "Logo ve Kurumsal Kimlik Tasarımı", "Sosyal Medya İçerik Tasarımı", "Video Kurgu ve Post Prodüksiyon",
      "2D ve 3D Animasyon Tasarımı", "Hareketli Grafikler ve Motion Design", "Dijital İllüstrasyon ve Çizim",
      "Ürün ve Ticari Fotoğrafçılık", "Etkinlik ve Konser Fotoğrafçılığı", "Metin Yazarlığı ve Blog İçerik Üretimi",
      "Sosyal Medya Yönetimi ve Danışmanlığı", "Seslendirme, Dublaj ve Ses Tasarımı", "Ambalaj ve Etiket Tasarımı",
      "Katalog, Dergi ve Kitap Mizanpajı", "Afiş, Broşür ve İlan Tasarımı", "NFT ve Kripto Sanat Üretimi",
      "YouTube Kanal Danışmanlığı ve Kurgusu", "Drone Çekimi ve Hava Fotoğrafçılığı", "Marka Danışmanlığı ve Konumlandırma",
      "Podcast Prodüksiyonu ve Düzenleme", "Karakter Tasarımı ve Modelleme", "Kullanıcı Deneyimi ve Arayüz Tasarımı Görselleştirme",
      "Moda ve Reklam Fotoğrafçılığı", "Kurumsal Tanıtım Filmi Hazırlama", "Storyboard ve Senaryo Yazımı",
      "Renk Düzenleme ve Color Grading", "Görsel Efekt ve VFX Uygulamaları", "Web Sitesi Görsel Arayüz Tasarımı",
      "Marka İsimlendirme ve Slogan Çalışması", "Sunum ve Deck Tasarımı", "Mimari Görselleştirme ve Render",
      "İç Mekan ve Dekorasyon Fotoğrafçılığı", "Dijital Pazarlama Görselleri ve Banner Tasarımı", "Tipografi ve Yazı Fontu Tasarımı",
      "Müzik Prodüksiyonu ve Jingle Yapımı", "Sanal Gerçeklik ve Artırılmış Gerçeklik İçerik Tasarımı"
    ]
  },
  { 
    icon: GraduationCap, 
    image: dersImg,
    color: "text-indigo-500 dark:text-indigo-400",
    bg: "bg-indigo-500/10 dark:bg-indigo-500/20",
    name: "Eğitim, Ders & Mentorluk", 
    providers: "31 Hizmet Sağlayıcı",
    subCats: [
      "Matematik ve Fen Bilimleri Özel Ders", "Yabancı Dil Eğitimi ve Konuşma Pratiği", "Yazılım ve Programlama Eğitimi",
      "YKS, LGS ve KPSS Sınav Koçluğu", "TOEFL, IELTS ve Dil Sınavlarına Hazırlık", "Enstrüman ve Müzik Teorisi Dersleri",
      "Güzel Sanatlar ve Çizim Teknikleri Eğitimi", "Kariyer Planlama ve Mentorluk", "Kişisel Gelişim ve Yaşam Koçluğu",
      "İşletme Yönetimi ve Girişimcilik Mentorluğu", "Finansal Okuryazarlık ve Borsa Eğitimi", "Veri Analizi ve İleri Seviye Excel Dersleri",
      "Dijital Pazarlama ve SEO Mentorluğu", "Hitabet, Diksiyon ve Etkili İletişim Eğitimi", "Grafik Tasarım ve Görsel Sanatlar Kursu",
      "Dans, Pilates ve Yoga Eğitimi", "Satranç ve Akıl Oyunları Mentorluğu", "Çocuklar İçin Robotik Kodlama ve STEM Eğitimi",
      "Akademik Danışmanlık ve Makale Yazım Desteği", "Fotoğrafçılık ve Video Kurgu Atölyesi", "Gastronomi ve Yemek Pişirme Teknikleri",
      "El Sanatları, Örgü ve Hobi Atölyeleri", "Sporcu Beslenmesi ve Performans Koçluğu", "Mindfulness ve Meditasyon Rehberliği",
      "Ebeveyn Danışmanlığı ve Çocuk Gelişimi Eğitimi", "Yaratıcı Yazarlık ve Editörlük Mentorluğu", "Sunum Teknikleri ve Topluluk Önünde Konuşma",
      "E-ticaret ve Global Satış Danışmanlığı", "Yapay Zeka Okuryazarlığı ve Prompt Mühendisliği", "Oyun Tasarımı ve Unity/Unreal Engine Mentorluğu",
      "Diyetisyen Onaylı Sağlıklı Yaşam Koçluğu", "Teknik Çizim ve 3D Modelleme Eğitimi", "Portfolyo Geliştirme ve Sanat Okulu Hazırlık",
      "Siber Güvenlik ve Network Altyapı Eğitimi", "Blockchain ve Web3 Teknolojileri Mentorluğu"
    ]
  },
  { 
    icon: Sparkles, 
    image: temizlikImg,
    color: "text-cyan-500 dark:text-cyan-400",
    bg: "bg-cyan-500/10 dark:bg-cyan-500/20",
    name: "Temizlik & Düzenleme", 
    providers: "52 Hizmet Sağlayıcı",
    subCats: [
      "Ev Temizliği", "Ofis ve İş Yeri Temizliği", "İnşaat Sonrası Temizlik",
      "Dış Cephe ve Cam Temizliği", "Koltuk ve Döşeme Yıkama", "Halı Yıkama",
      "Perde ve Stor Temizliği", "İlaçlama ve Pest Kontrol", "Dezenfeksiyon Hizmetleri",
      "Bahçe Temizliği ve Atık Toplama", "Havuz Temizliği", "Apartman ve Site Temizliği",
      "Kuru Temizleme ve Ütüleme", "Giysi ve Gardırop Düzenleme", "Mutfak ve Kiler Düzenleme",
      "Depo ve Garaj Düzenleme", "Ev Taşıma Öncesi ve Sonrası Temizlik", "Baca Temizliği",
      "Su Deposu Temizliği", "Yat ve Tekne Temizliği", "Araç Detaylı Temizlik ve Oto Kuaför",
      "Endüstriyel Alan Temizliği", "Okul ve Eğitim Alanları Temizliği", "Sağlık Kuruluşları Temizliği",
      "Restoran ve Mutfak Detaylı Temizliği", "Evsel Atık ve Moloz Kaldırma", "Yangın ve Su Baskını Sonrası Temizlik",
      "Eşya Paketleme ve İstifleme", "Dijital Düzenleme ve Arşivleme", "Kütüphane ve Doküman Düzenleme",
      "Mobilya ve Yüzey Cilalama", "Zemin Parlatma ve Kristalize Cila", "Çamaşır Yıkama ve Katlama",
      "Yerleşim ve Alan Optimizasyonu", "Minimalist Yaşam Danışmanlığı"
    ]
  },
  { 
    icon: Truck, 
    image: lojistikImg,
    color: "text-emerald-500 dark:text-emerald-400",
    bg: "bg-emerald-500/10 dark:bg-emerald-500/20",
    name: "Lojistik, Paket & Destek", 
    providers: "28 Hizmet Sağlayıcı",
    subCats: [
     "Şehir İçi Kurye ve Paket Teslimatı", "Market ve Bakkal Alışverişi", "Evden Eve Nakliye ve Taşımacılık",
     "Parça Eşya Taşıma", "Evrak, Dosya ve Pasaport Teslimatı", "Eczane Alışverişi ve İlaç Temini",
     "Çiçek ve Hediye Gönderimi", "Mobilya ve Beyaz Eşya Nakliyesi", "Motorlu Kurye Hizmetleri",
     "Bisikletli Kurye ve Çevreci Teslimat", "Kişisel Alışveriş Asistanlığı", "Kargo Teslim Alımı ve İade Yönetimi",
     "Havaalanı Transfer ve Karşılama Desteği", "Sıra Bekleme Hizmetleri", "Ağır Eşya Taşıma ve Yükleme Yardımı",
     "Moloz ve Eski Eşya Atımı", "Su ve Tüp Siparişi Teslimatı", "Şehirler Arası Kargo ve Lojistik Koordinasyonu",
     "Hassas ve Kırılacak Eşya Taşımacılığı", "Soğuk Zincir ve Gıda Taşımacılığı", "Depolama ve Ardiye Hizmetleri",
     "Günlük veya Saatlik Kurye Kiralama", "Acil Paket ve Ekspres Servis", "Etkinlik ve Fuar Ekipmanları Taşımacılığı",
     "Valiz ve Bagaj Taşıma Hizmeti", "Mağaza Teslimat Takibi ve Teslim Alımı", "Kurumsal Dağıtım ve Saha Operasyonları",
     "Gümrükleme ve Mevzuat Takip Desteği", "Araç Çekici ve Yol Yardım Koordinasyonu", "Yurt Dışı Paket Gönderim Danışmanlığı",
     "Dağıtımlı Broşür ve İlan Servisi", "Gece Teslimat ve Nöbetçi Kurye", "Özel Gün ve Organizasyon Lojistiği",
     "Hafif Ticari Araçla Nakliye", "Tedarik Zinciri ve Stok Yönetim Desteği"
    ]
  },
  { 
    icon: Heart, 
    image: bakimImg,
    color: "text-rose-500 dark:text-rose-400",
    bg: "bg-rose-500/10 dark:bg-rose-500/20",
    name: "Kişisel Bakım & Sağlık", 
    providers: "40 Hizmet Sağlayıcı",
    subCats: [
      "Berber ve Erkek Kuaförü", "Kadın Kuaförü ve Saç Tasarımı", "Cilt Bakımı ve Estetik",
      "Makyaj ve Kalıcı Makyaj Hizmetleri", "Manikür, Pedikür ve Nail Art", "Masaj ve Spa Hizmetleri",
      "Lazer Epilasyon ve Ağda", "Kişisel Spor Eğitmeni", "Yoga ve Pilates Eğitimi",
      "Diyetisyen ve Beslenme Danışmanlığı", "Psikolog ve Terapi Hizmetleri", "Fizyoterapi ve Rehabilitasyon",
      "Hasta ve Yaşlı Bakım Refakatçiliği", "Çocuk Bakımı ve Oyun Ablası", "Medikal Ayak Bakımı",
      "Diş Beyazlatma ve Ağız Bakım Danışmanlığı", "İmaj ve Stil Danışmanlığı", "Yaşam Koçluğu ve Mental Sağlık Desteği",
      "Mindfulness ve Meditasyon Rehberliği", "Fonksiyonel Tıp ve Bütüncül Sağlık Danışmanlığı", "Doğum Koçluğu ve Hamilelik Eğitimi",
      "Uyku Koçluğu", "Nefes Terapisi ve Teknikleri", "Alternatif Tıp Uygulamaları",
      "Aromaterapi ve Doğal Ürün Danışmanlığı", "Evde Serum ve Enjeksiyon Hizmetleri", "Protez Saç ve Saç Ekimi Danışmanlığı",
      "İşitme Cihazı Bakım ve Danışmanlığı", "Konuşma Terapisi", "Ergoterapi Hizmetleri",
      "Güzellik Uzmanlığı Eğitimi ve Danışmanlığı", "Bağımlılık Danışmanlığı", "Cinsel Terapi ve Aile Danışmanlığı",
      "Online Sağlık ve Wellness Danışmanlığı", "Kişisel Laboratuvar Testi Takibi ve Check-up Danışmanlığı"
    ]
  },
  { 
    icon: Briefcase, 
    image: danismanlikImg,
    color: "text-slate-500 dark:text-slate-400",
    bg: "bg-slate-500/10 dark:bg-slate-500/20",
    name: "Profesyonel & Danışmanlık", 
    providers: "22 Hizmet Sağlayıcı",
    subCats: [
      "Avukatlık ve Hukuki Danışmanlık", "Muhasebe ve Mali Müşavirlik", "Çeviri ve Tercümanlık",
      "Emlak ve Gayrimenkul Danışmanlığı", "Mimari Proje ve Tasarım", "İç Mimari Danışmanlığı",
      "İnsan Kaynakları ve İşe Alım Danışmanlığı", "İş Stratejisi ve Yönetim Danışmanlığı", "Pazarlama ve Satış Stratejisi",
      "Finansal Planlama ve Yatırım Danışmanlığı", "Hibe ve Teşvik Danışmanlığı", "Marka ve Patent Vekilliği",
      "Sigorta Danışmanlığı", "İş Sağlığı ve Güvenliği Danışmanlığı", "Çevre Danışmanlığı ve Atık Yönetimi",
      "BT ve Teknoloji Danışmanlığı", "Halkla İlişkiler ve İtibar Yönetimi", "Etkinlik ve Organizasyon Danışmanlığı",
      "Dış Ticaret ve Gümrük Danışmanlığı", "Proje Yönetimi ve Planlama", "İstatistiksel Veri Analizi ve Raporlama",
      "Teknik Yazarlık ve Şartname Hazırlama", "Siyasi Danışmanlık ve Kamuoyu Araştırması", "Enerji Verimliliği Danışmanlığı",
      "Kalite Yönetim Sistemleri ve ISO Danışmanlığı", "Tedarik Zinciri ve Satın Alma Danışmanlığı", "Hukuki ve Yeminli Tercüme",
      "Dış Kaynaklı Finans ve CFO Hizmetleri", "Perakende ve Mağazacılık Danışmanlığı", "Yurt Dışı Eğitim Danışmanlığı",
      "Kurumsal Sosyal Medya Stratejisi", "Kriz Yönetimi ve İletişimi", "Müşteri Deneyimi ve Memnuniyet Analizi",
      "Sürdürülebilirlik ve ESG Danışmanlığı", "Sanal Asistanlık ve İdari Destek"
    ]
  },
  { 
    icon: PawPrint, 
    image: hayvanImg,
    color: "text-orange-500 dark:text-orange-400",
    bg: "bg-orange-500/10 dark:bg-orange-500/20",
    name: "Evcil Hayvan Hizmetleri", 
    providers: "15 Hizmet Sağlayıcı",
    subCats: [
      "Köpek Gezdirme", "Evcil Hayvan Bakıcılığı ve Konaklama", "Evcil Hayvan Kuaförü ve Bakımı",
      "Veterinerlik ve Sağlık Danışmanlığı", "Temel ve İleri Seviye Köpek Eğitimi", "Evcil Hayvan Taşıma ve Pet Taksi",
      "Akvaryum Kurulumu ve Bakımı", "Evcil Hayvan Fotoğrafçılığı", "Beslenme ve Diyet Danışmanlığı",
      "Davranış Bozuklukları ve Rehabilitasyon", "Kedi Bakımı ve Oyun Arkadaşlığı", "Kuş Bakımı ve Kafes Temizliği",
      "Sürüngen ve Egzotik Hayvan Bakımı", "Evcil Hayvan Atık Temizliği", "Mikroçip Kaydı ve Künye Hizmetleri",
      "Kayıp Evcil Hayvan Arama Desteği", "Evcil Hayvan İlk Yardım Eğitimi", "Ameliyat Sonrası Evde Bakım Destek",
      "Yaşlı Evcil Hayvan Bakımı", "Evcil Hayvan Oteli ve Pansiyonu", "Evde Banyo ve Tırnak Kesimi",
      "Evcil Hayvan Sahiplendirme Danışmanlığı", "Evcil Hayvan Dostu Mekan ve Seyahat Planlama", "Sosyalleşme ve Oyun Grupları Organizasyonu",
      "Evcil Hayvan Güvenlik Filesi ve Kapı Montajı", "Akvaryum ve Teraryum Tasarımı", "Evcil Hayvan Masajı ve Wellness",
      "Terapi Hayvanı Eğitimi", "Organik Mama ve Ödül Maması Hazırlama", "Şov ve Yarışma Hazırlık Eğitimi",
      "Evcil Hayvan Veda ve Cenaze Hizmetleri", "Kemirme ve Tırmalama Sorunları Çözümü", "Tuvalet Eğitimi Danışmanlığı",
      "Evcil Hayvan Ürünleri ve İlaç Temini", "Irk Seçimi ve Hazırlık Danışmanlığı"
    ]
  },
  { 
    icon: CalendarDays, 
    image: etkinlikImg,
    color: "text-pink-500 dark:text-pink-400",
    bg: "bg-pink-500/10 dark:bg-pink-500/20",
    name: "Etkinlik & Organizasyon", 
    providers: "19 Hizmet Sağlayıcı",
    subCats: [
      "Düğün, Nişan ve Davet Organizasyonu", "Doğum Günü ve Parti Planlama", "Kurumsal Etkinlik ve Toplantı Yönetimi",
      "Ses, Işık ve Görüntü Sistemleri Kiralama", "Catering ve Yemek Hizmetleri", "DJ ve Canlı Müzik Performansı",
      "Sahne Kurulumu ve Dekor Tasarımı", "Davetiye Tasarımı ve Matbaa İşleri", "Etkinlik Fotoğrafçılığı ve Video Çekimi",
      "Host, Hostes ve Karşılama Ekibi", "Mekan Süsleme ve Çiçek Tasarımı", "Havai Fişek ve Lazer Gösterileri",
      "Palyaço ve Çocuk Eğlence Hizmetleri", "Fuar Stand Tasarımı ve Kurulumu", "Konser ve Festival Organizasyonu",
      "Seminer, Konferans ve Workshop Planlama", "Kına Gecesi ve Bekarlığa Veda Organizasyonu", "Mezuniyet Töreni ve Balo Hazırlığı",
      "Masa ve Sandalye Kiralama", "Çadır ve Portatif Yapı Kurulumu", "Sanatçı Menajerliği ve Booking",
      "Promosyon Ürünleri ve Etkinlik Kitleri", "Kokteyl ve Bar Servisi", "Dijital Etkinlik ve Webinar Teknik Desteği",
      "Güvenlik ve Giriş Kontrol Hizmetleri", "Temizlik ve Etkinlik Sonrası Toplama", "LCV ve Davetli Takibi",
      "VIP Transfer ve Protokol Hizmetleri", "Kostüm ve Ekipman Kiralama", "Tur ve Gezi Organizasyonu",
      "Sergi ve Galeri Kuratörlüğü", "Spor Müsabakaları ve Turnuva Yönetimi", "Ödül Töreni ve Plaket Hazırlığı",
      "Teknik Prodüksiyon ve Yayın Yönetimi", "Etkinlik Danışmanlığı ve Bütçe Planlama"
    ]
  }
];

const ProfileAvatar = ({ src, name, size = "w-8 h-8" }: { src?: string, name: string, size?: string }) => {
  const initials = name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  
  if (!src || src === "") {
    return (
      <div className={`${size} rounded-full bg-[#14281c] flex items-center justify-center text-white text-[10px] font-bold border border-border-muted overflow-hidden shrink-0`}>
        {initials}
      </div>
    );
  }
  
  return (
    <div className={`${size} rounded-full border border-border-muted overflow-hidden shrink-0`}>
      <img src={src} alt={name} className="w-full h-full object-cover" />
    </div>
  );
};

const VerifiedBadge = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#F97316" strokeWidth="1.2" strokeLinecap="round" strokeLinejoin="round" className="shrink-0 bg-[#F97316]/5 rounded-full p-[1px]">
    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"></path>
  </svg>
);

export default function HomeScreen({ onBack, onChat, onProfile, onOpenPublicProfile, onJobs }: { onBack?: () => void, onChat?: () => void, onProfile?: () => void, onOpenPublicProfile?: (user: any) => void, onJobs?: () => void }) {
  const [mode, setMode] = useState<Mode>("ulak");
  const [isPublishing, setIsPublishing] = useState(false); // Yeni menü için state
  const [isDark, setIsDark] = useState(false);
  const [expandedCategory, setExpandedCategory] = useState<string | null>(null);
  const [selectedSubCategory, setSelectedSubCategory] = useState<string | null>(null);
  const [isUzmanBadgeVisible, setIsUzmanBadgeVisible] = useState(true);

  useEffect(() => {
    if (isUzmanBadgeVisible) {
      const timer = setTimeout(() => {
        setIsUzmanBadgeVisible(false);
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [isUzmanBadgeVisible]);

  // Search & Filter States
  const [searchQueryUlak, setSearchQueryUlak] = useState("");
  const [isUlakSearchActive, setIsUlakSearchActive] = useState(false);
  const [searchQueryUzman, setSearchQueryUzman] = useState("");
  const [isUzmanSearchActive, setIsUzmanSearchActive] = useState(false);
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [filterType, setFilterType] = useState<string>("Tümü");
  const [filterDistance, setFilterDistance] = useState<number>(10);
  const [filterTime, setFilterTime] = useState<string>("Tümü");
  const [expandedCommentsPostIndex, setExpandedCommentsPostIndex] = useState<number | null>(null);

  // Uzman Filter & Sort States
  const [isUzmanFilterOpen, setIsUzmanFilterOpen] = useState(false);
  const [isUzmanSortOpen, setIsUzmanSortOpen] = useState(false);
  const [uzmanSort, setUzmanSort] = useState<string>("Varsayılan");
  const [uzmanFilterGender, setUzmanFilterGender] = useState<string>("Tümü");
  const [uzmanFilterRating, setUzmanFilterRating] = useState<string>("Tümü");
  const [uzmanFilterPrice, setUzmanFilterPrice] = useState<string>("Tümü");

  // Offer Modal State
  const [selectedBidPost, setSelectedBidPost] = useState<any>(null);
  const [bidPrice, setBidPrice] = useState("");
  const [bidMessage, setBidMessage] = useState("");

  const filteredSubCats = searchQueryUzman ? mainCategories.flatMap(cat => cat.subCats).filter(sub => sub.toLowerCase().includes(searchQueryUzman.toLowerCase())) : [];

  useEffect(() => {
    if (isDark) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [isDark]);

  const ulakHeadlines = [
    "Dinamik çevrende neler oluyor?",
    "Yardım, duyuru, danışma veya küçük iş paylaş.",
    "Yakınındaki gündelik ihtiyaçları ve paylaşımları gör.",
    "Dinamik çevrende destek olabileceğin işleri keşfet."
  ];

  const uzmanHeadlines = [
    "Hangi hizmete ihtiyacın var?",
    "İhtiyacını anlat, hizmet verenlerden teklif al.",
    "Profesyonel destek almak istediğin hizmeti seç.",
    "Hizmet kategorilerinden seçim yap, talebini oluştur."
  ];

  const headlines = mode === 'ulak' ? ulakHeadlines : uzmanHeadlines;
  const [headlineIndex, setHeadlineIndex] = useState(0);

  useEffect(() => {
    setHeadlineIndex(0); // mode değiştiğinde index'i sıfırla
    const interval = setInterval(() => {
      setHeadlineIndex((prev) => (prev + 1) % headlines.length);
    }, 4000);
    return () => clearInterval(interval);
  }, [mode, headlines.length]);

  const hideHeader = mode === 'uzman' && ((searchQueryUzman !== "" && isUzmanSearchActive) || selectedSubCategory !== null);

  const [ulakPosts, setUlakPosts] = useState<any[]>([
    {
      type: "yardım",
      authorName: "Ayşe T.",
      authorImg: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=150&q=80",
      title: "Yıldız Tornavida",
      desc: "Ufak bir montaj işim var, 1 saatliğine yıldız tornavida ödünç verebilecek komşu var mı?",
      publishTime: "5m",
      dist: "150m",
      likes: 2,
      comments: 0
    },
    {
       type: "duyuru",
       authorName: "Kemal S.",
       authorImg: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
       title: "Kayıp Kedi",
       desc: "Dün akşam üzeri parkın orada kedimiz Tarçın kayboldu. Görenlerin haber vermesini rica ederiz.",
       postImg: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=600&q=80",
       publishTime: "27m",
       dist: "0.2 km",
       likes: 12,
       comments: 5,
       postComments: [
         { name: "Fatma Y.", img: "https://i.pravatar.cc/150?img=32", text: "Çok geçmiş olsun, gözümüz yollarda olur.", time: "10m" },
         { name: "Murat C.", img: "https://i.pravatar.cc/150?img=11", text: "Parkın köşesinde gördüm sanırım, birazdan bakıcam.", time: "5m" }
       ]
    },
    {
      type: "iş",
      authorName: "Cansu B.",
      authorImg: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=150&q=80",
      desc: "Yarın saat 14:00'da ufak ırk köpeğimi 45 dakika gezdirecek biri lazım.",
      publishTime: "1h",
      dist: "1.2 km",
      likes: 4,
      comments: 1,
      price: "Teklif Bekliyor",
      postComments: [
        { name: "Selin G.", img: "https://i.pravatar.cc/150?img=5", text: "Hangi parkta dolaştırılması gerekiyor?", time: "20m" }
      ]
    }
  ]);

  const [toastMessage, setToastMessage] = useState<string | null>(null);

  useEffect(() => {
    if (toastMessage) {
      const timer = setTimeout(() => setToastMessage(null), 3000);
      return () => clearTimeout(timer);
    }
  }, [toastMessage]);

  const filteredUlakPosts = ulakPosts.filter(item => {
    // Type filter ("Tümü", "Duyuru", "İş", "Yardım", "Etkinlik")
    const typeMatch = filterType === "Tümü" || item.type.toLowerCase() === filterType.toLowerCase();
    // Search query
    const query = isUlakSearchActive ? searchQueryUlak.toLowerCase() : "";
    const searchMatch = !query || 
                        (item.title && item.title.toLowerCase().includes(query)) || 
                        (item.desc && item.desc.toLowerCase().includes(query)) ||
                        item.type.toLowerCase().includes(query);
    return typeMatch && searchMatch;
  });

  const [isUzmanBadgeVisibleLocal, setIsUzmanBadgeVisibleLocal] = useState(isUzmanBadgeVisible);

  return (
    <div className="h-[100dvh] w-full bg-surface flex justify-center font-sans text-content overflow-hidden">
      <AnimatePresence>
        {isPublishing && (
          <CreatePostScreen 
            mode={mode} 
            onClose={() => setIsPublishing(false)} 
            onSubmit={(post, action) => {
              if (mode === "ulak") {
                const newPost = {
                  type: post.type,
                  authorName: "Sen",
                  authorImg: "https://i.pravatar.cc/150?img=68",
                  title: post.title,
                  time: "şimdi",
                  location: post.location,
                  desc: post.desc,
                  likes: 0,
                  comments: 0,
                  postImg: post.mediaPreview || null,
                  postComments: []
                };
                setUlakPosts([newPost, ...ulakPosts]);
              }
              // Expert mode requests are handled separately (e.g. sent as notifications/to a different DB)
              // In this demo, we just close and show success.
              setIsPublishing(false);
              
              if (action === "GOTO_JOBS" && onJobs) {
                 onJobs();
              } else {
                 setToastMessage(mode === "ulak" ? "Paylaşımın dinamik çevrene ulaştı." : "Talebin uygun hizmet verenlere ulaştı.");
              }
            }} 
          />
        )}
      </AnimatePresence>

      {/* Toast Message */}
      <AnimatePresence>
        {toastMessage && (
          <motion.div 
            initial={{ opacity: 0, y: -20, x: "-50%" }}
            animate={{ opacity: 1, y: 30, x: "-50%" }}
            exit={{ opacity: 0, y: -20, x: "-50%" }}
            className="fixed top-0 left-1/2 z-[200] flex items-center gap-2 px-4 py-2.5 bg-green-50 text-green-700 rounded-full shadow-lg border border-green-200 whitespace-nowrap text-[12px] font-bold"
          >
            <div className="w-5 h-5 rounded-full bg-green-100 flex items-center justify-center">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
            </div>
            {toastMessage}
          </motion.div>
        )}
      </AnimatePresence>

      {/* Bid Modal */}
      <AnimatePresence>
        {selectedBidPost && (
          <>
            <motion.div 
              initial={{ opacity: 0 }} 
              animate={{ opacity: 1 }} 
              exit={{ opacity: 0 }}
              onClick={() => { setSelectedBidPost(null); setBidPrice(""); setBidMessage(""); }}
              className="absolute inset-0 bg-black/40 z-40"
            />
            <motion.div 
              initial={{ y: "100%" }} 
              animate={{ y: 0 }} 
              exit={{ y: "100%" }}
              transition={{ type: "spring", damping: 25, stiffness: 200 }}
              className="absolute bottom-0 left-0 right-0 bg-surface rounded-t-[24px] shadow-2xl z-50 flex flex-col pb-safe max-h-[85vh]"
            >
              <div className="flex justify-center pt-3 pb-2 shrink-0">
                <div className="w-12 h-1.5 bg-border-muted rounded-full" />
              </div>
              <div className="px-4 pb-4 border-b border-border-subtle flex justify-between items-center shrink-0">
                <div className="w-8 h-8" />
                <h2 className="text-[14px] font-extrabold tracking-tight text-content">Teklif Ver</h2>
                <button onClick={() => { setSelectedBidPost(null); setBidPrice(""); setBidMessage(""); }} className="w-8 h-8 flex items-center justify-center rounded-full bg-surface-muted active:scale-95">
                  <X className="w-4 h-4 text-content-muted" strokeWidth={2} />
                </button>
              </div>
              
              <div className="flex-1 overflow-y-auto p-4 flex flex-col gap-4">
                <div className="bg-surface-muted/30 p-3 rounded-xl border border-border-subtle mb-2">
                  <div className="flex items-center gap-2 mb-2">
                    <ProfileAvatar src={selectedBidPost.authorImg} name={selectedBidPost.authorName} size="w-8 h-8" />
                    <div className="flex flex-col">
                      <span className="text-[12px] font-bold text-content">{selectedBidPost.authorName}</span>
                      <span className="text-[10px] text-content-muted">{selectedBidPost.dist} ötede</span>
                    </div>
                  </div>
                  <p className="text-[12px] text-content leading-relaxed font-medium line-clamp-3">
                    {selectedBidPost.desc}
                  </p>
                </div>

                <div className="flex flex-col gap-1.5">
                  <label className="text-[10px] font-bold text-content-muted ml-1">Teklif Tutarınız (₺)</label>
                  <input 
                    type="number" 
                    value={bidPrice}
                    onChange={(e) => setBidPrice(e.target.value)}
                    placeholder="Örn: 800"
                    className="w-full bg-surface border border-border-muted rounded-xl px-4 py-3 text-[14px] font-bold text-content outline-none focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316]"
                  />
                </div>

                <div className="flex flex-col gap-1.5">
                  <label className="text-[10px] font-bold text-content-muted ml-1">Mesajınız (Opsiyonel)</label>
                  <textarea 
                    value={bidMessage}
                    onChange={(e) => setBidMessage(e.target.value)}
                    placeholder="Merhaba, işlemi istediğiniz saatte hemen halledebilirim..."
                    rows={3}
                    className="w-full bg-surface border border-border-muted rounded-xl px-4 py-3 text-[12px] font-medium text-content outline-none focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] resize-none"
                  />
                  <p className="text-[10px] text-content-muted text-center mt-1">Viaverse üzerinden verilen tekliflerde tutarından sadece %10 komisyon kesilir.</p>
                </div>

                <button 
                  disabled={!bidPrice}
                  onClick={() => {
                    setSelectedBidPost(null);
                    setBidPrice("");
                    setBidMessage("");
                    setToastMessage("Teklifiniz gönderildi! İşlerim menüsünden takip edebilirsiniz.");
                  }}
                  className={`w-full py-3.5 rounded-xl font-bold text-[14px] transition-all flex items-center justify-center mt-2 ${bidPrice ? 'bg-[#F97316] text-white shadow-sm active:scale-[0.98]' : 'bg-surface-muted text-content-muted cursor-not-allowed'}`}
                >
                  Teklifi Gönder
                </button>

              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>

      <div className="w-full max-w-md bg-surface h-full relative flex flex-col">
        
        {/* Header - Senin tasarımın aynısı */}
        {!hideHeader && (
        <header className="sticky top-0 bg-surface z-30 pt-3 pb-0 shrink-0">
          {/* Top Bar with Search, Messages, Profile */}
          <div className="flex items-center gap-2 px-3 mb-2">
            {/* Premium Back Button */}
            <button onClick={onBack} className="w-[32px] h-[32px] flex-shrink-0 flex items-center justify-center rounded-full border border-border-muted bg-surface-muted text-content hover:bg-surface-hover active:scale-95 transition-all shadow-sm">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><polyline points="15 18 9 12 15 6"></polyline></svg>
            </button>

            {/* Search input and button - Pill Design */}
            <div className="flex-1 flex items-center h-[40px] rounded-full overflow-hidden shadow-sm border border-border-faint bg-surface-muted">
              <div className="flex-1 flex items-center px-4 h-full">
                 <Compass width="16" height="16" className={`${mode === 'ulak' ? 'text-[#F97316]' : 'text-content-muted'} mr-2 flex-shrink-0`} strokeWidth={2} />
                 <div className="flex-1 overflow-hidden relative h-full flex items-center">
                   <input 
                     type="text"
                     value={mode === 'ulak' ? searchQueryUlak : searchQueryUzman}
                     onChange={(e) => {
                       if (mode === 'ulak') {
                         setSearchQueryUlak(e.target.value);
                         setIsUlakSearchActive(false);
                       } else {
                         setSearchQueryUzman(e.target.value);
                         setIsUzmanSearchActive(false);
                       }
                     }}
                     onKeyDown={(e) => {
                       if (e.key === 'Enter') {
                         if (mode === 'ulak') setIsUlakSearchActive(true);
                         else setIsUzmanSearchActive(true);
                       }
                     }}
                     placeholder={headlines[headlineIndex]}
                     className="w-full h-full bg-transparent border-none outline-none text-[12px] font-medium text-content placeholder-content-muted/50"
                   />
                 </div>
              </div>
              <button 
                onClick={() => {
                  if (mode === 'ulak' && searchQueryUlak) setIsUlakSearchActive(true);
                  else if (mode === 'uzman' && searchQueryUzman) setIsUzmanSearchActive(true);
                }} 
                className="bg-[#F97316] text-white px-4 h-full flex items-center justify-center cursor-pointer text-[12px] font-bold transition-transform active:scale-95 shrink-0"
              >
                 ara
              </button>
            </div>
            
            <div className="flex items-center gap-2 text-content">
              <button 
                onClick={() => setIsDark(!isDark)}
                className="w-[32px] h-[32px] rounded-full flex items-center justify-center bg-surface-muted border border-border-muted text-content-muted"
              >
                {isDark ? <Sun className="w-[18px] h-[18px]" strokeWidth={1.5} /> : <Moon className="w-[18px] h-[18px]" strokeWidth={1.5} />}
              </button>
            </div>
          </div>

          <div className="flex justify-center gap-12 px-6 pt-0">
            <button 
              onClick={() => setMode('ulak')}
              className={`flex flex-col items-center gap-1 pb-2 border-b-[3px] transition-all min-w-[60px] ${mode === 'ulak' ? 'border-[#F97316] opacity-100' : 'border-transparent opacity-40 hover:opacity-100'}`}
            >
              <Compass className="w-6 h-6 text-content" strokeWidth={mode === 'ulak' ? 2 : 1.5} />
              <span className="text-[10px] font-semibold tracking-tight">keşfet</span>
            </button>
            <button 
              onClick={() => setMode('uzman')}
              className={`flex flex-col items-center gap-1 pb-2 border-b-[3px] transition-all min-w-[80px] ${mode === 'uzman' ? 'border-[#F97316] opacity-100' : 'border-transparent opacity-40 hover:opacity-100'}`}
            >
              <MonitorSmartphone className="w-6 h-6 text-content" strokeWidth={mode === 'uzman' ? 2 : 1.5} />
              <span className="text-[10px] font-semibold tracking-tight">hizmet al</span>
            </button>
          </div>
        </header>
        )}

        {/* Content Area */}
        <main className="flex-1 overflow-y-auto bg-base pb-28">
          <AnimatePresence mode="wait">
            {mode === 'ulak' ? (
              <motion.div key="ulak" initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }} transition={{ duration: 0.2 }} className="flex flex-col">
                {/* Keşfet Filter Bar */}
                <div className="flex items-center gap-2 overflow-x-auto px-4 py-4 bg-surface scrollbar-hide shrink-0">
                  {["tümü", "duyuru", "yardım", "iş", "danışma"].map(t => {
                    const isActive = filterType.toLowerCase() === t;
                    
                    return (
                      <button 
                        key={t}
                        onClick={() => setFilterType(t === 'tümü' ? 'Tümü' : t.charAt(0).toUpperCase() + t.slice(1))}
                        className={`h-9 px-4 rounded-full text-[12px] font-semibold whitespace-nowrap shrink-0 transition-all flex items-center justify-center gap-1.5 border w-fit min-w-max ${isActive ? 'bg-[#F97316] text-white border-[#F97316] shadow-lg shadow-[#F97316]/20' : 'bg-surface text-content-muted border-border-subtle hover:border-border-muted hover:text-[#F97316]'}`}
                      >
                        {t === "tümü" && <LayoutGrid className="w-3.5 h-3.5" strokeWidth={2} />}
                        {t === "duyuru" && <img src={duyuruIcon} alt="duyuru" className="w-[18px] h-[18px] object-contain" />}
                        {t === "yardım" && <img src={yardimIcon} alt="yardım" className="w-[18px] h-[18px] object-contain" />}
                        {t === "iş" && <img src={isIcon} alt="iş" className="w-[18px] h-[18px] object-contain" />}
                        {t === "danışma" && <img src={danismaIcon} alt="danışma" className="w-[18px] h-[18px] object-contain" />}
                        <span className="leading-none">{t.charAt(0).toUpperCase() + t.slice(1)}</span>
                      </button>
                    );
                  })}
                </div>

                {isUlakSearchActive && searchQueryUlak && (
                  <div className="px-4 pt-4 pb-2 border-b border-border-subtle bg-surface">
                    <button onClick={() => { setSearchQueryUlak(""); setIsUlakSearchActive(false); }} className="flex items-center gap-1.5 text-content-muted hover:text-content text-[12px] font-medium transition-colors w-max">
                      <ChevronRight className="w-4 h-4 rotate-180" />
                      Geri dön
                    </button>
                    <p className="text-[14px] font-medium text-content mt-2">"{searchQueryUlak}" için sonuçlar</p>
                  </div>
                )}
                
                <div className="flex flex-col">
                  {filteredUlakPosts.length > 0 ? filteredUlakPosts.map((item, i) => (
                    <div key={i} className="bg-surface pb-2.5 border-b border-border-subtle">
                      <div className="flex justify-between items-start pt-3 px-3 pb-1">
                        <div 
                          className="flex items-center gap-2.5 cursor-pointer"
                          onClick={() => onOpenPublicProfile?.({ name: item.authorName, img: item.authorImg, distance: item.dist, joinedDate: "Ocak 2024", rating: 4.9, reviewsCount: 15, posts: [item] })}
                        >
                          <ProfileAvatar src={item.authorImg} name={item.authorName} size="w-[32px] h-[32px]" />
                          <div className="flex flex-col">
                            <div className="flex items-center gap-1.5 mb-1">
                              <span className="text-[12px] font-bold text-content leading-none hover:text-[#F97316] transition-colors">{item.authorName}</span>
                              <VerifiedBadge />
                            </div>
                            <div className="text-[10px] text-content-muted leading-none flex items-center gap-1">
                              <span>{item.publishTime}</span>
                              <span>•</span>
                              <span>{item.dist}</span>
                            </div>
                          </div>
                        </div>
                      </div>

                      <div className="px-3 pb-2 pt-1">
                        <p className="text-[12px] text-content leading-[1.45]">
                          {(() => {
                            const postTypeData = POST_TYPES.find(t => t.id === item.type.toLowerCase());
                            const uzmanTypeData = mainCategories.find(t => t.name === item.type);
                            
                            const TypeBadge = postTypeData ? (
                                <span className={`inline-flex items-center gap-1 px-2 py-0.5 ${postTypeData.iconBg} rounded text-[9px] font-bold ${postTypeData.color} tracking-tight uppercase ml-2 align-middle`}>
                                  <img src={postTypeData.image} alt={postTypeData.id} className="w-3.5 h-3.5 object-contain" />
                                  {postTypeData.id}
                                </span>
                            ) : uzmanTypeData ? (
                                <span className={`inline-flex items-center gap-1 px-2 py-0.5 ${uzmanTypeData.bg} rounded text-[9px] font-bold ${uzmanTypeData.color} tracking-tight uppercase ml-2 align-middle`}>
                                  {uzmanTypeData.image && <img src={uzmanTypeData.image} alt={uzmanTypeData.name} className="w-3.5 h-3.5 object-contain" />}
                                  {uzmanTypeData.name}
                                </span>
                            ) : (
                                <span className="text-[#F97316] ml-2 text-[10px] font-semibold tracking-tight">#{item.type.toLowerCase()}</span>
                            );

                            return item.title ? (
                                <span className="flex items-center mb-0.5 font-bold text-[#1C1C1C]">{item.title} {TypeBadge}</span>
                            ) : (
                                <span className="mb-0.5 block">{TypeBadge}</span>
                            );
                          })()}
                          {item.desc}
                        </p>
                        {item.postImg && (
                          <div className="mt-2 -mx-3">
                            <img src={item.postImg} alt="Post media" className="w-full h-auto object-cover max-h-[250px]" />
                          </div>
                        )}
                      </div>

                      <div className="px-3 py-1 flex items-center justify-between">
                        <div className="flex items-center gap-4">
                           <button className="flex items-center gap-1.5 py-1 text-content-muted hover:text-content transition-colors group">
                              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="group-hover:text-red-500 transition-colors"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
                              <span className="text-[12px] font-medium">{item.likes > 0 ? item.likes : 0}</span>
                           </button>
                           <button onClick={() => setExpandedCommentsPostIndex(expandedCommentsPostIndex === i ? null : i)} className="flex items-center gap-1.5 py-1 text-content-muted hover:text-content transition-colors group">
                              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="group-hover:text-blue-500 transition-colors"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
                              <span className="text-[12px] font-medium">{item.comments > 0 ? item.comments : 0}</span>
                           </button>
                        </div>
                        <div className="flex items-center gap-1.5">
                            {item.type === 'iş' && (
                              <button 
                                onClick={() => setSelectedBidPost(item)}
                                className="bg-[#F97316] text-white px-3 py-1.5 rounded-full text-[10px] font-bold active:scale-95 transition-transform tracking-tight shadow-[#F97316]/30 shadow-sm"
                              >
                                teklif ver
                              </button>
                            )}
                        </div>
                      </div>

                      {/* Expanded Comments */}
                      <AnimatePresence>
                        {expandedCommentsPostIndex === i && (
                          <motion.div 
                            initial={{ height: 0, opacity: 0 }}
                            animate={{ height: "auto", opacity: 1 }}
                            exit={{ height: 0, opacity: 0 }}
                            className="overflow-hidden"
                          >
                            <div className="px-3 pt-2 pb-1 border-t border-border-subtle mt-1 flex flex-col gap-3">
                              {item.postComments && item.postComments.length > 0 ? (
                                item.postComments.map((comment, idx) => (
                                  <div key={idx} className="flex gap-2">
                                    <ProfileAvatar src={comment.img} name={comment.name} size="w-6 h-6" />
                                    <div className="flex flex-col bg-surface-muted rounded-2xl rounded-tl-none px-3 py-2 text-[10px] text-content max-w-[90%]">
                                      <div className="flex items-center gap-1.5 mb-0.5">
                                        <span className="font-semibold">{comment.name}</span>
                                        <VerifiedBadge />
                                        <span className="text-[9px] text-content-muted">{comment.time}</span>
                                      </div>
                                      <p className="leading-relaxed">{comment.text}</p>
                                    </div>
                                  </div>
                                ))
                              ) : (
                                <p className="text-[10px] text-content-muted text-center py-2">Henüz yorum yapılmamış. İlk yorumu sen yap!</p>
                              )}
                              
                              <div className="flex gap-2 relative mt-1">
                                <img src="https://i.pravatar.cc/150?img=68" alt="Me" className="w-6 h-6 rounded-full object-cover shrink-0" />
                                <div className="flex-1 relative">
                                  <input 
                                    type="text" 
                                    placeholder="Yorum yaz..." 
                                    className="w-full bg-surface-muted rounded-full pl-3 pr-8 py-1.5 text-[12px] text-content outline-none border border-transparent focus:border-border-muted"
                                  />
                                  <button className="absolute right-2 top-1/2 -translate-y-1/2 text-[#F97316] hover:opacity-80">
                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="m22 2-7 20-4-9-9-4Z"/><path d="M22 2 11 13"/></svg>
                                  </button>
                                </div>
                              </div>
                            </div>
                          </motion.div>
                        )}
                      </AnimatePresence>
                    </div>
                  )) : (
                    <div className="text-[12px] text-content-muted text-center py-10 mt-10">aradığın kritere uygun sonuç bulunamadı.</div>
                  )}
                </div>
              </motion.div>
            ) : (
              <motion.div key="uzman" initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }} transition={{ duration: 0.2 }} className="flex flex-col">
                
                <div className="space-y-1.5 px-4 pt-4 pb-6">
                  {/* Categories / Search Results / Experts List */}
                  {selectedSubCategory ? (
                    <div className="flex flex-col gap-2 mt-1">
                       <button onClick={() => setSelectedSubCategory(null)} className="flex items-center gap-1.5 text-content-muted hover:text-content text-[12px] font-medium transition-colors w-max mb-1">
                         <ChevronRight className="w-4 h-4 rotate-180" />
                         Geri dön
                       </button>
                       <div className="flex flex-col gap-1 mb-2">
                         <h2 className="text-[16px] font-semibold text-content">{selectedSubCategory}</h2>
                       </div>

                       {/* Top Filter Bar */}
                       <div className="flex gap-2 w-full mb-3">
                         <button onClick={() => setIsUzmanFilterOpen(true)} className="flex-1 flex items-center justify-center gap-1.5 py-1.5 rounded-full bg-surface-muted/50 text-[12px] font-medium text-content hover:bg-surface-muted transition-colors relative">
                           {uzmanFilterGender !== "Tümü" || uzmanFilterRating !== "Tümü" || uzmanFilterPrice !== "Tümü" ? (
                             <div className="absolute top-0 right-3 w-2 h-2 rounded-full bg-[#F97316]" />
                           ) : null}
                           <SlidersHorizontal className="w-[14px] h-[14px]" strokeWidth={2} /> Filtrele
                         </button>
                         <button onClick={() => setIsUzmanSortOpen(true)} className="flex-1 flex items-center justify-between px-3 py-1.5 rounded-full bg-surface-muted/50 hover:bg-surface-muted transition-colors">
                           <div className="flex items-center gap-1.5">
                             <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-content-muted"><path d="m3 16 4 4 4-4"/><path d="M7 20V4"/><path d="m21 8-4-4-4 4"/><path d="M17 4v16"/></svg>
                             <span className="text-[12px] font-medium text-content">{uzmanSort}</span>
                           </div>
                           <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-content-muted"><path d="m6 9 6 6 6-6"/></svg>
                         </button>
                       </div>
                       
                       <div className="flex flex-col gap-2">
                         {[1, 2, 3].map(item => (
                           <div 
                              key={item} 
                              onClick={() => {
                                if (onOpenPublicProfile) {
                                  onOpenPublicProfile({
                                    id: item,
                                    name: item === 1 ? 'Ahmet Yılmaz' : item === 2 ? 'Ayşe Demir' : 'Cemil Kaya',
                                    role: selectedSubCategory || 'Uzman',
                                    avatar: `https://i.pravatar.cc/150?img=${item * 20}`,
                                    rating: `4.${9-item}`,
                                    reviewCount: `12${item}`,
                                    location: item === 1 ? 'İstanbul, Beylikdüzü' : item === 2 ? 'Ankara, Mamak' : 'İzmir, Bornova',
                                    isUzman: true,
                                    badges: ['hızlı yanıt veren', 'popüler'],
                                    description: item === 1 ? '15 yıllık deneyimimle profesyonel hizmet sunuyorum. İşimde titiz ve özenliyim.' : item === 2 ? 'Güler yüzlü ve hızlı çözüm odaklı çalışırım. %100 müşteri memnuniyeti garantisi.' : 'Güvenilir ve pratik destek arayanlar için buradayım. Referanslarım mevcuttur.'
                                  });
                                }
                              }}
                              className="flex flex-col border border-border-subtle rounded-2xl p-2.5 bg-surface gap-2 cursor-pointer hover:bg-surface-hover active:scale-[0.98] transition-all"
                           >
                             <div className="flex gap-2.5 items-center">
                               <div className="w-[48px] h-[48px] shrink-0 rounded-full overflow-hidden bg-surface-muted">
                                 <img src={`https://i.pravatar.cc/150?img=${item * 20}`} alt="avatar" className="w-full h-full object-cover" />
                               </div>
                               <div className="flex flex-col">
                                 <span className="font-semibold text-[12px] text-content tracking-tight">{item === 1 ? 'Ahmet Yılmaz' : item === 2 ? 'Ayşe Demir' : 'Cemil Kaya'}</span>
                                 <span className="text-[10px] text-content-muted mt-[1px] flex justify-start items-center gap-1">
                                    <Star className="w-3 h-3 text-[#F97316] fill-current" /> <span className="font-semibold text-content">4.{9-item}</span> (12{item})
                                 </span>
                                 <span className="text-[10px] text-content-muted mt-[1px] flex justify-start items-center gap-1">
                                   <MapPin className="w-3 h-3" strokeWidth={2} /> {item === 1 ? 'İstanbul, Beylikdüzü' : item === 2 ? 'Ankara, Mamak' : 'İzmir, Bornova'}
                                 </span>
                               </div>
                             </div>
                             
                             {/* Description */}
                             <div className="text-[10px] text-content-muted line-clamp-2 px-1">
                               {item === 1 ? '15 yıllık deneyimimle profesyonel hizmet sunuyorum. İşimde titiz ve özenliyim.' : item === 2 ? 'Güler yüzlü ve hızlı çözüm odaklı çalışırım. %100 müşteri memnuniyeti garantisi.' : 'Güvenilir ve pratik destek arayanlar için buradayım. Referanslarım mevcuttur.'}
                             </div>
                           </div>
                         ))}
                       </div>
                    </div>
                  ) : searchQueryUzman && isUzmanSearchActive ? (
                    <motion.div layout className="flex flex-col mb-4 bg-surface rounded-xl p-0 gap-1 mt-3">
                       <button onClick={() => { setSearchQueryUzman(""); setIsUzmanSearchActive(false); }} className="flex items-center gap-1.5 text-content-muted hover:text-content text-[12px] font-medium transition-colors w-max mb-2">
                         <ChevronRight className="w-4 h-4 rotate-180" />
                         Geri dön
                       </button>
                       <AnimatePresence>
                         {filteredSubCats.length > 0 ? filteredSubCats.map((sub, idx) => (
                           <motion.div 
                             initial={{ opacity: 0, y: 5 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}
                             key={idx} 
                             onClick={() => setSelectedSubCategory(sub)}
                             className="flex items-center gap-2 py-2.5 px-1 border-b border-border-subtle/50 last:border-0 cursor-pointer active:opacity-70 group"
                           >
                             <ChevronRight className="w-3.5 h-3.5 text-[#F97316] opacity-80" strokeWidth={2} />
                             <span className="text-[12px] font-medium text-content-muted tracking-tight group-hover:text-content transition-colors">{sub}</span>
                           </motion.div>
                         )) : (
                            <div className="text-[12px] text-content-muted text-center py-4">Sonuç bulunamadı...</div>
                         )}
                       </AnimatePresence>
                    </motion.div>
                  ) : (
                    <>
                      <div className="mb-5 px-1 mt-2">
                        <h2 className="text-[16px] tracking-tight font-medium text-content leading-snug">
                          Hizmet verenlere göz at veya <span className="text-[#F97316] font-semibold">talep oluştur</span>
                        </h2>
                        <p className="text-[12px] text-content-muted mt-1.5 leading-relaxed">
                          Kategorilere göre profesyonelleri inceleyebilir veya neye ihtiyacın olduğunu yazarak uygun hizmet verenlerden teklif alabilirsin.
                        </p>
                      </div>
                      <motion.div layout className="flex flex-col mb-4">
                      {mainCategories.map((cat, i) => (
                        <motion.div layout key={i} className="flex flex-col">
                          <div 
                            onClick={() => setExpandedCategory(expandedCategory === cat.name ? null : cat.name)}
                            className="flex items-center justify-between py-2.5 px-1 border-b border-border-subtle last:border-0 cursor-pointer active:opacity-70 transition-opacity group"
                          >
                            <div className="flex items-center gap-3">
                              <div className="w-8 h-8 flex items-center justify-center shrink-0">
                                <img src={cat.image} className="w-8 h-8 object-contain" alt={cat.name} />
                              </div>
                              <span className="text-[12px] font-medium text-content">{cat.name}</span>
                            </div>
                            <ChevronRight className={`w-4 h-4 text-content-muted transition-transform ${expandedCategory === cat.name ? 'rotate-90 opacity-100' : 'opacity-40'}`} />
                          </div>
                          
                          <AnimatePresence initial={false}>
                            {expandedCategory === cat.name && (
                              <motion.div 
                                layout
                                initial={{ height: 0, opacity: 0 }}
                                animate={{ height: "auto", opacity: 1 }}
                                exit={{ height: 0, opacity: 0 }}
                                transition={{ duration: 0.3, ease: [0.04, 0.62, 0.23, 0.98] }}
                                className="overflow-hidden origin-top"
                              >
                                <div className="pl-[34px] py-1 bg-surface-muted/30">
                                  {cat.subCats?.map((sub, j) => (
                                    <div key={j} onClick={() => setSelectedSubCategory(sub)} className="flex items-center gap-2.5 py-2 px-1 border-b border-border-subtle/50 last:border-0 cursor-pointer active:opacity-70 group">
                                      <div className="w-5 h-5 flex items-center justify-center shrink-0">
                                        <img src={cat.image} className="w-4 h-4 object-contain opacity-70 group-hover:opacity-100 transition-opacity" alt={sub} />
                                      </div>
                                      <span className="text-[10px] text-content-muted tracking-tight group-hover:text-content transition-colors flex-1">{sub}</span>
                                      <ChevronRight className="w-3.5 h-3.5 text-border-strong opacity-40 group-hover:opacity-100 group-hover:text-[#F97316]" strokeWidth={2} />
                                    </div>
                                  ))}
                                </div>
                              </motion.div>
                            )}
                          </AnimatePresence>
                        </motion.div>
                      ))}
                      </motion.div>
                    </>
                  )}
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </main>

        {/* Filter Modal (Uzman) */}
        <AnimatePresence>
          {isUzmanFilterOpen && (
            <>
              <motion.div 
                initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
                onClick={() => setIsUzmanFilterOpen(false)}
                className="fixed inset-0 bg-black/40 z-[50] backdrop-blur-sm"
              />
              <motion.div 
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.95 }}
                transition={{ duration: 0.15 }}
                className="fixed inset-0 m-auto w-[90%] max-w-sm h-max bg-surface z-[60] rounded-2xl shadow-2xl flex justify-center border border-border-subtle"
              >
                <div className="w-full bg-surface rounded-2xl p-4 flex flex-col">
                  
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-[14px] font-semibold tracking-tight text-content">Gelişmiş Filtre</h3>
                    <button onClick={() => setIsUzmanFilterOpen(false)} className="text-[10px] font-medium text-[#F97316] hover:opacity-80">
                      Uygula
                    </button>
                  </div>

                  <div className="space-y-4">
                    {/* Filter Gender */}
                    <div className="flex flex-col gap-1.5">
                      <label className="text-[10px] font-semibold text-content-muted uppercase tracking-wider">Cinsiyet</label>
                      <div className="flex flex-wrap gap-2">
                        {["Tümü", "Kadın", "Erkek"].map(t => (
                          <button 
                            key={t}
                            onClick={() => setUzmanFilterGender(t)}
                            className={`px-3 py-1.5 rounded-full text-[10px] font-medium transition-all ${uzmanFilterGender === t ? 'bg-[#F97316]/10 text-[#F97316] border border-[#F97316]/30 shadow-sm' : 'bg-surface border border-border-subtle text-content-muted hover:border-border-muted hover:bg-surface-muted'}`}
                          >
                            {t}
                          </button>
                        ))}
                      </div>
                    </div>

                    {/* Filter Rating */}
                    <div className="flex flex-col gap-1.5">
                      <label className="text-[10px] font-semibold text-content-muted uppercase tracking-wider">Puanlama</label>
                      <div className="flex flex-wrap gap-2">
                        {["Tümü", "4 Yıldız ve Üzeri", "Bölgenin En İyileri"].map(t => (
                          <button 
                            key={t}
                            onClick={() => setUzmanFilterRating(t)}
                            className={`px-3 py-1.5 rounded-full text-[10px] font-medium transition-all ${uzmanFilterRating === t ? 'bg-[#F97316]/10 text-[#F97316] border border-[#F97316]/30 shadow-sm' : 'bg-surface border border-border-subtle text-content-muted hover:border-border-muted hover:bg-surface-muted'}`}
                          >
                            {t}
                          </button>
                        ))}
                      </div>
                    </div>

                    {/* Filter Price */}
                    <div className="flex flex-col gap-1.5">
                      <label className="text-[10px] font-semibold text-content-muted uppercase tracking-wider">Fiyat</label>
                      <div className="flex flex-wrap gap-2">
                        {["Tümü", "Orta Segment", "Uygun Fiyatlı", "Özel Hizmet"].map(t => (
                          <button 
                            key={t}
                            onClick={() => setUzmanFilterPrice(t)}
                            className={`px-3 py-1.5 rounded-full text-[10px] font-medium transition-all ${uzmanFilterPrice === t ? 'bg-[#F97316]/10 text-[#F97316] border border-[#F97316]/30 shadow-sm' : 'bg-surface border border-border-subtle text-content-muted hover:border-border-muted hover:bg-surface-muted'}`}
                          >
                            {t}
                          </button>
                        ))}
                      </div>
                    </div>

                  </div>
                  
                </div>
              </motion.div>
            </>
          )}
        </AnimatePresence>

        {/* Sort Modal (Uzman) */}
        <AnimatePresence>
          {isUzmanSortOpen && (
            <>
              <motion.div 
                initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
                onClick={() => setIsUzmanSortOpen(false)}
                className="fixed inset-0 bg-black/40 z-[50] backdrop-blur-sm"
              />
              <motion.div 
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.95 }}
                transition={{ duration: 0.15 }}
                className="fixed inset-0 m-auto w-[90%] max-w-sm h-max bg-surface z-[60] rounded-2xl shadow-2xl flex justify-center border border-border-subtle"
              >
                <div className="w-full bg-surface rounded-2xl p-4 flex flex-col">
                  
                  <div className="flex items-center justify-between mb-3">
                    <h3 className="text-[14px] font-semibold tracking-tight text-content">Sıralama</h3>
                  </div>

                  <div className="flex flex-col gap-0.5">
                    {[
                      "Varsayılan", 
                      "En Yakınlar Önce", 
                      "Puana Göre (Azalan)", 
                      "Fiyata Göre (Artan)", 
                      "Yorum Sayısına Göre"
                    ].map(option => (
                      <button 
                        key={option}
                        onClick={() => {
                          setUzmanSort(option);
                          setIsUzmanSortOpen(false);
                        }}
                        className="flex items-center justify-between p-2.5 rounded-xl hover:bg-surface-muted transition-colors"
                      >
                        <span className={`text-[10px] font-medium ${uzmanSort === option ? 'text-[#F97316]' : 'text-content'}`}>
                          {option}
                        </span>
                        {uzmanSort === option && (
                          <div className="w-3.5 h-3.5 rounded-full border-[3px] border-[#F97316] bg-transparent" />
                        )}
                        {uzmanSort !== option && (
                          <div className="w-3.5 h-3.5 rounded-full border border-border-muted" />
                        )}
                      </button>
                    ))}
                  </div>
                </div>
              </motion.div>
            </>
          )}
        </AnimatePresence>

        {/* Floating Action Badge (Uzman Mode) */}
        <AnimatePresence>
          {mode === 'uzman' && isUzmanBadgeVisible && (
            <motion.div
              initial={{ x: "-100vw", y: 0, opacity: 0 }}
              animate={{ x: "-50%", y: 0, opacity: 1 }}
              exit={{ x: "100vw", opacity: 0 }}
              transition={{ 
                type: "spring", 
                damping: 25, 
                stiffness: 60,
                exit: { duration: 0.6, ease: "anticipate" }
              }}
              className="fixed bottom-[88px] left-1/2 z-50 w-max max-w-[90%]"
            >
              <div className="relative shadow-lg rounded-[20px] bg-[#14281c] border-none p-1 overflow-hidden flex items-center pl-2">
                <button 
                  onClick={() => setIsPublishing(true)}
                  className="flex-1 flex items-center cursor-pointer text-left group"
                >
                  <img src={megaphoneImg} alt="pop" className="w-[32px] object-contain shrink-0 drop-shadow-sm mr-2" />
                  <span className="text-white text-[10px] font-semibold leading-[1.3] pr-2">
                    İlan aç, teklifleri topla!
                  </span>
                </button>
                <button 
                  onClick={(e) => {
                    e.stopPropagation();
                    setIsUzmanBadgeVisible(false);
                  }}
                  className="w-6 h-6 shrink-0 flex items-center justify-center rounded-full bg-white/10 text-white/90 hover:text-white hover:bg-white/20 transition-colors ml-1 mr-1 z-10"
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              </div>
            </motion.div>
          )}
        </AnimatePresence>

        {/* Bottom Tab Bar (Fixed at the bottom) */}
        <BottomNavigation 
          activeTab="home" 
          onTabChange={(tab) => {
            if (tab === 'jobs') onJobs();
            else if (tab === 'messages') onChat();
            else if (tab === 'profile') onProfile();
            else if (tab === 'publish') setIsPublishing(true);
          }} 
        />
      </div>
    </div>
  );
}
