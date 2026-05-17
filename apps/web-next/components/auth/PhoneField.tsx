"use client";

import { useId, useMemo, type ReactNode } from "react";
import styles from "./PhoneField.module.css";

/**
 * Country options for the dial-code picker. Only TR is selectable right
 * now, but the list keeps the future expansion path obvious — add more
 * entries, drop `comingSoon`, and the dropdown unlocks itself.
 */
interface CountryOption {
    code: string;          // ISO 3166-1 alpha-2
    label: string;
    dialCode: string;      // "+90"
    comingSoon?: boolean;
}

const COUNTRIES: CountryOption[] = [
    { code: "TR", label: "Türkiye", dialCode: "+90" },
];

const DEFAULT_COUNTRY = "TR";

interface Props {
    /** Local-portion of the number (digits after the dial code). */
    value: string;
    onChange: (next: string) => void;
    /** Normalized E.164 string is passed up whenever input changes. */
    onNormalizedChange?: (e164: string) => void;
    label?: ReactNode;
    hint?: ReactNode;
    error?: ReactNode;
    placeholder?: string;
    autoComplete?: string;
    required?: boolean;
    disabled?: boolean;
}

/**
 * Phone input with a country-code dropdown. The dropdown is visible but
 * currently locked to +90 (Türkiye); copy the structure when a new
 * country is approved.
 *
 * Returns a normalized E.164-ish string via {@link onNormalizedChange}
 * (just dial-code + digits, no spaces). The server still validates with
 * libphonenumber; we just give it a clean payload.
 */
export function PhoneField({
    value,
    onChange,
    onNormalizedChange,
    label,
    hint,
    error,
    placeholder,
    autoComplete = "tel-national",
    required = false,
    disabled = false,
}: Props) {
    const id = useId();
    const country = useMemo(
        () => COUNTRIES.find((c) => c.code === DEFAULT_COUNTRY) ?? COUNTRIES[0],
        [],
    );
    const dialCode = country.dialCode;

    function commit(raw: string) {
        const digits = raw.replace(/[^0-9]/g, "");
        onChange(digits);
        if (onNormalizedChange) {
            const normalized = digits ? `${dialCode}${digits}` : "";
            onNormalizedChange(normalized);
        }
    }

    return (
        <label htmlFor={id} className={styles.label}>
            {label ? <span className={styles.text}>{label}</span> : null}
            <div
                className={[
                    styles.row,
                    error && styles.rowError,
                    disabled && styles.rowDisabled,
                ]
                    .filter(Boolean)
                    .join(" ")}
            >
                <div className={styles.country} aria-label="Country code">
                    <select
                        className={styles.select}
                        value={country.code}
                        disabled
                        aria-disabled="true"
                        tabIndex={-1}
                    >
                        {COUNTRIES.map((c) => (
                            <option key={c.code} value={c.code}>
                                {c.dialCode}
                            </option>
                        ))}
                    </select>
                    <span className={styles.chev} aria-hidden>
                        ▾
                    </span>
                </div>
                <input
                    id={id}
                    className={styles.input}
                    type="tel"
                    inputMode="tel"
                    value={value}
                    onChange={(event) => commit(event.target.value)}
                    placeholder={placeholder}
                    autoComplete={autoComplete}
                    required={required}
                    disabled={disabled}
                    aria-invalid={error ? true : undefined}
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
