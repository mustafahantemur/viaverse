"use client";

import { Container } from "@/components/primitives/Container";
import { BrandMark } from "@/components/primitives/BrandMark";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./SiteFooter.module.css";

export function SiteFooter() {
    const { t } = useTranslation();
    const currentYear = new Date().getFullYear();
    return (
        <footer className={styles.footer}>
            <Container className={styles.row}>
                <div className={styles.brand}>
                    <BrandMark size={28} />
                    <p className={styles.tagline}>{t.landing.footer.tagline}</p>
                </div>
                <nav aria-label="Footer" className={styles.cols}>
                    <ul>
                        <li className={styles.colTitle}>{t.landing.footer.product}</li>
                        <li>
                            <a href="#features">{t.landing.nav.services}</a>
                        </li>
                        <li>
                            <a href="#provider">{t.landing.nav.provider}</a>
                        </li>
                    </ul>
                    <ul>
                        <li className={styles.colTitle}>{t.landing.footer.help}</li>
                        <li>
                            <a href="#help">{t.landing.footer.helpCenter}</a>
                        </li>
                        <li>
                            <a href="#guide">{t.landing.nav.guide}</a>
                        </li>
                    </ul>
                    <ul>
                        <li className={styles.colTitle}>{t.landing.footer.legal}</li>
                        <li>
                            <a href="/legal/terms">{t.landing.footer.terms}</a>
                        </li>
                        <li>
                            <a href="/legal/kvkk">{t.landing.footer.kvkk}</a>
                        </li>
                    </ul>
                </nav>
            </Container>
            <Container className={styles.bottom}>
                <span>© {currentYear} Viaverse</span>
                <span>{t.landing.footer.madeIn}</span>
            </Container>
        </footer>
    );
}
