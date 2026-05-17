/* @jsx React.createElement */
// Feed card — author chip with verified, type-tagged title, body, like/comment, optional "teklif ver" CTA.

const VVFeedCard = ({ post, onBid }) => {
  const { Avatar, Verified, TypeBadge, Icon } = window.VV;
  return (
    <div style={{
      background: "var(--vv-surface)",
      padding: "12px 12px 10px",
      borderBottom: "1px solid var(--vv-border-subtle)",
    }}>
      <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 6 }}>
        <Avatar src={post.authorImg} name={post.authorName} size={34}/>
        <div style={{ display: "flex", flexDirection: "column", gap: 2 }}>
          <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
            <span style={{ fontSize: 12, fontWeight: 800, color: "var(--vv-fg)" }}>{post.authorName}</span>
            <Verified/>
          </div>
          <div style={{ fontSize: 10, color: "var(--vv-fg-muted)", display: "flex", gap: 4 }}>
            <span>{post.publishTime}</span><span>·</span><span>{post.dist}</span>
          </div>
        </div>
      </div>

      <div style={{ fontSize: 12, color: "var(--vv-fg)", lineHeight: 1.45 }}>
        {post.title && (
          <div style={{
            display: "inline-flex", alignItems: "center", gap: 8,
            fontWeight: 800, color: "#1C1C1C", marginRight: 6, marginBottom: 2,
          }}>
            {post.title}
            <TypeBadge type={post.type}/>
          </div>
        )}
        {!post.title && (
          <div style={{ marginBottom: 2 }}><TypeBadge type={post.type}/></div>
        )}
        <div>{post.desc}</div>
      </div>

      {post.postImg && (
        <div style={{ marginTop: 10, marginLeft: -12, marginRight: -12 }}>
          <img src={post.postImg} alt="" style={{ width: "100%", maxHeight: 240, objectFit: "cover" }}/>
        </div>
      )}

      <div style={{
        display: "flex", alignItems: "center", justifyContent: "space-between",
        marginTop: 8, paddingTop: 2,
      }}>
        <div style={{ display: "flex", gap: 18 }}>
          <button style={{
            display: "inline-flex", alignItems: "center", gap: 5,
            background: "none", border: "none", padding: 0, cursor: "pointer",
            color: "var(--vv-fg-muted)", fontSize: 11, fontWeight: 500,
          }}>
            <Icon name="heart" size={16}/>
            {post.likes}
          </button>
          <button style={{
            display: "inline-flex", alignItems: "center", gap: 5,
            background: "none", border: "none", padding: 0, cursor: "pointer",
            color: "var(--vv-fg-muted)", fontSize: 11, fontWeight: 500,
          }}>
            <Icon name="message" size={16}/>
            {post.comments}
          </button>
        </div>
        {post.type === "iş" && (
          <button onClick={onBid} style={{
            background: "var(--vv-primary)", color: "var(--vv-primary-foreground)",
            padding: "5px 14px", borderRadius: 999, border: "none",
            fontSize: 10, fontWeight: 800, letterSpacing: "-0.01em",
            cursor: "pointer", boxShadow: "0 4px 10px rgba(249,115,22,0.3)",
          }}>teklif ver</button>
        )}
      </div>
    </div>
  );
};

window.VVFeedCard = VVFeedCard;
