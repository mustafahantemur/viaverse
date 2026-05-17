"use client";

import Image from "next/image";
import { Button } from "@/components/primitives/Button";
import { Container } from "@/components/primitives/Container";
import { EyebrowBadge } from "@/components/primitives/EyebrowBadge";
import { Reveal } from "@/components/motion/Reveal";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./ProviderCTA.module.css";

interface Props {
    onSignup: () => void;
}

/** Dark forest-green CTA panel pitching the provider mode. */
export function ProviderCTA({ onSignup }: Props) {
    const { t } = useTranslation();
    const p = t.landing.provider;

    return (
        <section id="provider" className={styles.section}>
            <Container>
                <Reveal asTag="section">
                    <div className={styles.panel}>
                        <div className={styles.glow} aria-hidden />
                        <div className={styles.copy}>
                            <EyebrowBadge>{p.eyebrow}</EyebrowBadge>
                            <h2 className={styles.headline}>
                                {p.headline1}{" "}
                                <span className={styles.accentOrange}>{p.headlineAccent}</span>{" "}
                                {p.headline2}
                            </h2>
                            <p className={styles.lede}>{p.lede}</p>
                            <Button size="lg" onClick={onSignup}>
                                {p.cta}
                            </Button>
                        </div>
                        <div className={styles.media}>
                            <Image
                                src="https://images.unsplash.com/photo-1556761175-b413da4baf72?auto=format&fit=crop&w=900&q=80"
                                alt="A provider at work"
                                width={720}
                                height={576}
                                className={styles.mediaImage}
                            />
                            <Image
                                src="/brand/assets/viaverse_icon_silver_green.png"
                                alt="Viaverse"
                                width={120}
                                height={120}
                                className={styles.mediaMark}
                            />
                        </div>
                    </div>
                </Reveal>
            </Container>
        </section>
    );
}
