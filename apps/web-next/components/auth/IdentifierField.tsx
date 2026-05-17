"use client";

import {
    useId,
    type ChangeEvent,
    type InputHTMLAttributes,
    type ReactNode,
} from "react";
import styles from "./IdentifierField.module.css";

interface Props extends Omit<InputHTMLAttributes<HTMLInputElement>, "id"> {
    label: ReactNode;
    hint?: ReactNode;
    error?: ReactNode;
    /** Country dial code (e.g. "+90") shown when the value looks like a phone. */
    dialCode?: string;
}

const MAX_PHONE_DIGITS = 10;

/**
 * Single-line input that accepts either an email or a phone number.
 * As soon as the typed value looks phone-shaped (no @, has digits), the
 * dial-code chip slides in on the left of the input so the user sees the
 * exact server-side normalization that will be applied. Switches back to
 * a plain field the moment an @ appears.
 */
export function IdentifierField({
    label,
    hint,
    error,
    dialCode = "+90",
    value,
    onChange,
    ...rest
}: Props) {
    const id = useId();
    const raw = typeof value === "string" ? value : "";
    const showDial = looksLikePhone(raw);

    function handleChange(event: ChangeEvent<HTMLInputElement>) {
        const next = event.target.value;
        // When the user is clearly typing a phone (no '@'), cap to 10 digits
        // so they can't keep typing past a valid TR mobile length. Emails
        // pass through untouched.
        if (!next.includes("@")) {
            const digits = next.replace(/[^0-9]/g, "");
            if (digits.length > MAX_PHONE_DIGITS) {
                event.target.value = digits.slice(0, MAX_PHONE_DIGITS);
            }
        }
        onChange?.(event);
    }

    return (
        <label htmlFor={id} className={styles.label}>
            <span className={styles.text}>{label}</span>
            <div
                className={[
                    styles.row,
                    error && styles.rowError,
                ]
                    .filter(Boolean)
                    .join(" ")}
            >
                {showDial && (
                    <span className={styles.dial} aria-hidden>
                        {dialCode}
                    </span>
                )}
                <input
                    id={id}
                    className={styles.input}
                    aria-invalid={error ? true : undefined}
                    value={value}
                    onChange={handleChange}
                    {...rest}
                />
            </div>
            {error ? (
                <span className={styles.error}>{error}</span>
            ) : hint ? (
                <span className={styles.hint}>{hint}</span>
            ) : null}
        </label>
    );
}

function looksLikePhone(raw: string): boolean {
    const trimmed = raw.trim();
    if (!trimmed || trimmed.includes("@")) return false;
    // At least two digits before we commit to "this is a phone" — avoids
    // showing the chip for a single typo when the user is mid-email.
    const digits = trimmed.replace(/[^0-9]/g, "");
    return digits.length >= 2;
}
