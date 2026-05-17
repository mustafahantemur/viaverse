"use client";

import { useMemo, useState } from "react";
import { Button } from "@/components/primitives/Button";
import { ConsentList } from "./ConsentList";
import { Field } from "./Field";
import { FormError } from "./FormError";
import { OtpInput } from "./OtpInput";
import { PasswordField } from "./PasswordField";
import { PhoneField } from "./PhoneField";
import { SocialButtons } from "./SocialButtons";
import { useAsyncCallback } from "@/hooks/useAsyncCallback";
import { useRequiredConsents } from "@/hooks/useRequiredConsents";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import { registerStart, registerVerifyEmail } from "@/lib/authClient";
import { describeError } from "@/lib/authErrors";
import { evaluatePassword } from "@/lib/validation";

type RegisterStage = "form" | "emailOtp";

interface Props {
    onRegistered: () => void;
    onSwitchToLogin: (identifier: string) => void;
}

/**
 * Two-stage registration:
 *   1. {@code form}     — full signup form (everything captured up front)
 *   2. {@code emailOtp} — verify email with 6-digit OTP, then account is created
 *
 * Phone number is intentionally not collected here. We learned the hard
 * way that asking for a phone (and an SMS OTP) at signup hurts conversion
 * for something most users add later; verifying a phone now happens from
 * the profile screen, gated by the same OTP flow the backend already
 * exposes. The server-side draft holds the form data so refreshing the
 * page mid-OTP isn't fatal (within the draft TTL).
 */
