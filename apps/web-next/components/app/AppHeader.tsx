"use client";

import { useRouter } from "next/navigation";
import { useCallback, useEffect, useRef, useState } from "react";
import { BrandMark } from "@/components/primitives/BrandMark";
import { Container } from "@/components/primitives/Container";
import { HeaderToggles } from "@/components/landing/HeaderToggles";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import { logout, type MeView } from "@/lib/authClient";
import styles from "./AppHeader.module.css";

interface Props {
    me: MeView | null;
    onLogout: () => void;
}

/**
 * Signed-in top bar. Same brand + theme/lang toggles as the landing
 * header, but the right-side auth CTAs are replaced by a user pill
 * that opens a small menu (profile entry-point, language, theme,
 * log out). Profile and settings still route to the same `/app`
 * pages today — those screens land in the next slice; the entries
 * exist so the navigation doesn't feel like a dead-end.
 */
export function AppHeader({ me, onLogout }: Props) {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const menuRef = useRef<HTMLDivElement | null>(null);
    const router = useRouter();

    useEffect(() => {
        if (!open) return;
        const onDocClick = (event: MouseEvent) => {
            if (!menuRef.current?.contains(event.target as Node)) setOpen(false);
        };
        document.addEventListener("mousedown", onDocClick);
        return () => document.removeEventListener("mousedown", onDocClick);
    }, [open]);

    const handleLogout = useCallback(async () => {
        try {
            await logout();
        } finally {
            onLogout();
            router.replace("/");
        }
    }, [onLogout, router]);

    const initials = computeInitials(me);
    const displayName = me?.displayName ?? "…";

    return (
        <header className={styles.header}>
            <Container className={styles.row}>
                <a href="/app" className={styles.brand} aria-label="Viaverse home">
                    <BrandMark size={40} />
                </a>
                <div className={styles.actions}>
                    <HeaderToggles />
                    <div className={styles.userWrap} ref={menuRef}>
                        <button
                            type="button"
                            className={styles.userPill}
                            aria-haspopup="menu"
                            aria-expanded={open}
                            onClick={() => setOpen((v) => !v)}
                        >
                            <span className={styles.avatar}>{initials}</span>
                            <span className={styles.userName}>{displayName}</span>
                        </button>
                        {open && (
                            <div role="menu" className={styles.menu}>
                                <a role="menuitem" className={styles.menuItem} href="/app/profile">
                                    {t.home.profileMenu.profile}
                                </a>
                                <a role="menuitem" className={styles.menuItem} href="/app/profile#settings">
                                    {t.home.profileMenu.settings}
                                </a>
                                <button
                                    type="button"
                                    role="menuitem"
                                    className={[styles.menuItem, styles.menuItemDanger].join(" ")}
                                    onClick={handleLogout}
                                >
                                    {t.home.profileMenu.logout}
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </Container>
        </header>
    );
}

function computeInitials(me: MeView | null): string {
    if (!me) return "?";
    const first = me.firstName?.trim()?.[0];
    const last = me.lastName?.trim()?.[0];
    if (first && last) return (first + last).toUpperCase();
    if (first) return first.toUpperCase();
    return me.displayName?.trim()?.[0]?.toUpperCase() ?? "?";
}
