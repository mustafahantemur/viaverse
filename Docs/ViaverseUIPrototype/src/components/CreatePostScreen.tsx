import React, { useState } from "react";
import { motion, AnimatePresence } from "motion/react";
import { X, ChevronLeft, ChevronRight, MapPin, Image as ImageIcon, Video, HelpCircle, Megaphone, ClipboardList, Info, MessageCircle, Link, CheckCircle2 } from "lucide-react";

interface CreatePostScreenProps {
  onClose: () => void;
  onSubmit: (post: any, action?: "GOTO_JOBS") => void;
  mode: "ulak" | "uzman";
}

import isImg from '../icons/is1.png';
import duyuruImg from '../icons/duyuru1.png';
import danismaImg from '../icons/danısma1.png';
import yardimImg from '../icons/yardım1.png';

import { mainCategories } from "./HomeScreen";

type PostType = "yardım" | "duyuru" | "danışma" | "iş" | string;

export const POST_TYPES = [
  { id: "yardım", label: "Yardım", icon: HelpCircle, image: yardimImg, color: "text-blue-500 dark:text-blue-400", bg: "bg-blue-50/50 dark:bg-blue-500/10", hoverBg: "hover:bg-blue-50 dark:hover:bg-blue-500/20", iconBg: "bg-blue-100/50 dark:bg-blue-500/20", borderColor: "border-blue-100/50 dark:border-blue-500/20", desc: "Küçük bir destek veya zaman yardımı iste." },
  { id: "duyuru", label: "Duyuru", icon: Megaphone, image: duyuruImg, color: "text-orange-500 dark:text-orange-400", bg: "bg-orange-50/50 dark:bg-orange-500/10", hoverBg: "hover:bg-orange-50 dark:hover:bg-orange-500/20", iconBg: "bg-orange-100/50 dark:bg-orange-500/20", borderColor: "border-orange-100/50 dark:border-orange-500/20", desc: "Çevrendeki insanları bilgilendir." },
  { id: "danışma", label: "Danışma", icon: MessageCircle, image: danismaImg, color: "text-purple-500 dark:text-purple-400", bg: "bg-purple-50/50 dark:bg-purple-500/10", hoverBg: "hover:bg-purple-50 dark:hover:bg-purple-500/20", iconBg: "bg-purple-100/50 dark:bg-purple-500/20", borderColor: "border-purple-100/50 dark:border-purple-500/20", desc: "Fikir, öneri veya bilgi iste." },
  { id: "iş", label: "Küçük İş", icon: ClipboardList, image: isImg, color: "text-emerald-500 dark:text-emerald-400", bg: "bg-emerald-50/50 dark:bg-emerald-500/10", hoverBg: "hover:bg-emerald-50 dark:hover:bg-emerald-500/20", iconBg: "bg-emerald-100/50 dark:bg-emerald-500/20", borderColor: "border-emerald-100/50 dark:border-emerald-500/20", desc: "Kısa sürede yapılabilecek, düşük riskli ve gündelik işler için uygundur." },
];

