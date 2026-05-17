"use client";

import Image from "next/image";
import { Button } from "@/components/primitives/Button";
import { Container } from "@/components/primitives/Container";
import { EyebrowBadge } from "@/components/primitives/EyebrowBadge";
import { Reveal } from "@/components/motion/Reveal";
import styles from "./ProviderCTA.module.css";

interface Props {
    onSignup: () => void;
}

/** Dark forest-green CTA panel pitching the provider mode. */
export function ProviderCTA({ onSignup }: Props) {
    return (
        <section id="provider" className={styles.section}>
            <Container>
                <Reveal asTag="section">
                    <div className={styles.panel}>
                        <div className={styles.glow} aria-hidden />
                        <div className={styles.copy}>
                            <EyebrowBadge>Hizmet veren modu</EyebrowBadge>
                            <h2 className={styles.headline}>
                                Küçük yükleri paylaşmak,{" "}
                                <span className={styles.accentOrange}>
                                    emeğe hak ettiği değeri
                                </span>{" "}
                                katmak için.
                            </h2>
                            <p className={styles.lede}>
                                Yeteneğini, deneyimini ya da işletmeni Viaverse'de görünür yap.
                                Gelen talepleri yönet, teklif ver ve aktif işlerini tek yerden
                                takip et.
                            </p>
                            <Button size="lg" onClick={onSignup}>
                                Hizmet vermeye başla
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
