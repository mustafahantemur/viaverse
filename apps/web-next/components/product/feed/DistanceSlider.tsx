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

    return (
        <div className={styles.distanceField}>
            <div className={styles.distanceHeader}>
                <span className={styles.inlineFilterLabel}>{label}</span>
                <strong>{display}</strong>
            </div>
            <input
                type="range"
                min={1}
                max={31}
                value={sliderValue}
                onChange={(event) => {
                    const next = Number(event.target.value);
                    onChange(next > 30 ? "MAX" : next);
                }}
                aria-label="Mesafe filtresi"
            />
            <div className={styles.distanceTicks} aria-hidden>
                <span>1 km</span>
                <span>15 km</span>
                <span>30 km</span>
                <span>Max</span>
            </div>
        </div>
    );
}
