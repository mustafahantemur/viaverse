/* @jsx React.createElement */
// Chat list + chat thread (job/quote-linked).

const VVChatList = ({ threads, onOpen, onBack }) => {
  const { Avatar, Icon } = window.VV;
  return (
    <div style={{ display: "flex", flexDirection: "column", flex: 1, overflow: "hidden" }}>
      <header style={{
        position: "sticky", top: 0, background: "var(--vv-surface)", zIndex: 30,
        display: "flex", alignItems: "center", justifyContent: "space-between",
        padding: "16px 16px 12px", flexShrink: 0,
      }}>
        <button onClick={onBack} style={{
          width: 32, height: 32, borderRadius: "50%",
          border: "1px solid var(--vv-border-muted)", background: "var(--vv-surface-muted)",
          color: "var(--vv-fg)", cursor: "pointer",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}><Icon name="chevronLeft" size={18}/></button>
        <h2 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 16, margin: 0, color: "var(--vv-fg-strong)", letterSpacing: "-0.02em" }}>Mesajlar</h2>
        <button style={{
          width: 32, height: 32, borderRadius: "50%",
          border: "1px solid var(--vv-border-muted)", background: "var(--vv-surface-muted)",
          color: "var(--vv-fg-muted)", cursor: "pointer",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}><Icon name="search" size={14}/></button>
      </header>
      <div style={{ flex: 1, overflowY: "auto" }}>
        {threads.map((t, i) => (
          <div key={i} onClick={() => onOpen(t)} style={{
            display: "flex", alignItems: "center", gap: 12,
            padding: "12px 16px", borderBottom: "1px solid var(--vv-border-faint)",
            cursor: "pointer",
          }}>
            <div style={{ position: "relative" }}>
              <Avatar src={t.img} name={t.name} size={42}/>
              {t.unread > 0 && (
                <div style={{
                  position: "absolute", top: 0, right: 0,
                  width: 10, height: 10, borderRadius: "50%",
                  background: "var(--vv-primary)", border: "2px solid var(--vv-surface)",
                }}/>
              )}
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 2 }}>
                <span style={{ fontSize: 13, fontWeight: 800, color: "var(--vv-fg)" }}>{t.name}</span>
                <span style={{ fontSize: 10, color: t.unread ? "var(--vv-primary)" : "var(--vv-fg-muted)" }}>{t.time}</span>
              </div>
              <div style={{
                fontSize: 11, color: t.unread ? "var(--vv-fg)" : "var(--vv-fg-muted)",
                whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis",
                fontWeight: t.unread ? 600 : 400,
              }}>{t.preview}</div>
            </div>
            {t.unread > 0 && (
              <div style={{
                width: 20, height: 20, borderRadius: "50%",
                background: "var(--vv-primary)", color: "var(--vv-primary-foreground)",
                fontSize: 10, fontWeight: 800,
                display: "flex", alignItems: "center", justifyContent: "center",
              }}>{t.unread}</div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

const VVChatThread = ({ thread, onBack }) => {
  const { Avatar, Icon } = window.VV;
  const [msgs, setMsgs] = React.useState([
    { from: "them", text: "Merhaba, tesisat işi için ilanınızı gördüm. Ne zaman müsaitsiniz?", time: "10:42" },
    { from: "me", text: "Selam Ahmet usta, yarın öğleden sonra uygunum. Saat 14:00 gibi gelebilir misiniz?", time: "10:45" },
  ]);
  const [draft, setDraft] = React.useState("");

  return (
    <div style={{ display: "flex", flexDirection: "column", flex: 1, overflow: "hidden" }}>
      <header style={{
        position: "sticky", top: 0, background: "var(--vv-surface)", zIndex: 30,
        display: "flex", alignItems: "center", gap: 10,
        padding: "12px 16px", flexShrink: 0,
        borderBottom: "1px solid var(--vv-border-subtle)",
      }}>
        <button onClick={onBack} style={{
          width: 32, height: 32, borderRadius: "50%",
          border: "1px solid var(--vv-border-muted)", background: "var(--vv-surface-muted)",
          color: "var(--vv-fg)", cursor: "pointer",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}><Icon name="chevronLeft" size={18}/></button>
        <Avatar src={thread.img} name={thread.name} size={32}/>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 13, fontWeight: 800, color: "var(--vv-fg)" }}>{thread.name}</div>
          <div style={{ fontSize: 10, color: "var(--vv-trust)", fontWeight: 600 }}>Çevrimiçi</div>
        </div>
        <button style={{
          width: 32, height: 32, borderRadius: "50%",
          border: "1px solid var(--vv-border-muted)", background: "var(--vv-surface-muted)",
          color: "var(--vv-fg)", cursor: "pointer",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}><Icon name="phone" size={14}/></button>
      </header>

      <div style={{ flex: 1, overflowY: "auto", padding: 16, display: "flex", flexDirection: "column", gap: 10 }}>
        {msgs.map((m, i) => (
          <div key={i} style={{ display: "flex", justifyContent: m.from === "me" ? "flex-end" : "flex-start" }}>
            <div style={{
              maxWidth: "75%", padding: "10px 14px",
              borderRadius: 18,
              borderTopLeftRadius: m.from === "them" ? 6 : 18,
              borderTopRightRadius: m.from === "me" ? 6 : 18,
              background: m.from === "me" ? "var(--vv-primary)" : "#FFFBF5",
              color: m.from === "me" ? "var(--vv-primary-foreground)" : "var(--vv-fg)",
              border: m.from === "them" ? "1px solid var(--vv-border-muted)" : "none",
              boxShadow: m.from === "me"
                ? "0 12px 24px rgba(124,45,18,0.38), 0 4px 8px rgba(124,45,18,0.30)"
                : "0 2px 6px rgba(15,23,42,0.06), 0 1px 2px rgba(15,23,42,0.04)",
              fontSize: 12, lineHeight: 1.45,
            }}>
              {m.text}
              <div style={{
                fontSize: 9, marginTop: 4,
                color: m.from === "me" ? "rgba(254,239,212,0.7)" : "var(--vv-fg-muted)",
                textAlign: m.from === "me" ? "right" : "left",
              }}>{m.time}</div>
            </div>
          </div>
        ))}
      </div>

      <div style={{
        display: "flex", alignItems: "center", gap: 10,
        padding: "10px 14px 14px",
        background: "var(--vv-surface)",
        borderTop: "1px solid var(--vv-border-faint)",
      }}>
        <button style={{
          width: 36, height: 36, borderRadius: "50%",
          background: "var(--vv-surface-muted)", border: "1px solid var(--vv-border-muted)",
          color: "var(--vv-fg-muted)", cursor: "pointer",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}><Icon name="paperclip" size={14}/></button>
        <div style={{ flex: 1, background: "var(--vv-surface-muted)", borderRadius: 999, padding: "8px 14px" }}>
          <input value={draft} onChange={(e) => setDraft(e.target.value)}
            placeholder="Mesaj yaz..."
            style={{
              width: "100%", background: "none", border: "none", outline: "none",
              fontFamily: "var(--vv-font-sans)", fontSize: 12, color: "var(--vv-fg)",
            }}/>
        </div>
        <button onClick={() => {
          if (!draft.trim()) return;
          setMsgs([...msgs, { from: "me", text: draft, time: "şimdi" }]);
          setDraft("");
        }} style={{
          width: 36, height: 36, borderRadius: "50%",
          background: "var(--vv-primary)", border: "none",
          color: "var(--vv-primary-foreground)", cursor: "pointer",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}><Icon name="send" size={14} strokeWidth={2}/></button>
      </div>
    </div>
  );
};

window.VVChatList = VVChatList;
window.VVChatThread = VVChatThread;
