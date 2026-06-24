/* @jsx React.createElement */
// Viaverse — shared primitives. Imported by all screen components.
// Exposes everything on window for cross-script access.

const VV = {
  // The single most-used button. Always wraps a real action; never decorative.
  PrimaryButton({ children, onClick, disabled, fullWidth, size = "md", style }) {
    const heights = { sm: 40, md: 48, lg: 52 };
    return (
      <button
        onClick={onClick}
        disabled={disabled}
        style={{
          height: heights[size],
          width: fullWidth ? "100%" : "auto",
          padding: "0 22px",
          background: disabled ? "var(--vv-surface-muted)" : "var(--vv-primary)",
          color: disabled ? "var(--vv-fg-faint)" : "var(--vv-primary-foreground)",
          borderRadius: 14,
          border: "none",
          fontFamily: "var(--vv-font-sans)",
          fontSize: 14,
          fontWeight: 800,
          letterSpacing: "-0.01em",
          boxShadow: disabled ? "none" : "var(--vv-shadow-cta)",
          cursor: disabled ? "not-allowed" : "pointer",
          opacity: disabled ? 0.6 : 1,
          transition: "transform 120ms, background 120ms",
          ...style,
        }}
        onMouseDown={(e) => (e.currentTarget.style.transform = "scale(0.98)")}
        onMouseUp={(e) => (e.currentTarget.style.transform = "scale(1)")}
        onMouseLeave={(e) => (e.currentTarget.style.transform = "scale(1)")}
      >
        {children}
      </button>
    );
  },

  NeutralButton({ children, onClick, fullWidth, style }) {
    return (
      <button
        onClick={onClick}
        style={{
          height: 44,
          padding: "0 18px",
          width: fullWidth ? "100%" : "auto",
          background: "var(--vv-surface)",
          color: "var(--vv-fg)",
          border: "1px solid var(--vv-border-muted)",
          borderRadius: 14,
          fontFamily: "var(--vv-font-sans)",
          fontSize: 13,
          fontWeight: 700,
          cursor: "pointer",
          display: "inline-flex",
          alignItems: "center",
          justifyContent: "center",
          gap: 6,
          ...style,
        }}
      >
        {children}
      </button>
    );
  },

  GhostButton({ children, onClick, style }) {
    return (
      <button
        onClick={onClick}
        style={{
          background: "transparent",
          border: "none",
          color: "var(--vv-fg-muted)",
          fontFamily: "var(--vv-font-sans)",
          fontSize: 12,
          fontWeight: 700,
          cursor: "pointer",
          padding: "8px 12px",
          ...style,
        }}
      >
        {children}
      </button>
    );
  },

  Input({ value, defaultValue, placeholder, type = "text", onChange, style }) {
    const [focus, setFocus] = React.useState(false);
    const controlled = value != null;
    return (
      <input
        type={type}
        {...(controlled ? { value } : { defaultValue: defaultValue || "" })}
        placeholder={placeholder}
        onChange={onChange}
        onFocus={() => setFocus(true)}
        onBlur={() => setFocus(false)}
        style={{
          width: "100%",
          height: 52,
          padding: "0 16px",
          background: "rgba(255,251,245,0.4)",
          border: `1px solid ${focus ? "var(--vv-primary)" : "var(--vv-border-muted)"}`,
          boxShadow: focus ? "0 0 0 1px var(--vv-primary)" : "none",
          borderRadius: 16,
          fontFamily: "var(--vv-font-sans)",
          fontSize: 14,
          color: "var(--vv-fg)",
          outline: "none",
          boxSizing: "border-box",
          ...style,
        }}
      />
    );
  },

  Chip({ children, active, onClick, icon, style }) {
    return (
      <button
        onClick={onClick}
        style={{
          height: 36,
          padding: "0 16px",
          borderRadius: 999,
          fontSize: 12,
          fontWeight: 700,
          fontFamily: "var(--vv-font-sans)",
          background: active ? "var(--vv-primary)" : "var(--vv-surface)",
          color: active ? "var(--vv-primary-foreground)" : "var(--vv-fg-muted)",
          border: `1px solid ${active ? "var(--vv-primary)" : "var(--vv-border-subtle)"}`,
          boxShadow: active ? "var(--vv-shadow-cta)" : "none",
          display: "inline-flex",
          alignItems: "center",
          gap: 6,
          whiteSpace: "nowrap",
          cursor: "pointer",
          flexShrink: 0,
          ...style,
        }}
      >
        {icon}
        <span>{children}</span>
      </button>
    );
  },

  Avatar({ src, name, size = 32 }) {
    const initials = (name || "?")
      .split(" ")
      .map((s) => s[0])
      .join("")
      .toUpperCase()
      .slice(0, 2);
    if (!src) {
      return (
        <div
          style={{
            width: size,
            height: size,
            borderRadius: "50%",
            background: "#14281c",
            color: "#fff",
            fontSize: size * 0.32,
            fontWeight: 800,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            border: "1px solid var(--vv-border-muted)",
            flexShrink: 0,
          }}
        >
          {initials}
        </div>
      );
    }
    return (
      <img
        src={src}
        alt={name}
        style={{
          width: size,
          height: size,
          borderRadius: "50%",
          objectFit: "cover",
          border: "1px solid var(--vv-border-muted)",
          flexShrink: 0,
        }}
      />
    );
  },

  Verified({ size = 13 }) {
    return (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none"
        stroke="#F97316" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"
        style={{ background: "rgba(249,115,22,0.05)", borderRadius: "50%", padding: 1, flexShrink: 0 }}>
        <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
      </svg>
    );
  },

  ModePill({ children, color = "trust" }) {
    const palette = color === "trust"
      ? { bg: "rgba(16,185,129,0.1)", border: "rgba(16,185,129,0.25)", fg: "#10B981" }
      : { bg: "var(--vv-primary-soft)", border: "rgba(249,115,22,0.25)", fg: "var(--vv-primary)" };
    return (
      <div style={{
        display: "inline-flex", alignItems: "center", gap: 6,
        padding: "5px 12px", borderRadius: 999,
        background: palette.bg, border: `1px solid ${palette.border}`,
        fontSize: 10, fontWeight: 800, textTransform: "uppercase",
        letterSpacing: "0.1em", color: palette.fg,
      }}>
        <div style={{
          width: 6, height: 6, borderRadius: "50%", background: palette.fg,
          animation: "vv-pulse 1.8s ease-in-out infinite",
        }} />
        {children}
      </div>
    );
  },

  TypeBadge({ type }) {
    const map = {
      yardım:   { bg: "rgba(249,115,22,0.10)",  fg: "#F97316", icon: "local_help" },
      duyuru:   { bg: "rgba(245,158,11,0.10)",  fg: "#F59E0B", icon: "announcement" },
      iş:       { bg: "rgba(16,185,129,0.12)",  fg: "#10B981", icon: "work" },
      danışma:  { bg: "rgba(168,85,247,0.10)",  fg: "#A855F7", icon: "advisory" },
    };
    const m = map[type] || map["yardım"];
    return (
      <span style={{
        display: "inline-flex", alignItems: "center", gap: 4,
        padding: "2px 8px", borderRadius: 4,
        fontSize: 9, fontWeight: 800, textTransform: "uppercase",
        background: m.bg, color: m.fg,
      }}>
        <img src={`../../assets/categories/${m.icon}.png`} alt="" style={{ width: 14, height: 14, objectFit: "contain" }}/>
        {type.toUpperCase()}
      </span>
    );
  },

  // Numeric badge — the orange dot with white digit
  NumBadge({ children }) {
    return (
      <span style={{
        position: "absolute", top: -6, right: -6,
        width: 16, height: 16, borderRadius: "50%",
        background: "var(--vv-primary)", color: "var(--vv-primary-foreground)",
        fontSize: 9, fontWeight: 800,
        display: "flex", alignItems: "center", justifyContent: "center",
        border: "2px solid var(--vv-surface)",
      }}>{children}</span>
    );
  },

  Icon({ name, size = 18, color = "currentColor", strokeWidth = 1.5 }) {
    const paths = {
      compass: <><circle cx="12" cy="12" r="10"/><polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"/></>,
      pin: <><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></>,
      inbox: <><path d="M22 12h-6l-2 3h-4l-2-3H2"/><path d="M5.45 5.11 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"/></>,
      briefcase: <><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></>,
      message: <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>,
      user: <><circle cx="12" cy="8" r="4"/><path d="M4 21v-1a8 8 0 0 1 16 0v1"/></>,
      monitor: <><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></>,
      chevronLeft: <polyline points="15 18 9 12 15 6"/>,
      chevronRight: <polyline points="9 18 15 12 9 6"/>,
      chevronDown: <polyline points="6 9 12 15 18 9"/>,
      x: <><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></>,
      sun: <><circle cx="12" cy="12" r="4"/><path d="M12 2v2m0 16v2M4.93 4.93l1.41 1.41m11.32 11.32l1.41 1.41M2 12h2m16 0h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41"/></>,
      moon: <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>,
      heart: <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>,
      send: <><path d="m22 2-7 20-4-9-9-4Z"/><path d="M22 2 11 13"/></>,
      paperclip: <path d="m21.44 11.05-9.19 9.19a6 6 0 0 1-8.49-8.49l8.57-8.57A4 4 0 1 1 17.99 8.84l-8.59 8.57a2 2 0 0 1-2.83-2.83l8.49-8.48"/>,
      phone: <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>,
      search: <><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></>,
      bell: <><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></>,
      shield: <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>,
      map: <><polygon points="1 6 1 22 8 18 16 22 23 18 23 2 16 6 8 2 1 6"/><line x1="8" y1="2" x2="8" y2="18"/><line x1="16" y1="6" x2="16" y2="22"/></>,
      star: <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>,
      plus: <><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></>,
      filter: <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/>,
      info: <><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></>,
    };
    return (
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none"
        stroke={color} strokeWidth={strokeWidth} strokeLinecap="round" strokeLinejoin="round">
        {paths[name]}
      </svg>
    );
  },
};

window.VV = VV;
