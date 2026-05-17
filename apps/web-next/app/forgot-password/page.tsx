"use client";

import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useState } from "react";
import {
    ApiCallError,
    forgotPasswordComplete,
    forgotPasswordStart,
    forgotPasswordVerifyOtp,
} from "../lib/authClient";

type Stage = "identifier" | "otp" | "newPassword" | "done";

/**
 * Mirror of the server-side three-step forgot-password flow.
 */
export default function ForgotPasswordPage() {
    const router = useRouter();
    const search = useSearchParams();
    const initialIdentifier = search.get("identifier") ?? "";

    const [stage, setStage] = useState<Stage>("identifier");
    const [identifier, setIdentifier] = useState(initialIdentifier);
    const [flowId, setFlowId] = useState<string | null>(null);
    const [otp, setOtp] = useState("");
    const [resetToken, setResetToken] = useState<string | null>(null);
    const [newPassword, setNewPassword] = useState("");
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function handleSubmitIdentifier(event: React.FormEvent) {
        event.preventDefault();
        setBusy(true);
        setError(null);
        try {
            const result = await forgotPasswordStart(identifier.trim());
            setFlowId(result.flowId);
            setStage("otp");
        } catch (caught) {
            setError(formatError(caught));
        } finally {
            setBusy(false);
        }
    }

    async function handleSubmitOtp(event: React.FormEvent) {
        event.preventDefault();
        if (!flowId) return;
        setBusy(true);
        setError(null);
        try {
            const result = await forgotPasswordVerifyOtp(flowId, otp);
            setResetToken(result.resetToken);
            setStage("newPassword");
        } catch (caught) {
            setError(formatError(caught));
        } finally {
            setBusy(false);
        }
    }

    async function handleSubmitNewPassword(event: React.FormEvent) {
        event.preventDefault();
        if (!resetToken) return;
        setBusy(true);
        setError(null);
        try {
            await forgotPasswordComplete(resetToken, newPassword);
            setStage("done");
        } catch (caught) {
            setError(formatError(caught));
        } finally {
            setBusy(false);
        }
    }

    return (
        <main className="shell">
            <form
                className="auth-card"
                onSubmit={
                    stage === "identifier"
                        ? handleSubmitIdentifier
                        : stage === "otp"
                          ? handleSubmitOtp
                          : stage === "newPassword"
                            ? handleSubmitNewPassword
                            : (event) => {
                                  event.preventDefault();
                                  router.push("/login");
                              }
                }
            >
                <h2>Reset password</h2>
                {error && <p className="auth-error">{error}</p>}

                {stage === "identifier" && (
                    <>
                        <label>
                            Email or phone
                            <input
                                type="text"
                                value={identifier}
                                onChange={(event) => setIdentifier(event.target.value)}
                                autoComplete="username"
                                required
                            />
                        </label>
                        <button type="submit" disabled={busy || !identifier.trim()}>
                            {busy ? "Sending…" : "Send code"}
                        </button>
                        <p className="secondary-link">
                            If the identifier is registered, a code will be sent. The response is
                            the same either way for your privacy.
                        </p>
                    </>
                )}

                {stage === "otp" && (
                    <>
                        <label>
                            Verification code
                            <input
                                type="text"
                                inputMode="numeric"
                                pattern="\d{6}"
                                maxLength={6}
                                value={otp}
                                onChange={(event) => setOtp(event.target.value)}
                                autoComplete="one-time-code"
                                required
                            />
                        </label>
                        <button type="submit" disabled={busy || otp.length !== 6}>
                            {busy ? "Verifying…" : "Verify"}
                        </button>
                    </>
                )}

                {stage === "newPassword" && (
                    <>
                        <label>
                            New password
                            <input
                                type="password"
                                value={newPassword}
                                onChange={(event) => setNewPassword(event.target.value)}
                                autoComplete="new-password"
                                required
                                minLength={10}
                            />
                        </label>
                        <button type="submit" disabled={busy || !newPassword}>
                            {busy ? "Saving…" : "Save new password"}
                        </button>
                    </>
                )}

                {stage === "done" && (
                    <>
                        <p className="auth-success">
                            Password updated. You can now sign in with your new password.
                        </p>
                        <button type="submit">Go to sign in</button>
                    </>
                )}

                <p className="secondary-link">
                    <Link href="/login">Back to sign in</Link>
                </p>
            </form>
        </main>
    );
}

function formatError(caught: unknown): string {
    if (caught instanceof ApiCallError) {
        return caught.error.detail ?? caught.error.identityCode ?? caught.error.code ?? caught.message;
    }
    if (caught instanceof Error) {
        return caught.message;
    }
    return "Something went wrong";
}
