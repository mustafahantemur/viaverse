"use client";

import Link from "next/link";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import {
    Activity,
    Bell,
    Bookmark,
    BriefcaseBusiness,
    CalendarDays,
    Home,
    Inbox,
    Megaphone,
    Newspaper,
    Search,
    Settings,
    SlidersHorizontal,
    Store,
    User,
    WalletCards,
    Grid3X3,
} from "lucide-react";
import { BrandMark } from "@/components/primitives/BrandMark";
import { Button } from "@/components/primitives/Button";
import { mockAppApi, type SessionView } from "@/lib/mockAppClient";
import styles from "./ProductAppShell.module.css";

type AppSessionContextValue = {
    session: SessionView;
    reloadSession: () => Promise<void>;
    switchPersona: (personaId: string) => Promise<void>;
};

const AppSessionContext = createContext<AppSessionContextValue | null>(null);

export function useAppSession(): AppSessionContextValue {
    const value = useContext(AppSessionContext);
    if (!value) {
        throw new Error("useAppSession must be used inside ProductAppShell");
    }
    return value;
}

const navItems = [
    { href: "/app", label: "Ana akış", icon: Home },
    { href: "/app/services", label: "Hizmetler", icon: Search },
    { href: "/app/requests", label: "Taleplerim", icon: BriefcaseBusiness },
    { href: "/app/provider", label: "Hizmet veren", icon: Megaphone },
    { href: "/app/messages", label: "Mesajlar", icon: Inbox },
    { href: "/app/payments", label: "Ödemeler", icon: WalletCards },
    { href: "/app/activity", label: "Bildirimler", icon: Bell },
];

export function ProductAppShell({ children }: { children: ReactNode }) {
    const pathname = usePathname();
    const router = useRouter();
    const searchParams = useSearchParams();
    const [session, setSession] = useState<SessionView | null>(null);
    const [status, setStatus] = useState<"loading" | "ready" | "error">("loading");

    async function reloadSession() {
        const next = await mockAppApi.session();
        setSession(next);
    }

    async function switchPersona(personaId: string) {
        const next = await mockAppApi.switchPersona(personaId);
        setSession(next);
        router.refresh();
    }

    useEffect(() => {
        let cancelled = false;
        async function load() {
            try {
                const next = await mockAppApi.session();
                if (!cancelled) {
                    setSession(next);
                    setStatus("ready");
                }
            } catch {
                if (!cancelled) setStatus("error");
            }
        }
        load();
        return () => {
            cancelled = true;
        };
    }, []);

    const contextValue = useMemo(
        () => (session ? { session, reloadSession, switchPersona } : null),
        [session],
    );

    if (status === "loading") {
        return (
            <main className={styles.centerState}>
                <BrandMark size={54} />
                <p>Viaverse hazırlanıyor…</p>
            </main>
        );
    }

    if (status === "error" || !session || !contextValue) {
        return (
            <main className={styles.centerState}>
                <BrandMark size={54} />
                <p>Mock Web BFF yanıt vermiyor. `services:mock-web-bff` çalışıyor mu?</p>
                <Button onClick={() => window.location.reload()}>Tekrar dene</Button>
            </main>
        );
    }

    const currentUser = session.currentUser;
    const unread = currentUser.activeCapability === "STANDARD" ? 2 : 1;
    const sidebar = sidebarContext(pathname, currentUser.activeCapability);
    const activeTypeParam = searchParams.get("type")?.toUpperCase() ?? null;

    return (
        <AppSessionContext.Provider value={contextValue}>
            <div className={styles.shell}>
                <header className={styles.globalTopbar}>
                    <div className={styles.globalLeft}>
                        <Link href="/app" className={styles.brand} aria-label="Viaverse">
                            <BrandMark size={42} />
                        </Link>
                    </div>
                    <nav className={styles.topNav} aria-label="Ana ürün menüsü">
                        {navItems.map((item) => {
                            const Icon = item.icon;
                            const active = item.href === "/app" ? pathname === "/app" : pathname.startsWith(item.href);
                            return (
                                <Link
                                    key={item.href}
                                    href={item.href}
                                    className={[styles.topNavItem, active && styles.topNavItemActive].filter(Boolean).join(" ")}
                                    aria-label={item.label}
                                    title={item.label}
                                >
                                    <Icon size={22} aria-hidden />
                                </Link>
                            );
                        })}
                    </nav>
                    <div className={styles.globalActions}>
                        <button type="button" className={styles.iconButton} aria-label="Uygulamalar">
                            <Grid3X3 size={18} aria-hidden />
                        </button>
                        <Link href="/app/activity" className={styles.iconButton} aria-label="Bildirimler">
                            <Bell size={18} aria-hidden />
                            <span>{unread}</span>
                        </Link>
                        <label className={styles.personaPicker}>
                            <span>Mod</span>
                            <select
                                value={currentUser.id}
                                onChange={(event) => switchPersona(event.target.value)}
                            >
                                {session.personas.map((persona) => (
                                    <option key={persona.id} value={persona.id}>
                                        {persona.activeCapabilityLabel} · {persona.displayName}
                                    </option>
                                ))}
                            </select>
                        </label>
                        <Link href="/app/profile" className={styles.userPill}>
                            <span>{currentUser.initials}</span>
                        </Link>
                    </div>
                </header>
                <aside className={styles.sidebar} aria-label="Sayfa menüsü">
                    <label className={styles.sidebarSearch}>
                        <Search size={16} aria-hidden />
                        <input placeholder={sidebar.searchPlaceholder} />
                    </label>
                    <section className={styles.sidebarSection}>
                        <h2>{sidebar.title}</h2>
                        <nav className={styles.nav}>
                            {sidebar.items.map((item) => {
                                const Icon = item.icon;
                                const itemType = item.type ?? null;
                                const isActive = pathname === "/app"
                                    ? itemType === activeTypeParam
                                    : item.href === "/app" ? pathname === "/app" : pathname.startsWith(item.href);
                            return (
                                <Link
                                    key={`${sidebar.title}-${item.label}`}
                                    href={item.href}
                                    className={[styles.navItem, isActive && styles.navItemActive].filter(Boolean).join(" ")}
                                >
                                    <Icon size={18} aria-hidden />
                                    <span>{item.label}</span>
                                </Link>
                            );
                        })}
                        </nav>
                    </section>
                    {sidebar.featured.length > 0 && (
                        <section className={styles.sidebarSection}>
                            <h2>Öne çıkanlar</h2>
                            <div className={styles.sidebarFeatured}>
                                {sidebar.featured.map((item) => (
                                    <span key={item}>{item}</span>
                                ))}
                            </div>
                        </section>
                    )}
                    {sidebar.ads.length > 0 && (
                        <section className={styles.sidebarSection}>
                            <div className={styles.sidebarAds}>
                                {sidebar.ads.map((ad) => (
                                    <article key={ad.title} className={styles.sidebarAd}>
                                        <strong>{ad.title}</strong>
                                        <span>{ad.body}</span>
                                    </article>
                                ))}
                            </div>
                        </section>
                    )}
                    <div className={styles.sidebarBottom}>
                        <Link href="/app/profile" className={styles.profileShortcut}>
                            <span>{currentUser.initials}</span>
                            <strong>{currentUser.displayName}</strong>
                        </Link>
                        <Link href="/app/settings" className={styles.navItem}>
                            <Settings size={18} aria-hidden />
                            <span>Ayarlar</span>
                        </Link>
                    </div>
                </aside>

                <div className={styles.mainColumn}>
                    <main className={styles.content}>{children}</main>
                </div>

                <nav className={styles.mobileNav} aria-label="Viaverse mobil menü">
                    {navItems.slice(0, 6).map((item) => {
                        const Icon = item.icon;
                        const active = item.href === "/app" ? pathname === "/app" : pathname.startsWith(item.href);
                        return (
                            <Link
                                key={item.href}
                                href={item.href}
                                className={[styles.mobileNavItem, active && styles.mobileNavItemActive].filter(Boolean).join(" ")}
                            >
                                <Icon size={20} aria-hidden />
                                <span>{item.label}</span>
                            </Link>
                        );
                    })}
                </nav>
            </div>
        </AppSessionContext.Provider>
    );
}

