"use client";

import type { InputHTMLAttributes, ReactNode } from "react";
import { useId, useState } from "react";
import styles from "./Field.module.css";
import { describePasswordIssue, evaluatePassword, type PasswordEvaluation } from "@/lib/validation";

interface Props extends Omit<InputHTMLAttributes<HTMLInputElement>, "id" | "type"> {
    label: ReactNode;
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
    error,
    showStrengthMeter = false,
    value,
    ...rest
}: Props) {
    const id = useId();
    const [visible, setVisible] = useState(false);
    const evaluation: PasswordEvaluation | null =
        showStrengthMeter && typeof value === "string" && value.length > 0
            ? evaluatePassword(value)
            : null;

    return (
        <label htmlFor={id} className={styles.label}>
            <span className={styles.text}>{label}</span>
            <span style={{ position: "relative", display: "block" }}>
                <input
                    id={id}
                    type={visible ? "text" : "password"}
                    className={[styles.input, error && styles.inputError]
                        .filter(Boolean)
                        .join(" ")}
                    value={value}
                    aria-invalid={error ? true : undefined}
                    style={{ width: "100%", paddingRight: 64 }}
                    {...rest}
                />
                <button
                    type="button"
                    onClick={() => setVisible((v) => !v)}
                    aria-pressed={visible}
                    aria-label={visible ? "Hide password" : "Show password"}
                    style={{
                        position: "absolute",
                        right: 8,
                        top: "50%",
                        transform: "translateY(-50%)",
                        background: "transparent",
                        border: "none",
                        color: "var(--vv-fg-muted)",
                        fontSize: 12,
                        fontWeight: 700,
                        cursor: "pointer",
                        padding: "6px 10px",
                    }}
                >
                    {visible ? "Hide" : "Show"}
                </button>
            </span>

            {error ? <span className={styles.error}>{error}</span> : null}

            {evaluation && !error && (
                <PasswordChecklist evaluation={evaluation} />
            )}
        </label>
    );
}

function PasswordChecklist({ evaluation }: { evaluation: PasswordEvaluation }) {
    const segments = [0, 1, 2, 3];
    const meterColor =
        evaluation.score <= 1
            ? "var(--vv-danger)"
            : evaluation.score <= 2
              ? "#F59E0B"
              : "var(--vv-trust)";

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
                    Needs: {evaluation.issues.map(describePasswordIssue).join(" · ")}
                </span>
            )}
        </span>
    );
}
