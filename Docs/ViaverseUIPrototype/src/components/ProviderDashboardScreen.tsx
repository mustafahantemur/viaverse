import { useState } from "react";
import { motion, AnimatePresence } from "motion/react";
import { 
  LogOut, 
  ChevronRight, 
  Bell, 
  MapPin, 
  Globe, 
  Clock, 
  AlertCircle,
  LayoutDashboard,
  Inbox,
  Briefcase,
  MessageSquare,
  User,
  CheckCircle2,
  Zap,
  TrendingUp,
  Star
} from "lucide-react";
import ProviderRequestsScreen from "./ProviderRequestsScreen";
import ProviderRequestDetailScreen from "./ProviderRequestDetailScreen";
import ProviderCreateOfferSheet from "./ProviderCreateOfferSheet";
import ProviderJobsScreen from "./ProviderJobsScreen";
import ProviderMessagesScreen from "./ProviderMessagesScreen";
import ProviderProfileScreen from "./ProviderProfileScreen";
import ProviderBottomNavigation from "./ProviderBottomNavigation";

type ProviderTab = "requests" | "jobs" | "panel" | "messages" | "profile";

interface ProviderDashboardScreenProps {
  onBackToUserMode: () => void;
  profileData?: any;
}

export default function ProviderDashboardScreen({ onBackToUserMode, profileData }: ProviderDashboardScreenProps) {
  const [activeTab, setActiveTab] = useState<ProviderTab>("panel");
  const [selectedRequest, setSelectedRequest] = useState<any>(null);
  const [selectedRequestForOffer, setSelectedRequestForOffer] = useState<any>(null);

  if (activeTab === "requests") {
    return (
      <ProviderRequestsScreen 
        onBack={() => setActiveTab("panel")}
        onTabChange={(tab) => setActiveTab(tab as ProviderTab)}
      />
    );
  }

  if (activeTab === "jobs") {
    return (
      <ProviderJobsScreen 
        onBack={() => setActiveTab("panel")}
        onTabChange={(tab) => setActiveTab(tab as ProviderTab)}
      />
    );
  }

  if (activeTab === "messages") {
    return (
      <ProviderMessagesScreen 
        onBack={() => setActiveTab("panel")}
        onTabChange={(tab) => setActiveTab(tab as ProviderTab)}
      />
    );
  }

  if (activeTab === "profile") {
    return (
      <ProviderProfileScreen 
        onBack={() => setActiveTab("panel")}
        onTabChange={(tab) => setActiveTab(tab as ProviderTab)}
        onBackToUserMode={onBackToUserMode}
      />
    );
  }

  if (selectedRequest) {
    return (
      <ProviderRequestDetailScreen 
        onBack={() => setSelectedRequest(null)}
        requestData={selectedRequest}
      />
    );
  }

  return (
    <div className="min-h-screen bg-surface flex justify-center text-content font-sans">
      <div className="w-full max-w-md bg-surface min-h-screen relative flex flex-col">
        
        {/* Top Header */}
        <div className="flex items-center justify-between p-4 sticky top-0 bg-surface/90 backdrop-blur-md z-30 border-b border-border-faint">
          <div className="flex flex-col">
            <h1 className="text-[16px] font-extrabold text-content tracking-tight leading-none">Hizmet Veren Paneli</h1>
            <div className="flex items-center gap-1.5 mt-1">
              <div className="w-1.5 h-1.5 rounded-full bg-[#10B981] animate-pulse" />
              <span className="text-[9px] text-content-muted font-bold">Hizmet veren modu</span>
            </div>
          </div>
          
          <button 
            onClick={onBackToUserMode}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-full border border-border-subtle bg-surface hover:bg-surface-muted transition-colors active:scale-95"
          >
            <span className="text-[9px] font-bold text-content-muted tracking-tight">Üye moduna dön</span>
            <LogOut className="w-3 h-3 text-content-muted" strokeWidth={2} />
          </button>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto pb-24">
          
          {/* Status Area */}
          <div className="px-5 py-5 border-b border-border-faint flex items-center justify-between">
            <div className="flex flex-col">
              <div className="flex items-center gap-2 mb-1">
                <span className="inline-flex items-center px-2 py-0.5 rounded-full bg-[#10B981]/10 text-[#10B981] text-[10px] font-extrabold tracking-tight border border-[#10B981]/20">
                  Müsait
                </span>
              </div>
              <p className="text-[10px] text-content-muted font-medium">Bugün yeni taleplere açıksın.</p>
            </div>
            <button 
              onClick={() => console.log("Durum değiştirme tıklandı")}
              className="text-[10px] font-bold text-content-muted underline underline-offset-2 hover:text-content transition-colors"
            >
              Durumu değiştir
            </button>
          </div>

          {/* Mini Metrics */}
          <div className="grid grid-cols-4 gap-3 px-5 py-5 border-b border-border-faint">
             <div className="flex flex-col items-center justify-center p-3 rounded-xl bg-surface-muted border border-border-subtle">
               <span className="text-[15px] font-extrabold text-content tracking-tight">3</span>
               <span className="text-[9px] text-content-muted font-medium mt-0.5 text-center leading-tight">Yeni<br/>Talep</span>
             </div>
             <div className="flex flex-col items-center justify-center p-3 rounded-xl bg-surface-muted border border-border-subtle">
               <span className="text-[15px] font-extrabold text-content tracking-tight">2</span>
               <span className="text-[9px] text-content-muted font-medium mt-0.5 text-center leading-tight">Bekleyen<br/>Teklif</span>
             </div>
             <div className="flex flex-col items-center justify-center p-3 rounded-xl bg-surface-muted border border-border-subtle">
               <span className="text-[15px] font-extrabold text-[#10B981] tracking-tight">1</span>
               <span className="text-[9px] text-content-muted font-medium mt-0.5 text-center leading-tight">Aktif<br/>İş</span>
             </div>
             <div className="flex flex-col items-center justify-center p-3 rounded-xl bg-surface-muted border border-[#F97316]/20 bg-[#F97316]/5">
               <span className="text-[15px] font-extrabold text-[#EA580C] tracking-tight">%68</span>
               <span className="text-[9px] text-[#EA580C] font-medium mt-0.5 text-center leading-tight">Profil<br/>Gücü</span>
             </div>
          </div>

          {/* To-Do List */}
          <div className="px-5 py-5 border-b border-border-faint">
            <h3 className="text-[13px] font-bold text-content tracking-tight mb-3">Bugün ilgilenmen gerekenler</h3>
            <div className="flex flex-col space-y-3">
              <button onClick={() => setActiveTab("requests")} className="flex items-start gap-2 text-left hover:opacity-80 transition-opacity">
                 <div className="w-1.5 h-1.5 rounded-full bg-[#F97316] mt-1.5 shrink-0" />
                 <span className="text-[10px] text-content font-medium leading-relaxed">2 yeni talep yanıt bekliyor.</span>
              </button>
              <button className="flex items-start gap-2 text-left hover:opacity-80 transition-opacity">
                 <div className="w-1.5 h-1.5 rounded-full bg-content-muted mt-1.5 shrink-0" />
                 <span className="text-[10px] text-content font-medium leading-relaxed">1 teklif müşteriden cevap bekliyor.</span>
              </button>
              <button className="flex items-start gap-2 text-left hover:opacity-80 transition-opacity">
                 <div className="w-1.5 h-1.5 rounded-full bg-[#10B981] mt-1.5 shrink-0" />
                 <span className="text-[10px] text-content font-medium leading-relaxed">Profiline portföy ekleyerek görünürlüğünü %40 artırabilirsin.</span>
              </button>
            </div>
          </div>

          {/* New Requests */}
          <div className="px-5 py-5 border-b border-border-faint">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-[13px] font-bold text-content tracking-tight">Yeni Talepler</h3>
              <button 
                onClick={() => setActiveTab("requests")}
                className="text-[10px] font-extrabold text-[#F97316] hover:text-[#EA580C] transition-colors"
              >
                Tümünü gör
              </button>
            </div>
            
            <div className="flex flex-col gap-4">
              {/* Request 1 */}
              <div className="border border-border-subtle rounded-xl p-4 bg-surface hover:bg-surface-muted transition-colors">
                 <div className="flex items-center justify-between mb-2">
                   <div className="inline-flex items-center px-1.5 py-0.5 rounded text-[9px] font-bold bg-surface-muted text-content-muted border border-border-subtle">
                     Yaratıcı İşler & Medya
                   </div>
                   <span className="text-[9px] text-content-muted font-medium flex items-center gap-1">
                     <Clock className="w-3 h-3" /> Bugün
                   </span>
                 </div>
                 <h4 className="text-[12px] font-bold text-content mb-1 leading-tight">Logo ve sosyal medya tasarımı</h4>
                 <p className="text-[10px] text-content-muted font-medium leading-relaxed line-clamp-2 mb-3">
                   Yeni markam için modern bir logo ve Instagram gönderi şablonları hazırlayacak bir tasarımcı arıyorum.
                 </p>
                 <div className="flex items-center gap-3 mb-4">
                   <span className="text-[9px] font-medium flex items-center gap-1 text-content-muted">
                     <User className="w-3 h-3" /> Elif K.
                   </span>
                   <span className="text-[9px] font-medium flex items-center gap-1 text-content-muted">
                     <Globe className="w-3 h-3" /> Online
                   </span>
                 </div>
                 <div className="flex gap-2">
                   <button 
                     onClick={() => setSelectedRequestForOffer({
                        title: "Logo ve sosyal medya tasarımı",
                        category: "Yaratıcı İşler & Medya",
                        customerName: "Elif K.",
                        workMode: "online",
                        time: "Bugün",
                        createdAt: "Bugün",
                        description: "Yeni markam için modern bir logo ve Instagram gönderi şablonları hazırlayacak bir tasarımcı arıyorum.",
                        budget: "Teklif bekliyor",
                        status: "Yeni talep"
                     })}
                     className="flex-1 py-2 bg-[#F97316] text-white rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform"
                   >
                     Teklif Ver
                   </button>
                   <button 
                     onClick={() => setSelectedRequest({
                        title: "Logo ve sosyal medya tasarımı",
                        category: "Yaratıcı İşler & Medya",
                        customerName: "Elif K.",
                        workMode: "online",
                        time: "Bugün",
                        createdAt: "Bugün",
                        description: "Yeni markam için modern bir logo ve Instagram gönderi şablonları hazırlayacak bir tasarımcı arıyorum.",
                        budget: "Teklif bekliyor",
                        status: "Yeni talep"
                     })}
                     className="flex-1 py-2 bg-surface text-content border border-border-strong rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform"
                   >
                     Detay
                   </button>
                 </div>
              </div>

              {/* Request 2 */}
              <div className="border border-border-subtle rounded-xl p-4 bg-surface hover:bg-surface-muted transition-colors">
                 <div className="flex items-center justify-between mb-2">
                   <div className="inline-flex items-center px-1.5 py-0.5 rounded text-[9px] font-bold bg-surface-muted text-content-muted border border-border-subtle">
                     Temizlik & Düzenleme
                   </div>
                   <span className="text-[9px] text-content-muted font-medium flex items-center gap-1">
                     <Clock className="w-3 h-3" /> Dün
                   </span>
                 </div>
                 <h4 className="text-[12px] font-bold text-content mb-1 leading-tight">Detaylı ev temizliği</h4>
                 <p className="text-[10px] text-content-muted font-medium leading-relaxed line-clamp-2 mb-3">
                   2+1 boş ev için taşınma öncesi detaylı temizlik desteği arıyorum. Camlar dahil.
                 </p>
                 <div className="flex items-center gap-3 mb-4">
                   <span className="text-[9px] font-medium flex items-center gap-1 text-content-muted">
                     <User className="w-3 h-3" /> Murat A.
                   </span>
                   <span className="text-[9px] font-medium flex items-center gap-1 text-content-muted">
                     <MapPin className="w-3 h-3" /> 3 km (Kadıköy)
                   </span>
                 </div>
                 <div className="flex gap-2">
                   <button 
                     onClick={() => setSelectedRequestForOffer({
                        title: "Detaylı ev temizliği",
                        category: "Temizlik & Düzenleme",
                        customerName: "Murat A.",
                        workMode: "onsite",
                        distance: "3 km (Kadıköy)",
                        time: "Dün",
                        createdAt: "Dün",
                        description: "2+1 boş ev için taşınma öncesi detaylı temizlik desteği arıyorum. Camlar dahil.",
                        budget: "1.500 - 2.000₺",
                        status: "Yanıt bekliyor"
                     })}
                     className="flex-1 py-2 bg-[#F97316] text-white rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform"
                   >
                     Teklif Ver
                   </button>
                   <button 
                     onClick={() => setSelectedRequest({
                        title: "Detaylı ev temizliği",
                        category: "Temizlik & Düzenleme",
                        customerName: "Murat A.",
                        workMode: "onsite",
                        distance: "3 km (Kadıköy)",
                        time: "Dün",
                        createdAt: "Dün",
                        description: "2+1 boş ev için taşınma öncesi detaylı temizlik desteği arıyorum. Camlar dahil.",
                        budget: "1.500 - 2.000₺",
                        status: "Yanıt bekliyor"
                     })}
                     className="flex-1 py-2 bg-surface text-content border border-border-strong rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform"
                   >
                     Detay
                   </button>
                 </div>
              </div>

            </div>
          </div>

          {/* Active Job */}
          <div className="px-5 py-5 border-b border-border-faint">
            <h3 className="text-[13px] font-bold text-content tracking-tight mb-3">Aktif İşin</h3>
            <div className="border border-[#10B981]/30 bg-[#10B981]/5 rounded-xl p-4">
              <div className="flex items-center justify-between mb-2">
                 <span className="text-[12px] font-bold text-content">Kombi bakım randevusu</span>
                 <span className="px-2 py-0.5 bg-[#10B981] text-white text-[9px] font-bold rounded-full">Planlandı</span>
              </div>
              <div className="flex items-center gap-4 mb-3">
                 <span className="text-[10px] text-content-muted font-medium flex items-center gap-1">
                   <Clock className="w-3.5 h-3.5" /> Yarın 14:00
                 </span>
                 <span className="text-[10px] text-content-muted font-medium flex items-center gap-1">
                   <User className="w-3.5 h-3.5" /> Ayşe Y.
                 </span>
              </div>
              <div className="flex gap-2">
                <button 
                  onClick={() => setActiveTab("jobs")}
                  className="px-4 py-2 bg-white text-content border border-[#10B981]/30 rounded-lg text-[10px] font-bold text-center hover:bg-surface-muted transition-colors"
                >
                  İşi gör
                </button>
                <button 
                  onClick={() => setActiveTab("messages")}
                  className="px-4 py-2 bg-transparent text-content-muted rounded-lg text-[10px] font-bold text-center hover:text-content transition-colors underline underline-offset-2"
                >
                  Mesaj gönder
                </button>
              </div>
            </div>
          </div>

          {/* Profile Enhancement */}
          <div className="px-5 py-5">
             <h3 className="text-[13px] font-bold text-content tracking-tight mb-1">Profilini Güçlendir</h3>
             <p className="text-[10px] text-content-muted font-medium leading-relaxed mb-4">
               Fotoğraf, portföy, hizmet bölgesi ve açıklama ekleyen hizmet verenler ortalama 3 kat daha fazla talep alıyor.
             </p>
             <div className="flex flex-wrap gap-2">
                <button onClick={() => setActiveTab("profile")} className="px-4 py-2 bg-surface-muted border border-border-subtle rounded-lg text-[10px] font-bold text-content hover:bg-surface transition-colors">
                  Profilimi düzenle
                </button>
                <button onClick={() => setActiveTab("profile")} className="px-4 py-2 bg-surface-muted border border-border-subtle rounded-lg text-[10px] font-bold text-content hover:bg-surface transition-colors">
                  Profili önizle
                </button>
                <button onClick={() => setActiveTab("profile")} className="px-4 py-2 bg-surface-muted border border-border-subtle rounded-lg text-[10px] font-bold text-content hover:bg-surface transition-colors">
                  Hizmetlerim
                </button>
             </div>
          </div>

        </div>

        {/* ProviderCreateOfferSheet */}
        <ProviderCreateOfferSheet 
          open={!!selectedRequestForOffer}
          request={selectedRequestForOffer}
          onClose={() => setSelectedRequestForOffer(null)}
          onSubmitted={(offer) => console.log('Offer submitted', offer)}
        />

        <ProviderBottomNavigation 
          activeTab={activeTab} 
          onTabChange={(tab: string) => setActiveTab(tab as ProviderTab)} 
        />

      </div>
    </div>
  );
}
