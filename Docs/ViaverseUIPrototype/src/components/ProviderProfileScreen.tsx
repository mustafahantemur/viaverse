import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "motion/react";
import { 
  ChevronLeft, LayoutDashboard, Inbox, Briefcase, MessageSquare, User, 
  ChevronRight, Settings, CheckCircle2, ShieldCheck, CreditCard, 
  MapPin, Clock, Tag, Image as ImageIcon, FileText, Bell, HelpCircle, LogOut, FlipHorizontal, Info, Camera, Plus, Trash2, Link as LinkIcon
} from "lucide-react";
import ProviderBottomNavigation from "./ProviderBottomNavigation";

type ProviderTab = "requests" | "jobs" | "panel" | "messages" | "profile";

interface ProviderProfileScreenProps {
  onBack: () => void;
  onTabChange: (tab: string) => void;
  onBackToUserMode?: () => void;
}

type SubScreen = null | "edit_profile" | "preview_profile" | "portfolio" | "tags";

const PROVIDER_TYPES = [
  "Bireysel hizmet veren",
  "İşletme / ekip",
  "Yaratıcı / dijital profil",
  "Serbest danışman"
];

const AVAILABLE_TAGS = [
  "Hızlı yanıt", "Online çalışır", "Yerinde hizmet", "Portföy mevcut", 
  "Deneyimli", "Yaratıcı işler", "Teknik destek", "Eğitim", 
  "Danışmanlık", "İçerik üretimi", "Tasarım", "Yazılım", 
  "Temizlik", "Organizasyon", "Evcil hayvan hizmetleri"
];

const PORTFOLIO_TYPES = [
  "Görsel", "Bağlantı", "Önce / sonra", "Video", "Web sitesi", "Sosyal medya"
];

