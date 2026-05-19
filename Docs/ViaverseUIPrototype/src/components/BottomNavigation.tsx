import { motion } from "motion/react";
import { Compass, Briefcase, MessageCircle, User as UserIcon } from "lucide-react";
import viaverseLogo from "../icons/viaverse_v_silver_green.svg";

type Tab = "home" | "jobs" | "publish" | "messages" | "profile";

interface BottomNavigationProps {
  activeTab: Tab;
  onTabChange: (tab: Tab) => void;
}

export default function BottomNavigation({ activeTab, onTabChange }: BottomNavigationProps) {
  return (
    <nav className="fixed bottom-0 left-0 right-0 h-[80px] bg-surface border-t border-border-subtle flex items-center justify-around px-2 z-[100] pb-safe shadow-[0_-10px_40px_rgba(0,0,0,0.03)]">
      <div 
        onClick={() => onTabChange("home")} 
        className={`flex flex-col items-center w-[64px] cursor-pointer transition-opacity ${activeTab === 'home' ? 'opacity-100' : 'opacity-40 hover:opacity-100'}`}
      >
        <Compass className={`w-5 h-5 ${activeTab === 'home' ? 'text-[#F97316]' : 'text-content'}`} strokeWidth={activeTab === 'home' ? 2 : 1.5} />
        <span className={`text-[10px] font-semibold mt-1 tracking-tight whitespace-nowrap ${activeTab === 'home' ? 'text-[#F97316]' : 'text-content'}`}>Keşfet</span>
      </div>

      <div 
        onClick={() => onTabChange("jobs")} 
        className={`flex flex-col items-center w-[64px] cursor-pointer transition-opacity relative ${activeTab === 'jobs' ? 'opacity-100' : 'opacity-40 hover:opacity-100'}`}
      >
        <div className="relative">
          <Briefcase className={`w-5 h-5 ${activeTab === 'jobs' ? 'text-[#F97316]' : 'text-content'}`} strokeWidth={activeTab === 'jobs' ? 2 : 1.5} />
          <span className="absolute -top-1.5 -right-1.5 bg-[#F97316] text-white text-[9px] font-bold w-4 h-4 flex items-center justify-center rounded-full border-2 border-surface">1</span>
        </div>
        <span className={`text-[10px] font-semibold mt-1 tracking-tight ${activeTab === 'jobs' ? 'text-[#F97316]' : 'text-content'}`}>İşlerim</span>
      </div>
      
      {/* ORTADAKİ YAYINLA BUTONU */}
      <div 
        onClick={() => onTabChange("publish")} 
        className="flex flex-col items-center justify-center -mt-6 cursor-pointer"
      >
        <div className="w-14 h-14 rounded-full bg-[#F97316] flex items-center justify-center shadow-lg border-4 border-surface active:scale-95 transition-transform overflow-hidden relative group shadow-black/10">
            <motion.div
              animate={{ rotateY: 360 }}
              transition={{ duration: 8, ease: "linear", repeat: Infinity }}
              className="absolute inset-0 flex items-center justify-center transform-gpu"
              style={{ perspective: "1000px" }}
            >
              <img src={viaverseLogo} alt="Viaverse" className="w-8 h-8 object-contain" />
            </motion.div>
        </div>
        <span className={`text-[10px] font-semibold mt-1 tracking-tight ${activeTab === 'publish' ? 'text-[#F97316]' : 'text-content'}`}>Yayınla</span>
      </div>

      <div 
        onClick={() => onTabChange("messages")} 
        className={`flex flex-col items-center w-[64px] cursor-pointer transition-opacity relative ${activeTab === 'messages' ? 'opacity-100' : 'opacity-40 hover:opacity-100'}`}
      >
        <div className="relative">
          <MessageCircle className={`w-5 h-5 ${activeTab === 'messages' ? 'text-[#F97316]' : 'text-content'}`} strokeWidth={activeTab === 'messages' ? 2 : 1.5} />
          <span className="absolute -top-1.5 -right-1.5 bg-[#F97316] text-white text-[9px] font-bold w-4 h-4 flex items-center justify-center rounded-full border-2 border-surface">2</span>
        </div>
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
