"use client";

import type { InputHTMLAttributes, ReactNode } from "react";
import { useId } from "react";
import styles from "./Field.module.css";

interface Props extends Omit<InputHTMLAttributes<HTMLInputElement>, "id"> {
    label: ReactNode;
    hint?: ReactNode;
    error?: ReactNode;
}

/** Labelled text input. Hint shows by default, error replaces it when present. */
export function Field({ label, hint, error, ...rest }: Props) {
    const id = useId();
    return (
        <label htmlFor={id} className={styles.label}>
            <span className={styles.text}>{label}</span>
            <input
                id={id}
                className={[styles.input, error && styles.inputError].filter(Boolean).join(" ")}
                aria-invalid={error ? true : undefined}
                {...rest}
            />
            {error ? (
                <span className={styles.error}>{error}</span>
            ) : hint ? (
                <span className={styles.hint}>{hint}</span>
            ) : null}
        </label>
    );
}
