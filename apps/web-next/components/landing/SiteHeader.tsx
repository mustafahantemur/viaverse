"use client";

import { useEffect, useState } from "react";
import { Button } from "@/components/primitives/Button";
import { BrandMark } from "@/components/primitives/BrandMark";
import { Container } from "@/components/primitives/Container";
import { HeaderToggles } from "./HeaderToggles";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./SiteHeader.module.css";

interface Props {
    onLogin: () => void;
    onSignup: () => void;
}

/**
 * Sticky top nav. Translucent at the top of the page, gains a hairline
 * and a stronger blur once the user scrolls past 16px. Hosts the
 * theme + language toggles next to the auth CTAs.
 */
export function SiteHeader({ onLogin, onSignup }: Props) {
    const [scrolled, setScrolled] = useState(false);
    const { t } = useTranslation();

    useEffect(() => {
        const onScroll = () => setScrolled(window.scrollY > 16);
        onScroll();
        window.addEventListener("scroll", onScroll, { passive: true });
        return () => window.removeEventListener("scroll", onScroll);
    }, []);

    return (
        <header className={[styles.header, scrolled && styles.scrolled].filter(Boolean).join(" ")}>
            <Container className={styles.row}>
                <a href="/" className={styles.brand} aria-label="Viaverse home">
                    <BrandMark />
                </a>

                <nav aria-label="Primary" className={styles.nav}>
                    <a href="#features">{t.landing.nav.services}</a>
                    <a href="#provider">{t.landing.nav.provider}</a>
                    <a href="#guide">{t.landing.nav.guide}</a>
                    <a href="#help">{t.landing.nav.help}</a>
                </nav>

                <div className={styles.actions}>
                    <HeaderToggles />
                    <Button variant="ghost" onClick={onLogin}>
                        {t.landing.cta.signIn}
                    </Button>
                    <Button variant="primary" onClick={onSignup}>
                        {t.landing.cta.createAccount}
                    </Button>
                </div>
            </Container>
        </header>
    );
}
