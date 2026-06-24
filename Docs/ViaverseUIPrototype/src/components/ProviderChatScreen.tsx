import React, { useState, useRef, useEffect } from "react";
import { motion, AnimatePresence } from "motion/react";
import { ChevronLeft, Paperclip, Send, Info, ExternalLink, RefreshCw } from "lucide-react";

interface ProviderChatScreenProps {
  conversation: any;
  onBack: () => void;
}

export default function ProviderChatScreen({ conversation, onBack }: ProviderChatScreenProps) {
  const [messages, setMessages] = useState([
    {
      id: 1,
      sender: "customer",
      text: "Teklifinizi kabul ettim, detayları konuşabiliriz.",
      time: "10:30"
    },
    {
      id: 2,
      sender: "provider",
      text: "Teşekkür ederim. Logo yönü ve marka hissi için birkaç referans paylaşabilir misiniz?",
      time: "10:35"
    },
    {
      id: 3,
      sender: "customer",
      text: "Minimal, premium ve modern bir görünüm istiyoruz. Instagram için de şablon lazım.",
      time: "10:42"
    },
    {
      id: 4,
      sender: "provider",
      text: "Anladım. İlk taslakları 2 gün içinde paylaşabilirim.",
      time: "10:45"
    }
  ]);

  const [newMessage, setNewMessage] = useState("");
  const [feedbackMessage, setFeedbackMessage] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  useEffect(() => {
    if (feedbackMessage) {
      const timer = setTimeout(() => setFeedbackMessage(null), 3000);
      return () => clearTimeout(timer);
    }
  }, [feedbackMessage]);

  const handleSend = () => {
    if (!newMessage.trim()) return;
    
    setMessages(prev => [
      ...prev,
      {
        id: Date.now(),
        sender: "provider",
        text: newMessage.trim(),
        time: new Date().toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' })
      }
    ]);
    setNewMessage("");
  };

  const getInitials = (name: string) => {
    return name?.split(" ").map((n: string) => n[0]).join("").substring(0, 2).toUpperCase() || "C";
  };

  // Fallbacks
  const c = {
    customerName: conversation?.customerName || "Elif K.",
    jobTitle: conversation?.jobTitle || "Logo ve sosyal medya tasarımı",
    status: conversation?.status || "Teklif kabul edildi",
    offerAmount: conversation?.offerAmount || "3.500₺",
    estimatedTime: conversation?.estimatedTime || "3 gün içinde",
    workMode: conversation?.workMode || "Online"
  };

  const renderStatusBadge = (status: string) => {
    if (status === "Teklif kabul edildi" || status === "Aktif iş" || status === "Planlandı") {
      return <span className="px-1.5 py-0.5 rounded bg-[#10B981]/10 text-[#10B981] text-[9px] font-bold border border-[#10B981]/20">{status}</span>;
    }
    if (status === "Yanıt bekliyor") {
      return <span className="px-1.5 py-0.5 rounded bg-[#F97316]/10 text-[#F97316] text-[9px] font-bold border border-[#F97316]/20">{status}</span>;
    }
    return <span className="px-1.5 py-0.5 rounded bg-surface-muted text-content-muted text-[9px] font-bold border border-border-subtle">{status}</span>;
  };

  return (
    <div className="min-h-screen bg-surface flex justify-center text-content font-sans">
      <div className="w-full max-w-md bg-surface min-h-screen relative flex flex-col">
        
        {/* Top Header */}
        <div className="flex items-center px-4 py-2 bg-surface/90 backdrop-blur-md z-30 border-b border-border-faint shrink-0">
          <button 
            onClick={onBack}
            className="w-10 h-10 rounded-full flex items-center justify-center text-content active:scale-95 transition-transform bg-surface-muted ml-[-8px] mr-2 shrink-0"
          >
            <ChevronLeft className="w-5 h-5" strokeWidth={2} />
          </button>
          
          <div className="flex-1 min-w-0 pr-2">
            <div className="flex items-center gap-2 mb-0.5">
              <h1 className="text-[13px] font-bold text-content truncate">{c.customerName}</h1>
              <span className="px-1.5 py-0.5 rounded text-[9px] font-bold bg-[#F97316]/10 text-[#F97316] border border-[#F97316]/20 whitespace-nowrap">Hizmet veren modu</span>
            </div>
            <div className="flex items-center gap-1.5">
              <span className="text-[10px] text-content-muted font-medium truncate">{c.jobTitle}</span>
            </div>
          </div>
          
          <div className="shrink-0 flex flex-col items-end gap-1">
             <div className="w-8 h-8 rounded-full bg-surface-muted flex items-center justify-center text-[11px] font-extrabold text-content">
               {getInitials(c.customerName)}
             </div>
          </div>
        </div>

        {/* Context Strip */}
        <div className="bg-surface-muted/50 border-b border-border-faint p-3 shrink-0">
          <div className="flex items-start gap-2 mb-2">
            <Info className="w-3.5 h-3.5 text-content-muted shrink-0 mt-0.5" />
            <div className="flex-1">
              <p className="text-[10px] text-content-muted font-medium mb-1.5 leading-tight">Bu konuşma bir müşteri işiyle ilgili.</p>
              <div className="flex flex-wrap items-center gap-x-3 gap-y-1">
                <span className="text-[10px] font-bold text-content flex items-center gap-1"><span className="text-content-muted font-medium">Teklif:</span> {c.offerAmount}</span>
                <span className="text-[10px] font-bold text-content flex items-center gap-1"><span className="text-content-muted font-medium">Süre:</span> {c.estimatedTime}</span>
                <span className="text-[10px] font-bold text-content flex items-center gap-1"><span className="text-content-muted font-medium">Çalışma:</span> {c.workMode}</span>
                <span className="ml-auto">{renderStatusBadge(c.status)}</span>
              </div>
            </div>
          </div>
          <div className="flex gap-2">
            <button 
              onClick={() => setFeedbackMessage("İş detayı sonraki adımda açılacak.")}
              className="flex-1 py-1.5 bg-surface text-content border border-border-strong rounded text-[10px] font-bold text-center active:scale-95 transition-transform flex items-center justify-center gap-1"
            >
              <ExternalLink className="w-3 h-3" /> İş detayını gör
            </button>
            <button 
              onClick={() => setFeedbackMessage("Durum güncelleme sonraki adımda eklenecek.")}
              className="flex-1 py-1.5 bg-surface text-content border border-border-strong rounded text-[10px] font-bold text-center active:scale-95 transition-transform flex items-center justify-center gap-1"
            >
              <RefreshCw className="w-3 h-3" /> Durumu güncelle
            </button>
          </div>
        </div>

        {/* Toast Notification */}
        <AnimatePresence>
          {feedbackMessage && (
            <motion.div 
              initial={{ opacity: 0, y: 10 }} 
              animate={{ opacity: 1, y: 0 }} 
              exit={{ opacity: 0, y: 10 }}
              className="absolute top-2 left-1/2 -translate-x-1/2 z-[110] bg-surface text-content px-4 py-2 rounded-full shadow-lg border border-border-faint text-[10px] font-bold whitespace-nowrap flex items-center gap-2"
            >
              {feedbackMessage}
            </motion.div>
          )}
        </AnimatePresence>

        {/* Chat Area */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
           {messages.map((msg) => {
             const isProvider = msg.sender === "provider";
             return (
               <div key={msg.id} className={`flex flex-col ${isProvider ? "items-end" : "items-start"}`}>
                 <div 
                   className={`max-w-[80%] px-3 py-2 rounded-2xl ${
                     isProvider 
                       ? "bg-[#F97316] text-white rounded-tr-sm" 
                       : "bg-surface-muted border border-border-faint text-content rounded-tl-sm"
                   }`}
                 >
                   <p className="text-[11px] leading-relaxed">{msg.text}</p>
                 </div>
                 <span className="text-[9px] text-content-muted mt-1 font-medium px-1">
                   {msg.time}
                 </span>
               </div>
             );
           })}
           <div ref={messagesEndRef} />
        </div>

        {/* Input Area */}
        <div className="p-3 bg-surface border-t border-border-faint pb-safe shrink-0">
          <div className="flex items-center gap-2 bg-surface-muted rounded-2xl p-1.5 border border-border-subtle focus-within:border-[#F97316]/30 transition-colors">
            <button className="w-9 h-9 flex items-center justify-center text-content-muted hover:text-content active:scale-95 transition-transform rounded-xl">
              <Paperclip className="w-4 h-4" />
            </button>
            <input 
              type="text" 
              placeholder="Müşteriye mesaj yaz"
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              className="flex-1 bg-transparent text-[12px] font-medium outline-none placeholder:text-content-muted/70 text-content py-2 px-1"
              onKeyDown={(e) => {
                if (e.key === "Enter") handleSend();
              }}
            />
            <button 
              onClick={handleSend}
              disabled={!newMessage.trim()}
              className={`w-9 h-9 flex items-center justify-center rounded-xl transition-all ${
                newMessage.trim() ? "bg-[#F97316] text-white active:scale-95 shadow-sm shadow-[#F97316]/20" : "bg-surface text-content-muted/50"
              }`}
            >
              <Send className="w-4 h-4" />
            </button>
          </div>
        </div>

      </div>
    </div>
  );
}
