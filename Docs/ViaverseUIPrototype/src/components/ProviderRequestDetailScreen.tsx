import React, { useState, useEffect, Component, ReactNode } from "react";
import { motion, AnimatePresence } from "motion/react";
import { 
  ChevronLeft, User, ShieldCheck, MapPin, Globe, Clock, 
  Briefcase, FileText, XCircle, Info, CheckCircle2
} from "lucide-react";
import ProviderCreateOfferSheet from "./ProviderCreateOfferSheet";

interface ErrorBoundaryProps {
  children: ReactNode;
  onBack: () => void;
}

interface ErrorBoundaryState {
  hasError: boolean;
}

class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  public state: ErrorBoundaryState = { hasError: false };
  public props: ErrorBoundaryProps;

  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.props = props;
  }

  static getDerivedStateFromError(): ErrorBoundaryState {
    return { hasError: true };
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen bg-surface flex justify-center text-content font-sans">
          <div className="w-full max-w-md bg-surface min-h-screen flex flex-col">
            <div className="flex items-center p-4 sticky top-0 bg-surface/90 backdrop-blur-md z-30 border-b border-border-faint">
              <button 
                onClick={this.props.onBack}
                className="w-10 h-10 flex items-center justify-center rounded-full bg-surface-muted hover:bg-border-faint transition-colors active:scale-95"
              >
                <ChevronLeft className="w-5 h-5 text-content" />
              </button>
              <h1 className="text-[15px] font-extrabold text-content ml-3 tracking-tight">Talep detayı</h1>
            </div>
            <div className="flex-1 flex flex-col items-center justify-center p-6 text-center">
              <p className="text-[13px] text-content-muted font-medium">Talep bilgileri yüklenemedi.</p>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children; 
  }
}

interface RequestData {
  id?: number | string;
  title?: string;
  category?: string;
  customerName?: string;
  customer?: string;
  workMode?: string;
  mode?: string;
  distance?: string;
  dist?: string;
  time?: string;
  createdAt?: string;
  budget?: string;
  description?: string;
  desc?: string;
  attachments?: string[];
  verifiedCustomer?: boolean;
  status?: string;
}

interface ProviderRequestDetailScreenProps {
  onBack: () => void;
  requestData?: RequestData | null;
}

const FALLBACK_DATA: RequestData = {
  title: "Logo ve sosyal medya tasarımı",
  category: "Yaratıcı İşler & Medya",
  customerName: "Elif K.",
  workMode: "Online",
  time: "Bugün",
  createdAt: "12 dk önce",
  budget: "Teklif bekliyor",
  description: "Yeni markam için logo ve Instagram gönderi şablonları arıyorum. Modern, sade ve premium bir görünüm istiyorum. İlk aşamada logo, renk önerisi ve 6 gönderi şablonu yeterli.",
  attachments: ["Marka örneği", "Instagram referansı"],
  verifiedCustomer: true,
  status: "Yeni talep"
};

