import { useState } from "react";
import { motion } from "motion/react";
import { Phone, ChevronLeft, Send, Paperclip, Smile } from "lucide-react";
// Image placeholder
import ustaImg from "../icons/Usta.png";

interface ChatScreenProps {
  onBack: () => void;
}

export default function ChatScreen({ onBack }: ChatScreenProps) {
  const [message, setMessage] = useState("");

  return (
    <div className="w-full max-w-md mx-auto bg-surface min-h-screen flex flex-col relative overflow-hidden text-content">
      
      {/* Header */}
      <header className="sticky top-0 bg-surface/80 backdrop-blur-md z-30 pt-3 pb-3 px-4 border-b border-border-subtle flex items-center justify-between">
        
        {/* Left: Back + tiny profile */}
        <div className="flex items-center gap-2.5">
          <button 
            onClick={onBack} 
            className="w-[28px] h-[28px] flex items-center justify-center rounded-full bg-surface-muted border border-border-muted hover:bg-surface-hover transition-colors active:scale-95"
          >
            <ChevronLeft className="w-4 h-4 text-content" strokeWidth={1.5} />
          </button>
          
          <div className="w-[28px] h-[28px] rounded-full overflow-hidden bg-surface-muted border border-border-muted flex-shrink-0">
            <img src={ustaImg} alt="Profil" className="w-full h-full object-cover" />
          </div>
          <div className="flex flex-col">
            <span className="text-[13px] font-semibold tracking-tight text-content leading-tight">Ahmet Yılmaz</span>
            <span className="text-[10px] text-content-muted">Çevrimiçi</span>
          </div>
        </div>

        {/* Right: tiny phone */}
        <button className="w-[28px] h-[28px] flex items-center justify-center rounded-full bg-surface-muted border border-border-muted hover:bg-surface-hover transition-colors active:scale-95">
          <Phone className="w-4 h-4 text-content-muted" strokeWidth={1.5} />
        </button>
        
      </header>

      {/* Chat Area (Empty for now, just some dummy bubbles to show design) */}
      <main className="flex-1 overflow-y-auto p-4 space-y-4">
        
        {/* Received message */}
        <motion.div initial={{ opacity: 0, y: 5 }} animate={{ opacity: 1, y: 0 }} className="flex justify-start">
          <div className="bg-surface-muted border border-border-subtle rounded-2xl rounded-tl-sm px-4 py-2.5 max-w-[80%]">
            <p className="text-[12px] text-content leading-relaxed">Merhaba, tesisat işi için ilanınızı gördüm. Ne zaman müsaitsiniz?</p>
            <span className="text-[10px] text-content-muted mt-1 block">10:42</span>
          </div>
        </motion.div>

        {/* Sent message */}
        <motion.div initial={{ opacity: 0, y: 5 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }} className="flex justify-end">
          <div className="bg-[#F97316] text-white rounded-2xl rounded-tr-sm px-4 py-2.5 max-w-[80%] shadow-sm">
            <p className="text-[12px] leading-relaxed">Selam Ahmet usta, yarın öğleden sonra uygunum. Saat 14:00 gibi gelebilir misiniz?</p>
            <span className="text-[10px] text-orange-200 mt-1 block text-right">10:45</span>
          </div>
        </motion.div>

      </main>

      {/* Input Area */}
      <div className="sticky bottom-0 bg-surface border-t border-border-subtle p-3 px-4 pb-safe flex items-end gap-2">
        <button className="w-9 h-9 flex-shrink-0 flex items-center justify-center rounded-full bg-surface-muted text-content-muted hover:text-content transition-colors">
          <Paperclip className="w-[18px] h-[18px]" strokeWidth={1.5} />
        </button>
        
        <div className="flex-1 bg-surface-muted border border-border-muted rounded-2xl flex items-center px-3 min-h-[40px] focus-within:ring-1 focus-within:ring-[#F97316] transition-all">
          <button className="text-content-muted hover:text-content transition-colors mr-2">
            <Smile className="w-[18px] h-[18px]" strokeWidth={1.5} />
          </button>
          <input 
            type="text" 
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="Mesaj yaz..." 
            className="flex-1 bg-transparent border-none outline-none text-[12px] text-content placeholder:text-content-muted/70 py-2.5"
          />
        </div>

        <button 
          className={`w-10 h-10 flex-shrink-0 flex items-center justify-center rounded-full transition-all duration-200 
            ${message.trim() ? "bg-[#F97316] text-white shadow-md active:scale-95" : "bg-surface-muted text-content-muted border border-border-subtle"}`}
        >
          <Send className="w-[16px] h-[16px] ml-0.5" strokeWidth={message.trim() ? 2 : 1.5} />
        </button>
      </div>

    </div>
  );
}
