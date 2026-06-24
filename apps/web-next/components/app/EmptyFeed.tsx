"use client";

import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./EmptyFeed.module.css";

/**
 * Stand-in for the feed widget that lands when marketplace-service
 * ships. Today it's a single illustrated panel so the screen doesn't
 * end with a blank rectangle. Replace with `<NearbyFeed />` once the
 * feed endpoint exists.
 */
export function EmptyFeed() {
    const { t } = useTranslation();
    return (
        <section className={styles.panel}>
            <div className={styles.illustration} aria-hidden>
                <CompassGlyph />
            </div>
            <p className={styles.copy}>{t.home.emptyFeed}</p>
        </section>
    );
}

function CompassGlyph() {
    return (
        <svg viewBox="0 0 120 120" fill="none" aria-hidden>
            <circle cx="60" cy="60" r="46" stroke="var(--vv-primary)" strokeWidth="2.5" strokeDasharray="4 6" />
            <path
                d="M70 50 L62 70 L50 62 L58 50 Z"
                fill="var(--vv-primary)"
                opacity="0.85"
            />
            <circle cx="60" cy="60" r="3" fill="var(--vv-trust-deep)" />
        </svg>
    );
}
