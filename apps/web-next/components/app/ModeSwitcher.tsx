"use client";

import { useEffect, useRef, useState } from "react";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import { updateActiveMode, type ActiveMode, type CurrentProfileView } from "@/lib/authClient";
import styles from "./ModeSwitcher.module.css";

interface Props {
    profile: CurrentProfileView | null;
    onChange: (next: CurrentProfileView) => void;
}

/**
 * One-tap mode switcher for the top bar. Reads the current profile's
 * enabled capabilities; shows the active one as a pill, opens a small
 * menu listing the others on click. Modes the user hasn't enabled yet
 * are still listed but disabled with a tooltip pointing at the profile
 * screen. The chip itself is hidden until the profile has loaded so we
 * don't flash an empty pill on first paint.
 */
export function ModeSwitcher({ profile, onChange }: Props) {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [busy, setBusy] = useState(false);
    const containerRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        if (!open) return;
        const onDocClick = (event: MouseEvent) => {
            if (!containerRef.current?.contains(event.target as Node)) setOpen(false);
        };
        document.addEventListener("mousedown", onDocClick);
        return () => document.removeEventListener("mousedown", onDocClick);
    }, [open]);

    if (!profile) return null;

    const active = profile.activeMode;
    const enabled = new Set(
        profile.capabilities
            .filter((c) => c.status === "ENABLED")
            .map((c) => c.capability),
    );
    // Customer is always implicitly available.
    enabled.add("CUSTOMER");

    const labelFor = (mode: ActiveMode) => {
        switch (mode) {
            case "CUSTOMER":
                return t.home.modeSwitch.customer;
            case "INDIVIDUAL_PROVIDER":
                return t.home.modeSwitch.individualProvider;
            case "BUSINESS":
                return t.home.modeSwitch.business;
        }
    };

    async function pick(mode: ActiveMode) {
        if (mode === active || busy) return;
        if (!enabled.has(mode)) return;
        setBusy(true);
        try {
            const next = await updateActiveMode(mode);
            onChange(next);
            setOpen(false);
        } catch {
            // Surfaced by parent error toast eventually; the pill just
            // stays on the previous mode.
        } finally {
            setBusy(false);
        }
    }

    const modes: ActiveMode[] = ["CUSTOMER", "INDIVIDUAL_PROVIDER", "BUSINESS"];

    return (
        <div className={styles.wrap} ref={containerRef}>
            <button
                type="button"
                className={[styles.pill, accentClass(active)].join(" ")}
                aria-haspopup="menu"
                aria-expanded={open}
                onClick={() => setOpen((v) => !v)}
                disabled={busy}
            >
                <span className={styles.dot} aria-hidden />
                <span className={styles.pillLabel}>{labelFor(active)}</span>
            </button>
            {open && (
                <div role="menu" className={styles.menu}>
                    <span className={styles.menuTitle}>{t.home.modeSwitch.label}</span>
                    {modes.map((mode) => {
                        const isActive = mode === active;
                        const isEnabled = enabled.has(mode);
                        return (
                            <button
                                key={mode}
                                type="button"
                                role="menuitemradio"
                                aria-checked={isActive}
                                className={[
                                    styles.option,
                                    isActive && styles.optionActive,
                                    !isEnabled && styles.optionLocked,
                                ]
                                    .filter(Boolean)
                                    .join(" ")}
                                disabled={!isEnabled || busy}
                                title={!isEnabled ? t.home.modeSwitch.tooltipEnable : undefined}
                                onClick={() => pick(mode)}
                            >
                                <span className={[styles.optionDot, accentClass(mode)].join(" ")} aria-hidden />
                                {labelFor(mode)}
                            </button>
                        );
                    })}
                </div>
            )}
        </div>
    );
}

function accentClass(mode: ActiveMode): string {
    switch (mode) {
        case "INDIVIDUAL_PROVIDER":
            return styles.accentProvider;
        case "BUSINESS":
            return styles.accentBusiness;
        default:
            return styles.accentCustomer;
    }
}
