"use client";

import { useEffect, useState } from "react";
import { Bell, Lock, SlidersHorizontal } from "lucide-react";
import { Button } from "@/components/primitives/Button";
import { SelectField } from "@/components/product/ProductControls";
import styles from "@/components/product/ProductPages.module.css";
import { mockAppApi, type SettingsView } from "@/lib/mockAppClient";

export default function SettingsPage() {
    const [settings, setSettings] = useState<SettingsView | null>(null);
    const [message, setMessage] = useState<string | null>(null);

    useEffect(() => {
        mockAppApi.settings().then(setSettings);
    }, []);

    async function patch(payload: Partial<SettingsView>) {
        const next = await mockAppApi.patchSettings(payload);
        setSettings(next);
        setMessage("Ayarlar güncellendi.");
    }

    if (!settings) {
        return <div className={styles.empty}>Ayarlar yükleniyor…</div>;
    }

    return (
        <>
            <section className={styles.intro}>
                <div>
                    <p className={styles.eyebrow}>Ayarlar</p>
                    <h2>Bildirim, gizlilik ve deneyim tercihleri.</h2>
                    <p>
                        Bu mock yüzey ayarların profil servisinden mi, bildirim servisinden mi, yoksa Web BFF
                        kompozisyonundan mı geleceğini tartışmaya açar.
                    </p>
                </div>
            </section>

            {message && <p className={styles.statusBadge} style={{ marginBottom: 14 }}>{message}</p>}

            <section className={styles.grid3}>
                <SettingsCard
                    icon={<Bell size={20} />}
                    title="Bildirimler"
                    rows={[
                        {
                            label: "Push bildirimleri",
                            checked: settings.pushNotifications,
                            onChange: (value) => patch({ pushNotifications: value }),
                        },
                        {
                            label: "E-posta özeti",
                            checked: settings.emailDigest,
                            onChange: (value) => patch({ emailDigest: value }),
                        },
                        {
                            label: "Talep güncellemeleri",
                            checked: settings.requestUpdates,
                            onChange: (value) => patch({ requestUpdates: value }),
                        },
                    ]}
                />

                <article className={styles.card}>
                    <div className={styles.cardHeader}>
                        <h2>Gizlilik</h2>
                        <Lock size={20} aria-hidden />
                    </div>
                    <SelectField
                        label="Görünürlük"
                        value={settings.privacyLevel}
                        onChange={(privacyLevel) => patch({ privacyLevel })}
                        options={[
                            { value: "Yakınımda görünür", label: "Yakınımda görünür" },
                            { value: "Sadece etkileşimde olduğum kişiler", label: "Sadece etkileşimde olduğum kişiler" },
                            { value: "Sınırlı profil", label: "Sınırlı profil" },
                        ]}
                    />
                    <p className={styles.muted}>Gizlilik kapsamı bugün mock; ileride profil ve mesajlaşma kurallarıyla birleşmeli.</p>
                </article>

                <article className={styles.card}>
                    <div className={styles.cardHeader}>
                        <h2>Deneyim</h2>
                        <SlidersHorizontal size={20} aria-hidden />
                    </div>
                    <div className={styles.form}>
                        <SelectField
                            label="Dil"
                            value={settings.language}
                            onChange={(language) => patch({ language })}
                            options={[
                                { value: "tr", label: "Türkçe" },
                                { value: "en", label: "English" },
                            ]}
                        />
                        <SelectField
                            label="Tema"
                            value={settings.theme}
                            onChange={(theme) => patch({ theme })}
                            options={[
                                { value: "light", label: "Açık" },
                                { value: "dark", label: "Koyu" },
                            ]}
                        />
                    </div>
                </article>
            </section>
        </>
    );
}

function SettingsCard({
    icon,
    title,
    rows,
}: {
    icon: React.ReactNode;
    title: string;
    rows: Array<{ label: string; checked: boolean; onChange: (value: boolean) => void }>;
}) {
    return (
        <article className={styles.card}>
            <div className={styles.cardHeader}>
                <h2>{title}</h2>
                {icon}
            </div>
            <div className={styles.stack}>
                {rows.map((row) => (
                    <label key={row.label} className={styles.surface} style={{ padding: 12, display: "flex", gap: 10, alignItems: "center" }}>
                        <input
                            type="checkbox"
                            checked={row.checked}
                            onChange={(event) => row.onChange(event.target.checked)}
                        />
                        <span>{row.label}</span>
                    </label>
                ))}
            </div>
        </article>
    );
}
