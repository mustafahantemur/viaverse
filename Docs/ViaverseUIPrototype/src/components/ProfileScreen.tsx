import React, { useState } from "react";
import { User, Settings, CreditCard, RefreshCw, ShieldCheck, Lock, Star, Heart, ChevronLeft, ChevronRight, ChevronDown, Camera, CheckCircle2, Fingerprint, Smartphone, Laptop, Key, Eye, EyeOff, ShieldAlert, Monitor, LogOut, Plus, Wallet, ReceiptText, ArrowDownToLine, Landmark, Trash2, Clock, Check, BuildingIcon, FileText, Bell, Moon, HelpCircle, UserX, Sun, Globe, MapPin } from "lucide-react";
import { motion, AnimatePresence } from "motion/react";
import BottomNavigation from "./BottomNavigation";

const SECTIONS = [
  {
    title: "Hesap ve Kimlik Bilgileri",
    icon: <User className="w-3.5 h-3.5 shrink-0" strokeWidth={1.5} />,
    items: [
      { label: "Ad Soyad ve Profil Fotoğrafı", id: "name_photo" },
      { label: "Konum ve Görünürlük", id: "location" }
    ]
  },
  {
    title: "Güvenlik ve Doğrulama",
    icon: <ShieldCheck className="w-3.5 h-3.5 shrink-0" strokeWidth={1.5} />,
    items: [
      { label: "Telefon Numarası Doğrulama", id: "phone" },
      { label: "E-posta Adresi Doğrulama", id: "email" },
      { label: "E-Devlet ile Kimlik Onayı", id: "edevlet" },
      { label: "İki Faktörlü Doğrulama Ayarları", id: "2fa" },
      { label: "Giriş Yapılan Cihazların Yönetimi", id: "devices" },
      { label: "Şifre Değiştirme ve Hesap Güvenliği", id: "password" }
    ]
  },
  {
    title: "Gizlilik ve Veri Yönetimi",
    icon: <Lock className="w-3.5 h-3.5 shrink-0" strokeWidth={1.5} />,
    items: [
      { label: "Kişisel Verilerin Korunması ve KVKK Tercihleri", id: "kvkk" }
    ]
  },
  {
    title: "Cüzdan ve Finansal İşlemler",
    icon: <CreditCard className="w-3.5 h-3.5 shrink-0" strokeWidth={1.5} />,
    items: [
      { label: "Kayıtlı Kredi ve Banka Kartları", id: "cards" },
      { label: "Yerime Güvenli Ödeme Bakiyesi", id: "escrow" },
      { label: "İşlem Geçmişi ve Makbuzlar", id: "receipts" },
      { label: "Fatura ve Vergi Bilgileri Yönetimi", id: "tax" }
    ]
  },
  {
    title: "Tercihler ve Teknik Destek",
    icon: <Settings className="w-3.5 h-3.5 shrink-0" strokeWidth={1.5} />,
    items: [
      { label: "Bildirim ve Duyuru Tercihleri", id: "notifications" },
      { label: "Uygulama Teması ve Dil Seçenekleri", id: "theme" },
      { label: "Yardım Merkezi ve Canlı Destek Erişimi", id: "support" },
      { label: "Engellenen Kullanıcılar Listesi", id: "blocked" }
    ]
  }
];

function LocationDistanceScreen({ onBack }: { onBack: () => void }) {
  const [radius, setRadius] = useState(2);
  const [selectedCity, setSelectedCity] = useState("İstanbul");
  const [selectedDistrict, setSelectedDistrict] = useState("Kadıköy, Caferağa");

  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-12">
        <div className="relative text-center pb-8 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5 border-b border-border-subtle">
          <button onClick={onBack} className="absolute left-6 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-4 h-4" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Konum ve Görünürlük</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col pt-6 overflow-y-auto pb-24">
          <p className="text-[10px] text-content-muted mb-8 leading-[1.5]">
            Çevrenizdeki ilanları ve komşularınızı görmek için size uygun mesafeyi ayarlayın.
          </p>

          <div className="flex items-center justify-between mb-3">
            <h2 className="text-[12px] font-bold text-content">Konum</h2>
            <button className="text-[10px] flex items-center font-bold text-[#F97316] hover:bg-[#F97316]/10 px-2 py-1 rounded-md transition-colors" onClick={() => {
              if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(() => {
                  setSelectedCity("İstanbul");
                  setSelectedDistrict("Kadıköy, Caferağa");
                });
              }
            }}>
              <MapPin className="w-3 h-3 mr-1" />
              Mevcut Konumu Kullan
            </button>
          </div>
          <div className="flex flex-col mb-8">
            <input 
              type="text" 
              placeholder="Şehir (Örn: İstanbul)"
              value={selectedCity}
              onChange={(e) => setSelectedCity(e.target.value)}
              className="w-full px-4 rounded-none border border-border-muted text-content placeholder-content-muted focus:outline-none focus:border-content focus:ring-1 focus:ring-content transition-colors z-0 focus:z-10 relative bg-surface"
              style={{ fontSize: "13px", lineHeight: "8px", height: "40px" }}
            />
            <input 
              type="text" 
              placeholder="İlçe / Semt (Örn: Kadıköy, Caferağa)"
              value={selectedDistrict}
              onChange={(e) => setSelectedDistrict(e.target.value)}
              className="w-full px-4 rounded-none border border-border-muted text-content placeholder-content-muted focus:outline-none focus:border-content focus:ring-1 focus:ring-content transition-colors z-0 focus:z-10 relative -mt-[1px] bg-surface"
              style={{ fontSize: "13px", lineHeight: "8px", height: "40px" }}
            />
          </div>

          <div className="mb-8">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-[12px] font-bold text-content">Görünürlük Mesafesi</h2>
              <span className="text-[12px] font-bold text-[#F97316]">{radius} km</span>
            </div>
            <input 
              type="range" 
              min="1" 
              max="50" 
              step="1"
              value={radius}
              onChange={(e) => setRadius(Number(e.target.value))}
              className="w-full h-2 bg-border rounded-lg appearance-none cursor-pointer accent-[#F97316]"
            />
            <div className="flex justify-between text-[10px] text-content-muted mt-2 font-medium">
              <span>Sadece Yakın Çevrem (1km)</span>
              <span>Geniş Çevre (50km)</span>
            </div>
          </div>
          
          <button className="w-full bg-[#F97316] text-white rounded-xl py-3 text-[12px] font-bold mt-4 shadow-md active:scale-95 transition-transform" onClick={onBack}>
            Kaydet
          </button>
        </div>
      </div>
    </div>
  );
}

