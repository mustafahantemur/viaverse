"use client";

import styles from "@/components/product/ProductPages.module.css";
import type { DistanceValue } from "./feedModel";

export function DistanceSlider({
    value,
    onChange,
    label = "Mesafe",
}: {
    value: DistanceValue;
    onChange: (value: DistanceValue) => void;
    label?: string;
}) {
    const sliderValue = value === "MAX" ? 31 : value;
    const display = value === "MAX" ? "Maksimum" : `${value} km`;
    const pct = ((sliderValue - 1) / (31 - 1)) * 100;
    const clampedPct = Math.max(4, Math.min(96, pct));

    return (
        <div className={styles.distanceField}>
            <span className={styles.inlineFilterLabel}>{label}</span>
            <div className={styles.sliderTrack}>
                <span
                    className={styles.sliderLabel}
                    style={{ left: `${clampedPct}%` }}
                    aria-hidden
                >
                    {display}
                </span>
                <input
                    type="range"
                    min={1}
                    max={31}
                    value={sliderValue}
                    onChange={(event) => {
                        const next = Number(event.target.value);
                        onChange(next > 30 ? "MAX" : next);
                    }}
                    aria-label={`Mesafe filtresi: ${display}`}
                />
            </div>
        </div>
    );
}
