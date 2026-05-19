import { motion, AnimatePresence } from "motion/react";
import { useState } from "react";
import { Compass, Briefcase, ChevronRight, CheckCircle2, Navigation, MessageSquare, Info, X, MapPin, Calendar, Star } from "lucide-react";
import BottomNavigation from "./BottomNavigation";
import viaverseLogo from "../icons/viaverse_v_orange_green.svg";

export default function JobsScreen({ onHome, onChat, onProfile, onOpenPublicProfile }: { onHome: () => void, onChat: () => void, onProfile: () => void, onOpenPublicProfile: (user: any) => void }) {
  const [activeTab, setActiveTab] = useState<"aktif" | "gecmis" | "tekliflerim">("aktif");
  const [selectedJob, setSelectedJob] = useState<any>(null);
  const [viewingOffers, setViewingOffers] = useState(false);

  // Added a bids array for Tekliflerim tab
  const [bids] = useState([
    {
      id: 101,
      title: "Köpek Gezdirme",
      status: "pending", // pending, accepted, rejected
      desc: "Yarın saat 14:00'da ufak ırk köpeğimi 45 dakika gezdirecek biri lazım.",
      location: "Ataşehir / 1.2 km",
      time: "Yarın, 14:00",
      bidPrice: 800,
      createdAt: "10 dk önce"
    }
  ]);

  const [jobs] = useState([
    {
      id: 1,
      title: "Ev Temizliği",
      status: "offer_received", // "pending" | "offer_received" | "completed"
      desc: "3+1 evim için detaylı temizlik hizmeti istiyorum.",
      location: "İstanbul, Kadıköy",
      time: "20 Mayıs, 14:00",
      offersCount: 2,
      createdAt: "2 saat önce"
    },
    {
      id: 2,
      title: "Kombi Bakımı",
      status: "pending",
      desc: "Yıllık periyodik kombi bakımı ve petek temizliği",
      location: "İstanbul, Üsküdar",
      time: "Belli bir zaman (üç hafta içinde)",
      offersCount: 0,
      createdAt: "1 gün önce"
    }
  ]);

  return (
    <div className="h-[100dvh] w-full bg-surface flex justify-center font-sans text-content overflow-hidden">
      <div className="w-full max-w-md bg-base h-full relative flex flex-col">
        {/* Header */}
        <header className="bg-surface pt-4 pb-2 px-4 shrink-0 shadow-sm z-30 relative">
          <div className="flex items-center justify-between mb-4">
            <h1 className="text-[16px] font-extrabold text-content tracking-tight">İşlerim</h1>
          </div>

          <div className="flex gap-4 border-b border-border-subtle overflow-x-auto scrollbar-hide">
            <button 
              onClick={() => setActiveTab("aktif")}
              className={`pb-2.5 text-[12px] font-bold transition-all relative shrink-0 ${activeTab === 'aktif' ? 'text-content' : 'text-content-muted'}`}
            >
              Aktif Taleplerim
              {activeTab === 'aktif' && (
                <motion.div layoutId="jobsTab" className="absolute bottom-0 left-0 right-0 h-[3px] bg-[#F97316] rounded-t-full" />
              )}
            </button>
            <button 
              onClick={() => setActiveTab("tekliflerim")}
              className={`pb-2.5 text-[12px] font-bold transition-all relative shrink-0 ${activeTab === 'tekliflerim' ? 'text-content' : 'text-content-muted'}`}
            >
              Verdiğim Teklifler
              {activeTab === 'tekliflerim' && (
                <motion.div layoutId="jobsTab" className="absolute bottom-0 left-0 right-0 h-[3px] bg-[#F97316] rounded-t-full" />
              )}
            </button>
            <button 
              onClick={() => setActiveTab("gecmis")}
              className={`pb-2.5 text-[12px] font-bold transition-all relative shrink-0 ${activeTab === 'gecmis' ? 'text-content' : 'text-content-muted'}`}
            >
              Geçmiş İşlemler
              {activeTab === 'gecmis' && (
                <motion.div layoutId="jobsTab" className="absolute bottom-0 left-0 right-0 h-[3px] bg-[#F97316] rounded-t-full" />
              )}
            </button>
          </div>
        </header>

        {/* Content */}
        <main className="flex-1 overflow-y-auto pt-2 pb-28 px-4 space-y-3">
          <AnimatePresence mode="popLayout">
            {activeTab === 'tekliflerim' ? (
              <>
                {bids.map((bid) => (
                  <motion.div 
                    key={bid.id}
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, scale: 0.95 }}
                    className="bg-surface rounded-[16px] p-3 shadow-[0_2px_12px_rgb(0,0,0,0.04)] border border-border-subtle flex flex-col relative overflow-hidden active:scale-[0.98] transition-transform"
                  >
                    <div className="absolute top-0 left-0 bottom-0 w-1 bg-[#F97316]" />
                    <div className="flex justify-between items-start mb-1.5">
                      <h3 className="text-[12px] font-extrabold text-[#F97316] tracking-tight ml-1">Verdiğin Teklif: {bid.bidPrice} ₺</h3>
                      <span className="text-[9px] text-content-muted font-medium bg-surface-muted px-2 py-0.5 rounded-full">{bid.createdAt}</span>
                    </div>
                    <h4 className="text-[10px] font-bold text-content ml-1 mb-1">{bid.title}</h4>
                    <p className="text-[10px] text-content-muted leading-relaxed font-medium mb-3 line-clamp-2 ml-1">{bid.desc}</p>
                    <div className="flex justify-between items-center mt-2 pt-2 border-t border-gray-50">
                      <div className="flex items-center gap-2 text-[10px] text-content-muted font-medium ml-1">
                        <MapPin className="w-3 h-3" /> {bid.location}
                      </div>
                      <div className="flex items-center gap-1.5 py-1 px-2.5 bg-yellow-500/10 text-yellow-600 rounded-lg border border-yellow-500/20">
                        <Compass className="w-3 h-3 animate-spin duration-[3000ms]" />
                        <span className="font-bold text-[10px]">Değerlendiriliyor</span>
                      </div>
                    </div>
                  </motion.div>
                ))}
                
                {bids.length === 0 && (
                  <div className="text-center py-20 px-4">
                    <p className="text-[12px] font-bold text-content mb-1">Henüz teklif vermediniz</p>
                    <p className="text-[12px] text-content-muted mb-6">Yakınınızdaki iş fırsatlarını keşfedin ve ilk teklifinizi verin.</p>
                    <button 
                      onClick={onHome}
                      className="px-6 py-2.5 bg-[#F97316] text-white rounded-full font-bold text-[12px] shadow-lg shadow-[#F97316]/20 active:scale-95 transition-all"
                    >
                      İşleri Keşfet
                    </button>
                  </div>
                )}
              </>
            ) : (
              <>
                {jobs.filter(j => activeTab === 'aktif' ? j.status !== 'completed' : j.status === 'completed').map((job) => (
              <motion.div 
                key={job.id}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, scale: 0.95 }}
                onClick={() => setSelectedJob(job)}
                className={`bg-surface rounded-[16px] p-3 shadow-[0_2px_12px_rgb(0,0,0,0.04)] border border-border-subtle flex flex-col relative overflow-hidden cursor-pointer active:scale-[0.98] transition-transform`}
              >
                {job.status === "offer_received" && (
                  <div className="absolute top-0 left-0 bottom-0 w-1 bg-[#10B981]" />
                )}
                
                <div className="flex justify-between items-start mb-1.5">
                  <h3 className="text-[12px] font-extrabold text-content tracking-tight ml-1">{job.title}</h3>
                  <span className="text-[9px] text-content-muted font-medium bg-surface-muted px-2 py-0.5 rounded-full">{job.createdAt}</span>
                </div>
                
                <p className="text-[10px] text-content-muted leading-relaxed font-medium mb-3 line-clamp-2 ml-1">{job.desc}</p>
                
                <div className="flex items-center gap-3 text-[10px] text-content-muted font-medium mb-2 ml-1">
                  <div className="flex items-center gap-1"><Navigation className="w-3 h-3" /> {job.location}</div>
                  <div className="flex items-center gap-1"><Info className="w-3 h-3" /> {job.time}</div>
                </div>

                {job.status === "offer_received" ? (
                  <div className="mt-1 pt-1 flex justify-end">
                    <div className="flex items-center gap-1.5 py-1 px-2.5 bg-[#10B981]/10 text-[#10B981] rounded-lg border border-[#10B981]/20">
                      <div className="w-1.5 h-1.5 bg-[#10B981] rounded-full animate-pulse" />
                      <span className="font-bold text-[10px]">{job.offersCount} Gelen Teklif</span>
                    </div>
                  </div>
                ) : (
                  <div className="mt-2 pt-2 border-t border-gray-50 flex items-center gap-1 text-content-muted font-bold text-[10px]">
                     <Compass className="w-3 h-3" /> Teklif bekleniyor...
                  </div>
                )}
              </motion.div>
            ))}
            
            {jobs.filter(j => activeTab === 'aktif' ? j.status !== 'completed' : j.status === 'completed').length === 0 && (
              <div className="text-center py-20 px-4">
                <div className="w-20 h-20 bg-surface-muted/50 rounded-full flex items-center justify-center mx-auto mb-6">
                   <motion.div
                    animate={{ rotateY: 360 }}
                    transition={{ duration: 8, repeat: Infinity, ease: "linear" }}
                    style={{ transformStyle: "preserve-3d" }}
                  >
                   <img src={viaverseLogo} alt="Viaverse" className="w-12 h-12 drop-shadow-md object-contain" />
                  </motion.div>
                </div>
                <p className="text-[12px] font-bold text-content mb-1">Henüz bir talebiniz yok</p>
                <p className="text-[12px] text-content-muted mb-6">İhtiyacınız olan hizmeti arayın ve hemen bir talep oluşturun.</p>
                <button 
                  onClick={onHome}
                  className="px-6 py-2.5 bg-[#F97316] text-white rounded-full font-bold text-[12px] shadow-lg shadow-[#F97316]/20 active:scale-95 transition-all"
                >
                  Talep Oluştur
                </button>
              </div>
            )}
            </>
            )}
          </AnimatePresence>
        </main>

        {/* Selected Job Details Sheet */}
        <AnimatePresence>
          {selectedJob && (
            <>
              <motion.div 
                initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
                onClick={() => { setSelectedJob(null); setViewingOffers(false); }}
                className="absolute inset-0 bg-black/40 z-40"
              />
              <motion.div 
                initial={{ y: "100%" }} animate={{ y: 0 }} exit={{ y: "100%" }}
                transition={{ type: "spring", damping: 25, stiffness: 200 }}
                className="absolute bottom-0 left-0 right-0 bg-surface rounded-t-[24px] shadow-2xl z-50 flex flex-col pb-safe max-h-[85vh] h-[85vh]"
              >
                <div className="flex justify-center pt-3 pb-2 shrink-0">
                  <div className="w-12 h-1.5 bg-border-muted rounded-full" />
                </div>
                <div className="px-4 pb-4 border-b border-border-subtle flex justify-between items-center shrink-0">
                  {viewingOffers ? (
                    <button onClick={() => setViewingOffers(false)} className="w-8 h-8 flex items-center justify-center rounded-full bg-surface-muted active:scale-95">
                      <ChevronRight className="w-4 h-4 text-content rotate-180" strokeWidth={2} />
                    </button>
                  ) : (
                     <div className="w-8 h-8" />
                  )}
                  <h2 className="text-[12px] font-extrabold tracking-tight">
                    {viewingOffers ? "Gelen Teklifler" : "İş Detayı"}
                  </h2>
                  <button onClick={() => { setSelectedJob(null); setViewingOffers(false); }} className="w-8 h-8 flex items-center justify-center rounded-full bg-surface-muted active:scale-95">
                    <X className="w-4 h-4 text-content-muted" strokeWidth={2} />
                  </button>
                </div>
                
                <div className="flex-1 overflow-y-auto p-4 flex flex-col">
                  {!viewingOffers ? (
                    <div className="space-y-4">
                       <div className="bg-surface-muted/30 p-4 rounded-[16px] border border-border-subtle">
                         <div className="text-[10px] font-bold text-[#F97316] uppercase tracking-wider mb-1">Kategori</div>
                         <h3 className="text-[12px] font-extrabold text-content">{selectedJob.title}</h3>
                       </div>

                       <div className="bg-surface-muted/30 p-4 rounded-[16px] border border-border-subtle space-y-3">
                         <div>
                           <div className="text-[10px] font-bold text-content-muted mb-1">Neye ihtiyacım var?</div>
                           <p className="text-[12px] text-content font-medium leading-relaxed">{selectedJob.desc}</p>
                         </div>
                         
                         <div className="pt-3 border-t border-border-subtle/50 grid grid-cols-2 gap-3">
                           <div>
                             <div className="text-[10px] font-bold text-content-muted mb-1 flex items-center gap-1"><MapPin className="w-3 h-3"/> Lokasyon</div>
                             <p className="text-[12px] text-content font-medium">{selectedJob.location}</p>
                           </div>
                           <div>
                             <div className="text-[10px] font-bold text-content-muted mb-1 flex items-center gap-1"><Calendar className="w-3 h-3"/> Zaman</div>
                             <p className="text-[12px] text-content font-medium">{selectedJob.time}</p>
                           </div>
                         </div>
                       </div>

                       <div className="bg-surface-muted/30 p-4 rounded-[16px] border border-border-subtle">
                         <div className="text-[10px] font-bold text-content-muted mb-3">İşin Durumu</div>
                         
                         {selectedJob.status === 'offer_received' && (
                           <div className="flex flex-col gap-3">
                             <div className="flex items-center gap-2 text-[#10B981]">
                               <CheckCircle2 className="w-5 h-5" />
                               <span className="text-[12px] font-bold">Teklifler Geldi</span>
                             </div>
                             <button
                               onClick={() => setViewingOffers(true)}
                               className="w-full py-3 bg-[#10B981] text-white rounded-xl font-bold text-[12px] shadow-md shadow-[#10B981]/20 active:opacity-80 transition-all flex items-center justify-center gap-2"
                             >
                               {selectedJob.offersCount} Teklifi Gör
                               <ChevronRight className="w-4 h-4" />
                             </button>
                           </div>
                         )}

                         {selectedJob.status === 'pending' && (
                           <div className="flex items-center gap-2 text-content-muted">
                             <Compass className="w-4 h-4" />
                             <span className="text-[12px] font-bold">Uzmanların teklifi bekleniyor...</span>
                           </div>
                         )}

                         {selectedJob.status === 'completed' && (
                           <div className="flex items-center gap-2 text-content-muted">
                             <CheckCircle2 className="w-4 h-4" />
                             <span className="text-[12px] font-bold">Tamamlandı</span>
                           </div>
                         )}
                       </div>
                    </div>
                  ) : (
                    <div className="space-y-4">
                      {/* Mock Offer 1 */}
                      <div className="bg-surface-muted/30 p-4 rounded-2xl border border-border-subtle flex flex-col">
                        <div className="flex justify-between items-start mb-3">
                          <div 
                             className="flex gap-3 items-center cursor-pointer active:opacity-70 transition-opacity"
                             onClick={() => {
                               setSelectedJob(null);
                               setViewingOffers(false);
                               onOpenPublicProfile({
                                  id: 1,
                                  name: 'Ahmet Usta',
                                  role: selectedJob.title + ' Uzmanı',
                                  avatar: 'https://i.pravatar.cc/150?img=11',
                                  rating: '4.9',
                                  reviewCount: '124',
                                  location: selectedJob.location,
                                  isUzman: true,
                                  badges: ['hızlı yanıt', 'popüler'],
                                  description: '15 yıllık deneyimimle profesyonel hizmet sunuyorum.'
                               });
                             }}
                          >
                            <div className="w-10 h-10 rounded-full bg-content text-surface flex justify-center items-center overflow-hidden">
                              <img src="https://i.pravatar.cc/150?img=11" alt="avatar" className="w-full h-full object-cover" />
                            </div>
                            <div>
                              <div className="font-bold text-[12px] flex items-center gap-1 text-content">
                                Ahmet Usta <ChevronRight className="w-3 h-3 text-content-muted" />
                              </div>
                              <div className="flex items-center gap-1 text-[10px] font-medium text-[#F97316]">
                                <Star className="w-3 h-3 fill-current" /> 4.9 (124)
                              </div>
                            </div>
                          </div>
                          <div className="font-extrabold text-[12px] text-[#10B981] bg-[#10B981]/10 px-2 py-1 rounded-lg">1.200 ₺</div>
                        </div>
                        
                        <p className="text-[10px] text-content-muted leading-relaxed font-medium mb-4">
                          Merhaba, işleminiz için gerekli tüm ekipmanlar tarafımızca sağlanacaktır. İşlemi belirtilen saatte başlayıp en kısa sürede bitirmeyi planlıyoruz.
                        </p>
                        
                        <button 
                          onClick={() => {
                            setSelectedJob(null);
                            setViewingOffers(false);
                            onChat();
                          }}
                          className="w-full py-2.5 bg-[#10B981] text-white rounded-xl font-bold text-[12px] shadow-sm active:scale-[0.98] transition-all flex items-center justify-center gap-2 mt-auto"
                        >
                          <MessageSquare className="w-4 h-4" /> Teklifi Kabul Et & Mesajlaş
                        </button>
                      </div>

                      {/* Mock Offer 2 */}
                      {selectedJob.offersCount > 1 && (
                        <div className="bg-surface-muted/30 p-4 rounded-2xl border border-border-subtle flex flex-col">
                          <div className="flex justify-between items-start mb-3">
                            <div 
                               className="flex gap-3 items-center cursor-pointer active:opacity-70 transition-opacity"
                               onClick={() => {
                                 setSelectedJob(null);
                                 setViewingOffers(false);
                                 onOpenPublicProfile({
                                    id: 2,
                                    name: 'Temizeller LTD',
                                    role: selectedJob.title + ' Uzmanı',
                                    avatar: 'https://i.pravatar.cc/150?img=32',
                                    rating: '4.7',
                                    reviewCount: '89',
                                    location: selectedJob.location,
                                    isUzman: true,
                                    badges: ['kurumsal'],
                                    description: 'Kurumsal ekibimizle profesyonel temizlik hizmeti.'
                                 });
                               }}
                            >
                              <div className="w-10 h-10 rounded-full bg-content text-surface flex justify-center items-center overflow-hidden">
                                <img src="https://i.pravatar.cc/150?img=32" alt="avatar" className="w-full h-full object-cover" />
                              </div>
                              <div>
                                <div className="font-bold text-[12px] flex items-center gap-1 text-content">
                                  Temizeller LTD <ChevronRight className="w-3 h-3 text-content-muted" />
                                </div>
                                <div className="flex items-center gap-1 text-[10px] font-medium text-[#F97316]">
                                  <Star className="w-3 h-3 fill-current" /> 4.7 (89)
                                </div>
                              </div>
                            </div>
                            <div className="font-extrabold text-[12px] text-[#10B981] bg-[#10B981]/10 px-2 py-1 rounded-lg">1.450 ₺</div>
                          </div>
                          
                          <p className="text-[10px] text-content-muted leading-relaxed font-medium mb-4">
                            Alanında uzman 3 kişilik ekibimizle hizmet vermekteyiz.
                          </p>
                          
                          <button 
                            onClick={() => {
                              setSelectedJob(null);
                              setViewingOffers(false);
                              onChat();
                            }}
                            className="w-full py-2.5 bg-[#10B981] text-white rounded-xl font-bold text-[12px] shadow-sm active:scale-[0.98] transition-all flex items-center justify-center gap-2 mt-auto"
                          >
                            <MessageSquare className="w-4 h-4" /> Teklifi Kabul Et & Mesajlaş
                          </button>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              </motion.div>
            </>
          )}
        </AnimatePresence>

        {/* Bottom Navigation */}
        <BottomNavigation 
          activeTab="jobs" 
          onTabChange={(tab) => {
            if (tab === 'home') onHome();
            else if (tab === 'messages') onChat();
            else if (tab === 'profile') onProfile();
          }} 
        />
      </div>
    </div>
  );
}
