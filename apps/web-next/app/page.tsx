"use client";

import { useState } from "react";
import { AuthModal, type AuthView } from "@/components/auth/AuthModal";
import { FeatureGrid } from "@/components/landing/FeatureGrid";
import { Hero } from "@/components/landing/Hero";
import { ProviderCTA } from "@/components/landing/ProviderCTA";
import { SiteFooter } from "@/components/landing/SiteFooter";
import { SiteHeader } from "@/components/landing/SiteHeader";
import { useDisclosure } from "@/hooks/useDisclosure";

/**
 * Marketing landing page. Composed from independent section components so
 * design tweaks land in a single file each. Auth is mounted at the page
 * level so any section button (header, hero, provider CTA) can trigger
 * the same modal with the right initial view.
 */
export default function LandingPage() {
    const auth = useDisclosure();
    const [authView, setAuthView] = useState<AuthView>("login");

    function openAuth(view: AuthView) {
        setAuthView(view);
        auth.open();
    }

    return (
        <>
            <SiteHeader onLogin={() => openAuth("login")} onSignup={() => openAuth("register")} />
            <main>
                <Hero
                    onSignup={() => openAuth("register")}
                    onProviderSignup={() => openAuth("register")}
                />
                <FeatureGrid />
                <ProviderCTA onSignup={() => openAuth("register")} />
            </main>
            <SiteFooter />

            <AuthModal isOpen={auth.isOpen} onClose={auth.close} initialView={authView} />
        </>
    );
}
