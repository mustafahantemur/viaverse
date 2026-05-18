"use client";

import { EyebrowBadge } from "@/components/primitives/EyebrowBadge";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import type { ActiveMode, MeView } from "@/lib/authClient";
import styles from "./WelcomeHero.module.css";

interface Props {
    me: MeView | null;
    activeMode?: ActiveMode;
    /**
     * True when the user is seeing this screen immediately after creating
     * the account. Toggles the subtitle copy from "welcome back" to
     * "your account is ready". Today we treat `profileCompleted = false`
     * as the heuristic; a stricter "fresh signup" flag can replace this
     * once profile-service is wired.
     */
    isFreshSignup: boolean;
}

export function WelcomeHero({ me, activeMode = "CUSTOMER", isFreshSignup }: Props) {
    const { t, format } = useTranslation();
    const firstName = me?.firstName?.trim() || me?.displayName?.split(" ")[0] || "";
    const subtitle = isFreshSignup
        ? t.home.welcomeSubtitleNew
        : t.home.welcomeSubtitleReturning;
    return (
        <section className={styles.hero}>
            <EyebrowBadge>{t.home.modeLabels[activeMode]}</EyebrowBadge>
            <h1 className={styles.heading}>
                {format(t.home.welcomeHeading, { name: firstName || "👋" })}
            </h1>
            <p className={styles.subtitle}>{subtitle}</p>
        </section>
    );
}
