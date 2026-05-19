import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "motion/react";
import { 
  ChevronLeft, LayoutDashboard, Inbox, Briefcase, MessageSquare, User, Clock, CheckCircle2, XCircle, MapPin, Globe, X, Star
} from "lucide-react";
import ProviderBottomNavigation from "./ProviderBottomNavigation";

interface ProviderJobsScreenProps {
  onBack: () => void;
  onTabChange: (tab: string) => void;
}

export default function ProviderJobsScreen({ onBack, onTabChange }: ProviderJobsScreenProps) {
  const [activeSegment, setActiveSegment] = useState<"teklifler" | "aktif" | "tamamlanan">("teklifler");

  // MOCK DATA
  const [offers, setOffers] = useState([
    {
      id: 1,
      title: "Logo ve sosyal medya tasarımı",
      customerName: "Elif K.",
      category: "Yaratıcı İşler & Medya",
      budget: "3.500₺",
      estimatedTime: "3 gün içinde",
      status: "Bekliyor",
      time: "Bugün",
      message: "Logo ve 6 sosyal medya şablonunu 3 gün içinde teslim edebilirim."
    },
    {
      id: 2,
      title: "Ev temizliği",
      customerName: "Murat A.",
      category: "Temizlik & Düzenleme",
      budget: "1.750₺",
      estimatedTime: "Yarın 10:00",
      status: "Kabul edildi",
      time: "Dün",
      message: "Belirttiğiniz saat için uygunum."
    },
    {
      id: 3,
      title: "Web sitesi arayüz tasarımı",
      customerName: "Deniz T.",
      category: "Dijital & Yazılım Hizmetleri",
      budget: "Keşif sonrası netleşir",
      estimatedTime: "Müşteriyle netleşir",
      status: "Reddedildi",
      time: "2 gün önce",
      message: "Kapsamı netleştirip tekliflendirebilirim."
    }
  ]);

  const [activeJobs, setActiveJobs] = useState([
    {
      id: 101,
      title: "Ev temizliği",
      customerName: "Murat A.",
      date: "Yarın 10:00",
      workMode: "Yerinde",
      status: "Planlandı"
    },
    {
      id: 102,
      title: "Kombi bakım randevusu",
      customerName: "Selin A.",
      date: "Cuma 14:00",
      workMode: "Yerinde",
      status: "Yolda"
    },
    {
      id: 103,
      title: "Logo revizyon görüşmesi",
      customerName: "Elif K.",
      date: "Bugün 18:00",
      workMode: "Online",
      status: "Devam ediyor"
    }
  ]);

  const [completedJobs, setCompletedJobs] = useState([
    {
      id: 201,
      title: "Instagram Reels kurgu desteği",
      customerName: "Ayşe D.",
      date: "Geçen hafta",
      budget: "2.000₺",
      score: "5.0",
      status: "Tamamlandı"
    }
  ]);

  const [activeSheetType, setActiveSheetType] = useState<'offer_detail' | 'offer_withdraw' | 'job_detail' | 'job_status' | 'completed_detail' | 'review_detail' | null>(null);
  const [selectedItem, setSelectedItem] = useState<any>(null);
  const [feedbackMessage, setFeedbackMessage] = useState<string | null>(null);

  useEffect(() => {
    if (feedbackMessage) {
      const timer = setTimeout(() => setFeedbackMessage(null), 3000);
      return () => clearTimeout(timer);
    }
  }, [feedbackMessage]);

  const renderStatusBadge = (status: string) => {
    switch (status) {
      case "Bekliyor":
        return <span className="px-2 py-0.5 rounded-md bg-[#F97316]/10 text-[#F97316] text-[9px] font-bold border border-[#F97316]/20">Bekliyor</span>;
      case "Kabul edildi":
        return <span className="px-2 py-0.5 rounded-md bg-[#10B981]/10 text-[#10B981] text-[9px] font-bold border border-[#10B981]/20">Kabul Edildi</span>;
      case "Reddedildi":
        return <span className="px-2 py-0.5 rounded-md bg-red-500/10 text-red-600 text-[9px] font-bold border border-red-500/20">Reddedildi</span>;
      case "Süresi doldu":
        return <span className="px-2 py-0.5 rounded-md bg-surface-muted text-content-muted text-[9px] font-bold border border-border-subtle">Süresi Doldu</span>;
      case "Planlandı":
      case "Yolda":
      case "Devam ediyor":
        return <span className="px-2 py-0.5 rounded-md bg-[#10B981] text-white text-[9px] font-bold shadow-sm">{status}</span>;
      case "Tamamlandı":
        return <span className="px-2 py-0.5 rounded-md bg-surface-muted text-content-muted text-[9px] font-bold border border-border-subtle flex items-center gap-1"><CheckCircle2 className="w-2.5 h-2.5" /> {status}</span>;
      default:
        return <span className="px-2 py-0.5 rounded-md bg-surface-muted text-content-muted text-[9px] font-bold border border-border-subtle">{status}</span>;
    }
  };

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
            <h1 className="text-[16px] font-extrabold text-content tracking-tight leading-none">İşler</h1>
            <span className="text-[9px] text-content-muted font-bold mt-1">Hizmet veren modu</span>
          </div>
        </div>

        {/* Segments */}
        <div className="px-4 py-3 border-b border-border-faint sticky top-14 bg-surface z-20">
          <div className="flex p-1 bg-surface-muted rounded-xl">
            <button
              onClick={() => setActiveSegment("teklifler")}
              className={`flex-1 py-1.5 text-[12px] font-bold rounded-lg transition-all ${
                activeSegment === "teklifler" 
                  ? "bg-surface shadow-[0_1px_3px_rgb(0,0,0,0.1)] text-[#F97316]" 
                  : "text-content-muted hover:text-content"
              }`}
            >
              Tekliflerim
            </button>
            <button
              onClick={() => setActiveSegment("aktif")}
              className={`flex-1 py-1.5 text-[12px] font-bold rounded-lg transition-all ${
                activeSegment === "aktif" 
                  ? "bg-surface shadow-[0_1px_3px_rgb(0,0,0,0.1)] text-[#F97316]" 
                  : "text-content-muted hover:text-content"
              }`}
            >
              Aktif İşler
            </button>
            <button
              onClick={() => setActiveSegment("tamamlanan")}
              className={`flex-1 py-1.5 text-[12px] font-bold rounded-lg transition-all ${
                activeSegment === "tamamlanan" 
                  ? "bg-surface shadow-[0_1px_3px_rgb(0,0,0,0.1)] text-[#F97316]" 
                  : "text-content-muted hover:text-content"
              }`}
            >
              Tamamlananlar
            </button>
          </div>
        </div>

        {/* Toast Notification */}
        <AnimatePresence>
          {feedbackMessage && (
            <motion.div 
              initial={{ opacity: 0, y: 50 }} 
              animate={{ opacity: 1, y: 0 }} 
              exit={{ opacity: 0, y: 20 }}
              className="fixed bottom-20 left-1/2 -translate-x-1/2 z-[110] bg-surface text-content px-4 py-2.5 rounded-full shadow-lg border border-border-faint text-[11px] font-bold whitespace-nowrap flex items-center gap-2"
            >
              {feedbackMessage}
            </motion.div>
          )}
        </AnimatePresence>

        {/* Sheets/Modals */}
        <AnimatePresence>
          {activeSheetType && (
            <div className="fixed inset-0 z-[100] flex items-end justify-center sm:items-center p-0 sm:p-4">
              <motion.div 
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                onClick={() => setActiveSheetType(null)}
                className="absolute inset-0 bg-content/20 backdrop-blur-sm"
              />
              <motion.div 
                initial={{ opacity: 0, y: "100%" }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: "100%" }}
                transition={{ type: "spring", damping: 25, stiffness: 300 }}
                className="w-full max-w-md bg-surface sm:rounded-2xl rounded-t-[32px] shadow-xl border border-border-faint relative z-10 flex flex-col max-h-[85vh]"
              >
                 {/* Drag Handle */}
                 <div className="flex justify-center pt-3 pb-2 w-full absolute top-0 sm:hidden">
                    <div className="w-10 h-1 rounded-full bg-border-strong" />
                 </div>

                 {/* offer_detail */}
                 {activeSheetType === 'offer_detail' && selectedItem && (
                    <div className="p-4 pt-6">
                      <div className="flex justify-between items-center mb-4 border-b border-border-faint pb-3">
                         <h3 className="text-[14px] font-extrabold text-content">Teklif detayı</h3>
                         <button type="button" onClick={() => setActiveSheetType(null)} className="w-8 h-8 rounded-full bg-surface-muted flex items-center justify-center text-content-muted active:scale-95"><X className="w-4 h-4" /></button>
                      </div>
                      <div className="space-y-3 p-3 bg-surface-muted rounded-xl border border-border-subtle mb-4">
                         <div className="flex flex-col">
                           <span className="text-[10px] text-content-muted">Talep başlığı</span>
                           <span className="text-[12px] font-bold text-content">{selectedItem.title}</span>
                         </div>
                         <div className="flex flex-col">
                           <span className="text-[10px] text-content-muted">Müşteri</span>
                           <span className="text-[11px] font-bold text-content">{selectedItem.customerName}</span>
                         </div>
                         <div className="flex flex-col">
                           <span className="text-[10px] text-content-muted">Kategori</span>
                           <span className="text-[11px] font-bold text-content">{selectedItem.category}</span>
                         </div>
                         <div className="flex justify-between">
                            <div className="flex flex-col">
                              <span className="text-[10px] text-content-muted">Tutar</span>
                              <span className="text-[11px] font-bold text-[#F97316]">{selectedItem.budget}</span>
                            </div>
                            <div className="flex flex-col text-right">
                              <span className="text-[10px] text-content-muted">Süre</span>
                              <span className="text-[11px] font-bold text-content">{selectedItem.estimatedTime}</span>
                            </div>
                         </div>
                         <div className="flex flex-col">
                           <span className="text-[10px] text-content-muted">Durum</span>
                           <span className="text-[11px] font-bold text-content">{selectedItem.status}</span>
                         </div>
                         <div className="flex flex-col">
                           <span className="text-[10px] text-content-muted">Gönderilen mesaj</span>
                           <span className="text-[10px] font-medium text-content bg-surface p-2 rounded border border-border-faint mt-1">{selectedItem.message}</span>
                         </div>
                      </div>
                      <button type="button" onClick={() => setActiveSheetType(null)} className="w-full py-3 bg-surface-muted text-content font-bold text-[12px] rounded-xl active:scale-95">Kapat</button>
                    </div>
                 )}

                 {/* offer_withdraw */}
                 {activeSheetType === 'offer_withdraw' && selectedItem && (
                    <div className="p-4 pt-6">
                      <div className="flex flex-col items-center text-center p-4">
                        <div className="w-12 h-12 rounded-full bg-red-500/10 flex items-center justify-center mb-4 border border-red-500/20">
                          <XCircle className="w-6 h-6 text-red-500" strokeWidth={2} />
                        </div>
                        <h3 className="text-[14px] font-extrabold text-content mb-2">Teklifi geri çek?</h3>
                        <p className="text-[10px] text-content-muted mb-6 leading-relaxed max-w-[240px]">Bu teklif müşterinin teklifleri arasından kaldırılacak. Bu işlem şu an sadece test amaçlıdır.</p>
                        <div className="flex w-full gap-2">
                           <button type="button" onClick={() => setActiveSheetType(null)} className="flex-1 py-3 bg-surface border border-border-strong text-content font-bold text-[12px] rounded-xl active:scale-95">Vazgeç</button>
                           <button type="button" onClick={() => {
                              setOffers(prev => prev.filter(o => o.id !== selectedItem.id));
                              setActiveSheetType(null);
                              setFeedbackMessage("Teklif geri çekildi.");
                           }} className="flex-1 py-3 bg-red-50 text-red-600 border border-red-100 font-bold text-[12px] rounded-xl active:scale-95">Geri çek</button>
                        </div>
                      </div>
                    </div>
                 )}

                 {/* job_detail */}
                 {activeSheetType === 'job_detail' && selectedItem && (
                    <div className="p-4 pt-6">
                      <div className="flex justify-between items-center mb-4 border-b border-border-faint pb-3">
                         <h3 className="text-[14px] font-extrabold text-content">İş detayı</h3>
                         <button type="button" onClick={() => setActiveSheetType(null)} className="w-8 h-8 rounded-full bg-surface-muted flex items-center justify-center text-content-muted active:scale-95"><X className="w-4 h-4" /></button>
                      </div>
                      <div className="space-y-3 p-3 bg-surface-muted rounded-xl border border-border-subtle mb-4">
                         <div className="flex flex-col">
                           <span className="text-[10px] text-content-muted">İş başlığı</span>
                           <span className="text-[12px] font-bold text-content">{selectedItem.title}</span>
                         </div>
                         <div className="flex flex-col">
                           <span className="text-[10px] text-content-muted">Müşteri</span>
                           <span className="text-[11px] font-bold text-content">{selectedItem.customerName}</span>
                         </div>
                         <div className="flex justify-between">
                            <div className="flex flex-col">
                              <span className="text-[10px] text-content-muted">Tarih / Saat</span>
                              <span className="text-[11px] font-bold text-content">{selectedItem.date}</span>
                            </div>
                            <div className="flex flex-col text-right">
                              <span className="text-[10px] text-content-muted">Çalışma Şekli</span>
                              <span className="text-[11px] font-bold text-content">{selectedItem.workMode}</span>
                            </div>
                         </div>
                         <div className="flex justify-between">
                             <div className="flex flex-col">
                               <span className="text-[10px] text-content-muted">Durum</span>
                               <span className="text-[11px] font-bold text-[#F97316]">{selectedItem.status}</span>
                             </div>
                         </div>
                         <div className="flex flex-col">
                           <span className="text-[10px] text-content-muted">Kısa açıklama</span>
                           <span className="text-[10px] font-medium text-content bg-surface p-2 rounded border border-border-faint mt-1">İlgili detaylar üzerinden çalışılacak.</span>
                         </div>
                      </div>
                      <button type="button" onClick={() => setActiveSheetType(null)} className="w-full py-3 bg-surface-muted text-content font-bold text-[12px] rounded-xl active:scale-95">Kapat</button>
                    </div>
                 )}

                 {/* job_status */}
                 {activeSheetType === 'job_status' && selectedItem && (
                    <div className="p-4 pt-6">
                      <div className="flex justify-between items-center mb-4 border-b border-border-faint pb-3">
                         <h3 className="text-[14px] font-extrabold text-content">İş durumunu güncelle</h3>
                         <button type="button" onClick={() => setActiveSheetType(null)} className="w-8 h-8 rounded-full bg-surface-muted flex items-center justify-center text-content-muted active:scale-95"><X className="w-4 h-4" /></button>
                      </div>
                      <div className="flex flex-col gap-2 mb-4">
                         {["Planlandı", "Yolda", "Devam ediyor", "Tamamlanmayı bekliyor", "Tamamlandı"].map(st => (
                            <button 
                              key={st}
                              type="button" 
                              onClick={() => {
                                 setActiveJobs(prev => prev.map(job => job.id === selectedItem.id ? { ...job, status: st } : job));
                                 setActiveSheetType(null);
                                 setFeedbackMessage("İş durumu güncellendi.");
                              }}
                              className={`p-3 text-[12px] font-bold rounded-xl text-left transition-colors border ${selectedItem.status === st ? 'bg-[#F97316]/10 text-[#F97316] border-[#F97316]/20' : 'bg-surface text-content border-border-subtle hover:bg-surface-muted'}`}
                            >
                              {st}
                            </button>
                         ))}
                      </div>
                    </div>
                 )}

                 {/* completed_detail */}
                 {activeSheetType === 'completed_detail' && selectedItem && (
                    <div className="p-4 pt-6 pb-safe">
                      <div className="flex justify-between items-center mb-4 border-b border-border-faint pb-3">
                         <h3 className="text-[14px] font-extrabold text-content">Tamamlanan iş</h3>
                         <button type="button" onClick={() => setActiveSheetType(null)} className="w-8 h-8 rounded-full bg-surface-muted flex items-center justify-center text-content-muted active:scale-95"><X className="w-4 h-4" /></button>
                      </div>
                      <div className="space-y-3 p-3 bg-surface-muted rounded-xl border border-border-subtle mb-4">
                         <div className="flex flex-col">
                           <span className="text-[10px] text-content-muted">İş başlığı</span>
                           <span className="text-[12px] font-bold text-content">{selectedItem.title}</span>
                         </div>
                         <div className="flex flex-col">
                           <span className="text-[10px] text-content-muted">Müşteri</span>
                           <span className="text-[11px] font-bold text-content">{selectedItem.customerName}</span>
                         </div>
                         <div className="flex justify-between">
                            <div className="flex flex-col">
                              <span className="text-[10px] text-content-muted">Tarih</span>
                              <span className="text-[11px] font-bold text-content">{selectedItem.date}</span>
                            </div>
                            <div className="flex flex-col text-right">
                              <span className="text-[10px] text-content-muted">Tutar</span>
                              <span className="text-[11px] font-bold text-[#F97316]">{selectedItem.budget}</span>
                            </div>
                         </div>
                         <div className="flex justify-between">
                            <div className="flex flex-col">
                              <span className="text-[10px] text-content-muted">Puan</span>
                              <span className="text-[11px] font-bold text-content flex items-center gap-1"><Star className="w-3.5 h-3.5 text-[#F97316]" /> {selectedItem.score}</span>
                            </div>
                            <div className="flex flex-col text-right">
                              <span className="text-[10px] text-content-muted">Durum</span>
                              <span className="text-[11px] font-bold text-content">{selectedItem.status}</span>
                            </div>
                         </div>
                      </div>
                      <button type="button" onClick={() => setActiveSheetType(null)} className="w-full py-3 bg-surface-muted text-content font-bold text-[12px] rounded-xl active:scale-95">Kapat</button>
                    </div>
                 )}

                 {/* review_detail */}
                 {activeSheetType === 'review_detail' && selectedItem && (
                    <div className="p-4 pt-6 pb-safe">
                      <div className="flex justify-between items-center mb-4 border-b border-border-faint pb-3">
                         <h3 className="text-[14px] font-extrabold text-content">Müşteri yorumu</h3>
                         <button type="button" onClick={() => setActiveSheetType(null)} className="w-8 h-8 rounded-full bg-surface-muted flex items-center justify-center text-content-muted active:scale-95"><X className="w-4 h-4" /></button>
                      </div>
                      <div className="p-4 bg-surface-muted rounded-xl border border-border-subtle mb-4 flex flex-col items-center text-center">
                         <div className="w-12 h-12 rounded-full bg-[#F97316]/10 flex items-center justify-center mb-2">
                           <Star className="w-6 h-6 text-[#F97316]" strokeWidth={2} fill="currentColor" />
                         </div>
                         <span className="text-[16px] font-black text-content mb-3">{selectedItem.score}</span>
                         <p className="text-[10px] text-content font-medium leading-relaxed italic max-w-[200px]">
                           "Çok hızlı ve özenli çalıştı, tekrar hizmet almak isterim."
                         </p>
                      </div>
                      <button type="button" onClick={() => setActiveSheetType(null)} className="w-full py-3 bg-surface-muted text-content font-bold text-[12px] rounded-xl active:scale-95">Kapat</button>
                    </div>
                 )}

              </motion.div>
            </div>
          )}
        </AnimatePresence>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto pb-24">
          <AnimatePresence mode="wait">
            
            {/* TEXTIFLER TAB */}
            {activeSegment === "teklifler" && (
              <motion.div 
                key="teklifler"
                initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 10 }}
                transition={{ duration: 0.15 }}
                className="flex flex-col p-4 gap-3"
              >
                {offers.length > 0 ? offers.map((offer) => (
                  <div key={offer.id} className="p-3.5 rounded-xl border border-border-subtle bg-surface">
                    <div className="flex items-start justify-between mb-2">
                       <div className="flex flex-col pr-4">
                         <h3 className="text-[12px] font-bold text-content leading-tight mb-1">{offer.title}</h3>
                         <span className="text-[10px] text-content-muted font-medium flex items-center gap-1">
                           <User className="w-3 h-3" /> {offer.customerName}
                         </span>
                       </div>
                       <div className="shrink-0 flex flex-col items-end gap-1">
                         {renderStatusBadge(offer.status)}
                         <span className="text-[9px] font-medium text-content-muted">{offer.time}</span>
                       </div>
                    </div>
                    
                    <div className="flex flex-wrap gap-x-4 gap-y-2 mb-3 bg-surface-muted/50 p-2 rounded-lg border border-border-faint">
                       <div className="flex flex-col">
                         <span className="text-[9px] text-content-muted font-medium">Tutar</span>
                         <span className="text-[10px] font-bold text-content">{offer.budget}</span>
                       </div>
                       <div className="flex flex-col">
                         <span className="text-[9px] text-content-muted font-medium">Süre</span>
                         <span className="text-[10px] font-bold text-content">{offer.estimatedTime}</span>
                       </div>
                    </div>

                    <p className="text-[10px] text-content font-medium leading-relaxed mb-3 bg-[#F97316]/5 p-2 rounded-lg border border-[#F97316]/10 border-l-2 border-l-[#F97316]">
                      "{offer.message}"
                    </p>

                    <div className="flex gap-2">
                      {offer.status === "Bekliyor" && (
                        <>
                          <button type="button" className="flex-1 py-2 bg-surface text-content border border-border-strong rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform" onClick={(e) => { e.stopPropagation(); setSelectedItem(offer); setActiveSheetType('offer_detail'); }}>Detay</button>
                          <button type="button" className="flex-1 py-2 bg-red-50 text-red-600 border border-red-100 rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform" onClick={(e) => { e.stopPropagation(); setSelectedItem(offer); setActiveSheetType('offer_withdraw'); }}>Geri çek</button>
                        </>
                      )}
                      {offer.status === "Kabul edildi" && (
                        <>
                          <button type="button" className="flex-1 py-2 bg-[#F97316] text-white rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform" onClick={(e) => { e.stopPropagation(); onTabChange("mesajlar"); }}>Mesaja git</button>
                          <button type="button" className="flex-1 py-2 bg-surface text-content border border-border-strong rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform" onClick={(e) => { e.stopPropagation(); setActiveSegment("aktif"); }}>İşi gör</button>
                        </>
                      )}
                      {offer.status === "Reddedildi" || offer.status === "Süresi doldu" ? (
                        <button type="button" className="flex-1 py-2 bg-surface text-content border border-border-strong rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform" onClick={(e) => { e.stopPropagation(); setSelectedItem(offer); setActiveSheetType('offer_detail'); }}>Detay</button>
                      ) : null}
                    </div>
                  </div>
                )) : (
                  <div className="flex flex-col items-center justify-center p-8 text-center text-content-muted">
                    <div className="w-12 h-12 rounded-full bg-surface-muted flex items-center justify-center mb-4">
                      <Briefcase className="w-6 h-6 text-content-muted" strokeWidth={1.5} />
                    </div>
                    <h3 className="text-[13px] font-bold text-content mb-1">Henüz teklif göndermedin</h3>
                    <p className="text-[10px] uppercase font-medium mt-1 leading-relaxed max-w-[200px]">Talepler ekranından sana uygun işleri inceleyip teklif verebilirsin.</p>
                    <button type="button" onClick={() => { setFeedbackMessage("Talepler ekranına geçiş burada yapılacak."); onTabChange("talepler"); }} className="mt-4 px-4 py-2 bg-surface-muted text-content font-bold text-[10px] rounded-lg active:scale-95 transition-transform">
                      Talepleri gör
                    </button>
                  </div>
                )}
              </motion.div>
            )}

            {/* AKTIF ISLER TAB */}
            {activeSegment === "aktif" && (
              <motion.div 
                key="aktif"
                initial={{ opacity: 0, x: 10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -10 }}
                transition={{ duration: 0.15 }}
                className="flex flex-col p-4 gap-3"
              >
                {activeJobs.length > 0 ? activeJobs.map(job => (
                  <div key={job.id} className="p-3.5 rounded-xl border border-[#10B981]/20 bg-surface">
                    <div className="flex items-start justify-between mb-2.5">
                       <div className="flex flex-col pr-4">
                         <h3 className="text-[12px] font-bold text-content leading-tight mb-1">{job.title}</h3>
                         <span className="text-[10px] text-content-muted font-medium flex items-center gap-1">
                           <User className="w-3 h-3" /> {job.customerName}
                         </span>
                       </div>
                       <div className="shrink-0 flex flex-col items-end gap-1">
                         {renderStatusBadge(job.status)}
                       </div>
                    </div>
                    
                    <div className="flex items-center gap-4 mb-4">
                       <span className="text-[10px] text-content-muted font-medium flex items-center gap-1">
                         <Clock className="w-3.5 h-3.5" /> {job.date}
                       </span>
                       <span className="text-[10px] text-content-muted font-medium flex items-center gap-1">
                         {job.workMode === "Online" ? <Globe className="w-3.5 h-3.5"/> : <MapPin className="w-3.5 h-3.5"/>} 
                         {job.workMode}
                       </span>
                    </div>

                    <div className="flex gap-2">
                       <button type="button" className="flex-1 py-2 bg-surface text-content border border-border-strong rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform" onClick={(e) => { e.stopPropagation(); setSelectedItem(job); setActiveSheetType('job_detail'); }}>Detay</button>
                       <button type="button" className="flex-1 py-2 bg-[#F97316]/10 text-[#F97316] border border-[#F97316]/20 rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform" onClick={(e) => { e.stopPropagation(); onTabChange("mesajlar"); }}>Mesaj</button>
                       <button type="button" className="flex-1 py-2 bg-surface text-content border border-[#10B981]/30 hover:bg-[#10B981]/5 rounded-lg text-[10px] font-bold text-center active:scale-95 transition-colors" onClick={(e) => { e.stopPropagation(); setSelectedItem(job); setActiveSheetType('job_status'); }}>Durumu güncelle</button>
                    </div>
                  </div>
                )) : (
                  <div className="flex flex-col items-center justify-center p-8 text-center text-content-muted">
                    <div className="w-12 h-12 rounded-full bg-surface-muted flex items-center justify-center mb-4">
                      <Clock className="w-6 h-6 text-content-muted" strokeWidth={1.5} />
                    </div>
                    <h3 className="text-[13px] font-bold text-content mb-1">Şu an aktif işin yok</h3>
                    <p className="text-[10px] font-medium leading-relaxed max-w-[200px]">Kabul edilen teklifler aktif iş olarak burada görünür.</p>
                  </div>
                )}
              </motion.div>
            )}

            {/* TAMAMLANANLAR TAB */}
            {activeSegment === "tamamlanan" && (
              <motion.div 
                key="tamamlanan"
                initial={{ opacity: 0, x: 10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -10 }}
                transition={{ duration: 0.15 }}
                className="flex flex-col p-4 gap-3"
              >
                {completedJobs.length > 0 ? completedJobs.map(job => (
                  <div key={job.id} className="p-3.5 rounded-xl border border-border-subtle bg-surface">
                    <div className="flex items-start justify-between mb-2.5">
                       <div className="flex flex-col pr-4">
                         <h3 className="text-[12px] font-bold text-content leading-tight mb-1">{job.title}</h3>
                         <span className="text-[10px] text-content-muted font-medium flex items-center gap-1.5">
                           <User className="w-3 h-3" /> {job.customerName}
                           <span className="w-1 h-1 rounded-full bg-border-strong opacity-50" />
                           <Clock className="w-3 h-3" /> {job.date}
                         </span>
                       </div>
                       <div className="shrink-0 flex flex-col items-end gap-1">
                         {renderStatusBadge(job.status)}
                       </div>
                    </div>
                    
                    <div className="flex items-center gap-4 mb-4 bg-surface-muted/50 p-2 rounded-lg border border-border-faint">
                       <span className="text-[11px] font-bold text-content">
                         {job.budget}
                       </span>
                       <span className="w-px h-3 bg-border-subtle" />
                       <span className="text-[10px] font-bold text-[#F97316] flex items-center gap-1">
                         ★ {job.score}
                       </span>
                    </div>

                    <div className="flex gap-2">
                       <button type="button" className="flex-1 py-2 bg-surface text-content border border-border-strong rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform" onClick={(e) => { e.stopPropagation(); setSelectedItem(job); setActiveSheetType('completed_detail'); }}>Detay</button>
                       <button type="button" className="flex-1 py-2 bg-surface text-content border border-border-strong rounded-lg text-[10px] font-bold text-center active:scale-95 transition-transform" onClick={(e) => { e.stopPropagation(); setSelectedItem(job); setActiveSheetType('review_detail'); }}>Yorumu gör</button>
                    </div>
                  </div>
                )) : (
                  <div className="flex flex-col items-center justify-center p-8 text-center text-content-muted">
                    <div className="w-12 h-12 rounded-full bg-surface-muted flex items-center justify-center mb-4">
                      <CheckCircle2 className="w-6 h-6 text-content-muted" strokeWidth={1.5} />
                    </div>
                    <h3 className="text-[13px] font-bold text-content mb-1">Henüz tamamlanan işin yok</h3>
                    <p className="text-[10px] font-medium leading-relaxed max-w-[200px]">Tamamladığın işler ve müşteri yorumları burada görünür.</p>
                  </div>
                )}
              </motion.div>
            )}

          </AnimatePresence>
        </div>

        {/* Temporary Bottom Navigation Placeholder - matching ProviderDashboardScreen */}
        <ProviderBottomNavigation 
          activeTab="jobs" 
          onTabChange={(tab) => onTabChange(tab)} 
        />

      </div>
    </div>
  );
}
