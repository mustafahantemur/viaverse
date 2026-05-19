import { motion } from "motion/react";
import { ChevronLeft, Search } from "lucide-react";
import BottomNavigation from "./BottomNavigation";
// Image placeholders
import ustaImg from "../icons/Usta.png";
import ulakImg from "../icons/ulak.png";

interface ChatHistoryScreenProps {
  onBack: () => void;
  onChat: () => void;
  onHome: () => void;
  onJobs: () => void;
  onProfile: () => void;
}

export default function ChatHistoryScreen({ onBack, onChat, onHome, onJobs, onProfile }: ChatHistoryScreenProps) {
  const chats = [
    { id: 1, name: "Ahmet Yılmaz", lastMessage: "Selam Ahmet usta, yarın öğleden sonra uygunum...", time: "10:45", unread: 0, image: ustaImg },
    { id: 2, name: "Ayşe Demir", lastMessage: "Teşekkür ederim, görüşmek üzere.", time: "Dün", unread: 2, image: ulakImg },
    { id: 3, name: "Mehmet Kaya", lastMessage: "Fiyat teklifimi ilettim, incelersiniz.", time: "Salı", unread: 0, image: ustaImg },
  ];

  return (
    <div className="w-full max-w-md mx-auto bg-surface min-h-screen flex flex-col relative overflow-hidden text-content">
      {/* Header */}
      <header className="sticky top-0 bg-surface z-30 pt-3 pb-3 px-4 border-b border-border-subtle flex items-center justify-between">
        <div className="flex items-center gap-3">
          <button 
            onClick={onBack} 
            className="w-[32px] h-[32px] flex items-center justify-center rounded-full bg-surface-muted border border-border-muted hover:bg-surface-hover transition-colors active:scale-95"
          >
            <ChevronLeft className="w-5 h-5 text-content" strokeWidth={1.5} />
          </button>
          <h1 className="text-[16px] font-semibold tracking-tight">Mesajlar</h1>
        </div>
        <div className="flex items-center gap-2">
          <button className="w-[32px] h-[32px] flex items-center justify-center rounded-full bg-surface-muted border border-border-muted hover:bg-surface-hover transition-colors active:scale-95">
            <Search className="w-4 h-4 text-content-muted" strokeWidth={1.5} />
          </button>
        </div>
      </header>

      {/* Chat List */}
      <main className="flex-1 overflow-y-auto">
        <div className="divide-y divide-border-subtle">
          {chats.map((chat) => (
            <motion.div 
              key={chat.id}
              initial={{ opacity: 0, y: 5 }}
              animate={{ opacity: 1, y: 0 }}
              onClick={onChat}
              className="flex items-center gap-3 p-4 hover:bg-surface-hover cursor-pointer active:opacity-70 transition-colors"
            >
              <div className="relative">
                <div className="w-12 h-12 rounded-full overflow-hidden bg-surface-muted border border-border-subtle flex-shrink-0">
                  <img src={chat.image} alt={chat.name} className="w-full h-full object-cover" />
                </div>
                {chat.unread > 0 && (
                  <span className="absolute top-0 right-0 w-3.5 h-3.5 bg-[#F97316] border-2 border-surface rounded-full"></span>
                )}
              </div>
              
              <div className="flex-1 min-w-0">
                <div className="flex justify-between items-baseline mb-0.5">
                  <h3 className="text-[14px] font-semibold tracking-tight truncate pr-2">{chat.name}</h3>
                  <span className={`text-[11px] flex-shrink-0 ${chat.unread > 0 ? 'text-[#F97316] font-medium' : 'text-content-muted'}`}>
                    {chat.time}
                  </span>
                </div>
                <div className="flex justify-between items-center gap-2">
                  <p className={`text-[12px] truncate ${chat.unread > 0 ? 'text-content font-medium' : 'text-content-muted'}`}>
                    {chat.lastMessage}
                  </p>
                  {chat.unread > 0 && (
                    <span className="flex-shrink-0 bg-[#F97316] text-white text-[10px] font-bold w-4 h-4 flex items-center justify-center rounded-full">
                      {chat.unread}
                    </span>
                  )}
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </main>

      <BottomNavigation 
        activeTab="messages" 
        onTabChange={(tab) => {
          if (tab === 'home') onHome();
          else if (tab === 'jobs') onJobs();
          else if (tab === 'profile') onProfile();
        }} 
      />
    </div>
  );
}