export default function ProfileScreen({ onBack, onHome, onJobs, onMessages, onOpenPublicProfile, onLogout, onProviderWelcome }: { onBack?: () => void, onHome?: () => void, onJobs?: () => void, onMessages?: () => void, onOpenPublicProfile?: (user: any) => void, onLogout?: () => void, onProviderWelcome?: () => void }) {
  const [activeSubScreen, setActiveSubScreen] = useState<string | null>(null);

  if (activeSubScreen === "location") {
    return <LocationDistanceScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "name_photo") {
    return <NamePhotoScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "phone") {
    return <PhoneScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "email") {
    return <EmailScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "edevlet") {
    return <EDevletScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "2fa") {
    return <TwoFactorScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "devices") {
    return <DevicesScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "password") {
    return <PasswordScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "cards") {
    return <CardsScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "escrow") {
    return <EscrowScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "receipts") {
    return <ReceiptsScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "tax") {
    return <TaxScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "kvkk") {
    return <KvkkScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "notifications") {
    return <NotificationsScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "theme") {
    return <ThemeLanguageScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "support") {
    return <SupportScreen onBack={() => setActiveSubScreen(null)} />;
  }
  if (activeSubScreen === "blocked") {
    return <BlockedScreen onBack={() => setActiveSubScreen(null)} />;
  }

  const currentUser = { name: "Zehra Erdoğan", img: "https://i.pravatar.cc/150?img=47", distance: "", joinedDate: "Ekim 2023", rating: 4.8, reviewsCount: 32, posts: [] };

  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-12">
        {/* Header */}
        <div className="relative text-center pb-8 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          {onBack && (
            <button 
              onClick={onBack}
              className="absolute left-6 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1"
            >
              <ChevronLeft className="w-3.5 h-3.5" strokeWidth={1.5} />
            </button>
          )}
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Ayarlar</h1>
        </div>

        <div className="flex-1 overflow-y-auto px-5 pb-12 scrollbar-hide">
          {/* Profile Card */}
          <div className="flex items-center justify-between mb-4">
            <div>
              <p className="text-content-muted text-[10px] mb-0.5">Merhaba,</p>
              <h2 className="text-[10px] font-bold text-content tracking-tight">Zehra Erdoğan</h2>
              <button 
                onClick={() => onOpenPublicProfile?.(currentUser)}
                className="mt-1 flex items-center text-[#F97316] hover:text-[#F97316]/80 transition-colors"
              >
                <span className="text-[10px] font-semibold mr-1">Profili Görüntüle</span>
                <ChevronRight className="w-3 h-3" />
              </button>
            </div>
            <div className="w-[64px] h-[64px] rounded-full bg-border overflow-hidden ring-1 ring-border shadow-sm">
              <img 
                src="https://i.pravatar.cc/150?img=47" 
                alt="Profile" 
                className="w-full h-full object-cover"
              />
            </div>
          </div>

          <div className="mb-4">
            <button 
              onClick={onProviderWelcome}
              className="flex items-center w-full bg-[#10B981] hover:bg-[#059669] text-white rounded-[16px] py-2.5 px-3 transition-colors shadow-sm active:scale-[0.98]"
            >
              <div className="mr-3">
                <RefreshCw className="w-3.5 h-3.5" strokeWidth={2} />
              </div>
              <div className="flex-1 text-left">
                <h3 className="text-[10px] font-bold tracking-tight">Hizmet veren profiline geç</h3>
                <p className="text-[10px] text-white/80 mt-0.5 leading-snug tracking-tight">Gelen talepleri ve işlerini yönet.</p>
              </div>
              <div className="opacity-70">
                <ChevronRight className="w-3.5 h-3.5" strokeWidth={2} />
              </div>
            </button>
          </div>

           {/* Menu Items */}
           <div className="flex flex-col space-y-0 text-content mb-6">
            {SECTIONS.map((section, idx) => (
              <MenuAccordion 
                key={idx}
                icon={section.icon}
                title={section.title}
                items={section.items}
                onItemClick={(id) => setActiveSubScreen(id)}
              />
            ))}
          </div>

          <button 
            onClick={onLogout}
            className="w-full flex items-center justify-center gap-2 py-3 mt-4 rounded-xl text-white bg-[#F97316] hover:bg-[#EA580C] shadow-md transition-colors active:scale-[0.98]"
          >
            <LogOut className="w-4 h-4" strokeWidth={2} />
            <span className="text-[12px] font-bold tracking-tight">Çıkış Yap</span>
          </button>
        </div>

        <BottomNavigation 
          activeTab="profile" 
          onTabChange={(tab) => {
            if (tab === 'home') onHome?.();
            else if (tab === 'jobs') onJobs?.();
            else if (tab === 'messages') onMessages?.();
          }} 
        />
      </div>
    </div>
  );
}

const MenuAccordion: React.FC<{ icon: React.ReactNode, title: string, items: {label: string, id: string}[], onItemClick: (id: string) => void }> = ({ icon, title, items, onItemClick }) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="border-b border-border-subtle last:border-b-0">
      <button 
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center w-full text-left py-2.5 hover:bg-surface-hover/50 transition-colors active:bg-surface-hover group"
      >
        <div className="text-content mr-3 group-hover:text-[#F97316] transition-colors stroke-1">
          {icon}
        </div>
        <div className="flex-1 pr-3">
          <h3 className="text-[12px] font-semibold text-content transition-colors tracking-tight">
            {title}
          </h3>
        </div>
        <div className="text-content-muted/40 group-hover:text-content-muted transition-colors">
          {isOpen ? <ChevronDown className="w-3.5 h-3.5" strokeWidth={1.5} /> : <ChevronRight className="w-3.5 h-3.5" strokeWidth={1.5} />}
        </div>
      </button>
      <AnimatePresence initial={false}>
        {isOpen && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.2 }}
            className="overflow-hidden"
          >
            <div className="pb-4 pl-[44px] pr-4">
              <ul className="space-y-3">
                {items.map((item, idx) => (
                  <li 
                    key={idx} 
                    onClick={() => onItemClick(item.id)}
                    className="text-[10px] font-medium text-content-muted tracking-tight flex items-center group/item cursor-pointer hover:text-content transition-colors py-1.5 mt-0.5"
                  >
                    <span className="w-1 h-1 rounded-full bg-border-strong mr-2.5 shrink-0 group-hover/item:bg-[#F97316] transition-colors" />
                    <span className="leading-[1.4] line-clamp-2">{item.label}</span>
                  </li>
                ))}
              </ul>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

// Alt Ekranlar

