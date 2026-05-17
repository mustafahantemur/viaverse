"use client";

import type { ButtonHTMLAttributes, ReactNode } from "react";
import styles from "./Button.module.css";

type Variant = "primary" | "ghost" | "outline";
type Size = "md" | "lg";

interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: Variant;
    size?: Size;
    fullWidth?: boolean;
    leadingIcon?: ReactNode;
}

/**
 * Single source of truth for buttons. Variants:
 *   - primary  → orange CTA, used at most once per surface (design rule)
 *   - ghost    → transparent, header-style "Giriş yap"
 *   - outline  → bordered, used as the secondary action next to primary
 */
export function Button({
    variant = "primary",
    size = "md",
    fullWidth = false,
    leadingIcon,
    className,
    children,
    ...rest
}: Props) {
    const classes = [
        styles.btn,
        styles[`variant-${variant}`],
        styles[`size-${size}`],
        fullWidth && styles.full,
        className,
    ]
        .filter(Boolean)
        .join(" ");
    return (
        <button type="button" className={classes} {...rest}>
            {leadingIcon && <span className={styles.icon}>{leadingIcon}</span>}
            <span>{children}</span>
        </button>
    );
}
