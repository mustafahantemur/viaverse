import { useState } from "react";
import { motion, AnimatePresence } from "motion/react";
import { 
  ChevronLeft, 
  SlidersHorizontal, 
  Search, 
  User, 
  Clock, 
  MapPin, 
  Globe, 
  LayoutDashboard, 
  Inbox, 
  Briefcase, 
  MessageSquare,
  Zap
} from "lucide-react";
import ProviderRequestDetailScreen from "./ProviderRequestDetailScreen";
import ProviderCreateOfferSheet from "./ProviderCreateOfferSheet";
import ProviderBottomNavigation from "./ProviderBottomNavigation";

interface ProviderRequestsScreenProps {
  onBack: () => void;
  onTabChange: (tab: string) => void;
}


const FILTERS = ["Tümü", "Yeni", "Online", "Yerinde", "Yakınımda", "Acil"];

const MOCK_REQUESTS = [
  {
    id: 1,
    title: "Logo ve sosyal medya tasarımı",
    category: "Yaratıcı İşler & Medya",
    customer: "Elif K.",
    workMode: "online",
    time: "Bugün",
    description: "Yeni markam için logo ve Instagram gönderi şablonları arıyorum.",
    budget: "Teklif bekliyor",
    status: "Yeni talep"
  },
  {
    id: 2,
    title: "Ev temizliği",
    category: "Temizlik & Düzenleme",
    customer: "Murat A.",
    workMode: "onsite",
    distance: "3 km",
    time: "Yarın 10:00",
    description: "2+1 ev için detaylı temizlik desteği arıyorum.",
    budget: "1.500 - 2.000₺",
    status: "Yanıt bekliyor"
  },
  {
    id: 3,
    title: "Web sitesi arayüz tasarımı",
    category: "Dijital & Yazılım Hizmetleri",
    customer: "Deniz T.",
    workMode: "online",
    time: "Bu hafta",
    description: "Landing page ve mobil uyumlu tasarım için destek arıyorum.",
    budget: "Teklif bekliyor",
    status: "Yeni talep"
  },
  {
    id: 4,
    title: "Kombi bakım randevusu",
    category: "Ev, Tamirat & Tadilat",
    customer: "Selin A.",
    workMode: "onsite",
    distance: "5 km",
    time: "Cuma",
    description: "Kombi bakımı ve petek kontrolü için hizmet arıyorum.",
    budget: "Teklif bekliyor",
    status: "Acil"
  }
];

