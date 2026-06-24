/* @jsx React.createElement */
// Wires the kit's screens together. Loaded last.

const MAIN_CATEGORIES = [
  { icon: "home_repair",            name: "Ev, Tamirat & Tadilat",         providers: "45 Hizmet Sağlayıcı", subCats: ["Boya ve Badana", "Su Tesisatı ve Onarım", "Elektrik Tesisatı ve Arıza", "Mobilya Montajı ve Tamiri", "Kombi ve Radyatör Bakımı", "Klima Montajı ve Servisi", "Mutfak ve Banyo Tadilatı", "Parke ve Zemin Döşeme"] },
  { icon: "digital_software",       name: "Dijital & Yazılım Hizmetleri",  providers: "90 Hizmet Sağlayıcı", subCats: ["Web Tasarım ve Geliştirme", "Mobil Uygulama Geliştirme", "E-ticaret Sistemleri", "Veri Bilimi ve Analitiği", "Yapay Zeka Çözümleri", "Siber Güvenlik", "Bulut Bilişim", "UI/UX Tasarımı"] },
  { icon: "creative_media",         name: "Yaratıcı İşler & Medya",        providers: "65 Hizmet Sağlayıcı", subCats: ["Logo ve Kurumsal Kimlik", "Sosyal Medya İçerik Tasarımı", "Video Kurgu", "2D / 3D Animasyon", "Ürün Fotoğrafçılığı", "Metin Yazarlığı", "Seslendirme", "Ambalaj Tasarımı"] },
  { icon: "education",              name: "Eğitim, Ders & Mentorluk",      providers: "31 Hizmet Sağlayıcı", subCats: ["Matematik Özel Ders", "Yabancı Dil Eğitimi", "Yazılım ve Programlama Eğitimi", "YKS, LGS Sınav Koçluğu", "Enstrüman Dersleri", "Kariyer Mentorluğu", "Yaşam Koçluğu", "Finansal Okuryazarlık"] },
  { icon: "cleaning",               name: "Temizlik & Düzenleme",          providers: "52 Hizmet Sağlayıcı", subCats: ["Ev Temizliği", "Ofis Temizliği", "İnşaat Sonrası Temizlik", "Cam Temizliği", "Halı Yıkama", "İlaçlama", "Bahçe Temizliği", "Apartman Temizliği"] },
  { icon: "logistics",              name: "Lojistik, Paket & Destek",      providers: "28 Hizmet Sağlayıcı", subCats: ["Şehir İçi Kurye", "Market Alışverişi", "Evden Eve Nakliye", "Parça Eşya Taşıma", "Eczane Alışverişi", "Çiçek ve Hediye Gönderimi", "Motorlu Kurye", "Havaalanı Transfer"] },
  { icon: "care_health",            name: "Kişisel Bakım & Sağlık",        providers: "40 Hizmet Sağlayıcı", subCats: ["Berber ve Kuaför", "Cilt Bakımı ve Estetik", "Manikür / Pedikür", "Masaj ve Spa", "Kişisel Spor Eğitmeni", "Diyetisyen", "Fizyoterapi", "Yaşlı Bakım Refakatçiliği"] },
  { icon: "professional_consulting",name: "Profesyonel & Danışmanlık",     providers: "22 Hizmet Sağlayıcı", subCats: ["Avukatlık", "Muhasebe", "Çeviri ve Tercümanlık", "Emlak Danışmanlığı", "İK ve İşe Alım", "İş Stratejisi", "Pazarlama Stratejisi", "Sigorta Danışmanlığı"] },
  { icon: "pets",                   name: "Evcil Hayvan Hizmetleri",       providers: "15 Hizmet Sağlayıcı", subCats: ["Köpek Gezdirme", "Pet Bakıcılığı", "Pet Kuaförü", "Veterinerlik Danışmanlığı", "Köpek Eğitimi", "Pet Taksi", "Akvaryum Bakımı", "Pet Otel"] },
  { icon: "events",                 name: "Etkinlik & Organizasyon",       providers: "19 Hizmet Sağlayıcı", subCats: ["Düğün Organizasyonu", "Doğum Günü", "Kurumsal Etkinlik", "Ses & Işık", "Catering", "DJ ve Canlı Müzik", "Sahne Kurulumu", "Etkinlik Fotoğrafçılığı"] },
];

