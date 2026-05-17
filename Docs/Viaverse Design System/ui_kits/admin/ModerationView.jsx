/* @jsx React.createElement */
const ModerationView = () => {
  const reports = [
    { id: "RPT-822", subject: "Spam ilan paylaşımı",     reporter: "@aysed", target: "Kemal_S88", type: "Post",      severity: "low",    time: "12m" },
    { id: "RPT-821", subject: "Sohbette iletişim ihlali",reporter: "@mertk", target: "Provider Ali",  type: "Chat",      severity: "high",   time: "1h" },
    { id: "RPT-820", subject: "Yanıltıcı kategori",      reporter: "@selino",target: "Temizleller LTD",type: "Business", severity: "medium", time: "3h" },
    { id: "RPT-819", subject: "Taciz şikayeti",          reporter: "@ahmety",target: "Cansu B.",      type: "Profile",   severity: "high",   time: "1g" },
  ];
  const sevTint = { low: "#F59E0B", medium: "#3B82F6", high: "#EF4444" };

  return (
    <div>
      <div style={{ marginBottom: 18 }}>
        <div style={{ fontSize: 11, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.15em", marginBottom: 6 }}>Operasyon</div>
        <h1 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 26, letterSpacing: "-0.02em", margin: 0, color: "var(--vv-fg-strong)" }}>Moderasyon</h1>
        <p style={{ fontSize: 12, color: "var(--vv-fg-muted)", margin: "6px 0 0" }}>4 yeni rapor — 1 yüksek öncelik.</p>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
        {reports.map((r) => (
          <div key={r.id} style={{
            background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
            borderRadius: 14, padding: 16,
            borderLeft: `3px solid ${sevTint[r.severity]}`,
          }}>
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 8 }}>
              <code style={{ fontSize: 11, color: "var(--vv-fg-muted)" }}>{r.id}</code>
              <span style={{
                padding: "3px 10px", borderRadius: 999, fontSize: 10, fontWeight: 800,
                background: sevTint[r.severity] + "1F", color: sevTint[r.severity],
                textTransform: "uppercase", letterSpacing: "0.05em",
              }}>{r.severity}</span>
            </div>
            <div style={{ fontSize: 14, fontWeight: 800, color: "var(--vv-fg-strong)", marginBottom: 4 }}>{r.subject}</div>
            <div style={{ fontSize: 12, color: "var(--vv-fg-muted)", marginBottom: 14 }}>
              {r.reporter} → <strong>{r.target}</strong> · {r.type} · {r.time} önce
            </div>
            <div style={{ display: "flex", gap: 8 }}>
              <button style={{
                background: "var(--vv-primary)", color: "var(--vv-primary-foreground)", border: "none",
                padding: "8px 16px", borderRadius: 8, cursor: "pointer",
                fontSize: 12, fontWeight: 800,
              }}>İncele</button>
              <button style={{
                background: "var(--vv-surface-muted)", color: "var(--vv-fg)",
                border: "1px solid var(--vv-border-muted)",
                padding: "8px 14px", borderRadius: 8, cursor: "pointer",
                fontSize: 12, fontWeight: 700,
              }}>Yok say</button>
              <div style={{ marginLeft: "auto", display: "flex", alignItems: "center", color: "var(--vv-fg-muted)", fontSize: 11 }}>
                Atanan: Selin O.
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

window.ModerationView = ModerationView;
