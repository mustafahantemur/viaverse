/* @jsx React.createElement */
const WebHowItWorks = () => {
  const steps = [
    { n: "01", t: "Hesabını oluştur",      d: "Telefon ya da Google ile saniyeler içinde başla — kart bilgisi gerekmez.",
      img: "https://images.unsplash.com/photo-1556761175-5973dc0f32e7?auto=format&fit=crop&w=900&q=80" },
    { n: "02", t: "Konumunu seç",          d: "Sadece çevrendeki paylaşımları, hizmetleri ve insanları görürsün.",
      img: "https://images.unsplash.com/photo-1502920917128-1aa500764cbd?auto=format&fit=crop&w=900&q=80" },
    { n: "03", t: "Yardım iste, iş yap",   d: "Bir el ister, doğrulanmış ustadan teklif al ya da küçük işlerle gününe değer kat.",
      img: "https://images.unsplash.com/photo-1521737852567-6949f3f9f2b5?auto=format&fit=crop&w=900&q=80" },
  ];
  return (
    <section style={{ padding: "72px 56px" }}>
      <div style={{ textAlign: "center", marginBottom: 48 }}>
        <div style={{ fontSize: 11, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.2em", marginBottom: 10 }}>
          Nasıl çalışır
        </div>
        <h2 style={{
          fontFamily: "var(--vv-font-display)", fontWeight: 800,
          fontSize: 40, lineHeight: 1.1, letterSpacing: "-0.02em",
          color: "var(--vv-fg-strong)", margin: 0,
        }}>Üç adımda çevrenle bağ kur.</h2>
      </div>
      <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 18 }}>
        {steps.map((s) => (
          <div key={s.n} style={{
            background: "var(--vv-surface)",
            border: "1px solid var(--vv-border-subtle)",
            borderRadius: 24, overflow: "hidden",
            display: "flex", flexDirection: "column",
            boxShadow: "var(--vv-shadow-sm)",
          }}>
            <div style={{
              height: 180,
              backgroundImage: `url(${s.img})`,
              backgroundSize: "cover", backgroundPosition: "center",
              position: "relative",
            }}>
              <div style={{
                position: "absolute", top: 14, left: 14,
                padding: "5px 11px", borderRadius: 999,
                background: "rgba(255,251,245,0.94)", backdropFilter: "blur(6px)",
                fontSize: 11, fontWeight: 800, color: "var(--vv-primary)",
                letterSpacing: "0.02em",
              }}>{s.n}</div>
            </div>
            <div style={{ padding: "24px 28px 28px", display: "flex", flexDirection: "column", gap: 10 }}>
              <h3 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 22, margin: 0, color: "var(--vv-fg-strong)", letterSpacing: "-0.02em" }}>{s.t}</h3>
              <p style={{ fontSize: 14, color: "var(--vv-fg-muted)", lineHeight: 1.6, margin: 0, fontWeight: 500 }}>{s.d}</p>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};

window.WebHowItWorks = WebHowItWorks;
