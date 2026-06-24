import React, { useState } from "react";
import { motion, AnimatePresence } from "motion/react";
import { 
  ChevronLeft, LayoutDashboard, Inbox, Briefcase, MessageSquare, User, Search, X, MessageCircle
} from "lucide-react";
import ProviderChatScreen from "./ProviderChatScreen";
import ProviderBottomNavigation from "./ProviderBottomNavigation";

interface ProviderMessagesScreenProps {
  onBack: () => void;
  onTabChange: (tab: string) => void;
}

export default function ProviderMessagesScreen({ onBack, onTabChange }: ProviderMessagesScreenProps) {
  const [activeFilter, setActiveFilter] = useState("Tümü");
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedMessage, setSelectedMessage] = useState<any>(null);

  const filters = ["Tümü", "Aktif işler", "Teklifler", "Okunmamış", "Tamamlananlar"];

  const messages = [
    {
      id: 1,
      customerName: "Elif K.",
      jobTitle: "Logo ve sosyal medya tasarımı",
      status: "Teklif kabul edildi",
      lastMessage: "Teklifinizi kabul ettim, detayları konuşabiliriz.",
      time: "12 dk",
      unreadCount: 2,
      avatarColor: "bg-purple-100 text-purple-600",
      offerAmount: "3.500₺",
      estimatedTime: "3 gün içinde",
      workMode: "Online"
    },
    {
      id: 2,
      customerName: "Murat A.",
      jobTitle: "Ev temizliği",
      status: "Aktif iş",
      lastMessage: "Yarın 10:00 benim için uygun.",
      time: "Bugün",
      unreadCount: 0,
      avatarColor: "bg-blue-100 text-blue-600",
      offerAmount: "1.750₺",
      estimatedTime: "Yarın 10:00",
      workMode: "Yerinde"
    },
    {
      id: 3,
      customerName: "Selin A.",
      jobTitle: "Kombi bakım randevusu",
      status: "Planlandı",
      lastMessage: "Adres bilgisini paylaştım.",
      time: "Dün",
      unreadCount: 1,
      avatarColor: "bg-emerald-100 text-emerald-600",
      offerAmount: "600₺",
      estimatedTime: "Cuma 14:00",
      workMode: "Yerinde"
    },
    {
      id: 4,
      customerName: "Deniz T.",
      jobTitle: "Web sitesi arayüz tasarımı",
      status: "Yanıt bekliyor",
      lastMessage: "Kapsamı netleştirdikten sonra başlayabiliriz.",
      time: "2 gün",
      unreadCount: 0,
      avatarColor: "bg-orange-100 text-orange-600",
      offerAmount: "Keşif sonrası",
      estimatedTime: "Belli değil",
      workMode: "Hibrit"
    }
  ];

  const getInitials = (name: string) => {
    return name.split(" ").map(n => n[0]).join("").substring(0, 2).toUpperCase();
  };

  const renderStatusBadge = (status: string) => {
    if (status === "Teklif kabul edildi" || status === "Aktif iş" || status === "Planlandı") {
      return <span className="px-1.5 py-0.5 rounded-full bg-[#10B981]/10 text-[#10B981] text-[9px] font-bold border border-[#10B981]/20">{status}</span>;
    }
    if (status === "Yanıt bekliyor") {
      return <span className="px-1.5 py-0.5 rounded-full bg-[#F97316]/10 text-[#F97316] text-[9px] font-bold border border-[#F97316]/20">{status}</span>;
    }
    if (status === "Tamamlandı") {
      return <span className="px-1.5 py-0.5 rounded-full bg-surface-muted text-content-muted text-[9px] font-bold border border-border-subtle">{status}</span>;
    }
    return <span className="px-1.5 py-0.5 rounded-full bg-surface-muted text-content-muted text-[9px] font-bold border border-border-subtle">{status}</span>;
  };

  const filteredMessages = messages.filter(msg => {
    if (activeFilter === "Okunmamış" && msg.unreadCount === 0) return false;
    if (activeFilter === "Aktif işler" && !(msg.status === "Aktif iş" || msg.status === "Planlandı")) return false;
    if (activeFilter === "Teklifler" && !(msg.status === "Teklif kabul edildi" || msg.status === "Yanıt bekliyor")) return false;
    if (activeFilter === "Tamamlananlar" && msg.status !== "Tamamlandı") return false;
    
    if (searchQuery) {
      if (!msg.customerName.toLowerCase().includes(searchQuery.toLowerCase()) && 
          !msg.jobTitle.toLowerCase().includes(searchQuery.toLowerCase())) {
        return false;
      }
    }
    return true;
  });

  if (selectedMessage) {
    return (
      <ProviderChatScreen 
        conversation={selectedMessage}
        onBack={() => setSelectedMessage(null)}
      />
    );
  }

  return (
    <div className="min-h-screen bg-surface flex justify-center text-content font-sans">
      <div className="w-full max-w-md bg-surface min-h-screen relative flex flex-col">
        
        {/* Top Header */}
        <div className="flex items-center px-4 h-14 sticky top-0 bg-surface/90 backdrop-blur-md z-30 border-b border-border-faint shrink-0">
          <button 
            onClick={onBack}
            className="w-10 h-10 rounded-full flex items-center justify-center text-content active:scale-95 transition-transform bg-surface-muted ml-[-8px] mr-2"
          >
            <ChevronLeft className="w-5 h-5" strokeWidth={2} />
          </button>
          <div className="flex flex-col">
            <h1 className="text-[16px] font-extrabold text-content tracking-tight leading-none">Mesajlar</h1>
            <span className="text-[9px] text-content-muted font-bold mt-1">Hizmet veren modu</span>
          </div>
        </div>

        <div className="p-4 border-b border-border-faint sticky top-14 bg-surface/95 backdrop-blur-md z-20 space-y-3">
           {/* Filters */}
           <div className="flex gap-2 overflow-x-auto no-scrollbar pb-1 -mx-4 px-4 mask-edges">
             {filters.map(f => (
               <button
                 key={f}
                 onClick={() => setActiveFilter(f)}
                 className={`shrink-0 px-3 py-1.5 rounded-full text-[10px] font-bold transition-all border ${
                   activeFilter === f 
                    ? "bg-[#F97316] text-white border-[#F97316] shadow-sm" 
                    : "bg-surface-muted text-content-muted border-border-subtle hover:bg-surface-hover"
                 }`}
               >
                 {f}
               </button>
             ))}
           </div>
           
           {/* Search */}
           <div className="relative">
             <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-content-muted" />
             <input 
               type="text" 
               placeholder="Müşteri veya iş ara" 
               value={searchQuery}
               onChange={(e) => setSearchQuery(e.target.value)}
               className="w-full h-9 bg-surface-muted rounded-full pl-9 pr-4 text-[12px] font-medium text-content placeholder:text-content-muted/80 outline-none border border-border-subtle focus:border-[#F97316]/50 transition-colors"
             />
           </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto pb-24">
           {filteredMessages.length > 0 ? (
             <div className="flex flex-col">
                {filteredMessages.map((msg, idx) => (
                  <button 
                    key={msg.id}
                    onClick={() => setSelectedMessage(msg)}
                    className={`flex items-start gap-3 p-4 bg-surface active:bg-surface-muted transition-colors text-left border-b border-border-faint ${msg.unreadCount > 0 ? 'bg-[#F97316]/[0.02]' : ''}`}
                  >
                    <div className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 text-[12px] font-extrabold ${msg.avatarColor}`}>
                      {getInitials(msg.customerName)}
                    </div>
                    
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center justify-between mb-0.5">
                        <span className="text-[12px] font-bold text-content truncate pr-2">{msg.customerName}</span>
                        <span className={`text-[10px] shrink-0 font-medium ${msg.unreadCount > 0 ? 'text-[#F97316]' : 'text-content-muted'}`}>
                          {msg.time}
                        </span>
                      </div>
                      
                      <div className="flex items-center gap-1.5 mb-1.5">
                         <span className="text-[11px] font-bold text-content truncate">{msg.jobTitle}</span>
                         <span className="shrink-0">{renderStatusBadge(msg.status)}</span>
                      </div>
                      
                      <p className={`text-[10px] line-clamp-2 leading-relaxed ${msg.unreadCount > 0 ? 'text-content font-bold' : 'text-content-muted font-medium'}`}>
                        {msg.lastMessage}
                      </p>
                    </div>

                    {msg.unreadCount > 0 && (
                      <div className="shrink-0 flex items-center justify-center self-center w-5 h-5 rounded-full bg-[#F97316] text-white text-[9px] font-black shadow-sm shadow-[#F97316]/20">
                        {msg.unreadCount}
                      </div>
                    )}
                  </button>
                ))}
             </div>
           ) : (
             <div className="flex flex-col items-center justify-center p-8 text-center text-content-muted mt-10">
                <div className="w-12 h-12 rounded-full bg-surface-muted flex items-center justify-center mb-4">
                  <MessageCircle className="w-6 h-6 text-content-muted" strokeWidth={1.5} />
                </div>
                <h3 className="text-[13px] font-bold text-content mb-1">Henüz mesaj yok</h3>
                <p className="text-[10px] font-medium leading-relaxed max-w-[200px]">Tekliflerin kabul edildiğinde müşteri konuşmaları burada görünür.</p>
                <button 
                  onClick={() => onTabChange("talepler")}
                  className="mt-4 px-4 py-2 bg-surface-muted text-content font-bold text-[10px] rounded-lg active:scale-95 transition-transform"
                >
                  Talepleri gör
                </button>
             </div>
           )}
        </div>

        {/* Temporary Bottom Navigation Placeholder - matching ProviderDashboardScreen */}
        <ProviderBottomNavigation 
          activeTab="messages" 
          onTabChange={(tab) => onTabChange(tab)} 
        />

      </div>
    </div>
  );
}
