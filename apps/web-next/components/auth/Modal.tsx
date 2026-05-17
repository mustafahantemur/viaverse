"use client";

import { useEffect, useRef } from "react";
import type { ReactNode } from "react";
import styles from "./Modal.module.css";

interface Props {
    isOpen: boolean;
    onClose: () => void;
    /** Spoken by screen readers when the modal opens. */
    labelledBy?: string;
    children: ReactNode;
}

/**
 * Bare-bones accessible modal. Click-outside + Escape close, body scroll
 * lock while open, initial focus on the first focusable child. No external
 * deps — Next.js apps tend to inherit too many of those.
 */
export function Modal({ isOpen, onClose, labelledBy, children }: Props) {
    const surfaceRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        if (!isOpen) return;
        const previouslyFocused = document.activeElement as HTMLElement | null;
        const previousOverflow = document.body.style.overflow;
        document.body.style.overflow = "hidden";

        const onKey = (event: KeyboardEvent) => {
            if (event.key === "Escape") onClose();
        };
        document.addEventListener("keydown", onKey);

        // Move focus into the modal so keyboard users don't drop back to the page.
        const focusTarget = surfaceRef.current?.querySelector<HTMLElement>(
            "input, button, [tabindex]",
        );
        focusTarget?.focus();

        return () => {
            document.removeEventListener("keydown", onKey);
            document.body.style.overflow = previousOverflow;
            previouslyFocused?.focus?.();
        };
    }, [isOpen, onClose]);

    if (!isOpen) return null;

    return (
        <div
            className={styles.overlay}
            onClick={(event) => {
                if (event.target === event.currentTarget) onClose();
            }}
            role="presentation"
        >
            <div
                className={styles.surface}
                role="dialog"
                aria-modal="true"
                aria-labelledby={labelledBy}
                ref={surfaceRef}
            >
                <button
                    type="button"
                    onClick={onClose}
                    aria-label="Close"
                    className={styles.close}
                >
                    ×
                </button>
                {children}
            </div>
        </div>
    );
}
