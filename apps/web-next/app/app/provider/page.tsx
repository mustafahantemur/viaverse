"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { BriefcaseBusiness, Send, ShieldCheck } from "lucide-react";
import { Button } from "@/components/primitives/Button";
import { TextField } from "@/components/product/ProductControls";
import styles from "@/components/product/ProductPages.module.css";
import { useAppSession } from "@/components/product/ProductAppShell";
import {
    mockAppApi,
    type OfferView,
    type OpportunityView,
    type ProfileView,
} from "@/lib/mockAppClient";

export default function ProviderPage() {
    const { session } = useAppSession();
    const [profile, setProfile] = useState<ProfileView | null>(null);
    const [opportunities, setOpportunities] = useState<OpportunityView[]>([]);
    const [offers, setOffers] = useState<OfferView[]>([]);
    const [message, setMessage] = useState<string | null>(null);

    async function load() {
        const [nextProfile, nextOpportunities, nextOffers] = await Promise.all([
            mockAppApi.profile(),
            mockAppApi.opportunities(),
            mockAppApi.myOffers(),
        ]);
        setProfile(nextProfile);
        setOpportunities(nextOpportunities);
        setOffers(nextOffers);
    }

    useEffect(() => {
        load();
    }, [session.currentUser.id]);

    const providerCapable = session.currentUser.capabilities.some(
        (capability) => capability.enabled && (capability.key === "INDIVIDUAL_PROVIDER" || capability.key === "BUSINESS"),
    );

    return (
        <>
            <section className={styles.intro}>
                <div>
                    <p className={styles.eyebrow}>Provider-aware varyasyon</p>
                    <h2>Hizmet veren modu fırsatları, profili ve teklifleri öne çıkarır.</h2>
                    <p>
                        Bu alan gerçek marketplace motoru değildir; ama hangi veri ve durumların gelecekte Web BFF
                        tarafından toplanacağını görünür yapar.
                    </p>
                </div>
                <div className={styles.actions}>
                    <Link href="/app/profile">
                        <Button variant="outline" leadingIcon={<ShieldCheck size={17} />}>Profil durumunu gör</Button>
                    </Link>
                </div>
            </section>

            {!providerCapable && (
                <article className={styles.card} style={{ marginBottom: 16 }}>
                    <div className={styles.cardHeader}>
                        <h2>Bu persona şu an hizmet veren değil</h2>
                    </div>
                    <p className={styles.muted}>
                        Üst bardaki mod seçiminden `Bireysel hizmet veren` veya `İşletme` personasına geçerek
                        provider akışını deneyebilirsin.
                    </p>
                </article>
            )}

            <section className={styles.grid3} style={{ marginBottom: 18 }}>
                <Metric title="Uygun fırsat" value={opportunities.length.toString()} />
                <Metric title="Gönderilen teklif" value={offers.length.toString()} />
                <Metric title="Aktif yetkinlik" value={session.currentUser.activeCapabilityLabel} />
            </section>

            <section className={styles.layout2}>
                <article className={styles.card}>
                    <div className={styles.cardHeader}>
                        <h2>Yakındaki fırsatlar</h2>
                        <BriefcaseBusiness size={20} aria-hidden />
                    </div>
                    <div className={styles.stack}>
                        {opportunities.length === 0 ? (
                            <div className={styles.empty}>Şimdilik uygun açık talep yok.</div>
                        ) : (
                            opportunities.map((opportunity) => (
                                <OpportunityCard
                                    key={opportunity.request.id}
                                    opportunity={opportunity}
                                    disabled={!providerCapable}
                                    onSubmitted={async () => {
                                        setMessage("Teklif gönderildi. Kabul edilirse mesajlaşma akışına bağlanacak.");
                                        await load();
                                    }}
                                />
                            ))
                        )}
                    </div>
                </article>

                <div className={styles.stack}>
                    <article className={styles.card}>
                        <div className={styles.cardHeader}>
                            <h2>Hizmet profili</h2>
                            <span className={styles.badge}>{profile?.activeCapability}</span>
                        </div>
                        {profile?.individualProviderProfile ? (
                            <>
                                <p className={styles.muted}>{profile.individualProviderProfile.serviceBlurb}</p>
                                <div className={styles.badgeRow} style={{ marginTop: 12 }}>
                                    <span className={styles.statusBadge}>{profile.individualProviderProfile.providerType}</span>
                                    <span className={styles.softBadge}>{profile.individualProviderProfile.locationScope}</span>
                                    <span className={styles.softBadge}>{profile.individualProviderProfile.availabilitySummary}</span>
                                </div>
                            </>
                        ) : (
                            <p className={styles.muted}>Bu persona için bireysel hizmet profili açık değil.</p>
                        )}
                    </article>

                    <article className={styles.card}>
                        <div className={styles.cardHeader}>
                            <h2>Tekliflerim</h2>
                        </div>
                        {message && <p className={styles.statusBadge}>{message}</p>}
                        <div className={styles.stack} style={{ marginTop: message ? 12 : 0 }}>
                            {offers.length === 0 ? (
                                <div className={styles.empty}>Henüz teklif göndermedin.</div>
                            ) : (
                                offers.map((offer) => (
                                    <div key={offer.id} className={styles.feedCard}>
                                        <div className={styles.cardHeader}>
                                            <h3>{offer.amountExpectation}</h3>
                                            <span className={styles.statusBadge}>{offer.status}</span>
                                        </div>
                                        <p className={styles.muted}>{offer.message}</p>
                                        {offer.conversationId && (
                                            <div className={styles.actions}>
                                                <Link href="/app/messages">
                                                    <Button variant="outline">Mesajlaşmaya git</Button>
                                                </Link>
                                            </div>
                                        )}
                                    </div>
                                ))
                            )}
                        </div>
                    </article>
                </div>
            </section>
        </>
    );
}

