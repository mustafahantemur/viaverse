/* @jsx React.createElement */
const WebNav = ({ onApp, onLogin }) => {
  const links = ["Hizmetler", "Hizmet ver", "Rehber", "Yardım"];
  return (
    <nav style={{
      position: "sticky", top: 0, zIndex: 60,
      background: "rgba(255,251,245,0.86)",
      backdropFilter: "blur(12px)",
      borderBottom: "1px solid var(--vv-border-faint)",
      padding: "16px 56px",
      display: "flex", alignItems: "center", justifyContent: "space-between"
    }}>
      <div style={{ display: "flex", alignItems: "center", gap: 40 }}>
        <a href="#" onClick={(e) => {e.preventDefault();onApp && onApp();}} style={{
          display: "flex", alignItems: "center", gap: 10, textDecoration: "none"
        }}>
          <img src="../../assets/viaverse_icon.png" alt="" style={{ width: 36, height: 36 }} />
          <img src="../../assets/viaverse_wordmark.png" alt="Viaverse" style={{ height: "22px", width: "39.0781px" }} />
        </a>
        <ul style={{ display: "flex", gap: 28, listStyle: "none", margin: 0, padding: 0 }}>
          {links.map((l) =>
          <li key={l}><a href="#" style={{
              fontFamily: "var(--vv-font-sans)", fontSize: 14, fontWeight: 600,
              color: "var(--vv-fg)", textDecoration: "none"
            }}>{l}</a></li>
          )}
        </ul>
      </div>
      <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
        <button onClick={onLogin} style={{
          background: "none", border: "none", padding: "10px 18px",
          fontFamily: "var(--vv-font-sans)", fontSize: 14, fontWeight: 700,
          color: "var(--vv-fg)", cursor: "pointer"
        }}>Giriş yap</button>
        <button onClick={onApp} style={{
          background: "var(--vv-primary)", color: "var(--vv-primary-foreground)", border: "none",
          padding: "11px 22px", borderRadius: 999, cursor: "pointer",
          fontFamily: "var(--vv-font-sans)", fontSize: 14, fontWeight: 800,
          letterSpacing: "-0.01em", boxShadow: "var(--vv-shadow-cta)"
        }}>Hesap oluştur</button>
      </div>
    </nav>);

};

window.WebNav = WebNav;