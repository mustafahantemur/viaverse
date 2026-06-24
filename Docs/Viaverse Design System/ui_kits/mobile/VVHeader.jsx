/* @jsx React.createElement */
// Header: back chip + rotating-search pill (with "ara" submit) + theme toggle.
// Below: two-tab row (Keşfet / Hizmet al) with orange under-bar on active.

const VVHeader = ({ mode, onModeChange, onBack, dark, onDarkChange, hideTabs }) => {
  const { Icon } = window.VV;
  const placeholders = mode === "keşfet"
    ? [
        "Dinamik çevrende neler oluyor?",
        "Yardım, duyuru, danışma veya küçük iş paylaş.",
        "Yakınındaki gündelik ihtiyaçları gör.",
      ]
    : [
        "Hangi hizmete ihtiyacın var?",
        "İhtiyacını anlat, teklif al.",
        "Hizmet kategorilerinden seçim yap.",
      ];
  const [idx, setIdx] = React.useState(0);
  React.useEffect(() => {
    setIdx(0);
    const t = setInterval(() => setIdx((p) => (p + 1) % placeholders.length), 4000);
    return () => clearInterval(t);
  }, [mode]);

  return (
    <header style={{
      position: "sticky", top: 0, background: "var(--vv-surface)", zIndex: 30,
      paddingTop: 12, paddingBottom: 0, flexShrink: 0,
    }}>
      <div style={{ display: "flex", alignItems: "center", gap: 8, padding: "0 12px 8px" }}>
        <button onClick={onBack} style={{
          width: 32, height: 32, flexShrink: 0,
          display: "flex", alignItems: "center", justifyContent: "center",
          borderRadius: "50%", border: "1px solid var(--vv-border-muted)",
          background: "var(--vv-surface-muted)", color: "var(--vv-fg)", cursor: "pointer",
        }}>
          <Icon name="chevronLeft" size={18}/>
        </button>

        <div style={{
          flex: 1, display: "flex", alignItems: "center", height: 40,
          borderRadius: 999, overflow: "hidden",
          border: "1px solid var(--vv-border-faint)",
          background: "var(--vv-surface-muted)",
          boxShadow: "var(--vv-shadow-xs)",
        }}>
          <div style={{ flex: 1, display: "flex", alignItems: "center", padding: "0 14px" }}>
            <span style={{ marginRight: 8, color: mode === "keşfet" ? "var(--vv-primary)" : "var(--vv-fg-muted)" }}>
              <Icon name="compass" size={16} strokeWidth={2}/>
            </span>
            <span style={{
              fontSize: 12, color: "var(--vv-fg-muted)", fontWeight: 500,
              whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis",
              flex: 1,
            }}>{placeholders[idx]}</span>
          </div>
          <button style={{
            background: "var(--vv-primary)", color: "var(--vv-primary-foreground)", border: "none",
            height: "100%", padding: "0 18px", fontSize: 12, fontWeight: 800,
            cursor: "pointer",
          }}>ara</button>
        </div>

        <button onClick={() => onDarkChange(!dark)} style={{
          width: 32, height: 32, borderRadius: "50%",
          background: "var(--vv-surface-muted)", border: "1px solid var(--vv-border-muted)",
          color: "var(--vv-fg-muted)", cursor: "pointer",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}>
          <Icon name={dark ? "sun" : "moon"} size={16}/>
        </button>
      </div>

      {!hideTabs && (
        <div style={{ display: "flex", justifyContent: "center", gap: 48, padding: "0 24px" }}>
          {[
            { id: "keşfet", label: "keşfet", icon: "compass" },
            { id: "hizmet", label: "hizmet al", icon: "monitor" },
          ].map((t) => {
            const active = mode === t.id;
            return (
              <button key={t.id} onClick={() => onModeChange(t.id)} style={{
                display: "flex", flexDirection: "column", alignItems: "center", gap: 4,
                paddingBottom: 8, minWidth: 60,
                borderBottom: `3px solid ${active ? "var(--vv-primary)" : "transparent"}`,
                opacity: active ? 1 : 0.4,
                background: "none", border: "none",
                borderBottomLeftRadius: 0, borderBottomRightRadius: 0,
                cursor: "pointer", color: "var(--vv-fg)",
              }}>
                <span style={{ paddingBottom: 4, color: "var(--vv-fg)" }}>
                  <Icon name={t.icon} size={24} strokeWidth={active ? 2 : 1.5}/>
                </span>
                <span style={{ fontSize: 10, fontWeight: 700, letterSpacing: "-0.02em" }}>{t.label}</span>
              </button>
            );
          })}
        </div>
      )}
    </header>
  );
};

window.VVHeader = VVHeader;
