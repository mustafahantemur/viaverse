"use client";

import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import {
    ApiCallError,
    getRequiredConsents,
    register,
    start,
    verifyOtp,
    type RequiredConsents,
} from "../lib/authClient";

type Stage = "identifier" | "otp" | "details";

/**
 * Three-stage registration:
 *   1. identifier → server dispatches OTP
 *   2. otp        → server returns a registration token
 *   3. details    → display name + password + consents → account created
 *
 * If the user lands here from /login (unknown identifier) the identifier
 * is pre-filled via ?identifier= query param.
 */
export default function RegisterPage() {
    const router = useRouter();
    const search = useSearchParams();
    const initialIdentifier = search.get("identifier") ?? "";

    const [stage, setStage] = useState<Stage>(initialIdentifier ? "identifier" : "identifier");
    const [identifier, setIdentifier] = useState(initialIdentifier);
    const [flowId, setFlowId] = useState<string | null>(null);
    const [otp, setOtp] = useState("");
    const [registrationToken, setRegistrationToken] = useState<string | null>(null);
    const [displayName, setDisplayName] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [password, setPassword] = useState("");
    const [marketingConsentAccepted, setMarketingConsentAccepted] = useState(false);
    const [requiredAccepted, setRequiredAccepted] = useState<Record<string, boolean>>({});

    const [consents, setConsents] = useState<RequiredConsents | null>(null);
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (stage === "details" && !consents) {
            getRequiredConsents().then(setConsents).catch(() => setConsents(null));
        }
    }, [stage, consents]);

    async function handleSubmitIdentifier(event: React.FormEvent) {
        event.preventDefault();
        setBusy(true);
        setError(null);
        try {
            const result = await start(identifier.trim());
            if (result.nextStep === "OTP_REQUIRED" && result.flowId) {
                setFlowId(result.flowId);
                setStage("otp");
            } else {
                setError("This identifier already has an account. Sign in instead.");
            }
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
            const result = await verifyOtp(flowId, otp);
            setRegistrationToken(result.registrationToken);
            setStage("details");
        } catch (caught) {
            setError(formatError(caught));
        } finally {
            setBusy(false);
        }
    }

    async function handleSubmitDetails(event: React.FormEvent) {
        event.preventDefault();
        if (!registrationToken || !consents) return;
        setBusy(true);
        setError(null);
        try {
            const accepted = consents.required
                .filter((doc) => requiredAccepted[doc.type])
                .map((doc) => doc.type);
            await register({
                registrationToken,
                displayName: displayName.trim(),
                firstName: firstName.trim() || undefined,
                lastName: lastName.trim() || undefined,
                password,
                acceptedRequiredConsents: accepted,
                marketingConsentAccepted,
            });
            router.push("/");
        } catch (caught) {
            setError(formatError(caught));
        } finally {
            setBusy(false);
        }
    }

    const allRequiredAccepted = consents
        ? consents.required.every((doc) => requiredAccepted[doc.type])
        : false;

    return (
        <main className="shell">
            <form
                className="auth-card"
                onSubmit={
                    stage === "identifier"
                        ? handleSubmitIdentifier
                        : stage === "otp"
                          ? handleSubmitOtp
                          : handleSubmitDetails
                }
            >
                <h2>Create account</h2>
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
                            {busy ? "Sending OTP…" : "Send verification code"}
                        </button>
                    </>
                )}

                {stage === "otp" && (
                    <>
                        <p className="secondary-link">
                            A 6-digit code has been sent to <strong>{identifier}</strong>. In local
                            dev open Mailpit at <code>http://localhost:8025</code>.
                        </p>
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

                {stage === "details" && (
                    <>
                        <label>
                            Display name
                            <input
                                type="text"
                                value={displayName}
                                onChange={(event) => setDisplayName(event.target.value)}
                                required
                            />
                        </label>
                        <label>
                            First name
                            <input
                                type="text"
                                value={firstName}
                                onChange={(event) => setFirstName(event.target.value)}
                            />
                        </label>
                        <label>
                            Last name
                            <input
                                type="text"
                                value={lastName}
                                onChange={(event) => setLastName(event.target.value)}
                            />
                        </label>
                        <label>
                            Password (10+ chars, mix of upper/lower/digit/symbol)
                            <input
                                type="password"
                                value={password}
                                onChange={(event) => setPassword(event.target.value)}
                                autoComplete="new-password"
                                required
                                minLength={10}
                            />
                        </label>

                        {consents && (
                            <>
                                {consents.required.map((doc) => (
                                    <label
                                        key={doc.type}
                                        style={{
                                            flexDirection: "row",
                                            alignItems: "flex-start",
                                            gap: 8,
                                        }}
                                    >
                                        <input
                                            type="checkbox"
                                            checked={!!requiredAccepted[doc.type]}
                                            onChange={(event) =>
                                                setRequiredAccepted({
                                                    ...requiredAccepted,
                                                    [doc.type]: event.target.checked,
                                                })
                                            }
                                            required
                                        />
                                        <span>
                                            I accept the{" "}
                                            <a href={doc.url} target="_blank" rel="noreferrer">
                                                {humanizeConsent(doc.type)} ({doc.version})
                                            </a>
                                        </span>
                                    </label>
                                ))}
                                <label
                                    style={{
                                        flexDirection: "row",
                                        alignItems: "flex-start",
                                        gap: 8,
                                    }}
                                >
                                    <input
                                        type="checkbox"
                                        checked={marketingConsentAccepted}
                                        onChange={(event) =>
                                            setMarketingConsentAccepted(event.target.checked)
                                        }
                                    />
                                    <span>
                                        (Optional) I want to receive product updates and offers
                                    </span>
                                </label>
                            </>
                        )}

                        <button
                            type="submit"
                            disabled={
                                busy ||
                                !displayName.trim() ||
                                !password ||
                                !allRequiredAccepted
                            }
                        >
                            {busy ? "Creating account…" : "Create account"}
                        </button>
                    </>
                )}

                <p className="secondary-link">
                    Already have an account? <Link href="/login">Sign in</Link>
                </p>
            </form>
        </main>
    );
}

function humanizeConsent(type: string): string {
    return type
        .toLowerCase()
        .split("_")
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ");
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
