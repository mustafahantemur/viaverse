import { useState, useEffect } from "react";
import { motion, AnimatePresence } from "motion/react";
import { X, Briefcase, CheckCircle2 } from "lucide-react";

interface ProviderCreateOfferSheetProps {
  open: boolean;
  request: any;
  onClose: () => void;
  onSubmitted?: (offer: any) => void;
}

export default function ProviderCreateOfferSheet({ open, request, onClose, onSubmitted }: ProviderCreateOfferSheetProps) {
  const [price, setPrice] = useState("");
  const [isPriceNegotiable, setIsPriceNegotiable] = useState(false);
  const [duration, setDuration] = useState("");
  const [workMode, setWorkMode] = useState("");
  const [message, setMessage] = useState("");
  const [isSuccess, setIsSuccess] = useState(false);

  // Normalize request data safely
  const normalizedRequest = {
    title: request?.title || "Logo ve sosyal medya tasarımı",
    category: request?.category || "Yaratıcı İşler & Medya",
    customerName: request?.customerName || request?.customer || "Elif K.",
    workMode: request?.workMode || request?.mode || "Online",
    time: request?.time || "Bugün",
    description: request?.description || request?.desc || "Yeni markam için logo ve Instagram gönderi şablonları arıyorum."
  };

  useEffect(() => {
    if (open) {
      setIsSuccess(false);
      setPrice("");
      setIsPriceNegotiable(false);
      setDuration("");
      setMessage("");
      
      const reqMode = normalizedRequest.workMode.toLowerCase();
      if (reqMode === "online") setWorkMode("Online");
      else if (reqMode === "onsite" || reqMode === "yerinde") setWorkMode("Yerinde");
      else setWorkMode("Hibrit");
    }
  }, [open, normalizedRequest.workMode]);

  const handleSubmit = () => {
    if (!open) return;
    setIsSuccess(true);
    if (onSubmitted) {
      onSubmitted({ price, isPriceNegotiable, duration, workMode, message });
    }
  };

  const handleClose = () => {
    setIsSuccess(false);
    onClose();
  };

  const isValid = isPriceNegotiable || price.trim() !== "";

  if (!open) return null;

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-[100] flex items-end justify-center sm:items-center p-0 sm:p-4">
        <motion.div 
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          onClick={handleClose}
          className="absolute inset-0 bg-content/20 backdrop-blur-sm"
        />
        <motion.div 
          initial={{ opacity: 0, y: "100%" }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: "100%" }}
          transition={{ type: "spring", damping: 25, stiffness: 300 }}
          className="w-full max-w-md bg-surface sm:rounded-2xl rounded-t-3xl shadow-xl border border-border-faint relative z-10 flex flex-col max-h-[85vh]"
        >
          {/* Drag Handle & Close */}
          <div className="flex justify-center pt-3 pb-2 w-full absolute top-0 sm:hidden">
             <div className="w-10 h-1 rounded-full bg-border-strong" />
          </div>
          
          {isSuccess ? (
            <div className="p-8 flex flex-col items-center text-center pb-safe">
              <div className="w-16 h-16 rounded-full bg-[#10B981]/10 flex items-center justify-center mb-4">
                <CheckCircle2 className="w-8 h-8 text-[#10B981]" strokeWidth={2.5} />
              </div>
              <h3 className="text-[16px] font-extrabold text-content tracking-tight mb-2">
                Teklifin gönderildi
              </h3>
              <p className="text-[11px] text-content-muted font-medium mb-8 leading-relaxed">
                Müşteri teklifini incelediğinde sonucu tekliflerinde görebileceksin. Kabul edilirse mesajlaşmaya geçebilirsiniz.
              </p>
              <button 
                type="button"
                onClick={handleClose}
                className="w-full py-3.5 bg-[#F97316] text-white rounded-xl font-bold text-[13px] tracking-tight active:scale-95 transition-transform"
              >
                Tamam
              </button>
            </div>
          ) : (
            <>
              <div className="flex items-center justify-between p-4 sm:pt-4 pt-6 border-b border-border-faint shrink-0">
                <div className="flex flex-col">
                  <h3 className="text-[14px] font-extrabold text-content tracking-tight mb-0.5">Teklif ver</h3>
                  <span className="text-[10px] text-content-muted font-medium">Fiyat, süre ve kısa mesajını yazarak müşteriye teklif gönder.</span>
                </div>
                <button 
                  type="button"
                  onClick={handleClose}
                  className="w-8 h-8 flex items-center justify-center rounded-full bg-surface-muted text-content-muted hover:text-content active:scale-95 transition-all"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>

              <div className="flex-1 overflow-y-auto p-5 pb-safe">
                {/* 2. Talep Mini Özeti */}
                <div className="p-3 bg-surface-muted border border-border-subtle rounded-xl mb-6">
                   <div className="flex justify-between items-start mb-1.5">
                      <h4 className="text-[12px] font-extrabold text-content leading-tight line-clamp-1">{normalizedRequest.title}</h4>
                      <span className="text-[9px] font-bold text-content-muted shrink-0">{normalizedRequest.time}</span>
                   </div>
                   <div className="flex items-center gap-1.5 mb-2 text-[9px] font-bold">
                      <span className="text-[#F97316]">{normalizedRequest.customerName}</span>
                      <span className="w-1 h-1 rounded-full bg-border-strong" />
                      <span className="text-content-muted">{normalizedRequest.workMode}</span>
                      <span className="w-1 h-1 rounded-full bg-border-strong" />
                      <span className="text-content-muted">{normalizedRequest.category}</span>
                   </div>
                   <p className="text-[10px] text-content-muted leading-relaxed line-clamp-2">
                     {normalizedRequest.description}
                   </p>
                </div>

                <form className="flex flex-col gap-5">
                  {/* 3. Teklif tutarı */}
                  <div className="flex flex-col gap-2">
                    <label className="text-[12px] font-bold text-content tracking-tight">Teklif tutarı</label>
                    <div className="relative">
                      <input 
                        type="number"
                        placeholder="Örn: 3500"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        disabled={isPriceNegotiable}
                        className="w-full h-11 bg-surface-muted border border-border-subtle rounded-xl px-4 text-[13px] text-content placeholder-content-muted/50 focus:outline-none focus:border-[#F97316]/50 transition-colors disabled:opacity-50"
                      />
                      <span className="absolute right-4 top-1/2 -translate-y-1/2 text-[13px] font-bold text-content-muted">₺</span>
                    </div>
                    <label className="flex items-center gap-2 mt-1 cursor-pointer w-max">
                       <input 
                         type="checkbox" 
                         checked={isPriceNegotiable}
                         onChange={(e) => {
                           setIsPriceNegotiable(e.target.checked);
                           if (e.target.checked) setPrice("");
                         }}
                         className="w-4 h-4 rounded border-border-strong text-[#F97316] focus:ring-[#F97316]/20 bg-surface-muted"
                       />
                       <span className="text-[10px] font-medium text-content-muted">Keşif / görüşme sonrası netleşir</span>
                    </label>
                  </div>

                  {/* 4. Tahmini süre */}
                  <div className="flex flex-col gap-2">
                    <label className="text-[12px] font-bold text-content tracking-tight">Tahmini süre</label>
                    <div className="flex flex-wrap gap-2 mb-1">
                      {["Bugün", "Yarın", "2-3 gün", "1 hafta", "Müşteriyle netleşir"].map((d) => (
                        <button
                          key={d}
                          type="button"
                          onClick={() => setDuration(d)}
                          className={`px-3 py-1.5 rounded-full text-[10px] font-bold border transition-colors ${
                            duration === d 
                            ? "bg-[#F97316]/10 border-[#F97316]/30 text-[#F97316]" 
                            : "bg-surface border-border-strong text-content-muted hover:border-border-heavy"
                          }`}
                        >
                          {d}
                        </button>
                      ))}
                    </div>
                    <input 
                      type="text"
                      placeholder="Örn: 3 gün içinde"
                      value={duration}
                      onChange={(e) => setDuration(e.target.value)}
                      className="w-full h-10 bg-surface-muted border border-border-subtle rounded-xl px-4 text-[12px] text-content placeholder-content-muted/50 focus:outline-none focus:border-[#F97316]/50 transition-colors"
                    />
                  </div>

                  {/* 5. Çalışma şekli */}
                  <div className="flex flex-col gap-2">
                    <label className="text-[12px] font-bold text-content tracking-tight">Çalışma şekli</label>
                    <div className="flex gap-2">
                      {["Online", "Yerinde", "Hibrit"].map((m) => (
                        <button
                          key={m}
                          type="button"
                          onClick={() => setWorkMode(m)}
                          className={`px-4 py-2 rounded-lg text-[10px] font-bold border transition-colors flex-1 ${
                            workMode === m 
                            ? "bg-[#F97316] border-[#F97316] text-white shadow-sm shadow-[#F97316]/20" 
                            : "bg-surface border-border-strong text-content text-center hover:bg-surface-muted"
                          }`}
                        >
                          {m}
                        </button>
                      ))}
                    </div>
                  </div>

                  {/* 6. Mesaj */}
                  <div className="flex flex-col gap-2">
                    <label className="text-[12px] font-bold text-content tracking-tight">Müşteriye mesajın</label>
                    <textarea 
                      placeholder="Merhaba, bu iş için uygun olduğumu düşünüyorum. Belirttiğiniz kapsam için ... sürede tamamlayabilirim."
                      value={message}
                      onChange={(e) => setMessage(e.target.value)}
                      className="w-full h-24 bg-surface-muted border border-border-subtle rounded-xl p-3 text-[12px] text-content placeholder-content-muted/50 focus:outline-none focus:border-[#F97316]/50 transition-colors resize-none"
                    />
                  </div>
                </form>
              </div>

              {/* Footer */}
              <div className="p-4 border-t border-border-faint bg-surface shrink-0 pb-safe">
                 <p className="text-[9px] text-content-muted font-medium mb-3 text-center">
                   Teklifin kabul edilirse müşteriyle mesajlaşmaya geçebilirsin.
                 </p>
                 <div className="flex gap-2">
                    <button 
                      type="button"
                      onClick={handleClose}
                      className="flex-[0.6] py-3.5 bg-surface border border-border-strong text-content rounded-xl font-bold text-[12px] active:scale-95 transition-transform"
                    >
                      Vazgeç
                    </button>
                    <button 
                      type="button"
                      onClick={handleSubmit}
                      disabled={!isValid}
                      className="flex-1 py-3.5 bg-[#F97316] text-white rounded-xl font-extrabold text-[12px] shadow-sm flex items-center justify-center gap-1.5 active:scale-95 transition-transform disabled:opacity-50 disabled:active:scale-100"
                    >
                      <Briefcase className="w-4 h-4" strokeWidth={2}/>
                      Teklifi gönder
                    </button>
                 </div>
              </div>
            </>
          )}

        </motion.div>
      </div>
    </AnimatePresence>
  );
}
