"use client";

import { Container } from "@/components/primitives/Container";
import { Reveal } from "@/components/motion/Reveal";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./FeatureGrid.module.css";

/**
 * Renders the three value-prop cards under the hero. Plain CSS grid, no
 * carousel or animation library — staggered reveal is the only motion.
 */
export function FeatureGrid() {
    const { t } = useTranslation();
    const features = [
        { title: t.landing.features.f1Title, description: t.landing.features.f1Body },
        { title: t.landing.features.f2Title, description: t.landing.features.f2Body },
        { title: t.landing.features.f3Title, description: t.landing.features.f3Body },
    ];

    return (
        <section id="features" className={styles.section}>
            <Container>
                <div className={styles.grid}>
                    {features.map((feature, index) => (
                        <Reveal key={feature.title} delayMs={index * 80}>
                            <article className={styles.card}>
                                <h3 className={styles.title}>{feature.title}</h3>
                                <p className={styles.copy}>{feature.description}</p>
                            </article>
                        </Reveal>
                    ))}
                </div>
            </Container>
        </section>
    );
}
