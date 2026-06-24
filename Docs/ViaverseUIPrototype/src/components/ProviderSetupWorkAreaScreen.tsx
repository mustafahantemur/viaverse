import { useState } from "react";
import { motion, AnimatePresence } from "motion/react";
import { ChevronLeft, Globe, MapPin, Layers, CheckCircle2 } from "lucide-react";
import ProviderSetupProfileScreen from "./ProviderSetupProfileScreen";

interface ProviderSetupWorkAreaScreenProps {
  onBack: () => void;
  selectedCategories: string[];
  onNext: (data: any) => void;
}

const WORK_TYPES = [
  {
    id: "online",
    title: "Online hizmet veriyorum",
    description: "Tasarım, yazılım, içerik, danışmanlık, eğitim veya dijital işler için uygundur.",
    examples: "UX design, sosyal medya, yazılım, video kurgu, çeviri, online ders",
    icon: Globe
  },
  {
    id: "onsite",
    title: "Yerinde hizmet veriyorum",
    description: "Fiziksel olarak belirli bölgelerde hizmet veriyorsan seç.",
    examples: "Tamirat, temizlik, bakım, kurye, organizasyon, evcil hayvan hizmetleri",
    icon: MapPin
  },
  {
    id: "hybrid",
    title: "Hem online hem yerinde",
    description: "Bazı işleri online, bazı işleri belirli bölgelerde yapıyorsan seç.",
    examples: "Danışmanlık + yerinde keşif, online eğitim + yüz yüze destek",
    icon: Layers
  }
];