export function RegisterFlow({ onRegistered, onSwitchToLogin }: Props) {
    const { t, format } = useTranslation();
    const [stage, setStage] = useState<RegisterStage>("form");

    // Form state
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [displayName, setDisplayName] = useState("");
    const [email, setEmail] = useState("");
    const [phoneLocal, setPhoneLocal] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [requiredAccepted, setRequiredAccepted] = useState<Record<string, boolean>>({});
    const [marketingAccepted, setMarketingAccepted] = useState(false);

    // Draft progress
    const [draftId, setDraftId] = useState<string | null>(null);
    const [emailOtp, setEmailOtp] = useState("");

    const { consents } = useRequiredConsents();

    const passwordsMatch = password === confirmPassword;
    const passwordIsValid = evaluatePassword(password).isValid;
    const allConsentsAccepted = consents
        ? consents.required.every((doc) => requiredAccepted[doc.type])
        : false;

    const startFlow = useAsyncCallback(async () => {
        if (!consents) return;
        const result = await registerStart({
            email: email.trim().toLowerCase(),
            displayName: displayName.trim(),
            firstName: firstName.trim() || undefined,
            lastName: lastName.trim() || undefined,
            password,
            acceptedRequiredConsents: consents.required
                .filter((doc) => requiredAccepted[doc.type])
                .map((doc) => doc.type),
            marketingConsentAccepted: marketingAccepted,
        });
        setDraftId(result.draftId);
        setStage("emailOtp");
    });

    // Email OTP submit. The code is passed as an argument (rather than read
    // from state) so it stays correct when OtpInput auto-submits the moment
    // its sixth cell is filled — at that point the React state update from
    // the same render hasn't propagated, and reading `emailOtp` from the
    // closure would send the previous five-digit value to the server.
    const verifyEmailFlow = useAsyncCallback(async (otp: string) => {
        if (!draftId || otp.length !== 6) return;
        await registerVerifyEmail(draftId, otp);
        onRegistered();
    });

    const canSubmitForm = useMemo(
        () =>
            !startFlow.pending &&
            email.trim().length > 0 &&
            displayName.trim().length > 0 &&
            passwordIsValid &&
            passwordsMatch &&
            allConsentsAccepted,
        [
            startFlow.pending,
            email,
            displayName,
            passwordIsValid,
            passwordsMatch,
            allConsentsAccepted,
        ],
    );

    const confirmError =
        confirmPassword.length === 0 || passwordsMatch
            ? undefined
            : t.auth.register.passwordMismatch;

    const startError = startFlow.cause ? describeError(startFlow.cause, t) : null;
    const emailError = verifyEmailFlow.cause ? describeError(verifyEmailFlow.cause, t) : null;

    return (
        <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
            <header>
                <h2 id="auth-modal-title" style={titleStyle}>
                    {stage === "form" && t.auth.register.title}
                    {stage === "emailOtp" && t.auth.emailOtp.title}
                </h2>
                <p style={subtitleStyle}>
                    {stage === "form" && t.auth.register.subtitle}
                    {stage === "emailOtp" &&
                        format(t.auth.emailOtp.subtitle, { email: email.trim() })}
                </p>
            </header>

            {stage === "form" && (
                <form
                    style={{ display: "flex", flexDirection: "column", gap: 14 }}
                    onSubmit={(event) => {
                        event.preventDefault();
                        startFlow.run();
                    }}
                >
                    <FormError>{startError}</FormError>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
                        <Field
                            label={t.auth.register.firstNameLabel}
                            value={firstName}
                            onChange={(event) => setFirstName(event.target.value)}
                            autoComplete="given-name"
                        />
                        <Field
                            label={t.auth.register.lastNameLabel}
                            value={lastName}
                            onChange={(event) => setLastName(event.target.value)}
                            autoComplete="family-name"
                        />
                    </div>
                    <Field
                        label={t.auth.register.displayNameLabel}
                        hint={t.auth.register.displayNameHint}
                        value={displayName}
                        onChange={(event) => setDisplayName(event.target.value)}
                        required
                    />
                    <Field
                        label={t.auth.register.emailLabel}
                        type="email"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                        autoComplete="email"
                        required
                    />
                    {/* Phone is collected here for parity with the web brief;
                        verification happens later from the profile screen. The
                        value is currently stashed locally only — the backend
                        call stays phoneless until that flow ships. */}
                    <PhoneField
                        label={t.auth.register.phoneLabel}
                        hint={t.auth.register.phoneHint}
                        placeholder="5XXXXXXXXX"
                        value={phoneLocal}
                        onChange={setPhoneLocal}
                        autoComplete="tel"
                    />
                    <PasswordField
                        label={t.auth.register.passwordLabel}
                        hint={t.auth.register.passwordHint}
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        autoComplete="new-password"
                        required
                        showStrengthMeter
                    />
                    <PasswordField
                        label={t.auth.register.passwordConfirmLabel}
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
                            {t.common.loading}
                        </span>
                    )}
                    <Button type="submit" size="lg" fullWidth disabled={!canSubmitForm}>
                        {startFlow.pending ? t.auth.register.submitting : t.auth.register.submit}
                    </Button>
                    <SocialButtons
                        variant="register"
                        onGoogle={() => startSocial("google")}
                        onApple={() => startSocial("apple")}
                    />
                </form>
            )}

            {stage === "emailOtp" && (
                <form
                    style={{ display: "flex", flexDirection: "column", gap: 14 }}
                    onSubmit={(event) => {
                        event.preventDefault();
                        verifyEmailFlow.run(emailOtp);
                    }}
                >
                    <FormError>{emailError}</FormError>
                    <OtpInput
                        label={t.auth.emailOtp.label}
                        hint={t.auth.emailOtp.resendHint}
                        value={emailOtp}
                        onChange={setEmailOtp}
                        onComplete={(code) => {
                            if (code.length === 6 && !verifyEmailFlow.pending) verifyEmailFlow.run(code);
                        }}
                        autoFocus
                    />
                    {isDevMailpitEnabled() && (
                        <span style={devHintStyle}>{t.auth.emailOtp.devHint}</span>
                    )}
                    <Button
                        type="submit"
                        size="lg"
                        fullWidth
                        disabled={verifyEmailFlow.pending || emailOtp.length !== 6}
                    >
                        {verifyEmailFlow.pending
                            ? t.auth.emailOtp.submitting
                            : t.auth.emailOtp.submit}
                    </Button>
                </form>
            )}

            <p style={footerStyle}>
                {t.auth.register.haveAccount}{" "}
                <button
                    type="button"
                    onClick={() => onSwitchToLogin(email.trim())}
                    style={inlineLink}
                >
                    {t.auth.register.signIn}
                </button>
            </p>
        </div>
    );
}

function startSocial(provider: "google" | "apple") {
    if (typeof window !== "undefined") {
        window.open(`/api/auth/social/${provider}`, "_blank", "noopener,noreferrer");
    }
}

function isDevMailpitEnabled(): boolean {
    return (
        process.env.NODE_ENV === "development" ||
        process.env.NEXT_PUBLIC_DEV_MAILPIT_HINT === "true"
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

const devHintStyle: React.CSSProperties = {
    fontSize: 12,
    color: "var(--vv-fg-muted)",
    background: "var(--vv-surface-muted)",
    padding: "8px 10px",
    borderRadius: "var(--vv-radius-sm)",
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
