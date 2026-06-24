"use client";

import type { InputHTMLAttributes, ReactNode } from "react";
import { useId, useState } from "react";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./Field.module.css";
import {
    describePasswordIssue,
    evaluatePassword,
    type PasswordEvaluation,
    type PasswordIssue,
} from "@/lib/validation";

interface Props extends Omit<InputHTMLAttributes<HTMLInputElement>, "id" | "type"> {
    label: ReactNode;
    hint?: ReactNode;
    error?: ReactNode;
    /** Show the strength meter + checklist (only on the "set new password" field). */
    showStrengthMeter?: boolean;
}

/**
 * Password input with a show/hide toggle. When {@code showStrengthMeter} is on,
 * renders a 4-bar strength meter + remaining-requirements checklist driven by
 * {@link evaluatePassword} so the user can self-validate before submit.
 */
export function PasswordField({
    label,
    hint,
    error,
    showStrengthMeter = false,
    value,
    ...rest
}: Props) {
    const id = useId();
    const { locale } = useTranslation();
    const [visible, setVisible] = useState(false);
    const evaluation: PasswordEvaluation | null =
        showStrengthMeter && typeof value === "string" && value.length > 0
            ? evaluatePassword(value)
            : null;

    const showA11y = locale === "tr" ? "Parolayı göster" : "Show password";
    const hideA11y = locale === "tr" ? "Parolayı gizle" : "Hide password";

    return (
        <label htmlFor={id} className={styles.label}>
            <span className={styles.text}>{label}</span>
            <span className={styles.inputWrap}>
                <input
                    id={id}
                    type={visible ? "text" : "password"}
                    className={[styles.input, error && styles.inputError]
                        .filter(Boolean)
                        .join(" ")}
                    value={value}
                    aria-invalid={error ? true : undefined}
                    {...rest}
                />
                <button
                    type="button"
                    onClick={() => setVisible((v) => !v)}
                    aria-pressed={visible}
                    aria-label={visible ? hideA11y : showA11y}
                    className={styles.toggle}
                    tabIndex={-1}
                >
                    {visible ? <EyeOffIcon /> : <EyeIcon />}
                </button>
            </span>

            {error ? (
                <span className={styles.error}>{error}</span>
            ) : hint && !evaluation ? (
                <span className={styles.hint}>{hint}</span>
            ) : null}

            {evaluation && !error && <PasswordChecklist evaluation={evaluation} locale={locale} />}
        </label>
    );
}

function PasswordChecklist({
    evaluation,
    locale,
}: {
    evaluation: PasswordEvaluation;
    locale: string;
}) {
    const segments = [0, 1, 2, 3];
    const meterColor =
        evaluation.score <= 1
            ? "var(--vv-danger)"
            : evaluation.score <= 2
              ? "#F59E0B"
              : "var(--vv-trust)";

    const needsLabel = locale === "tr" ? "Gerekenler" : "Needs";

    return (
        <span style={{ display: "flex", flexDirection: "column", gap: 6, marginTop: 2 }}>
            <span style={{ display: "flex", gap: 4 }}>
                {segments.map((i) => (
                    <span
                        key={i}
                        style={{
                            height: 4,
                            flex: 1,
                            borderRadius: 2,
                            background: i < evaluation.score ? meterColor : "var(--vv-border-muted)",
                            transition: "background 200ms ease",
                        }}
                    />
                ))}
            </span>
            {evaluation.issues.length > 0 && (
                <span
                    style={{
                        fontSize: 12,
                        color: "var(--vv-fg-muted)",
                        lineHeight: 1.5,
                    }}
                >
                    {needsLabel}: {evaluation.issues.map((issue) => describeIssue(issue, locale)).join(" · ")}
                </span>
            )}
        </span>
    );
}

function EyeIcon() {
    return (
        <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            aria-hidden
        >
            <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12z" />
            <circle cx="12" cy="12" r="3" />
        </svg>
    );
}

function EyeOffIcon() {
    return (
        <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            aria-hidden
        >
            <path d="M17.94 17.94A10.94 10.94 0 0 1 12 19c-7 0-11-7-11-7a19.66 19.66 0 0 1 5.06-5.94M9.9 4.24A10.94 10.94 0 0 1 12 4c7 0 11 7 11 7a19.65 19.65 0 0 1-3.22 4.19" />
            <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
            <line x1="1" y1="1" x2="23" y2="23" />
        </svg>
    );
}

function describeIssue(issue: PasswordIssue, locale: string): string {
    if (locale !== "tr") return describePasswordIssue(issue);
    switch (issue) {
        case "tooShort":
            return "en az 10 karakter";
        case "tooLong":
            return "en fazla 128 karakter";
        case "missingLower":
            return "küçük harf";
        case "missingUpper":
            return "büyük harf";
        case "missingDigit":
            return "rakam";
        case "missingSymbol":
            return "sembol (örn. !@#$)";
    }
}
