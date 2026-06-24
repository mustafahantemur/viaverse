/* @jsx React.createElement */
const DisputeView = () => {
  return (
    <div>
      <div style={{ marginBottom: 18 }}>
        <div style={{ fontSize: 11, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.15em", marginBottom: 6 }}>Operasyon</div>
        <h1 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 26, letterSpacing: "-0.02em", margin: 0, color: "var(--vv-fg-strong)" }}>Uyuşmazlık · DSP-0412</h1>
        <p style={{ fontSize: 12, color: "var(--vv-fg-muted)", margin: "6px 0 0" }}>İş tamamlanmadı iddiası · 1.200 ₺ · 18 saat önce açıldı.</p>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "1.4fr 1fr", gap: 16 }}>
        <div style={{
          background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
          borderRadius: 14, padding: 18,
        }}>
          <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 14 }}>
            <span style={{
              padding: "3px 10px", borderRadius: 999, fontSize: 10, fontWeight: 800,
              background: "rgba(245,158,11,0.12)", color: "#F59E0B", textTransform: "uppercase", letterSpacing: "0.05em",
            }}>İncelemede</span>
            <span style={{ fontSize: 11, color: "var(--vv-fg-muted)" }}>SLA 36 saat içinde</span>
          </div>

          <div style={{ fontSize: 12, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.1em", marginBottom: 8 }}>Olay zaman çizelgesi</div>
          {[
            { t: "Talep açıldı · Cansu B.",                  s: "İlan: Köpek gezdirme · 14:00",       k: "neutral", time: "20 May 12:14" },
            { t: "Teklif kabul edildi · Ahmet Usta",         s: "1.200 ₺ + %10 komisyon",              k: "neutral", time: "20 May 13:02" },
            { t: "İş başlangıcı işaretlendi",                s: "Provider check-in · Ataşehir",        k: "neutral", time: "21 May 14:08" },
            { t: "İş 'tamamlandı' işaretlendi",              s: "Provider tarafından",                  k: "neutral", time: "21 May 14:55" },
            { t: "Müşteri onayı reddedildi",                 s: "'Köpek gezdirilmedi, sadece otoparkta bekletildi.'", k: "warning", time: "21 May 15:30" },
            { t: "Uyuşmazlık açıldı",                        s: "Refund talep edildi · 1.200 ₺",        k: "danger",  time: "22 May 06:14" },
          ].map((e, i, a) => (
            <div key={i} style={{ display: "flex", gap: 12 }}>
              <div style={{ display: "flex", flexDirection: "column", alignItems: "center", paddingTop: 4 }}>
                <div style={{
                  width: 9, height: 9, borderRadius: "50%",
                  background: e.k === "danger" ? "#EF4444" : e.k === "warning" ? "#F59E0B" : "var(--vv-border-muted)",
                }}/>
                {i < a.length - 1 && <div style={{ flex: 1, width: 1, background: "var(--vv-border-faint)", minHeight: 26, margin: "4px 0" }}/>}
              </div>
              <div style={{ paddingBottom: 14, flex: 1 }}>
                <div style={{ fontSize: 12, fontWeight: 700, color: "var(--vv-fg)" }}>{e.t}</div>
                <div style={{ fontSize: 11, color: "var(--vv-fg-muted)", marginTop: 2 }}>{e.s}</div>
                <div style={{ fontSize: 10, color: "var(--vv-fg-faint)", marginTop: 2 }}>{e.time}</div>
              </div>
            </div>
          ))}

          <div style={{
            background: "var(--vv-surface-muted)", borderRadius: 10,
            padding: 14, marginTop: 6,
            border: "1px dashed var(--vv-border-muted)",
          }}>
            <div style={{ fontSize: 11, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.1em", marginBottom: 8 }}>Karar</div>
            <div style={{ display: "flex", gap: 8 }}>
              <button style={{ background: "#10B981", color: "#fff", border: "none", padding: "10px 16px", borderRadius: 10, fontSize: 12, fontWeight: 800, cursor: "pointer" }}>Müşteriye iade</button>
              <button style={{ background: "var(--vv-primary)", color: "var(--vv-primary-foreground)", border: "none", padding: "10px 16px", borderRadius: 10, fontSize: 12, fontWeight: 800, cursor: "pointer" }}>%50 paylaştır</button>
              <button style={{ background: "var(--vv-surface)", color: "var(--vv-fg)", border: "1px solid var(--vv-border-muted)", padding: "10px 14px", borderRadius: 10, fontSize: 12, fontWeight: 700, cursor: "pointer" }}>Sağlayıcı haklı</button>
            </div>
          </div>
        </div>

        <div style={{ display: "flex", flexDirection: "column", gap: 14 }}>
          {[
            { label: "Müşteri", name: "Cansu B.",   img: "https://i.pravatar.cc/64?img=44", meta: "47 iş · 4.8 ★ · 2024'ten beri" },
            { label: "Sağlayıcı", name: "Ahmet Usta", img: "https://i.pravatar.cc/64?img=12", meta: "124 iş · 4.9 ★ · doğrulanmış" },
          ].map((p) => (
            <div key={p.label} style={{
              background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
              borderRadius: 14, padding: 16,
            }}>
              <div style={{ fontSize: 10, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.1em" }}>{p.label}</div>
              <div style={{ display: "flex", alignItems: "center", gap: 10, marginTop: 10 }}>
                <img src={p.img} alt="" style={{ width: 40, height: 40, borderRadius: "50%" }}/>
                <div>
                  <div style={{ fontSize: 13, fontWeight: 800, color: "var(--vv-fg)" }}>{p.name}</div>
                  <div style={{ fontSize: 11, color: "var(--vv-fg-muted)" }}>{p.meta}</div>
                </div>
              </div>
            </div>
          ))}
          <div style={{
            background: "var(--vv-surface)", border: "1px solid var(--vv-border-subtle)",
            borderRadius: 14, padding: 16,
          }}>
            <div style={{ fontSize: 10, fontWeight: 800, color: "var(--vv-fg-muted)", textTransform: "uppercase", letterSpacing: "0.1em", marginBottom: 10 }}>Ödeme</div>
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 6, fontSize: 12 }}>
              <span>İş bedeli</span><strong>1.200 ₺</strong>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 6, fontSize: 12 }}>
              <span>Viaverse komisyonu (%10)</span><span>120 ₺</span>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between", paddingTop: 8, marginTop: 6, borderTop: "1px solid var(--vv-border-subtle)", fontSize: 13 }}>
              <strong>Sağlayıcıya kalan</strong><strong style={{ color: "var(--vv-trust-deep)" }}>1.080 ₺</strong>
            </div>
            <div style={{ fontSize: 10, color: "var(--vv-fg-muted)", marginTop: 12, lineHeight: 1.5 }}>
              Fon escrow'da bekletiliyor. Karar verildikten sonra T+1 ödeme tetiklenir.
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

window.DisputeView = DisputeView;
