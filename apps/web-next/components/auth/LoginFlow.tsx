"use client";

import { useState } from "react";
import { Button } from "@/components/primitives/Button";
import { Field } from "./Field";
import { FormError } from "./FormError";
import { OtpInput } from "./OtpInput";
import { PasswordField } from "./PasswordField";
import { SocialButtons } from "./SocialButtons";
import { useAsyncCallback } from "@/hooks/useAsyncCallback";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import { passwordLogin, verifyTotp } from "@/lib/authClient";
import { describeError } from "@/lib/authErrors";
import { normalizeIdentifier } from "@/lib/identifier";

type LoginStage = "credentials" | "totp";

interface Props {
    /** Pre-fill identifier (e.g. carried from a register prompt). */
    initialIdentifier?: string;
    onAuthenticated: () => void;
    onSwitchToRegister: (identifier: string) => void;
    onForgotPassword: (identifier: string) => void;
}

/**
 * Single-step login: identifier + password together. If 2FA is on, the
 * server returns a partial-auth token and the user advances to the TOTP
 * stage. No identifier-first step — the user types both and submits.
 */
export function LoginFlow({
    initialIdentifier = "",
    onAuthenticated,
    onSwitchToRegister,
    onForgotPassword,
}: Props) {
    const { t } = useTranslation();
    const [stage, setStage] = useState<LoginStage>("credentials");
    const [identifier, setIdentifier] = useState(initialIdentifier);
    const [password, setPassword] = useState("");
    const [partialAuthToken, setPartialAuthToken] = useState<string | null>(null);
    const [totpCode, setTotpCode] = useState("");

    const loginFlow = useAsyncCallback(async () => {
        const normalized = normalizeIdentifier(identifier);
        const result = await passwordLogin(normalized, password);
        if (result.nextStep === "TOTP_REQUIRED" && result.partialAuthToken) {
            setPartialAuthToken(result.partialAuthToken);
            setStage("totp");
        } else {
            onAuthenticated();
        }
    });

    const totpFlow = useAsyncCallback(async () => {
        if (!partialAuthToken) return;
        await verifyTotp(partialAuthToken, totpCode);
        onAuthenticated();
    });

    const loginError = loginFlow.cause ? describeError(loginFlow.cause, t) : null;
    const totpError = totpFlow.cause ? describeError(totpFlow.cause, t) : null;

    return (
        <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
            <header>
                <h2 id="auth-modal-title" style={titleStyle}>
                    {stage === "credentials" ? t.auth.login.title : t.auth.totp.title}
                </h2>
                <p style={subtitleStyle}>
                    {stage === "credentials" ? t.auth.login.subtitle : t.auth.totp.subtitle}
                </p>
            </header>

            {stage === "credentials" && (
                <form
                    style={{ display: "flex", flexDirection: "column", gap: 14 }}
                    onSubmit={(event) => {
                        event.preventDefault();
                        loginFlow.run();
                    }}
                >
                    <FormError>{loginError}</FormError>
                    <Field
                        label={t.auth.login.identifierLabel}
                        placeholder={t.auth.login.identifierPlaceholder}
                        value={identifier}
                        onChange={(event) => setIdentifier(event.target.value)}
                        autoFocus
                        autoComplete="username"
                        required
                    />
                    <PasswordField
                        label={t.auth.login.passwordLabel}
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        autoComplete="current-password"
                        required
                    />
                    <Button
                        type="submit"
                        size="lg"
                        fullWidth
                        disabled={loginFlow.pending || !identifier.trim() || !password}
                    >
                        {loginFlow.pending ? t.auth.login.submitting : t.auth.login.submit}
                    </Button>
                    <button
                        type="button"
                        onClick={() => onForgotPassword(identifier.trim())}
                        style={inlineLink}
                    >
                        {t.auth.login.forgotPassword}
                    </button>
                    <SocialButtons
                        variant="login"
                        onGoogle={() => startSocial("google")}
                        onApple={() => startSocial("apple")}
                    />
                </form>
            )}

            {stage === "totp" && (
                <form
                    style={{ display: "flex", flexDirection: "column", gap: 14 }}
                    onSubmit={(event) => {
                        event.preventDefault();
                        totpFlow.run();
                    }}
                >
                    <FormError>{totpError}</FormError>
                    <OtpInput
                        label={t.auth.totp.label}
                        value={totpCode}
                        onChange={setTotpCode}
                        onComplete={(code) => {
                            if (code.length === 6 && !totpFlow.pending) totpFlow.run();
                        }}
                        autoFocus
                    />
                    <Button
                        type="submit"
                        size="lg"
                        fullWidth
                        disabled={totpFlow.pending || totpCode.length !== 6}
                    >
                        {totpFlow.pending ? t.auth.totp.submitting : t.auth.totp.submit}
                    </Button>
                </form>
            )}

            <p style={footerStyle}>
                {t.auth.login.noAccount}{" "}
                <button
                    type="button"
                    onClick={() => onSwitchToRegister(identifier.trim())}
                    style={inlineLink}
                >
                    {t.auth.login.createOne}
                </button>
            </p>
        </div>
    );
}

function startSocial(provider: "google" | "apple") {
    // Placeholder — wire to /api/auth/social/{provider} popup flow when the
    // OAuth client IDs are configured per environment. We keep the buttons
    // present so the UI commitment is visible even while wiring is pending.
    if (typeof window !== "undefined") {
        window.open(`/api/auth/social/${provider}`, "_blank", "noopener,noreferrer");
    }
}

const titleStyle: React.CSSProperties = {
    margin: 0,
    fontSize: 24,
    fontWeight: 800,
    letterSpacing: "-0.02em",
    color: "var(--vv-fg-strong)",
};

const subtitleStyle: React.CSSProperties = {
    margin: "6px 0 0",
    color: "var(--vv-fg-muted)",
    fontSize: 14,
};

const footerStyle: React.CSSProperties = {
    margin: 0,
    fontSize: 13,
    color: "var(--vv-fg-muted)",
    textAlign: "center",
};

const inlineLink: React.CSSProperties = {
    background: "transparent",
    border: "none",
    padding: 0,
    color: "var(--vv-primary)",
    fontWeight: 700,
    fontSize: "inherit",
    cursor: "pointer",
    textDecoration: "underline",
    textUnderlineOffset: 2,
};
