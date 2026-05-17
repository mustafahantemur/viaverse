/* @jsx React.createElement */
const WebFooter = () => {
  const cols = [
    { t: "Hizmetler", links: ["Ev & Tamirat", "Dijital", "Kişisel Bakım", "Lojistik", "Tüm kategoriler"] },
    { t: "Viaverse",  links: ["Hakkımızda", "Kariyer", "Basın", "Blog", "İletişim"] },
    { t: "Hizmet veren", links: ["Hizmet ver", "Ücretler & komisyon", "Doğrulama", "Başarı hikayeleri"] },
    { t: "Yardım",    links: ["Destek merkezi", "Güven & güvenlik", "Topluluk kuralları", "İade & uyuşmazlık"] },
  ];
  return (
    <footer style={{
      padding: "64px 56px 32px",
      background: "var(--vv-surface)",
      borderTop: "1px solid var(--vv-border-subtle)",
    }}>
      <div style={{ display: "grid", gridTemplateColumns: "1.4fr repeat(4, 1fr)", gap: 40, marginBottom: 48 }}>
        <div>
          <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 16 }}>
            <img src="../../assets/viaverse_icon.png" alt="" style={{ width: 36, height: 36 }}/>
            <img src="../../assets/viaverse_wordmark.png" alt="Viaverse" style={{ height: 22 }}/>
          </div>
          <p style={{ fontSize: 13, color: "var(--vv-fg-muted)", lineHeight: 1.65, fontWeight: 500, margin: 0, maxWidth: 280 }}>
            Küçük yükleri paylaşmak, emeğe hak ettiği değeri katmak için. Çevrendeki yardım, duyuru,
            küçük iş ve profesyonel hizmetler — hepsi tek bir yerde.
          </p>
          <div style={{ display: "flex", gap: 10, marginTop: 24 }}>
            {["App Store", "Google Play"].map((s) => (
              <button key={s} style={{
                background: "var(--vv-fg-strong)", color: "#FFFBF5",
                border: "none", padding: "10px 16px", borderRadius: 12,
                fontSize: 12, fontWeight: 700, cursor: "pointer",
              }}>{s}</button>
            ))}
          </div>
        </div>
        {cols.map((c) => (
          <div key={c.t}>
            <div style={{
              fontSize: 11, fontWeight: 800, color: "var(--vv-fg-muted)",
              textTransform: "uppercase", letterSpacing: "0.15em", marginBottom: 14,
            }}>{c.t}</div>
            <ul style={{ listStyle: "none", margin: 0, padding: 0, display: "flex", flexDirection: "column", gap: 10 }}>
              {c.links.map((l) => (
                <li key={l}>
                  <a href="#" style={{
                    fontSize: 13, color: "var(--vv-fg)", textDecoration: "none", fontWeight: 500,
                  }}>{l}</a>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>
      <div style={{
        display: "flex", alignItems: "center", justifyContent: "space-between",
        paddingTop: 24, borderTop: "1px solid var(--vv-border-subtle)",
        fontSize: 11, color: "var(--vv-fg-muted)", fontWeight: 500,
      }}>
        <div>© 2026 Viaverse · Sizin için küçük bir iş, bir başkası için büyük bir nefes.</div>
        <div style={{ display: "flex", gap: 20 }}>
          <a href="#" style={{ color: "var(--vv-fg-muted)", textDecoration: "none" }}>Gizlilik</a>
          <a href="#" style={{ color: "var(--vv-fg-muted)", textDecoration: "none" }}>Kullanım koşulları</a>
          <a href="#" style={{ color: "var(--vv-fg-muted)", textDecoration: "none" }}>Çerezler</a>
          <a href="#" style={{ color: "var(--vv-fg-muted)", textDecoration: "none" }}>🌍 Türkçe</a>
        </div>
      </div>
    </footer>
  );
};

window.WebFooter = WebFooter;
