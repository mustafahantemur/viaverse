"use client";

import { useState } from "react";
import { Button } from "@/components/primitives/Button";
import { Field } from "./Field";
import { FormError } from "./FormError";
import { OtpInput } from "./OtpInput";
import { PasswordField } from "./PasswordField";
import { useAsyncCallback } from "@/hooks/useAsyncCallback";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import {
    forgotPasswordComplete,
    forgotPasswordStart,
    forgotPasswordVerifyOtp,
} from "@/lib/authClient";
import { describeError } from "@/lib/authErrors";
import { normalizeIdentifier } from "@/lib/identifier";
import { evaluatePassword } from "@/lib/validation";

type ForgotStage = "identifier" | "otp" | "newPassword" | "done";

interface Props {
    initialIdentifier?: string;
    onDone: () => void;
    onBackToLogin: (identifier: string) => void;
}

export function ForgotPasswordFlow({
    initialIdentifier = "",
    onDone,
    onBackToLogin,
}: Props) {
    const { t } = useTranslation();
    const [stage, setStage] = useState<ForgotStage>("identifier");
    const [identifier, setIdentifier] = useState(initialIdentifier);
    const [flowId, setFlowId] = useState<string | null>(null);
    const [otp, setOtp] = useState("");
    const [resetToken, setResetToken] = useState<string | null>(null);
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const startFlow = useAsyncCallback(async () => {
        const result = await forgotPasswordStart(normalizeIdentifier(identifier));
        setFlowId(result.flowId);
        setStage("otp");
    });

    const verifyFlow = useAsyncCallback(async () => {
        if (!flowId) return;
        const result = await forgotPasswordVerifyOtp(flowId, otp);
        setResetToken(result.resetToken);
        setStage("newPassword");
    });

    const completeFlow = useAsyncCallback(async () => {
        if (!resetToken) return;
        await forgotPasswordComplete(resetToken, newPassword);
        setStage("done");
    });

    const passwordsMatch = newPassword === confirmPassword;
    const passwordIsValid = evaluatePassword(newPassword).isValid;
    const confirmError =
        confirmPassword.length === 0 || passwordsMatch
            ? undefined
            : t.auth.register.passwordMismatch;

    const startError = startFlow.cause ? describeError(startFlow.cause, t) : null;
    const verifyError = verifyFlow.cause ? describeError(verifyFlow.cause, t) : null;
    const completeError = completeFlow.cause ? describeError(completeFlow.cause, t) : null;

    return (
        <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
            <header>
                <h2 id="auth-modal-title" style={titleStyle}>
                    {t.auth.forgot.title}
                </h2>
                <p style={subtitleStyle}>{t.auth.forgot.subtitle}</p>
            </header>

            {stage === "identifier" && (
                <form
                    style={{ display: "flex", flexDirection: "column", gap: 14 }}
                    onSubmit={(event) => {
                        event.preventDefault();
                        startFlow.run();
                    }}
                >
                    <FormError>{startError}</FormError>
                    <Field
                        label={t.auth.forgot.identifierLabel}
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
                        {startFlow.pending ? t.common.loading : t.auth.forgot.submitStart}
                    </Button>
                </form>
            )}

            {stage === "otp" && (
                <form
                    style={{ display: "flex", flexDirection: "column", gap: 14 }}
                    onSubmit={(event) => {
                        event.preventDefault();
                        verifyFlow.run();
                    }}
                >
                    <FormError>{verifyError}</FormError>
                    <OtpInput
                        label={t.auth.forgot.otpLabel}
                        value={otp}
                        onChange={setOtp}
                        onComplete={(code) => {
                            if (code.length === 6 && !verifyFlow.pending) verifyFlow.run();
                        }}
                        autoFocus
                    />
                    <Button
                        type="submit"
                        size="lg"
                        fullWidth
                        disabled={verifyFlow.pending || otp.length !== 6}
                    >
                        {verifyFlow.pending ? t.common.loading : t.auth.forgot.submitVerify}
                    </Button>
                </form>
            )}

            {stage === "newPassword" && (
                <form
                    style={{ display: "flex", flexDirection: "column", gap: 14 }}
                    onSubmit={(event) => {
                        event.preventDefault();
                        completeFlow.run();
                    }}
                >
                    <FormError>{completeError}</FormError>
                    <PasswordField
                        label={t.auth.forgot.newPasswordLabel}
                        value={newPassword}
                        onChange={(event) => setNewPassword(event.target.value)}
                        autoFocus
                        autoComplete="new-password"
                        required
                        showStrengthMeter
                    />
                    <PasswordField
                        label={t.auth.forgot.newPasswordConfirmLabel}
                        value={confirmPassword}
                        onChange={(event) => setConfirmPassword(event.target.value)}
                        autoComplete="new-password"
                        required
                        error={confirmError}
                    />
                    <Button
                        type="submit"
                        size="lg"
                        fullWidth
                        disabled={
                            completeFlow.pending || !passwordIsValid || !passwordsMatch
                        }
                    >
                        {completeFlow.pending ? t.common.loading : t.auth.forgot.submitComplete}
                    </Button>
                </form>
            )}

            {stage === "done" && (
                <>
                    <p style={{ ...subtitleStyle, color: "var(--vv-fg)" }}>
                        {t.auth.forgot.successMessage}
                    </p>
                    <Button size="lg" fullWidth onClick={onDone}>
                        {t.auth.forgot.backToLogin}
                    </Button>
                </>
            )}

            <p style={footerStyle}>
                <button
                    type="button"
                    onClick={() => onBackToLogin(identifier.trim())}
                    style={inlineLink}
                >
                    {t.auth.forgot.backToLogin}
                </button>
            </p>
        </div>
    );
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
