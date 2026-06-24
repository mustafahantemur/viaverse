"use client";

import { useEffect, useState } from "react";
import { ShieldCheck, UserRound } from "lucide-react";
import { Button } from "@/components/primitives/Button";
import { SelectField, TextField } from "@/components/product/ProductControls";
import styles from "@/components/product/ProductPages.module.css";
import { useAppSession } from "@/components/product/ProductAppShell";
import {
    mockAppApi,
    type CapabilityView,
    type ProfileView,
} from "@/lib/mockAppClient";

export default function ProfilePage() {
    const { reloadSession } = useAppSession();
    const [profile, setProfile] = useState<ProfileView | null>(null);
    const [displayName, setDisplayName] = useState("");
    const [headline, setHeadline] = useState("");
    const [bio, setBio] = useState("");
    const [locationLabel, setLocationLabel] = useState("");
    const [activeCapability, setActiveCapability] = useState<CapabilityView["key"]>("STANDARD");
    const [message, setMessage] = useState<string | null>(null);
    const [busy, setBusy] = useState(false);

    async function load() {
        const next = await mockAppApi.profile();
        setProfile(next);
        setDisplayName(next.displayName);
        setHeadline(next.headline);
        setBio(next.bio);
        setLocationLabel(next.locationLabel);
        setActiveCapability(next.activeCapability);
    }

    useEffect(() => {
        load();
    }, []);

    async function save() {
        setBusy(true);
        setMessage(null);
        try {
            const next = await mockAppApi.patchProfile({
                displayName,
                headline,
                bio,
                locationLabel,
                activeCapability,
            });
            setProfile(next);
            await reloadSession();
            setMessage("Profil güncellendi.");
        } finally {
            setBusy(false);
        }
    }

    if (!profile) {
        return <div className={styles.empty}>Profil yükleniyor…</div>;
    }

    const enabledCapabilities = profile.capabilities.filter((capability) => capability.enabled);

    return (
        <>
            <section className={styles.intro}>
                <div>
                    <p className={styles.eyebrow}>Profil</p>
                    <h2>Tek hesap, farklı yetkinlikler.</h2>
                    <p>
                        Hizmet alan, Bireysel hizmet veren ve İşletme yetkinlikleri aynı profil sınırında
                        gösterilir; bu ekran gelecekte profile/account boşluklarını netleştirir.
                    </p>
                </div>
            </section>

            {message && <p className={styles.statusBadge} style={{ marginBottom: 14 }}>{message}</p>}

            <section className={styles.layout2}>
                <article className={styles.card}>
                    <div className={styles.cardHeader}>
                        <h2>Temel bilgiler</h2>
                        <UserRound size={20} aria-hidden />
                    </div>
                    <div className={styles.form}>
                        <TextField label="Görünen ad" value={displayName} onChange={setDisplayName} />
                        <TextField label="Başlık" value={headline} onChange={setHeadline} />
                        <TextField label="Hakkında" value={bio} onChange={setBio} textarea />
                        <TextField label="Konum / kapsam" value={locationLabel} onChange={setLocationLabel} />
                        <SelectField
                            label="Aktif yetkinlik"
                            value={activeCapability}
                            onChange={setActiveCapability}
                            options={enabledCapabilities.map((capability) => ({
                                value: capability.key,
                                label: capability.label,
                            }))}
                        />
                    </div>
                    <div className={styles.actions}>
                        <Button disabled={busy || !displayName.trim()} onClick={save}>Kaydet</Button>
                    </div>
                </article>

                <div className={styles.stack}>
                    <article className={styles.card}>
                        <div className={styles.cardHeader}>
                            <h2>Yetkinlikler</h2>
                            <ShieldCheck size={20} aria-hidden />
                        </div>
                        <div className={styles.stack}>
                            {profile.capabilities.map((capability) => (
                                <div key={capability.key} className={styles.feedCard}>
                                    <div className={styles.cardHeader}>
                                        <div>
                                            <h3>{capability.label}</h3>
                                            <p className={styles.muted}>{capability.summary}</p>
                                        </div>
                                        <span className={capability.enabled ? styles.statusBadge : styles.softBadge}>
                                            {capability.status}
                                        </span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </article>

                    <article className={styles.card}>
                        <div className={styles.cardHeader}>
                            <h2>Hizmet durumu</h2>
                        </div>
                        {profile.individualProviderProfile ? (
                            <div className={styles.stack}>
                                <span className={styles.statusBadge}>{profile.individualProviderProfile.providerType}</span>
                                <p className={styles.muted}>{profile.individualProviderProfile.serviceBlurb}</p>
                                <div className={styles.badgeRow}>
                                    <span className={styles.softBadge}>{profile.individualProviderProfile.availabilitySummary}</span>
                                    <span className={styles.softBadge}>{profile.individualProviderProfile.locationScope}</span>
                                </div>
                            </div>
                        ) : (
                            <p className={styles.muted}>Bireysel hizmet veren profili henüz açık değil.</p>
                        )}
                    </article>

                    <article className={styles.card}>
                        <div className={styles.cardHeader}>
                            <h2>İşletme</h2>
                        </div>
                        {profile.businessProfile ? (
                            <div className={styles.stack}>
                                <span className={styles.statusBadge}>{profile.businessProfile.providerType}</span>
                                <h3>{profile.businessProfile.tradeName}</h3>
                                <p className={styles.muted}>{profile.businessProfile.legalName}</p>
                                <div className={styles.badgeRow}>
                                    <span className={styles.softBadge}>{profile.businessProfile.sector}</span>
                                    <span className={styles.softBadge}>{profile.businessProfile.verificationStatus}</span>
                                </div>
                            </div>
                        ) : (
                            <p className={styles.muted}>İşletme profili bu personada başlatılmamış.</p>
                        )}
                    </article>
                </div>
            </section>
        </>
    );
}
