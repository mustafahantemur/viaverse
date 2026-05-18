"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { AppHeader } from "@/components/app/AppHeader";
import { EmptyFeed } from "@/components/app/EmptyFeed";
import { QuickActions } from "@/components/app/QuickActions";
import { WelcomeHero } from "@/components/app/WelcomeHero";
import { Container } from "@/components/primitives/Container";
import {
    currentProfile,
    getAccessToken,
    me,
    refresh,
    setAccessToken,
    type CurrentProfileView,
    type MeView,
} from "@/lib/authClient";

/**
 * Post-auth landing. First render attempts to recover the session: if
 * the access token is still in memory we go straight to `me()`; if it
 * was lost (hard refresh) we ask the BFF to mint a new one from the
 * HttpOnly refresh cookie before calling `me()`. On any auth failure
 * we send the user back to the landing page.
 *
 * This is the *welcome shell*. Profile and settings screens land in
 * the next slice; the quick-action cards point at placeholders so
 * the surface feels real today.
 */
export default function AppHomePage() {
    const router = useRouter();
    const [view, setView] = useState<MeView | null>(null);
    const [profileView, setProfileView] = useState<CurrentProfileView | null>(null);
    const [status, setStatus] = useState<"loading" | "ready" | "error">("loading");

    useEffect(() => {
        let cancelled = false;
        async function bootstrap() {
            try {
                if (!getAccessToken()) {
                    await refresh();
                }
                const [fetched, fetchedProfile] = await Promise.all([me(), currentProfile()]);
                if (!cancelled) {
                    setView(fetched);
                    setProfileView(fetchedProfile);
                    setStatus("ready");
                }
            } catch {
                if (!cancelled) {
                    setStatus("error");
                    setAccessToken(null);
                    router.replace("/");
                }
            }
        }
        bootstrap();
        return () => {
            cancelled = true;
        };
    }, [router]);

    if (status !== "ready") {
        return (
            <main style={{ minHeight: "100dvh", display: "grid", placeItems: "center" }}>
                <div
                    style={{
                        color: "var(--vv-fg-muted)",
                        fontSize: 14,
                    }}
                >
                    …
                </div>
            </main>
        );
    }

    const isFreshSignup = view ? !view.profileCompleted : true;

    return (
        <>
            <AppHeader me={view} onLogout={() => setView(null)} />
            <main style={{ paddingBottom: 48 }}>
                <Container>
                    <WelcomeHero
                        me={view}
                        activeMode={profileView?.activeMode}
                        isFreshSignup={isFreshSignup}
                    />
                    <QuickActions
                        // Wired-up handlers land per-feature; for now the
                        // primary CTA at least gives haptic feedback by
                        // scrolling to the empty feed. Provider/settings
                        // are visible but inert until their flows ship.
                        onPostRequest={() => {
                            document.getElementById("empty-feed")?.scrollIntoView({
                                behavior: "smooth",
                            });
                        }}
                        onBecomeProvider={() => router.push("/app/profile#provider")}
                        onOpenSettings={() => router.push("/app/profile#settings")}
                    />
                    <div id="empty-feed">
                        <EmptyFeed />
                    </div>
                </Container>
            </main>
        </>
    );
}
