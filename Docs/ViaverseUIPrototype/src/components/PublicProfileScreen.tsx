import { useState } from "react";
import { motion, AnimatePresence } from "motion/react";
import { ChevronLeft, MapPin, Star, Clock, ShieldCheck, MessageCircle, Phone, MoreHorizontal, AlertTriangle, Ban, Info, CheckCircle2, X, ArrowUpCircle, ArrowDownCircle, Briefcase, ChevronRight, Globe, Image as ImageIcon, Link as LinkIcon, User } from "lucide-react";

interface PublicProfileScreenProps {
  user: {
    id?: number;
    name: string;
    img: string;
    avatar?: string;
    distance?: string;
    joinedDate?: string;
    rating?: number;
    reviewsCount?: number;
    reviewCount?: number;
    posts?: any[];
    // New fields
    isVerified?: boolean;
    isUzman?: boolean;
    completedJobs?: number;
    aiTags?: string[];
    description?: string;
    about?: string;
    location?: string;
    locationMasked?: string;
    trustScore?: number; // 0-100
    badges?: string[];
    role?: string;
    providerType?: string;
    serviceTitle?: string;
    workMode?: string;
    serviceArea?: string;
    services?: string[];
    tags?: string[];
    portfolioItems?: any[];
    responseTime?: string;
  };
  onBack: () => void;
}

const ProfileAvatar = ({ src, name, size = "w-8 h-8" }: { src?: string, name: string, size?: string }) => {
  const initials = name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  
  if (!src || src === "") {
    return (
      <div className={`${size} rounded-full bg-[#14281c] flex items-center justify-center text-white text-[10px] font-bold border border-border-muted overflow-hidden shrink-0`}>
        {initials}
      </div>
    );
  }
  
  return (
    <div className={`${size} rounded-full border border-border-muted overflow-hidden shrink-0`}>
      <img src={src} alt={name} className="w-full h-full object-cover" />
    </div>
  );
};

const VerifiedBadge = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#F97316" strokeWidth="1.2" strokeLinecap="round" strokeLinejoin="round" className="shrink-0 bg-[#F97316]/5 rounded-full p-[1px]">
    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"></path>
  </svg>
);

