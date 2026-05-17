/* @jsx React.createElement */
// Offer card — used in the "Incoming Offers" bottom sheet inside Jobs.

const VVOfferCard = ({ offer, onAccept }) => {
  const { Avatar, Icon } = window.VV;
  return (
    <div style={{
      background: "var(--vv-surface)",
      border: "1px solid var(--vv-border-subtle)",
      borderRadius: 14, padding: 14, marginBottom: 10,
      boxShadow: "var(--vv-shadow-sm)",
    }}>
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 6 }}>
        <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
          <Avatar src={offer.img} name={offer.name} size={34}/>
          <div>
            <div style={{ fontSize: 13, fontWeight: 800, color: "var(--vv-fg)", display: "inline-flex", alignItems: "center", gap: 4 }}>
              {offer.name} <Icon name="chevronRight" size={12} color="var(--vv-fg-muted)"/>
            </div>
            <div style={{ fontSize: 10, fontWeight: 700, color: "#F59E0B" }}>
              ★ {offer.rating} <span style={{ color: "var(--vv-fg-muted)", fontWeight: 500 }}>({offer.reviews})</span>
            </div>
          </div>
        </div>
        <div style={{
          padding: "5px 12px", borderRadius: 999,
          background: "rgba(16,185,129,0.12)", color: "#064E3B",
          fontWeight: 800, fontSize: 13,
        }}>{offer.price} ₺</div>
      </div>
      <div style={{ fontSize: 11, color: "var(--vv-fg)", lineHeight: 1.5, marginBottom: 10 }}>{offer.desc}</div>
      <button onClick={onAccept} style={{
        width: "100%", background: "#047857", color: "#fff",
        border: "none", borderRadius: 12, padding: "11px 0",
        fontSize: 12, fontWeight: 800, cursor: "pointer",
        boxShadow: "0 4px 12px rgba(4,120,87,0.25)",
        display: "inline-flex", alignItems: "center", justifyContent: "center", gap: 6,
      }}>
        <Icon name="message" size={14} strokeWidth={2}/>
        Teklifi Kabul Et &amp; Mesajlaş
      </button>
    </div>
  );
};

window.VVOfferCard = VVOfferCard;
