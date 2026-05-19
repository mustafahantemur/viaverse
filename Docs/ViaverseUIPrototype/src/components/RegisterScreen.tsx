import { motion, AnimatePresence } from "motion/react";
import { useState } from "react";
import { MapPin } from "lucide-react";
import viaverseLogo from "../icons/viaverse_v_orange_green.svg";

export default function RegisterScreen({ onBack, onClose }: { onBack?: () => void, onClose?: () => void }) {
  const [step, setStep] = useState<"form" | "otp" | "location">("form");
  const [otp, setOtp] = useState(["", "", "", "", "", ""]);
  const [isCalendarOpen, setIsCalendarOpen] = useState(false);
  const [selectedDate, setSelectedDate] = useState("");
  const [promoChecked, setPromoChecked] = useState(false);
  const [selectedCity, setSelectedCity] = useState("");
  const [selectedDistrict, setSelectedDistrict] = useState("");
  const [radius, setRadius] = useState(2);

  const handleOtpChange = (index: number, value: string) => {
    if (value.length > 1) value = value.slice(-1);
    if (!/^\d*$/.test(value)) return;
    
    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);

    if (value && index < 5) {
      const nextInput = document.getElementById(`reg-otp-${index + 1}`);
      nextInput?.focus();
    }
  };

  const handleDateSelect = (day: number) => {
    const formattedDay = day.toString().padStart(2, '0');
    setSelectedDate(`${formattedDay}/03/1990`);
    setIsCalendarOpen(false);
  };

  return (
    <>
      <style dangerouslySetInnerHTML={{__html: `
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap');
        .premium-ui { font-family: 'Inter', sans-serif; -webkit-font-smoothing: antialiased; }
        .airbnb-tight { letter-spacing: -0.04em; }
        .airbnb-checkbox {
          appearance: none;
          background-color: #fff;
          margin: 0;
          font: inherit;
          color: currentColor;
          width: 1.5rem;
          height: 1.5rem;
          border: 1px solid #B0B0B0;
          border-radius: 0.35rem;
          display: grid;
          place-content: center;
          cursor: pointer;
        }
        .airbnb-checkbox::before {
          content: "";
          width: 0.85em;
          height: 0.85em;
          transform: scale(0);
          transition: 120ms transform ease-in-out;
          box-shadow: inset 1em 1em white;
          transform-origin: center;
          clip-path: polygon(14% 44%, 0 65%, 50% 100%, 100% 16%, 80% 0%, 43% 62%);
        }
        .airbnb-checkbox:checked {
          background-color: #222222;
          border-color: #222222;
        }
        .airbnb-checkbox:checked::before {
          transform: scale(1);
        }
      `}} />

      <div className="min-h-screen w-full bg-surface flex justify-center premium-ui text-content pb-10">
        <div className="w-full max-w-md bg-surface min-h-screen relative flex flex-col px-6 pt-4">
          
          {/* Header Navigation */}
          <div className="flex justify-between items-center w-full mb-4">
            <button onClick={() => {
              if (step === "otp") setStep("form");
              else if (onBack) onBack();
            }} className="p-2 -ml-2 text-content hover:bg-surface-hover rounded-[12px] transition-all">
              <svg viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg" style={{ display: 'block', fill: 'none', height: '16px', width: '16px', stroke: 'currentcolor', strokeWidth: '3', overflow: 'visible' }} aria-hidden="true" role="presentation" focusable="false"><g fill="none"><path d="m20 28-11.29289322-11.2928932c-.39052429-.3905243-.39052429-1.0236893 0-1.4142136l11.29289322-11.2928932"></path></g></svg>
            </button>
            <button onClick={onClose} className="p-2 -mr-2 text-content hover:bg-surface-hover rounded-[12px] transition-all">
              <svg viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg" aria-hidden="true" role="presentation" focusable="false" style={{ display: 'block', fill: 'none', height: '16px', width: '16px', stroke: 'currentcolor', strokeWidth: '3', overflow: 'visible' }}><path d="m6 6 20 20M26 6 6 26"></path></svg>
            </button>
          </div>

          <AnimatePresence mode="wait">
            {step === "form" && (
              <motion.div
                key="form"
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.3 }}
              >
                {/* Dinamik Yerime Logosu (Fırıldak) */}
            <div className="flex justify-start mb-6 mt-2">
              <motion.div
                animate={{ rotateY: 360 }}
                transition={{ duration: 7, ease: "linear", repeat: Infinity }}
                style={{ transformStyle: "preserve-3d" }}
              >
                <img src={viaverseLogo} alt="Viaverse" className="w-16 h-16 drop-shadow-md object-contain" />
              </motion.div>
            </div>

            <h1 className="font-[800] text-content airbnb-tight" style={{ fontSize: "20px", lineHeight: "26px" }}>
              Hesabınızı oluşturalım
            </h1>
            <p className="text-content-muted text-[12px] mt-2 mb-8">
              Bu bilgiler rezervasyon yapmak veya hizmet sunmak için gereklidir.
            </p>

            {/* Legal Name Section */}
            <h2 className="text-[15px] font-bold text-content mb-3">Yasal ad</h2>
            <div className="flex flex-col">
              <input 
                type="text" 
                placeholder="Ad"
                className="w-full px-4 rounded-[12px] border border-border-muted text-content placeholder-content-muted focus:outline-none focus:border-content focus:ring-1 focus:ring-content transition-colors z-0 focus:z-10 relative bg-surface mt-1"
                style={{ fontSize: "13px", lineHeight: "8px", height: "40px" }}
              />
              <input 
                type="text" 
                placeholder="Soyad"
                className="w-full px-4 rounded-[12px] border border-border-muted text-content placeholder-content-muted focus:outline-none focus:border-content focus:ring-1 focus:ring-content transition-colors z-0 focus:z-10 relative mt-2 bg-surface"
                style={{ fontSize: "13px", lineHeight: "8px", height: "40px" }}
              />
            </div>
            <p className="text-content-muted text-[12px] mt-2 leading-[1.4]">
              Kimliğinizdeki adla eşleştiğinden emin olun. Başka bir isim kullanıyorsanız, <span className="underline font-semibold cursor-pointer text-content">tercih edilen bir ilk ad ekleyebilirsiniz</span>.
            </p>

            {/* Date of Birth Section */}
            <h2 className="text-[15px] font-bold text-content mt-8 mb-3">Doğum tarihi</h2>
            <div className="relative">
              <input 
                type="text" 
                readOnly
                value={selectedDate}
                onClick={() => setIsCalendarOpen(true)}
                placeholder="Tarih seçin"
                className="w-full px-4 rounded-[12px] border border-border-muted text-content placeholder-content-muted focus:outline-none focus:border-content focus:ring-1 focus:ring-content transition-all cursor-pointer bg-surface"
                style={{ fontSize: "13px", lineHeight: "8px", height: "40px" }}
              />
              <div className="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-content">
                <svg viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg" aria-hidden="true" role="presentation" focusable="false" style={{ display: 'block', fill: 'currentcolor', height: '20px', width: '20px', overflow: 'visible' }}><path d="M26 4h-2V1h-4v3H12V1H8v3H6C3.79 4 2 5.79 2 8v20c0 2.21 1.79 4 4 4h20c2.21 0 4-1.79 4-4V8c0-2.21-1.79-4-4-4zM6 8h2v3h4V8h8v3h4V8h2c.552 0 1 .448 1 1v5H3V9c0-.552.448-1 1-1zm20 20H6c-.552 0-1-.448-1-1V18h22v9c0 .552-.448 1-1 1z"></path></svg>
              </div>
            </div>

            {/* Email Section */}
            <h2 className="text-[15px] font-bold text-content mt-8 mb-3">E-posta</h2>
            <input 
              type="email" 
              placeholder="E-posta"
              className="w-full px-4 rounded-[12px] border border-border-muted text-content placeholder-content-muted focus:outline-none focus:border-content focus:ring-1 focus:ring-content transition-all bg-surface"
              style={{ fontSize: "13px", lineHeight: "8px", height: "40px" }}
            />
            <p className="text-content-muted text-[12px] mt-2 leading-[1.4]">
              Seyahat onaylarını ve makbuzları size e-posta ile göndereceğiz.
            </p>

            {/* Password Section */}
            <h2 className="text-[15px] font-bold text-content mt-8 mb-3">Şifre</h2>
            <input 
              type="password" 
              placeholder="Şifre oluşturun (en az 8 karakter)"
              className="w-full px-4 rounded-[12px] border border-border-muted text-content placeholder-content-muted focus:outline-none focus:border-content focus:ring-1 focus:ring-content transition-all bg-surface"
              style={{ fontSize: "13px", lineHeight: "8px", height: "40px" }}
            />
            <p className="text-content-muted text-[12px] mt-2 leading-[1.4]">
              Güçlü bir şifre seçtiğinizden emin olun.
            </p>

            {/* Terms Links */}
            <p className="text-content text-[12px] mt-8 leading-[1.5]">
              <span className="font-semibold">Kabul et ve devam et</span>'i seçerek, Yerime'nin <span className="text-[#0000EE] font-semibold underline cursor-pointer">Hizmet Şartları</span>, <span className="text-[#0000EE] font-semibold underline cursor-pointer">Ödeme Hizmet Şartları</span> ve <span className="text-[#0000EE] font-semibold underline cursor-pointer">Ayrımcılık Yapmama Politikası</span>'nı kabul etmiş ve <span className="text-[#0000EE] font-semibold underline cursor-pointer">Gizlilik Politikası</span>'nı okuduğumu onaylıyorum.
            </p>

            <button onClick={() => setStep("otp")} className="mt-6 w-full text-white font-bold text-[14px] rounded-[12px] active:scale-[0.96] transition-all flex justify-center items-center" style={{ height: "40px", lineHeight: "12px", backgroundColor: "#F97316" }}>
              Kabul et ve devam et
            </button>

            {/* Promotions Divider & Checkbox */}
            <div className="mt-8 border-t border-border-subtle pt-6 flex gap-4 pr-2 items-start">
              <div className="flex-1 flex flex-col gap-2">
                <p className="text-content text-[12px] leading-tight flex-1">
                  Yerime size fırsatlar ve pazarlama bildirimleri gibi promosyonlar gönderecektir. Hesap ayarlarından veya pazarlama e-postalarından istediğiniz zaman çıkabilirsiniz.
                </p>
                <button className="text-left text-content text-[12px] leading-snug cursor-pointer group flex items-start -ml-1">
                  <span className="border border-transparent p-1">Yerime promosyonlarını almak istemiyorum.</span>
                </button>
              </div>
              <div className="pt-2">
                <input 
                  type="checkbox" 
                  className="airbnb-checkbox"
                  checked={promoChecked}
                  onChange={(e) => setPromoChecked(e.target.checked)}
                />
              </div>
            </div>

              </motion.div>
            )}

            {step === "otp" && (
              <motion.div
                key="otp"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.3 }}
                className="flex flex-col flex-1"
              >
                <h2 className="text-[18px] font-bold text-content mb-2 mt-4 airbnb-tight">Doğrulama Kodunu Girin</h2>
                <p className="text-[12px] text-content-muted mb-8 leading-[1.5]">
                  İletişim bilgilerinizi güvene almak için gönderdiğimiz 6 haneli kodu lütfen girin.
                </p>

                <div className="flex space-x-2 w-full mb-8">
                  {otp.map((digit, idx) => (
                    <input
                      key={idx}
                      id={`reg-otp-${idx}`}
                      type="text"
                      inputMode="numeric"
                      value={digit}
                      onChange={(e) => handleOtpChange(idx, e.target.value)}
                      className="w-[45px] h-14 bg-surface text-center text-[18px] font-bold text-content rounded-[12px] border border-border-subtle focus:border-[#F97316] outline-none transition-colors"
                    />
                  ))}
                </div>

                <div className="text-left mb-8">
                  <p className="text-[12px] text-content-muted mb-2">Kodu almadınız mı?</p>
                  <button className="text-[12px] font-bold text-[#F97316]">Tekrar Gönder (00:59)</button>
                </div>

                <div className="mt-8 w-full">
                  <button 
                    onClick={() => setStep("location")}
                    className="w-full bg-[#F97316] text-white rounded-[12px] flex items-center justify-center font-bold text-[14px] active:scale-[0.98] transition-transform"
                    style={{ height: "44px" }}
                  >
                    Devam Et
                  </button>
                </div>
              </motion.div>
            )}

            {step === "location" && (
              <motion.div
                key="location"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.3 }}
                className="flex flex-col flex-1"
              >
                <h2 className="text-[18px] font-bold text-content mb-2 mt-4 airbnb-tight">Yakın Çevrenizi Belirleyin</h2>
                <p className="text-[12px] text-content-muted mb-8 leading-[1.5]">
                  Çevrenizdeki ilanları ve komşularınızı görmek için size uygun mesafeyi seçin. Bu onaylandıktan sonra ayarlarınızdan değiştirebilirsiniz.
                </p>

                {/* City/District inputs */}
                <div className="flex items-center justify-between mb-3">
                  <h2 className="text-[15px] font-bold text-content">Konum</h2>
                  <button className="text-[11px] flex items-center font-bold text-[#F97316] hover:bg-[#F97316]/10 px-2 py-1 rounded-md transition-colors" onClick={() => {
                    if (navigator.geolocation) {
                      navigator.geolocation.getCurrentPosition(() => {
                        setSelectedCity("İstanbul");
                        setSelectedDistrict("Kadıköy, Caferağa");
                      });
                    }
                  }}>
                    <MapPin className="w-3.5 h-3.5 mr-1" />
                    Mevcut Konumu Kullan
                  </button>
                </div>
                <div className="flex flex-col mb-8">
                  <input 
                    type="text" 
                    placeholder="Şehir (Örn: İstanbul)"
                    value={selectedCity}
                    onChange={(e) => setSelectedCity(e.target.value)}
                    className="w-full px-4 rounded-[12px] border border-border-muted text-content placeholder-content-muted focus:outline-none focus:border-content focus:ring-1 focus:ring-content transition-colors z-0 focus:z-10 relative bg-surface"
                    style={{ fontSize: "13px", lineHeight: "8px", height: "40px" }}
                  />
                  <input 
                    type="text" 
                    placeholder="İlçe / Semt (Örn: Kadıköy, Caferağa)"
                    value={selectedDistrict}
                    onChange={(e) => setSelectedDistrict(e.target.value)}
                    className="w-full px-4 rounded-[12px] border border-border-muted text-content placeholder-content-muted focus:outline-none focus:border-content focus:ring-1 focus:ring-content transition-colors z-0 focus:z-10 relative mt-2 bg-surface"
                    style={{ fontSize: "13px", lineHeight: "8px", height: "40px" }}
                  />
                </div>

                <div className="mb-8">
                  <div className="flex justify-between items-center mb-4">
                    <h2 className="text-[15px] font-bold text-content">Görünürlük Mesafesi</h2>
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
                  <div className="flex justify-between text-[11px] text-content-muted mt-2 font-medium">
                    <span>Sadece Yakın Çevrem (1km)</span>
                    <span>Geniş Çevre (50km)</span>
                  </div>
                </div>

                <div className="mt-8 w-full mt-auto mb-10">
                  <button 
                    onClick={onClose}
                    className="w-full bg-[#F97316] text-white rounded-[12px] flex items-center justify-center font-bold text-[14px] active:scale-[0.98] transition-transform"
                    style={{ height: "44px" }}
                  >
                    Kayıt Ol ve Başla
                  </button>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>

      {/* Calendar Overlay / Modal */}
      <AnimatePresence>
        {isCalendarOpen && (
          <>
            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setIsCalendarOpen(false)}
              className="fixed inset-0 bg-black/40 z-40 premium-ui"
            />
            
            <motion.div 
              initial={{ opacity: 0, y: 100 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: 100 }}
              transition={{ type: "spring", damping: 25, stiffness: 300 }}
              className="fixed bottom-0 left-0 right-0 bg-surface pt-6 pb-12 px-6 z-50 md:bottom-auto md:top-1/2 md:-translate-y-1/2 md:left-1/2 md:-translate-x-1/2 md:w-full md:max-w-sm rounded-[24px] premium-ui shadow-2xl"
            >
              <div className="flex justify-between items-center mb-6">
                <button className="p-2 hover:bg-surface-hover rounded-[12px] transition-colors">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                    <polyline points="15 18 9 12 15 6"></polyline>
                  </svg>
                </button>
                <div className="font-bold text-[14px] text-content">Mart 1990</div>
                <button className="p-2 hover:bg-surface-hover rounded-[12px] transition-colors">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                    <polyline points="9 18 15 12 9 6"></polyline>
                  </svg>
                </button>
              </div>

              <div className="grid grid-cols-7 mb-2">
                {['Pz', 'Pt', 'Sa', 'Ça', 'Pe', 'Cu', 'Ct'].map(day => (
                  <div key={day} className="text-center text-[12px] font-semibold text-content-muted h-8 flex items-center justify-center">
                    {day}
                  </div>
                ))}
              </div>

              <div className="grid grid-cols-7 gap-y-1">
                {/* Empty spaces for start of month */}
                <div /><div /><div /><div />
                
                {[...Array(31)].map((_, i) => (
                  <div key={i} className="flex justify-center items-center aspect-square">
                    <button 
                      onClick={() => handleDateSelect(i + 1)}
                      className="w-10 h-10 flex items-center justify-center rounded-full text-[12px] font-medium text-content hover:bg-surface-hover hover:border hover:border-[#222222] focus:outline-none focus:ring-2 focus:ring-black transition-all"
                    >
                      {i + 1}
                    </button>
                  </div>
                ))}
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>
    </>
  );
}
