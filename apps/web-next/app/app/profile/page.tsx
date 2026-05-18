"use client";

import { useRouter } from "next/navigation";
import { useEffect, useMemo, useState } from "react";
import { AppHeader } from "@/components/app/AppHeader";
import { Button } from "@/components/primitives/Button";
import { Container } from "@/components/primitives/Container";
import {
    currentProfile,
    enableIndividualProvider,
    getAccessToken,
    getCapabilityTerms,
    me,
    refresh,
    setAccessToken,
    startBusinessOnboarding,
    submitBusinessOnboarding,
    updateActiveMode,
    updateBusinessDraft,
    updateProfile,
    type ActiveMode,
    type BusinessSector,
    type CapabilityTerms,
    type CurrentProfileView,
    type MeView,
    type UpdateBusinessDraftPayload,
} from "@/lib/authClient";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./ProfilePage.module.css";

const SECTORS: BusinessSector[] = ["PHARMACY", "CLINIC", "AGENCY", "SHOP", "SOFTWARE", "OTHER"];

export default function ProfilePage() {
    const router = useRouter();
    const { t } = useTranslation();
    const [meView, setMeView] = useState<MeView | null>(null);
    const [profile, setProfile] = useState<CurrentProfileView | null>(null);
    const [terms, setTerms] = useState<CapabilityTerms | null>(null);
    const [status, setStatus] = useState<"loading" | "ready" | "error">("loading");
    const [message, setMessage] = useState<string | null>(null);
    const [busy, setBusy] = useState(false);

    useEffect(() => {
        let cancelled = false;
        async function bootstrap() {
            try {
                if (!getAccessToken()) {
                    await refresh();
                }
                const [fetchedMe, fetchedProfile, fetchedTerms] = await Promise.all([
                    me(),
                    currentProfile(),
                    getCapabilityTerms(),
                ]);
                if (!cancelled) {
                    setMeView(fetchedMe);
                    setProfile(fetchedProfile);
                    setTerms(fetchedTerms);
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

    if (status !== "ready" || !profile) {
        return (
            <main className={styles.loading}>
                <span>{t.common.loading}</span>
            </main>
        );
    }

    const providerTerms = terms?.capabilityTerms.find((document) => document.type === "PROVIDER_TERMS");
    const businessTerms = terms?.capabilityTerms.find((document) => document.type === "BUSINESS_TERMS");

    async function run(action: () => Promise<void>) {
        setBusy(true);
        setMessage(null);
        try {
            await action();
            setMessage(t.profile.saved);
        } finally {
            setBusy(false);
        }
    }

    return (
        <>
            <AppHeader me={meView} onLogout={() => setMeView(null)} />
            <main className={styles.page}>
                <Container>
                    <header className={styles.hero}>
                        <p className={styles.eyebrow}>{t.profile.activeMode}</p>
                        <h1>{t.profile.title}</h1>
                        <p>{t.profile.subtitle}</p>
                    </header>

                    {message && <p className={styles.message}>{message}</p>}

                    <section className={styles.grid}>
                        <ProfileBasicsCard
                            profile={profile}
                            busy={busy}
                            onSave={(next) =>
                                run(async () => {
                                    setProfile(await updateProfile(next));
                                })
                            }
                        />
                        <TrustCard profile={profile} />
                        <ModesCard
                            profile={profile}
                            busy={busy}
                            onSwitch={(mode) =>
                                run(async () => {
                                    setProfile(await updateActiveMode(mode));
                                })
                            }
                        />
                        <ProviderCard
                            profile={profile}
                            terms={providerTerms}
                            busy={busy}
                            onEnable={(version, serviceBlurb) =>
                                run(async () => {
                                    setProfile(await enableIndividualProvider(version, serviceBlurb));
                                })
                            }
                        />
                        <BusinessCard
                            profile={profile}
                            terms={businessTerms}
                            busy={busy}
                            onStart={() =>
                                run(async () => {
                                    await startBusinessOnboarding();
                                    setProfile(await currentProfile());
                                })
                            }
                            onSaveDraft={(draft) =>
                                run(async () => {
                                    await updateBusinessDraft(draft);
                                    setProfile(await currentProfile());
                                })
                            }
                            onSubmit={(version) =>
                                run(async () => {
                                    await submitBusinessOnboarding(version);
                                    setProfile(await currentProfile());
                                })
                            }
                        />
                    </section>
                </Container>
            </main>
        </>
    );
}

function TrustCard({ profile }: { profile: CurrentProfileView }) {
    const { t } = useTranslation();

    return (
        <article className={styles.card}>
            <div className={styles.cardHeader}>
                <h2>{t.profile.trust}</h2>
                <span>{t.profile.trustLevels[profile.trust.badge]}</span>
            </div>
            <p className={styles.muted}>{t.profile.trustHint}</p>
            <strong>
                {t.profile.trustScore}: {profile.trust.score}
            </strong>
        </article>
    );
}

function ProfileBasicsCard({
    profile,
    busy,
    onSave,
}: {
    profile: CurrentProfileView;
    busy: boolean;
    onSave: (payload: {
        displayName: string;
        firstName: string;
        lastName: string;
        headline: string;
        bio: string;
        publicVisibility: CurrentProfileView["publicVisibility"];
    }) => Promise<void>;
}) {
    const { t } = useTranslation();
    const [displayName, setDisplayName] = useState(profile.displayName ?? "");
    const [firstName, setFirstName] = useState(profile.firstName ?? "");
    const [lastName, setLastName] = useState(profile.lastName ?? "");
    const [headline, setHeadline] = useState(profile.headline ?? "");
    const [bio, setBio] = useState(profile.bio ?? "");
    const [publicVisibility, setPublicVisibility] = useState(profile.publicVisibility);

    return (
        <article id="settings" className={styles.card}>
            <div className={styles.cardHeader}>
                <h2>{t.profile.basics}</h2>
                <span>{t.profile.completeness}: {profile.completenessScore}%</span>
            </div>
            <div className={styles.formGrid}>
                <Field label={t.profile.displayName} value={displayName} onChange={setDisplayName} />
                <Field label={t.profile.firstName} value={firstName} onChange={setFirstName} />
                <Field label={t.profile.lastName} value={lastName} onChange={setLastName} />
                <Field label={t.profile.headline} value={headline} onChange={setHeadline} />
                <Field label={t.profile.bio} value={bio} onChange={setBio} textarea />
                <label className={styles.field}>
                    <span>{t.profile.visibility}</span>
                    <select
                        value={publicVisibility}
                        onChange={(event) =>
                            setPublicVisibility(event.target.value as CurrentProfileView["publicVisibility"])
                        }
                    >
                        {(["PUBLIC", "LIMITED", "PRIVATE"] as const).map((visibility) => (
                            <option key={visibility} value={visibility}>
                                {t.profile.publicVisibility[visibility]}
                            </option>
                        ))}
                    </select>
                </label>
            </div>
            <Button
                disabled={busy}
                onClick={() =>
                    onSave({ displayName, firstName, lastName, headline, bio, publicVisibility })
                }
            >
                {t.common.save}
            </Button>
        </article>
    );
}

function ModesCard({
    profile,
    busy,
    onSwitch,
}: {
    profile: CurrentProfileView;
    busy: boolean;
    onSwitch: (mode: ActiveMode) => Promise<void>;
}) {
    const { t } = useTranslation();
    const enabledModes = profile.capabilities
        .filter((capability) => capability.status === "ENABLED")
        .map((capability) => capability.capability);

    return (
        <article className={styles.card}>
            <div className={styles.cardHeader}>
                <h2>{t.profile.activeMode}</h2>
            </div>
            <p className={styles.muted}>{t.profile.switchHint}</p>
            <div className={styles.modeRow}>
                {enabledModes.map((mode) => (
                    <button
                        key={mode}
                        type="button"
                        className={[
                            styles.modeChip,
                            profile.activeMode === mode && styles.modeChipActive,
                        ]
                            .filter(Boolean)
                            .join(" ")}
                        disabled={busy || profile.activeMode === mode}
                        onClick={() => onSwitch(mode)}
                    >
                        {t.home.modeLabels[mode]}
                    </button>
                ))}
            </div>
        </article>
    );
}

function ProviderCard({
    profile,
    terms,
    busy,
    onEnable,
}: {
    profile: CurrentProfileView;
    terms?: CapabilityTerms["capabilityTerms"][number];
    busy: boolean;
    onEnable: (version: string, serviceBlurb: string) => Promise<void>;
}) {
    const { t } = useTranslation();
    const provider = profile.capabilities.find((capability) => capability.capability === "INDIVIDUAL_PROVIDER");
    const [serviceBlurb, setServiceBlurb] = useState(profile.individualProviderProfile?.serviceBlurb ?? "");
    const [accepted, setAccepted] = useState(false);

    return (
        <article id="provider" className={styles.card}>
            <div className={styles.cardHeader}>
                <h2>{t.profile.provider}</h2>
                {provider && <span>{provider.status}</span>}
            </div>
            {provider?.status === "ENABLED" ? (
                <p className={styles.muted}>{t.profile.providerEnabled}</p>
            ) : (
                <>
                    <Field
                        label={t.profile.serviceBlurb}
                        value={serviceBlurb}
                        onChange={setServiceBlurb}
                    />
                    <TermsCheckbox
                        label={t.profile.acceptProviderTerms}
                        checked={accepted}
                        onChange={setAccepted}
                        href={terms?.url}
                        version={terms?.version}
                    />
                    <Button
                        disabled={busy || !accepted || !terms}
                        onClick={() => terms && onEnable(terms.version, serviceBlurb)}
                    >
                        {t.profile.enableProvider}
                    </Button>
                </>
            )}
        </article>
    );
}

function BusinessCard({
    profile,
    terms,
    busy,
    onStart,
    onSaveDraft,
    onSubmit,
}: {
    profile: CurrentProfileView;
    terms?: CapabilityTerms["capabilityTerms"][number];
    busy: boolean;
    onStart: () => Promise<void>;
    onSaveDraft: (payload: UpdateBusinessDraftPayload) => Promise<void>;
    onSubmit: (version: string) => Promise<void>;
}) {
    const { t } = useTranslation();
    const business = profile.businessProfile;
    const [accepted, setAccepted] = useState(false);
    const [draft, setDraft] = useState<UpdateBusinessDraftPayload>(() => ({
        legalName: business?.legalName ?? "",
        tradeName: business?.tradeName ?? "",
        sector: business?.sector ?? "OTHER",
        taxId: business?.taxId ?? "",
        addressLine: business?.addressLine ?? "",
        district: business?.district ?? "",
        city: business?.city ?? "",
        country: business?.country ?? "TR",
        phone: business?.phone ?? "",
        emailPublic: business?.emailPublic ?? "",
        openingHoursJson: business?.openingHoursJson ?? "",
    }));
    const canEdit = !business || business.verificationStatus === "DRAFT" || business.verificationStatus === "REJECTED";
    const fields = useMemo(
        () =>
            [
                ["legalName", t.profile.legalName],
                ["tradeName", t.profile.tradeName],
                ["taxId", t.profile.taxId],
                ["addressLine", t.profile.addressLine],
                ["district", t.profile.district],
                ["city", t.profile.city],
                ["country", t.profile.country],
                ["phone", t.profile.phone],
                ["emailPublic", t.profile.emailPublic],
                ["openingHoursJson", t.profile.openingHours],
            ] as const,
        [t],
    );

    return (
        <article className={styles.card}>
            <div className={styles.cardHeader}>
                <h2>{t.profile.business}</h2>
                {business && <span>{t.profile.status}: {business.verificationStatus}</span>}
            </div>
            {!business ? (
                <Button disabled={busy} onClick={onStart}>
                    {t.profile.startBusiness}
                </Button>
            ) : (
                <>
                    <div className={styles.formGrid}>
                        {fields.map(([key, label]) => (
                            <Field
                                key={key}
                                label={label}
                                value={(draft[key] as string | undefined) ?? ""}
                                onChange={(value) => setDraft((current) => ({ ...current, [key]: value }))}
                                textarea={key === "openingHoursJson"}
                                disabled={!canEdit}
                            />
                        ))}
                        <label className={styles.field}>
                            <span>{t.profile.sector}</span>
                            <select
                                disabled={!canEdit}
                                value={draft.sector}
                                onChange={(event) =>
                                    setDraft((current) => ({
                                        ...current,
                                        sector: event.target.value as BusinessSector,
                                    }))
                                }
                            >
                                {SECTORS.map((sector) => (
                                    <option key={sector} value={sector}>
                                        {sector}
                                    </option>
                                ))}
                            </select>
                        </label>
                    </div>
                    {canEdit && (
                        <>
                            <div className={styles.actions}>
                                <Button variant="outline" disabled={busy} onClick={() => onSaveDraft(draft)}>
                                    {t.profile.saveDraft}
                                </Button>
                            </div>
                            <TermsCheckbox
                                label={t.profile.acceptBusinessTerms}
                                checked={accepted}
                                onChange={setAccepted}
                                href={terms?.url}
                                version={terms?.version}
                            />
                            <Button
                                disabled={busy || !accepted || !terms}
                                onClick={() => terms && onSubmit(terms.version)}
                            >
                                {t.profile.submitBusiness}
                            </Button>
                        </>
                    )}
                </>
            )}
        </article>
    );
}

function Field({
    label,
    value,
    onChange,
    textarea = false,
    disabled = false,
}: {
    label: string;
    value: string;
    onChange: (value: string) => void;
    textarea?: boolean;
    disabled?: boolean;
}) {
    return (
        <label className={styles.field}>
            <span>{label}</span>
            {textarea ? (
                <textarea
                    value={value}
                    disabled={disabled}
                    onChange={(event) => onChange(event.target.value)}
                />
            ) : (
                <input
                    value={value}
                    disabled={disabled}
                    onChange={(event) => onChange(event.target.value)}
                />
            )}
        </label>
    );
}

function TermsCheckbox({
    label,
    checked,
    onChange,
    href,
    version,
}: {
    label: string;
    checked: boolean;
    onChange: (value: boolean) => void;
    href?: string;
    version?: string;
}) {
    return (
        <label className={styles.terms}>
            <input
                type="checkbox"
                checked={checked}
                onChange={(event) => onChange(event.target.checked)}
            />
            <span>
                {label}{" "}
                {href && (
                    <a href={href} target="_blank" rel="noreferrer">
                        ({version})
                    </a>
                )}
            </span>
        </label>
    );
}