export default function ProviderProfileScreen({ onBack, onTabChange, onBackToUserMode }: ProviderProfileScreenProps) {
  const [feedbackMessage, setFeedbackMessage] = useState<string | null>(null);
  const [activeSubScreen, setActiveSubScreen] = useState<SubScreen>(null);
  const [expandedSections, setExpandedSections] = useState<Record<string, boolean>>({
    'hizmet_profilim': true
  });

  const [providerProfile, setProviderProfile] = useState({
    displayName: "Zeynep Kaya",
    title: "UX/UI Designer",
    description: "Mobil uygulamalar için sade, modern ve kullanıcı odaklı arayüzler tasarlarım.",
    type: "Bireysel hizmet veren"
  });

  const [portfolioItems, setPortfolioItems] = useState([
    { id: 1, title: "Mobil uygulama arayüz tasarımı", type: "Görsel", desc: "Örnek e-ticaret uygulaması" }
  ]);

  const [selectedTags, setSelectedTags] = useState<string[]>(["Hızlı yanıt", "Yaratıcı işler"]);

  useEffect(() => {
    if (feedbackMessage) {
      const timer = setTimeout(() => setFeedbackMessage(null), 3000);
      return () => clearTimeout(timer);
    }
  }, [feedbackMessage]);

  const handleAction = (message: string) => {
    setFeedbackMessage(message);
  };

  const toggleSection = (section: string) => {
    setExpandedSections(prev => ({
      ...prev,
      [section]: !prev[section]
    }));
  };

  const AccordionSection = ({ id, title, icon: Icon, children }: any) => {
    const isExpanded = !!expandedSections[id];
    return (
      <div className="border-b border-border-faint last:border-none">
        <button 
          onClick={() => toggleSection(id)}
          className="w-full flex items-center justify-between p-4 bg-surface active:bg-surface-muted transition-colors"
        >
           <div className="flex items-center gap-3">
             <div className="w-7 h-7 rounded-full bg-surface-muted flex items-center justify-center">
               <Icon className="w-3.5 h-3.5 text-content-muted" strokeWidth={2} />
             </div>
             <span className="text-[13px] font-bold text-content">{title}</span>
           </div>
           <motion.div animate={{ rotate: isExpanded ? 90 : 0 }} transition={{ duration: 0.2 }}>
              <ChevronRight className="w-4 h-4 text-content-muted" />
           </motion.div>
        </button>
        <AnimatePresence>
          {isExpanded && (
            <motion.div 
              initial={{ height: 0, opacity: 0 }}
              animate={{ height: "auto", opacity: 1 }}
              exit={{ height: 0, opacity: 0 }}
              className="overflow-hidden"
            >
              <div className="pl-[52px] pr-4 pb-2">
                {children}
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    );
  };

  const ListItem = ({ icon: Icon, title, description, badge, onClick, danger }: any) => (
    <button 
      onClick={(e) => { e.stopPropagation(); onClick(); }}
      className={`w-full flex items-center justify-between py-2.5 border-b border-border-faint last:border-none active:opacity-70 transition-opacity flex-1 min-w-0 ${danger ? 'text-red-500' : 'text-content'}`}
    >
      <div className="flex flex-col items-start text-left flex-1 min-w-0 pr-3">
        <div className="flex items-center gap-2">
           {Icon && <Icon className={`w-3.5 h-3.5 shrink-0 ${danger ? 'text-red-400' : 'text-content-muted'}`} />}
          <span className="text-[12px] font-bold">{title}</span>
          {badge && (
            <span className="px-1.5 py-0.5 rounded-full bg-[#10B981]/10 text-[#10B981] text-[9px] font-bold border border-[#10B981]/20">
              {badge}
            </span>
          )}
        </div>
        {description && (
          <span className={`text-[10px] mt-0.5 leading-tight ${danger ? 'text-red-400/80' : 'text-content-muted'}`}>{description}</span>
        )}
      </div>
      <ChevronRight className={`w-3.5 h-3.5 shrink-0 ${danger ? 'text-red-300' : 'text-border-strong'}`} />
    </button>
  );

  const EditProfileSubScreen = () => {
    const [localProfile, setLocalProfile] = useState(providerProfile);

    const handleSave = () => {
      setProviderProfile(localProfile);
      setActiveSubScreen(null);
      handleAction("Hizmet profilin güncellendi.");
    };

    return (
      <div className="absolute inset-0 bg-surface z-40 flex flex-col min-h-screen animate-in slide-in-from-right-8 duration-200">
        <div className="flex items-center justify-between px-4 h-14 border-b border-border-faint shrink-0">
          <div className="flex items-center">
            <button onClick={() => setActiveSubScreen(null)} className="w-10 h-10 rounded-full flex items-center justify-center text-content active:scale-95 transition-transform bg-surface-muted ml-[-8px] mr-2">
              <ChevronLeft className="w-5 h-5" strokeWidth={2} />
            </button>
            <h1 className="text-[15px] font-bold text-content">Profilimi düzenle</h1>
          </div>
        </div>
        
        <div className="flex-1 overflow-y-auto p-4 pb-24 space-y-5">
          <div className="flex flex-col items-center mb-2">
            <div className="w-20 h-20 rounded-full bg-surface-muted border border-border-strong flex items-center justify-center relative mb-2">
              <User className="w-8 h-8 text-content-muted" strokeWidth={1.5} />
              <button className="absolute bottom-0 right-0 w-7 h-7 bg-surface border border-border-strong rounded-full flex items-center justify-center shadow-sm">
                <Camera className="w-3.5 h-3.5 text-content" />
              </button>
            </div>
            <span className="text-[10px] font-medium text-content-muted">Logo veya fotoğraf ekle</span>
          </div>

          <div className="space-y-3.5">
            <div className="space-y-1.5">
              <label className="text-[11px] font-bold text-content">Görünen ad veya işletme adı</label>
              <input 
                type="text" 
                value={localProfile.displayName}
                onChange={(e) => setLocalProfile({...localProfile, displayName: e.target.value})}
                className="w-full bg-surface-muted border border-border-subtle rounded-xl px-3.5 py-2.5 text-[12px] font-medium text-content placeholder:text-content-muted/50 focus:outline-none focus:border-[#F97316]/50 focus:ring-1 focus:ring-[#F97316]/50 transition-all"
              />
            </div>

            <div className="space-y-1.5">
              <label className="text-[11px] font-bold text-content">Hizmet başlığı</label>
              <input 
                type="text" 
                value={localProfile.title}
                onChange={(e) => setLocalProfile({...localProfile, title: e.target.value})}
                className="w-full bg-surface-muted border border-border-subtle rounded-xl px-3.5 py-2.5 text-[12px] font-medium text-content placeholder:text-content-muted/50 focus:outline-none focus:border-[#F97316]/50 focus:ring-1 focus:ring-[#F97316]/50 transition-all"
                placeholder="Örn: UX/UI Designer"
              />
            </div>

            <div className="space-y-1.5">
              <label className="text-[11px] font-bold text-content">Kısa açıklama</label>
              <textarea 
                value={localProfile.description}
                onChange={(e) => setLocalProfile({...localProfile, description: e.target.value})}
                rows={3}
                className="w-full bg-surface-muted border border-border-subtle rounded-xl px-3.5 py-2.5 text-[12px] font-medium text-content placeholder:text-content-muted/50 focus:outline-none focus:border-[#F97316]/50 focus:ring-1 focus:ring-[#F97316]/50 transition-all resize-none"
                placeholder="Kendinden veya hizmetlerinden kısaca bahset..."
              />
            </div>

            <div className="space-y-1.5">
              <label className="text-[11px] font-bold text-content">Hizmet veren tipi</label>
              <div className="grid grid-cols-1 gap-2">
                {PROVIDER_TYPES.map(type => (
                  <button 
                    key={type}
                    onClick={() => setLocalProfile({...localProfile, type})}
                    className={`text-left px-3.5 py-2.5 rounded-xl border text-[12px] font-medium transition-all ${localProfile.type === type ? 'border-[#F97316] bg-[#F97316]/5 text-content' : 'border-border-subtle bg-surface-muted text-content-muted hover:border-border-strong'}`}
                  >
                    {type}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>

        <div className="absolute bottom-0 w-full p-4 bg-surface border-t border-border-faint pb-safe">
          <button 
            onClick={handleSave}
            className="w-full py-3 bg-[#F97316] text-white rounded-xl text-[13px] font-bold active:scale-[0.98] transition-transform shadow-md shadow-[#F97316]/20"
          >
            Kaydet
          </button>
        </div>
      </div>
    );
  };

  const PreviewProfileSubScreen = () => {
    return (
      <div className="absolute inset-0 bg-surface z-40 flex flex-col min-h-screen animate-in slide-in-from-right-8 duration-200">
        <div className="flex items-center justify-between px-4 h-14 border-b border-border-faint shrink-0">
          <div className="flex items-center">
            <button onClick={() => setActiveSubScreen(null)} className="w-10 h-10 rounded-full flex items-center justify-center text-content active:scale-95 transition-transform bg-surface-muted ml-[-8px] mr-2">
              <ChevronLeft className="w-5 h-5" strokeWidth={2} />
            </button>
            <div className="flex flex-col">
              <h1 className="text-[15px] font-bold text-content">Profil önizlemesi</h1>
              <span className="text-[9px] text-content-muted">Müşteriler hizmet profilini bu şekilde görecek.</span>
            </div>
          </div>
        </div>
        
        <div className="flex-1 overflow-y-auto">
          <div className="pt-8 pb-6 px-4 flex flex-col items-center border-b border-border-faint text-center">
            <div className="w-20 h-20 rounded-full bg-surface-muted border border-border-strong flex items-center justify-center mb-3">
              <User className="w-10 h-10 text-content-muted" strokeWidth={1} />
            </div>
            <h2 className="text-[18px] font-bold text-content tracking-tight">{providerProfile.displayName}</h2>
            <p className="text-[13px] font-medium text-[#F97316] mt-0.5">{providerProfile.title}</p>
            
            <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-surface-muted border border-border-subtle mt-3">
              <span className="text-[10px] font-bold text-content-muted">{providerProfile.type}</span>
            </div>
            
            {providerProfile.description && (
              <p className="text-[12px] text-content mt-4 leading-relaxed max-w-[280px]">
                {providerProfile.description}
              </p>
            )}
            
            <div className="flex flex-wrap items-center justify-center gap-2 mt-5">
              {selectedTags.map(tag => (
                <span key={tag} className="px-2 py-1 rounded-md bg-surface-muted text-[10px] font-bold text-content border border-border-subtle">
                  {tag}
                </span>
              ))}
            </div>
          </div>

          <div className="p-4 border-b border-border-faint">
             <h3 className="text-[13px] font-bold text-content mb-3">Güven ve Onaylı Bilgiler</h3>
             <div className="space-y-2">
                <div className="flex items-center gap-2 text-[11px] font-medium text-content">
                   <div className="w-5 h-5 rounded-full bg-[#10B981]/10 flex items-center justify-center">
                     <CheckCircle2 className="w-3 h-3 text-[#10B981]" />
                   </div>
                   Telefon onaylı
                </div>
                <div className="flex items-center gap-2 text-[11px] font-medium text-content">
                   <div className="w-5 h-5 rounded-full bg-[#10B981]/10 flex items-center justify-center">
                     <CheckCircle2 className="w-3 h-3 text-[#10B981]" />
                   </div>
                   E-posta onaylı
                </div>
             </div>
          </div>

          {portfolioItems.length > 0 && (
            <div className="p-4">
               <h3 className="text-[13px] font-bold text-content mb-3">Örnek İşler ({portfolioItems.length})</h3>
               <div className="space-y-3">
                 {portfolioItems.map(item => (
                   <div key={item.id} className="flex gap-3 p-3 rounded-xl border border-border-faint bg-surface-muted">
                     <div className="w-12 h-12 rounded-lg bg-surface flex items-center justify-center shrink-0 border border-border-subtle">
                        {item.type === "Görsel" ? <ImageIcon className="w-5 h-5 text-content-muted" /> : <LinkIcon className="w-5 h-5 text-content-muted" />}
                     </div>
                     <div className="flex flex-col justify-center">
                        <span className="text-[12px] font-bold text-content line-clamp-1">{item.title}</span>
                        <span className="text-[10px] text-content-muted line-clamp-1 mt-0.5">{item.desc}</span>
                     </div>
                   </div>
                 ))}
               </div>
            </div>
          )}
        </div>

        <div className="absolute bottom-0 w-full p-4 bg-surface border-t border-border-faint pb-safe px-4 pt-3 space-y-2">
          <button className="w-full py-3 bg-[#F97316] text-white rounded-xl text-[13px] font-bold opacity-80 cursor-default">
            Teklif al
          </button>
          <button className="w-full py-3 bg-surface-muted text-content border border-border-strong rounded-xl text-[13px] font-bold opacity-80 cursor-default">
            Mesaj gönder
          </button>
        </div>
      </div>
    );
  };

  const PortfolioSubScreen = () => {
    const [isAdding, setIsAdding] = useState(false);
    const [newItem, setNewItem] = useState({ title: "", type: "Görsel", desc: "" });

    const handleAdd = () => {
      if (newItem.title.trim()) {
        setPortfolioItems([...portfolioItems, { id: Date.now(), ...newItem }]);
        setIsAdding(false);
        setNewItem({ title: "", type: "Görsel", desc: "" });
        handleAction("Örnek iş eklendi.");
      }
    };

    const handleDelete = (id: number) => {
      setPortfolioItems(portfolioItems.filter(item => item.id !== id));
      handleAction("Örnek iş silindi.");
    };

    return (
      <div className="absolute inset-0 bg-surface z-40 flex flex-col min-h-screen animate-in slide-in-from-right-8 duration-200">
        <div className="flex items-center justify-between px-4 h-14 border-b border-border-faint shrink-0">
          <div className="flex items-center">
            <button onClick={() => setActiveSubScreen(null)} className="w-10 h-10 rounded-full flex items-center justify-center text-content active:scale-95 transition-transform bg-surface-muted ml-[-8px] mr-2">
              <ChevronLeft className="w-5 h-5" strokeWidth={2} />
            </button>
            <div className="flex flex-col">
               <h1 className="text-[15px] font-bold text-content leading-tight">Portföy ve örnek işler</h1>
            </div>
          </div>
        </div>
        
        <div className="flex-1 overflow-y-auto p-4 pb-24 space-y-4">
          <p className="text-[11px] font-medium text-content-muted leading-relaxed">
            Yaptığın işleri, görselleri veya bağlantıları müşterilere gösterebilirsin.
          </p>

          <button 
            onClick={() => setIsAdding(true)}
            className="w-full flex items-center justify-center gap-1.5 py-3 rounded-xl border border-dashed border-[#F97316]/50 text-[#F97316] bg-[#F97316]/5 hover:bg-[#F97316]/10 transition-colors text-[12px] font-bold"
          >
            <Plus className="w-4 h-4" /> Örnek iş ekle
          </button>

          {isAdding && (
            <div className="p-4 rounded-xl border border-border-strong bg-surface-muted space-y-3">
              <h3 className="text-[12px] font-bold text-content">Yeni örnek iş</h3>
              <input 
                type="text" 
                value={newItem.title}
                onChange={(e) => setNewItem({...newItem, title: e.target.value})}
                placeholder="Örn: Mobil uygulama arayüz tasarımı"
                className="w-full bg-surface border border-border-subtle rounded-lg px-3 py-2 text-[12px] font-medium text-content focus:outline-none focus:border-[#F97316]/50"
              />
              <div className="flex flex-wrap gap-1.5">
                {PORTFOLIO_TYPES.map(type => (
                  <button 
                    key={type}
                    onClick={() => setNewItem({...newItem, type})}
                    className={`px-2.5 py-1.5 rounded-lg text-[10px] font-bold border transition-colors ${newItem.type === type ? 'bg-[#F97316] text-white border-[#F97316]' : 'bg-surface text-content-muted border-border-subtle hover:border-border-strong'}`}
                  >
                    {type}
                  </button>
                ))}
              </div>
              <input 
                type="text" 
                value={newItem.desc}
                onChange={(e) => setNewItem({...newItem, desc: e.target.value})}
                placeholder="Açıklama veya bağlantısı ekle"
                className="w-full bg-surface border border-border-subtle rounded-lg px-3 py-2 text-[12px] font-medium text-content focus:outline-none focus:border-[#F97316]/50"
              />
              <div className="flex gap-2 pt-2">
                <button onClick={() => setIsAdding(false)} className="flex-1 py-2 text-[11px] font-bold text-content-muted">İptal</button>
                <button onClick={handleAdd} className="flex-1 py-2 text-[11px] font-bold text-white bg-content rounded-lg">Ekle</button>
              </div>
            </div>
          )}

          <div className="space-y-2 mt-4">
            {portfolioItems.map(item => (
              <div key={item.id} className="flex items-center justify-between p-3 rounded-xl border border-border-faint bg-surface-muted">
                <div className="flex items-center gap-3 overflow-hidden">
                  <div className="w-10 h-10 rounded-lg bg-surface flex items-center justify-center shrink-0 border border-border-subtle">
                     {item.type === "Görsel" || item.type === "Önce / sonra" ? <ImageIcon className="w-4 h-4 text-content-muted" /> : <LinkIcon className="w-4 h-4 text-content-muted" />}
                  </div>
                  <div className="flex flex-col min-w-0">
                     <span className="text-[12px] font-bold text-content truncate">{item.title}</span>
                     <span className="text-[10px] text-content-muted flex items-center gap-1">
                       <span className="bg-surface-hover px-1 rounded text-[9px] font-bold border border-border-faint">{item.type}</span>
                       <span className="truncate">{item.desc}</span>
                     </span>
                  </div>
                </div>
                <button onClick={() => handleDelete(item.id)} className="p-2 text-content-muted hover:text-red-500 transition-colors shrink-0">
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            ))}
            {portfolioItems.length === 0 && !isAdding && (
              <div className="text-center py-8 text-content-muted text-[11px] font-medium">
                Henüz örnek iş eklenmemiş.
              </div>
            )}
          </div>
        </div>
      </div>
    );
  };

  const TagsSubScreen = () => {
    const [localTags, setLocalTags] = useState<string[]>(selectedTags);

    const toggleTag = (tag: string) => {
      if (localTags.includes(tag)) {
        setLocalTags(localTags.filter(t => t !== tag));
      } else {
        setLocalTags([...localTags, tag]);
      }
    };

    const handleSave = () => {
      setSelectedTags(localTags);
      setActiveSubScreen(null);
      handleAction("Hizmet etiketlerin güncellendi.");
    };

    return (
      <div className="absolute inset-0 bg-surface z-40 flex flex-col min-h-screen animate-in slide-in-from-right-8 duration-200">
        <div className="flex items-center justify-between px-4 h-14 border-b border-border-faint shrink-0">
          <div className="flex items-center">
            <button onClick={() => setActiveSubScreen(null)} className="w-10 h-10 rounded-full flex items-center justify-center text-content active:scale-95 transition-transform bg-surface-muted ml-[-8px] mr-2">
              <ChevronLeft className="w-5 h-5" strokeWidth={2} />
            </button>
            <h1 className="text-[15px] font-bold text-content">Öne çıkan etiketler</h1>
          </div>
        </div>
        
        <div className="flex-1 overflow-y-auto p-4 pb-24 space-y-4">
          <p className="text-[11px] font-medium text-content-muted leading-relaxed">
            Müşterilerin seni daha hızlı anlaması için profilinde görünecek etiketleri seç.
          </p>

          <div className="flex flex-wrap gap-2">
            {AVAILABLE_TAGS.map(tag => {
              const isSelected = localTags.includes(tag);
              return (
                <button 
                  key={tag}
                  onClick={() => toggleTag(tag)}
                  className={`px-3 py-1.5 rounded-full text-[12px] font-bold border transition-colors ${isSelected ? 'bg-[#F97316] text-white border-[#F97316]' : 'bg-surface-muted text-content-muted border-border-subtle hover:border-border-strong'}`}
                >
                  {tag}
                </button>
              );
            })}
          </div>
        </div>

        <div className="absolute bottom-0 w-full p-4 bg-surface border-t border-border-faint pb-safe">
          <button 
            onClick={handleSave}
            className="w-full py-3 bg-[#F97316] text-white rounded-xl text-[13px] font-bold active:scale-[0.98] transition-transform shadow-md shadow-[#F97316]/20"
          >
            Kaydet
          </button>
        </div>
      </div>
    );
  };

  return (
    <div className="min-h-screen bg-surface flex justify-center text-content font-sans">
      <div className="w-full max-w-md bg-surface min-h-screen relative flex flex-col">
        
        {/* Top Header */}
        <div className="flex items-center justify-between px-4 h-14 sticky top-0 bg-surface/90 backdrop-blur-md z-30 border-b border-border-faint shrink-0">
          <div className="flex items-center">
            <button 
              onClick={onBack}
              className="w-10 h-10 rounded-full flex items-center justify-center text-content active:scale-95 transition-transform bg-surface-muted ml-[-8px] mr-2"
            >
              <ChevronLeft className="w-5 h-5" strokeWidth={2} />
            </button>
            <div className="flex flex-col">
              <h1 className="text-[15px] font-bold text-content tracking-tight leading-none">Hizmet Profilim</h1>
            </div>
          </div>
          <button 
            onClick={() => onBackToUserMode ? onBackToUserMode() : handleAction("Üye moduna dönüş burada yapılacak.")}
            className="flex items-center gap-1.5 px-3 py-1.5 bg-surface text-content border border-border-strong rounded-full text-[10px] font-bold hover:bg-surface-muted transition-colors"
          >
            <FlipHorizontal className="w-3 h-3" />
            Üye moduna dön
          </button>
        </div>

        {activeSubScreen === "edit_profile" && <EditProfileSubScreen />}
        {activeSubScreen === "preview_profile" && <PreviewProfileSubScreen />}
        {activeSubScreen === "portfolio" && <PortfolioSubScreen />}
        {activeSubScreen === "tags" && <TagsSubScreen />}

        {/* Toast Notification */}
        <AnimatePresence>
          {feedbackMessage && (
            <motion.div 
              initial={{ opacity: 0, y: 50 }} 
              animate={{ opacity: 1, y: 0 }} 
              exit={{ opacity: 0, y: 20 }}
              className="fixed bottom-20 left-1/2 -translate-x-1/2 z-[110] bg-[#1a1a1a] text-white px-4 py-2.5 rounded-full shadow-lg text-[11px] font-bold whitespace-nowrap flex items-center gap-2 max-w-[90%] text-center"
            >
              {feedbackMessage}
            </motion.div>
          )}
        </AnimatePresence>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto pb-24">
          
          {/* Soft Alert Info */}
          <div className="mx-4 mt-4 mb-2 px-3 py-2.5 bg-blue-500/5 border border-blue-500/10 rounded-xl flex items-start gap-2.5">
             <div className="mt-0.5 text-[#F97316]">
               <Info className="w-4 h-4" />
             </div>
             <p className="text-[10px] text-content-muted font-medium leading-relaxed">
               Şu an <strong className="font-bold text-content">hizmet veren</strong> olarak işlem yapıyorsun. Üye moduna istediğin zaman dönebilirsin.
             </p>
          </div>

          {/* Profile Summary */}
          <div className="px-4 py-5 flex items-center gap-4 border-b border-border-faint">
             <div className="w-16 h-16 rounded-full bg-surface-muted border border-border-strong flex items-center justify-center shrink-0">
               <User className="w-8 h-8 text-content-muted" strokeWidth={1} />
             </div>
             <div className="flex-1 min-w-0 flex flex-col justify-center">
                <div className="flex items-center gap-2 mb-0.5">
                   <h2 className="text-[14px] font-bold text-content truncate">{providerProfile.displayName}</h2>
                   <span className="shrink-0 px-1.5 py-0.5 rounded-full bg-[#10B981]/10 text-[#10B981] text-[9px] font-bold border border-[#10B981]/20">Müsait</span>
                </div>
                <span className="text-[11px] font-medium text-content-muted block mb-1.5 truncate">{providerProfile.title}</span>
                <div className="flex items-center gap-1.5 mb-2">
                   <div className="flex-1 h-1.5 bg-border-faint rounded-full overflow-hidden max-w-[100px]">
                      <div className="h-full bg-[#F97316] w-[78%] rounded-full" />
                   </div>
                   <span className="text-[9px] font-bold text-content-muted">%78 Profil gücü</span>
                </div>
                <div className="flex gap-3">
                   <button onClick={() => setActiveSubScreen("preview_profile")} className="text-[10px] font-bold text-[#F97316] underline underline-offset-2">Profili görüntüle</button>
                   <button onClick={() => setActiveSubScreen("edit_profile")} className="text-[10px] font-bold text-content-muted hover:text-content transition-colors">Profilimi düzenle</button>
                </div>
             </div>
          </div>

          <div className="h-1 w-full bg-surface-muted border-b border-border-faint" />

          {/* Accordion Sections */}
          
          <AccordionSection id="hizmet_profilim" title="Hizmet profilim" icon={User}>
            <ListItem 
              title="Profilimi düzenle" 
              description="Ad, başlık, açıklama ve profil görselini düzenle."
              onClick={() => setActiveSubScreen("edit_profile")} 
            />
            <ListItem 
              title="Profili görüntüle" 
              description="Müşterilerin seni nasıl gördüğünü önizle."
              onClick={() => setActiveSubScreen("preview_profile")} 
            />
            <ListItem 
              title="Portföy ve örnek işler" 
              description="Görsel, bağlantı veya önce/sonra çalışmaları ekle."
              onClick={() => setActiveSubScreen("portfolio")} 
            />
            <ListItem 
              title="Öne çıkan etiketler" 
              description="Hızlı yanıt, online çalışır, portföy mevcut gibi etiketleri yönet."
              onClick={() => setActiveSubScreen("tags")} 
            />
          </AccordionSection>

          <AccordionSection id="hizmetler" title="Hizmetler ve çalışma alanı" icon={Briefcase}>
            <ListItem 
              title="Hizmetlerim" 
              description="Hizmet verdiğin kategori ve alt kategorileri düzenle."
              onClick={() => handleAction("Kategori seçimi açılacak.")} 
            />
            <ListItem 
              title="Çalışma şeklim" 
              description="Online, yerinde veya hibrit çalışma tercihini güncelle."
              onClick={() => handleAction("Çalışma şekli ayarları açılacak.")} 
            />
            <ListItem 
              title="Hizmet bölgem" 
              description="Yerinde hizmet verdiğin dinamik çevre ve mesafeyi ayarla."
              onClick={() => handleAction("Hizmet bölgesi haritası açılacak.")} 
            />
            <ListItem 
              title="Müsaitlik" 
              description="Çalışma günlerini, saatlerini ve müsaitlik durumunu yönet."
              badge="Aktif"
              onClick={() => handleAction("Müsaitlik takvimi açılacak.")} 
            />
          </AccordionSection>

          <AccordionSection id="fiyatlar" title="Fiyatlar ve teklifler" icon={Tag}>
            <ListItem 
              title="Fiyatlar & paketler" 
              description="Başlangıç fiyatı, paket veya keşif sonrası fiyat bilgisini düzenle."
              onClick={() => handleAction("Fiyat/paket ayarları açılacak.")} 
            />
            <ListItem 
              title="Teklif tercihleri" 
              description="Hangi taleplere teklif almak istediğini belirle."
              onClick={() => handleAction("Teklif filtreleri açılacak.")} 
            />
            <ListItem 
              title="Otomatik yanıtlar" 
              description="Sık kullandığın teklif mesajlarını hazırla."
              onClick={() => handleAction("Hazır şablonlar açılacak.")} 
            />
          </AccordionSection>

          <AccordionSection id="odeme" title="Ödeme alma" icon={CreditCard}>
            <ListItem 
              title="IBAN / banka hesabı" 
              description="Ödemelerinin yatırılacağı hesabı yönet."
              onClick={() => handleAction("Ödeme alma bilgileri sonraki adımda düzenlenecek.")} 
            />
            <ListItem 
              title="Bekleyen ödemeler" 
              description="Tamamlanan işlerden bekleyen ödemeleri takip et."
              onClick={() => handleAction("Bekleyen ödemeler listesi açılacak.")} 
            />
            <ListItem 
              title="Ödeme geçmişi" 
              description="Geçmiş ödeme ve kazanç kayıtlarını gör."
              onClick={() => handleAction("Ödeme geçmişi açılacak.")} 
            />
            <ListItem 
              title="Fatura ve vergi bilgileri" 
              description="İşletme, serbest çalışan veya bireysel ödeme bilgilerini düzenle."
              onClick={() => handleAction("Fatura bilgileri açılacak.")} 
            />
          </AccordionSection>

          <AccordionSection id="guven" title="Güven ve doğrulama" icon={ShieldCheck}>
            <ListItem 
              title="Telefon doğrulama" 
              badge="Doğrulandı"
              onClick={() => handleAction("Telefon doğrulama başarılı.")} 
            />
            <ListItem 
              title="E-posta doğrulama" 
              badge="Doğrulandı"
              onClick={() => handleAction("E-posta doğrulama başarılı.")} 
            />
            <ListItem 
              title="Kimlik / işletme doğrulama" 
              description="Doğrulanmış rozeti almak için bilgilerini onayla."
              onClick={() => handleAction("Kimlik doğrulama akışı açılacak.")} 
            />
            <ListItem 
              title="Belgeler ve sertifikalar" 
              onClick={() => handleAction("Belge yükleme açılacak.")} 
            />
            <ListItem 
              title="Güvenli ödeme durumu" 
              badge="Aktif"
              onClick={() => handleAction("Güvenli ödeme bilgi ekranı.")} 
            />
          </AccordionSection>

          <AccordionSection id="ayarlar" title="Panel ayarları" icon={Settings}>
            <ListItem 
              title="Bildirim tercihleri" 
              onClick={() => handleAction("Bildirim ayarları açılacak.")} 
            />
            <ListItem 
              title="Yardım merkezi" 
              onClick={() => handleAction("Viaverse yardım merkezine yönlendirilecek.")} 
            />
            <ListItem 
              title="Üye moduna dön" 
              onClick={() => onBackToUserMode ? onBackToUserMode() : handleAction("Üye moduna dönüş burada yapılacak.")} 
            />
            <ListItem 
              title="Panelden çıkış" 
              danger
              onClick={() => handleAction("Oturum kapatılacak.")} 
            />
          </AccordionSection>
          
        </div>

        {/* Temporary Bottom Navigation Placeholder - matching ProviderDashboardScreen */}
        <ProviderBottomNavigation 
          activeTab="profile" 
          onTabChange={(tab) => onTabChange(tab)} 
        />

      </div>
    </div>
  );
}
