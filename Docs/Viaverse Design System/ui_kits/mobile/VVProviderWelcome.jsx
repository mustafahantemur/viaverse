/* @jsx React.createElement */
// Provider / Work Mode welcome screen — the "become a host" moment.

const VVProviderWelcome = ({ onStart, onBack }) => {
  const { PrimaryButton, GhostButton, ModePill, Icon } = window.VV;
  return (
    <div style={{ position: "absolute", inset: 0, background: "var(--vv-surface)", display: "flex", flexDirection: "column" }}>
      <header style={{
        display: "flex", alignItems: "center", justifyContent: "space-between",
        padding: "16px 16px 8px",
      }}>
        <button onClick={onBack} style={{
          width: 40, height: 40, marginLeft: -8, background: "none", border: "none",
          color: "var(--vv-fg-muted)", cursor: "pointer",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}><Icon name="chevronLeft" size={22}/></button>
        <ModePill>Hizmet Veren Modu</ModePill>
        <div style={{ width: 40 }}/>
      </header>

      <div style={{ flex: 1, padding: "16px 24px 32px", overflowY: "auto" }}>
        <div style={{ marginBottom: 24, width: 64, height: 64, position: "relative" }}>
          <img src="../../assets/logo_v_orange_green.svg" alt=""
            style={{ width: 48, height: 48, animation: "vv-spin-y 6s linear infinite" }}/>
        </div>

        <h1 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 22, margin: 0, color: "var(--vv-fg-strong)", letterSpacing: "-0.02em", lineHeight: 1.15 }}>
          Hizmet vermeye başla
        </h1>
        <p style={{ fontSize: 13, color: "var(--vv-fg-muted)", lineHeight: 1.6, marginTop: 8, marginBottom: 32, fontWeight: 500 }}>
          Yeteneğini, deneyimini ya da işletmeni Viaverse'de görünür yap. Gelen talepleri yönet, teklif ver ve işlerini tek yerden takip et.
        </p>

        {[
          { icon: "user",     title: "Profilini oluştur",     sub: "Müşterilerin seni, işlerini ve tarzını nasıl göreceğini düzenle." },
          { icon: "briefcase",title: "Hizmetlerini seç",      sub: "Kategori, alt kategori, hizmet bölgesi ve çalışma alanlarını belirle." },
          { icon: "message",  title: "Taleplere yanıt ver",   sub: "Uygun işlere teklif ver, aktif işlerini takip et." },
        ].map((row) => (
          <div key={row.title} style={{
            display: "flex", alignItems: "flex-start", gap: 14,
            padding: "16px 0",
            borderBottom: "1px solid var(--vv-border-subtle)",
          }}>
            <div style={{
              width: 40, height: 40, borderRadius: "50%",
              background: "var(--vv-surface-muted)", border: "1px solid var(--vv-border-faint)",
              display: "flex", alignItems: "center", justifyContent: "center",
              color: row.icon === "briefcase" ? "var(--vv-primary)" : "var(--vv-fg)",
              flexShrink: 0,
            }}><Icon name={row.icon} size={20}/></div>
            <div>
              <div style={{ fontSize: 13, fontWeight: 800, color: "var(--vv-fg)", letterSpacing: "-0.01em" }}>{row.title}</div>
              <div style={{ fontSize: 11, color: "var(--vv-fg-muted)", marginTop: 4, lineHeight: 1.5, fontWeight: 500 }}>{row.sub}</div>
            </div>
          </div>
        ))}

        <div style={{ display: "flex", gap: 10, marginTop: 24, paddingTop: 18, borderTop: "1px solid var(--vv-border-subtle)" }}>
          <span style={{ color: "var(--vv-trust)", marginTop: 2 }}><Icon name="shield" size={14} strokeWidth={2}/></span>
          <div>
            <div style={{ fontSize: 13, fontWeight: 800, color: "var(--vv-fg)" }}>Modun her zaman belli olur</div>
            <div style={{ fontSize: 11, color: "var(--vv-fg-muted)", lineHeight: 1.6, fontWeight: 500, marginTop: 2 }}>
              Hizmet veren panelindeyken ayrı başlık ve ayrı menü görürsün. İstediğin zaman üye moduna dönebilirsin.
            </div>
          </div>
        </div>
      </div>

      <div style={{
        padding: "16px 24px",
        background: "rgba(255,251,245,0.9)",
        backdropFilter: "blur(8px)",
        borderTop: "1px solid var(--vv-border-faint)",
      }}>
        <PrimaryButton fullWidth size="lg" onClick={onStart}>Hizmet vermeye başla</PrimaryButton>
        <p style={{ fontSize: 10, color: "var(--vv-fg-muted)", textAlign: "center", marginTop: 10, fontWeight: 500 }}>
          İstersen daha sonra profilinden tekrar devam edebilirsin.
        </p>
        <div style={{ display: "flex", justifyContent: "center", marginTop: 4 }}>
          <GhostButton onClick={onBack}>Şimdilik geç</GhostButton>
        </div>
      </div>
    </div>
  );
};

window.VVProviderWelcome = VVProviderWelcome;