function NamePhotoScreen({ onBack }: { onBack: () => void }) {
  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-12">
        <div className="relative text-center pb-8 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={onBack} className="absolute left-6 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-3.5 h-3.5" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Ad Soyad ve Profil Fotoğrafı</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col items-center">
          <div className="relative mb-4 mt-4">
            <div className="w-[100px] h-[100px] rounded-full bg-surface-muted border-2 border-border overflow-hidden">
              <img src="https://i.pravatar.cc/150?img=47" alt="Profile" className="w-full h-full object-cover" />
            </div>
            <button className="absolute bottom-0 right-0 w-8 h-8 bg-[#F97316] text-white rounded-full flex items-center justify-center border-2 border-surface shadow-sm active:scale-95 transition-transform">
              <Camera className="w-3.5 h-3.5" strokeWidth={2} />
            </button>
          </div>
          
          <div className="w-full space-y-4">
            <div className="flex flex-col">
              <label className="text-[10px] font-medium text-content-muted mb-1.5 ml-1">Adınız</label>
              <input type="text" defaultValue="Zehra" className="w-full bg-surface text-content text-[10px] px-3 py-2.5.5 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all" />
            </div>
            <div className="flex flex-col">
              <label className="text-[10px] font-medium text-content-muted mb-1.5 ml-1">Soyadınız</label>
              <input type="text" defaultValue="Erdoğan" className="w-full bg-surface text-content text-[10px] px-3 py-2.5.5 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all" />
            </div>
          </div>
          
          <div className="mt-auto mb-4 w-full pt-8">
            <button className="w-full bg-[#F97316] text-white rounded-full py-2.5 text-[10px] font-bold shadow-md active:scale-[0.98] transition-transform">
              Değişiklikleri Kaydet
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

function PhoneScreen({ onBack }: { onBack: () => void }) {
  const [step, setStep] = useState<"unverified" | "otp" | "verified" | "change">("unverified");
  const [phone, setPhone] = useState("");
  const [otp, setOtp] = useState(["", "", "", "", "", ""]);

  const handleOtpChange = (index: number, value: string) => {
    if (value.length > 1) value = value.slice(-1);
    if (!/^\d*$/.test(value)) return;
    
    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);

    // simple auto-focus logic can be added, but for now just update state
    if (value && index < 5) {
      const nextInput = document.getElementById(`otp-${index + 1}`);
      nextInput?.focus();
    }
  };

  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-12">
        <div className="relative text-center pb-8 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={() => {
            if (step === "otp") setStep("unverified");
            else if (step === "change") setStep("verified");
            else onBack();
          }} className="absolute left-6 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-3.5 h-3.5" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Telefon Numarası</h1>
        </div>

        <div className="flex-1 px-5 flex flex-col pt-4">
          <AnimatePresence mode="wait">
            {step === "unverified" && (
              <motion.div
                key="unverified"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="flex flex-col items-center flex-1"
              >
                <div className="w-16 h-16 bg-[#F97316]/10 rounded-full flex items-center justify-center mb-4">
                  <ShieldCheck className="w-8 h-8 text-[#F97316]" strokeWidth={2} />
                </div>
                <h2 className="text-[10px] font-bold text-content mb-2 text-center">Numaranız Doğrulanmamış</h2>
                <p className="text-[10px] text-content-muted mb-4 text-center px-3 leading-[1.5]">
                  Güvenliğiniz için lütfen telefon numaranızı ekleyip doğrulayın. İşlemlerinizde size bu numara üzerinden ulaşacağız.
                </p>

                <div className="w-full flex items-center space-x-3 mb-4">
                  <div className="bg-surface-muted border border-border-subtle rounded-[12px] px-3 py-2.5.5 text-[10px] text-content-muted font-medium">
                    +90
                  </div>
                  <input 
                    type="tel" 
                    placeholder="555 444 33 22" 
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    className="flex-1 bg-surface text-content text-[10px] px-3 py-2.5.5 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all" 
                  />
                </div>

                <div className="mt-auto mb-4 w-full">
                  <button 
                    onClick={() => setStep("otp")}
                    className="w-full bg-[#F97316] text-white rounded-full py-2.5 text-[10px] font-bold active:scale-[0.98] transition-transform shadow-md"
                  >
                    Kodu Gönder
                  </button>
                </div>
              </motion.div>
            )}

            {step === "otp" && (
              <motion.div
                key="otp"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="flex flex-col items-center flex-1"
              >
                <h2 className="text-[10px] font-bold text-content mb-2 mt-4">Kodu Girin</h2>
                <p className="text-[10px] text-content-muted mb-4 text-center leading-[1.5]">
                  <span className="font-semibold text-content">+90 {phone}</span> numarasına gönderdiğimiz 6 haneli doğrulama kodunu girin.
                </p>

                <div className="flex space-x-2 w-full justify-center mb-4">
                  {otp.map((digit, idx) => (
                    <input
                      key={idx}
                      id={`otp-${idx}`}
                      type="text"
                      inputMode="numeric"
                      value={digit}
                      onChange={(e) => handleOtpChange(idx, e.target.value)}
                      className="w-10 h-10 bg-surface text-center text-[10px] font-bold text-content rounded-[12px] border border-border-subtle focus:border-[#F97316] outline-none transition-colors"
                    />
                  ))}
                </div>

                <div className="text-center mb-4">
                  <p className="text-[10px] text-content-muted mb-2">Kodu almadınız mı?</p>
                  <button className="text-[10px] font-bold text-[#F97316]">Tekrar Gönder (00:59)</button>
                </div>

                <div className="mt-auto mb-4 w-full">
                  <button 
                    onClick={() => setStep("verified")}
                    className="w-full bg-[#F97316] text-white rounded-full py-2.5 text-[10px] font-bold active:scale-[0.98] transition-transform shadow-md"
                  >
                    Doğrula
                  </button>
                </div>
              </motion.div>
            )}

            {step === "verified" && (
              <motion.div
                key="verified"
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                className="flex flex-col items-center flex-1 pt-8"
              >
                <div className="w-16 h-16 bg-[#10B981]/10 rounded-full flex items-center justify-center mb-4">
                  <CheckCircle2 className="w-8 h-8 text-[#10B981]" strokeWidth={2} />
                </div>
                <h2 className="text-[10px] font-bold text-content mb-1">Doğrulanmış Numara</h2>
                <p className="text-[10px] text-content-muted mb-10">
                  {phone ? `+90 ${phone}` : "0555 *** ** 99"}
                </p>
                
                <div className="w-full p-3 border border-border-subtle rounded-[16px] bg-surface-muted/50 mb-4">
                  <p className="text-[10px] text-content-muted leading-[1.5] text-center">
                    Telefon numaranız hizmet sağlayanlarla iletişim kurmanız ve hesabınızın güvenliği için gereklidir.
                  </p>
                </div>

                <div className="mt-auto mb-4 w-full">
                  <button 
                    onClick={() => {
                        setStep("change");
                        setPhone("");
                    }}
                    className="w-full bg-surface border border-border-strong text-content rounded-full py-2.5 text-[10px] font-bold active:scale-[0.98] transition-transform hover:bg-surface-hover"
                  >
                    Numaramı Değiştir
                  </button>
                </div>
              </motion.div>
            )}

            {step === "change" && (
              <motion.div
                key="change"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="flex flex-col items-center flex-1"
              >
                <h2 className="text-[10px] font-bold text-content mb-2 text-center mt-4">Yeni Numara Ekle</h2>
                <p className="text-[10px] text-content-muted mb-4 text-center px-3 leading-[1.5]">
                  Eski doğrulamanız geçersiz olacaktır ve yeni numaranızı doğrulamanız gerekecektir.
                </p>

                <div className="w-full flex items-center space-x-3 mb-4">
                  <div className="bg-surface-muted border border-border-subtle rounded-[12px] px-3 py-2.5.5 text-[10px] text-content-muted font-medium">
                    +90
                  </div>
                  <input 
                    type="tel" 
                    placeholder="Yeni numaranızı girin" 
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    className="flex-1 bg-surface text-content text-[10px] px-3 py-2.5.5 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all" 
                  />
                </div>

                <div className="mt-auto mb-4 w-full">
                  <button 
                    onClick={() => {
                        setOtp(["", "", "", "", "", ""]);
                        setStep("otp");
                    }}
                    className="w-full bg-[#F97316] text-white rounded-full py-2.5 text-[10px] font-bold active:scale-[0.98] transition-transform shadow-md"
                  >
                    Kodu Gönder
                  </button>
                </div>
              </motion.div>
            )}

          </AnimatePresence>
        </div>
      </div>
    </div>
  );
}

function EmailScreen({ onBack }: { onBack: () => void }) {
  const [step, setStep] = useState<"unverified" | "otp" | "verified" | "change">("unverified");
  const [email, setEmail] = useState("");
  const [otp, setOtp] = useState(["", "", "", "", "", ""]);

  const handleOtpChange = (index: number, value: string) => {
    if (value.length > 1) value = value.slice(-1);
    if (!/^\d*$/.test(value)) return;
    
    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);

    if (value && index < 5) {
      const nextInput = document.getElementById(`email-otp-${index + 1}`);
      nextInput?.focus();
    }
  };

  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-12">
        <div className="relative text-center pb-8 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={() => {
            if (step === "otp") setStep("unverified");
            else if (step === "change") setStep("verified");
            else onBack();
          }} className="absolute left-6 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-3.5 h-3.5" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">E-posta Adresi</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col pt-4">
          <AnimatePresence mode="wait">
            {step === "unverified" && (
              <motion.div
                key="unverified"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="flex flex-col items-center flex-1"
              >
                <div className="w-16 h-16 bg-[#F97316]/10 rounded-full flex items-center justify-center mb-4">
                  <ShieldCheck className="w-8 h-8 text-[#F97316]" strokeWidth={2} />
                </div>
                <h2 className="text-[10px] font-bold text-content mb-2 text-center">E-posta Adresiniz Doğrulanmamış</h2>
                <p className="text-[10px] text-content-muted mb-4 text-center px-3 leading-[1.5]">
                  Güvenliğiniz, faturalandırma ve önemli bildirimler için lütfen e-posta adresinizi doğrulayın.
                </p>

                <div className="w-full mb-4">
                  <input 
                    type="email" 
                    placeholder="ornek@posta.com" 
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full bg-surface text-content text-[10px] px-3 py-2.5.5 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all" 
                  />
                </div>

                <div className="mt-auto mb-4 w-full">
                  <button 
                    onClick={() => setStep("otp")}
                    className="w-full bg-[#F97316] text-white rounded-full py-2.5 text-[10px] font-bold active:scale-[0.98] transition-transform shadow-md"
                  >
                    Kodu Gönder
                  </button>
                </div>
              </motion.div>
            )}

            {step === "otp" && (
              <motion.div
                key="otp"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="flex flex-col items-center flex-1"
              >
                <h2 className="text-[10px] font-bold text-content mb-2 mt-4">Kodu Girin</h2>
                <p className="text-[10px] text-content-muted mb-4 text-center leading-[1.5]">
                  <span className="font-semibold text-content">{email}</span> adresine gönderdiğimiz 6 haneli doğrulama kodunu girin. Lütfen spam klasörünü de kontrol edin.
                </p>

                <div className="flex space-x-2 w-full justify-center mb-4">
                  {otp.map((digit, idx) => (
                    <input
                      key={idx}
                      id={`email-otp-${idx}`}
                      type="text"
                      inputMode="numeric"
                      value={digit}
                      onChange={(e) => handleOtpChange(idx, e.target.value)}
                      className="w-10 h-10 bg-surface text-center text-[10px] font-bold text-content rounded-[12px] border border-border-subtle focus:border-[#F97316] outline-none transition-colors"
                    />
                  ))}
                </div>

                <div className="text-center mb-4">
                  <p className="text-[10px] text-content-muted mb-2">Kodu almadınız mı?</p>
                  <button className="text-[10px] font-bold text-[#F97316]">Tekrar Gönder (00:59)</button>
                </div>

                <div className="mt-auto mb-4 w-full">
                  <button 
                    onClick={() => setStep("verified")}
                    className="w-full bg-[#F97316] text-white rounded-full py-2.5 text-[10px] font-bold active:scale-[0.98] transition-transform shadow-md"
                  >
                    Doğrula
                  </button>
                </div>
              </motion.div>
            )}

            {step === "verified" && (
              <motion.div
                key="verified"
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                className="flex flex-col items-center flex-1 pt-8"
              >
                <div className="w-16 h-16 bg-[#10B981]/10 rounded-full flex items-center justify-center mb-4">
                  <CheckCircle2 className="w-8 h-8 text-[#10B981]" strokeWidth={2} />
                </div>
                <h2 className="text-[10px] font-bold text-content mb-1">Doğrulanmış E-posta</h2>
                <p className="text-[10px] text-content-muted mb-10">
                  {email || "z***.erdogan@gmail.com"}
                </p>
                
                <div className="w-full p-3 border border-border-subtle rounded-[16px] bg-surface-muted/50 mb-4">
                  <p className="text-[10px] text-content-muted leading-[1.5] text-center">
                    E-posta adresiniz faturalandırma ve önemli hesap bildirimleri için kullanılır.
                  </p>
                </div>

                <div className="mt-auto mb-4 w-full">
                  <button 
                    onClick={() => {
                        setStep("change");
                        setEmail("");
                    }}
                    className="w-full bg-surface border border-border-strong text-content rounded-full py-2.5 text-[10px] font-bold active:scale-[0.98] transition-transform hover:bg-surface-hover"
                  >
                    E-posta Adresini Değiştir
                  </button>
                </div>
              </motion.div>
            )}

            {step === "change" && (
              <motion.div
                key="change"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="flex flex-col items-center flex-1"
              >
                <h2 className="text-[10px] font-bold text-content mb-2 text-center mt-4">Yeni E-posta Ekle</h2>
                <p className="text-[10px] text-content-muted mb-4 text-center px-3 leading-[1.5]">
                  Eski doğrulamanız geçersiz olacaktır ve yeni e-posta adresinizi doğrulamanız gerekecektir.
                </p>

                <div className="w-full mb-4">
                  <input 
                    type="email" 
                    placeholder="Yeni e-posta adresinizi girin" 
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full bg-surface text-content text-[10px] px-3 py-2.5.5 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all" 
                  />
                </div>

                <div className="mt-auto mb-4 w-full">
                  <button 
                    onClick={() => {
                        setOtp(["", "", "", "", "", ""]);
                        setStep("otp");
                    }}
                    className="w-full bg-[#F97316] text-white rounded-full py-2.5 text-[10px] font-bold active:scale-[0.98] transition-transform shadow-md"
                  >
                    Kodu Gönder
                  </button>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </div>
  );
}

