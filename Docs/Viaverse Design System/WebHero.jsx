/* @jsx React.createElement */
const WebHero = ({ onApp }) => {
  return (
    <section style={{
      padding: "72px 56px 56px",
      display: "grid", gridTemplateColumns: "1fr 1.1fr", gap: 56, alignItems: "center",
      position: "relative"
    }}>
      <div>
        <div style={{
          display: "inline-flex", alignItems: "center", gap: 8,
          padding: "5px 14px", borderRadius: 999,
          background: "rgba(16,185,129,0.1)", border: "1px solid rgba(16,185,129,0.2)",
          fontSize: 11, fontWeight: 800, color: "#10B981",
          textTransform: "uppercase", letterSpacing: "0.12em",
          marginBottom: 24
        }}>
          <span style={{ width: 6, height: 6, borderRadius: "50%", background: "#10B981" }} />
          Şu an çevrende olanlar
        </div>
        <h1 style={{
          fontFamily: "var(--vv-font-display)", fontWeight: 800,
          fontSize: 60, lineHeight: 1.18, letterSpacing: "-0.03em",
          color: "var(--vv-fg-strong)", margin: 0
        }}>
          Yakında <span style={{ color: "var(--vv-primary)" }}>küçük bir yardım</span>.<br />
          İyi yapılmış <span style={{ color: "var(--vv-trust-deep)" }}>büyük bir iş</span>.
        </h1>
        <p style={{
          fontSize: 17, color: "var(--vv-fg-muted)", lineHeight: 1.65,
          margin: "24px 0 32px", maxWidth: 540, fontWeight: 500
        }}>
          Bir el iste, çevrende olanları paylaş, küçük işlere teklif al — ya da kendi yeteneğini
          işe dönüştür. Viaverse, tam olarak nerede olduğunla bağlantılı tek yer.
        </p>
        <div style={{ display: "flex", gap: 14, alignItems: "center", flexWrap: "wrap" }}>
          <button onClick={onApp} style={{
            background: "var(--vv-primary)", color: "var(--vv-primary-foreground)", border: "none",
            padding: "16px 28px", borderRadius: 14, cursor: "pointer",
            fontFamily: "var(--vv-font-sans)", fontSize: 15, fontWeight: 800,
            letterSpacing: "-0.01em", boxShadow: "var(--vv-shadow-cta)"
          }}>Hesap oluştur — ücretsiz</button>
          <button style={{
            background: "var(--vv-surface)", color: "var(--vv-fg)",
            border: "1px solid var(--vv-border-muted)",
            padding: "16px 24px", borderRadius: 14, cursor: "pointer",
            fontFamily: "var(--vv-font-sans)", fontSize: 14, fontWeight: 700
          }}>Hizmet vermeye başla →</button>
        </div>

        <div style={{ display: "flex", gap: 32, marginTop: 48 }}>
          {[
          { n: "240k+", l: "yakınında aktif" },
          { n: "12k", l: "doğrulanmış usta" },
          { n: "%97", l: "geri dönüş oranı" }].
          map((s) =>
          <div key={s.l}>
              <div style={{
              fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 28,
              color: "var(--vv-fg-strong)", letterSpacing: "-0.02em"
            }}>{s.n}</div>
              <div style={{ fontSize: 12, color: "var(--vv-fg-muted)", fontWeight: 600, marginTop: 2 }}>{s.l}</div>
            </div>
          )}
        </div>
      </div>

      <div style={{
        position: "relative",
        display: "grid",
        gridTemplateColumns: "1.2fr 1fr",
        gridTemplateRows: "1fr 1fr",
        gap: 14,
        height: 520,
        paddingBottom: 56 /* breathing room for the floating V */
      }}>
        <img src="https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=900&q=80"
        alt="" style={{
          gridColumn: "1", gridRow: "1 / 3",
          width: "100%", height: "100%", objectFit: "cover", borderRadius: 28,
          boxShadow: "var(--vv-shadow-lg)"
        }} />
        <img src="https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=600&q=80"
        alt="" style={{
          gridColumn: "2", gridRow: "1",
          width: "100%", height: "100%", objectFit: "cover", borderRadius: 22,
          boxShadow: "var(--vv-shadow-md)"
        }} />
        <img src="https://images.unsplash.com/photo-1521737604893-d14cc237f11d?auto=format&fit=crop&w=600&q=80"
        alt="" style={{
          gridColumn: "2", gridRow: "2",
          width: "100%", height: "100%", objectFit: "cover", borderRadius: 22,
          boxShadow: "var(--vv-shadow-md)"
        }} />

        {/* V mark sits at the bottom-left corner of the photo grid */}
        <img src="../../assets/viaverse_icon.png" alt="Viaverse"
        style={{
          position: "absolute", left: -64, bottom: -24,
          zIndex: 2,
          filter: "drop-shadow(0 22px 28px rgba(124,45,18,0.32)) drop-shadow(0 6px 10px rgba(15,23,42,0.18))", height: "124px", width: "124px", objectFit: "contain"
        }} />
      </div>
    </section>);

};

window.WebHero = WebHero;