type SidebarItem = {
    label: string;
    href: string;
    icon: typeof Home;
    type?: string | null;
};

type SidebarAd = {
    title: string;
    body: string;
};

function sidebarContext(pathname: string, capability: string): {
    title: string;
    searchPlaceholder: string;
    items: SidebarItem[];
    featured: string[];
    ads: SidebarAd[];
} {
    if (pathname.startsWith("/app/services")) {
        return {
            title: "Hizmet keşfi",
            searchPlaceholder: "Hizmet, kategori veya profil ara",
            items: [
                { label: "Tüm hizmetler", href: "/app/services", icon: Search },
                { label: "Bireysel hizmet veren", href: "/app/services?type=individual", icon: User },
                { label: "İşletme", href: "/app/services?type=business", icon: Store },
                { label: "Kayıtlı aramalar", href: "/app/services?saved=1", icon: Bookmark },
            ],
            featured: ["Temizlik 4.7+", "Aynı gün dönüş", "5 km yakınlık filtresi"],
            ads: [
                { title: "Ev düzeni kampanyası", body: "Yakındaki temizlik ve düzen hizmetlerinde sponsor alanı." },
            ],
        };
    }
    if (pathname.startsWith("/app/provider")) {
        return {
            title: capability === "BUSINESS" ? "İşletme görünümü" : "Hizmet veren",
            searchPlaceholder: "Talep veya fırsat ara",
            items: [
                { label: "Yakındaki talepler", href: "/app/provider", icon: BriefcaseBusiness },
                { label: "Tekliflerim", href: "/app/provider#offers", icon: Megaphone },
                { label: "Profil durumu", href: "/app/profile", icon: User },
            ],
            featured: ["Yanıt bekleyen 2 talep", "Kabul edilen teklif mesajlara düşer"],
            ads: [],
        };
    }
    if (pathname.startsWith("/app/requests")) {
        return {
            title: "Taleplerim",
            searchPlaceholder: "Talep başlığı veya kategori ara",
            items: [
                { label: "Açık talepler", href: "/app/requests", icon: BriefcaseBusiness },
                { label: "Teklif bekleyenler", href: "/app/requests#offers", icon: Activity },
                { label: "Konum ve bütçe", href: "/app/requests#filters", icon: SlidersHorizontal },
            ],
            featured: ["Talep buradan oluşturulur", "Hizmet verenler fırsat olarak görür"],
            ads: [],
        };
    }
    return {
        title: "Ana akış",
        searchPlaceholder: "Paylaşım, etiket veya konum ara",
        items: [
            { label: "Tüm paylaşımlar", href: "/app", icon: Home, type: null },
            { label: "Paylaşımlar", href: "/app?type=POST", icon: Newspaper, type: "POST" },
            { label: "Duyurular", href: "/app?type=ANNOUNCEMENT", icon: Megaphone, type: "ANNOUNCEMENT" },
            { label: "Etkinlikler", href: "/app?type=EVENT", icon: CalendarDays, type: "EVENT" },
        ],
        featured: ["#kadikoy", "#trafik", "#elektrikkesintisi", "#etkinlik"],
        ads: [],
    };
}
