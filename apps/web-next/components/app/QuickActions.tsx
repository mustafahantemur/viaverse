"use client";

import type { ReactNode } from "react";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./QuickActions.module.css";

interface ActionCardProps {
    title: string;
    description: string;
    icon: ReactNode;
    accent?: "primary" | "trust" | "neutral";
    onClick?: () => void;
    disabled?: boolean;
}

function ActionCard({ title, description, icon, accent = "neutral", onClick, disabled }: ActionCardProps) {
    return (
        <button
            type="button"
            className={[
                styles.card,
                accent === "primary" && styles.cardPrimary,
                accent === "trust" && styles.cardTrust,
                disabled && styles.cardDisabled,
            ]
                .filter(Boolean)
                .join(" ")}
            onClick={onClick}
            disabled={disabled}
        >
            <span className={styles.icon} aria-hidden>
                {icon}
            </span>
            <span className={styles.title}>{title}</span>
            <span className={styles.description}>{description}</span>
        </button>
    );
}

/**
 * Four-card "what now?" grid that the new signed-in user lands on.
 * Each card is a starting point — Phase 2 will wire them to real
 * destinations (post-request flow, jobs feed, provider onboarding,
 * profile settings). Until then the click handlers are placeholders
 * so the layout is real and the wiring is one trivial commit.
 */
export function QuickActions({
    onPostRequest,
    onBrowseJobs,
    onBecomeProvider,
    onOpenSettings,
}: {
    onPostRequest?: () => void;
    onBrowseJobs?: () => void;
    onBecomeProvider?: () => void;
    onOpenSettings?: () => void;
}) {
    const { t } = useTranslation();
    const a = t.home.actions;
    return (
        <section className={styles.grid}>
            <ActionCard
                title={a.postRequest.title}
                description={a.postRequest.description}
                accent="primary"
                icon={<PlusIcon />}
                onClick={onPostRequest}
                disabled={!onPostRequest}
            />
            <ActionCard
                title={a.browseJobs.title}
                description={a.browseJobs.description}
                icon={<CompassIcon />}
                onClick={onBrowseJobs}
                disabled={!onBrowseJobs}
            />
            <ActionCard
                title={a.becomeProvider.title}
                description={a.becomeProvider.description}
                accent="trust"
                icon={<BoltIcon />}
                onClick={onBecomeProvider}
                disabled={!onBecomeProvider}
            />
            <ActionCard
                title={a.settings.title}
                description={a.settings.description}
                icon={<GearIcon />}
                onClick={onOpenSettings}
                disabled={!onOpenSettings}
            />
        </section>
    );
}

/* ---------- inline icons ---------- */

function PlusIcon() {
    return (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
            <path d="M12 5v14M5 12h14" />
        </svg>
    );
}

function CompassIcon() {
    return (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="9" />
            <path d="m15 9-2 6-6 2 2-6 6-2z" strokeLinejoin="round" />
        </svg>
    );
}

function BoltIcon() {
    return (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinejoin="round">
            <path d="M13 2 4 14h7l-1 8 9-12h-7l1-8z" />
        </svg>
    );
}

function GearIcon() {
    return (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinejoin="round">
            <circle cx="12" cy="12" r="3" />
            <path d="M19.4 15a1.7 1.7 0 0 0 .3 1.8l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.8-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1.1-1.5 1.7 1.7 0 0 0-1.8.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.8 1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.5-1.1 1.7 1.7 0 0 0-.3-1.8l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.8.3H9a1.7 1.7 0 0 0 1-1.5V3a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.8-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.8V9a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z" />
        </svg>
    );
}
