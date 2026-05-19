import { useState } from "react";
import { motion } from "motion/react";
import { ChevronLeft, User, Building2, Palette, Lightbulb, CheckCircle2 } from "lucide-react";
import ProviderSetupCategoriesScreen from "./ProviderSetupCategoriesScreen";

interface ProviderSetupTypeScreenProps {
  onBack: () => void;
  onNext: (selectedType: string) => void;
}

const PROVIDER_TYPES = [
  {
    id: "individual",
    title: "Bireysel hizmet veren",
    description: "Tek başına hizmet veriyor, teklifleri kendi adına yönetiyorsan.",
    examples: "tamirci, eğitmen, temizlik, bakım",
    icon: User
  },
  {
    id: "business",
    title: "İşletme / ekip",
    description: "Bir işletme, ofis, atölye veya ekip adına hizmet veriyorsan.",
    examples: "temizlik firması, ajans, güzellik salonu",
    icon: Building2
  },
  {
    id: "creative",
    title: "Yaratıcı / dijital profil",
    description: "Tasarım, sosyal medya, içerik, yazılım veya yaratıcı işler sunuyorsan.",
    examples: "UX designer, içerik üreticisi, yazılımcı",
    icon: Palette
  },
  {
    id: "consultant",
    title: "Serbest uzman / danışman",
    description: "Deneyimini, bilgisini veya profesyonel uzmanlığını hizmete dönüştürüyorsan.",
    examples: "hukuk, muhasebe, danışman, koç",
    icon: Lightbulb
  }
];

export default function ProviderSetupTypeScreen({ onBack, onNext }: ProviderSetupTypeScreenProps) {
  const [selectedType, setSelectedType] = useState<string | null>(null);
  const [showCategories, setShowCategories] = useState(false);

  const handleNext = () => {
    if (selectedType) {
      setShowCategories(true);
    }
  };

  if (showCategories) {
    return (
      <ProviderSetupCategoriesScreen 
        onBack={() => setShowCategories(false)}
        selectedType={selectedType}
        onNext={(selectedCategories) => {
          console.log("Seçilen Tip:", selectedType);
          alert("Kurulum adımları tamamlandı!\n\nSeçilen Tip: " + selectedType + "\nKategori Sayısı: " + selectedCategories.length + "\n\n(Çalışma şekli bilgisi loga yazdırıldı.)\nSonraki adım daha sonra eklenecek.");
        }}
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
            <span className="text-[10px] font-bold text-content-muted">1 / 4</span>
          </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto px-6 pt-4 pb-32">
          
          {/* Title Area */}
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4 }}
            className="mb-8"
          >
            <h1 className="text-[20px] font-extrabold text-content tracking-tight mb-2 leading-tight">
              Hizmet profilini oluşturalım
            </h1>
            <p className="text-[12px] text-content-muted leading-relaxed font-medium">
              Seni müşterilere nasıl göstereceğimizi seç. Bu seçimi daha sonra hizmet veren panelinden düzenleyebilirsin.
            </p>
          </motion.div>

          {/* Options List */}
          <motion.div 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1, duration: 0.4 }}
            className="flex flex-col space-y-3"
          >
            {PROVIDER_TYPES.map((type) => {
              const isSelected = selectedType === type.id;
              const Icon = type.icon;
              
              return (
                <button
                  key={type.id}
                  onClick={() => setSelectedType(type.id)}
                  className={`relative flex items-start p-4 w-full text-left transition-all duration-200 border rounded-xl overflow-hidden ${
                    isSelected 
                      ? "border-[#F97316] bg-[#F97316]/5 shadow-sm" 
                      : "border-border-subtle bg-transparent hover:border-border-strong hover:bg-surface-muted"
                  }`}
                >
                  {/* Left Indicator bar when selected */}
                  {isSelected && (
                    <div className="absolute left-0 top-0 bottom-0 w-1 bg-[#F97316]" />
                  )}

                  <div className="flex-1 flex gap-3">
                    <div className={`shrink-0 w-10 h-10 rounded-full flex items-center justify-center border transition-colors ${
                      isSelected ? "bg-white border-[#F97316]/20" : "bg-surface border-border-faint"
                    }`}>
                      <Icon className={`w-5 h-5 ${isSelected ? "text-[#F97316]" : "text-content-muted"}`} strokeWidth={selectedType === type.id ? 2 : 1.5} />
                    </div>
                    
                    <div className="flex flex-col pt-0.5 max-w-[85%]">
                      <span className={`text-[12px] font-extrabold tracking-tight ${isSelected ? "text-[#F97316]" : "text-content"}`}>
                        {type.title}
                      </span>
                      <span className="text-[10px] text-content-muted font-medium mt-1 leading-snug">
                        {type.description}
                      </span>
                      <span className="text-[9px] text-content-muted/80 font-medium mt-2 italic leading-tight">
                        Örn: {type.examples}
                      </span>
                    </div>
                  </div>

                  {/* Check Icon Right */}
                  <div className={`shrink-0 ml-2 mt-2 transition-opacity ${isSelected ? "opacity-100" : "opacity-0"}`}>
                    <CheckCircle2 className="w-5 h-5 text-[#F97316]" strokeWidth={2} />
                  </div>
                </button>
              );
            })}
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
              onClick={handleNext}
              disabled={!selectedType}
              className={`w-full py-3.5 rounded-xl font-extrabold text-[14px] tracking-tight shadow-sm transition-all flex items-center justify-center gap-2 ${
                selectedType 
                  ? "bg-[#F97316] hover:bg-[#EA580C] text-white active:scale-[0.98]" 
                  : "bg-surface-muted border border-border-strong text-content-muted cursor-not-allowed"
              }`}
            >
              Devam et
            </button>
            <p className="text-[10px] text-content-muted text-center mt-3 font-medium px-4">
              Bu seçim, profil alanlarını ve önerilen ayarları kişiselleştirir.
            </p>
          </motion.div>
        </div>

      </div>
    </div>
  );
}
