/* @jsx React.createElement */
const WebWhy = () => {
  const cards = [
    {
      kicker: "Vizyon",
      tr: "Yükü paylaş, emeğin değerini katla.",
      en: "Sharing the load, valuing the effort.",
      body: "Viaverse, günlük hayatın küçük yüklerini insanlar arasında daha adil dağıtmak için var.",
    },
    {
      kicker: "Karşılaşma",
      tr: "Senin için küçük bir iş, başkası için büyük bir nefes.",
      en: "A small task for you, a big breath for someone else.",
      body: "Bazı işler küçük görünür ama insanı yorar. Bazı insanlar da küçük bir fırsatla nefes alır.",
    },
    {
      kicker: "Bağ",
      tr: "Yorucu işleri yeni fırsatlarla buluştururuz.",
      en: "Connecting heavy tasks with fresh opportunities.",
      body: "Bir tarafta el atılması gereken bir iş, diğer tarafta o işi yapacak doğru kişi. Viaverse aradaki bağı kurar.",
    },
    {
      kicker: "Hafiflik",
      tr: "Yükünü hafiflet, emeği ödüllendir.",
      en: "Lighten the burden, empower the effort.",
      body: "Yardım iste, talep aç, küçük işleri çevrendeki ustaya devret. Ne kadar verdiğini sen bilirsin.",
    },
  ];
  return (
    <section style={{ padding: "72px 56px" }}>
      <div style={{ textAlign: "center", maxWidth: 720, margin: "0 auto 48px" }}>
        <div style={{ fontSize: 11, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.2em", marginBottom: 10 }}>
          Neden Viaverse?
        </div>
        <h2 style={{
          fontFamily: "var(--vv-font-display)", fontWeight: 800,
          fontSize: 40, lineHeight: 1.1, letterSpacing: "-0.02em",
          color: "var(--vv-fg-strong)", margin: 0,
        }}>
          Bir reklam değil — bir <span style={{ color: "var(--vv-trust-deep)" }}>karşılaşma alanı</span>.
        </h2>
        <p style={{ fontSize: 15, color: "var(--vv-fg-muted)", lineHeight: 1.65, marginTop: 16, fontWeight: 500 }}>
          Bir tarafta küçük yükleri olan biri, diğer tarafta bir saatini değerlendirmek isteyen biri.
          Viaverse, gündelik bir el atışını adil bir alışverişe dönüştürür.
        </p>
      </div>

      <div style={{
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))",
        gap: 16,
      }}>
        {cards.map((c) => (
          <div key={c.kicker} style={{
            background: "var(--vv-surface)",
            border: "1px solid var(--vv-border-subtle)",
            borderRadius: 20, padding: "26px 26px 24px",
            display: "flex", flexDirection: "column", gap: 12,
            boxShadow: "var(--vv-shadow-sm)",
            position: "relative",
          }}>
            <div style={{
              fontSize: 10, fontWeight: 800, color: "var(--vv-primary)",
              textTransform: "uppercase", letterSpacing: "0.18em",
            }}>{c.kicker}</div>
            <h3 style={{
              fontFamily: "var(--vv-font-display)", fontWeight: 800,
              fontSize: 19, color: "var(--vv-fg-strong)", letterSpacing: "-0.02em",
              margin: 0, lineHeight: 1.25, maxWidth: "85%",
            }}>{c.tr}</h3>
            <div style={{
              fontFamily: "var(--vv-font-sans)", fontStyle: "italic",
              fontSize: 12, color: "var(--vv-fg-muted)", fontWeight: 500,
              opacity: 0.9, maxWidth: "85%",
            }}>{c.en}</div>
            <p style={{
              fontSize: 13, color: "var(--vv-fg-muted)", lineHeight: 1.65,
              margin: "4px 0 0", fontWeight: 500,
            }}>{c.body}</p>
          </div>
        ))}
      </div>
    </section>
  );
};

window.WebWhy = WebWhy;
