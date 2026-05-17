/* @jsx React.createElement */
// Web app shell — sidebar nav + content area mirroring mobile screens.

const WebApp = ({ onExit }) => {
  const [active, setActive] = React.useState("kesfet");
  const [filter, setFilter] = React.useState("Tümü");

  const navItems = [
    { id: "kesfet",    label: "Keşfet",     icon: "pin" },
    { id: "hizmet",    label: "Hizmet al",  icon: "monitor" },
    { id: "islerim",   label: "İşlerim",    icon: "inbox", badge: 3 },
    { id: "mesajlar",  label: "Mesajlar",   icon: "message", badge: 2 },
    { id: "profil",    label: "Profil",     icon: "user" },
  ];

  const icons = {
    pin: <><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></>,
    inbox: <><path d="M22 12h-6l-2 3h-4l-2-3H2"/><path d="M5.45 5.11 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"/></>,
    monitor: <><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></>,
    message: <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>,
    user: <><circle cx="12" cy="8" r="4"/><path d="M4 21v-1a8 8 0 0 1 16 0v1"/></>,
    plus: <><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></>,
    heart: <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>,
    chat: <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>,
    search: <><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></>,
    bell: <><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></>,
  };

  const posts = [
    { type: "yardım", icon: "local_help", color: "#F97316", authorName: "Ayşe T.", authorImg: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=120&q=80", title: "Yıldız tornavida", desc: "Ufak bir montaj işim var, 1 saatliğine yıldız tornavida ödünç verebilecek biri var mı?", time: "5 dk", dist: "150m", likes: 2, comments: 0,
      img: "https://images.unsplash.com/photo-1581094288338-2314dddb7ece?auto=format&fit=crop&w=900&q=80" },
    { type: "iş", icon: "work", color: "#10B981", authorName: "Cansu B.", authorImg: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=120&q=80", desc: "Yarın 14:00'da ufak ırk köpeğimi 45 dk gezdirebilecek biri lazım.", time: "1 sa", dist: "1.2 km", likes: 4, comments: 1,
      img: "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?auto=format&fit=crop&w=900&q=80" },
    { type: "duyuru", icon: "announcement", color: "#F59E0B", authorName: "Kemal S.", authorImg: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=120&q=80", title: "Kayıp kedi · Tarçın", desc: "Dün akşam parkın yakınında kedimiz kayboldu. Gören olursa lütfen ulaşsın.", time: "27 dk", dist: "0.2 km", likes: 12, comments: 5,
      img: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=900&q=80" },
    { type: "danışma", icon: "advisory", color: "#A855F7", authorName: "Mert K.", authorImg: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80", desc: "Köşede yeni açılan kafeyi deneyen oldu mu? Brunch yapıyorlar mı bilen?", time: "2 sa", dist: "0.4 km", likes: 8, comments: 3,
      img: "https://images.unsplash.com/photo-1507914997799-a3d77a30ad06?auto=format&fit=crop&w=900&q=80" },
  ];
  const filtered = filter === "Tümü" ? posts : posts.filter((p) => p.type === filter.toLowerCase());

  return (
    <div style={{
      position: "fixed", inset: 0, background: "var(--vv-bg)",
      display: "grid", gridTemplateColumns: "248px 1fr",
      fontFamily: "var(--vv-font-sans)", zIndex: 100,
    }}>
      <aside style={{
        background: "var(--vv-surface)",
        borderRight: "1px solid var(--vv-border-subtle)",
        display: "flex", flexDirection: "column",
        padding: "20px 16px",
      }}>
        <div style={{ display: "flex", alignItems: "center", gap: 10, padding: "0 8px 24px" }}>
          <img src="../../assets/viaverse_icon.png" alt="" style={{ width: 32, height: 32 }}/>
          <img src="../../assets/viaverse_wordmark.png" alt="Viaverse" style={{ height: 18 }}/>
        </div>

        <button style={{
          background: "var(--vv-primary)", color: "var(--vv-primary-foreground)", border: "none",
          padding: "12px 16px", borderRadius: 12, cursor: "pointer",
          fontSize: 13, fontWeight: 800, letterSpacing: "-0.01em",
          boxShadow: "var(--vv-shadow-cta)", marginBottom: 20,
          display: "flex", alignItems: "center", gap: 8, justifyContent: "center",
        }}>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">{icons.plus}</svg>
          Paylaş
        </button>

        {navItems.map((n) => (
          <button key={n.id} onClick={() => setActive(n.id)} style={{
            display: "flex", alignItems: "center", gap: 10,
            padding: "10px 12px", borderRadius: 10,
            background: active === n.id ? "var(--vv-primary-soft)" : "none",
            color: active === n.id ? "var(--vv-primary)" : "var(--vv-fg)",
            border: "none", cursor: "pointer",
            fontSize: 13, fontWeight: 700, letterSpacing: "-0.01em",
            margin: "2px 0", position: "relative",
          }}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
              strokeWidth={active === n.id ? 2 : 1.6} strokeLinecap="round" strokeLinejoin="round">{icons[n.icon]}</svg>
            <span style={{ flex: 1, textAlign: "left" }}>{n.label}</span>
            {n.badge && (
              <span style={{
                background: "var(--vv-primary)", color: "var(--vv-primary-foreground)",
                fontSize: 10, fontWeight: 800,
                padding: "1px 7px", borderRadius: 999,
              }}>{n.badge}</span>
            )}
          </button>
        ))}

        <div style={{ flex: 1 }}/>

        <div style={{
          display: "flex", alignItems: "center", gap: 10,
          padding: "10px 12px", borderRadius: 12,
          background: "var(--vv-surface-muted)",
          marginBottom: 10,
        }}>
          <img src="https://i.pravatar.cc/64?img=47" alt="" style={{ width: 32, height: 32, borderRadius: "50%" }}/>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontSize: 12, fontWeight: 800, color: "var(--vv-fg)" }}>Zehra E.</div>
            <div style={{ fontSize: 10, color: "var(--vv-fg-muted)" }}>Kadıköy / İstanbul</div>
          </div>
        </div>
        <button onClick={onExit} style={{
          background: "none", border: "none",
          fontSize: 11, color: "var(--vv-fg-muted)", fontWeight: 700,
          padding: "8px 12px", textAlign: "left", cursor: "pointer",
        }}>← Pazarlama sayfasına dön</button>
      </aside>

      <main style={{ overflow: "hidden", display: "flex", flexDirection: "column" }}>
        <header style={{
          padding: "16px 32px",
          borderBottom: "1px solid var(--vv-border-subtle)",
          background: "var(--vv-surface)",
          display: "flex", alignItems: "center", gap: 16,
        }}>
          <div style={{
            flex: 1, maxWidth: 540,
            display: "flex", alignItems: "center", gap: 10,
            background: "var(--vv-surface-muted)", borderRadius: 999,
            padding: "8px 18px",
          }}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="var(--vv-primary)" strokeWidth="2">{icons.pin}</svg>
            <span style={{ flex: 1, fontSize: 13, color: "var(--vv-fg-muted)" }}>Çevrende neler oluyor, gör</span>
            <button style={{
              background: "var(--vv-primary)", color: "var(--vv-primary-foreground)", border: "none",
              padding: "6px 16px", borderRadius: 999, fontSize: 12, fontWeight: 800, cursor: "pointer",
            }}>ara</button>
          </div>
          <div style={{ marginLeft: "auto", display: "flex", gap: 8 }}>
            <button style={{
              width: 36, height: 36, borderRadius: "50%",
              background: "var(--vv-surface-muted)", border: "1px solid var(--vv-border-muted)",
              color: "var(--vv-fg)", cursor: "pointer",
              display: "flex", alignItems: "center", justifyContent: "center",
            }}><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">{icons.bell}</svg></button>
          </div>
        </header>

        <div style={{ flex: 1, overflowY: "auto", padding: 32 }}>
          {active === "kesfet" && (
            <>
              <div style={{ display: "flex", gap: 10, marginBottom: 20 }}>
                {["Tümü", "Duyuru", "Yardım", "İş", "Danışma"].map((c) => (
                  <button key={c} onClick={() => setFilter(c)} style={{
                    height: 36, padding: "0 16px", borderRadius: 999,
                    fontSize: 12, fontWeight: 700,
                    background: filter === c ? "var(--vv-primary)" : "var(--vv-surface)",
                    color: filter === c ? "var(--vv-primary-foreground)" : "var(--vv-fg-muted)",
                    border: `1px solid ${filter === c ? "var(--vv-primary)" : "var(--vv-border-subtle)"}`,
                    cursor: "pointer",
                  }}>{c}</button>
                ))}
              </div>
              <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(360px, 1fr))", gap: 14 }}>
                {filtered.map((p, i) => (
                  <article key={i} style={{
                    background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
                    borderRadius: 18, overflow: "hidden",
                    display: "flex", flexDirection: "column",
                    boxShadow: "var(--vv-shadow-sm)",
                  }}>
                    {p.img && (
                      <div style={{
                        height: 140,
                        backgroundImage: `url(${p.img})`,
                        backgroundSize: "cover", backgroundPosition: "center",
                        position: "relative",
                      }}>
                        <span style={{
                          position: "absolute", top: 10, left: 10,
                          display: "inline-flex", alignItems: "center", gap: 4,
                          padding: "3px 9px 3px 5px", borderRadius: 999,
                          background: "rgba(255,251,245,0.94)", backdropFilter: "blur(6px)",
                          fontSize: 10, fontWeight: 800, color: p.color,
                          textTransform: "uppercase", letterSpacing: "0.05em",
                        }}>
                          <img src={`../../assets/categories/${p.icon}.png`} alt="" style={{ width: 16, height: 16 }}/>
                          {p.type}
                        </span>
                      </div>
                    )}
                    <div style={{ padding: 16, display: "flex", flexDirection: "column", gap: 10 }}>
                      <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
                        <img src={p.authorImg} alt="" style={{ width: 32, height: 32, borderRadius: "50%", objectFit: "cover" }}/>
                        <div style={{ flex: 1 }}>
                          <div style={{ fontSize: 13, fontWeight: 800, color: "var(--vv-fg)" }}>{p.authorName}</div>
                          <div style={{ fontSize: 10, color: "var(--vv-fg-muted)" }}>{p.time} · {p.dist}</div>
                        </div>
                      </div>
                      {p.title && <div style={{ fontSize: 14, fontWeight: 800, color: "var(--vv-fg-strong)" }}>{p.title}</div>}
                      <div style={{ fontSize: 13, color: "var(--vv-fg)", lineHeight: 1.5 }}>{p.desc}</div>
                      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginTop: 4, paddingTop: 8, borderTop: "1px solid var(--vv-border-faint)" }}>
                        <div style={{ display: "flex", gap: 16, color: "var(--vv-fg-muted)", fontSize: 12 }}>
                          <span style={{ display: "inline-flex", alignItems: "center", gap: 5 }}><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">{icons.heart}</svg> {p.likes}</span>
                          <span style={{ display: "inline-flex", alignItems: "center", gap: 5 }}><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">{icons.chat}</svg> {p.comments}</span>
                        </div>
                        {p.type === "iş" && (
                          <button style={{
                            background: "var(--vv-primary)", color: "var(--vv-primary-foreground)", border: "none",
                            padding: "5px 14px", borderRadius: 999, fontSize: 11, fontWeight: 800, cursor: "pointer",
                          }}>teklif ver</button>
                        )}
                      </div>
                    </div>
                  </article>
                ))}
              </div>
            </>
          )}

          {active === "hizmet" && (
            <div>
              <h2 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 22, letterSpacing: "-0.02em", color: "var(--vv-fg-strong)", margin: "4px 0 6px" }}>
                Hizmet verenlere göz at veya <span style={{ color: "var(--vv-primary)" }}>talep oluştur</span>
              </h2>
              <p style={{ fontSize: 13, color: "var(--vv-fg-muted)", margin: "0 0 20px", maxWidth: 560 }}>
                Kategorilere göre profesyonelleri keşfedebilir veya neye ihtiyacın olduğunu yazıp uygun hizmet verenlerden teklif alabilirsin.
              </p>
              <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))", gap: 12 }}>
                {[
                  ["home_repair","Ev, Tamirat & Tadilat","45 sağlayıcı"],
                  ["digital_software","Dijital & Yazılım","90 sağlayıcı"],
                  ["creative_media","Yaratıcı İşler","65 sağlayıcı"],
                  ["education","Eğitim & Mentorluk","31 sağlayıcı"],
                  ["cleaning","Temizlik & Düzenleme","52 sağlayıcı"],
                  ["logistics","Lojistik & Paket","28 sağlayıcı"],
                  ["care_health","Kişisel Bakım","40 sağlayıcı"],
                  ["professional_consulting","Profesyonel","22 sağlayıcı"],
                  ["pets","Evcil Hayvan","15 sağlayıcı"],
                  ["events","Etkinlik","19 sağlayıcı"],
                ].map(([icon,name,n]) => (
                  <div key={icon} style={{
                    background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
                    borderRadius: 14, padding: 16, display: "flex", alignItems: "center", gap: 12, cursor: "pointer",
                  }}>
                    <img src={`../../assets/categories/${icon}.png`} alt="" style={{ width: 40, height: 40 }}/>
                    <div>
                      <div style={{ fontSize: 13, fontWeight: 700, color: "var(--vv-fg)" }}>{name}</div>
                      <div style={{ fontSize: 10, color: "var(--vv-fg-muted)" }}>{n}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {(active === "islerim" || active === "mesajlar" || active === "profil") && (
            <div style={{
              textAlign: "center", padding: 60, color: "var(--vv-fg-muted)",
              background: "var(--vv-surface)", borderRadius: 18, border: "1px dashed var(--vv-border-muted)",
            }}>
              <img src="../../assets/categories/megaphone.png" alt="" style={{ width: 80, height: 80 }}/>
              <p style={{ fontSize: 13, marginTop: 12, fontWeight: 500 }}>"{active}" ekranı mobil ekranın web versiyonu olarak gelir.</p>
            </div>
          )}
        </div>
      </main>
    </div>
  );
};

window.WebApp = WebApp;
