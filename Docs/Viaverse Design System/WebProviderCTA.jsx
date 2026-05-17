/* @jsx React.createElement */
const WebProviderCTA = () => {
  return (
    <section style={{
      margin: "0 56px 72px", borderRadius: 32,
      background: "linear-gradient(120deg, #022C22 0%, #064E3B 70%, #0A3A33 100%)",
      padding: "64px 56px",
      display: "grid", gridTemplateColumns: "1fr 1fr", gap: 48, alignItems: "center",
      position: "relative", overflow: "hidden"
    }}>
      <div style={{
        position: "absolute", inset: 0,
        background: "radial-gradient(40% 80% at 100% 0%, rgba(249,115,22,0.25), transparent 60%)"
      }} />
      <div style={{ position: "relative", zIndex: 1 }}>
        <div style={{
          display: "inline-flex", alignItems: "center", gap: 8,
          padding: "5px 14px", borderRadius: 999,
          background: "rgba(16,185,129,0.15)", border: "1px solid rgba(16,185,129,0.3)",
          fontSize: 11, fontWeight: 800, color: "#34D399",
          textTransform: "uppercase", letterSpacing: "0.12em",
          marginBottom: 24
        }}>
          <span style={{ width: 6, height: 6, borderRadius: "50%", background: "#10B981" }} />
          Hizmet veren modu
        </div>
        <h2 style={{
          fontFamily: "var(--vv-font-display)", fontWeight: 800,
          fontSize: 44, lineHeight: 1.1, letterSpacing: "-0.02em",
          color: "#FFFBF5", margin: 0
        }}>
          Küçük yükleri paylaşmak, <span style={{ color: "var(--vv-primary)" }}>emeğe hak ettiği değeri</span> katmak için.
        </h2>
        <p style={{ fontSize: 16, color: "rgba(255,251,245,0.7)", lineHeight: 1.65, margin: "20px 0 32px", fontWeight: 500, maxWidth: 540 }}>
          Yeteneğini, deneyimini ya da işletmeni Viaverse'de görünür yap. Gelen talepleri yönet,
          teklif ver ve aktif işlerini tek yerden takip et.
        </p>
        <button style={{
          background: "var(--vv-primary)", color: "var(--vv-primary-foreground)", border: "none",
          padding: "16px 28px", borderRadius: 14, cursor: "pointer",
          fontFamily: "var(--vv-font-sans)", fontSize: 15, fontWeight: 800,
          letterSpacing: "-0.01em", boxShadow: "var(--vv-shadow-cta)"
        }}>Hizmet vermeye başla</button>
      </div>
      <div style={{ position: "relative", zIndex: 1 }}>
        <img
          src="https://images.unsplash.com/photo-1556761175-b413da4baf72?auto=format&fit=crop&w=900&q=80"
          alt="" style={{
            width: "100%", borderRadius: 24, boxShadow: "0 30px 60px rgba(0,0,0,0.35)",
            objectFit: "cover", aspectRatio: "5/4", display: "block"
          }} />
        {/* Reversed (silver/green) V — sits at bottom-left of image */}
        <img src="../../assets/viaverse_icon_silver_green.png" alt="Viaverse"
        style={{
          position: "absolute", left: -32, bottom: -32,
          height: 120,
          filter: "drop-shadow(0 24px 36px rgba(0,0,0,0.55)) drop-shadow(0 8px 14px rgba(0,0,0,0.4))", margin: "2110px 1.11111e+06px 0px 0px", opacity: "1", width: "120px", objectFit: "contain"
        }} />
      </div>
    </section>);

};

window.WebProviderCTA = WebProviderCTA;