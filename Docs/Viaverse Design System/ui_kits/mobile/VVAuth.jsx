/* @jsx React.createElement */
// Auth screen — login (phone/email + password + OAuth) → OTP (6 digit code grid)

const VVAuth = ({ onAuthed, onSkip }) => {
  const { PrimaryButton, Input, Icon, NeutralButton } = window.VV;
  const [step, setStep] = React.useState("login");
  const [otp, setOtp] = React.useState("");

  if (step === "otp") {
    return (
      <div style={{ position: "absolute", inset: 0, background: "var(--vv-surface)", display: "flex", flexDirection: "column", padding: "60px 32px 32px" }}>
        <button onClick={() => setStep("login")} style={{
          position: "absolute", top: 24, left: 16, padding: 8,
          background: "none", border: "none", cursor: "pointer", color: "var(--vv-fg)",
        }}>
          <Icon name="chevronLeft" size={24}/>
        </button>
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", marginTop: 60 }}>
          <h1 style={{ fontFamily: "var(--vv-font-display)", fontWeight: 800, fontSize: 20, letterSpacing: "-0.02em", margin: 0, color: "var(--vv-fg-strong)" }}>Doğrulama Kodu</h1>
          <p style={{ color: "var(--vv-fg-muted)", textAlign: "center", fontSize: 12, marginTop: 8, marginBottom: 36, maxWidth: 240, lineHeight: 1.5 }}>
            Telefonunuza gelen 6 haneli doğrulama kodunu giriniz.
          </p>
          <div style={{ display: "flex", gap: 10, marginBottom: 36 }}>
            {[0,1,2,3,4,5].map(i => (
              <input key={i} maxLength={1} value={otp[i] || ""}
                onChange={(e) => {
                  const v = e.target.value.replace(/\D/g, "");
                  const next = (otp.substring(0, i) + (v || "") + otp.substring(i + 1)).slice(0, 6);
                  setOtp(next);
                  if (v && i < 5) {
                    const el = document.getElementById(`otp-${i+1}`);
                    if (el) el.focus();
                  }
                  if (next.length === 6) setTimeout(onAuthed, 350);
                }}
                id={`otp-${i}`}
                style={{
                  width: 40, height: 56,
                  border: `1px solid ${otp[i] ? "var(--vv-fg-faint)" : "var(--vv-border-muted)"}`,
                  borderRadius: 12, textAlign: "center", fontFamily: "var(--vv-font-display)",
                  fontSize: 20, fontWeight: 800, color: "var(--vv-fg-strong)",
                  background: "rgba(255,251,245,0.4)", outline: "none",
                }}
              />
            ))}
          </div>
          <PrimaryButton fullWidth size="lg" disabled={otp.length !== 6} onClick={onAuthed}>
            Doğrula ve Devam Et
          </PrimaryButton>
          <button style={{
            marginTop: 28, background: "none", border: "none",
            color: "var(--vv-primary)", fontWeight: 800, fontSize: 12, cursor: "pointer",
          }}>Yeni Kod Gönder (45s)</button>
        </div>
      </div>
    );
  }

  return (
    <div style={{ position: "absolute", inset: 0, background: "var(--vv-surface)", display: "flex", flexDirection: "column", padding: "60px 32px 32px" }}>
      <button onClick={onSkip} style={{
        position: "absolute", top: 24, left: 16, padding: 8,
        background: "none", border: "none", cursor: "pointer", color: "var(--vv-fg)",
      }}>
        <Icon name="x" size={20}/>
      </button>
      <button onClick={onSkip} style={{
        position: "absolute", top: 28, right: 24, padding: "4px 10px",
        background: "none", border: "none", cursor: "pointer",
        color: "var(--vv-fg-muted)", fontWeight: 800, fontSize: 12,
      }}>Atla</button>

      <div style={{ flex: 1, display: "flex", flexDirection: "column", justifyContent: "center", gap: 28 }}>
        <div style={{ display: "flex", justifyContent: "center" }}>
          <img src="../../assets/viaverse_icon.png" alt=""
            style={{ width: 96, height: 96, animation: "vv-spin-y 7s linear infinite" }}/>
        </div>

        <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
          <Input placeholder="Telefon numarası veya e-posta"/>
          <Input placeholder="Şifre" type="password"/>
          <button style={{
            alignSelf: "flex-end",
            background: "none", border: "none",
            color: "var(--vv-primary)", fontWeight: 700, fontSize: 11,
            padding: "4px 0", cursor: "pointer",
          }}>Şifremi unuttum</button>
        </div>

        <PrimaryButton fullWidth size="lg" onClick={() => setStep("otp")}>Giriş Yap</PrimaryButton>

        <div style={{ display: "flex", alignItems: "center", gap: 16 }}>
          <div style={{ flex: 1, borderTop: "1px solid var(--vv-border-subtle)" }}/>
          <span style={{ fontSize: 10, fontWeight: 800, color: "var(--vv-fg-muted)", letterSpacing: "0.2em" }}>VEYA</span>
          <div style={{ flex: 1, borderTop: "1px solid var(--vv-border-subtle)" }}/>
        </div>

        <div style={{ display: "flex", gap: 10 }}>
          <NeutralButton fullWidth style={{ height: 52, background: "#fff" }}>
            <img src="../../assets/ic_google.png" alt="" style={{ width: 18, height: 18 }}/>
            <span style={{ fontSize: 12, fontWeight: 800 }}>Google</span>
          </NeutralButton>
          <NeutralButton fullWidth style={{ height: 52, background: "#fff" }}>
            <img src="../../assets/ic_apple.png" alt="" style={{ width: 18, height: 18 }}/>
            <span style={{ fontSize: 12, fontWeight: 800 }}>Apple</span>
          </NeutralButton>
        </div>
      </div>
    </div>
  );
};

window.VVAuth = VVAuth;
