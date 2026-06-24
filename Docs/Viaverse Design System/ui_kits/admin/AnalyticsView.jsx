/* @jsx React.createElement */
const AnalyticsView = () => {
  const kpis = [
    { l: "Aktif kullanıcı (DAU)", n: "42.380", delta: "+5.2%", up: true },
    { l: "Yeni paylaşımlar",      n: "1.207",  delta: "+12.1%", up: true },
    { l: "Tamamlanan iş",         n: "284",    delta: "-1.8%",  up: false },
    { l: "Ortalama iş tutarı",    n: "742 ₺",  delta: "+3.4%",  up: true },
  ];

  return (
    <div>
      <div style={{ marginBottom: 18 }}>
        <div style={{ fontSize: 11, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.15em", marginBottom: 6 }}>Finans & ölçüm</div>
        <h1 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 26, letterSpacing: "-0.02em", margin: 0, color: "var(--vv-fg-strong)" }}>Analitik</h1>
        <p style={{ fontSize: 12, color: "var(--vv-fg-muted)", margin: "6px 0 0" }}>Son 7 gün — saat başı yenileniyor.</p>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: 12, marginBottom: 18 }}>
        {kpis.map((k) => (
          <div key={k.l} style={{
            background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
            borderRadius: 14, padding: 18,
          }}>
            <div style={{ fontSize: 11, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.1em" }}>{k.l}</div>
            <div style={{ display: "flex", alignItems: "baseline", gap: 10, marginTop: 8 }}>
              <div style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 26, color: "var(--vv-fg-strong)", letterSpacing: "-0.02em" }}>{k.n}</div>
              <div style={{
                fontSize: 11, fontWeight: 800,
                color: k.up ? "#10B981" : "#EF4444",
              }}>{k.delta}</div>
            </div>
          </div>
        ))}
      </div>

      <div style={{
        background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
        borderRadius: 14, padding: 20, marginBottom: 16,
      }}>
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 16 }}>
          <div style={{ fontSize: 13, fontWeight: 800, color: "var(--vv-fg)" }}>Günlük aktif kullanıcılar · son 14 gün</div>
          <div style={{ display: "flex", gap: 6 }}>
            {["1G","7G","14G","30G"].map((p, i) => (
              <button key={p} style={{
                fontSize: 11, fontWeight: 700, padding: "4px 10px",
                background: i === 2 ? "var(--vv-primary-soft)" : "var(--vv-surface-muted)",
                color: i === 2 ? "var(--vv-primary)" : "var(--vv-fg-muted)",
                border: "none", borderRadius: 999, cursor: "pointer",
              }}>{p}</button>
            ))}
          </div>
        </div>
        <Chart/>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14 }}>
        <div style={{
          background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
          borderRadius: 14, padding: 18,
        }}>
          <div style={{ fontSize: 13, fontWeight: 800, marginBottom: 12 }}>En çok talep edilen kategoriler</div>
          {[
            ["home_repair", "Ev, Tamirat & Tadilat", 86, "#F59E0B"],
            ["cleaning", "Temizlik & Düzenleme",     72, "#06B6D4"],
            ["pets", "Evcil Hayvan",                  61, "#F97316"],
            ["logistics", "Lojistik & Paket",         54, "#10B981"],
            ["digital_software", "Dijital & Yazılım", 48, "#3B82F6"],
          ].map(([icon, name, n, col]) => (
            <div key={icon} style={{ display: "flex", alignItems: "center", gap: 10, padding: "8px 0" }}>
              <img src={`../../assets/categories/${icon}.png`} alt="" style={{ width: 24, height: 24 }}/>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 12, fontWeight: 700, color: "var(--vv-fg)" }}>{name}</div>
                <div style={{ height: 4, borderRadius: 4, background: "var(--vv-surface-muted)", marginTop: 4, overflow: "hidden" }}>
                  <div style={{ width: `${n}%`, height: "100%", background: col }}/>
                </div>
              </div>
              <div style={{ fontSize: 11, fontWeight: 800, color: "var(--vv-fg-muted)", minWidth: 30, textAlign: "right" }}>{n}%</div>
            </div>
          ))}
        </div>

        <div style={{
          background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
          borderRadius: 14, padding: 18,
        }}>
          <div style={{ fontSize: 13, fontWeight: 800, marginBottom: 12 }}>Doğrulama tamamlama oranı</div>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "center", padding: "16px 0" }}>
            <Donut value={87}/>
          </div>
          <div style={{ display: "flex", justifyContent: "space-around", borderTop: "1px solid var(--vv-border-faint)", paddingTop: 14 }}>
            <Stat n="2.418" l="Toplam başvuru"/>
            <Stat n="2.103" l="Tamamlandı" color="#10B981"/>
            <Stat n="315" l="Yarım kaldı"  color="#F59E0B"/>
          </div>
        </div>
      </div>
    </div>
  );
};