function EDevletScreen({ onBack }: { onBack: () => void }) {
  const [verified, setVerified] = useState(false);

  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-12">
        <div className="relative text-center pb-8 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={onBack} className="absolute left-6 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-3.5 h-3.5" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">E-Devlet Onayı</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col items-center pt-8">
          <div className="w-16 h-16 bg-[#10B981]/10 rounded-full flex items-center justify-center mb-4">
            {verified ? <CheckCircle2 className="w-8 h-8 text-[#10B981]" strokeWidth={2} /> : <ShieldAlert className="w-8 h-8 text-[#F97316]" strokeWidth={2} />}
          </div>
          <h2 className="text-[10px] font-bold text-content mb-1">
            {verified ? "E-Devlet Onaylı Profil" : "Kimliğiniz Doğrulanmadı"}
          </h2>
          <p className="text-[10px] text-content-muted mb-10 text-center leading-[1.5]">
            {verified 
              ? "Profilinizde E-Devlet onay rozeti görünmektedir. Bu sayede hizmet alan/veren kişilerle daha güvenli işlem yapabilirsiniz."
              : "Güvenilir bir profil oluşturmak ve \"E-Devlet Onaylı\" rozeti almak için kimliğinizi doğrulayın."}
          </p>

          {!verified && (
            <div className="w-full p-3 border border-border-subtle rounded-[16px] bg-surface-muted/50 mb-4">
              <ul className="space-y-3">
                <li className="flex items-start text-[10px] text-content-muted leading-[1.5]">
                  <span className="w-1.5 h-1.5 rounded-full bg-[#F97316] mt-1.5 mr-2 shrink-0" />
                  Bilgileriniz sadece doğrulama amacıyla kullanılır, üçüncü şahıslarla paylaşılmaz.
                </li>
                <li className="flex items-start text-[10px] text-content-muted leading-[1.5]">
                  <span className="w-1.5 h-1.5 rounded-full bg-[#F97316] mt-1.5 mr-2 shrink-0" />
                  Sistem sizi E-Devlet kapısına yönlendirecektir.
                </li>
              </ul>
            </div>
          )}

          <div className="mt-auto mb-4 w-full">
            {!verified ? (
              <button 
                onClick={() => setVerified(true)}
                className="w-full bg-[#F97316] text-white rounded-full py-2.5 text-[10px] font-bold active:scale-[0.98] transition-transform shadow-md"
              >
                E-Devlet ile Doğrula
              </button>
            ) : (
              <button className="w-full bg-surface border border-border-strong text-[#EF4444] rounded-full py-2.5 text-[10px] font-bold active:scale-[0.98] transition-transform hover:bg-[#EF4444]/5">
                Onayı Kaldır
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

function TwoFactorScreen({ onBack }: { onBack: () => void }) {
  const [enabled, setEnabled] = useState(false);

  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-12">
        <div className="relative text-center pb-8 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={onBack} className="absolute left-6 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-3.5 h-3.5" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">İki Faktörlü Doğrulama</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col pt-4">
          <div className="w-full p-3 border border-border-subtle rounded-[16px] bg-surface mb-4 flex justify-between items-center shadow-sm">
            <div className="flex flex-col pr-4">
              <span className="text-[10px] font-bold text-content mb-1">SMS ile Doğrulama (2FA)</span>
              <span className="text-[10px] text-content-muted leading-[1.4]">Giriş yaparken şifrenize ek olarak telefonunuza SMS kodu gönderilir.</span>
            </div>
            <div 
              onClick={() => setEnabled(!enabled)}
              className={`w-10 h-6 flex items-center rounded-full p-1 cursor-pointer transition-colors ${enabled ? "bg-[#10B981]" : "bg-border-strong"}`}
            >
              <div className={`bg-white w-3.5 h-3.5 rounded-full shadow-md transform transition-transform ${enabled ? "translate-x-6" : ""}`} />
            </div>
          </div>
          
          <div className="w-full p-3 rounded-[12px] bg-surface-muted/50 border border-border-subtle flex items-start">
            <ShieldAlert className="w-3.5 h-3.5 text-content-muted shrink-0 mr-3 mt-0.5" strokeWidth={1.5} />
            <p className="text-[10px] text-content-muted leading-[1.5]">
              Hesabınızın güvenliğini artırmak için iki faktörlü doğrulamayı etkinleştirmenizi öneririz. Bu özellik, şifreniz çalınsa bile hesabınıza izinsiz girişi engeller.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

function DevicesScreen({ onBack }: { onBack: () => void }) {
  const devices = [
    { id: 1, name: "iPhone 14 Pro", location: "İstanbul, TR", time: "Şu an aktif", current: true, icon: <Smartphone className="w-3.5 h-3.5" /> },
    { id: 2, name: "MacBook Air M2", location: "İstanbul, TR", time: "2 saat önce", current: false, icon: <Laptop className="w-3.5 h-3.5" /> },
    { id: 3, name: "Chrome - Windows", location: "Ankara, TR", time: "Dün, 14:30", current: false, icon: <Monitor className="w-3.5 h-3.5" /> },
  ];

  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-12">
        <div className="relative text-center pb-8 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={onBack} className="absolute left-6 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-3.5 h-3.5" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Cihaz Yönetimi</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col pt-4">
          <p className="text-[10px] text-content-muted mb-4 leading-[1.5]">
            Hesabınıza şu anda giriş yapılmış cihazlar aşağıdadır. Tanımadığınız bir cihaz varsa oturumunu kapatın.
          </p>

          <div className="space-y-4">
            {devices.map(dev => (
              <div key={dev.id} className="flex items-center justify-between p-3 border border-border-subtle rounded-[16px] bg-surface shadow-sm">
                <div className="flex items-center space-x-4">
                  <div className={`w-10 h-10 rounded-full flex items-center justify-center ${dev.current ? "bg-[#10B981]/10 text-[#10B981]" : "bg-surface-muted text-content-muted"}`}>
                    {dev.icon}
                  </div>
                  <div className="flex flex-col">
                    <span className="text-[10px] font-bold text-content leading-tight mb-1">{dev.name}</span>
                    <span className="text-[10px] text-content-muted">{dev.location} &bull; {dev.time}</span>
                  </div>
                </div>
                {!dev.current && (
                  <button className="text-[#EF4444] p-2 hover:bg-[#EF4444]/10 rounded-full transition-colors">
                    <LogOut className="w-3.5 h-3.5" strokeWidth={2} />
                  </button>
                )}
              </div>
            ))}
          </div>

          <div className="mt-12 w-full">
             <button className="w-full bg-surface border border-border-strong text-content rounded-full py-2.5 text-[10px] font-bold active:scale-[0.98] transition-transform hover:bg-surface-hover">
              Diğer Tüm Cihazlardan Çıkış Yap
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

function PasswordScreen({ onBack }: { onBack: () => void }) {
  const [showOld, setShowOld] = useState(false);
  const [showNew, setShowNew] = useState(false);
  
  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-12">
        <div className="relative text-center pb-8 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={onBack} className="absolute left-6 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-3.5 h-3.5" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Şifre Değiştir</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col pt-4">
          <div className="space-y-5">
            <div className="flex flex-col relative">
              <label className="text-[10px] font-medium text-content-muted mb-1.5 ml-1">Mevcut Şifreniz</label>
              <input 
                type={showOld ? "text" : "password"} 
                placeholder="********"
                className="w-full bg-surface text-content text-[10px] px-3 py-2.5.5 pr-12 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all" 
              />
              <button 
                type="button"
                onClick={() => setShowOld(!showOld)}
                className="absolute right-4 top-[26px] text-content-muted hover:text-content"
              >
                {showOld ? <EyeOff className="w-3.5 h-3.5" /> : <Eye className="w-3.5 h-3.5" />}
              </button>
            </div>
            
            <div className="h-px w-full bg-border-subtle my-2" />

            <div className="flex flex-col relative">
              <label className="text-[10px] font-medium text-content-muted mb-1.5 ml-1">Yeni Şifreniz</label>
              <input 
                type={showNew ? "text" : "password"} 
                placeholder="En az 8 karakter"
                className="w-full bg-surface text-content text-[10px] px-3 py-2.5.5 pr-12 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all" 
              />
              <button 
                type="button"
                onClick={() => setShowNew(!showNew)}
                className="absolute right-4 top-[26px] text-content-muted hover:text-content"
              >
                {showNew ? <EyeOff className="w-3.5 h-3.5" /> : <Eye className="w-3.5 h-3.5" />}
              </button>
            </div>

            <div className="flex flex-col relative">
              <label className="text-[10px] font-medium text-content-muted mb-1.5 ml-1">Yeni Şifreniz (Tekrar)</label>
              <input 
                type={showNew ? "text" : "password"} 
                placeholder="En az 8 karakter"
                className="w-full bg-surface text-content text-[10px] px-3 py-2.5.5 pr-12 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all" 
              />
            </div>
          </div>

          <div className="mt-auto mb-4 w-full pt-8">
            <button className="w-full bg-[#F97316] text-white rounded-full py-2.5 text-[10px] font-bold shadow-md active:scale-[0.98] transition-transform">
              Şifreyi Güncelle
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

function CardsScreen({ onBack }: { onBack: () => void }) {
  const [step, setStep] = useState<"list" | "add">("list");
  const [activeCardId, setActiveCardId] = useState(1);
  const cards = [
    { id: 1, last4: "4242", brand: "Visa", exp: "12/26", type: "Kredi Kartı", primary: true },
    { id: 2, last4: "5531", brand: "Mastercard", exp: "08/25", type: "Banka Kartı", primary: false }
  ];

  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-12">
        <div className="relative text-center pb-8 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={() => {
            if (step === "add") setStep("list");
            else onBack();
          }} className="absolute left-6 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-3.5 h-3.5" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">
            {step === "list" ? "Kredi ve Banka Kartları" : "Yeni Kart Ekle"}
          </h1>
        </div>
        
        <div className="flex-1 px-5 flex flex-col pt-4 overflow-y-auto pb-24">
          <AnimatePresence mode="wait">
            {step === "list" && (
              <motion.div
                key="list"
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="flex flex-col"
              >
                <p className="text-[10px] text-content-muted mb-4 leading-[1.5]">
                  Ödemelerinizde kullanmak üzere kartlarınızı güvenle saklayabilirsiniz. Altyapı BDDK onaylı iyzico tarafından sağlanmaktadır.
                </p>

                <div className="space-y-3">
                  {cards.map(card => (
                    <div 
                      key={card.id} 
                      onClick={() => setActiveCardId(card.id)}
                      className={`relative p-3 border rounded-[16px] cursor-pointer shadow-sm overflow-hidden flex items-center justify-between group transition-colors ${activeCardId === card.id ? 'border-[#F97316] bg-[#F97316]/5' : 'border-border-strong bg-surface hover:bg-surface-hover'}`}
                    >
                      <div className="flex items-center space-x-3 relative z-10">
                        {/* Radio inner circle */}
                        <div className={`w-3.5 h-3.5 rounded-full border-2 flex items-center justify-center shrink-0 ${activeCardId === card.id ? 'border-[#F97316]' : 'border-border-strong'}`}>
                          {activeCardId === card.id && <div className="w-2.5 h-2.5 bg-[#F97316] rounded-full" />}
                        </div>
                        
                        <div className="w-10 h-7 bg-surface border border-border-subtle rounded flex items-center justify-center p-0.5 shadow-sm shrink-0">
                           {card.brand === "Visa" ? (
                             <svg viewBox="0 0 38 24" fill="none" className="w-full h-full"><path d="M13.793 18.006L16.273 6h3.94l-2.48 12.006h-3.94zm14.735-11.75c-.752-.352-1.921-.724-3.415-.724-3.791 0-6.46 2.023-6.485 4.928-.025 2.15 1.942 3.352 3.419 4.076 1.516.745 2.023 1.222 2.023 1.884 0 .848-.999 1.408-1.944 1.4-1.637 0-2.528-.248-3.834-.827l-.54-.256-.554 3.468c.951.442 2.705.827 4.502.848 4.019 0 6.634-1.988 6.666-5.068.026-1.7-1.026-3.003-3.262-4.084-1.353-.69-2.181-1.15-2.181-1.85-.015-.65.736-1.332 2.102-1.332 1.341-.02 2.302.296 3.033.64l.366.173.57-3.532h.001zm-21.906 11.23h-3.8L5.195 7.64c-.167-.803-.896-1.39-1.745-1.572L.034 5.309.112 6h5.83l3.522 10.37h.176l5.244-10.37h4.081L6.622 17.486zM37.942 6h-3.064c-.722 0-1.258.204-1.575.942L28.847 18.006h4.116l.82-2.27h5.029l.478 2.27h3.69L37.942 6zm-3.513 7.828l1.246-3.424 1.968-5.38h.046l.995 5.378.745 3.426h-5.001z" fill="#1434CB"/></svg>
                           ) : (
                             <svg viewBox="0 0 38 24" fill="none" className="w-full h-full"><rect x="19" y="4" width="10" height="16" fill="#F79E1B"/><path d="M14 20c4.418 0 8-3.582 8-8s-3.582-8-8-8-8 3.582-8 8 3.582 8 8 8z" fill="#EB001B"/><path d="M24 20c4.418 0 8-3.582 8-8s-3.582-8-8-8-8 3.582-8 8 3.582 8 8 8z" fill="#F79E1B"/></svg>
                           )}
                        </div>
                        <div className="flex flex-col">
                          <span className="text-[10px] font-bold text-content leading-tight mb-0.5">&bull;&bull;&bull;&bull; {card.last4}</span>
                          <span className="text-[10px] text-content-muted">{card.type}</span>
                        </div>
                      </div>
                      <div className="flex items-center space-x-3 relative z-10">
                        <button 
                          className="text-content-muted hover:text-[#EF4444] transition-colors p-2 -mr-2"
                          onClick={(e) => {
                            e.stopPropagation();
                            // delete logic
                          }}
                        >
                          <Trash2 className="w-3.5 h-3.5" strokeWidth={2} />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>

                <button 
                  onClick={() => setStep("add")}
                  className="flex items-center justify-center space-x-2 w-full p-3.5 border border-dashed border-border-strong rounded-[16px] text-[#F97316] hover:bg-[#F97316]/5 transition-colors mt-4 font-medium shadow-sm text-[10px]"
                >
                  <Plus className="w-3.5 h-3.5" strokeWidth={2} />
                  <span>Yeni Kart Ekle</span>
                </button>
              </motion.div>
            )}

            {step === "add" && (
              <motion.div
                key="add"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="flex flex-col"
              >
                <div className="p-3 border border-border-subtle rounded-[16px] bg-surface-muted/50 mb-4 flex items-start">
                  <ShieldCheck className="w-3.5 h-3.5 text-[#10B981] shrink-0 mr-3 mt-0.5" strokeWidth={1.5} />
                  <p className="text-[10px] text-content-muted leading-[1.5]">
                    Kart bilgileriniz BDDK lisanslı ödeme kuruluşu iyzico tarafından güvenle saklanır. Yerime, kart bilgilerinizi göremez ve kaydedemez.
                  </p>
                </div>

                <div className="space-y-4">
                  <div className="flex flex-col">
                    <label className="text-[10px] font-medium text-content-muted mb-1.5 ml-1">Kart Üzerindeki İsim</label>
                    <input 
                      type="text" 
                      placeholder="AD SOYAD"
                      className="w-full bg-surface text-content text-[10px] px-3 py-2.5.5 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all uppercase" 
                    />
                  </div>

                  <div className="flex flex-col relative">
                    <label className="text-[10px] font-medium text-content-muted mb-1.5 ml-1">Kart Numarası</label>
                    <div className="relative">
                      <input 
                        type="text" 
                        inputMode="numeric"
                        placeholder="0000 0000 0000 0000"
                        maxLength={19}
                        className="w-full bg-surface text-content text-[10px] px-3 py-2.5.5 pl-11 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all font-mono" 
                      />
                      <CreditCard className="w-3.5 h-3.5 text-content-muted absolute left-4 top-[15px]" />
                    </div>
                  </div>

                  <div className="flex space-x-4">
                    <div className="flex flex-col flex-1">
                      <label className="text-[10px] font-medium text-content-muted mb-1.5 ml-1">Son Kul. Tarihi</label>
                      <input 
                        type="text" 
                        inputMode="numeric"
                        placeholder="AA/YY"
                        maxLength={5}
                        className="w-full bg-surface text-content text-[10px] px-3 py-2.5.5 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all font-mono text-center" 
                      />
                    </div>
                    <div className="flex flex-col flex-1">
                      <label className="text-[10px] font-medium text-content-muted mb-1.5 ml-1">CVC</label>
                      <input 
                        type="password" 
                        inputMode="numeric"
                        placeholder="***"
                        maxLength={3}
                        className="w-full bg-surface text-content text-[10px] px-3 py-2.5.5 rounded-[12px] border border-border-subtle focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none transition-all font-mono text-center" 
                      />
                    </div>
                  </div>
                </div>

                <div className="mt-4 w-full">
                  <button 
                    className="w-full bg-[#F97316] text-white rounded-full py-2.5 text-[10px] font-bold shadow-md active:scale-[0.98] transition-transform"
                    onClick={() => setStep("list")}
                  >
                    Kartı Kaydet
                  </button>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </div>
  );
}

function EscrowScreen({ onBack }: { onBack: () => void }) {
  const [iban, setIban] = useState("TR88 0006 2000 0001 2345 6789 01");
  const [accountName, setAccountName] = useState("Zehra Erdoğan");
  const [isEditingIban, setIsEditingIban] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  const handleWithdrawal = () => {
    setIsProcessing(true);
    setTimeout(() => {
      setIsProcessing(false);
      setSuccessMessage("Para çekme talebiniz başarıyla alındı. Tutar gün içinde hesabınıza aktarılacaktır.");
      setTimeout(() => setSuccessMessage(""), 5000);
    }, 1500);
  };

  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-12">
        <div className="relative text-center pb-8 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={onBack} className="absolute left-6 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-3.5 h-3.5" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Güvenli Ödeme Cüzdanı</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col pt-4 overflow-y-auto pb-24">
          <div className="bg-gradient-to-br from-[#1E293B] to-[#0F172A] rounded-[24px] p-3 text-white mb-4 relative overflow-hidden shadow-lg border border-[#1E293B]/20">
            <div className="absolute top-0 right-0 w-48 h-48 bg-[#F97316] opacity-10 blur-[50px] -mr-10 -mt-10 rounded-full" />
            <div className="absolute bottom-0 left-0 w-32 h-32 bg-[#38BDF8] opacity-[0.15] blur-[40px] -ml-10 -mb-10 rounded-full" />
            
            <div className="flex flex-col relative z-10">
              <div className="flex items-center justify-between mb-2">
                <span className="text-[10px] text-white/70 font-medium">Kullanılabilir Bakiye</span>
                <Wallet className="w-3.5 h-3.5 text-white/50" />
              </div>
              <span className="text-[36px] font-extrabold tracking-tight mb-1">₺2.450<span className="text-[10px] text-white/50">,00</span></span>
              
              <div className="flex items-center space-x-2 mt-4 text-[10px] text-[#10B981] font-medium bg-[#10B981]/10 px-3 py-1.5 rounded-full w-max border border-[#10B981]/20">
                <Clock className="w-3 h-3" />
                <span>Bekleyen (Tamamlanmamış): ₺450,00</span>
              </div>
            </div>
          </div>

          <h2 className="text-[10px] font-bold text-content mb-4 flex items-center">
            <Landmark className="w-3.5 h-3.5 mr-2 text-[#38BDF8]" strokeWidth={1.5} />
            Banka Hesabı (IBAN)
          </h2>
          <p className="text-[10px] text-content-muted leading-[1.5] mb-4">
            Yerime'de kazandığınız ücretler veya iadeleriniz bu IBAN numarasına gönderilir. Sadece kendi adınıza açılmış vadesiz TL hesaplarını ekleyebilirsiniz.
          </p>

          <AnimatePresence mode="wait">
            {successMessage && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                className="mb-4 p-3 rounded-xl bg-[#10B981]/10 border border-[#10B981]/20 flex items-start space-x-3"
              >
                <CheckCircle2 className="w-3.5 h-3.5 text-[#10B981] shrink-0 mt-0.5" />
                <p className="text-[10px] text-[#10B981] leading-[1.5] font-medium">{successMessage}</p>
              </motion.div>
            )}
          </AnimatePresence>

          <div className="p-3 border border-border-strong rounded-[20px] bg-surface mb-4 shadow-sm">
            <div className="flex justify-between items-start mb-4">
              <div className="flex flex-col w-full">
                {isEditingIban ? (
                  <div className="space-y-4">
                    <div className="flex flex-col">
                      <label className="text-[10px] font-medium text-content-muted uppercase tracking-wider mb-2">Ad Soyad (Hesap Sahibi)</label>
                      <input 
                        type="text" 
                        value={accountName} 
                        onChange={(e) => setAccountName(e.target.value)}
                        placeholder="Ad ve Soyad"
                        className="text-[10px] font-bold text-content w-full outline-none bg-surface-muted border border-border-subtle focus:border-[#F97316] rounded-xl px-3 py-2.5 transition-colors uppercase"
                      />
                    </div>
                    <div className="flex flex-col">
                      <label className="text-[10px] font-medium text-content-muted uppercase tracking-wider mb-2">Yeni IBAN Numarası</label>
                      <input 
                        type="text" 
                        value={iban} 
                        onChange={(e) => setIban(e.target.value)}
                        placeholder="TR00 0000 0000 0000 0000 0000 00"
                        className="text-[10px] font-bold text-content w-full outline-none bg-surface-muted border border-border-subtle focus:border-[#F97316] rounded-xl px-3 py-2.5 transition-colors font-mono"
                        autoFocus
                      />
                    </div>
                  </div>
                ) : (
                  <>
                    <span className="text-[10px] font-medium text-content-muted uppercase tracking-wider mb-2">Kayıtlı IBAN</span>
                    <span className="text-[10px] font-bold text-content font-mono bg-surface-muted px-3 py-2.5 rounded-xl border border-border-subtle flex items-center justify-between">
                      {iban || "Henüz IBAN eklenmemiş."}
                      {iban && <CheckCircle2 className="w-3.5 h-3.5 text-[#10B981]" />}
                    </span>
                    {iban && <span className="text-[10px] font-medium text-content mt-3 px-1">{accountName}</span>}
                  </>
                )}
              </div>
            </div>
            
            <button 
              onClick={() => setIsEditingIban(!isEditingIban)}
              className={`w-full border rounded-xl py-2.5 text-[10px] font-bold transition-colors mt-2 ${isEditingIban ? 'bg-[#1E293B] text-white border-[#1E293B]' : 'bg-surface border-border-strong text-content hover:bg-surface-hover'}`}
            >
              {isEditingIban ? "Bilgileri Kaydet" : "Hesabı Değiştir / Güncelle"}
            </button>
          </div>

          <div className="w-full pt-4 mt-auto">
             <button 
              onClick={handleWithdrawal}
              disabled={isProcessing}
              style={{ color: "#FFFFFF" }}
              className="w-full bg-[#1E293B] rounded-full py-2.5 text-[10px] font-bold shadow-md active:scale-[0.98] transition-transform flex items-center justify-center space-x-2 disabled:opacity-80"
            >
              {isProcessing ? (
                <motion.div animate={{ rotate: 360 }} transition={{ repeat: Infinity, duration: 1, ease: "linear" }}>
                  <RefreshCw className="w-3.5 h-3.5 text-white/70" />
                </motion.div>
              ) : (
                <>
                  <span>Bakiyeyi Banka Hesabıma Aktar</span>
                  <ArrowDownToLine className="w-3.5 h-3.5 text-white/70" />
                </>
              )}
            </button>
            <p className="text-[10px] text-center text-content-muted mt-4 font-medium flex items-center justify-center">
               <ShieldCheck className="w-3.5 h-3.5 mr-1" />
               Min. çekim tutarı ₺100'dir. Çekimler aynı gün içinde IBAN'ınıza gönderilir.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

function ReceiptsScreen({ onBack }: { onBack: () => void }) {
  const transactions = [
    { id: 1, title: "Musluk Tamiri (Usta)", date: "Bugün, 14:30", amount: "-₺450,00", type: "outcome", status: "completed" },
    { id: 2, title: "Paket Teslimatı", date: "Dün, 16:45", amount: "+₺120,00", type: "income", status: "completed" },
    { id: 3, title: "Bakiye Çekimi", date: "12 Mar 2026, 09:15", amount: "-₺1.000,00", type: "outcome", status: "bank" },
    { id: 4, title: "Mobilya Kurulumu", date: "10 Mar 2026, 18:00", amount: "+₺850,00", type: "income", status: "completed" },
  ];

  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-12">
        <div className="relative text-center pb-8 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5 border-b border-border-subtle">
          <button onClick={onBack} className="absolute left-6 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-3.5 h-3.5" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">İşlem Geçmişi</h1>
        </div>
        
        <div className="flex px-5 py-4 space-x-2 overflow-x-auto no-scrollbar border-b border-border-subtle shrink-0">
          <button className="px-3 py-1.5 bg-[#F97316] text-white text-[10px] font-bold rounded-full shadow-md whitespace-nowrap shrink-0 snap-start active:scale-95 transition-all">Tümü</button>
          <button className="px-3 py-1.5 bg-surface-muted text-content-muted text-[10px] font-medium border border-border-subtle hover:border-[#F97316] hover:text-[#F97316] rounded-full whitespace-nowrap shrink-0 snap-start transition-all duration-200">Gelenler</button>
          <button className="px-3 py-1.5 bg-surface-muted text-content-muted text-[10px] font-medium border border-border-subtle hover:border-[#F97316] hover:text-[#F97316] rounded-full whitespace-nowrap shrink-0 snap-start transition-all duration-200">Gidenler</button>
          <button className="px-3 py-1.5 bg-surface-muted text-content-muted text-[10px] font-medium border border-border-subtle hover:border-[#F97316] hover:text-[#F97316] rounded-full whitespace-nowrap shrink-0 snap-start transition-all duration-200">Çekim İşlemleri</button>
        </div>

        <div className="flex-1 px-5 flex flex-col pt-2 overflow-y-auto pb-24">
          <div className="space-y-0">
            {transactions.map((tx, idx) => (
              <div key={tx.id} className={`flex items-center justify-between py-2.5 ${idx !== transactions.length - 1 ? 'border-b border-border-subtle' : ''}`}>
                <div className="flex items-start space-x-4">
                  <div className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 ${
                    tx.status === 'bank' ? 'bg-[#3B82F6]/10 text-[#3B82F6]' :
                    tx.type === 'income' ? 'bg-[#10B981]/10 text-[#10B981]' : 
                    'bg-[#EF4444]/10 text-[#EF4444]'
                  }`}>
                    {tx.status === 'bank' ? <Landmark className="w-3.5 h-3.5" /> : 
                     tx.type === 'income' ? <ArrowDownToLine className="w-3.5 h-3.5" /> : 
                     <ReceiptText className="w-3.5 h-3.5" />}
                  </div>
                  <div className="flex flex-col">
                    <span className="text-[10px] font-bold text-content leading-tight mb-1">{tx.title}</span>
                    <span className="text-[10px] text-content-muted flex items-center">
                      {tx.date}
                    </span>
                  </div>
                </div>
                <div className="flex flex-col items-end pl-4">
                  <span className={`text-[10px] font-bold ${tx.type === 'income' ? 'text-[#10B981]' : 'text-content'}`}>
                    {tx.amount}
                  </span>
                  <button className="text-[10px] text-content-muted hover:text-content flex items-center mt-1 underline transition-colors">
                    Makbuz
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="mt-4 flex justify-center pb-8 border-t border-border-subtle pt-6">
            <button className="flex items-center text-[10px] font-bold text-content-muted hover:text-content transition-colors group">
              <FileText className="w-3.5 h-3.5 mr-2 group-hover:text-[#F97316] transition-colors" />
              Tüm Ekstreyi İndir (PDF)
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

function TaxScreen({ onBack }: { onBack: () => void }) {
  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-10">
        <div className="relative text-center pb-6 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={onBack} className="absolute left-5 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-6 h-6" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Vergi Bilgileri</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col pt-4 overflow-y-auto pb-24">
          <p className="text-[10px] text-content-muted mb-6 leading-[1.5]">
            Hizmet veren veya hizmet alan olarak kestiğiniz/aldığınız faturalar için vergi bilgilerinizi güncel tutun.
          </p>
          <div className="p-4 border border-border-strong rounded-[16px] bg-surface">
            <p className="text-[10px] font-medium text-content mb-2">Bireysel / Şahıs Şirketi</p>
            <input type="text" placeholder="TC Kimlik No / VKN" className="w-full bg-surface-muted text-content text-[10px] px-3 py-2.5 rounded-[12px] border border-border-subtle focus:border-[#F97316] outline-none" />
            <div className="mt-4">
              <input type="text" placeholder="Vergi Dairesi" className="w-full bg-surface-muted text-content text-[10px] px-3 py-2.5 rounded-[12px] border border-border-subtle focus:border-[#F97316] outline-none" />
            </div>
            <button className="w-full bg-[#1E293B] text-white rounded-full py-2.5 text-[10px] font-bold mt-4">Kaydet</button>
          </div>
        </div>
      </div>
    </div>
  );
}

function KvkkScreen({ onBack }: { onBack: () => void }) {
  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-10">
        <div className="relative text-center pb-6 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={onBack} className="absolute left-5 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-6 h-6" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Veri & Gizlilik</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col pt-4 overflow-y-auto pb-24">
           <div className="space-y-4">
              <div className="flex items-start justify-between p-4 border border-border-strong rounded-[16px]">
                 <div>
                   <p className="text-[10px] font-bold text-content">Pazarlama İletişimi</p>
                   <p className="text-[10px] text-content-muted mt-1">Kampanyalar ve indirimler hakkında e-posta ve SMS alın.</p>
                 </div>
                 <input type="checkbox" className="w-5 h-5 accent-[#F97316]" defaultChecked />
              </div>
              <div className="flex items-start justify-between p-4 border border-border-strong rounded-[16px]">
                 <div>
                   <p className="text-[10px] font-bold text-content">Kişiselleştirme</p>
                   <p className="text-[10px] text-content-muted mt-1">Arama ve kullanım alışkanlıklarınıza göre hizmet önerileri.</p>
                 </div>
                 <input type="checkbox" className="w-5 h-5 accent-[#F97316]" defaultChecked />
              </div>
              <button className="flex items-center text-[10px] text-[#EF4444] font-medium pt-4 p-2">
                Verilerimin tamamını indir
              </button>
           </div>
        </div>
      </div>
    </div>
  );
}


function Toggle({ checked, onChange }: { checked: boolean; onChange: () => void }) {
  return (
    <button 
      onClick={onChange}
      className={`w-11 h-6 rounded-full transition-colors relative flex items-center px-[3px] shrink-0 border-2 outline-none ${checked ? 'bg-[#F97316] border-[#F97316]' : 'bg-transparent border-[#F97316]/60'}`}
    >
      <div className={`w-3.5 h-3.5 rounded-full transition-transform duration-200 ${checked ? 'bg-white translate-x-[20px]' : 'bg-[#F97316]/60 translate-x-0'}`} />
    </button>
  );
}

function NotificationsScreen({ onBack }: { onBack: () => void }) {
  const [prefs, setPrefs] = useState({
    arama: true,
    mesaj: true,
    teklif: true,
    sistem: false
  });

  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-10">
        <div className="relative text-center pb-6 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5 border-b border-border-subtle">
          <button onClick={onBack} className="absolute left-5 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-6 h-6" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Bildirimler</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col pt-6 overflow-y-auto pb-24">
           <div className="space-y-1">
              <div className="flex items-center justify-between py-4 border-b border-border-subtle hover:bg-surface-muted px-2 -mx-2 rounded-xl transition-colors cursor-pointer" onClick={() => setPrefs(prev => ({...prev, arama: !prev.arama}))}>
                 <div className="pr-4">
                   <p className="text-[12px] font-bold text-content">Arama</p>
                   <p className="text-[10px] text-content-muted mt-1 leading-snug">Uygulama içi ve dışı arama bildirimleri.</p>
                 </div>
                 <Toggle checked={prefs.arama} onChange={() => {}} />
              </div>
              <div className="flex items-center justify-between py-4 border-b border-border-subtle hover:bg-surface-muted px-2 -mx-2 rounded-xl transition-colors cursor-pointer" onClick={() => setPrefs(prev => ({...prev, mesaj: !prev.mesaj}))}>
                 <div className="pr-4">
                   <p className="text-[12px] font-bold text-content">Mesajlar (Sohbet)</p>
                   <p className="text-[10px] text-content-muted mt-1 leading-snug">Uygulama içinden gelen yeni sohbet mesajları.</p>
                 </div>
                 <Toggle checked={prefs.mesaj} onChange={() => {}} />
              </div>
              <div className="flex items-center justify-between py-4 border-b border-border-subtle hover:bg-surface-muted px-2 -mx-2 rounded-xl transition-colors cursor-pointer" onClick={() => setPrefs(prev => ({...prev, teklif: !prev.teklif}))}>
                 <div className="pr-4">
                   <p className="text-[12px] font-bold text-content">Teklif İşlemleri</p>
                   <p className="text-[10px] text-content-muted mt-1 leading-snug">Yeni teklifler ve durum güncellemeleri.</p>
                 </div>
                 <Toggle checked={prefs.teklif} onChange={() => {}} />
              </div>
              <div className="flex items-center justify-between py-4 hover:bg-surface-muted px-2 -mx-2 rounded-xl transition-colors cursor-pointer" onClick={() => setPrefs(prev => ({...prev, sistem: !prev.sistem}))}>
                 <div className="pr-4">
                   <p className="text-[12px] font-bold text-content">Sistem Güncellemeleri</p>
                   <p className="text-[10px] text-content-muted mt-1 leading-snug">Kampanyalar, yenilikler ve duyurular.</p>
                 </div>
                 <Toggle checked={prefs.sistem} onChange={() => {}} />
              </div>
           </div>
        </div>
      </div>
    </div>
  );
}

function ThemeLanguageScreen({ onBack }: { onBack: () => void }) {
  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-10">
        <div className="relative text-center pb-6 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={onBack} className="absolute left-5 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-6 h-6" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Tema ve Dil</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col pt-4 overflow-y-auto pb-24">
           <h2 className="text-[10px] font-bold text-content mb-3">Tema</h2>
           <div className="grid grid-cols-2 gap-3 mb-6">
             <button className="flex items-center justify-center space-x-2 p-3 border border-[#F97316] bg-[#F97316]/5 rounded-[12px] text-content">
               <Sun className="w-4 h-4" />
               <span className="text-[10px] font-medium">Açık</span>
             </button>
             <button className="flex items-center justify-center space-x-2 p-3 border border-border-strong bg-surface rounded-[12px] text-content-muted hover:text-content">
               <Moon className="w-4 h-4" />
               <span className="text-[10px] font-medium">Koyu</span>
             </button>
           </div>
           
           <h2 className="text-[10px] font-bold text-content mb-3">Uygulama Dili</h2>
           <div className="p-4 border border-border-strong rounded-[16px] flex justify-between items-center bg-surface cursor-pointer">
             <div className="flex items-center space-x-3">
               <Globe className="w-4 h-4 text-content-muted" />
               <span className="text-[10px] font-medium text-content">Türkçe</span>
             </div>
             <ChevronRight className="w-4 h-4 text-content-muted" />
           </div>
        </div>
      </div>
    </div>
  );
}

function SupportScreen({ onBack }: { onBack: () => void }) {
  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-10">
        <div className="relative text-center pb-6 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={onBack} className="absolute left-5 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-6 h-6" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Destek</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col pt-4 overflow-y-auto pb-24">
           <div className="space-y-3">
             <button className="w-full flex items-center justify-between p-4 border border-border-strong rounded-[16px] bg-surface hover:bg-surface-hover">
               <div className="flex items-center space-x-3">
                 <HelpCircle className="w-4 h-4 text-[#F97316]" />
                 <span className="text-[10px] font-medium text-content">Sıkça Sorulan Sorular</span>
               </div>
               <ChevronRight className="w-4 h-4 text-content-muted" />
             </button>
             <button className="w-full flex items-center justify-between p-4 border border-border-strong rounded-[16px] bg-surface hover:bg-surface-hover">
               <div className="flex items-center space-x-3">
                 <ShieldAlert className="w-4 h-4 text-content-muted" />
                 <span className="text-[10px] font-medium text-content">Güvenlik Merkezi</span>
               </div>
               <ChevronRight className="w-4 h-4 text-content-muted" />
             </button>
             <button className="w-full bg-[#1E293B] text-white rounded-full py-3.5 text-[10px] font-bold mt-4 shadow-md text-center">
               Canlı Desteğe Bağlan
             </button>
           </div>
        </div>
      </div>
    </div>
  );
}

function BlockedScreen({ onBack }: { onBack: () => void }) {
  return (
    <div className="min-h-screen bg-surface flex justify-center">
      <div className="w-full max-w-[480px] bg-surface min-h-screen relative shadow-2xl flex flex-col pt-10">
        <div className="relative text-center pb-6 sticky top-0 bg-surface/90 backdrop-blur-sm z-10 px-5">
          <button onClick={onBack} className="absolute left-5 top-0 text-content-muted hover:text-content transition-colors p-1 -ml-1">
            <ChevronLeft className="w-6 h-6" strokeWidth={1.5} />
          </button>
          <h1 className="text-[15px] font-semibold text-content tracking-tight mt-0.5">Engellenenler</h1>
        </div>
        <div className="flex-1 px-5 flex flex-col pt-4 overflow-y-auto pb-24 items-center justify-center">
          <UserX className="w-10 h-10 text-border-strong mb-4" />
          <h2 className="text-[10px] font-bold text-content mb-2">Engellenen Kimse Yok</h2>
          <p className="text-[10px] text-content-muted text-center max-w-[250px] leading-[1.5]">
            Sizi rahatsız eden kişileri engellediğinizde burada listelenir.
          </p>
        </div>
      </div>
    </div>
  );
}
