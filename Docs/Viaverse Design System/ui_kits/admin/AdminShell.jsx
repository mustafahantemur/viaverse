/* @jsx React.createElement */
const AdminShell = ({ active, onChange, children }) => {
  const groups = [
    {
      label: "Operasyon",
      items: [
        { id: "verification", icon: "shield-check", label: "Doğrulama",   badge: 12 },
        { id: "moderation",   icon: "flag",         label: "Moderasyon",  badge: 4 },
        { id: "disputes",     icon: "scale",        label: "Uyuşmazlık",  badge: 2 },
        { id: "support",      icon: "life-buoy",    label: "Destek" },
      ],
    },
    {
      label: "Ürün",
      items: [
        { id: "categories",   icon: "grid",     label: "Kategoriler" },
        { id: "subscriptions",icon: "package",  label: "Abonelikler" },
        { id: "seo",          icon: "globe",    label: "SEO" },
      ],
    },
    {
      label: "Finans & ölçüm",
      items: [
        { id: "payments",     icon: "card",     label: "Ödemeler" },
        { id: "analytics",    icon: "chart",    label: "Analitik" },
        { id: "audit",        icon: "history",  label: "Denetim kayıtları" },
      ],
    },
  ];

  const ico = {
    "shield-check": <><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><path d="M9 12l2 2 4-4"/></>,
    flag: <><path d="M4 22V4a1 1 0 0 1 1.5-.87L20 11l-14.5 7.87A1 1 0 0 1 4 18z"/></>,
    scale: <><path d="M12 3v18"/><path d="M5 3h14"/><path d="M8 21h8"/><path d="M3 8l4-5 4 5-2 2a3 3 0 0 1-4 0z"/><path d="M21 8l-4-5-4 5 2 2a3 3 0 0 0 4 0z"/></>,
    "life-buoy": <><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="4"/><line x1="4.93" y1="4.93" x2="9.17" y2="9.17"/><line x1="14.83" y1="14.83" x2="19.07" y2="19.07"/><line x1="14.83" y1="9.17" x2="19.07" y2="4.93"/><line x1="14.83" y1="9.17" x2="18.36" y2="5.64"/><line x1="4.93" y1="19.07" x2="9.17" y2="14.83"/></>,
    grid: <><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></>,
    package: <><path d="M16.5 9.4l-9-5.19"/><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/></>,
    globe: <><circle cx="12" cy="12" r="10"/><line x1="2" y1="12" x2="22" y2="12"/><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/></>,
    card: <><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></>,
    chart: <><line x1="3" y1="21" x2="21" y2="21"/><rect x="6" y="13" width="3" height="6"/><rect x="11" y="9" width="3" height="10"/><rect x="16" y="5" width="3" height="14"/></>,
    history: <><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></>,
  };

  return (
    <div style={{
      position: "fixed", inset: 0,
      display: "grid", gridTemplateColumns: "232px 1fr",
      fontFamily: "var(--vv-font-sans)", color: "var(--vv-fg)",
      background: "var(--vv-bg)",
    }}>
      <aside style={{
        background: "var(--vv-surface)",
        borderRight: "1px solid var(--vv-border-subtle)",
        display: "flex", flexDirection: "column",
        padding: "18px 14px",
        overflowY: "auto",
      }}>
        <div style={{ display: "flex", alignItems: "center", gap: 10, padding: "0 6px 18px", borderBottom: "1px solid var(--vv-border-faint)", marginBottom: 14 }}>
          <img src="../../assets/viaverse_icon.png" alt="" style={{ width: 28, height: 28 }}/>
          <div>
            <div style={{ fontSize: 13, fontWeight: 800, color: "var(--vv-fg-strong)" }}>Viaverse</div>
            <div style={{ fontSize: 10, fontWeight: 700, color: "var(--vv-trust)", textTransform: "uppercase", letterSpacing: "0.1em" }}>Admin · v0.4</div>
          </div>
        </div>

        {groups.map((g) => (
          <div key={g.label} style={{ marginBottom: 14 }}>
            <div style={{
              fontSize: 10, fontWeight: 800, color: "var(--vv-fg-muted)",
              textTransform: "uppercase", letterSpacing: "0.15em",
              padding: "6px 10px",
            }}>{g.label}</div>
            {g.items.map((it) => (
              <button key={it.id} onClick={() => onChange(it.id)} style={{
                width: "100%", display: "flex", alignItems: "center", gap: 10,
                padding: "8px 10px", borderRadius: 8,
                background: active === it.id ? "var(--vv-primary-soft)" : "none",
                color: active === it.id ? "var(--vv-primary)" : "var(--vv-fg)",
                border: "none", cursor: "pointer",
                fontSize: 12, fontWeight: 700, letterSpacing: "-0.01em",
                margin: "1px 0",
              }}>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={active === it.id ? 2 : 1.5} strokeLinecap="round" strokeLinejoin="round">{ico[it.icon]}</svg>
                <span style={{ flex: 1, textAlign: "left" }}>{it.label}</span>
                {it.badge != null && (
                  <span style={{
                    background: it.badge > 0 ? "rgba(239,68,68,0.12)" : "var(--vv-surface-muted)",
                    color: it.badge > 0 ? "#EF4444" : "var(--vv-fg-muted)",
                    fontSize: 10, fontWeight: 800,
                    padding: "1px 6px", borderRadius: 999,
                  }}>{it.badge}</span>
                )}
              </button>
            ))}
          </div>
        ))}

        <div style={{ flex: 1 }}/>

        <div style={{
          display: "flex", alignItems: "center", gap: 10,
          padding: "10px", borderRadius: 10,
          background: "var(--vv-surface-muted)",
        }}>
          <img src="https://i.pravatar.cc/64?img=51" alt="" style={{ width: 28, height: 28, borderRadius: "50%" }}/>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontSize: 11, fontWeight: 800, color: "var(--vv-fg)" }}>Selin O.</div>
            <div style={{ fontSize: 9, color: "var(--vv-fg-muted)" }}>Trust & Safety lead</div>
          </div>
        </div>
      </aside>

      <main style={{ display: "flex", flexDirection: "column", overflow: "hidden" }}>
        <header style={{
          height: 56, flexShrink: 0,
          padding: "0 24px",
          display: "flex", alignItems: "center", gap: 16,
          background: "var(--vv-surface)",
          borderBottom: "1px solid var(--vv-border-subtle)",
        }}>
          <div style={{
            flex: 1, maxWidth: 480, display: "flex", alignItems: "center", gap: 8,
            background: "var(--vv-surface-muted)", border: "1px solid var(--vv-border-faint)",
            padding: "7px 14px", borderRadius: 8,
          }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="var(--vv-fg-muted)" strokeWidth="1.6"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
            <input placeholder="Kullanıcı, talep ID veya işlem ara…  ⌘K" style={{
              flex: 1, background: "none", border: "none", outline: "none",
              fontSize: 12, color: "var(--vv-fg)", fontFamily: "var(--vv-font-sans)",
            }}/>
          </div>
          <div style={{ marginLeft: "auto", display: "flex", gap: 8, alignItems: "center" }}>
            <span style={{
              display: "inline-flex", alignItems: "center", gap: 6, padding: "5px 10px",
              borderRadius: 999, background: "rgba(16,185,129,0.1)", border: "1px solid rgba(16,185,129,0.2)",
              fontSize: 10, fontWeight: 800, color: "#10B981", textTransform: "uppercase", letterSpacing: "0.1em",
            }}>
              <span style={{ width: 5, height: 5, borderRadius: "50%", background: "#10B981", animation: "vv-pulse 1.8s ease-in-out infinite" }}/>
              Sistem sağlıklı
            </span>
            <button style={{
              padding: "6px 14px", borderRadius: 8, border: "1px solid var(--vv-border-muted)",
              background: "var(--vv-surface)", fontSize: 12, fontWeight: 700, cursor: "pointer",
            }}>Dışa aktar</button>
            <button style={{
              padding: "6px 14px", borderRadius: 8, border: "none",
              background: "var(--vv-primary)", color: "var(--vv-primary-foreground)", fontSize: 12, fontWeight: 800, cursor: "pointer",
              boxShadow: "var(--vv-shadow-cta)",
            }}>+ Yeni manuel doğrulama</button>
          </div>
        </header>

        <div style={{ flex: 1, overflowY: "auto", padding: 24 }}>
          {children}
        </div>
      </main>
    </div>
  );
};

window.AdminShell = AdminShell;
