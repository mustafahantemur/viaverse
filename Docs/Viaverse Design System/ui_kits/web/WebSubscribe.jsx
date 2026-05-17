/* @jsx React.createElement */
const WebSubscribe = () => {
  const [email, setEmail] = React.useState("");
  const [done, setDone] = React.useState(false);
  return (
    <section style={{ padding: "72px 56px 32px" }}>
      <div style={{
        background: "linear-gradient(135deg, #FFFBF5 0%, #FCEFD9 100%)",
        border: "1px solid var(--vv-border-subtle)",
        borderRadius: 28, padding: "44px 48px",
        display: "grid", gridTemplateColumns: "1fr 1fr", gap: 40, alignItems: "center",
        boxShadow: "var(--vv-shadow-sm)",
        position: "relative", overflow: "hidden",
      }}>
        <div style={{
          position: "absolute", right: -80, top: -80, width: 320, height: 320,
          borderRadius: "50%",
          background: "radial-gradient(closest-side, rgba(249,115,22,0.18), transparent 70%)",
          pointerEvents: "none",
        }}/>

        <div style={{ position: "relative" }}>
          <div style={{
            display: "inline-flex", alignItems: "center", gap: 8,
            padding: "5px 14px", borderRadius: 999,
            background: "rgba(249,115,22,0.10)", border: "1px solid rgba(249,115,22,0.22)",
            fontSize: 11, fontWeight: 800, color: "var(--vv-primary)",
            textTransform: "uppercase", letterSpacing: "0.12em",
            marginBottom: 18,
          }}>Haftalık bülten</div>
          <h2 style={{
            fontFamily: "var(--vv-font-display)", fontWeight: 800,
            fontSize: 30, lineHeight: 1.15, letterSpacing: "-0.02em",
            color: "var(--vv-fg-strong)", margin: 0,
          }}>
            Çevrendekileri kaçırma.
          </h2>
          <p style={{ fontSize: 14, color: "var(--vv-fg-muted)", lineHeight: 1.6, margin: "10px 0 0", fontWeight: 500, maxWidth: 420 }}>
            Yakındaki yeni hizmet verenler, ipuçları ve sade bir özet — haftada bir, posta kutuna.
          </p>
        </div>

        <form onSubmit={(e) => { e.preventDefault(); if (email) setDone(true); }} style={{ position: "relative" }}>
          {!done ? (
            <div style={{
              display: "flex", alignItems: "center", gap: 10,
              background: "var(--vv-surface)",
              border: "1px solid var(--vv-border-muted)",
              borderRadius: 16, padding: 6,
              boxShadow: "var(--vv-shadow-sm)",
            }}>
              <input type="email" required value={email} onChange={(e) => setEmail(e.target.value)}
                placeholder="ornek@viaverse.com"
                style={{
                  flex: 1, height: 44, padding: "0 16px",
                  background: "none", border: "none", outline: "none",
                  fontFamily: "var(--vv-font-sans)", fontSize: 14, color: "var(--vv-fg)",
                }}/>
              <button type="submit" style={{
                height: 44, padding: "0 22px",
                background: "var(--vv-primary)", color: "var(--vv-primary-foreground)",
                border: "none", borderRadius: 12, cursor: "pointer",
                fontFamily: "var(--vv-font-sans)", fontSize: 13, fontWeight: 800,
                letterSpacing: "-0.01em", boxShadow: "var(--vv-shadow-cta)",
              }}>Abone ol</button>
            </div>
          ) : (
            <div style={{
              display: "flex", alignItems: "center", gap: 12,
              padding: "16px 18px", borderRadius: 14,
              background: "rgba(16,185,129,0.12)",
              border: "1px solid rgba(16,185,129,0.25)",
            }}>
              <div style={{
                width: 28, height: 28, borderRadius: "50%",
                background: "#10B981", color: "#FFFBF5",
                display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0,
              }}>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
              </div>
              <div>
                <div style={{ fontSize: 13, fontWeight: 800, color: "var(--vv-trust-deep)" }}>Listede yer aldın</div>
                <div style={{ fontSize: 11, color: "var(--vv-fg-muted)" }}>{email} — onay e-postası gönderdik.</div>
              </div>
            </div>
          )}
          <p style={{ fontSize: 10, color: "var(--vv-fg-muted)", marginTop: 10, fontWeight: 500 }}>
            Yakındakilerden haberdar ol — haftada bir, kısa.
          </p>
        </form>
      </div>
    </section>
  );
};

window.WebSubscribe = WebSubscribe;
