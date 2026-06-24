"use client";

import { useState } from "react";
import { AuthModal, type AuthView } from "@/components/auth/AuthModal";
import { FeatureGrid } from "@/components/landing/FeatureGrid";
import { Hero } from "@/components/landing/Hero";
import { ProviderCTA } from "@/components/landing/ProviderCTA";
import { SiteFooter } from "@/components/landing/SiteFooter";
import { SiteHeader } from "@/components/landing/SiteHeader";
import { useDisclosure } from "@/hooks/useDisclosure";

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
