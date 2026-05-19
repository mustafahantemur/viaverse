import { useState, useRef } from "react";
import { motion } from "motion/react";
import { ChevronLeft, Camera, Image as ImageIcon, Link as LinkIcon, Eye, CheckCircle2 } from "lucide-react";
import ProviderProfilePreviewScreen from "./ProviderProfilePreviewScreen";

interface ProviderSetupProfileScreenProps {
  onBack: () => void;
  setupData: any;
  onComplete: (profileData: any) => void;
}

const SUGGESTED_TAGS = [
  "Hızlı yanıt", "Online çalışır", "Yerinde hizmet", 
  "Portföy mevcut", "Deneyimli", "Yaratıcı işler", 
  "Teknik destek", "Eğitim", "Danışmanlık", 
  "İçerik üretimi", "Tasarım", "Yazılım"
];

export default function ProviderSetupProfileScreen({ onBack, setupData, onComplete }: ProviderSetupProfileScreenProps) {
  const [displayName, setDisplayName] = useState("");
  const [serviceTitle, setServiceTitle] = useState("");
  const [description, setDescription] = useState("");
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [showPreview, setShowPreview] = useState(false);
  
  const fileInputRef = useRef<HTMLInputElement>(null);

  const toggleTag = (tag: string) => {
    setSelectedTags(prev => 
      prev.includes(tag) ? prev.filter(t => t !== tag) : [...prev, tag]
    );
  };

  const isFormValid = displayName.trim().length > 0 && serviceTitle.trim().length > 0;

  const handleComplete = () => {
    if (isFormValid) {
      onComplete({
        ...setupData,
        profile: {
          displayName,
          serviceTitle,
          description,
          tags: selectedTags
        }
      });
    }
  };

  if (showPreview) {
    const previewData = {
      ...setupData,
      profile: {
        displayName: displayName || "Görünen Ad",
        serviceTitle: serviceTitle || "Hizmet Başlığı",
        description: description || "",
        tags: selectedTags
      }
    };
    return (
      <ProviderProfilePreviewScreen 
        onBack={() => setShowPreview(false)}
        previewData={previewData}
        onComplete={handleComplete}
      />
    );
  }

  return (
    <div className="min-h-screen bg-surface flex justify-center text-content font-sans">
      <div className="w-full max-w-md bg-surface min-h-screen relative flex flex-col">
        
        {/* Top Header */}
        <div className="flex items-center justify-between pt-4 pb-2 px-4 sticky top-0 bg-surface/90 backdrop-blur-md z-30">
          <button 
            onClick={onBack} 
            className="w-10 h-10 -ml-2 text-content-muted flex items-center justify-center hover:text-content transition-colors rounded-full active:scale-95"
          >
             <ChevronLeft className="w-6 h-6" strokeWidth={1.5} />
          </button>
          
          <div className="flex items-center gap-1.5 px-3 py-1 bg-[#10B981]/10 rounded-full border border-[#10B981]/20">
             <span className="text-[10px] font-bold text-[#10B981] uppercase tracking-wide">Hizmet Veren Kurulumu</span>
          </div>
          
          <div className="w-10 h-10 -mr-2 flex items-center justify-center">
            <span className="text-[10px] font-bold text-content-muted">4 / 4</span>
          </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto px-6 pt-4 pb-32">
          
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4 }}
            className="mb-8"
          >
            <h1 className="text-[20px] font-extrabold text-content tracking-tight mb-2 leading-tight">
              Hizmet profilini düzenle
            </h1>
            <p className="text-[12px] text-content-muted leading-relaxed font-medium">
              Müşterilerin seni, işlerini ve tarzını bu bilgilerle görecek.
            </p>
          </motion.div>

          <motion.div 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1, duration: 0.4 }}
            className="space-y-6"
          >
            {/* 1. Profil fotoğrafı / logo */}
            <div className="flex items-start gap-4 pb-6 border-b border-border-faint">
              <button 
                onClick={() => fileInputRef.current?.click()}
                className="w-[72px] h-[72px] rounded-[24px] bg-surface-muted border border-border-subtle hover:border-border-strong flex flex-col items-center justify-center gap-1 shrink-0 transition-colors"
              >
                <Camera className="w-5 h-5 text-content-muted" strokeWidth={1.5} />
                <span className="text-[9px] font-extrabold text-content-muted tracking-tight">Ekle</span>
              </button>
              <input type="file" ref={fileInputRef} className="hidden" accept="image/*" />
              <div className="flex flex-col justify-center pt-2">
                <span className="text-[12px] font-bold text-content tracking-tight">Profil fotoğrafı veya logo</span>
                <span className="text-[10px] text-content-muted font-medium mt-1 leading-snug">
                  Müşterilerin seni tanımasını kolaylaştırır.
                </span>
              </div>
            </div>

            {/* 2. Görünen ad */}
            <div className="pb-6 border-b border-border-faint">
              <label className="block text-[12px] font-bold text-content tracking-tight mb-2">Görünen ad</label>
              <input 
                type="text" 
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
                placeholder="Örn: Zeynep Kaya, Nova Design Studio"
                className="w-full bg-surface border border-border-strong rounded-xl px-4 py-3 text-[13px] text-content placeholder-content-muted focus:outline-none focus:border-[#F97316]/50 focus:bg-white transition-colors font-medium"
              />
              <p className="text-[10px] text-content-muted mt-2 font-medium">Bu ad müşterilere hizmet profilinde görünür.</p>
            </div>

            {/* 3. Hizmet başlığı */}
            <div className="pb-6 border-b border-border-faint">
              <label className="block text-[12px] font-bold text-content tracking-tight mb-2">Hizmet başlığı</label>
              <input 
                type="text" 
                value={serviceTitle}
                onChange={(e) => setServiceTitle(e.target.value)}
                placeholder="Örn: UX/UI Designer, Temizlik Hizmeti"
                className="w-full bg-surface border border-border-strong rounded-xl px-4 py-3 text-[13px] text-content placeholder-content-muted focus:outline-none focus:border-[#F97316]/50 focus:bg-white transition-colors font-medium"
              />
              <p className="text-[10px] text-content-muted mt-2 font-medium">Ne sunduğunu birkaç kelimeyle anlat.</p>
            </div>

            {/* 4. Kendini anlat */}
            <div className="pb-6 border-b border-border-faint">
              <label className="block text-[12px] font-bold text-content tracking-tight mb-2">Kendini ve hizmetini anlat</label>
              <textarea 
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Nasıl çalıştığını, hangi işlerde iyi olduğunu ve müşterilere nasıl yardımcı olabileceğini kısaca yaz."
                className="w-full bg-surface border border-border-strong rounded-xl px-4 py-3 text-[13px] text-content placeholder-content-muted focus:outline-none focus:border-[#F97316]/50 focus:bg-white transition-colors font-medium resize-none min-h-[96px]"
              />
              <p className="text-[10px] text-content-muted mt-2 font-medium">2-3 cümle yeterli. Daha sonra panelinden düzenleyebilirsin.</p>
            </div>

            {/* 5. Öne çıkan alanlar */}
            <div className="pb-6 border-b border-border-faint">
              <div className="mb-3">
                <span className="block text-[12px] font-bold text-content tracking-tight">Öne çıkan alanlar</span>
                <span className="text-[10px] text-content-muted font-medium mt-1 leading-snug block">Müşterilerin seni daha hızlı anlaması için birkaç etiket ekle.</span>
              </div>
              <div className="flex flex-wrap gap-2">
                {SUGGESTED_TAGS.map(tag => {
                  const isSelected = selectedTags.includes(tag);
                  return (
                    <button
                      key={tag}
                      onClick={() => toggleTag(tag)}
                      className={`px-3 py-1.5 rounded-full border text-[10px] font-bold transition-all flex items-center ${
                        isSelected 
                          ? "bg-[#F97316] border-[#F97316] text-white" 
                          : "bg-surface border-border-strong text-content-muted hover:border-border-heavy hover:text-content"
                      }`}
                    >
                      {isSelected && <CheckCircle2 className="w-3 h-3 mr-1" strokeWidth={2.5}/>}
                      {tag}
                    </button>
                  );
                })}
              </div>
            </div>

            {/* 6. Portföy ve örnek işler */}
            <div className="pb-6 border-b border-border-faint">
              <div className="mb-3">
                <span className="block text-[12px] font-bold text-content tracking-tight">Portföy ve örnek işler</span>
                <span className="text-[10px] text-content-muted font-medium mt-1 leading-snug block">Daha önce yaptığın işleri, görselleri veya bağlantıları ekleyebilirsin.</span>
              </div>
              
              <div className="flex gap-2 mb-4">
                <button className="flex items-center gap-1.5 px-3 py-2 bg-surface hover:bg-surface-muted border border-border-subtle rounded-xl text-[10px] font-extrabold text-content transition-colors active:scale-95">
                  <ImageIcon className="w-4 h-4" strokeWidth={1.5} /> Görsel ekle
                </button>
                <button className="flex items-center gap-1.5 px-3 py-2 bg-surface hover:bg-surface-muted border border-border-subtle rounded-xl text-[10px] font-extrabold text-content transition-colors active:scale-95">
                  <LinkIcon className="w-4 h-4" strokeWidth={1.5} /> Bağlantı ekle
                </button>
              </div>

              <div className="bg-surface-muted border border-border-faint border-dashed rounded-xl p-3 flex items-center justify-center text-center">
                <span className="text-[10px] text-content-muted/80 font-medium italic">
                  Behance, Instagram, web sitesi, video linki veya örnek çalışma...
                </span>
              </div>
            </div>

            {/* 7. Profil önizleme bağlantısı */}
            <div className="flex items-center justify-between p-4 bg-surface rounded-xl border border-border-subtle">
              <span className="text-[12px] font-bold text-content tracking-tight">Profilin müşterilere böyle görünecek</span>
              <button 
                onClick={() => setShowPreview(true)}
                className="flex items-center gap-1 text-[10px] font-extrabold text-[#F97316] hover:text-[#EA580C] transition-colors"
              >
                Önizlemeyi gör <Eye className="w-3.5 h-3.5" strokeWidth={2}/>
              </button>
            </div>

          </motion.div>
        </div>

        {/* Bottom CTA Area */}
        <div className="absolute bottom-0 left-0 right-0 bg-surface/90 backdrop-blur-md px-6 py-4 pb-safe border-t border-border-faint z-40">
          <motion.div
             initial={{ opacity: 0, y: 10 }}
             animate={{ opacity: 1, y: 0 }}
             transition={{ delay: 0.2, duration: 0.3 }}
          >
            <button 
              onClick={handleComplete}
              disabled={!isFormValid}
              className={`w-full py-3.5 rounded-xl font-extrabold text-[14px] tracking-tight shadow-sm transition-all flex items-center justify-center gap-2 ${
                isFormValid 
                  ? "bg-[#F97316] hover:bg-[#EA580C] text-white active:scale-[0.98]" 
                  : "bg-surface-muted border border-border-strong text-content-muted cursor-not-allowed"
              }`}
            >
              Kurulumu tamamla
            </button>
            <p className="text-[10px] text-content-muted text-center mt-3 font-medium px-4">
              Bu bilgileri daha sonra hizmet veren panelinden düzenleyebilirsin.
            </p>
          </motion.div>
        </div>

      </div>
    </div>
  );
}
