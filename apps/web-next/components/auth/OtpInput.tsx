"use client";

import {
    useEffect,
    useRef,
    type ClipboardEvent,
    type KeyboardEvent,
    type ReactNode,
} from "react";
import { useId } from "react";
import styles from "./OtpInput.module.css";

interface Props {
    value: string;
    onChange: (next: string) => void;
    /** Number of digits expected. */
    length?: number;
    /** Auto-submit when all slots are filled. */
    onComplete?: (value: string) => void;
    label?: ReactNode;
    hint?: ReactNode;
    error?: ReactNode;
    autoFocus?: boolean;
    disabled?: boolean;
}

/**
 * Six separate square cells (configurable via length). Each cell holds one
 * digit; typing advances focus, backspace retreats, paste fills all cells.
 *
 * The cells share the `one-time-code` autocomplete hint, so OS-level OTP
 * autofill still works on iOS/Android.
 */
export function OtpInput({
    value,
    onChange,
    length = 6,
    onComplete,
    label,
    hint,
    error,
    autoFocus = false,
    disabled = false,
}: Props) {
    const inputsRef = useRef<Array<HTMLInputElement | null>>([]);
    const groupId = useId();

    const digits = normalize(value, length);

    useEffect(() => {
        if (autoFocus) inputsRef.current[0]?.focus();
    }, [autoFocus]);

    function commit(next: string) {
        onChange(next);
        if (next.length === length && onComplete) onComplete(next);
    }

    function setCell(index: number, raw: string) {
        const digit = raw.replace(/[^0-9]/g, "").slice(0, 1);
        const arr = digits.split("");
        // Pad missing slots so we can index safely.
        while (arr.length < length) arr.push("");
        arr[index] = digit;
        commit(arr.join("").slice(0, length));
        if (digit && index < length - 1) {
            inputsRef.current[index + 1]?.focus();
            inputsRef.current[index + 1]?.select();
        }
    }

    function onKeyDown(event: KeyboardEvent<HTMLInputElement>, index: number) {
        if (event.key === "Backspace") {
            if (!digits[index] && index > 0) {
                event.preventDefault();
                const arr = digits.split("");
                while (arr.length < length) arr.push("");
                arr[index - 1] = "";
                commit(arr.join(""));
                inputsRef.current[index - 1]?.focus();
            }
        } else if (event.key === "ArrowLeft" && index > 0) {
            event.preventDefault();
            inputsRef.current[index - 1]?.focus();
        } else if (event.key === "ArrowRight" && index < length - 1) {
            event.preventDefault();
            inputsRef.current[index + 1]?.focus();
        }
    }

    function onPaste(event: ClipboardEvent<HTMLInputElement>) {
        const text = event.clipboardData.getData("text").replace(/[^0-9]/g, "").slice(0, length);
        if (!text) return;
        event.preventDefault();
        commit(text);
        const nextIndex = Math.min(text.length, length - 1);
        inputsRef.current[nextIndex]?.focus();
    }

    return (
        <div className={styles.group} aria-describedby={hint || error ? `${groupId}-hint` : undefined}>
            {label ? (
                <span id={`${groupId}-label`} className={styles.label}>
                    {label}
                </span>
            ) : null}
            <div
                className={styles.cells}
                role="group"
                aria-labelledby={label ? `${groupId}-label` : undefined}
            >
                {Array.from({ length }).map((_, i) => (
                    <input
                        key={i}
                        ref={(el) => {
                            inputsRef.current[i] = el;
                        }}
                        className={[styles.cell, error && styles.cellError].filter(Boolean).join(" ")}
                        type="text"
                        inputMode="numeric"
                        pattern="\\d{1}"
                        maxLength={1}
                        autoComplete={i === 0 ? "one-time-code" : "off"}
                        value={digits[i] ?? ""}
                        onChange={(event) => setCell(i, event.target.value)}
                        onKeyDown={(event) => onKeyDown(event, i)}
                        onPaste={onPaste}
                        onFocus={(event) => event.target.select()}
                        disabled={disabled}
                        aria-invalid={error ? true : undefined}
                        aria-label={`${i + 1}`}
                    />
                ))}
            </div>
            {error ? (
                <span id={`${groupId}-hint`} className={styles.error}>
                    {error}
                </span>
            ) : hint ? (
                <span id={`${groupId}-hint`} className={styles.hint}>
                    {hint}
                </span>
            ) : null}
        </div>
    );
}

function normalize(raw: string, length: number): string {
    return raw.replace(/[^0-9]/g, "").slice(0, length);
}