export default function ProviderSetupWorkAreaScreen({ onBack, selectedCategories, onNext }: ProviderSetupWorkAreaScreenProps) {
  const [workType, setWorkType] = useState<"online" | "onsite" | "hybrid" | null>(null);
  
  const [onlineScope, setOnlineScope] = useState<string[]>([]);
  const [city, setCity] = useState("");
  const [district, setDistrict] = useState("");
  const [distance, setDistance] = useState<number>(10);
  
  const [showProfile, setShowProfile] = useState(false);

  const toggleOnlineScope = (scope: string) => {
    setOnlineScope(prev => 
      prev.includes(scope) ? prev.filter(s => s !== scope) : [...prev, scope]
    );
  };

  const handleNext = () => {
    if (workType) {
      setShowProfile(true);
    }
  };

  if (showProfile) {
    const workAreaData = { workType, onlineScope, city, district, distance };
    return (
      <ProviderSetupProfileScreen
        onBack={() => setShowProfile(false)}
        setupData={{ selectedCategories, workAreaData }}
        onComplete={(finalProfileData) => {
          console.log("🚀 Kurulum Tamamlandı!");
          console.log(finalProfileData);
          alert("Hizmet veren profiliniz başarıyla oluşturuldu!\n\n(Detayları geliştirici konsolunda görebilirsiniz.)\n\nHizmet veren paneli yapım aşamasındadır.");
          
          // Bubble up or simulate finish
          onNext(finalProfileData);
        }}
      />
    );
  }

  const renderOnlineForm = () => (
    <motion.div 
      initial={{ opacity: 0, height: 0 }}
      animate={{ opacity: 1, height: "auto" }}
      exit={{ opacity: 0, height: 0 }}
      className="mt-6 pt-6 border-t border-border-faint space-y-4"
    >
      <div>
        <h3 className="text-[12px] font-bold text-content tracking-tight mb-1">Online çalışma kapsamı</h3>
        <p className="text-[10px] text-content-muted leading-relaxed">
          Online hizmetlerde dinamik çevre mesafe sınırı zorunlu değildir. Müşteriler seni hizmet alanına ve uygunluk durumuna göre bulabilir.
        </p>
      </div>
      <div className="flex flex-col space-y-2">
        {["Türkiye geneli", "Dünya geneli", "Sadece seçtiğim dillerde", "Sadece belirli zaman dilimlerinde"].map(scope => {
          const isSelected = onlineScope.includes(scope);
          return (
            <button 
              key={scope}
              onClick={() => toggleOnlineScope(scope)}
              className={`flex items-center justify-between p-3 rounded-xl border transition-all ${
                isSelected ? "bg-[#F97316]/5 border-[#F97316]/30 text-[#EA580C]" : "bg-surface border-border-subtle text-content-muted hover:bg-surface-muted"
              }`}
            >
              <span className={`text-[10px] text-left leading-snug ${isSelected ? "font-bold tracking-tight" : "font-medium"}`}>{scope}</span>
              <div className={`shrink-0 ml-2 transition-opacity ${isSelected ? "opacity-100" : "opacity-0"}`}>
                <CheckCircle2 className="w-4 h-4 text-[#F97316]" strokeWidth={2} />
              </div>
            </button>
          );
        })}
      </div>
    </motion.div>
  );

  const renderOnsiteForm = () => (
    <motion.div 
      initial={{ opacity: 0, height: 0 }}
      animate={{ opacity: 1, height: "auto" }}
      exit={{ opacity: 0, height: 0 }}
      className="mt-6 pt-6 border-t border-border-faint space-y-4"
    >
      <div>
        <h3 className="text-[12px] font-bold text-content tracking-tight mb-1">Hizmet bölgen</h3>
        <p className="text-[10px] text-content-muted leading-relaxed">
          Dinamik çevrende hangi mesafeye kadar yerinde hizmet vereceğini seç.
        </p>
      </div>
      
      <div className="space-y-4">
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-[10px] font-bold text-content-muted mb-1.5 px-1">Şehir</label>
            <input 
              type="text" 
              placeholder="Örn: İstanbul" 
              value={city}
              onChange={(e) => setCity(e.target.value)}
              className="w-full bg-surface border border-border-strong rounded-xl px-3 py-2.5 text-[12px] text-content focus:outline-none focus:border-[#F97316]/50 transition-colors"
            />
          </div>
          <div>
            <label className="block text-[10px] font-bold text-content-muted mb-1.5 px-1">İlçe / bölge</label>
            <input 
              type="text" 
              placeholder="Örn: Kadıköy" 
              value={district}
              onChange={(e) => setDistrict(e.target.value)}
              className="w-full bg-surface border border-border-strong rounded-xl px-3 py-2.5 text-[12px] text-content focus:outline-none focus:border-[#F97316]/50 transition-colors"
            />
          </div>
        </div>

        <div className="pt-2">
          <div className="flex items-center justify-between px-1 mb-3">
            <label className="block text-[12px] font-bold text-content">Hizmet mesafesi</label>
            <span className="text-[12px] font-bold text-[#F97316]">{distance} km</span>
          </div>
          <div className="px-1">
            <input 
              type="range"
              min="1"
              max="50"
              step="1"
              value={distance}
              onChange={(e) => setDistance(Number(e.target.value))}
              className="w-full h-1.5 rounded-full appearance-none cursor-pointer outline-none"
              style={{
                background: `linear-gradient(to right, #F97316 ${((distance - 1) / 49) * 100}%, rgba(0,0,0,0.05) ${((distance - 1) / 49) * 100}%)`
              }}
            />
            <style>{`
              input[type="range"]::-webkit-slider-thumb {
                -webkit-appearance: none;
                appearance: none;
                width: 20px;
                height: 20px;
                border-radius: 50%;
                background: white;
                border: 2.5px solid #F97316;
                cursor: pointer;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
              }
              input[type="range"]::-moz-range-thumb {
                width: 20px;
                height: 20px;
                border-radius: 50%;
                background: white;
                border: 2.5px solid #F97316;
                cursor: pointer;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
              }
            `}</style>
            <div className="flex items-center justify-between mt-3">
              <span className="text-[10px] text-content-muted font-medium">Yakın çevre</span>
              <span className="text-[10px] text-content-muted font-medium">Geniş hizmet alanı</span>
            </div>
          </div>
        </div>
      </div>
    </motion.div>
  );

  return (
    <div className="min-h-screen bg-surface flex justify-center text-content font-sans">
      <div className="w-full max-w-md bg-surface min-h-screen relative flex flex-col">
        
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
            <span className="text-[10px] font-bold text-content-muted">3 / 4</span>
          </div>
        </div>

        <div className="flex-1 overflow-y-auto px-6 pt-4 pb-32">
          
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4 }}
            className="mb-8"
          >
            <h1 className="text-[20px] font-extrabold text-content tracking-tight mb-2 leading-tight">
              Nasıl hizmet veriyorsun?
            </h1>
            <p className="text-[12px] text-content-muted leading-relaxed font-medium">
              Bazı işler bulunduğun çevrede yapılır, bazıları tamamen online yürütülür. Hizmetlerine en uygun çalışma şeklini seç.
            </p>
          </motion.div>

          <motion.div 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1, duration: 0.4 }}
            className="flex flex-col space-y-3"
          >
            {WORK_TYPES.map((type) => {
              const isSelected = workType === type.id;
              const Icon = type.icon;
              
              return (
                <button
                  key={type.id}
                  onClick={() => setWorkType(type.id as any)}
                  className={`relative flex items-start p-4 w-full text-left transition-all duration-200 border rounded-xl overflow-hidden ${
                    isSelected 
                      ? "border-[#F97316] bg-[#F97316]/5 shadow-sm" 
                      : "border-border-subtle bg-transparent hover:border-border-strong hover:bg-surface-muted"
                  }`}
                >
                  {isSelected && (
                    <div className="absolute left-0 top-0 bottom-0 w-1 bg-[#F97316]" />
                  )}

                  <div className="flex-1 flex gap-3">
                    <div className={`shrink-0 w-10 h-10 rounded-full flex items-center justify-center border transition-colors ${
                      isSelected ? "bg-white border-[#F97316]/20" : "bg-surface border-border-faint"
                    }`}>
                      <Icon className={`w-5 h-5 ${isSelected ? "text-[#F97316]" : "text-content-muted"}`} strokeWidth={isSelected ? 2 : 1.5} />
                    </div>
                    
                    <div className="flex flex-col pt-0.5 max-w-[85%]">
                      <span className={`text-[12px] font-extrabold tracking-tight ${isSelected ? "text-[#F97316]" : "text-content"}`}>
                        {type.title}
                      </span>
                      <span className="text-[10px] text-content-muted font-medium mt-1 leading-snug">
                        {type.description}
                      </span>
                      <span className="text-[10px] text-content-muted/80 font-medium mt-2 italic leading-tight">
                        Örn: {type.examples}
                      </span>
                    </div>
                  </div>

                  <div className={`shrink-0 ml-2 mt-2 transition-opacity ${isSelected ? "opacity-100" : "opacity-0"}`}>
                    <CheckCircle2 className="w-5 h-5 text-[#F97316]" strokeWidth={2} />
                  </div>
                </button>
              );
            })}
          </motion.div>

          <AnimatePresence>
            {workType === "online" && renderOnlineForm()}
            {workType === "onsite" && renderOnsiteForm()}
            {workType === "hybrid" && (
              <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                className="overflow-hidden"
              >
                {renderOnlineForm()}
                {renderOnsiteForm()}
              </motion.div>
            )}
          </AnimatePresence>

        </div>

        <div className="absolute bottom-0 left-0 right-0 bg-surface/90 backdrop-blur-md px-6 py-4 pb-safe border-t border-border-faint z-40">
          <motion.div
             initial={{ opacity: 0, y: 10 }}
             animate={{ opacity: 1, y: 0 }}
             transition={{ delay: 0.2, duration: 0.3 }}
          >
            <button 
              onClick={handleNext}
              disabled={!workType}
              className={`w-full py-3.5 rounded-xl font-extrabold text-[14px] tracking-tight shadow-sm transition-all flex items-center justify-center gap-2 ${
                workType 
                  ? "bg-[#F97316] hover:bg-[#EA580C] text-white active:scale-[0.98]" 
                  : "bg-surface-muted border border-border-strong text-content-muted cursor-not-allowed"
              }`}
            >
              Devam et
            </button>
            <p className="text-[10px] text-content-muted text-center mt-3 font-medium px-4">
              Bu ayarları daha sonra hizmet veren panelinden değiştirebilirsin.
            </p>
          </motion.div>
        </div>

      </div>
    </div>
  );
}
