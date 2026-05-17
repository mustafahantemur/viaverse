"use client";

import Image from "next/image";
import { Button } from "@/components/primitives/Button";
import { Container } from "@/components/primitives/Container";
import { EyebrowBadge } from "@/components/primitives/EyebrowBadge";
import { Reveal } from "@/components/motion/Reveal";
import styles from "./Hero.module.css";

interface Props {
    onSignup: () => void;
    onProviderSignup: () => void;
}

/** Above-the-fold marketing hero. Mirrors the design system's WebHero composition. */
export function Hero({ onSignup, onProviderSignup }: Props) {
    return (
        <section className={styles.hero}>
            <Container className={styles.grid}>
                <Reveal>
                    <div className={styles.copy}>
                        <EyebrowBadge>Şu an çevrende olanlar</EyebrowBadge>
                        <h1 className={styles.headline}>
                            Yakında <span className={styles.accentOrange}>küçük bir yardım</span>.
                            <br />
                            İyi yapılmış{" "}
                            <span className={styles.accentGreen}>büyük bir iş</span>.
                        </h1>
                        <p className={styles.lede}>
                            Bir el iste, çevrende olanları paylaş, küçük işlere teklif al — ya da
                            kendi yeteneğini işe dönüştür. Viaverse, tam olarak nerede olduğunla
                            bağlantılı tek yer.
                        </p>
                        <div className={styles.actions}>
                            <Button size="lg" onClick={onSignup}>
                                Hesap oluştur — ücretsiz
                            </Button>
                            <Button size="lg" variant="outline" onClick={onProviderSignup}>
                                Hizmet vermeye başla →
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
    const items = [
        { value: "240k+", label: "yakınında aktif" },
        { value: "12k", label: "doğrulanmış usta" },
        { value: "%97", label: "geri dönüş oranı" },
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
