"use client";

import { useState } from "react";
import { Button } from "@/components/primitives/Button";
import { Field } from "./Field";
import { FormError } from "./FormError";
import { PasswordField } from "./PasswordField";
import { useAsyncCallback } from "@/hooks/useAsyncCallback";
import {
    forgotPasswordComplete,
    forgotPasswordStart,
    forgotPasswordVerifyOtp,
} from "@/lib/authClient";
import { evaluatePassword } from "@/lib/validation";

type Stage = "identifier" | "otp" | "newPassword" | "done";

interface Props {
    initialIdentifier?: string;
    onDone: () => void;
    onBackToLogin: (identifier: string) => void;
}

export function ForgotPasswordFlow({ initialIdentifier = "", onDone, onBackToLogin }: Props) {
    const [stage, setStage] = useState<Stage>("identifier");
    const [identifier, setIdentifier] = useState(initialIdentifier);
    const [flowId, setFlowId] = useState<string | null>(null);
    const [otp, setOtp] = useState("");
    const [resetToken, setResetToken] = useState<string | null>(null);
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const startFlow = useAsyncCallback(async () => {
        const result = await forgotPasswordStart(identifier.trim());
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
        confirmPassword.length === 0 || passwordsMatch ? undefined : "Passwords don't match";

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
                    Reset password
                </h2>
                <p
                    style={{
                        margin: "6px 0 0",
                        color: "var(--vv-fg-muted)",
                        fontSize: 14,
                    }}
                >
                    {stage === "identifier" &&
                        "If the identifier is registered, we'll send a code."}
                    {stage === "otp" && "Enter the code we sent you."}
                    {stage === "newPassword" && "Pick something hard to guess."}
                    {stage === "done" && "Password updated."}
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
                        {startFlow.pending ? "Sending…" : "Send code"}
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
                    <FormError>{verifyFlow.error}</FormError>
                    <Field
                        label="Verification code"
                        value={otp}
                        onChange={(event) =>
                            setOtp(event.target.value.replace(/[^0-9]/g, "").slice(0, 6))
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
                        disabled={verifyFlow.pending || otp.length !== 6}
                    >
                        {verifyFlow.pending ? "Verifying…" : "Verify"}
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
                    <FormError>{completeFlow.error}</FormError>
                    <PasswordField
                        label="New password"
                        value={newPassword}
                        onChange={(event) => setNewPassword(event.target.value)}
                        autoFocus
                        autoComplete="new-password"
                        required
                        showStrengthMeter
                    />
                    <PasswordField
                        label="Confirm password"
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
                            completeFlow.pending ||
                            !passwordIsValid ||
                            !passwordsMatch
                        }
                    >
                        {completeFlow.pending ? "Saving…" : "Save new password"}
                    </Button>
                </form>
            )}

            {stage === "done" && (
                <Button size="lg" fullWidth onClick={onDone}>
                    Continue to sign in
                </Button>
            )}

            <p
                style={{
                    margin: 0,
                    fontSize: 13,
                    color: "var(--vv-fg-muted)",
                    textAlign: "center",
                }}
            >
                Remembered it?{" "}
                <button
                    type="button"
                    onClick={() => onBackToLogin(identifier.trim())}
                    style={{
                        background: "transparent",
                        border: "none",
                        padding: 0,
                        color: "var(--vv-primary)",
                        fontWeight: 700,
                        fontSize: "inherit",
                        cursor: "pointer",
                        textDecoration: "underline",
                        textUnderlineOffset: 2,
                    }}
                >
                    Back to sign in
                </button>
            </p>
        </div>
    );
}
