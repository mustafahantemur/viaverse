import { Container } from "@/components/primitives/Container";
import { BrandMark } from "@/components/primitives/BrandMark";
import styles from "./SiteFooter.module.css";

export function SiteFooter() {
    const currentYear = new Date().getFullYear();
    return (
        <footer className={styles.footer}>
            <Container className={styles.row}>
                <div className={styles.brand}>
                    <BrandMark size={28} />
                    <p className={styles.tagline}>
                        Yakındaki yardımı, küçük işleri ve hizmet verenleri buluşturan yer.
                    </p>
                </div>
                <nav aria-label="Footer" className={styles.cols}>
                    <ul>
                        <li className={styles.colTitle}>Ürün</li>
                        <li>
                            <a href="#features">Hizmetler</a>
                        </li>
                        <li>
                            <a href="#provider">Hizmet ver</a>
                        </li>
                    </ul>
                    <ul>
                        <li className={styles.colTitle}>Yardım</li>
                        <li>
                            <a href="#help">Yardım merkezi</a>
                        </li>
                        <li>
                            <a href="#guide">Rehber</a>
                        </li>
                    </ul>
                    <ul>
                        <li className={styles.colTitle}>Yasal</li>
                        <li>
                            <a href="/legal/terms">Kullanım koşulları</a>
                        </li>
                        <li>
                            <a href="/legal/kvkk">KVKK</a>
                        </li>
                    </ul>
                </nav>
            </Container>
            <Container className={styles.bottom}>
                <span>© {currentYear} Viaverse</span>
                <span>Made with care in TR.</span>
            </Container>
        </footer>
    );
}
