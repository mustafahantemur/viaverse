import { useState } from "react";
import { motion, AnimatePresence } from "motion/react";
import { ChevronLeft, ChevronDown, ChevronUp, CheckCircle2 } from "lucide-react";
import { mainCategories } from "./HomeScreen";
import ProviderSetupWorkAreaScreen from "./ProviderSetupWorkAreaScreen";

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

const categoryIconMap: Record<string, string> = {
  "Ev, Tamirat & Tadilat": tamiratImg,
  "Dijital & Yazılım Hizmetleri": yazilimImg,
  "Yaratıcı İşler & Medya": yaraticiImg,
  "Eğitim, Ders & Mentorluk": dersImg,
  "Temizlik & Düzenleme": temizlikImg,
  "Lojistik, Paket & Destek": lojistikImg,
  "Kişisel Bakım & Sağlık": bakimImg,
  "Profesyonel & Danışmanlık": danismanlikImg,
  "Evcil Hayvan Hizmetleri": hayvanImg,
  "Etkinlik & Organizasyon": etkinlikImg
};

interface ProviderSetupCategoriesScreenProps {
  onBack: () => void;
  selectedType: string | null;
  onNext: (selectedCategories: string[]) => void;
}

export default function ProviderSetupCategoriesScreen({ onBack, selectedType, onNext }: ProviderSetupCategoriesScreenProps) {
  const [expandedCategories, setExpandedCategories] = useState<string[]>([]);
  const [selectedSubCategories, setSelectedSubCategories] = useState<string[]>([]);
  const [showWorkArea, setShowWorkArea] = useState(false);

  const toggleCategory = (catName: string) => {
    setExpandedCategories(prev => 
      prev.includes(catName) 
        ? prev.filter(c => c !== catName)
        : [...prev, catName]
    );
  };

  const toggleSubCategory = (subCatName: string) => {
    setSelectedSubCategories(prev => 
      prev.includes(subCatName)
        ? prev.filter(s => s !== subCatName)
        : [...prev, subCatName]
    );
  };

  const handleNext = () => {
    if (selectedSubCategories.length > 0) {
      setShowWorkArea(true);
    }
  };

  if (showWorkArea) {
    return (
      <ProviderSetupWorkAreaScreen
        onBack={() => setShowWorkArea(false)}
        selectedCategories={selectedSubCategories}
        onNext={(workAreaData) => {
          console.log("Seçilen Hizmetler:", selectedSubCategories);
          console.log("Çalışma Şekli:", workAreaData);
          onNext(selectedSubCategories); // Bubble up to ProviderSetupTypeScreen to show the final alert
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
            <span className="text-[10px] font-bold text-content-muted">2 / 4</span>
          </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto px-6 pt-4 pb-32">
          
          {/* Title Area */}
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4 }}
            className="mb-6"
          >
            <h1 className="text-[20px] font-extrabold text-content tracking-tight mb-2 leading-tight">
              Hangi hizmetleri sunuyorsun?
            </h1>
            <p className="text-[12px] text-content-muted leading-relaxed font-medium">
              Müşterilerin senden hangi konularda teklif alabileceğini seç. Birden fazla hizmet alanı ekleyebilirsin.
            </p>
          </motion.div>

          {/* Categories List */}
          <motion.div 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1, duration: 0.4 }}
            className="flex flex-col border-t border-border-faint"
          >
            {mainCategories.map((cat) => {
              const isExpanded = expandedCategories.includes(cat.name);
              const customImageUrl = categoryIconMap[cat.name];
              
              // Count selected subcategories for this category
              const selectedCount = cat.subCats.filter(sub => selectedSubCategories.includes(sub)).length;

              return (
                <div key={cat.name} className="flex flex-col border-b border-border-faint">
                  {/* Main Category Header */}
                  <button 
                    onClick={() => toggleCategory(cat.name)}
                    className="flex justify-between items-center py-3 px-1 w-full text-left active:bg-surface-muted transition-colors group"
                  >
                    <div className="flex items-center gap-3">
                      <div className={`w-8 h-8 rounded-full flex items-center justify-center ${cat.bg}`}>
                        {customImageUrl ? (
                          <img src={customImageUrl} alt={cat.name} className="w-8 h-8 object-contain" />
                        ) : (
                          <div className={`w-4 h-4 ${cat.color}`} />
                        )}
                      </div>
                      <div className="flex flex-col">
                        <span className="text-[12px] font-bold text-content tracking-tight group-hover:text-[#F97316] transition-colors">{cat.name}</span>
                        {selectedCount > 0 && (
                          <span className="text-[9px] font-bold text-[#F97316] mt-0.5">
                            {selectedCount} hizmet seçildi
                          </span>
                        )}
                      </div>
                    </div>
                    {isExpanded ? (
                      <ChevronUp className="w-5 h-5 text-content-muted drop-shadow-sm" strokeWidth={1.5} />
                    ) : (
                      <ChevronDown className="w-5 h-5 text-content-muted" strokeWidth={1.5} />
                    )}
                  </button>

                  {/* SubCategories List */}
                  <AnimatePresence>
                    {isExpanded && (
                      <motion.div
                        initial={{ height: 0, opacity: 0 }}
                        animate={{ height: "auto", opacity: 1 }}
                        exit={{ height: 0, opacity: 0 }}
                        className="overflow-hidden"
                      >
                        <div className="flex flex-col pl-11 pr-1 pb-3 pt-1 space-y-1.5">
                          {cat.subCats.map((sub, index) => {
                            const isSelected = selectedSubCategories.includes(sub);
                            return (
                              <button
                                key={index}
                                onClick={() => toggleSubCategory(sub)}
                                className={`flex items-center justify-between py-2.5 px-3 rounded-lg border transition-all ${
                                  isSelected 
                                    ? "bg-[#F97316]/5 border-[#F97316]/30 text-[#EA580C]" 
                                    : "bg-surface border-transparent hover:bg-surface-muted text-content-muted hover:text-content"
                                }`}
                              >
                                <div className="flex items-center gap-2.5 flex-1">
                                  {customImageUrl && (
                                    <div className="w-5 h-5 shrink-0 opacity-40">
                                      <img src={customImageUrl} alt={sub} className="w-full h-full object-contain" />
                                    </div>
                                  )}
                                  <span className={`text-[11px] text-left leading-snug tracking-tight font-medium ${isSelected ? "font-bold" : ""}`}>
                                    {sub}
                                  </span>
                                </div>
                                <div className={`shrink-0 ml-2 transition-opacity ${isSelected ? "opacity-100" : "opacity-0"}`}>
                                  <CheckCircle2 className="w-4 h-4 text-[#F97316]" strokeWidth={2} />
                                </div>
                              </button>
                            );
                          })}
                        </div>
                      </motion.div>
                    )}
                  </AnimatePresence>
                </div>
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
              disabled={selectedSubCategories.length === 0}
              className={`w-full py-3.5 rounded-xl font-extrabold text-[14px] tracking-tight shadow-sm transition-all flex items-center justify-center gap-2 ${
                selectedSubCategories.length > 0
                  ? "bg-[#F97316] hover:bg-[#EA580C] text-white active:scale-[0.98]" 
                  : "bg-surface-muted border border-border-strong text-content-muted cursor-not-allowed"
              }`}
            >
              Devam et
            </button>
            <p className="text-[10px] text-content-muted text-center mt-3 font-medium px-4 h-4">
              {selectedSubCategories.length > 0 
                ? `${selectedSubCategories.length} hizmet seçildi`
                : "Seçtiğin hizmetleri daha sonra panelinden düzenleyebilirsin."
              }
            </p>
          </motion.div>
        </div>

      </div>
    </div>
  );
}
