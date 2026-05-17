import { Container } from "@/components/primitives/Container";
import { Reveal } from "@/components/motion/Reveal";
import styles from "./FeatureGrid.module.css";

interface Feature {
    title: string;
    description: string;
}

const FEATURES: readonly Feature[] = [
    {
        title: "Çevrendeki canlı akış",
        description:
            "Mahalleni saran küçük talepler, duyurular ve hizmet ilanları — hepsi tek bir akışta, mesafeye göre sıralı.",
    },
    {
        title: "Hızlı teklif, hızlı dönüş",
        description:
            "Talep oluştur, doğrulanmış ustalardan saatler içinde teklif al. Ya da kendi yeteneğinle teklif vermeye başla.",
    },
    {
        title: "Şeffaf ücretlendirme",
        description:
            "Viaverse üzerinden gelen tekliflerde sadece %10 komisyon kesilir. Sürpriz yok, gizli ücret yok.",
    },
] as const;

/**
 * Renders the three value-prop cards under the hero. Plain CSS grid, no
 * carousel or animation library — staggered reveal is the only motion.
 */
export function FeatureGrid() {
    return (
        <section id="features" className={styles.section}>
            <Container>
                <div className={styles.grid}>
                    {FEATURES.map((feature, index) => (
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