const SAMPLE_POSTS = [
  {
    type: "yardım", authorName: "Ayşe T.",
    authorImg: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=150&q=80",
    title: "Yıldız Tornavida",
    desc: "Ufak bir montaj işim var, 1 saatliğine yıldız tornavida ödünç verebilecek komşu var mı?",
    publishTime: "5m", dist: "150m", likes: 2, comments: 0,
  },
  {
    type: "duyuru", authorName: "Kemal S.",
    authorImg: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&q=80",
    title: "Kayıp Kedi",
    desc: "Dün akşam üzeri parkın orada kedimiz Tarçın kayboldu. Görenlerin haber vermesini rica ederiz.",
    postImg: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=600&q=80",
    publishTime: "27m", dist: "0.2 km", likes: 12, comments: 5,
  },
  {
    type: "iş", authorName: "Cansu B.",
    authorImg: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=150&q=80",
    desc: "Yarın saat 14:00'da ufak ırk köpeğimi 45 dakika gezdirecek biri lazım.",
    publishTime: "1h", dist: "1.2 km", likes: 4, comments: 1,
  },
  {
    type: "danışma", authorName: "Mert K.",
    authorImg: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
    desc: "Mahallede yeni açılan kahveciyi deneyen oldu mu? Brunch yapıyorlar mı bilen?",
    publishTime: "2h", dist: "0.4 km", likes: 8, comments: 3,
  },
];

const SAMPLE_THREADS = [
  { name: "Ahmet Yılmaz",  img: "https://i.pravatar.cc/150?img=12", time: "10:45", preview: "Selam Ahmet usta, yarın öğleden sonra uygunum...", unread: 0 },
  { name: "Ayşe Demir",    img: "https://i.pravatar.cc/150?img=44", time: "Dün",   preview: "Teşekkür ederim, görüşmek üzere.",              unread: 2 },
  { name: "Mehmet Kaya",   img: "https://i.pravatar.cc/150?img=33", time: "Salı",  preview: "Fiyat teklifimi ilettim, inceleyiniz.",         unread: 0 },
];

const SAMPLE_OFFERS = [
  { name: "Ahmet Usta",      img: "https://i.pravatar.cc/150?img=12", rating: 4.9, reviews: 124, price: "1.200",
    desc: "Merhaba, işleminiz için gerekli tüm ekipmanlar tarafımca sağlanacaktır. Belirtilen saatte başlayıp en kısa sürede bitirmeyi planlıyorum." },
  { name: "Temizleller LTD", img: "https://i.pravatar.cc/150?img=49", rating: 4.7, reviews: 89,  price: "1.450",
    desc: "Alanında uzman 3 kişilik ekibimizle hizmet vermekteyiz." },
];

/* =====================================================================
   HOME (Keşfet / Hizmet al)
   ===================================================================== */