export default function ProviderRequestsScreen({ onBack, onTabChange }: ProviderRequestsScreenProps) {
  const [activeFilter, setActiveFilter] = useState("Tümü");
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedRequest, setSelectedRequest] = useState<any>(null);
  const [selectedRequestForOffer, setSelectedRequestForOffer] = useState<any>(null);

  const activeTab: string = "talepler";

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
          <button 
            onClick={onBack} 
            className="w-8 h-8 -ml-1 text-content-muted flex items-center justify-center hover:text-content transition-colors rounded-full active:scale-95 shrink-0"
          >
             <ChevronLeft className="w-5 h-5" strokeWidth={1.5} />
          </button>
          
          <div className="flex flex-col items-center">
            <h1 className="text-[15px] font-extrabold text-content tracking-tight leading-none mb-1">Talepler</h1>
            <div className="flex items-center gap-1.5">
              <div className="w-1.5 h-1.5 rounded-full bg-[#10B981] animate-pulse" />
              <span className="text-[9px] text-content-muted font-bold tracking-tight">Hizmet veren modu</span>
            </div>
          </div>
          
          <button 
            className="w-8 h-8 -mr-1 text-content-muted flex items-center justify-center hover:text-content transition-colors rounded-full active:scale-95 shrink-0"
          >
             <SlidersHorizontal className="w-4 h-4" strokeWidth={1.5} />
          </button>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto pb-24">

           {/* Search & Intro */}
           <div className="px-5 pt-4 pb-2">
              <p className="text-[10px] text-content-muted font-medium mb-3">
                Seçtiğin hizmetlere uygun gelen müşteri taleplerini incele.
              </p>
              
              <div className="relative">
                <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-content-muted" strokeWidth={1.5} />
                <input 
                  type="text" 
                  placeholder="Talep ara" 
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full bg-surface-muted border border-border-subtle rounded-full pl-9 pr-4 h-[36px] text-[12px] text-content placeholder-content-muted focus:outline-none focus:border-[#F97316]/50 transition-colors"
                />
              </div>
           </div>

           {/* Filters */}
           <div className="px-5 py-3 border-b border-border-faint">
              <div className="flex gap-2 overflow-x-auto pb-1 scrollbar-hide">
                 {FILTERS.map((filter) => (
                    <button
                       key={filter}
                       onClick={() => setActiveFilter(filter)}
                       className={`px-3.5 h-8 rounded-full border text-[10px] font-bold whitespace-nowrap transition-all ${
                         activeFilter === filter
                           ? "bg-[#F97316] border-[#F97316] text-white"
                           : "bg-surface border-border-strong text-content-muted hover:border-border-heavy hover:text-content"
                       }`}
                    >
                       {filter}
                    </button>
                 ))}
              </div>
           </div>

           {/* Results */}
           <div className="flex flex-col">
              {MOCK_REQUESTS.map((req) => (
                 <motion.div 
                   key={req.id}
                   initial={{ opacity: 0 }}
                   animate={{ opacity: 1 }}
                   className="flex flex-col p-5 border-b border-border-faint hover:bg-surface-muted/50 transition-colors"
                 >
                    {/* Top Meta */}
                    <div className="flex items-center justify-between mb-3">
                       <div className="flex items-center gap-2">
                          <span className="text-[10px] font-bold text-content flex items-center gap-1">
                             <User className="w-3 h-3 text-content-muted" />
                             {req.customer}
                          </span>
                          <span className="w-1 h-1 rounded-full bg-border-strong" />
                          <span className="text-[9px] font-medium text-content-muted flex items-center gap-1">
                             {req.workMode === "online" ? (
                               <><Globe className="w-3 h-3 text-[#F97316]" strokeWidth={2}/> {req.workMode === "online" ? "Online" : "Hibrit"}</>
                             ) : (
                               <><MapPin className="w-3 h-3 text-[#F97316]" strokeWidth={2}/> {req.distance}</>
                             )}
                          </span>
                       </div>
                       <span className="text-[9px] font-medium text-content-muted flex items-center gap-1">
                         <Clock className="w-3 h-3" /> {req.time}
                       </span>
                    </div>

                    {/* Content */}
                    <h3 className="text-[12px] font-extrabold text-content mb-1.5 leading-tight">{req.title}</h3>
                    <div className="mb-2">
                      <span className="inline-flex items-center px-1.5 py-0.5 rounded-full bg-surface-muted text-content-muted border border-border-subtle text-[9px] font-bold tracking-tight">
                         {req.category}
                      </span>
                    </div>
                    <p className="text-[10px] text-content-muted font-medium leading-relaxed line-clamp-2 mb-3">
                       {req.description}
                    </p>

                    {/* Meta Details */}
                    <div className="flex items-center gap-4 mb-4 text-[9px] font-medium">
                       <div className="flex items-center gap-1.5 text-content-muted">
                         <span className="font-bold text-content">Bütçe:</span> {req.budget}
                       </div>
                       <div className="flex items-center gap-1.5 text-content-muted">
                         <span className="font-bold text-content">Durum:</span> 
                         <span className={`font-bold ${req.status === "Acil" ? "text-red-500" : "text-[#F97316]"}`}>
                            {req.status}
                         </span>
                       </div>
                    </div>

                    {/* Actions */}
                    <div className="flex gap-2">
                       <button 
                         onClick={() => setSelectedRequestForOffer({
                            ...req,
                            customerName: req.customer,
                            createdAt: req.time
                         })}
                         className="flex-1 py-2.5 bg-[#F97316] text-white rounded-full text-[11px] font-bold active:scale-[0.98] transition-transform"
                       >
                          Teklif ver
                       </button>
                       <button 
                         onClick={() => setSelectedRequest({
                            ...req,
                            customerName: req.customer,
                            createdAt: req.time
                         })}
                         className="flex-[0.8] py-2.5 bg-surface text-content border border-border-strong rounded-full text-[11px] font-bold active:scale-[0.98] transition-transform"
                       >
                          Detay
                       </button>
                       <button 
                         onClick={() => console.log('Uygun değil tıklandı')}
                         className="px-4 py-2.5 bg-surface text-content-muted border border-border-subtle rounded-full text-[11px] font-bold hover:bg-surface-muted active:scale-[0.98] transition-all"
                       >
                          Uygun değil
                       </button>
                    </div>

                 </motion.div>
              ))}

              {MOCK_REQUESTS.length === 0 && (
                <div className="px-6 py-12 flex flex-col items-center text-center">
                  <div className="w-12 h-12 bg-surface-muted rounded-full flex items-center justify-center mb-4 border border-border-subtle">
                     <Inbox className="w-5 h-5 text-content-muted" strokeWidth={1.5} />
                  </div>
                  <h3 className="text-[13px] font-bold text-content tracking-tight mb-2">Şu an yeni talep yok</h3>
                  <p className="text-[10px] text-content-muted font-medium mb-5 leading-relaxed max-w-[240px]">
                    Hizmet alanını, kategorilerini veya profil bilgilerini güncelleyerek daha fazla talep alabilirsin.
                  </p>
                  <button 
                    onClick={() => console.log("Profilimi güçlendir")}
                    className="px-5 py-2.5 rounded-full border border-[#F97316] text-[#F97316] text-[11px] font-bold hover:bg-[#F97316]/5 transition-colors"
                  >
                    Profilimi güçlendir
                  </button>
                </div>
              )}
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
          activeTab="requests" 
          onTabChange={(tab) => onTabChange(tab)} 
        />

      </div>
    </div>
  );
}