function ProviderRequestDetailScreenContent({ onBack, requestData }: ProviderRequestDetailScreenProps) {
  const normalizedRequest = {
    title: requestData?.title || FALLBACK_DATA.title,
    category: requestData?.category || FALLBACK_DATA.category,
    customerName: requestData?.customerName || requestData?.customer || FALLBACK_DATA.customerName,
    workMode: requestData?.workMode || requestData?.mode || FALLBACK_DATA.workMode,
    time: requestData?.time || FALLBACK_DATA.time,
    createdAt: requestData?.createdAt || FALLBACK_DATA.createdAt,
    budget: requestData?.budget || FALLBACK_DATA.budget,
    description: requestData?.description || requestData?.desc || FALLBACK_DATA.description,
    distance: requestData?.distance || requestData?.dist || FALLBACK_DATA.distance,
    attachments: requestData?.attachments || FALLBACK_DATA.attachments,
    verifiedCustomer: requestData?.verifiedCustomer ?? FALLBACK_DATA.verifiedCustomer,
    status: requestData?.status || FALLBACK_DATA.status
  };

  const data = normalizedRequest as RequestData;
  const cName = data.customerName || "Müşteri";
  const wMode = data.workMode?.toLowerCase();
  
  const [isOfferSheetOpen, setIsOfferSheetOpen] = useState(false);
  const [showDeclineConfirm, setShowDeclineConfirm] = useState(false);
  const [feedbackMessage, setFeedbackMessage] = useState<string | null>(null);

  useEffect(() => {
    let timeoutId: NodeJS.Timeout;
    if (feedbackMessage) {
      timeoutId = setTimeout(() => {
        setFeedbackMessage(null);
      }, 3000);
    }
    return () => clearTimeout(timeoutId);
  }, [feedbackMessage]);

  const handleDeclineConfirm = () => {
    setShowDeclineConfirm(false);
    setFeedbackMessage("Talep uygun değil olarak işaretlendi.");
  };
  
  return (
    <div className="min-h-screen bg-surface flex justify-center text-content font-sans">
      <div className="w-full max-w-md bg-surface min-h-screen relative flex flex-col">

        
        {/* Top Header */}
        <div className="flex items-center justify-between p-4 sticky top-0 bg-surface/90 backdrop-blur-md z-30 border-b border-border-faint">
          <button 
            onClick={onBack} 
            className="w-10 h-10 -ml-2 text-content-muted flex items-center justify-center hover:text-content transition-colors rounded-full active:scale-95"
          >
             <ChevronLeft className="w-6 h-6" strokeWidth={1.5} />
          </button>
          
          <div className="flex flex-col items-center">
             <span className="text-[15px] font-extrabold text-content tracking-tight">Talep detayı</span>
             <div className="flex items-center gap-1.5 mt-0.5">
               <div className="w-1.5 h-1.5 rounded-full bg-[#10B981] animate-pulse" />
               <span className="text-[9px] text-content-muted font-bold tracking-tight">Hizmet veren modu</span>
             </div>
          </div>
          
          <div className="w-10 h-10" /> {/* Spacer */}
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto pb-36">
          
          {/* 1. Üst Talep Özeti */}
          <div className="px-5 pt-6 pb-5 border-b border-border-faint">
            <div className="flex items-center gap-2 mb-3">
              <span className="inline-flex items-center px-1.5 py-0.5 rounded text-[9px] font-bold bg-surface-muted text-content-muted border border-border-subtle">
                {data.category || "Hizmet Talebi"}
              </span>
              <span className="text-[9px] text-content-muted font-medium flex items-center gap-1">
                <Clock className="w-3 h-3" /> {data.createdAt || "Yeni"}
              </span>
            </div>
            
            <h2 className="text-[14px] sm:text-[16px] font-extrabold text-content mb-3 leading-tight">
              {data.title}
            </h2>

            <div className="flex items-center gap-2 text-[10px]">
              <span className="inline-flex items-center px-2 py-1 bg-[#F97316]/5 border border-[#F97316]/20 rounded-full font-bold text-[#F97316] gap-1">
                {wMode === "online" ? <Globe className="w-3 h-3" strokeWidth={2}/> : <MapPin className="w-3 h-3" strokeWidth={2}/>}
                {wMode === "online" ? "Online" : wMode === "onsite" ? "Yerinde" : "Hibrit"}
              </span>
              {data.status && (
                <span className="inline-flex items-center px-2 py-1 bg-surface-muted border border-border-subtle rounded-full font-bold text-content-muted">
                  {data.status}
                </span>
              )}
            </div>
          </div>

          {/* 2. Müşteri Özeti */}
          <div className="px-5 py-4 border-b border-border-faint flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-surface-muted border border-border-subtle flex items-center justify-center text-[#F97316] font-bold text-[14px]">
                {cName.charAt(0)}
              </div>
              <div className="flex flex-col">
                 <span className="text-[12px] font-bold text-content">{cName}</span>
                 {(data.verifiedCustomer !== false) && (
                   <span className="text-[9px] font-medium text-content-muted flex items-center gap-1 mt-0.5">
                     <ShieldCheck className="w-3 h-3 text-[#10B981]" strokeWidth={2.5}/> Telefon doğrulandı
                   </span>
                 )}
              </div>
            </div>
            <button 
              onClick={() => console.log('Müşteri profili')}
              className="text-[10px] font-bold text-[#F97316] hover:text-[#EA580C] underline underline-offset-2"
            >
              Profili gör
            </button>
          </div>

          {/* 3. İhtiyaç (Description) */}
          <div className="px-5 py-5 border-b border-border-faint">
            <h3 className="text-[12px] font-bold text-content tracking-tight mb-2">İhtiyaç</h3>
            <p className="text-[10px] text-content-muted font-medium leading-relaxed">
              {data.description}
            </p>
          </div>

          {/* 4. Talep detayları */}
          <div className="px-5 py-5 border-b border-border-faint">
            <h3 className="text-[12px] font-bold text-content tracking-tight mb-3">Detaylar</h3>
            <div className="flex flex-col gap-3">
              <div className="flex justify-between items-center text-[10px]">
                <span className="text-content-muted font-medium">Bütçe</span>
                <span className="text-content font-bold">{data.budget || "Teklif bekliyor"}</span>
              </div>
              <div className="flex justify-between items-center text-[10px]">
                <span className="text-content-muted font-medium">Zaman</span>
                <span className="text-content font-bold">{data.time || "Belirtilmedi"}</span>
              </div>
              <div className="flex justify-between items-center text-[10px]">
                <span className="text-content-muted font-medium">Çalışma Şekli</span>
                <span className="text-content font-bold">{wMode === "online" ? "Uzaktan çalışma" : "Mekan/Yerinde"}</span>
              </div>
              {(data.distance || wMode === "onsite") && (
                <div className="flex justify-between items-center text-[10px]">
                  <span className="text-content-muted font-medium">Hizmet Alanı</span>
                  <span className="text-content font-bold">{data.distance ? `${data.distance} dinamik çevre` : "Belirtilmedi"}</span>
                </div>
              )}
              <div className="flex justify-between items-center text-[10px]">
                <span className="text-content-muted font-medium">Ekler</span>
                <span className="text-content font-bold">{data.attachments?.length ? `${data.attachments.length} dosya/bağlantı` : "Ek yok"}</span>
              </div>
            </div>
          </div>

          {/* 5. Ekler */}
          <div className="px-5 py-5 border-b border-border-faint">
            <h3 className="text-[12px] font-bold text-content tracking-tight mb-3">Ekler ve örnekler</h3>
            {data.attachments && data.attachments.length > 0 ? (
              <div className="flex flex-col gap-2">
                {data.attachments.map((att, i) => (
                  <div key={i} className="flex items-center gap-3 p-3 bg-surface-muted border border-border-subtle rounded-xl">
                     <FileText className="w-4 h-4 text-content-muted" />
                     <span className="text-[10px] font-bold text-content">{att}</span>
                  </div>
                ))}
              </div>
            ) : (
              <span className="text-[10px] text-content-muted font-medium block">
                Bu talep için ek dosya paylaşılmamış.
              </span>
            )}
          </div>

          {/* 6. Yanıtlamadan Önce (Not) */}
          <div className="px-5 py-5">
            <div className="p-4 bg-[#10B981]/5 border border-[#10B981]/20 rounded-xl">
              <h4 className="text-[10px] font-bold text-[#10B981] mb-1.5">Yanıtlamadan önce</h4>
              <p className="text-[9px] text-content-muted font-medium leading-relaxed">
                Teklif verirken fiyat, süre ve çalışma şeklini net yazman müşterinin daha hızlı karar vermesine yardımcı olur.
              </p>
            </div>
          </div>

        </div>

        {/* 7. Alt Aksiyon Alanı */}
        <div className="absolute bottom-0 left-0 right-0 bg-surface/90 backdrop-blur-md px-6 py-4 pb-safe border-t border-border-faint z-40">
          <div className="flex flex-col gap-3">
            <div className="flex gap-2">
              <button 
                onClick={() => setShowDeclineConfirm(true)}
                className="flex-[0.6] py-3.5 rounded-xl font-bold text-[12px] border border-border-strong text-content hover:bg-surface-muted transition-colors active:scale-[0.98] flex items-center justify-center gap-1.5"
              >
                <XCircle className="w-4 h-4" />
                Uygun değil
              </button>
              <button 
                onClick={() => setIsOfferSheetOpen(true)}
                className="flex-1 py-3.5 rounded-xl font-extrabold text-[12px] md:text-[13px] bg-[#F97316] text-white hover:bg-[#EA580C] shadow-sm transition-transform active:scale-[0.98] flex items-center justify-center gap-1.5"
              >
                <Briefcase className="w-4 h-4" strokeWidth={2}/>
                Teklif ver
              </button>
            </div>
            <button 
              onClick={() => setFeedbackMessage('Talep daha sonra bakmak için bırakıldı.')}
              className="w-full py-2 rounded-xl font-bold text-[10px] text-content-muted hover:text-content transition-colors flex items-center justify-center"
            >
              Daha sonra bak
            </button>
          </div>
        </div>

        {/* ProviderCreateOfferSheet */}
        <ProviderCreateOfferSheet 
          open={isOfferSheetOpen}
          request={data}
          onClose={() => setIsOfferSheetOpen(false)}
          onSubmitted={(offer) => console.log('Offer submitted', offer)}
        />

        {/* Modals & Toasts */}
        <AnimatePresence>
          {showDeclineConfirm && (
            <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
              <motion.div 
                initial={{ opacity: 0 }} 
                animate={{ opacity: 1 }} 
                exit={{ opacity: 0 }} 
                onClick={() => setShowDeclineConfirm(false)}
                className="absolute inset-0 bg-content/20 backdrop-blur-sm"
              />
              <motion.div 
                initial={{ opacity: 0, scale: 0.95 }} 
                animate={{ opacity: 1, scale: 1 }} 
                exit={{ opacity: 0, scale: 0.95 }}
                transition={{ type: "spring", damping: 25, stiffness: 300 }}
                className="w-full max-w-[320px] bg-surface rounded-2xl shadow-xl border border-border-faint p-5 relative z-10 flex flex-col items-center text-center mx-auto"
              >
                <div className="w-12 h-12 rounded-full bg-red-50 flex items-center justify-center mb-3">
                  <Info className="w-6 h-6 text-red-500" strokeWidth={2} />
                </div>
                <h3 className="text-[14px] font-extrabold text-content tracking-tight mb-2">
                  Bu talebi gizle?
                </h3>
                <p className="text-[10px] text-content-muted font-medium mb-6 leading-relaxed">
                  Bu talep sana uygun değil olarak işaretlenecek. Daha sonra talepler listenden kaldırılabilir.
                </p>
                <div className="flex w-full gap-2">
                  <button 
                    onClick={() => setShowDeclineConfirm(false)}
                    className="flex-1 py-3 bg-surface border border-border-strong text-content rounded-xl font-bold text-[12px] active:scale-95 transition-transform"
                  >
                    Vazgeç
                  </button>
                  <button 
                    onClick={handleDeclineConfirm}
                    className="flex-1 py-3 bg-red-500 text-white rounded-xl font-bold text-[12px] active:scale-95 transition-transform"
                  >
                    Uygun değil
                  </button>
                </div>
              </motion.div>
            </div>
          )}

          {feedbackMessage && (
            <div className="fixed top-20 left-0 right-0 z-50 flex justify-center px-4 pointer-events-none">
              <motion.div 
                initial={{ opacity: 0, y: -20 }} 
                animate={{ opacity: 1, y: 0 }} 
                exit={{ opacity: 0, y: -20, filter: "blur(4px)" }}
                className="bg-surface-muted border border-border-subtle shadow-sm px-4 py-2.5 rounded-full flex items-center gap-2 max-w-sm"
              >
                <CheckCircle2 className="w-4 h-4 text-[#10B981]" strokeWidth={2.5}/>
                <span className="text-[11px] font-bold text-content tracking-tight">{feedbackMessage}</span>
              </motion.div>
            </div>
          )}
        </AnimatePresence>

      </div>
    </div>
  );
}

export default function ProviderRequestDetailScreen(props: ProviderRequestDetailScreenProps) {
  return (
    <ErrorBoundary onBack={props.onBack}>
      <ProviderRequestDetailScreenContent {...props} />
    </ErrorBoundary>
  );
}
