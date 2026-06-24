import { motion, AnimatePresence } from "motion/react";
import { useState } from "react";
import { Instagram, X, ChevronLeft } from "lucide-react";
import viaverseLogo from "../icons/viaverse_v_orange_green.svg";

export default function AuthScreen({ onContinue, onSkip }: { onContinue?: () => void, onSkip?: () => void }) {
  const [showOTP, setShowOTP] = useState(false);
  const [otpValue, setOtpValue] = useState("");

  const handleContinue = () => {
    // In a real app, this would trigger SMS/Email OTP
    setShowOTP(true);
  };

  const handleVerifyOTP = () => {
    if (otpValue.length === 6) {
      onContinue?.();
    }
  };

  if (showOTP) {
    return (
      <div className="min-h-screen w-full bg-base flex justify-center text-content">
        <div className="w-full max-w-md bg-surface min-h-screen relative flex flex-col px-8 pt-6 shadow-sm">
          <button 
            onClick={() => setShowOTP(false)}
            className="absolute top-6 left-4 p-2 text-content hover:bg-surface-hover rounded-[12px] transition-all"
          >
            <ChevronLeft size={24} />
          </button>

          <div className="flex flex-col items-center mt-20">
            <h1 className="font-bold text-[20px] mb-2 tracking-tight">Doğrulama Kodu</h1>
            <p className="text-content-muted text-center text-[12px] mb-10 leading-relaxed max-w-[240px]">
              Telefonunuza gelen 6 haneli doğrulama kodunu giriniz.
            </p>

            <div className="flex gap-2.5 mb-10">
              {[...Array(6)].map((_, i) => (
                <input
                  key={i}
                  id={`otp-input-${i}`}
                  type="text"
                  maxLength={1}
                  className="w-10 h-14 border border-border-muted rounded-xl text-center text-[20px] font-bold focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] outline-none bg-surface-muted/30 transition-all"
                  value={otpValue[i] || ""}
                  onChange={(e) => {
                    const val = e.target.value.replace(/\D/g, "");
                    if (val) {
                      const newOtp = otpValue.substring(0, i) + val + otpValue.substring(i + 1);
                      setOtpValue(newOtp);
                      if (newOtp.length === 6) {
                        setTimeout(() => onContinue?.(), 300);
                      } else if (i < 5) {
                        document.getElementById(`otp-input-${i + 1}`)?.focus();
                      }
                    }
                  }}
                  onKeyDown={(e) => {
                    if (e.key === "Backspace") {
                      if (!otpValue[i] && i > 0) {
                        document.getElementById(`otp-input-${i - 1}`)?.focus();
                        const newOtp = otpValue.substring(0, i - 1) + otpValue.substring(i);
                        setOtpValue(newOtp);
                      } else {
                        const newOtp = otpValue.substring(0, i) + otpValue.substring(i + 1);
                        setOtpValue(newOtp);
                      }
                    }
                  }}
                />
              ))}
            </div>

            <button 
              onClick={handleVerifyOTP}
              disabled={otpValue.length !== 6}
              className={`w-full h-[52px] rounded-2xl font-bold text-[15px] transition-all shadow-lg ${otpValue.length === 6 ? 'bg-[#F97316] text-white shadow-[#F97316]/20' : 'bg-surface-muted text-content-muted shadow-none opacity-50 cursor-not-allowed'}`}
            >
              Doğrula ve Devam Et
            </button>

            <button className="mt-8 text-[12px] font-bold text-[#F97316] hover:opacity-80">
              Yeni Kod Gönder (45s)
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen w-full bg-base flex justify-center text-content">
      <div className="w-full max-w-md bg-surface min-h-screen relative flex flex-col px-8 pt-6 shadow-sm overflow-hidden">
        
        {/* Kapatma Butonu */}
        <motion.button 
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          onClick={onSkip}
          className="absolute top-6 left-4 p-2 text-content hover:bg-surface-hover rounded-[12px] transition-all z-10"
        >
          <X size={20} />
        </motion.button>
        
        {/* Atla Butonu */}
        <motion.button 
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          onClick={onSkip}
          className="absolute top-6 right-6 px-3 py-1.5 text-content-muted font-bold text-[12px] hover:bg-surface-hover rounded-[12px] transition-all z-10"
        >
          Atla
        </motion.button>

        {/* Logo Section */}
        <div className="flex flex-col items-center mt-16 mb-10">
          <motion.div 
            initial={{ opacity: 0, scale: 0.5 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.6, type: "spring", bounce: 0.4 }}
          >
            <motion.div
              animate={{ rotateY: 360 }}
              transition={{ duration: 7, ease: "linear", repeat: Infinity }}
              style={{ transformStyle: "preserve-3d" }}
            >
              <img src={viaverseLogo} alt="Viaverse" className="w-20 h-20 drop-shadow-lg object-contain" />
            </motion.div>
          </motion.div>
          <motion.div 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
            className="mb-8"
          />
        </div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5, duration: 0.5 }}
          className="flex-1 flex flex-col"
        >
          <div className="space-y-3.5">
            <input 
              type="text" 
              placeholder="Telefon numarası veya e-posta"
              className="w-full rounded-[16px] border border-border-muted text-content placeholder-content-muted focus:outline-none focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] transition-all bg-surface-muted/20 px-4"
              style={{ fontSize: "14px", height: "52px" }}
            />
            <input 
              type="password" 
              placeholder="Şifre"
              className="w-full rounded-[16px] border border-border-muted text-content placeholder-content-muted focus:outline-none focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] transition-all bg-surface-muted/20 px-4"
              style={{ fontSize: "14px", height: "52px" }}
            />
          </div>

          <button 
            onClick={handleContinue}
            className="mt-6 w-full bg-[#F97316] text-white font-bold text-[14px] rounded-[16px] shadow-lg shadow-[#F97316]/20 active:scale-[0.98] transition-all flex items-center justify-center" 
            style={{ height: "52px" }}
          >
            Giriş Yap
          </button>

          <div className="flex items-center my-8 shrink-0">
            <div className="flex-1 border-b border-border-subtle"></div>
            <span className="px-4 text-content-muted font-bold uppercase tracking-[0.2em] text-[10px]">VEYA</span>
            <div className="flex-1 border-b border-border-subtle"></div>
          </div>

          {/* Giriş Seçenekleri */}
          <div className="space-y-3">
            {/* GOOGLE BUTONU */}
            <button className="w-full border border-border-muted rounded-[16px] flex items-center px-5 hover:bg-surface-hover active:scale-[0.98] transition-all bg-white" style={{ height: "52px" }}>
              <div className="w-8 h-8 flex items-center justify-center">
                <svg className="w-[20px] h-[20px]" viewBox="0 0 24 24">
                  <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
                  <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                  <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
                  <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
                </svg>
              </div>
              <span className="flex-1 text-[12px] font-bold text-center mr-8">Google ile Giriş yap</span>
            </button>

            <div className="flex gap-3">
              {/* INSTAGRAM BUTONU */}
              <button className="flex-1 border border-border-muted rounded-[16px] flex items-center justify-center gap-2.5 hover:bg-surface-hover active:scale-[0.98] transition-all bg-white" style={{ height: "52px" }}>
                <div className="p-1.5 rounded-lg bg-gradient-to-tr from-[#F58529] via-[#DD2A7B] to-[#8134AF] flex items-center justify-center">
                  <Instagram size={14} className="text-white" />
                </div>
                <span className="text-[12px] font-bold">Instagram</span>
              </button>

              {/* APPLE BUTONU */}
              <button className="flex-1 border border-border-muted rounded-[16px] flex items-center justify-center gap-2.5 hover:bg-surface-hover active:scale-[0.98] transition-all bg-white" style={{ height: "52px" }}>
                <svg className="w-[18px] h-[18px] fill-content" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path d="M18.71 19.5C17.88 20.74 17 21.95 15.66 21.97C14.32 22 13.89 21.18 12.37 21.18C10.84 21.18 10.37 21.95 9.09997 22C7.78997 22.05 6.79997 20.68 5.95997 19.47C4.24997 17 2.93997 12.45 4.69997 9.39C5.56997 7.87 7.12997 6.91 8.81997 6.88C10.1 6.86 11.32 7.75 12.11 7.75C12.89 7.75 14.37 6.68 15.92 6.84C16.57 6.87 18.39 7.1 19.56 8.82C19.47 8.88 17.39 10.1 17.41 12.63C17.44 15.65 20.06 16.66 20.09 16.67C20.06 16.74 19.67 18.11 18.71 19.5ZM13 3.5C13.73 2.67 14.94 2.04 15.94 2C16.07 3.17 15.6 4.35 14.9 5.19C14.21 6.04 13.07 6.7 11.95 6.61C11.8 5.46 12.36 4.26 13 3.5Z"/>
                </svg>
                <span className="text-[12px] font-bold">Apple</span>
              </button>
            </div>
          </div>
          
        </motion.div>
      </div>
    </div>
  );
}
