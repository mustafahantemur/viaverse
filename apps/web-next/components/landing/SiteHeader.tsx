"use client";

import { useEffect, useState } from "react";
import { Button } from "@/components/primitives/Button";
import { BrandMark } from "@/components/primitives/BrandMark";
import { Container } from "@/components/primitives/Container";
import styles from "./SiteHeader.module.css";

interface Props {
    onLogin: () => void;
    onSignup: () => void;
}

/**
 * Sticky top nav. Translucent at the top of the page, gains a hairline
 * and a stronger blur once the user scrolls past 16px — same trick the
 * design system spec calls for on the mobile header.
 */
export function SiteHeader({ onLogin, onSignup }: Props) {
    const [scrolled, setScrolled] = useState(false);

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
                    <a href="#features">Hizmetler</a>
                    <a href="#provider">Hizmet ver</a>
                    <a href="#guide">Rehber</a>
                    <a href="#help">Yardım</a>
                </nav>

                <div className={styles.actions}>
                    <Button variant="ghost" onClick={onLogin}>
                        Giriş yap
                    </Button>
                    <Button variant="primary" onClick={onSignup}>
                        Hesap oluştur
                    </Button>
                </div>
            </Container>
        </header>
    );
}
