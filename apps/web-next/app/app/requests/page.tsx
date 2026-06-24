"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { CheckCircle2, MessageCircle, Send } from "lucide-react";
import { Button } from "@/components/primitives/Button";
import { SelectField, TextField } from "@/components/product/ProductControls";
import styles from "@/components/product/ProductPages.module.css";
import {
    formatRelative,
    mockAppApi,
    type OfferView,
    type ServiceCategoryView,
    type ServiceRequestView,
} from "@/lib/mockAppClient";

export default function RequestsPage() {
    const [categories, setCategories] = useState<ServiceCategoryView[]>([]);
    const [requests, setRequests] = useState<ServiceRequestView[]>([]);
    const [offersByRequest, setOffersByRequest] = useState<Record<string, OfferView[]>>({});
    const [message, setMessage] = useState<string | null>(null);

    async function load() {
        const [nextCategories, nextRequests] = await Promise.all([
            mockAppApi.categories(),
            mockAppApi.myRequests(),
        ]);
        setCategories(nextCategories);
        setRequests(nextRequests);
    }

    useEffect(() => {
        load();
    }, []);

    async function loadOffers(requestId: string) {
        const offers = await mockAppApi.requestOffers(requestId);
        setOffersByRequest((current) => ({ ...current, [requestId]: offers }));
    }

    async function acceptOffer(offerId: string) {
        const result = await mockAppApi.acceptOffer(offerId);
        setMessage("Teklif kabul edildi. Akış mesajlaşmaya geçti.");
        await load();
        await loadOffers(result.offer.requestId);
    }

    return (
        <>
            <section className={styles.intro}>
                <div>
                    <p className={styles.eyebrow}>Talep ve iş isteği</p>
                    <h2>Bir işi yaptırmak istediğinde net bir talep oluştur.</h2>
                    <p>
                        Kategori, konum/mesafe kapsamı, zamanlama ve bütçe beklentisi burada şekilleniyor.
                        Kabul edilen teklif doğrudan mesajlaşma yönüne bağlanır.
                    </p>
                </div>
            </section>

            {message && <p className={styles.statusBadge} style={{ marginBottom: 14 }}>{message}</p>}

            <section className={styles.layout2}>
                <CreateRequestCard categories={categories} onCreated={load} />

                <div className={styles.stack}>
                    <article className={styles.card}>
                        <div className={styles.cardHeader}>
                            <h2>Taleplerim</h2>
                            <span className={styles.badge}>{requests.length}</span>
                        </div>
                        {requests.length === 0 ? (
                            <div className={styles.empty}>Henüz talep oluşturmadın.</div>
                        ) : (
                            <div className={styles.stack}>
                                {requests.map((request) => (
                                    <RequestCard
                                        key={request.id}
                                        request={request}
                                        offers={offersByRequest[request.id]}
                                        onLoadOffers={() => loadOffers(request.id)}
                                        onAccept={acceptOffer}
                                    />
                                ))}
                            </div>
                        )}
                    </article>
                </div>
            </section>
        </>
    );
}