const HomeScreen = ({ onChat, onProfile, onJobs, onProviderWelcome, dark, onDarkChange }) => {
  const { VVHeader, VVFeedCard, VVCategoryRow, VV } = window;
  const { Chip } = VV;
  const [mode, setMode] = React.useState("keşfet");
  const [filter, setFilter] = React.useState("Tümü");
  const [expanded, setExpanded] = React.useState(null);
  const [tab, setTab] = React.useState("home");

  const chips = [
    { id: "Tümü",     iconCat: null,        label: "Tümü" },
    { id: "Duyuru",   iconCat: "announcement", label: "Duyuru" },
    { id: "Yardım",   iconCat: "local_help",   label: "Yardım" },
    { id: "İş",       iconCat: "work",         label: "İş" },
    { id: "Danışma",  iconCat: "advisory",     label: "Danışma" },
  ];

  return (
    <div style={{ position: "absolute", inset: 0, display: "flex", flexDirection: "column", background: "var(--vv-bg)" }}>
      <VVHeader mode={mode} onModeChange={setMode} dark={dark} onDarkChange={onDarkChange}/>

      <main style={{ flex: 1, overflowY: "auto", paddingBottom: 100, background: "var(--vv-bg)" }}>
        {mode === "keşfet" ? (
          <>
            <div style={{
              display: "flex", gap: 8, padding: "16px",
              background: "var(--vv-surface)", overflowX: "auto",
            }}>
              {chips.map((c) => (
                <Chip key={c.id} active={filter === c.id} onClick={() => setFilter(c.id)}
                  icon={c.iconCat ? <img src={`../../assets/categories/${c.iconCat}.png`} alt=""
                    style={{ width: 16, height: 16, objectFit: "contain" }}/> : null}>
                  {c.label}
                </Chip>
              ))}
            </div>
            <div>
              {SAMPLE_POSTS
                .filter((p) => filter === "Tümü" || p.type === filter.toLowerCase().replace("ş", "ş"))
                .map((p, i) => <VVFeedCard key={i} post={p} onBid={onJobs}/>)
              }
            </div>
          </>
        ) : (
          <div style={{ padding: 16 }}>
            <h2 style={{ fontFamily: "var(--vv-font-display)", fontSize: 17, fontWeight: 800, color: "var(--vv-fg-strong)", letterSpacing: "-0.02em", margin: "4px 0 6px" }}>
              Hizmet verenlere göz at veya <span style={{ color: "var(--vv-primary)" }}>talep oluştur</span>
            </h2>
            <p style={{ fontSize: 12, color: "var(--vv-fg-muted)", lineHeight: 1.6, margin: "0 0 16px" }}>
              Kategorilere göre profesyonelleri inceleyebilir veya neye ihtiyacın olduğunu yazarak uygun hizmet verenlerden teklif alabilirsin.
            </p>
            {MAIN_CATEGORIES.map((cat) => (
              <window.VVCategoryRow key={cat.name}
                category={cat}
                expanded={expanded === cat.name}
                onToggle={() => setExpanded(expanded === cat.name ? null : cat.name)}
                onSelectSub={() => {}}
              />
            ))}
          </div>
        )}
      </main>

      <window.VVBottomNav active={tab} onChange={(t) => {
        setTab(t);
        if (t === "messages") onChat();
        else if (t === "jobs") onJobs();
        else if (t === "profile") onProfile();
        else if (t === "publish") onProviderWelcome();
      }}/>
    </div>
  );
};

/* =====================================================================
   JOBS — Active requests + incoming offers (bottom sheet)
   ===================================================================== */