export default function CreatePostScreen({ onClose, onSubmit, mode }: CreatePostScreenProps) {
  // Common step state (Ulak: 1,2,5. Uzman: 101-106)
  const [step, setStep] = useState(mode === "ulak" ? 1 : 101);

  // ULAK specific states
  const [type, setType] = useState<PostType | null>(null);
  const [ulakTitle, setUlakTitle] = useState("");
  const [ulakDesc, setUlakDesc] = useState("");
  const [mediaPreview, setMediaPreview] = useState<string | null>(null);
  const [radiusIndex, setRadiusIndex] = useState(0);
  const [showRadiusMenu, setShowRadiusMenu] = useState(false);
  const radiusOptions = [150, 500, 1000, 5000, 10000, 50000];
  const radiusLabels = ["150m", "500m", "1km", "5km", "10km", "50km"];

  // UZMAN specific states
  const [uzmanCat, setUzmanCat] = useState<any>(null);
  const [uzmanSubCat, setUzmanSubCat] = useState<string | null>(null);
  
  const [title, setTitle] = useState("");
  const [desc, setDesc] = useState("");
  const [workMode, setWorkMode] = useState<"online" | "onsite" | "hybrid" | null>(null);

  const [city, setCity] = useState("İstanbul");
  const [district, setDistrict] = useState("Kadıköy");
  const [distance, setDistance] = useState(5);

  const [timing, setTiming] = useState<string | null>(null);
  const timingOptions = ["Bugün", "Yarın", "Bu hafta", "Tarih seç", "Hizmet verenle netleşsin"];

  const [budgetType, setBudgetType] = useState<"offer" | "range">("offer");
  const [minBudget, setMinBudget] = useState("");
  const [maxBudget, setMaxBudget] = useState("");

  const [postData, setPostData] = useState<any>(null);

  const handleBack = () => {
    if (mode === "ulak") {
      step > 1 ? setStep(step - 1) : onClose();
    } else {
      if (step === 101) onClose();
      else if (step === 106) onClose(); 
      else setStep(step - 1);
    }
  };

  const isUlakFormValid = () => {
    return step === 2 && ulakDesc.trim().length > 5;
  };

  const isUzmanStep103Valid = () => {
    return title.trim().length > 0 && desc.trim().length > 5 && workMode !== null;
  };

  const isUzmanStep104Valid = () => {
    return timing !== null;
  };

  const handleSubmitUlak = () => {
    if (!isUlakFormValid()) return;
    const generatedPost = {
      id: Date.now(),
      type,
      title: ulakTitle.trim(),
      desc: ulakDesc.trim(),
      location: `mevcut konum (${radiusLabels[radiusIndex]})`,
      publishTime: "şimdi",
      authorName: "Anıl Can",
      authorImg: "",
      likes: 0,
      comments: 0,
      dist: "0m",
      postImg: mediaPreview || undefined
    };
    setPostData(generatedPost);
    setStep(5);
  };

  const handleSubmitUzman = () => {
    let finalBudget = "Teklif bekliyor";
    if (budgetType === "range" && minBudget && maxBudget) {
      finalBudget = `${minBudget}₺ - ${maxBudget}₺`;
    }
    
    let finalLocation = "Online";
    let finalDistance = "";
    if (workMode !== "online") {
      finalLocation = `${district}, ${city}`;
      finalDistance = `${distance} km`;
    }

    const generatedPost = {
      id: Date.now(),
      title: title.trim(),
      category: uzmanCat?.name,
      subCategory: uzmanSubCat,
      description: desc.trim(),
      workMode: workMode,
      location: finalLocation,
      distance: finalDistance,
      time: timing,
      budget: finalBudget,
      attachments: [],
      customerName: "Anıl Can",
      createdAt: "Şimdi",
      isServiceRequest: true,
      status: "Yeni talep"
    };

    setPostData(generatedPost);
    setStep(106);
  };

  const handleAddMedia = () => {
    setMediaPreview("https://images.unsplash.com/photo-1542838132-92c53300491e?q=80&w=800&auto=format&fit=crop");
  };

  // ---------------- ULAK SUCCESS ----------------
  if (step === 5) {
    return (
      <motion.div 
        initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: 30 }}
        className="fixed inset-0 z-[100] bg-surface flex flex-col font-sans items-center justify-center p-4 text-center"
      >
        <div className="w-20 h-20 rounded-full bg-[#10B981]/10 flex items-center justify-center mb-6 shadow-sm">
          <svg className="w-10 h-10 text-[#10B981]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
          </svg>
        </div>
        <h2 className="text-[16px] font-extrabold text-content mb-3 tracking-tight">Paylaşımın dinamik çevrene ulaştı.</h2>
        <p className="text-[12px] text-content-muted leading-relaxed font-medium mb-8 max-w-[280px]">
          Bu paylaşım, yakınındaki ilgili kişilerin akışında görünebilir.
        </p>
        <div className="w-full space-y-3 mt-4">
          <button 
            onClick={() => onSubmit(postData, "GOTO_JOBS")}
            className="w-full py-4 bg-[#F97316] text-white rounded-xl font-bold text-[13px] shadow-md shadow-[#F97316]/20 active:opacity-80 transition-all flex items-center justify-center gap-2"
          >
            <ClipboardList className="w-5 h-5" /> İşlerimi Görüntüle
          </button>
          <button 
            onClick={() => onSubmit(postData)}
            className="w-full py-4 bg-surface-muted text-content rounded-xl font-bold text-[13px] active:opacity-80 transition-all"
          >
            Ana Ekrana Dön
          </button>
        </div>
      </motion.div>
    );
  }

  // ---------------- UZMAN SUCCESS ----------------
  if (step === 106) {
    return (
      <motion.div 
        initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: 30 }}
        className="fixed inset-0 z-[100] bg-surface flex flex-col font-sans items-center justify-center p-4 text-center"
      >
        <div className="w-20 h-20 rounded-full bg-green-50 flex items-center justify-center mb-6 border border-green-100">
          <CheckCircle2 className="w-10 h-10 text-green-500" strokeWidth={2} />
        </div>
        <h2 className="text-[16px] font-extrabold text-content mb-2 tracking-tight">Talebin uygun hizmet verenlere ulaştı.</h2>
        <p className="text-[10px] text-content-muted leading-relaxed font-medium mb-8 max-w-[280px]">
          Hizmet verenler talebini inceleyip teklif gönderebilir.
        </p>
        <div className="w-full space-y-3 mt-4">
          <button 
            onClick={() => onSubmit(postData, "GOTO_JOBS")}
            className="w-full py-3.5 bg-[#F97316] text-white rounded-xl font-bold text-[12px] active:scale-[0.98] transition-transform flex items-center justify-center gap-2"
          >
            <ClipboardList className="w-4 h-4" /> İşlerime git
          </button>
          <button 
            onClick={() => onSubmit(postData)}
            className="w-full py-3.5 bg-transparent text-content-muted rounded-xl font-bold text-[12px] active:opacity-80 transition-all border border-transparent hover:border-border-subtle hover:bg-surface-muted"
          >
            Ana ekrana dön
          </button>
        </div>
      </motion.div>
    );
  }

  // ---------------- MAIN RENDER ----------------
  return (
    <motion.div 
      initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: 30 }}
      className="fixed inset-0 z-[100] bg-surface flex flex-col font-sans overflow-hidden"
    >
      {/* Header */}
      <header className="flex items-center justify-between px-4 h-14 border-b border-border-subtle shrink-0 bg-surface">
        <button onClick={handleBack} className="w-10 h-10 rounded-full flex items-center justify-center text-content active:scale-95 transition-transform bg-surface-muted ml-[-8px]">
          <ChevronLeft className="w-5 h-5" strokeWidth={2} />
        </button>
        <span className="text-content tracking-tight flex flex-col items-center">
          {mode === "ulak" ? (
             <span className="text-[12px] font-bold">Dinamik çevrene paylaş</span>
          ) : (
            <>
              <span className="opacity-50 text-[10px] uppercase tracking-widest leading-none mb-0.5 font-bold mt-1 text-[#F97316]">
                {step === 105 ? "son adım" : step >= 101 ? `adım ${step-100}/5` : ""}
              </span>
              <span className="leading-none text-[12px] font-bold mb-1">
                {step === 101 ? "Hizmet Kategorisi" : 
                 step === 102 ? "Hizmet Detayı" :
                 step === 103 ? "Talep Bilgileri" : 
                 step === 104 ? "Zaman ve Bütçe" :
                 step === 105 ? "Talep Özeti" : ""}
              </span>
            </>
          )}
        </span>
        <div className="w-10 h-10 flex items-center justify-end">
          <button onClick={onClose} className="rounded-full flex items-center justify-center text-content-muted active:scale-95 transition-transform mr-[-8px]">
            <X className="w-5 h-5" strokeWidth={2} />
          </button>
        </div>
      </header>

      {/* Content Area */}
      <div className="flex-1 overflow-y-auto bg-surface pb-safe">
        <AnimatePresence mode="wait">
          
          {/* ===================== ULAK FLOW ===================== */}
          {mode === "ulak" && step === 1 && (
            <motion.div key="ulak1" initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 10 }} className="p-4 space-y-4">
              <div className="mb-5">
                <h2 className="text-[16px] font-extrabold text-content tracking-tight leading-tight">
                  Ne paylaşmak istiyorsun?
                </h2>
                <div className="mt-3 inline-flex items-start gap-1.5 px-3 py-2.5 bg-surface-muted rounded-xl border border-border-subtle">
                  <Info className="w-4 h-4 text-[#F97316] shrink-0 mt-0.5" />
                  <p className="text-[10px] text-content-muted font-medium leading-relaxed">
                    Yardım, duyuru, danışma veya küçük iş paylaşarak dinamik çevrene ulaş.
                  </p>
                </div>
              </div>
              <div className="flex flex-col gap-3">
                {POST_TYPES.map(item => (
                  <button
                    key={item.id}
                    onClick={() => { setType(item.id as any); setStep(2); }}
                    className="flex items-center gap-4 p-4 rounded-2xl bg-surface border border-border-subtle hover:bg-surface-hover active:scale-[0.98] transition-all text-left shadow-[0_2px_10px_rgb(0,0,0,0.02)]"
                  >
                    <div className="w-8 h-8 flex items-center justify-center shrink-0">
                      <img src={item.image} alt={item.label} className="w-8 h-8 object-contain" />
                    </div>
                    <div className="flex-1">
                      <span className="text-[12px] font-bold text-content block">{item.label}</span>
                      <span className="text-[10px] text-content-muted font-medium block mt-0.5">{item.desc}</span>
                    </div>
                    <ChevronRight className="w-5 h-5 text-content-muted opacity-40" strokeWidth={1.5} />
                  </button>
                ))}
              </div>
            </motion.div>
          )}

          {mode === "ulak" && step === 2 && (
            <motion.div key="ulak2" initial={{ opacity: 0, x: 10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -10 }} className="flex flex-col h-full p-4">
              <div className="mb-4">
                {POST_TYPES.find(t => t.id === type) && (() => {
                  const selectedType = POST_TYPES.find(t => t.id === type)!;
                  return (
                    <span className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-surface-muted border border-border-subtle rounded-full text-[10px] font-bold text-content tracking-tight uppercase">
                      <img src={selectedType.image} alt={selectedType.label} className="w-4 h-4 object-contain" />
                      {selectedType.id}
                    </span>
                  );
                })()}
              </div>
              <div className="flex flex-col flex-1">
                <input
                  placeholder="Başlık ekle (isteğe bağlı)"
                  value={ulakTitle}
                  onChange={(e) => setUlakTitle(e.target.value)}
                  className="w-full text-[12px] font-extrabold text-content placeholder:text-content-muted/50 outline-none pb-2 bg-transparent tracking-tight"
                />
                <textarea
                  autoFocus
                  placeholder="Neler oluyor? Neye ihtiyacın var?"
                  value={ulakDesc}
                  onChange={(e) => setUlakDesc(e.target.value)}
                  className="w-full text-[10px] text-content placeholder:text-content-muted/50 font-medium outline-none resize-none min-h-[120px] bg-transparent leading-relaxed"
                />
                <AnimatePresence>
                  {mediaPreview && (
                    <motion.div initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 0.95 }} className="relative w-full h-[200px] mt-4 rounded-2xl overflow-hidden border border-border-subtle">
                      <img src={mediaPreview} className="w-full h-full object-cover" alt="Media preview" />
                      <button onClick={() => setMediaPreview(null)} className="absolute top-3 right-3 w-8 h-8 bg-black/60 backdrop-blur-md rounded-full flex items-center justify-center text-white active:scale-95 transition-transform">
                        <X className="w-4 h-4" strokeWidth={2} />
                      </button>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
              <div className="mt-auto pt-4 flex items-center justify-between border-t border-border-subtle pb-4">
                <div className="flex items-center gap-2">
                  <button onClick={handleAddMedia} className="w-10 h-10 rounded-full bg-surface-muted flex items-center justify-center text-[#F97316] hover:bg-[#F97316]/10 transition-colors active:scale-95">
                    <ImageIcon className="w-5 h-5" strokeWidth={2} />
                  </button>
                  <button onClick={handleAddMedia} className="w-10 h-10 rounded-full bg-surface-muted flex items-center justify-center text-[#F97316] hover:bg-[#F97316]/10 transition-colors active:scale-95">
                    <Video className="w-5 h-5" strokeWidth={2} />
                  </button>
                </div>
                <div className="relative">
                  <button onClick={() => setShowRadiusMenu(!showRadiusMenu)} className={`flex items-center gap-1.5 px-3 py-1.5 transition-colors rounded-full text-content-muted select-none ${showRadiusMenu ? 'bg-[#F97316]/10 text-[#F97316]' : 'bg-surface-muted hover:bg-surface-hover active:bg-surface-muted'}`}>
                    <MapPin className="w-3.5 h-3.5" strokeWidth={2} />
                    <span className="text-[10px] font-bold tracking-tight">{radiusLabels[radiusIndex]} çevrendekiler</span>
                  </button>
                  <AnimatePresence>
                    {showRadiusMenu && (
                      <motion.div initial={{ opacity: 0, y: 10, scale: 0.95 }} animate={{ opacity: 1, y: 0, scale: 1 }} exit={{ opacity: 0, y: 10, scale: 0.95 }} className="absolute bottom-full right-0 mb-3 w-[200px] bg-surface rounded-[20px] shadow-[0_10px_40px_-10px_rgba(0,0,0,0.15)] border border-border-subtle p-4 z-50 origin-bottom-right">
                        <div className="flex items-center justify-between mb-3">
                          <span className="text-[10px] font-bold text-content-muted uppercase tracking-wider">mesafe seç</span>
                          <span className="text-[10px] font-black text-[#F97316]">{radiusLabels[radiusIndex]}</span>
                        </div>
                        <input type="range" min="0" max={radiusOptions.length - 1} value={radiusIndex} onChange={(e) => setRadiusIndex(Number(e.target.value))} className="w-full h-1.5 bg-surface-muted rounded-lg appearance-none cursor-pointer accent-[#F97316]" />
                        <div className="flex justify-between text-[9px] font-bold text-content-muted mt-2.5">
                          <span>150m</span><span>50km</span>
                        </div>
                      </motion.div>
                    )}
                  </AnimatePresence>
                </div>
              </div>
            </motion.div>
          )}


          {/* ===================== UZMAN FLOW ===================== */}
          
          {/* STEP 101: Main Category */}
          {mode === "uzman" && step === 101 && (
            <motion.div key="uzman101" initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 10 }} className="p-4 space-y-4 pb-12">
              <div className="mb-2">
                 <h2 className="text-[16px] font-extrabold text-content tracking-tight leading-tight">Hangi hizmete ihtiyacın var?</h2>
                 <p className="text-[11px] text-content-muted mt-1 font-medium">İhtiyacını anlat, uygun hizmet verenlerden teklif al.</p>
              </div>
              <div className="grid grid-cols-4 gap-2">
                {mainCategories.map((item, index) => (
                  <button
                    key={index}
                    onClick={() => { setUzmanCat(item); setStep(102); }}
                    className="flex flex-col items-center justify-start gap-1 p-2 bg-surface hover:bg-surface-muted border border-border-faint hover:border-border-subtle rounded-2xl transition-all h-[95px] w-full"
                  >
                    <div className="w-11 h-11 rounded-2xl flex items-center justify-center p-2.5 mb-1 bg-surface-muted shrink-0">
                      <img src={item.image} alt={item.name} className="w-full h-full object-contain" />
                    </div>
                    <span className="text-[10px] text-content font-bold text-center leading-tight line-clamp-2">{item.name}</span>
                  </button>
                ))}
              </div>
            </motion.div>
          )}

          {/* STEP 102: Sub Category */}
          {mode === "uzman" && step === 102 && uzmanCat && (
            <motion.div key="uzman102" initial={{ opacity: 0, x: 10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -10 }} className="p-4 space-y-4 pb-12">
              <div className="mb-2">
                 <span className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-surface-muted border border-border-subtle rounded-md text-[10px] font-bold text-content tracking-tight mb-4">
                   <img src={uzmanCat.image} alt={uzmanCat.name} className="w-3 h-3 object-contain" />
                   {uzmanCat.name}
                 </span>
                 <h2 className="text-[16px] font-extrabold text-content tracking-tight leading-tight">Daha detaylı seçelim</h2>
              </div>
              <div className="flex flex-col border border-border-subtle rounded-xl overflow-hidden bg-surface">
                {uzmanCat.subCats.map((sub: string, index: number) => (
                  <button
                    key={index}
                    onClick={() => { setUzmanSubCat(sub); setStep(103); }}
                    className={`text-left p-3.5 text-[11px] font-medium text-content hover:bg-surface-muted transition-colors active:bg-surface-hover flex items-center justify-between ${index !== uzmanCat.subCats.length - 1 ? 'border-b border-border-faint' : ''}`}
                  >
                    {sub}
                    <ChevronRight className="w-3.5 h-3.5 opacity-30" />
                  </button>
                ))}
              </div>
            </motion.div>
          )}

          {/* STEP 103: Title, Desc, Work Mode */}
          {mode === "uzman" && step === 103 && (
            <motion.div key="uzman103" initial={{ opacity: 0, x: 10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -10 }} className="p-4 space-y-6 pb-24 h-full flex flex-col">
              
              <div className="space-y-1.5 border-b border-border-subtle pb-4">
                 <label className="text-[12px] font-bold text-content">Talep başlığı</label>
                 <input
                   type="text"
                   value={title}
                   onChange={(e) => setTitle(e.target.value)}
                   placeholder="Örn: Ev temizliği, Kombi bakımı, Matematik özel ders..."
                   className="w-full text-[13px] text-content placeholder:text-content-muted font-medium outline-none p-3 bg-surface-muted rounded-xl border border-border-subtle focus:border-[#F97316]"
                 />
              </div>

              <div className="space-y-1.5 border-b border-border-subtle pb-4">
                 <label className="text-[12px] font-bold text-content">İhtiyacını anlat</label>
                 <textarea
                   value={desc}
                   onChange={(e) => setDesc(e.target.value)}
                   placeholder="Ne istediğini, beklentini ve varsa önemli detayları kısaca yaz."
                   className="w-full text-[10px] text-content placeholder:text-content-muted font-medium outline-none resize-none min-h-[90px] p-3 bg-surface-muted rounded-xl border border-border-subtle focus:border-[#F97316]"
                 />
              </div>

              <div className="space-y-2">
                 <label className="text-[12px] font-bold text-content">Nasıl hizmet almak istiyorsun?</label>
                 <div className="grid grid-cols-3 gap-2">
                   {[
                     { id: "online", label: "Online" },
                     { id: "onsite", label: "Yerinde" },
                     { id: "hybrid", label: "Fark etmez" },
                   ].map(modeOpt => (
                     <button
                       key={modeOpt.id}
                       onClick={() => setWorkMode(modeOpt.id as any)}
                       className={`py-3 rounded-xl border text-[11px] font-bold flex items-center justify-center transition-all ${workMode === modeOpt.id ? 'bg-[#F97316] text-white border-[#F97316] shadow-sm' : 'bg-surface border-border-subtle text-content-muted hover:border-border-muted'}`}
                     >
                       {modeOpt.label}
                     </button>
                   ))}
                 </div>
              </div>

              <div className="mt-auto pt-6">
                 <button 
                   onClick={() => setStep(104)}
                   disabled={!isUzmanStep103Valid()}
                   className={`w-full py-3.5 rounded-xl text-[12px] font-extrabold tracking-tight transition-all text-white ${isUzmanStep103Valid() ? 'bg-[#F97316] shadow-md shadow-[#F97316]/20 active:scale-[0.98]' : 'bg-[#F97316]/40 cursor-not-allowed'}`}
                 >
                   Devam Et
                 </button>
              </div>
            </motion.div>
          )}

          {/* STEP 104: Location, Time, Budget, Attachments */}
          {mode === "uzman" && step === 104 && (
            <motion.div key="uzman104" initial={{ opacity: 0, x: 10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -10 }} className="p-4 space-y-6 pb-24">
              
              {/* Location Section */}
              <div className="space-y-2 pb-4 border-b border-border-subtle">
                 <label className="text-[12px] font-bold text-content">Hizmet konumu</label>
                 
                 {workMode === "online" ? (
                   <div className="p-3 bg-surface-muted rounded-xl flex items-start gap-2 border border-border-subtle">
                     <Info className="w-4 h-4 text-blue-500 shrink-0 mt-0.5" />
                     <p className="text-[10px] text-content-muted leading-relaxed font-medium">Online hizmetlerde konum zorunlu değildir. Hizmet verenler çalışma şekline göre teklif verebilir.</p>
                   </div>
                 ) : (
                   <div className="space-y-4">
                     <div className="grid grid-cols-2 gap-2">
                       <input value={city} onChange={e=>setCity(e.target.value)} placeholder="İl" className="w-full text-[12px] p-2.5 bg-surface-muted rounded-lg border border-border-subtle" />
                       <input value={district} onChange={e=>setDistrict(e.target.value)} placeholder="İlçe" className="w-full text-[12px] p-2.5 bg-surface-muted rounded-lg border border-border-subtle" />
                     </div>
                     <div className="space-y-1">
                       <div className="flex justify-between items-center">
                         <span className="text-[10px] font-medium text-content-muted">Hizmet verenin gelebileceği mesafe</span>
                         <span className="text-[10px] font-bold text-[#F97316] mb-1">{distance} km</span>
                       </div>
                       <input type="range" min="1" max="50" value={distance} onChange={e => setDistance(Number(e.target.value))} className="w-full h-1.5 bg-surface-muted rounded-lg appearance-none cursor-pointer accent-[#F97316]" />
                       <div className="flex justify-between text-[9px] font-medium text-content-muted mt-1 select-none">
                         <span>Yakın çevre</span><span>Geniş alan</span>
                       </div>
                     </div>
                   </div>
                 )}
              </div>

              {/* Timing Section */}
              <div className="space-y-2 pb-4 border-b border-border-subtle">
                 <label className="text-[12px] font-bold text-content">Ne zaman ihtiyacın var?</label>
                 <div className="flex flex-wrap gap-2">
                   {timingOptions.map(t => (
                     <button
                       key={t}
                       onClick={() => setTiming(t)}
                       className={`px-3 py-1.5 rounded-full text-[10px] font-semibold transition-all border ${timing === t ? 'bg-[#F97316]/10 text-[#F97316] border-[#F97316]/30' : 'bg-surface border-border-subtle text-content-muted hover:border-border-muted'}`}
                     >
                       {t}
                     </button>
                   ))}
                 </div>
              </div>

              {/* Budget Section */}
              <div className="space-y-3 pb-4 border-b border-border-subtle">
                 <label className="text-[12px] font-bold text-content">Bütçe</label>
                 <div className="grid grid-cols-2 gap-2">
                   <button
                     onClick={() => setBudgetType('offer')}
                     className={`py-2 rounded-lg border flex items-center justify-center text-[10px] font-bold ${budgetType === 'offer' ? 'bg-[#F97316]/10 text-[#F97316] border-[#F97316]/30' : 'bg-surface-muted text-content-muted border-border-subtle'}`}
                   >
                     Teklif bekliyorum
                   </button>
                   <button
                     onClick={() => setBudgetType('range')}
                     className={`py-2 rounded-lg border flex items-center justify-center text-[10px] font-bold ${budgetType === 'range' ? 'bg-[#F97316]/10 text-[#F97316] border-[#F97316]/30' : 'bg-surface-muted text-content-muted border-border-subtle'}`}
                   >
                     Bütçe aralığı seç
                   </button>
                 </div>
                 {budgetType === "range" && (
                   <div className="flex items-center gap-2 mt-2">
                     <div className="flex-1 relative">
                       <input type="number" placeholder="Min" value={minBudget} onChange={e=>setMinBudget(e.target.value)} className="w-full text-[12px] p-2.5 bg-surface rounded-lg border border-border-subtle pr-6" />
                       <span className="absolute right-2.5 top-1/2 -translate-y-1/2 text-[10px] text-content-muted">₺</span>
                     </div>
                     <span className="text-content-muted">-</span>
                     <div className="flex-1 relative">
                       <input type="number" placeholder="Max" value={maxBudget} onChange={e=>setMaxBudget(e.target.value)} className="w-full text-[12px] p-2.5 bg-surface rounded-lg border border-border-subtle pr-6" />
                       <span className="absolute right-2.5 top-1/2 -translate-y-1/2 text-[10px] text-content-muted">₺</span>
                     </div>
                   </div>
                 )}
              </div>

              {/* Attachments Section */}
              <div className="space-y-2">
                 <label className="text-[12px] font-bold text-content">Ek dosya veya görsel</label>
                 <p className="text-[10px] text-content-muted font-medium mb-1">Referans görsel, örnek çalışma veya bağlantı paylaşabilirsin.</p>
                 <div className="flex gap-2">
                   <button className="flex-1 py-3 bg-surface border border-dashed border-border-muted rounded-xl flex flex-col items-center justify-center gap-1.5 hover:bg-surface-muted transition-colors active:scale-95 text-content-muted">
                     <ImageIcon className="w-4 h-4" />
                     <span className="text-[10px] font-bold">Görsel Ekle</span>
                   </button>
                   <button className="flex-1 py-3 bg-surface border border-dashed border-border-muted rounded-xl flex flex-col items-center justify-center gap-1.5 hover:bg-surface-muted transition-colors active:scale-95 text-content-muted">
                     <Link className="w-4 h-4" />
                     <span className="text-[10px] font-bold">Bağlantı Ekle</span>
                   </button>
                 </div>
              </div>

              <div className="mt-8">
                 <button 
                   onClick={() => setStep(105)}
                   disabled={!isUzmanStep104Valid()}
                   className={`w-full py-3.5 rounded-xl text-[12px] font-extrabold tracking-tight transition-all text-white ${isUzmanStep104Valid() ? 'bg-[#F97316] shadow-md shadow-[#F97316]/20 active:scale-[0.98]' : 'bg-[#F97316]/40 cursor-not-allowed'}`}
                 >
                   Özet ve Onay
                 </button>
              </div>

            </motion.div>
          )}

          {/* STEP 105: Summary */}
          {mode === "uzman" && step === 105 && (
            <motion.div key="uzman105" initial={{ opacity: 0, x: 10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -10 }} className="p-4 space-y-4 pb-24 flex flex-col h-full">
              
              <div className="mb-2">
                 <h2 className="text-[16px] font-extrabold text-content tracking-tight leading-tight">Talep Özeti</h2>
                 <p className="text-[10px] text-content-muted mt-1 font-medium">Bölgendeki hizmet verenlere gönderilecek talep bilgileri.</p>
              </div>

              <div className="bg-surface-muted rounded-xl p-4 border border-border-subtle flex flex-col gap-3">
                 <div className="flex justify-between items-start border-b border-border-faint pb-3">
                   <div className="flex flex-col">
                     <span className="text-[10px] text-content-muted font-medium mb-0.5">Kategori</span>
                     <span className="text-[12px] font-bold text-content">{uzmanCat?.name}</span>
                   </div>
                 </div>
                 <div className="flex justify-between items-start border-b border-border-faint pb-3">
                   <div className="flex flex-col">
                     <span className="text-[10px] text-content-muted font-medium mb-0.5">Alt Kategori</span>
                     <span className="text-[12px] font-bold text-content">{uzmanSubCat}</span>
                   </div>
                 </div>
                 <div className="flex justify-between items-start border-b border-border-faint pb-3">
                   <div className="flex flex-col">
                     <span className="text-[10px] text-content-muted font-medium mb-0.5">Çalışma Şekli</span>
                     <span className="text-[11px] font-bold text-content">
                       {workMode === "online" ? "Online" : workMode === "onsite" ? "Yerinde" : "Fark etmez / Hibrit"}
                     </span>
                   </div>
                   {workMode !== "online" && (
                    <div className="flex flex-col text-right">
                      <span className="text-[10px] text-content-muted font-medium mb-0.5">Konum</span>
                      <span className="text-[11px] font-bold text-content">{district}, {city}</span>
                    </div>
                   )}
                 </div>
                 <div className="flex justify-between items-start border-b border-border-faint pb-3">
                   <div className="flex flex-col">
                     <span className="text-[10px] text-content-muted font-medium mb-0.5">Zaman</span>
                     <span className="text-[11px] font-bold text-content">{timing}</span>
                   </div>
                   <div className="flex flex-col text-right">
                     <span className="text-[10px] text-content-muted font-medium mb-0.5">Bütçe</span>
                     <span className="text-[11px] font-bold text-[#F97316]">
                       {budgetType === "range" ? `${minBudget || 0}₺ - ${maxBudget || 0}₺` : "Teklif bekliyor"}
                     </span>
                   </div>
                 </div>
              </div>

              <div className="mt-auto pt-6">
                 <button 
                   onClick={handleSubmitUzman}
                   className="w-full py-3.5 rounded-xl text-[13px] font-extrabold tracking-tight transition-all text-white bg-[#F97316] shadow-md shadow-[#F97316]/20 active:scale-[0.98]"
                 >
                   Talep oluştur
                 </button>
              </div>

            </motion.div>
          )}

        </AnimatePresence>
      </div>

      {/* Bottom Actions for Ulak Only */}
      {mode === "ulak" && step < 5 && (
        <div className="p-4 bg-surface shrink-0 border-t border-border-subtle pb-safe z-50">
          <button 
            onClick={step === 1 ? () => setStep(step + 1) : handleSubmitUlak}
            disabled={step === 1 ? (!type) : (!isUlakFormValid())}
            className={`w-full py-3.5 rounded-xl text-[12px] font-bold tracking-tight transition-all text-white ${(step === 1 ? type : isUlakFormValid()) ? 'bg-[#F97316] active:scale-[0.98] shadow-md shadow-[#F97316]/20' : 'bg-[#F97316]/50 cursor-not-allowed'}`}
          >
            {step === 1 ? "Devam" : "Paylaş"}
          </button>
        </div>
      )}
    </motion.div>
  );
}
