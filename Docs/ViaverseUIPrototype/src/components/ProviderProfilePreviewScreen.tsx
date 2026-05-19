import { useState } from "react";
import { motion } from "motion/react";
import { ChevronLeft, User, MapPin, Globe, CheckCircle2, ShieldCheck, Image as ImageIcon, Link as LinkIcon, MessageSquare, Briefcase } from "lucide-react";
import ProviderDashboardScreen from "./ProviderDashboardScreen";

interface ProviderProfilePreviewScreenProps {
  onBack: () => void;
  onComplete: () => void;
  previewData: any;
}

export default function ProviderProfilePreviewScreen({ onBack, onComplete, previewData }: ProviderProfilePreviewScreenProps) {
  const [showDashboard, setShowDashboard] = useState(false);

  const profile = previewData?.profile || {};
  const categories = previewData?.selectedCategories || [];
  const workArea = previewData?.workAreaData || {};

  if (showDashboard) {
    return (
      <ProviderDashboardScreen 
        onBackToUserMode={() => {
          console.log("Üye moduna dönüldü, routing daha sonra entegre edilecek.");
          onComplete(); // Use the original complete to navigate out or similar
        }}
        profileData={previewData}
      />
    );
  }

  return (
    <div className="min-h-screen bg-surface flex justify-center text-content font-sans">
      <div className="w-full max-w-md bg-surface min-h-screen relative flex flex-col">
        
        {/* Top Header */}
        <div className="flex items-center justify-between p-4 sticky top-0 bg-surface/90 backdrop-blur-md z-30 border-b border-border-faint">
          <button 
            onClick={onBack} 
            className="w-10 h-10 -ml-2 text-content-muted flex items-center justify-center hover:text-content transition-colors rounded-full active:scale-95"
          >
             <ChevronLeft className="w-6 h-6" strokeWidth={1.5} />
          </button>
          
          <div className="flex flex-col items-center">
             <span className="text-[15px] font-extrabold text-content tracking-tight">Profil önizlemesi</span>
             <span className="text-[9px] text-content-muted font-medium mt-0.5">Müşteriler böyle görecek</span>
          </div>
          
          <div className="flex items-center justify-center w-10 h-10 -mr-2">
            <div className="w-2 h-2 rounded-full bg-[#10B981] animate-pulse" />
          </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto pb-32">
          
          {/* 1. Header Profile Area */}
          <div className="px-5 pt-6 pb-5 border-b border-border-faint">
            <div className="flex items-start gap-4">
              <div className="w-16 h-16 rounded-[20px] bg-surface-muted border border-border-strong flex flex-col items-center justify-center shrink-0">
                <User className="w-6 h-6 text-content-muted/50" />
              </div>
              <div className="flex flex-col justify-center pt-1">
                <div className="flex items-center gap-1.5 mb-1">
                  <h1 className="text-[18px] font-extrabold text-content tracking-tight leading-none">
                    {profile.displayName || "Görünen Ad"}
                  </h1>
                  <ShieldCheck className="w-4 h-4 text-[#10B981]" strokeWidth={2.5} />
                </div>
                <h2 className="text-[12px] font-bold text-content-muted mb-2 leading-snug">
                  {profile.serviceTitle || "Hizmet Başlığı"}
                </h2>
                <div className="inline-flex items-center px-2 py-0.5 rounded-md bg-surface-muted border border-border-subtle max-w-max">
                  <span className="text-[9px] font-bold text-content-muted">Hizmet Veren Profili</span>
                </div>
              </div>
            </div>
            
            <p className="text-[10px] text-content mt-5 leading-relaxed font-medium">
              {profile.description || "Ne sunduğunu, nasıl çalıştığını ve hangi işlerde iyi olduğunu anlatan kısa tanıtım yazısı burada görünecek."}
            </p>
          </div>

          {/* 2. Kategoriler */}
          <div className="px-5 py-5 border-b border-border-faint">
            <h3 className="text-[12px] font-bold text-content tracking-tight mb-3">Hizmet Alanları</h3>
            <div className="flex flex-wrap gap-2">
              {categories.length > 0 ? (
                <>
                  {categories.slice(0, 4).map((c: string, i: number) => (
                    <span key={i} className="px-2.5 py-1.5 rounded-lg bg-surface-muted border border-border-subtle text-[10px] font-medium text-content tracking-tight">
                      {c}
                    </span>
                  ))}
                  {categories.length > 4 && (
                    <span className="px-2.5 py-1.5 rounded-lg bg-surface-muted border border-border-subtle text-[10px] font-bold text-content-muted">
                      +{categories.length - 4} hizmet
                    </span>
                  )}
                </>
              ) : (
                <span className="text-[10px] text-content-muted italic">Kategori bilgisi seçilmedi</span>
              )}
            </div>
          </div>

          {/* 3. Çalışma Şekli */}
          <div className="px-5 py-5 border-b border-border-faint">
            <h3 className="text-[12px] font-bold text-content tracking-tight mb-3">Çalışma Şekli</h3>
            <div className="flex flex-col gap-3">
              {(workArea.workType === "online" || workArea.workType === "hybrid") && (
                <div className="flex items-start gap-3">
                  <Globe className="w-4 h-4 text-[#F97316] shrink-0 mt-0.5" strokeWidth={2} />
                  <div>
                    <span className="block text-[10px] font-bold text-content mb-0.5">Online Hizmet Veriyor</span>
                    <span className="block text-[10px] text-content-muted">
                      {workArea.onlineScope?.length > 0 ? workArea.onlineScope.join(", ") : "Uzaktan iletişimle çalışır"}
                    </span>
                  </div>
                </div>
              )}
              
              {(workArea.workType === "onsite" || workArea.workType === "hybrid") && (
                <div className="flex items-start gap-3">
                  <MapPin className="w-4 h-4 text-[#F97316] shrink-0 mt-0.5" strokeWidth={2} />
                  <div>
                    <span className="block text-[10px] font-bold text-content mb-0.5">Yerinde Hizmet Veriyor</span>
                    <span className="block text-[10px] text-content-muted">
                      {workArea.district || "Bölge"} / {workArea.city || "Şehir"} • Dinamik çevrede {workArea.distance || 10} km
                    </span>
                  </div>
                </div>
              )}

              {(!workArea.workType) && (
                <span className="text-[10px] text-content-muted italic">Çalışma alanı belirtilmemiş</span>
              )}
            </div>
          </div>

          {/* 4. Öne Çıkan Özellikler */}
          {profile.tags && profile.tags.length > 0 && (
            <div className="px-5 py-5 border-b border-border-faint">
               <h3 className="text-[12px] font-bold text-content tracking-tight mb-3">Öne Çıkanlar</h3>
               <div className="flex flex-wrap gap-2">
                 {profile.tags.map((tag: string, i: number) => (
                   <span key={i} className="inline-flex items-center px-2 py-1 bg-[#F97316]/5 border border-[#F97316]/20 rounded-full text-[9px] font-bold text-[#F97316] tracking-tight">
                     <CheckCircle2 className="w-3 h-3 mr-1" strokeWidth={2.5}/>
                     {tag}
                   </span>
                 ))}
               </div>
            </div>
          )}

          {/* 5. Portföy ve Örnek İşler */}
          <div className="px-5 py-5 border-b border-border-faint">
             <h3 className="text-[12px] font-bold text-content tracking-tight mb-3">Portföy ve Örnek İşler</h3>
             <div className="flex flex-col gap-2">
                <div className="flex items-center gap-3 p-3 bg-surface-muted border border-border-subtle rounded-xl">
                  <ImageIcon className="w-4 h-4 text-content-muted" />
                  <div className="flex flex-col">
                     <span className="text-[10px] font-bold text-content">Görsel galerisi</span>
                     <span className="text-[9px] text-content-muted">Çalışmalar veya önce/sonra fotoğrafları</span>
                  </div>
                </div>
                <div className="flex items-center gap-3 p-3 bg-surface-muted border border-border-subtle rounded-xl">
                  <LinkIcon className="w-4 h-4 text-content-muted" />
                  <div className="flex flex-col">
                     <span className="text-[10px] font-bold text-content">Dış bağlantılar</span>
                     <span className="text-[9px] text-content-muted">Behance, Instagram veya web sitesi</span>
                  </div>
                </div>
             </div>
          </div>

          {/* 6. Güven */}
          <div className="px-5 py-5">
             <h3 className="text-[12px] font-bold text-content tracking-tight mb-3">Profil Güvenliği</h3>
             <div className="space-y-2">
                <div className="flex items-center gap-2 text-[10px] text-content font-medium">
                  <CheckCircle2 className="w-3.5 h-3.5 text-[#10B981]" strokeWidth={2.5}/>
                  Kimlik bilgisi doğrulandı
                </div>
                <div className="flex items-center gap-2 text-[10px] text-content font-medium">
                  <CheckCircle2 className="w-3.5 h-3.5 text-[#10B981]" strokeWidth={2.5}/>
                  İletişim adresleri onaylı
                </div>
             </div>
          </div>

          {/* 7. Dummy Actions (Müşteri Modu Gösterimi) */}
          <div className="mx-5 my-6 p-4 border border-dashed border-[#F97316]/40 bg-[#F97316]/5 rounded-2xl relative">
             <span className="absolute -top-2 left-4 bg-[#F97316]/10 text-[#EA580C] text-[8px] font-extrabold px-2 py-0.5 rounded-full border border-[#F97316]/20">
               Müşteri Görünümü
             </span>
             <p className="text-[10px] text-content-muted/80 font-medium mb-3 mt-1">
               Müşteriler hizmet almak için profilinde bu butonları görür ve seninle bu yolla iletişime geçer.
             </p>
             <div className="flex gap-2">
               <button className="flex-1 py-3 bg-[#F97316] text-white rounded-xl text-[12px] font-bold flex items-center justify-center pointer-events-none opacity-80">
                 <Briefcase className="w-4 h-4 mr-1.5" strokeWidth={2}/> Teklif al
               </button>
               <button className="flex-1 py-3 bg-surface border border-border-strong text-content rounded-xl text-[12px] font-bold flex items-center justify-center pointer-events-none opacity-80">
                 <MessageSquare className="w-4 h-4 mr-1.5" strokeWidth={2}/> Mesaj gönder
               </button>
             </div>
          </div>

        </div>

        {/* Bottom CTA Area */}
        <div className="absolute bottom-0 left-0 right-0 bg-surface/90 backdrop-blur-md px-6 py-4 pb-safe border-t border-border-faint z-40">
          <div className="flex flex-col gap-2">
            <button 
              onClick={() => setShowDashboard(true)}
              className="w-full py-3.5 rounded-xl font-extrabold text-[14px] tracking-tight shadow-sm transition-all flex items-center justify-center gap-2 bg-[#F97316] hover:bg-[#EA580C] text-white active:scale-[0.98]"
            >
              Kurulumu tamamla
            </button>
            <button 
              onClick={onBack}
              className="w-full py-2.5 rounded-xl font-bold text-[12px] text-content-muted hover:text-content transition-colors flex items-center justify-center"
            >
              Düzenlemeye dön
            </button>
          </div>
        </div>

      </div>
    </div>
  );
}