function Chart() {
  const data = [42, 48, 52, 47, 55, 61, 58, 64, 70, 66, 72, 78, 75, 82];
  const w = 800, h = 180, pad = 12;
  const max = Math.max(...data), min = Math.min(...data);
  const pts = data.map((v, i) => [
    pad + (i * (w - pad * 2)) / (data.length - 1),
    h - pad - ((v - min) / (max - min)) * (h - pad * 2),
  ]);
  const linePath = pts.map(([x, y], i) => `${i === 0 ? "M" : "L"}${x.toFixed(1)} ${y.toFixed(1)}`).join(" ");
  const areaPath = linePath + ` L${pts[pts.length-1][0]} ${h-pad} L${pts[0][0]} ${h-pad} Z`;
  return (
    <svg viewBox={`0 0 ${w} ${h}`} style={{ width: "100%", display: "block" }}>
      <defs>
        <linearGradient id="grad" x1="0" x2="0" y1="0" y2="1">
          <stop offset="0%"  stopColor="#F97316" stopOpacity="0.25"/>
          <stop offset="100%" stopColor="#F97316" stopOpacity="0"/>
        </linearGradient>
      </defs>
      <path d={areaPath} fill="url(#grad)"/>
      <path d={linePath} fill="none" stroke="#F97316" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      {pts.map((p, i) => i === pts.length - 1 && (
        <circle key={i} cx={p[0]} cy={p[1]} r="5" fill="#F97316" stroke="#fff" strokeWidth="2"/>
      ))}
    </svg>
  );
}

function Donut({ value }) {
  const r = 56, c = 2 * Math.PI * r;
  const offset = c - (value / 100) * c;
  return (
    <div style={{ position: "relative", width: 140, height: 140 }}>
      <svg viewBox="0 0 140 140" style={{ width: "100%", height: "100%", transform: "rotate(-90deg)" }}>
        <circle cx="70" cy="70" r={r} fill="none" stroke="var(--vv-surface-muted)" strokeWidth="14"/>
        <circle cx="70" cy="70" r={r} fill="none" stroke="#10B981" strokeWidth="14"
          strokeDasharray={c} strokeDashoffset={offset} strokeLinecap="round"/>
      </svg>
      <div style={{
        position: "absolute", inset: 0,
        display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center",
      }}>
        <div style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 28, letterSpacing: "-0.02em", color: "var(--vv-fg-strong)" }}>%{value}</div>
        <div style={{ fontSize: 10, fontWeight: 700, color: "var(--vv-fg-muted)" }}>tamamlanma</div>
      </div>
    </div>
  );
}

function Stat({ n, l, color }) {
  return (
    <div style={{ textAlign: "center" }}>
      <div style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 18, color: color || "var(--vv-fg-strong)", letterSpacing: "-0.02em" }}>{n}</div>
      <div style={{ fontSize: 10, fontWeight: 700, color: "var(--vv-fg-muted)", marginTop: 2 }}>{l}</div>
    </div>
  );
}

window.AnalyticsView = AnalyticsView;
