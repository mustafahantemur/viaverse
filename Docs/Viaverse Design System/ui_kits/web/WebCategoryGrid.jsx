/* @jsx React.createElement */
const WebCategoryGrid = () => {
  const cats = [
    { icon: "home_repair",             name: "Ev, Tamirat & Tadilat",         sub: "Boya · Tesisat · Elektrik" },
    { icon: "digital_software",        name: "Dijital & Yazılım",             sub: "Web · Mobil · Veri" },
    { icon: "creative_media",          name: "Yaratıcı İşler & Medya",        sub: "Logo · Video · Foto" },
    { icon: "education",               name: "Eğitim & Mentorluk",            sub: "Ders · Sınav · Koçluk" },
    { icon: "cleaning",                name: "Temizlik & Düzenleme",          sub: "Ev · Ofis · Halı" },
    { icon: "logistics",               name: "Lojistik & Paket",              sub: "Kurye · Nakliye · Market" },
    { icon: "care_health",             name: "Kişisel Bakım & Sağlık",        sub: "Kuaför · Masaj · Spor" },
    { icon: "professional_consulting", name: "Profesyonel & Danışmanlık",     sub: "Hukuk · Muhasebe · İK" },
    { icon: "pets",                    name: "Evcil Hayvan",                  sub: "Gezdirme · Bakım · Vet" },
    { icon: "events",                  name: "Etkinlik & Organizasyon",       sub: "Düğün · Parti · DJ" },
  ];
  return (
    <section style={{ padding: "72px 56px" }}>
      <div style={{ display: "flex", alignItems: "flex-end", justifyContent: "space-between", marginBottom: 36 }}>
        <div>
          <div style={{ fontSize: 11, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.2em", marginBottom: 10 }}>
            Hizmetler
          </div>
          <h2 style={{
            fontFamily: "var(--vv-font-display)", fontWeight: 800,
            fontSize: 40, lineHeight: 1.1, letterSpacing: "-0.02em",
            color: "var(--vv-fg-strong)", margin: 0, maxWidth: 720,
          }}>
            Yükünüzü hafifletin, <span style={{ color: "var(--vv-primary)" }}>emeği ödüllendirin</span>.
          </h2>
        </div>
        <a href="#" style={{
          color: "var(--vv-fg)", textDecoration: "none",
          fontSize: 14, fontWeight: 700,
          borderBottom: "2px solid var(--vv-primary)", paddingBottom: 2,
        }}>Tüm kategoriler →</a>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(5, 1fr)", gap: 14 }}>
        {cats.map((c) => (
          <div key={c.icon} style={{
            background: "var(--vv-surface)",
            border: "1px solid var(--vv-border-subtle)",
            borderRadius: 18, padding: "22px 18px",
            display: "flex", flexDirection: "column", gap: 12,
            transition: "transform 200ms, box-shadow 200ms",
            cursor: "pointer",
          }}
          onMouseEnter={(e) => { e.currentTarget.style.transform = "translateY(-2px)"; e.currentTarget.style.boxShadow = "var(--vv-shadow-md)"; }}
          onMouseLeave={(e) => { e.currentTarget.style.transform = ""; e.currentTarget.style.boxShadow = ""; }}
          >
            <img src={`../../assets/categories/${c.icon}.png`} alt=""
              style={{ width: 56, height: 56, objectFit: "contain" }}/>
            <div>
              <div style={{ fontSize: 14, fontWeight: 800, color: "var(--vv-fg-strong)", letterSpacing: "-0.01em" }}>{c.name}</div>
              <div style={{ fontSize: 11, color: "var(--vv-fg-muted)", marginTop: 4, fontWeight: 500 }}>{c.sub}</div>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};

window.WebCategoryGrid = WebCategoryGrid;