function CreateRequestCard({
    categories,
    onCreated,
}: {
    categories: ServiceCategoryView[];
    onCreated: () => Promise<void>;
}) {
    const [title, setTitle] = useState("");
    const [categoryId, setCategoryId] = useState("local-help");
    const [description, setDescription] = useState("");
    const [locationScope, setLocationScope] = useState("Yakınımda / 5 km");
    const [timing, setTiming] = useState("Bugün / yarın");
    const [budgetExpectation, setBudgetExpectation] = useState("");
    const [busy, setBusy] = useState(false);

    async function submit() {
        setBusy(true);
        try {
            await mockAppApi.createRequest({
                title,
                categoryId,
                description,
                locationScope,
                timing,
                budgetExpectation,
            });
            setTitle("");
            setDescription("");
            setBudgetExpectation("");
            await onCreated();
        } finally {
            setBusy(false);
        }
    }

    return (
        <article className={styles.card}>
            <div className={styles.cardHeader}>
                <h2>Yeni talep</h2>
                <Send size={20} aria-hidden />
            </div>
            <div className={styles.form}>
                <TextField label="Başlık" value={title} onChange={setTitle} placeholder="Elektrik ustası önerisi arıyorum" />
                <SelectField
                    label="Kategori"
                    value={categoryId}
                    onChange={setCategoryId}
                    options={categories.map((category) => ({ value: category.id, label: category.label }))}
                />
                <TextField
                    label="Açıklama"
                    value={description}
                    onChange={setDescription}
                    textarea
                    placeholder="İhtiyacını, beklentini ve özel notları yaz."
                />
                <TextField label="Konum ve mesafe kapsamı" value={locationScope} onChange={setLocationScope} />
                <TextField label="Zamanlama" value={timing} onChange={setTiming} placeholder="Bugün 19:00, hafta sonu, esnek…" />
                <TextField label="Bütçe beklentisi" value={budgetExpectation} onChange={setBudgetExpectation} placeholder="300-500 TL veya keşif sonrası" />
            </div>
            <div className={styles.actions}>
                <Button disabled={busy || !title.trim() || !description.trim()} onClick={submit}>
                    Talebi yayınla
                </Button>
            </div>
        </article>
    );
}

function RequestCard({
    request,
    offers,
    onLoadOffers,
    onAccept,
}: {
    request: ServiceRequestView;
    offers?: OfferView[];
    onLoadOffers: () => Promise<void>;
    onAccept: (offerId: string) => Promise<void>;
}) {
    return (
        <article className={styles.feedCard}>
            <div className={styles.cardHeader}>
                <div>
                    <h3>{request.title}</h3>
                    <p className={styles.muted}>{request.description}</p>
                </div>
                <span className={request.status === "MATCHED" ? styles.statusBadge : styles.badge}>{request.status}</span>
            </div>
            <div className={styles.badgeRow}>
                <span className={styles.softBadge}>{request.categoryLabel}</span>
                <span className={styles.softBadge}>{request.locationScope}</span>
                <span className={styles.softBadge}>{request.timing}</span>
                <span className={styles.softBadge}>{request.budgetExpectation}</span>
                <span className={styles.softBadge}>{formatRelative(request.createdAt)}</span>
            </div>
            <div className={styles.actions}>
                <Button variant="outline" onClick={onLoadOffers}>Teklifleri gör ({request.offerCount})</Button>
                {request.conversationId && (
                    <Link href="/app/messages">
                        <Button variant="outline" leadingIcon={<MessageCircle size={16} />}>Mesaja git</Button>
                    </Link>
                )}
            </div>
            {offers && (
                <div className={styles.stack} style={{ marginTop: 14 }}>
                    {offers.length === 0 ? (
                        <div className={styles.empty}>Bu talebe henüz teklif yok.</div>
                    ) : (
                        offers.map((offer) => (
                            <div key={offer.id} className={styles.surface} style={{ padding: 14 }}>
                                <div className={styles.cardHeader}>
                                    <div>
                                        <h3>{offer.providerName}</h3>
                                        <p className={styles.muted}>{offer.message}</p>
                                    </div>
                                    <span className={styles.statusBadge}>{offer.amountExpectation}</span>
                                </div>
                                <div className={styles.badgeRow}>
                                    <span className={styles.softBadge}>{offer.providerType}</span>
                                    <span className={styles.softBadge}>{offer.status}</span>
                                </div>
                                {offer.status === "SUBMITTED" && request.status === "OPEN" && (
                                    <div className={styles.actions}>
                                        <Button onClick={() => onAccept(offer.id)} leadingIcon={<CheckCircle2 size={16} />}>
                                            Teklifi kabul et
                                        </Button>
                                    </div>
                                )}
                            </div>
                        ))
                    )}
                </div>
            )}
        </article>
    );
}
