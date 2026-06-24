/* @jsx React.createElement */
// Expandable category row in the Hizmet al list.
// Closed: claymation icon + name + chevron. Open: list of sub-services.

const VVCategoryRow = ({ category, expanded, onToggle, onSelectSub }) => {
  const { Icon } = window.VV;
  return (
    <div style={{
      background: "var(--vv-surface)",
      borderRadius: 16,
      border: "1px solid var(--vv-border-subtle)",
      overflow: "hidden",
      marginBottom: 8,
    }}>
      <button onClick={onToggle} style={{
        width: "100%", background: "none", border: "none",
        display: "flex", alignItems: "center", gap: 12,
        padding: "12px 14px", cursor: "pointer", color: "var(--vv-fg)",
      }}>
        <img src={`../../assets/categories/${category.icon}.png`} alt=""
          style={{ width: 32, height: 32, objectFit: "contain", flexShrink: 0 }}/>
        <div style={{ flex: 1, textAlign: "left" }}>
          <div style={{ fontSize: 13, fontWeight: 700, color: "var(--vv-fg)" }}>{category.name}</div>
          <div style={{ fontSize: 10, color: "var(--vv-fg-muted)", marginTop: 2 }}>{category.providers}</div>
        </div>
        <span style={{ transition: "transform 200ms", transform: expanded ? "rotate(180deg)" : "rotate(0deg)", color: "var(--vv-fg-muted)" }}>
          <Icon name="chevronDown" size={16}/>
        </span>
      </button>
      {expanded && (
        <div style={{ borderTop: "1px solid var(--vv-border-subtle)" }}>
          {category.subCats.slice(0, 8).map((sub) => (
            <button key={sub} onClick={() => onSelectSub(sub)} style={{
              width: "100%", background: "none", border: "none",
              borderTop: "1px solid var(--vv-border-faint)",
              padding: "11px 16px 11px 56px",
              display: "flex", justifyContent: "space-between", alignItems: "center",
              cursor: "pointer", color: "var(--vv-primary-hover)",
            }}>
              <span style={{ fontSize: 12, fontWeight: 500, color: "var(--vv-primary)" }}>{sub}</span>
              <span style={{ color: "var(--vv-fg-muted)" }}><Icon name="chevronRight" size={14}/></span>
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

window.VVCategoryRow = VVCategoryRow;
