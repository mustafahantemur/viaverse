"use client";

import Image from "next/image";
import { Button } from "@/components/primitives/Button";
import { Container } from "@/components/primitives/Container";
import { EyebrowBadge } from "@/components/primitives/EyebrowBadge";
import { Reveal } from "@/components/motion/Reveal";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./Hero.module.css";

interface Props {
    onSignup: () => void;
    onProviderSignup: () => void;
}

/** Above-the-fold marketing hero. Mirrors the design system's WebHero composition. */
export function Hero({ onSignup, onProviderSignup }: Props) {
    const { t } = useTranslation();
    const hero = t.landing.hero;

    return (
        <section className={styles.hero}>
            <Container className={styles.grid}>
                <Reveal>
                    <div className={styles.copy}>
                        <EyebrowBadge>{hero.eyebrow}</EyebrowBadge>
                        <h1 className={styles.headline}>
                            {hero.headline1}{" "}
                            <span className={styles.accentOrange}>{hero.headlineAccent1}</span>.
                            <br />
                            {hero.headline2}{" "}
                            <span className={styles.accentGreen}>{hero.headlineAccent2}</span>.
                        </h1>
                        <p className={styles.lede}>{hero.lede}</p>
                        <div className={styles.actions}>
                            <Button size="lg" onClick={onSignup}>
                                {hero.ctaPrimary}
                            </Button>
                            <Button size="lg" variant="outline" onClick={onProviderSignup}>
                                {hero.ctaSecondary}
                            </Button>
                        </div>
                        <HeroStats />
                    </div>
                </Reveal>

                <Reveal delayMs={120}>
                    <HeroVisual />
                </Reveal>
            </Container>
        </section>
    );
}

function HeroStats() {
    const { t } = useTranslation();
    const items = [
        { value: "240k+", label: t.landing.hero.stats.nearbyActive },
        { value: "12k", label: t.landing.hero.stats.verifiedPros },
        { value: "%97", label: t.landing.hero.stats.returnRate },
    ];
    return (
        <div className={styles.stats}>
            {items.map((item) => (
                <div key={item.label}>
                    <div className={styles.statValue}>{item.value}</div>
                    <div className={styles.statLabel}>{item.label}</div>
                </div>
            ))}
        </div>
    );
}

function HeroVisual() {
    return (
        <div className={styles.visual}>
            <Image
                src="https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=900&q=80"
                alt="Neighbours helping each other"
                width={720}
                height={520}
                className={`${styles.visualImage} ${styles.visualPrimary}`}
                priority
            />
            <Image
                src="https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=600&q=80"
                alt="A craftsperson at work"
                width={480}
                height={260}
                className={`${styles.visualImage} ${styles.visualSecondary}`}
            />
            <Image
                src="https://images.unsplash.com/photo-1521737604893-d14cc237f11d?auto=format&fit=crop&w=600&q=80"
                alt="A team in a workshop"
                width={480}
                height={260}
                className={`${styles.visualImage} ${styles.visualTertiary}`}
            />
            <Image
                src="/brand/assets/viaverse_icon.png"
                alt="Viaverse"
                width={124}
                height={124}
                className={styles.visualMark}
            />
        </div>
    );
}
