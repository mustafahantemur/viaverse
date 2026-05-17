"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import {
    ApiCallError,
    passwordLogin,
    start,
    verifyTotp,
} from "../lib/authClient";

/**
 * Two-stage login: first /auth/start tells us if the identifier is known
 * (PASSWORD_REQUIRED) or new (OTP_REQUIRED, redirect to register flow).
 * On password-login the server may respond TOTP_REQUIRED — we then
 * collect a 6-digit code and call /verify-totp.
 */
export default function LoginPage() {
    const router = useRouter();
    const [stage, setStage] = useState<"identifier" | "password" | "totp">("identifier");
    const [identifier, setIdentifier] = useState("");
    const [password, setPassword] = useState("");
    const [partialAuthToken, setPartialAuthToken] = useState<string | null>(null);
    const [totpCode, setTotpCode] = useState("");
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function handleSubmitIdentifier(event: React.FormEvent) {
        event.preventDefault();
        setBusy(true);
        setError(null);
        try {
            const result = await start(identifier.trim());
            if (result.nextStep === "PASSWORD_REQUIRED") {
                setStage("password");
            } else {
                // Unknown identifier → onboarding. Push the user to register.
                router.push(`/register?identifier=${encodeURIComponent(identifier.trim())}`);
            }
        } catch (caught) {
            setError(formatError(caught));
        } finally {
            setBusy(false);
        }
    }

    async function handleSubmitPassword(event: React.FormEvent) {
        event.preventDefault();
        setBusy(true);
        setError(null);
        try {
            const result = await passwordLogin(identifier.trim(), password);
            if (result.nextStep === "TOTP_REQUIRED" && result.partialAuthToken) {
                setPartialAuthToken(result.partialAuthToken);
                setStage("totp");
            } else {
                router.push("/");
            }
        } catch (caught) {
            setError(formatError(caught));
        } finally {
            setBusy(false);
        }
    }

    async function handleSubmitTotp(event: React.FormEvent) {
        event.preventDefault();
        if (!partialAuthToken) return;
        setBusy(true);
        setError(null);
        try {
            await verifyTotp(partialAuthToken, totpCode);
            router.push("/");
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
                        : stage === "password"
                          ? handleSubmitPassword
                          : handleSubmitTotp
                }
            >
                <h2>Sign in</h2>
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
                            {busy ? "Checking…" : "Continue"}
                        </button>
                    </>
                )}

                {stage === "password" && (
                    <>
                        <label>
                            Password for <strong>{identifier}</strong>
                            <input
                                type="password"
                                value={password}
                                onChange={(event) => setPassword(event.target.value)}
                                autoComplete="current-password"
                                required
                            />
                        </label>
                        <button type="submit" disabled={busy || !password}>
                            {busy ? "Signing in…" : "Sign in"}
                        </button>
                        <p className="secondary-link">
                            <Link href={`/forgot-password?identifier=${encodeURIComponent(identifier)}`}>
                                Forgot password?
                            </Link>
                        </p>
                    </>
                )}

                {stage === "totp" && (
                    <>
                        <label>
                            6-digit code from your authenticator app
                            <input
                                type="text"
                                inputMode="numeric"
                                pattern="\d{6}"
                                maxLength={6}
                                value={totpCode}
                                onChange={(event) => setTotpCode(event.target.value)}
                                autoComplete="one-time-code"
                                required
                            />
                        </label>
                        <button type="submit" disabled={busy || totpCode.length !== 6}>
                            {busy ? "Verifying…" : "Verify"}
                        </button>
                    </>
                )}

                <p className="secondary-link">
                    No account? <Link href="/register">Create one</Link>
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
