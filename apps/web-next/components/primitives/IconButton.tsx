"use client";

import type { ButtonHTMLAttributes, ReactNode } from "react";
import styles from "./IconButton.module.css";

interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
    children: ReactNode;
    /** Accessible label — passed to aria-label and tooltip title. */
    label: string;
}

/** Small circular button for header chrome (theme/language toggles, etc.). */
export function IconButton({ children, label, className, ...rest }: Props) {
    return (
        <button
            type="button"
            aria-label={label}
            title={label}
            className={[styles.btn, className].filter(Boolean).join(" ")}
            {...rest}
        >
            {children}
        </button>
    );
}