const JobsScreen = ({ onHome, onChat, onProfile, onProviderWelcome }) => {
  const { VVOfferCard, VV } = window;
  const { Icon } = VV;
  const [tab, setTab] = React.useState("jobs");
  const [activeTab, setActiveTab] = React.useState("active");
  const [showOffers, setShowOffers] = React.useState(true);

  return (
    <div style={{ position: "absolute", inset: 0, display: "flex", flexDirection: "column", background: "var(--vv-bg)" }}>
      <header style={{ padding: "16px 16px 0", background: "var(--vv-surface)" }}>
        <h2 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 22, margin: 0, letterSpacing: "-0.02em", color: "var(--vv-fg-strong)" }}>İşlerim</h2>
        <div style={{ display: "flex", gap: 18, marginTop: 16, borderBottom: "1px solid var(--vv-border-subtle)" }}>
          {[
            { id: "active",  label: "Aktif Taleplerim" },
            { id: "sent",    label: "Verdiğim Teklifler" },
            { id: "past",    label: "Geçmiş İşlemler" },
          ].map((t) => (
            <button key={t.id} onClick={() => setActiveTab(t.id)} style={{
              fontSize: 12, fontWeight: 700, letterSpacing: "-0.01em",
              background: "none", border: "none", padding: "8px 0",
              borderBottom: `3px solid ${activeTab === t.id ? "var(--vv-primary)" : "transparent"}`,
              color: activeTab === t.id ? "var(--vv-fg)" : "var(--vv-fg-muted)",
              cursor: "pointer", marginBottom: -1,
            }}>{t.label}</button>
          ))}
        </div>
      </header>

      <main style={{ flex: 1, overflowY: "auto", padding: "16px", paddingBottom: 100 }}>
        {activeTab === "active" && (
          <div style={{
            background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
            borderRadius: 14, padding: 14,
            borderLeft: "3px solid var(--vv-primary)",
            boxShadow: "var(--vv-shadow-sm)",
          }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 4 }}>
              <span style={{ fontSize: 13, fontWeight: 800, color: "var(--vv-primary)" }}>Aktif Talep · Köpek Gezdirme</span>
              <span style={{ fontSize: 10, color: "var(--vv-fg-muted)", background: "var(--vv-surface-muted)", padding: "3px 8px", borderRadius: 999 }}>10 dk önce</span>
            </div>
            <div style={{ fontSize: 11, color: "var(--vv-fg-muted)", lineHeight: 1.5, marginBottom: 12 }}>
              Yarın saat 14:00'da ufak ırk köpeğimi 45 dakika gezdirecek biri lazım.
            </div>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <div style={{ display: "flex", alignItems: "center", gap: 4, color: "var(--vv-fg-muted)", fontSize: 10 }}>
                <Icon name="map" size={12}/> Ataşehir / 1.2 km
              </div>
              <button onClick={() => setShowOffers(true)} style={{
                background: "rgba(249,115,22,0.12)", border: "none",
                color: "var(--vv-primary)", padding: "6px 12px", borderRadius: 999,
                fontSize: 11, fontWeight: 800, cursor: "pointer",
              }}>2 Teklif Bekliyor →</button>
            </div>
          </div>
        )}
        {activeTab === "sent" && (
          <div style={{ padding: 16, textAlign: "center", color: "var(--vv-fg-muted)", fontSize: 12 }}>
            Henüz teklif vermedin. Keşfet'ten iş paylaşımlarına teklif gönderebilirsin.
          </div>
        )}
        {activeTab === "past" && (
          <div style={{ padding: 16, textAlign: "center", color: "var(--vv-fg-muted)", fontSize: 12 }}>
            Geçmiş işlemlerin burada görünecek.
          </div>
        )}
      </main>

      {showOffers && (
        <>
          <div onClick={() => setShowOffers(false)} style={{
            position: "absolute", inset: 0, background: "rgba(0,0,0,0.4)", zIndex: 60,
          }}/>
          <div style={{
            position: "absolute", bottom: 0, left: 0, right: 0,
            background: "var(--vv-surface)", borderTopLeftRadius: 24, borderTopRightRadius: 24,
            boxShadow: "var(--vv-shadow-lg)", zIndex: 70,
            maxHeight: "75%", display: "flex", flexDirection: "column",
          }}>
            <div style={{ display: "flex", justifyContent: "center", padding: "10px 0 4px" }}>
              <div style={{ width: 48, height: 5, borderRadius: 999, background: "var(--vv-border-muted)" }}/>
            </div>
            <div style={{
              display: "flex", alignItems: "center", justifyContent: "space-between",
              padding: "12px 16px", borderBottom: "1px solid var(--vv-border-subtle)",
            }}>
              <div style={{ width: 32 }}/>
              <h3 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 14, margin: 0, letterSpacing: "-0.02em", color: "var(--vv-fg-strong)" }}>Gelen Teklifler</h3>
              <button onClick={() => setShowOffers(false)} style={{
                width: 32, height: 32, borderRadius: "50%",
                background: "var(--vv-surface-muted)", border: "none",
                color: "var(--vv-fg-muted)", cursor: "pointer",
                display: "flex", alignItems: "center", justifyContent: "center",
              }}><Icon name="x" size={14}/></button>
            </div>
            <div style={{ flex: 1, overflowY: "auto", padding: 16 }}>
              {SAMPLE_OFFERS.map((o, i) => <VVOfferCard key={i} offer={o} onAccept={onChat}/>)}
            </div>
          </div>
        </>
      )}

      <window.VVBottomNav active="jobs" onChange={(t) => {
        if (t === "home") onHome();
        else if (t === "messages") onChat();
        else if (t === "profile") onProfile();
        else if (t === "publish") onProviderWelcome();
      }}/>
    </div>
  );
};

