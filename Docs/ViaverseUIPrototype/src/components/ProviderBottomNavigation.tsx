import { motion } from "motion/react";
import { Inbox, Briefcase, MessageCircle, User as UserIcon } from "lucide-react";
import viaverseLogo from "../icons/viaverse_v_silver_green.svg";

type ProviderTab = "requests" | "jobs" | "panel" | "messages" | "profile";

interface ProviderBottomNavigationProps {
  activeTab: ProviderTab;
  onTabChange: (tab: string) => void;
}

export default function ProviderBottomNavigation({ activeTab, onTabChange }: ProviderBottomNavigationProps) {
  return (
    <nav className="fixed bottom-0 left-0 right-0 h-[80px] bg-surface border-t border-border-subtle flex items-center justify-around px-2 z-[100] pb-safe shadow-[0_-10px_40px_rgba(0,0,0,0.03)]">
      <div 
        onClick={() => onTabChange("requests")} 
        className={`flex flex-col items-center w-[64px] cursor-pointer transition-opacity ${activeTab === 'requests' ? 'opacity-100' : 'opacity-40 hover:opacity-100'}`}
      >
        <Inbox className={`w-5 h-5 ${activeTab === 'requests' ? 'text-[#F97316]' : 'text-content'}`} strokeWidth={activeTab === 'requests' ? 2 : 1.5} />
        <span className={`text-[10px] font-semibold mt-1 tracking-tight whitespace-nowrap ${activeTab === 'requests' ? 'text-[#F97316]' : 'text-content'}`}>Talepler</span>
      </div>

      <div 
        onClick={() => onTabChange("jobs")} 
        className={`flex flex-col items-center w-[64px] cursor-pointer transition-opacity ${activeTab === 'jobs' ? 'opacity-100' : 'opacity-40 hover:opacity-100'}`}
      >
        <Briefcase className={`w-5 h-5 ${activeTab === 'jobs' ? 'text-[#F97316]' : 'text-content'}`} strokeWidth={activeTab === 'jobs' ? 2 : 1.5} />
        <span className={`text-[10px] font-semibold mt-1 tracking-tight ${activeTab === 'jobs' ? 'text-[#F97316]' : 'text-content'}`}>İşler</span>
      </div>
      
      {/* ORTADAKİ DÖNEN PANEL BUTONU */}
      <div 
        onClick={() => onTabChange("panel")} 
        className="flex flex-col items-center justify-center -mt-6 cursor-pointer"
      >
        <div className={`w-14 h-14 rounded-full bg-[#F97316] flex items-center justify-center shadow-lg border-4 border-surface active:scale-95 transition-transform overflow-hidden relative group shadow-black/10`}>
            <motion.div
              animate={{ rotateY: 360 }}
              transition={{ duration: 8, ease: "linear", repeat: Infinity }}
              className="absolute inset-0 flex items-center justify-center transform-gpu"
              style={{ perspective: "1000px" }}
            >
              <img src={viaverseLogo} alt="Viaverse Panel" className="w-8 h-8 object-contain" />
            </motion.div>
        </div>
        <span className={`text-[10px] font-semibold mt-1 tracking-tight ${activeTab === 'panel' ? 'text-[#F97316]' : 'text-content'}`}>Panel</span>
      </div>

      <div 
        onClick={() => onTabChange("messages")} 
        className={`flex flex-col items-center w-[64px] cursor-pointer transition-opacity ${activeTab === 'messages' ? 'opacity-100' : 'opacity-40 hover:opacity-100'}`}
      >
        <MessageCircle className={`w-5 h-5 ${activeTab === 'messages' ? 'text-[#F97316]' : 'text-content'}`} strokeWidth={activeTab === 'messages' ? 2 : 1.5} />
        <span className={`text-[10px] font-semibold mt-1 tracking-tight ${activeTab === 'messages' ? 'text-[#F97316]' : 'text-content'}`}>Mesajlar</span>
      </div>

      <div 
        onClick={() => onTabChange("profile")} 
        className={`flex flex-col items-center w-[64px] cursor-pointer transition-opacity ${activeTab === 'profile' ? 'opacity-100' : 'opacity-40 hover:opacity-100'}`}
      >
        <div className={`w-5 h-5 rounded-full overflow-hidden border ${activeTab === 'profile' ? 'border-[#F97316] ring-1 ring-[#F97316]' : 'border-border-muted'}`}>
          <img src="https://i.pravatar.cc/150?img=47" alt="P" className="w-full h-full object-cover" />
        </div>
        <span className={`text-[10px] font-semibold mt-1 tracking-tight ${activeTab === 'profile' ? 'text-[#F97316]' : 'text-content'}`}>Profil</span>
      </div>
    </nav>
  );
}
