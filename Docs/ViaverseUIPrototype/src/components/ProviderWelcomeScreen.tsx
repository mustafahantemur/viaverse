import { useState } from "react";
import { motion } from "motion/react";
import { ChevronLeft, User, Briefcase, MessageSquare, ShieldCheck } from "lucide-react";
import viaverseLogo from "../icons/viaverse_v_orange_green.svg";
import ProviderSetupTypeScreen from "./ProviderSetupTypeScreen";
import ProviderDashboardScreen from "./ProviderDashboardScreen";

interface ProviderWelcomeScreenProps {
  onBack: () => void;
  onStart: () => void;
}

export default function ProviderWelcomeScreen({ onBack, onStart }: ProviderWelcomeScreenProps) {
  const [showSetup, setShowSetup] = useState(false);
  const [skipToDashboard, setSkipToDashboard] = useState(false);

  if (skipToDashboard) {
    return (
      <ProviderDashboardScreen 
        onBackToUserMode={onBack} 
        profileData={{
          name: "Test Kullanıcısı",
          role: "Test Hizmet Veren"
        }}
      />
    );
  }

  if (showSetup) {
    return (
      <ProviderSetupTypeScreen
        onBack={() => setShowSetup(false)}
        onNext={(type) => {
          console.log("Seçilen hizmet veren tipi:", type);
          alert("Seçim kaydedildi: " + type + "\nSonraki adım daha sonra eklenecek.");
        }}
      />
    );
  }

  return (
    <div className="min-h-screen bg-surface flex justify-center text-content font-sans">
      <div className="w-full max-w-md bg-surface min-h-screen relative flex flex-col">
        
        {/* Top Bar */}
        <div className="flex items-center justify-between pt-4 pb-2 px-4 sticky top-0 bg-surface/90 backdrop-blur-md z-30">
          <button 
            onClick={onBack} 
            className="w-10 h-10 -ml-2 text-content-muted flex items-center justify-center hover:text-content transition-colors rounded-full active:scale-95"
          >
             <ChevronLeft className="w-6 h-6" strokeWidth={1.5} />
          </button>
          
          <div className="flex items-center gap-1.5 px-3 py-1 bg-[#10B981]/10 rounded-full border border-[#10B981]/20">
             <div className="w-1.5 h-1.5 rounded-full bg-[#10B981] animate-pulse" />
             <span className="text-[10px] font-bold text-[#10B981] uppercase tracking-wide">Hizmet Veren Modu</span>
          </div>
          
          {/* Spacer for centering */}
          <div className="w-10 h-10 -mr-2" /> 
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto px-6 pt-4 pb-32">
          
          {/* Hero Animation / Logo */}
          <div className="mb-6 flex justify-start relative w-16 h-16">
            <div className="absolute inset-0 bg-gradient-to-tr from-[#F97316]/20 to-[#10B981]/20 blur-xl rounded-full scale-[1.5] -z-10" />
            <motion.div
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              transition={{ duration: 0.5, type: "spring", bounce: 0.4 }}
              className="relative w-16 h-16 flex items-center justify-center z-10"
            >
               <motion.div
                 animate={{ rotateY: 360 }}
                 transition={{ duration: 6, ease: "linear", repeat: Infinity }}
                 style={{ transformStyle: "preserve-3d" }}
               >
                 <img src={viaverseLogo} alt="Viaverse" className="w-10 h-10 drop-shadow-sm object-contain" />
               </motion.div>
            </motion.div>
          </div>

          <motion.h1 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1, duration: 0.4 }}
            className="text-[20px] font-extrabold text-content tracking-tight mb-2 leading-tight"
          >
            Hizmet vermeye başla
          </motion.h1>
          
          <motion.p 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2, duration: 0.4 }}
            className="text-[12px] text-content-muted leading-relaxed font-medium mb-10 max-w-[90%]"
          >
            Yeteneğini, deneyimini ya da işletmeni Viaverse'de görünür yap. Gelen talepleri yönet, teklif ver ve işlerini tek yerden takip et.
          </motion.p>

          {/* List Items (Features) */}
          <motion.div 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3, duration: 0.4 }}
            className="space-y-0"
          >
             {/* Item 1 */}
             <div className="flex items-start gap-4 py-4 border-b border-border-subtle">
               <div className="w-10 h-10 rounded-full bg-surface-muted flex items-center justify-center shrink-0 border border-border-faint">
                 <User className="w-5 h-5 text-content" strokeWidth={1.5} />
               </div>
               <div className="flex flex-col pt-0.5">
                 <span className="text-[12px] font-extrabold text-content tracking-tight">Profilini oluştur</span>
                 <span className="text-[10px] text-content-muted font-medium mt-0.5 leading-snug">Müşterilerin seni, işlerini ve tarzını nasıl göreceğini düzenle.</span>
               </div>
             </div>

             {/* Item 2 */}
             <div className="flex items-start gap-4 py-4 border-b border-border-subtle">
               <div className="w-10 h-10 rounded-full bg-surface-muted flex items-center justify-center shrink-0 border border-border-faint">
                 <Briefcase className="w-5 h-5 text-[#F97316]" strokeWidth={1.5} />
               </div>
               <div className="flex flex-col pt-0.5">
                 <span className="text-[12px] font-extrabold text-content tracking-tight">Hizmetlerini seç</span>
                 <span className="text-[10px] text-content-muted font-medium mt-0.5 leading-snug">Kategori, alt kategori, hizmet bölgesi ve çalışma alanlarını belirle.</span>
               </div>
             </div>

             {/* Item 3 */}
             <div className="flex items-start gap-4 py-4 border-b border-border-subtle">
               <div className="w-10 h-10 rounded-full bg-surface-muted flex items-center justify-center shrink-0 border border-border-faint">
                 <MessageSquare className="w-5 h-5 text-content" strokeWidth={1.5} />
               </div>
               <div className="flex flex-col pt-0.5">
                 <span className="text-[12px] font-extrabold text-content tracking-tight">Taleplere yanıt ver</span>
                 <span className="text-[10px] text-content-muted font-medium mt-0.5 leading-snug">Uygun işlere teklif ver, aktif işlerini takip et.</span>
               </div>
             </div>
          </motion.div>

          {/* Info Area */}
          <motion.div 
             initial={{ opacity: 0, y: 10 }}
             animate={{ opacity: 1, y: 0 }}
             transition={{ delay: 0.4, duration: 0.4 }}
             className="mt-6 pt-5 border-t border-border-subtle flex items-start gap-3"
          >
             <ShieldCheck className="w-4 h-4 text-[#10B981] shrink-0 mt-0.5" strokeWidth={2} />
             <div className="flex flex-col">
                <span className="text-[12px] font-extrabold text-content tracking-tight mb-0.5">Modun her zaman belli olur</span>
                <span className="text-[10px] text-content-muted leading-relaxed font-medium">Hizmet veren panelindeyken ayrı başlık ve ayrı menü görürsün. İstediğin zaman üye moduna dönebilirsin.</span>
             </div>
          </motion.div>

        </div>

        {/* Bottom CTA Area */}
        <div className="absolute bottom-0 left-0 right-0 bg-surface/90 backdrop-blur-md px-6 py-4 pb-safe border-t border-border-faint z-40">
          <motion.div
             initial={{ opacity: 0, y: 10 }}
             animate={{ opacity: 1, y: 0 }}
             transition={{ delay: 0.5, duration: 0.3 }}
          >
            <button 
              onClick={() => setShowSetup(true)} 
              className="w-full py-3.5 bg-[#F97316] hover:bg-[#EA580C] text-white rounded-xl font-extrabold text-[14px] tracking-tight shadow-sm transition-colors active:scale-[0.98] flex items-center justify-center gap-2"
            >
              Hizmet vermeye başla
            </button>
            <p className="text-[10px] text-content-muted text-center mt-3 font-medium px-4">
              İstersen daha sonra profilinden tekrar devam edebilirsin.
            </p>
            <button 
              onClick={onBack}
              className="w-full py-2.5 mt-1 text-[12px] font-extrabold text-content-muted hover:text-content transition-colors tracking-tight text-center"
            >
              Şimdilik geç
            </button>
            
            {/* Temporary Test Shortcut */}
            <div className="mt-4 pt-4 border-t border-border-faint flex flex-col items-center justify-center">
               <button 
                 onClick={() => setSkipToDashboard(true)}
                 className="text-[11px] font-bold text-content-muted hover:text-[#F97316] transition-colors"
               >
                 Atla ve panele geç
               </button>
               <span className="text-[9px] text-content-muted/70 mt-0.5">Geçici test kısayolu</span>
            </div>
          </motion.div>
        </div>

      </div>
    </div>
  );
}