/* =====================================================================
   PROFILE
   ===================================================================== */
const ProfileScreen = ({ onHome, onChat, onJobs, onProviderWelcome, onLogout }) => {
  const { VV } = window;
  const { Avatar, Icon, NeutralButton, ModePill } = VV;
  const [tab] = React.useState("profile");

  return (
    <div style={{ position: "absolute", inset: 0, display: "flex", flexDirection: "column", background: "var(--vv-bg)" }}>
      <header style={{
        position: "sticky", top: 0, padding: "16px",
        background: "var(--vv-surface)",
        display: "flex", alignItems: "center", justifyContent: "space-between",
      }}>
        <button onClick={onHome} style={{
          width: 32, height: 32, borderRadius: "50%",
          border: "1px solid var(--vv-border-muted)", background: "var(--vv-surface-muted)",
          color: "var(--vv-fg)", cursor: "pointer",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}><Icon name="chevronLeft" size={18}/></button>
        <h2 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 14, margin: 0, letterSpacing: "-0.02em", color: "var(--vv-fg-strong)" }}>Profil</h2>
        <button style={{
          width: 32, height: 32, borderRadius: "50%",
          border: "1px solid var(--vv-border-muted)", background: "var(--vv-surface-muted)",
          color: "var(--vv-fg)", cursor: "pointer",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}><Icon name="bell" size={14}/></button>
      </header>

      <main style={{ flex: 1, overflowY: "auto", paddingBottom: 100 }}>
        <div style={{ background: "var(--vv-surface)", padding: "20px 24px 28px", textAlign: "center" }}>
          <div style={{ position: "relative", display: "inline-block" }}>
            <img src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80"
              style={{ width: 90, height: 90, borderRadius: "50%", objectFit: "cover", border: "3px solid var(--vv-surface)", boxShadow: "var(--vv-shadow-sm)" }}/>
            <div style={{
              position: "absolute", bottom: 4, right: 4,
              width: 24, height: 24, borderRadius: "50%",
              background: "var(--vv-primary)", color: "var(--vv-primary-foreground)",
              display: "flex", alignItems: "center", justifyContent: "center",
              border: "2px solid var(--vv-surface)",
            }}><Icon name="star" size={12} strokeWidth={2}/></div>
          </div>
          <h2 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 20, letterSpacing: "-0.02em", color: "var(--vv-fg-strong)", margin: "12px 0 4px" }}>Zehra E.</h2>
          <div style={{ display: "inline-flex", alignItems: "center", gap: 4, color: "var(--vv-fg-muted)", fontSize: 11, marginBottom: 12 }}>
            <Icon name="map" size={12}/> 150m, Kadıköy / İstanbul
          </div>
          <p style={{ fontSize: 12, color: "var(--vv-fg)", lineHeight: 1.6, margin: 0, padding: "0 8px", fontStyle: "italic" }}>
            "Merhaba, komşularımla yardımlaşmayı ve çevreme değer katmayı seven biriyim. Boş zamanlarımda becerilerimi paylaşmaktan mutluluk duyarım."
          </p>
        </div>

        <div style={{ display: "flex", gap: 8, padding: 16 }}>
          {[
            { n: "12",  l: "Açtığı\nPaylaşım" },
            { n: "47",  l: "Tamamlanan\nİş" },
            { n: "8",   l: "Yardım\nTalebi" },
            { n: "%94", l: "Yanıt\nOranı", green: true },
          ].map((s, i) => (
            <div key={i} style={{
              flex: 1, background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
              borderRadius: 14, padding: "12px 6px", textAlign: "center",
            }}>
              <div style={{
                fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 22,
                color: s.green ? "var(--vv-trust)" : "var(--vv-fg-strong)",
                letterSpacing: "-0.02em",
              }}>{s.n}</div>
              <div style={{ fontSize: 9, color: "var(--vv-fg-muted)", marginTop: 4, fontWeight: 600, whiteSpace: "pre-line", lineHeight: 1.3 }}>{s.l}</div>
            </div>
          ))}
        </div>

        <div style={{ padding: "0 16px" }}>
          <NeutralButton fullWidth onClick={onProviderWelcome} style={{ height: 52 }}>
            <ModePill>Hizmet Veren Modu</ModePill>
            <span style={{ marginLeft: "auto", color: "var(--vv-fg-muted)" }}><Icon name="chevronRight" size={16}/></span>
          </NeutralButton>
        </div>

        <div style={{ padding: 16 }}>
          <div style={{ background: "var(--vv-surface)", borderRadius: 14, border: "1px solid var(--vv-border-subtle)" }}>
            {[
              { icon: "user",     label: "Hesap & Profil" },
              { icon: "shield",   label: "Gizlilik & Güvenlik" },
              { icon: "bell",     label: "Bildirimler" },
              { icon: "map",      label: "Dil & Bölge" },
              { icon: "info",     label: "Destek & Yasal" },
            ].map((it, i, a) => (
              <button key={it.label} style={{
                width: "100%", display: "flex", alignItems: "center", gap: 12,
                padding: "14px 16px", background: "none", border: "none",
                borderBottom: i < a.length - 1 ? "1px solid var(--vv-border-faint)" : "none",
                cursor: "pointer", color: "var(--vv-fg)",
              }}>
                <span style={{ color: "var(--vv-fg-muted)" }}><Icon name={it.icon} size={18}/></span>
                <span style={{ flex: 1, textAlign: "left", fontSize: 13, fontWeight: 600 }}>{it.label}</span>
                <span style={{ color: "var(--vv-fg-muted)" }}><Icon name="chevronRight" size={16}/></span>
              </button>
            ))}
          </div>
          <button onClick={onLogout} style={{
            width: "100%", marginTop: 12, padding: "12px 0",
            background: "none", border: "none",
            color: "#EF4444", fontSize: 13, fontWeight: 800, cursor: "pointer",
          }}>Çıkış Yap</button>
        </div>
      </main>

      <window.VVBottomNav active={tab} onChange={(t) => {
        if (t === "home") onHome();
        else if (t === "messages") onChat();
        else if (t === "jobs") onJobs();
        else if (t === "publish") onProviderWelcome();
      }}/>
    </div>
  );
};

