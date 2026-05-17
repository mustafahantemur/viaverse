/* @jsx React.createElement */
const WebFeedTeaser = () => {
  const posts = [
    {
      type: "yardım", icon: "local_help", color: "#F97316",
      title: "Yıldız tornavida",
      desc: "Ufak bir montaj işim var, 1 saatliğine yıldız tornavida ödünç verebilecek biri var mı?",
      authorName: "Ayşe T.",
      authorImg: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=120&q=80",
      meta: "5 dk · 150m",
      bg: "https://images.unsplash.com/photo-1581094288338-2314dddb7ece?auto=format&fit=crop&w=900&q=80",
    },
    {
      type: "iş", icon: "work", color: "#10B981",
      title: "Köpek gezdirme · yarın 14:00",
      desc: "Ufak ırk köpeğimi 45 dk gezdirebilecek biri lazım. 1.2km içinde olsa süper.",
      authorName: "Cansu B.",
      authorImg: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=120&q=80",
      meta: "1 sa · 1.2 km",
      price: "Teklif bekliyor",
      bg: "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?auto=format&fit=crop&w=900&q=80",
    },
    {
      type: "duyuru", icon: "announcement", color: "#F59E0B",
      title: "Kayıp kedi · Tarçın",
      desc: "Dün akşam parkın yakınında kedimiz kayboldu. Gören olursa lütfen ulaşsın.",
      authorName: "Kemal S.",
      authorImg: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=120&q=80",
      meta: "27 dk · 200m",
      bg: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=900&q=80",
    },
  ];
  return (
    <section style={{ padding: "72px 56px", background: "var(--vv-surface-muted)" }}>
      <div style={{ display: "flex", alignItems: "flex-end", justifyContent: "space-between", marginBottom: 36 }}>
        <div>
          <div style={{ fontSize: 11, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.2em", marginBottom: 10 }}>
            Çevrende
          </div>
          <h2 style={{
            fontFamily: "var(--vv-font-display)", fontWeight: 800,
            fontSize: 40, lineHeight: 1.1, letterSpacing: "-0.02em",
            color: "var(--vv-fg-strong)", margin: 0, maxWidth: 680,
          }}>
            Yorucu işleri, yeni <span style={{ color: "var(--vv-primary)" }}>fırsatlarla</span> buluşturuyoruz.
          </h2>
        </div>
        <div style={{ display: "inline-flex", alignItems: "center", gap: 8, padding: "8px 14px", background: "var(--vv-surface)", borderRadius: 999, border: "1px solid var(--vv-border-subtle)" }}>
          <span style={{ width: 6, height: 6, borderRadius: "50%", background: "#10B981", animation: "vv-pulse 1.8s ease-in-out infinite" }}/>
          <span style={{ fontSize: 11, fontWeight: 700, color: "var(--vv-fg)" }}>Canlı</span>
        </div>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 16 }}>
        {posts.map((p) => (
          <article key={p.title} style={{
            background: "var(--vv-surface)",
            border: "1px solid var(--vv-border-subtle)",
            borderRadius: 18, overflow: "hidden",
            display: "flex", flexDirection: "column",
            boxShadow: "var(--vv-shadow-sm)",
          }}>
            <div style={{
              height: 160, position: "relative",
              backgroundImage: `url(${p.bg})`, backgroundSize: "cover", backgroundPosition: "center",
            }}>
              <span style={{
                position: "absolute", top: 12, left: 12,
                display: "inline-flex", alignItems: "center", gap: 6,
                padding: "5px 10px 5px 6px", borderRadius: 999,
                background: "rgba(255,251,245,0.94)", backdropFilter: "blur(6px)",
                fontSize: 10, fontWeight: 800, color: p.color,
                textTransform: "uppercase", letterSpacing: "0.05em",
              }}>
                <img src={`../../assets/categories/${p.icon}.png`} alt="" style={{ width: 20, height: 20 }}/>
                {p.type}
              </span>
            </div>
            <div style={{ padding: 20, display: "flex", flexDirection: "column", gap: 12 }}>
              <h3 style={{
                fontFamily: "var(--vv-font-display)", fontWeight: 800,
                fontSize: 18, color: "var(--vv-fg-strong)", letterSpacing: "-0.02em", margin: 0, lineHeight: 1.25,
              }}>{p.title}</h3>
              <p style={{ fontSize: 13, color: "var(--vv-fg-muted)", lineHeight: 1.6, margin: 0, fontWeight: 500 }}>{p.desc}</p>
              <div style={{
                display: "flex", alignItems: "center", justifyContent: "space-between",
                paddingTop: 12, borderTop: "1px solid var(--vv-border-subtle)",
              }}>
                <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  <img src={p.authorImg} alt="" style={{ width: 28, height: 28, borderRadius: "50%", objectFit: "cover" }}/>
                  <div>
                    <div style={{ fontSize: 12, fontWeight: 800, color: "var(--vv-fg)" }}>{p.authorName}</div>
                    <div style={{ fontSize: 10, color: "var(--vv-fg-muted)" }}>{p.meta}</div>
                  </div>
                </div>
                {p.price && (
                  <span style={{
                    padding: "4px 10px", borderRadius: 999,
                    background: "rgba(249,115,22,0.1)", color: "var(--vv-primary)",
                    fontSize: 10, fontWeight: 800,
                  }}>teklif ver</span>
                )}
              </div>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
};

window.WebFeedTeaser = WebFeedTeaser;
