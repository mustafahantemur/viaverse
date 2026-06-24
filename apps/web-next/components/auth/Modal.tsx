"use client";

import { useEffect, useRef } from "react";
import type { ReactNode } from "react";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./Modal.module.css";

interface Props {
    isOpen: boolean;
    onClose: () => void;
    /** Spoken by screen readers when the modal opens. */
    labelledBy?: string;
    children: ReactNode;
}

/**
 * Accessible modal. Escape and the explicit close button trigger
 * {@code onClose}; clicking the overlay does NOT — once you start an
 * auth flow we want the user to use the close button on purpose, not
 * lose state to a stray click.
 */
export function Modal({ isOpen, onClose, labelledBy, children }: Props) {
    const surfaceRef = useRef<HTMLDivElement | null>(null);
    const { t } = useTranslation();

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
        <div className={styles.overlay} role="presentation">
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
                    aria-label={t.auth.modal.close}
                    className={styles.close}
                >
                    ×
                </button>
                {children}
            </div>
        </div>
    );
}