export default function PublicProfileScreen({ user, onBack }: PublicProfileScreenProps) {
  const isUzman = user.isUzman === true;

  const [activeTab, setActiveTab] = useState<"posts" | "about" | "portfolio" | "reviews">(isUzman ? "portfolio" : "posts");
  const [showMoreAbout, setShowMoreAbout] = useState(false);
  const [showOptionsMenu, setShowOptionsMenu] = useState(false);
  const [showReportModal, setShowReportModal] = useState(false);
  const [showBlockModal, setShowBlockModal] = useState(false);
  const [reportReason, setReportReason] = useState("");
  const [customReason, setCustomReason] = useState("");
  const [reportSubmitted, setReportSubmitted] = useState(false);
  const [blockSubmitted, setBlockSubmitted] = useState(false);

  // Fallback defaults for missing props to show full experience
  const defaultPosts = [
    { id: 1, type: "yardım", title: "Yıldız Tornavida", desc: "Ufak bir montaj işim var. Sadece 1 saatliğine ödünç verebilecek komşum var mı?", time: "2 saat önce", upvotes: 12, comments: 4 },
    { id: 2, type: "duyuru", title: "Kayıp Cüzdan", desc: "Parkın orada cüzdan bulduk, içinde kimlik var. Sahibi mesaj atsın.", time: "1 gün önce", upvotes: 45, comments: 8 },
  ];

  const posts = user.posts && user.posts.length > 0 ? user.posts : defaultPosts;
  const isVerified = user.isVerified !== undefined ? user.isVerified : true;
  const completedJobs = user.completedJobs !== undefined ? user.completedJobs : 47;
  const aiTags = user.aiTags || ["Hızlı ödeme yapar", "İletişimi kibardır", "Dakiktir"];
  const about = user.about || "Merhaba, komşularımla yardımlaşmayı ve çevreme değer katmayı seven biriyim. Boş zamanlarımda becerilerimi paylaşmaktan mutluluk duyarım.";
  const locationMasked = "150m, Kadıköy/İstanbul"; 
  const trustScore = user.trustScore !== undefined ? user.trustScore : 94;

  const getDisplayName = (name: string) => {
    const parts = name.split(" ");
    if (parts.length > 1) {
      const firstName = parts[0];
      const lastInitial = parts[parts.length - 1].charAt(0).toUpperCase();
      return `${firstName} ${lastInitial}.`;
    }
    return name;
  };

  const displayName = getDisplayName(user.name);

  return (
    <div className="min-h-screen bg-base flex justify-center text-content font-sans">
      <div className="w-full max-w-[480px] bg-[#FAF9F6] min-h-screen relative shadow-sm flex flex-col pt-0 pb-safe">
        
        {/* Header Overlay */}
        <div className="absolute top-4 left-4 z-40">
          <button 
            onClick={onBack} 
            className="bg-white/80 backdrop-blur-md text-[#1C1C1C] p-2 rounded-full shadow-sm border border-[#E5E7EB] transition-all active:scale-95"
          >
            <ChevronLeft className="w-5 h-5" strokeWidth={2.5} />
          </button>
        </div>

        <div className="absolute top-4 right-4 z-40">
          <button 
            onClick={() => setShowOptionsMenu(!showOptionsMenu)} 
            className="bg-white/80 backdrop-blur-md text-[#1C1C1C] p-2 rounded-full shadow-sm border border-[#E5E7EB] transition-all active:scale-95"
          >
            <MoreHorizontal className="w-5 h-5" strokeWidth={2.5} />
          </button>
          
          {/* Options Dropdown */}
          <AnimatePresence>
            {showOptionsMenu && (
              <motion.div 
                key="options-menu"
                initial={{ opacity: 0, y: -10, scale: 0.95 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                exit={{ opacity: 0, y: -10, scale: 0.95 }}
                className="absolute right-0 top-full mt-2 w-56 bg-white rounded-2xl shadow-xl ring-1 ring-black/5 overflow-hidden z-50 py-1 border border-[#F3F4F6]"
              >
                <button 
                  className="w-full text-left px-5 py-3.5 text-[14px] text-[#4B5563] hover:bg-[#F9FAFB] flex items-center font-bold border-b border-[#F3F4F6] transition-colors"
                  onClick={() => { setShowReportModal(true); setShowOptionsMenu(false); }}
                >
                  <AlertTriangle className="w-4 h-4 mr-3 text-[#9CA3AF]" strokeWidth={2.5} /> Şikayet Et
                </button>
                <button 
                  className="w-full text-left px-5 py-3.5 text-[14px] text-[#EF4444] hover:bg-red-50 flex items-center font-bold transition-colors"
                  onClick={() => { setShowBlockModal(true); setShowOptionsMenu(false); }}
                >
                  <Ban className="w-4 h-4 mr-3" strokeWidth={2.5}/> Engelle
                </button>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        {!isUzman ? (
          <>
            {/* Profile Info Section */}
            <div className="px-5 pt-24 pb-6 bg-[#FAF9F6] flex flex-col items-center">
                <div className="relative">
                    <ProfileAvatar src={user.img} name={user.name} size="w-[100px] h-[100px]" />
                    {isVerified && (
                        <div className="absolute bottom-1 right-1 bg-white rounded-full p-1 shadow-sm border border-[#E5E7EB]">
                            <VerifiedBadge />
                        </div>
                    )}
                </div>
                
                <div className="mt-5 text-center">
                  <h2 className="text-[22px] font-extrabold text-[#111827] tracking-tight mb-1.5 flex items-center justify-center gap-2">
                    {displayName}
                  </h2>
                  <div className="flex items-center justify-center gap-1.5 text-[13px] font-bold text-[#6B7280]">
                     <MapPin className="w-3.5 h-3.5" strokeWidth={2.5} />
                     <span>{locationMasked}</span>
                  </div>
                  <p className="mt-4 max-w-[280px] text-[13px] font-medium text-[#4B5563] leading-relaxed">
                     "{about}"
                  </p>
                </div>

                {/* Stat Cards */}
                <div className="grid grid-cols-4 gap-2 w-full mt-6">
                    <div className="bg-white rounded-[16px] p-3 flex flex-col items-center justify-center border border-[#E5E7EB] shadow-sm">
                        <span className="text-[16px] font-extrabold text-[#111827] mb-0.5">12</span>
                        <span className="text-[10px] font-bold tracking-tight text-[#6B7280] text-center w-min leading-tight">Açtığı Paylaşım</span>
                    </div>
                    <div className="bg-white rounded-[16px] p-3 flex flex-col items-center justify-center border border-[#E5E7EB] shadow-sm">
                        <span className="text-[16px] font-extrabold text-[#111827] mb-0.5">{completedJobs}</span>
                        <span className="text-[10px] font-bold tracking-tight text-[#6B7280] text-center w-min leading-tight">Tamamlanan İş</span>
                    </div>
                    <div className="bg-white rounded-[16px] p-3 flex flex-col items-center justify-center border border-[#E5E7EB] shadow-sm">
                        <span className="text-[16px] font-extrabold text-[#111827] mb-0.5">8</span>
                        <span className="text-[10px] font-bold tracking-tight text-[#6B7280] text-center w-min leading-tight">Yardım Talebi</span>
                    </div>
                    <div className="bg-white rounded-[16px] p-3 flex flex-col items-center justify-center border border-[#E5E7EB] shadow-sm">
                        <span className="text-[16px] font-extrabold text-[#10B981] mb-0.5">%94</span>
                        <span className="text-[10px] font-bold tracking-tight text-[#6B7280] text-center w-min leading-tight">Yanıt Oranı</span>
                    </div>
                </div>

                {/* Action Buttons */}
                <div className="flex items-center gap-3 mt-6 w-full">
                    <button className="flex-1 py-3.5 bg-white border border-[#E5E7EB] text-[#111827] rounded-[16px] font-extrabold text-[14px] shadow-sm flex items-center justify-center gap-2 active:scale-[0.98] transition-transform">
                      <MessageCircle className="w-5 h-5" strokeWidth={2.5} />
                      Mesaj Gönder
                    </button>
                </div>
            </div>

            {/* REGULAR USER TABBED LAYOUT */}
            <div className="flex-1 bg-white rounded-t-[32px] shadow-[0_-4px_24px_rgba(0,0,0,0.04)] flex flex-col overflow-hidden">
            <div className="flex px-4 pt-2 border-b border-[#F3F4F6] sticky top-0 bg-white z-30">
                {[
                    { id: "posts", label: "Paylaşımları" },
                    { id: "trust", label: "Güvenlik" },
                    { id: "about", label: "Hakkında" }
                ].map(tab => (
                <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id as any)}
                    className={`flex-1 py-4 text-[13px] font-extrabold tracking-tight transition-all relative ${
                    activeTab === tab.id ? 'text-[#111827]' : 'text-[#9CA3AF] hover:text-[#6B7280]'
                    }`}
                >
                    {tab.label}
                    {activeTab === tab.id && (
                    <motion.div 
                        layoutId="activeTabProfile"
                        className="absolute bottom-0 left-6 right-6 h-[3px] bg-[#F97316] rounded-t-full" 
                    />
                    )}
                </button>
                ))}
            </div>

            <div className="flex-1 overflow-y-auto bg-white pb-24 text-[#111827]">
                <AnimatePresence mode="wait">
                {activeTab === "posts" && (
                    <motion.div 
                    key="posts"
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -10 }}
                    className="flex flex-col pt-2"
                    >
                    {posts.map((post: any, index: number) => (
                        <div key={`post-${post.id || index}`} className="bg-white pb-4 border-b border-[#F3F4F6] pt-4 px-5">
                        <div className="flex justify-between items-start mb-2">
                            <div className="flex items-center gap-2.5">
                                <ProfileAvatar src={user.img} name={user.name} size="w-[32px] h-[32px]" />
                                <div className="flex flex-col">
                                <div className="flex items-center gap-1.5 mb-1">
                                    <span className="text-[13px] font-extrabold text-[#111827] leading-none">{displayName}</span>
                                    {isVerified && <VerifiedBadge />}
                                </div>
                                <div className="text-[10px] text-[#9CA3AF] leading-none flex items-center gap-1 font-bold">
                                    <span>{post.time}</span>
                                    <span>•</span>
                                    <span>{locationMasked.split(',')[0]}</span>
                                </div>
                                </div>
                            </div>
                            <div className="flex items-center gap-1">
                                {post.type === 'iş' && (
                                <button className="bg-[#F97316] text-white px-3 py-1.5 rounded-full text-[10px] font-bold active:scale-95 transition-transform tracking-tight shadow-[#F97316]/30 shadow-sm">
                                    teklif ver
                                </button>
                                )}
                            </div>
                        </div>

                        <div className="px-1 pb-2 pt-1">
                            <div className="text-[13px] text-[#4B5563] leading-relaxed font-medium">
                            {post.title ? (
                                <div className="flex flex-col mb-1.5">
                                <span className="text-[#F97316] text-[10px] font-extrabold tracking-tight uppercase mb-0.5">{post.type}</span>
                                <span className="font-extrabold text-[14px] text-[#111827] tracking-tight">{post.title}</span>
                                </div>
                            ) : (
                                <span className="text-[#F97316] text-[10px] font-extrabold tracking-tight uppercase mb-1.5 block">{post.type}</span>
                            )}
                            <p className="text-[#4B5563]">{post.desc}</p>
                            </div>
                        </div>

                        <div className="px-1 pt-1 flex items-center gap-4 text-[#9CA3AF]">
                            <button className="flex items-center gap-1.5 py-1 hover:text-[#EF4444] transition-colors group">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" className="group-hover:text-[#EF4444] transition-colors"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
                                <span className="text-[12px] font-bold">{post.upvotes || 0}</span>
                            </button>
                            <button className="flex items-center gap-1.5 py-1 hover:text-[#3B82F6] transition-colors group">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" className="group-hover:text-[#3B82F6] transition-colors"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
                                <span className="text-[12px] font-bold">{post.comments || 0}</span>
                            </button>
                        </div>
                        </div>
                    ))}
                    </motion.div>
                )}

                {activeTab === "trust" && (
                    <motion.div 
                    key="trust"
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -10 }}
                    className="p-5 space-y-4"
                    >
                        <div className="bg-[#FAF9F6] border border-[#E5E7EB] rounded-[20px] p-5">
                           <h4 className="text-[13px] font-extrabold text-[#111827] mb-4">Doğrulama Durumu</h4>
                           <div className="space-y-4">
                               <div className="flex items-center gap-3">
                                   <div className="w-8 h-8 rounded-full bg-[#10B981]/10 flex items-center justify-center">
                                       <Phone className="w-4 h-4 text-[#10B981]" strokeWidth={2.5}/>
                                   </div>
                                   <div>
                                       <div className="text-[13px] font-extrabold text-[#111827]">Telefon numarası eklendi</div>
                                       <div className="text-[11px] font-bold text-[#6B7280]">Doğrulandı</div>
                                   </div>
                               </div>
                               <div className="flex items-center gap-3">
                                   <div className="w-8 h-8 rounded-full bg-[#10B981]/10 flex items-center justify-center">
                                       <CheckCircle2 className="w-4 h-4 text-[#10B981]" strokeWidth={2.5}/>
                                   </div>
                                   <div>
                                       <div className="text-[13px] font-extrabold text-[#111827]">E-posta adresi eklendi</div>
                                       <div className="text-[11px] font-bold text-[#6B7280]">Doğrulandı</div>
                                   </div>
                               </div>
                           </div>
                        </div>

                        <div className="bg-[#FAF9F6] border border-[#E5E7EB] rounded-[20px] p-5">
                           <h4 className="text-[13px] font-extrabold text-[#111827] mb-4">Topluluk Değerlendirmesi</h4>
                           <div className="flex items-center gap-4 mb-4">
                               <div className="w-14 h-14 bg-white rounded-full border border-[#E5E7EB] shadow-sm flex flex-col items-center justify-center">
                                   <span className="text-[16px] font-extrabold text-[#F97316] leading-none mb-0.5">4.9</span>
                                   <Star className="w-3 h-3 fill-[#F97316] text-[#F97316]"/>
                               </div>
                               <div>
                                   <div className="text-[13px] font-extrabold text-[#111827]">Mükemmel Profil</div>
                                   <div className="text-[11px] font-bold text-[#6B7280]">{user.reviewCount || "42"} değerlendirme</div>
                               </div>
                           </div>
                        </div>
                    </motion.div>
                )}

                {activeTab === "about" && (
                    <motion.div 
                    key="about"
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -10 }}
                    className="p-5 space-y-6"
                    >
                    <div>
                        <h3 className="text-[13px] font-extrabold text-[#111827] mb-2 tracking-tight">Hakkında</h3>
                        <p className="text-[13px] text-[#4B5563] leading-relaxed font-medium bg-[#FAF9F6] p-4 rounded-[20px] border border-[#E5E7EB]">{about}</p>
                    </div>
                    </motion.div>
                )}
                </AnimatePresence>
            </div>
            </div>
          </>
        ) : (
          <div className="flex-1 overflow-y-auto bg-[#FAF9F6] pb-[100px] flex flex-col relative text-[#111827]">
             
             {/* 1. Top Profile Area */}
             <div className="px-5 pt-20 pb-6 border-b border-[#E5E7EB]">
                <div className="flex items-start gap-4">
                  <div className="w-16 h-16 rounded-[20px] bg-white border border-[#E5E7EB] flex flex-col items-center justify-center shrink-0 overflow-hidden relative shadow-sm">
                    {user.img ? (
                        <img src={user.img} alt={user.name} className="w-full h-full object-cover" />
                    ) : (
                        <User className="w-6 h-6 text-[#9CA3AF]" />
                    )}
                    {isVerified && (
                        <div className="absolute bottom-1 right-1 bg-white rounded-full p-[2px] shadow-sm">
                            <VerifiedBadge />
                        </div>
                    )}
                  </div>
                  <div className="flex flex-col justify-center pt-1">
                    <h1 className="text-[18px] font-extrabold tracking-tight leading-none mb-1">
                      {user.name}
                    </h1>
                    <h2 className="text-[12px] font-bold text-[#6B7280] leading-snug mb-2">
                      {user.serviceTitle || user.role || "Hizmet Başlığı"}
                    </h2>
                    <div className="inline-flex items-center px-2 py-0.5 rounded-md bg-white border border-[#E5E7EB] shadow-[0_1px_2px_rgba(0,0,0,0.02)] max-w-max">
                      <span className="text-[9px] font-bold text-[#6B7280]">{user.providerType || "Bireysel hizmet veren"}</span>
                    </div>
                  </div>
                </div>

                <div className="flex flex-wrap items-center gap-x-4 gap-y-2 mt-5 mb-4 text-[10px] font-bold">
                    <div className="flex items-center gap-1.5">
                        <Star className="w-4 h-4 fill-[#F97316] text-[#F97316]" />
                        <span className="text-[12px] font-extrabold text-[#111827]">{user.rating || "4.9"}</span>
                        <span className="text-[10px] font-bold text-[#6B7280]">({user.reviewCount || "42"})</span>
                    </div>
                    <div className="w-1 h-1 rounded-full bg-[#D1D5DB]" />
                    <div className="flex items-center gap-1.5 text-[#6B7280]">
                        <CheckCircle2 className="w-3.5 h-3.5 text-[#10B981]" strokeWidth={2.5}/>
                        <span>{completedJobs} iş</span>
                    </div>
                </div>
                
                <p className="text-[10px] text-[#4B5563] leading-relaxed font-medium">
                  {user.description || about || "Ne sunduğunu, nasıl çalıştığını ve hangi işlerde iyi olduğunu anlatan kısa tanıtım yazısı burada görünecek."}
                </p>
             </div>

             {/* 2. Primary Actions Area */}
             <div className="px-5 py-5 border-b border-[#E5E7EB] flex gap-3">
                 <button className="flex-1 py-3 bg-[#F97316] hover:bg-[#EA580C] text-white rounded-xl font-extrabold text-[13px] tracking-tight shadow-sm transition-all active:scale-[0.98] flex items-center justify-center gap-2">
                     <Briefcase className="w-4 h-4" strokeWidth={2}/> Teklif Al
                 </button>
                 <button className="flex-1 py-3 bg-white hover:bg-gray-50 border border-[#E5E7EB] text-[#111827] rounded-xl font-bold text-[13px] tracking-tight shadow-sm transition-all active:scale-[0.98] flex items-center justify-center gap-2">
                     <MessageCircle className="w-4 h-4" strokeWidth={2.5} /> Mesaj Gönder
                 </button>
             </div>

             {/* 3. Services */}
             <div className="px-5 py-5 border-b border-[#E5E7EB]">
                <h3 className="text-[12px] font-bold tracking-tight mb-3">Hizmet Alanları</h3>
                <div className="flex flex-wrap gap-2">
                    {(user.services || ["Sosyal medya tasarımı", "Logo tasarımı", "Web tasarım"]).map((svc, i) => (
                        <span key={i} className="px-2.5 py-1.5 rounded-lg bg-white border border-[#E5E7EB] text-[10px] font-medium tracking-tight whitespace-nowrap shadow-[0_1px_2px_rgba(0,0,0,0.02)]">
                            {svc}
                        </span>
                    ))}
                </div>
             </div>

             {/* 4. Work Mode */}
             <div className="px-5 py-5 border-b border-[#E5E7EB]">
                <h3 className="text-[12px] font-bold tracking-tight mb-3">Çalışma Şekli</h3>
                <div className="flex flex-col gap-3">
                    {user.workMode !== "onsite" && (
                        <div className="flex items-start gap-3">
                            <Globe className="w-4 h-4 text-[#F97316] shrink-0 mt-0.5" strokeWidth={2} />
                            <div>
                                <span className="block text-[10px] font-bold mb-0.5">Online Hizmet Veriyor</span>
                                <span className="block text-[10px] text-[#6B7280]">Bölge bağımsız uzaktan çalışır</span>
                            </div>
                        </div>
                    )}
                    {user.workMode !== "online" && (
                        <div className="flex items-start gap-3">
                            <MapPin className="w-4 h-4 text-[#F97316] shrink-0 mt-0.5" strokeWidth={2} />
                            <div>
                                <span className="block text-[10px] font-bold mb-0.5">Yerinde Hizmet Veriyor</span>
                                <span className="block text-[10px] text-[#6B7280]">
                                    {user.location || "İstanbul"} • Dinamik çevrede {user.serviceArea || "10 km"}
                                </span>
                            </div>
                        </div>
                    )}
                </div>
             </div>

             {/* 6. Tags / Highlights */}
             <div className="px-5 py-5 border-b border-[#E5E7EB]">
                 <h3 className="text-[12px] font-bold tracking-tight mb-3">Öne Çıkanlar</h3>
                 <div className="flex flex-wrap gap-2">
                   {(user.tags || aiTags || ["Hızlı yanıt", "Online çalışır", "Portföy mevcut", "Tasarım"]).map((tag: string, i: number) => (
                     <span key={i} className="inline-flex items-center px-2 py-1 bg-[#F97316]/5 border border-[#F97316]/20 rounded-full text-[9px] font-bold text-[#F97316] tracking-tight">
                       {tag}
                     </span>
                   ))}
                 </div>
             </div>

             {/* 5. Portfolio */}
             <div className="px-5 py-5 border-b border-[#E5E7EB]">
                 <h3 className="text-[12px] font-bold tracking-tight mb-3">Portföy ve Örnek İşler</h3>
                 <div className="flex flex-col gap-2">
                    <div className="flex items-center gap-3 p-3 bg-white border border-[#E5E7EB] rounded-xl shadow-[0_1px_2px_rgba(0,0,0,0.02)]">
                      <ImageIcon className="w-4 h-4 text-[#9CA3AF]" />
                      <div className="flex flex-col">
                         <span className="text-[10px] font-bold">Görsel galerisi</span>
                         <span className="text-[9px] text-[#6B7280]">Çalışmalar veya önce/sonra fotoğrafları</span>
                      </div>
                    </div>
                    <div className="flex items-center gap-3 p-3 bg-white border border-[#E5E7EB] rounded-xl shadow-[0_1px_2px_rgba(0,0,0,0.02)]">
                      <LinkIcon className="w-4 h-4 text-[#9CA3AF]" />
                      <div className="flex flex-col">
                         <span className="text-[10px] font-bold">Behance Portfolyosu</span>
                         <span className="text-[9px] text-[#6B7280]">Dış bağlantı içerir</span>
                      </div>
                    </div>
                 </div>
             </div>

             {/* 7. Trust Area */}
             <div className="px-5 py-5 border-b border-[#E5E7EB]">
                 <h3 className="text-[12px] font-bold tracking-tight mb-3">Profil Güvenliği</h3>
                 <div className="space-y-2">
                    <div className="flex items-center gap-2 text-[10px] font-medium">
                      <CheckCircle2 className="w-3.5 h-3.5 text-[#10B981]" strokeWidth={2.5}/>
                      Kimlik ve iletişim bilgisi doğrulandı
                    </div>
                    <div className="flex items-center gap-2 text-[10px] font-medium">
                      <CheckCircle2 className="w-3.5 h-3.5 text-[#10B981]" strokeWidth={2.5}/>
                      Profil bilgileri tamamlandı
                    </div>
                 </div>
             </div>

             {/* 8. Reviews */}
             <div className="px-5 py-5 border-b border-[#E5E7EB]">
                 <div className="flex justify-between items-center mb-4">
                    <h3 className="text-[12px] font-bold tracking-tight">Değerlendirmeler</h3>
                    <div className="flex items-center gap-1">
                        <Star className="w-3 h-3 fill-[#F97316] text-[#F97316]" />
                        <span className="text-[10px] font-bold">{user.rating || "4.9"}</span>
                    </div>
                 </div>
                 <div className="space-y-3">
                     {[
                         { name: "Selin Y.", service: "Sosyal medya tasarımı", rating: 5, date: "1 hafta önce", comment: "Muazzam bir vizyonu var. İletişimi çok iyi ve işini zamanında teslim etti." },
                         { name: "Can T.", service: "Logo tasarımı", rating: 5, date: "2 ay önce", comment: "Markamız için harika bir kimlik çalıştı. Gözüm kapalı tavsiye ederim." }
                     ].map((rev, i) => (
                         <div key={i} className="flex flex-col py-2 border-b border-[#F3F4F6] last:border-0 last:pb-0">
                             <div className="flex justify-between items-start mb-1">
                                 <div>
                                     <div className="text-[12px] font-extrabold mb-0.5">{rev.name}</div>
                                     <div className="text-[9px] font-bold text-[#6B7280]">{rev.service}</div>
                                 </div>
                                 <div className="text-[9px] font-medium text-[#9CA3AF]">{rev.date}</div>
                             </div>
                             <div className="flex items-center gap-0.5 mb-1.5">
                                 {[1,2,3,4,5].map(star => <Star key={star} className="w-2.5 h-2.5 fill-[#F97316] text-[#F97316]" />)}
                             </div>
                             <p className="text-[10px] text-[#4B5563] font-medium leading-relaxed">{rev.comment}</p>
                         </div>
                     ))}
                 </div>
             </div>

          </div>
        )}

        {/* Modals Overlay */}
        <AnimatePresence>
          {(showReportModal || showBlockModal) && (
            <motion.div 
              key="modal-overlay"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="absolute inset-0 z-50 bg-[#111827]/40 backdrop-blur-sm flex items-center justify-center p-5"
            >
              {showBlockModal && (
                <motion.div 
                  key="block-modal"
                  initial={{ scale: 0.95, opacity: 0, y: 20 }}
                  animate={{ scale: 1, opacity: 1, y: 0 }}
                  exit={{ scale: 0.95, opacity: 0, y: 20 }}
                  className="bg-white w-full max-w-[340px] rounded-[24px] p-6 relative flex flex-col items-center text-center shadow-2xl border border-[#F3F4F6]"
                >
                  <div className="w-16 h-16 bg-[#FEF2F2] rounded-full flex items-center justify-center mb-4">
                    <Ban className="w-8 h-8 text-[#EF4444]" strokeWidth={2.5} />
                  </div>
                  <h3 className="text-[14px] font-extrabold text-[#111827] mb-2 tracking-tight">Kullanıcıyı Engelle</h3>
                  <p className="text-[10px] text-[#6B7280] leading-relaxed mb-6 font-medium">
                    Bu kullanıcıyı engellediğinizde ilanlarınızı göremez ve sizinle iletişime geçemez. Emin misiniz?
                  </p>
                  <div className="flex gap-3 w-full">
                    <button 
                      onClick={() => setShowBlockModal(false)}
                      className="flex-1 py-3 bg-white border border-[#E5E7EB] text-[#4B5563] font-bold rounded-[14px] text-[12px] active:scale-[0.98] transition-transform shadow-sm"
                    >
                      İptal
                    </button>
                    <button 
                      onClick={() => {
                        setBlockSubmitted(true);
                        setTimeout(() => onBack(), 1500);
                      }}
                      className="flex-1 py-3 bg-[#EF4444] text-white font-bold rounded-[14px] text-[12px] shadow-sm shadow-[#EF4444]/20 active:scale-[0.98] transition-transform"
                    >
                      {blockSubmitted ? "Engellendi" : "Engelle"}
                    </button>
                  </div>
                </motion.div>
              )}

              {showReportModal && (
                <motion.div 
                  key="report-modal"
                  initial={{ scale: 0.95, opacity: 0, y: 20 }}
                  animate={{ scale: 1, opacity: 1, y: 0 }}
                  exit={{ scale: 0.95, opacity: 0, y: 20 }}
                  className="bg-white w-full max-w-[340px] rounded-[24px] p-6 relative flex flex-col shadow-2xl border border-[#F3F4F6]"
                >
                  <button onClick={() => setShowReportModal(false)} className="absolute top-4 right-4 text-[#9CA3AF] p-1.5 hover:bg-[#F3F4F6] transition-colors rounded-full active:scale-95">
                    <X className="w-5 h-5" strokeWidth={2.5}/>
                  </button>
                  
                  {reportSubmitted ? (
                    <div className="flex flex-col items-center text-center py-4">
                      <div className="w-16 h-16 bg-[#10B981]/10 rounded-full flex items-center justify-center mb-4">
                        <CheckCircle2 className="w-8 h-8 text-[#10B981]" strokeWidth={2.5} />
                      </div>
                      <h3 className="text-[14px] font-extrabold text-[#111827] mb-2 tracking-tight">Şikayetiniz Alındı</h3>
                      <p className="text-[10px] text-[#6B7280] leading-relaxed font-medium">
                        Şikayetiniz topluluk ekibine ulaştı. Profili inceleyeceğiz. Yardımınız için teşekkürler.
                      </p>
                      <button 
                        onClick={() => { setShowReportModal(false); onBack(); }}
                        className="w-full mt-6 py-3 bg-[#F97316] text-white font-bold rounded-[14px] text-[12px] shadow-sm shadow-[#F97316]/20 active:scale-[0.98] transition-transform"
                      >
                        Kapat
                      </button>
                    </div>
                  ) : (
                    <>
                      <h3 className="text-[14px] font-extrabold text-[#111827] mb-1.5 tracking-tight">Şikayet Et</h3>
                      <p className="text-[10px] text-[#6B7280] mb-5 leading-snug font-medium">
                        Lütfen bu hesabı neden şikayet ettiğinizi belirtin.
                      </p>
                      
                      <div className="flex flex-col mb-5">
                        {["Sahte Hesap / Dolandırıcılık", "Uygunsuz Dil / Davranış", "Rahatsız Edici Mesajlar", "Diğer"].map((reason, idx, arr) => (
                          <button
                            key={reason}
                            onClick={() => setReportReason(reason)}
                            className={`w-full text-left py-3 px-2 text-[12px] transition-colors flex items-center justify-between ${idx !== arr.length - 1 ? 'border-b border-[#F3F4F6]' : ''} ${reportReason === reason ? 'text-[#F97316] font-bold' : 'text-[#4B5563] font-medium hover:bg-[#F9FAFB] rounded-lg'}`}
                          >
                            {reason}
                            {reportReason === reason && <CheckCircle2 className="w-4 h-4 text-[#F97316]" strokeWidth={2.5} />}
                          </button>
                        ))}
                      </div>

                      <AnimatePresence>
                        {reportReason === "Diğer" && (
                          <motion.div 
                            initial={{ height: 0, opacity: 0 }}
                            animate={{ height: "auto", opacity: 1 }}
                            exit={{ height: 0, opacity: 0 }}
                            className="mb-5 overflow-hidden"
                          >
                            <textarea 
                              placeholder="Lütfen şikayet nedeninizi detaylı yazın..."
                              className="w-full bg-[#FAF9F6] border border-[#E5E7EB] rounded-[16px] p-3 text-[10px] text-[#111827] placeholder-[#9CA3AF] focus:outline-none focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316] resize-none h-24 font-medium"
                              value={customReason}
                              onChange={(e) => setCustomReason(e.target.value)}
                            />
                          </motion.div>
                        )}
                      </AnimatePresence>

                      <button 
                        disabled={!reportReason || (reportReason === "Diğer" && !customReason.trim())}
                        onClick={() => setReportSubmitted(true)}
                        className={`w-full py-3 font-bold rounded-[14px] text-[12px] transition-all flex items-center justify-center ${
                          (!reportReason || (reportReason === "Diğer" && !customReason.trim())) 
                            ? 'bg-[#F3F4F6] text-[#9CA3AF] cursor-not-allowed' 
                            : 'bg-[#F97316] text-white shadow-sm shadow-[#F97316]/20 active:scale-[0.98]'
                        }`}
                      >
                        Gönder
                      </button>
                    </>
                  )}
                </motion.div>
              )}
            </motion.div>
          )}
        </AnimatePresence>

      </div>
    </div>
  );
}