/* =====================================================================
   APP shell
   ===================================================================== */
const VVApp = () => {
  const [step, setStep] = React.useState("auth");
  const [dark, setDark] = React.useState(false);
  const [thread, setThread] = React.useState(null);

  React.useEffect(() => {
    document.documentElement.classList.toggle("dark", dark);
  }, [dark]);

  if (step === "auth")    return <window.VVAuth onAuthed={() => setStep("home")} onSkip={() => setStep("home")}/>;
  if (step === "home")    return <HomeScreen
    onChat={() => setStep("chats")} onProfile={() => setStep("profile")}
    onJobs={() => setStep("jobs")} onProviderWelcome={() => setStep("provider")}
    dark={dark} onDarkChange={setDark}
  />;
  if (step === "jobs")    return <JobsScreen
    onHome={() => setStep("home")} onChat={() => setStep("chats")}
    onProfile={() => setStep("profile")} onProviderWelcome={() => setStep("provider")}
  />;
  if (step === "chats")   return <window.VVChatList threads={SAMPLE_THREADS}
    onBack={() => setStep("home")} onOpen={(t) => { setThread(t); setStep("chat"); }}/>;
  if (step === "chat")    return <window.VVChatThread thread={thread} onBack={() => setStep("chats")}/>;
  if (step === "profile") return <ProfileScreen
    onHome={() => setStep("home")} onChat={() => setStep("chats")}
    onJobs={() => setStep("jobs")} onProviderWelcome={() => setStep("provider")}
    onLogout={() => setStep("auth")}
  />;
  if (step === "provider") return <window.VVProviderWelcome
    onBack={() => setStep("home")} onStart={() => setStep("home")}
  />;
  return null;
};

window.VVApp = VVApp;
