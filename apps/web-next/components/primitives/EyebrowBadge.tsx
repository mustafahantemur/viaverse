import type { ReactNode } from "react";
import styles from "./EyebrowBadge.module.css";

/** Pill-shaped status label with a trust-green dot. Used above section headings. */
export function EyebrowBadge({ children }: { children: ReactNode }) {
    return (
        <span className={styles.badge}>
            <span aria-hidden className={styles.dot} />
            {children}
        </span>
    );
}
