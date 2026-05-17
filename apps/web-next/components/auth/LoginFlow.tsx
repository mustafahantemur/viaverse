"use client";

import { useState } from "react";
import { Button } from "@/components/primitives/Button";
import { Field } from "./Field";
import { FormError } from "./FormError";
import { PasswordField } from "./PasswordField";
import { useAsyncCallback } from "@/hooks/useAsyncCallback";
import { passwordLogin, start, verifyTotp } from "@/lib/authClient";

type Stage = "identifier" | "password" | "totp";

interface Props {
    /** Pre-fill identifier (e.g. carried from a register prompt). */
    initialIdentifier?: string;
    onAuthenticated: () => void;
    onSwitchToRegister: (identifier: string) => void;
    onForgotPassword: (identifier: string) => void;
}

/**
 * Three-stage login state machine: identifier → password → optional TOTP.
 * Hosted inside the auth modal (or any future standalone screen). Owns its
 * own state; the parent supplies callbacks for navigation between flows.
 */
export function LoginFlow({
    initialIdentifier = "",
    onAuthenticated,
    onSwitchToRegister,
    onForgotPassword,
}: Props) {
    const [stage, setStage] = useState<Stage>("identifier");
    const [identifier, setIdentifier] = useState(initialIdentifier);
    const [password, setPassword] = useState("");
    const [partialAuthToken, setPartialAuthToken] = useState<string | null>(null);
    const [totpCode, setTotpCode] = useState("");

    const startFlow = useAsyncCallback(async () => {
        const result = await start(identifier.trim());
        if (result.nextStep === "PASSWORD_REQUIRED") {
            setStage("password");
        } else {
            // Unknown identifier → bounce to register, preserving the identifier.
            onSwitchToRegister(identifier.trim());
        }
    });

    const loginFlow = useAsyncCallback(async () => {
        const result = await passwordLogin(identifier.trim(), password);
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

    return (
        <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
            <header>
                <h2
                    id="auth-modal-title"
                    style={{
                        margin: 0,
                        fontSize: 24,
                        fontWeight: 800,
                        letterSpacing: "-0.02em",
                    }}
                >
                    Sign in
                </h2>
                <p
                    style={{
                        margin: "6px 0 0",
                        color: "var(--vv-fg-muted)",
                        fontSize: 14,
                    }}
                >
                    {stage === "identifier" && "We'll figure out the rest."}
                    {stage === "password" && (
                        <>
                            Continue as <strong>{identifier}</strong>.
                        </>
                    )}
                    {stage === "totp" && "Enter the code from your authenticator app."}
                </p>
            </header>

            {stage === "identifier" && (
                <form
                    style={{ display: "flex", flexDirection: "column", gap: 14 }}
                    onSubmit={(event) => {
                        event.preventDefault();
                        startFlow.run();
                    }}
                >
                    <FormError>{startFlow.error}</FormError>
                    <Field
                        label="Email or phone"
                        value={identifier}
                        onChange={(event) => setIdentifier(event.target.value)}
                        autoFocus
                        autoComplete="username"
                        required
                    />
                    <Button
                        type="submit"
                        size="lg"
                        fullWidth
                        disabled={startFlow.pending || !identifier.trim()}
                    >
                        {startFlow.pending ? "Checking…" : "Continue"}
                    </Button>
                </form>
            )}

            {stage === "password" && (
                <form
                    style={{ display: "flex", flexDirection: "column", gap: 14 }}
                    onSubmit={(event) => {
                        event.preventDefault();
                        loginFlow.run();
                    }}
                >
                    <FormError>{loginFlow.error}</FormError>
                    <PasswordField
                        label="Password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        autoFocus
                        autoComplete="current-password"
                        required
                    />
                    <Button
                        type="submit"
                        size="lg"
                        fullWidth
                        disabled={loginFlow.pending || !password}
                    >
                        {loginFlow.pending ? "Signing in…" : "Sign in"}
                    </Button>
                    <button
                        type="button"
                        onClick={() => onForgotPassword(identifier.trim())}
                        style={inlineLink}
                    >
                        Forgot password?
                    </button>
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
                    <FormError>{totpFlow.error}</FormError>
                    <Field
                        label="6-digit code"
                        value={totpCode}
                        onChange={(event) =>
                            setTotpCode(event.target.value.replace(/[^0-9]/g, "").slice(0, 6))
                        }
                        inputMode="numeric"
                        pattern="\d{6}"
                        autoComplete="one-time-code"
                        autoFocus
                        required
                    />
                    <Button
                        type="submit"
                        size="lg"
                        fullWidth
                        disabled={totpFlow.pending || totpCode.length !== 6}
                    >
                        {totpFlow.pending ? "Verifying…" : "Verify"}
                    </Button>
                </form>
            )}

            <p
                style={{
                    margin: 0,
                    fontSize: 13,
                    color: "var(--vv-fg-muted)",
                    textAlign: "center",
                }}
            >
                No account?{" "}
                <button
                    type="button"
                    onClick={() => onSwitchToRegister(identifier.trim())}
                    style={inlineLink}
                >
                    Create one
                </button>
            </p>
        </div>
    );
}

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