function Metric({ title, value }: { title: string; value: string }) {
    return (
        <article className={styles.card}>
            <div className={styles.metric}>
                <strong>{value}</strong>
                <span>{title}</span>
            </div>
        </article>
    );
}

function OpportunityCard({
    opportunity,
    disabled,
    onSubmitted,
}: {
    opportunity: OpportunityView;
    disabled: boolean;
    onSubmitted: () => Promise<void>;
}) {
    const [amountExpectation, setAmountExpectation] = useState("");
    const [message, setMessage] = useState("");
    const [busy, setBusy] = useState(false);

    async function submit() {
        setBusy(true);
        try {
            await mockAppApi.createOffer({
                requestId: opportunity.request.id,
                amountExpectation,
                message,
            });
            setAmountExpectation("");
            setMessage("");
            await onSubmitted();
        } finally {
            setBusy(false);
        }
    }

    return (
        <article className={styles.feedCard}>
            <div className={styles.cardHeader}>
                <div>
                    <h3>{opportunity.request.title}</h3>
                    <p className={styles.muted}>{opportunity.request.description}</p>
                </div>
                <span className={styles.statusBadge}>{opportunity.fitScore}%</span>
            </div>
            <div className={styles.badgeRow}>
                <span className={styles.badge}>{opportunity.matchReason}</span>
                <span className={styles.softBadge}>{opportunity.request.categoryLabel}</span>
                <span className={styles.softBadge}>{opportunity.request.locationScope}</span>
                <span className={styles.softBadge}>{opportunity.request.timing}</span>
            </div>
            <div className={styles.form} style={{ marginTop: 14 }}>
                <TextField label="Tutar beklentisi" value={amountExpectation} onChange={setAmountExpectation} placeholder="450 TL" />
                <TextField label="Teklif notu" value={message} onChange={setMessage} placeholder="Nasıl yardımcı olacağını açıkla." />
            </div>
            <div className={styles.actions}>
                <Button
                    disabled={disabled || busy || !amountExpectation.trim() || !message.trim()}
                    onClick={submit}
                    leadingIcon={<Send size={16} />}
                >
                    Teklif gönder
                </Button>
            </div>
        </article>
    );
}
