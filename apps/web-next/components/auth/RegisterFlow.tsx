"use client";

import { useMemo, useState } from "react";
import { Button } from "@/components/primitives/Button";
import { ConsentList } from "./ConsentList";
import { Field } from "./Field";
import { FormError } from "./FormError";
import { PasswordField } from "./PasswordField";
import { useAsyncCallback } from "@/hooks/useAsyncCallback";
import { useRequiredConsents } from "@/hooks/useRequiredConsents";
import { register, start, verifyOtp } from "@/lib/authClient";
import { evaluatePassword } from "@/lib/validation";

type Stage = "identifier" | "otp" | "details";

interface Props {
    initialIdentifier?: string;
    onRegistered: () => void;
    onSwitchToLogin: (identifier: string) => void;
}

export function RegisterFlow({ initialIdentifier = "", onRegistered, onSwitchToLogin }: Props) {
    const [stage, setStage] = useState<Stage>("identifier");
    const [identifier, setIdentifier] = useState(initialIdentifier);
    const [flowId, setFlowId] = useState<string | null>(null);
    const [otp, setOtp] = useState("");
    const [registrationToken, setRegistrationToken] = useState<string | null>(null);

    const [displayName, setDisplayName] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [requiredAccepted, setRequiredAccepted] = useState<Record<string, boolean>>({});
    const [marketingAccepted, setMarketingAccepted] = useState(false);

    const { consents } = useRequiredConsents();

    const startFlow = useAsyncCallback(async () => {
        const result = await start(identifier.trim());
        if (result.nextStep === "OTP_REQUIRED" && result.flowId) {
            setFlowId(result.flowId);
            setStage("otp");
        } else {
            // Identifier is known → push the user to login instead.
            onSwitchToLogin(identifier.trim());
        }
    });

    const verifyFlow = useAsyncCallback(async () => {
        if (!flowId) return;
        const result = await verifyOtp(flowId, otp);
        setRegistrationToken(result.registrationToken);
        setStage("details");
    });

    const passwordsMatch = password === confirmPassword;
    const passwordIsValid = evaluatePassword(password).isValid;
    const confirmError =
        confirmPassword.length === 0 || passwordsMatch
            ? undefined
            : "Passwords don't match";
    const allConsentsAccepted = consents
        ? consents.required.every((doc) => requiredAccepted[doc.type])
        : false;

    const completeFlow = useAsyncCallback(async () => {
        if (!registrationToken || !consents) return;
        await register({
            registrationToken,
            displayName: displayName.trim(),
            firstName: firstName.trim() || undefined,
            lastName: lastName.trim() || undefined,
            password,
            acceptedRequiredConsents: consents.required
                .filter((doc) => requiredAccepted[doc.type])
                .map((doc) => doc.type),
            marketingConsentAccepted: marketingAccepted,
        });
        onRegistered();
    });

    const canSubmitDetails = useMemo(
        () =>
            !completeFlow.pending &&
            displayName.trim().length > 0 &&
            passwordIsValid &&
            passwordsMatch &&
            allConsentsAccepted,
        [completeFlow.pending, displayName, passwordIsValid, passwordsMatch, allConsentsAccepted],
    );

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
                    Create account
                </h2>
                <p
                    style={{
                        margin: "6px 0 0",
                        color: "var(--vv-fg-muted)",
                        fontSize: 14,
                    }}
                >
                    {stage === "identifier" && "Free, takes about a minute."}
                    {stage === "otp" && (
                        <>
                            We sent a 6-digit code to <strong>{identifier}</strong>.
                        </>
                    )}
                    {stage === "details" && "Set a password and accept the legal terms."}
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
                        {startFlow.pending ? "Sending OTP…" : "Send verification code"}
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
                        hint="In local dev open Mailpit at http://localhost:8025"
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

            {stage === "details" && (
                <form
                    style={{ display: "flex", flexDirection: "column", gap: 14 }}
                    onSubmit={(event) => {
                        event.preventDefault();
                        completeFlow.run();
                    }}
                >
                    <FormError>{completeFlow.error}</FormError>
                    <Field
                        label="Display name"
                        value={displayName}
                        onChange={(event) => setDisplayName(event.target.value)}
                        autoFocus
                        required
                    />
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
                        <Field
                            label="First name"
                            value={firstName}
                            onChange={(event) => setFirstName(event.target.value)}
                        />
                        <Field
                            label="Last name"
                            value={lastName}
                            onChange={(event) => setLastName(event.target.value)}
                        />
                    </div>
                    <PasswordField
                        label="Password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
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

                    {consents ? (
                        <ConsentList
                            consents={consents}
                            accepted={requiredAccepted}
                            onToggle={(type, value) =>
                                setRequiredAccepted({ ...requiredAccepted, [type]: value })
                            }
                            marketingAccepted={marketingAccepted}
                            onMarketingToggle={setMarketingAccepted}
                        />
                    ) : (
                        <span style={{ fontSize: 12, color: "var(--vv-fg-muted)" }}>
                            Loading legal documents…
                        </span>
                    )}

                    <Button type="submit" size="lg" fullWidth disabled={!canSubmitDetails}>
                        {completeFlow.pending ? "Creating account…" : "Create account"}
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
                Already have an account?{" "}
                <button
                    type="button"
                    onClick={() => onSwitchToLogin(identifier.trim())}
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
                    Sign in
                </button>
            </p>
        </div>
    );
}
