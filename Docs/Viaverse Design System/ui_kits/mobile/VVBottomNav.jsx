/* @jsx React.createElement */
// 5-tab bottom nav with center FAB (rotating V mark).
// Tabs: Keşfet · İşlerim(1) · [FAB · Yayınla] · Mesajlar(2) · Profil

const VVBottomNav = ({ active, onChange }) => {
  const { Icon, NumBadge } = window.VV;
  const tab = (id, label, iconName, badge) => {
    const isActive = active === id;
    return (
      <div onClick={() => onChange(id)} key={id} style={{
        display: "flex", flexDirection: "column", alignItems: "center", gap: 4,
        width: 64, cursor: "pointer", opacity: isActive ? 1 : 0.45,
      }}>
        <div style={{ position: "relative" }}>
          <span style={{ color: isActive ? "var(--vv-primary)" : "var(--vv-fg)" }}>
            <Icon name={iconName} size={20} strokeWidth={isActive ? 2 : 1.5}/>
          </span>
          {badge != null && <NumBadge>{badge}</NumBadge>}
        </div>
        <span style={{
          fontSize: 10, fontWeight: 700, letterSpacing: "-0.02em",
          color: isActive ? "var(--vv-primary)" : "var(--vv-fg)",
        }}>{label}</span>
      </div>
    );
  };

  return (
    <nav style={{
      position: "absolute", bottom: 0, left: 0, right: 0,
      height: 80, background: "var(--vv-surface)",
      borderTop: "1px solid var(--vv-border-subtle)",
      display: "flex", alignItems: "center", justifyContent: "space-around",
      padding: "0 12px", zIndex: 50,
      boxShadow: "var(--vv-shadow-nav)",
    }}>
      {tab("home", "Nearby",   "pin")}
      {tab("jobs", "Activity", "inbox", 3)}

      <div onClick={() => onChange("publish")} style={{
        display: "flex", flexDirection: "column", alignItems: "center", gap: 4,
        marginTop: -24, cursor: "pointer",
      }}>
        <div style={{
          width: 56, height: 56, borderRadius: "50%",
          background: "var(--vv-primary)",
          boxShadow: "0 8px 22px rgba(249,115,22,0.35)",
          border: "4px solid var(--vv-surface)",
          display: "flex", alignItems: "center", justifyContent: "center",
          overflow: "hidden",
        }}>
          <img src="../../assets/viaverse_icon_silver_green.png" alt=""
            style={{ width: 36, height: 36, animation: "vv-spin-y 8s linear infinite" }}/>
        </div>
        <span style={{
          fontSize: 10, fontWeight: 700, letterSpacing: "-0.02em",
          color: active === "publish" ? "var(--vv-primary)" : "var(--vv-fg)",
        }}>Share</span>
      </div>

      {tab("messages", "Chats", "message", 2)}
      {tab("profile", "Me",    "user")}
    </nav>
  );
};

window.VVBottomNav = VVBottomNav;
