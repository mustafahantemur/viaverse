/* @jsx React.createElement */
const VerificationView = () => {
  const rows = [
    { id: "VRF-1182", name: "Ahmet Yılmaz",   kind: "Worker",   cat: "Tesisat",         submitted: "Bugün · 09:22", status: "pending", risk: "low",    docs: 3 },
    { id: "VRF-1181", name: "Cansu Karaca",   kind: "Worker",   cat: "Köpek gezdirme",  submitted: "Bugün · 08:54", status: "pending", risk: "low",    docs: 2 },
    { id: "VRF-1180", name: "Temizleller LTD",kind: "Business", cat: "Temizlik",        submitted: "Dün · 19:01",   status: "pending", risk: "medium", docs: 6 },
    { id: "VRF-1179", name: "Mehmet Kaya",    kind: "Worker",   cat: "Elektrik",        submitted: "Dün · 14:30",   status: "review",  risk: "high",   docs: 4 },
    { id: "VRF-1178", name: "Avukat&Co",      kind: "Business", cat: "Hukuk",           submitted: "Dün · 11:15",   status: "approved",risk: "low",    docs: 8 },
    { id: "VRF-1177", name: "Ayşe Demir",     kind: "Worker",   cat: "Eğitim",          submitted: "Pzt · 16:42",   status: "rejected",risk: "high",   docs: 3 },
  ];
  const statusStyle = {
    pending:  { fg: "#F59E0B", bg: "rgba(245,158,11,0.12)", label: "Bekliyor" },
    review:   { fg: "#3B82F6", bg: "rgba(59,130,246,0.12)", label: "İncele" },
    approved: { fg: "#10B981", bg: "rgba(16,185,129,0.12)", label: "Onaylı" },
    rejected: { fg: "#EF4444", bg: "rgba(239,68,68,0.12)",  label: "Red" },
  };
  const riskDot = { low: "#10B981", medium: "#F59E0B", high: "#EF4444" };

  return (
    <div>
      <div style={{ display: "flex", alignItems: "flex-end", justifyContent: "space-between", marginBottom: 18 }}>
        <div>
          <div style={{ fontSize: 11, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.15em", marginBottom: 6 }}>Operasyon</div>
          <h1 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 26, letterSpacing: "-0.02em", margin: 0, color: "var(--vv-fg-strong)" }}>Doğrulama kuyruğu</h1>
          <p style={{ fontSize: 12, color: "var(--vv-fg-muted)", margin: "6px 0 0" }}>12 sıradaki başvuru — ortalama bekleme 18 dk.</p>
        </div>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: 12, marginBottom: 18 }}>
        {[
          { l: "Bekleyen",          n: "12", c: "#F59E0B" },
          { l: "Bugün onaylanan",   n: "47", c: "#10B981" },
          { l: "Bugün reddedilen",  n: "3",  c: "#EF4444" },
          { l: "Ortalama süre",     n: "18 dk", c: "var(--vv-fg)" },
        ].map((k) => (
          <div key={k.l} style={{
            background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
            borderRadius: 12, padding: 16,
          }}>
            <div style={{ fontSize: 10, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.15em" }}>{k.l}</div>
            <div style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 26, color: k.c, letterSpacing: "-0.02em", marginTop: 4 }}>{k.n}</div>
          </div>
        ))}
      </div>

      <div style={{ background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)", borderRadius: 14, overflow: "hidden" }}>
        <div style={{
          display: "flex", alignItems: "center", gap: 10,
          padding: "12px 14px", borderBottom: "1px solid var(--vv-border-subtle)",
          background: "var(--vv-surface)",
        }}>
          {["Tümü (12)", "Worker (8)", "Business (4)", "Yüksek risk (1)"].map((t, i) => (
            <button key={t} style={{
              height: 28, padding: "0 12px", borderRadius: 999,
              fontSize: 11, fontWeight: 700,
              background: i === 0 ? "var(--vv-primary-soft)" : "var(--vv-surface-muted)",
              color: i === 0 ? "var(--vv-primary)" : "var(--vv-fg-muted)",
              border: "1px solid " + (i === 0 ? "rgba(249,115,22,0.25)" : "transparent"),
              cursor: "pointer",
            }}>{t}</button>
          ))}
          <div style={{ flex: 1 }}/>
          <button style={{
            height: 28, padding: "0 12px", borderRadius: 999,
            fontSize: 11, fontWeight: 700, background: "var(--vv-surface-muted)",
            color: "var(--vv-fg-muted)", border: "1px solid var(--vv-border-subtle)", cursor: "pointer",
          }}>↓ En yeni</button>
        </div>

        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 12 }}>
          <thead>
            <tr style={{ background: "var(--vv-surface-muted)", textAlign: "left" }}>
              <th style={th(48)}><input type="checkbox" /></th>
              <th style={th()}>ID</th>
              <th style={th()}>İsim</th>
              <th style={th()}>Tip</th>
              <th style={th()}>Kategori</th>
              <th style={th()}>Risk</th>
              <th style={th()}>Belgeler</th>
              <th style={th()}>Gönderilme</th>
              <th style={th()}>Durum</th>
              <th style={th()}/>
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => {
              const s = statusStyle[r.status];
              return (
                <tr key={r.id} style={{ borderTop: "1px solid var(--vv-border-faint)" }}>
                  <td style={td()}><input type="checkbox" /></td>
                  <td style={td()}><code style={{ fontSize: 11, color: "var(--vv-fg-muted)" }}>{r.id}</code></td>
                  <td style={{ ...td(), fontWeight: 700, color: "var(--vv-fg)" }}>{r.name}</td>
                  <td style={td()}>{r.kind}</td>
                  <td style={td()}>{r.cat}</td>
                  <td style={td()}>
                    <span style={{ display: "inline-flex", alignItems: "center", gap: 6 }}>
                      <span style={{ width: 7, height: 7, borderRadius: "50%", background: riskDot[r.risk] }}/>
                      <span style={{ textTransform: "capitalize" }}>{r.risk}</span>
                    </span>
                  </td>
                  <td style={td()}>{r.docs}</td>
                  <td style={td()}>{r.submitted}</td>
                  <td style={td()}>
                    <span style={{
                      padding: "3px 10px", borderRadius: 999,
                      background: s.bg, color: s.fg, fontSize: 10, fontWeight: 800,
                      textTransform: "uppercase", letterSpacing: "0.05em",
                    }}>{s.label}</span>
                  </td>
                  <td style={{ ...td(), textAlign: "right" }}>
                    <button style={{
                      padding: "4px 10px", border: "1px solid var(--vv-border-muted)",
                      background: "var(--vv-surface)", borderRadius: 6,
                      fontSize: 11, fontWeight: 700, cursor: "pointer",
                    }}>Aç →</button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );

  function th(w) { return {
    fontSize: 10, fontWeight: 800, color: "var(--vv-fg-muted)",
    textTransform: "uppercase", letterSpacing: "0.1em",
    padding: "10px 14px", width: w,
  };}
  function td() { return { padding: "12px 14px", color: "var(--vv-fg)", verticalAlign: "middle" }; }
};

window.VerificationView = VerificationView;
