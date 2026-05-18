"use client";

import { useRouter } from "next/navigation";
import { useEffect, useMemo, useState } from "react";
import { AppHeader } from "@/components/app/AppHeader";
import { Button } from "@/components/primitives/Button";
import { Container } from "@/components/primitives/Container";
import {
    acceptOffer,
    completeJob,
    createServiceRequest,
    currentProfile,
    getAccessToken,
    listOffers,
    me,
    myJobs,
    myServiceRequests,
    refresh,
    setAccessToken,
    startJob,
    submitOffer,
    workFeed,
    type CurrentProfileView,
    type JobView,
    type MeView,
    type OfferView,
    type ServiceCategory,
    type ServiceRequestView,
} from "@/lib/authClient";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./MarketplacePage.module.css";

const CATEGORIES: ServiceCategory[] = [
    "HOME_REPAIR",
    "DIGITAL_SOFTWARE",
    "CREATIVE_MEDIA",
    "EDUCATION",
    "CLEANING",
    "LOGISTICS",
    "CARE_HEALTH",
    "PROFESSIONAL_CONSULTING",
    "PETS",
    "EVENTS",
    "LOCAL_HELP",
];

export default function MarketplacePage() {
    const router = useRouter();
    const { t } = useTranslation();
    const [meView, setMeView] = useState<MeView | null>(null);
    const [profile, setProfile] = useState<CurrentProfileView | null>(null);
    const [workRequests, setWorkRequests] = useState<ServiceRequestView[]>([]);
    const [myRequests, setMyRequests] = useState<ServiceRequestView[]>([]);
    const [jobs, setJobs] = useState<JobView[]>([]);
    const [offersByRequest, setOffersByRequest] = useState<Record<string, OfferView[]>>({});
    const [status, setStatus] = useState<"loading" | "ready" | "error">("loading");
    const [busy, setBusy] = useState(false);
    const [message, setMessage] = useState<string | null>(null);

    useEffect(() => {
        let cancelled = false;
        async function bootstrap() {
            try {
                if (!getAccessToken()) {
                    await refresh();
                }
                const [fetchedMe, fetchedProfile, fetchedWorkFeed, fetchedMine, fetchedJobs] = await Promise.all([
                    me(),
                    currentProfile(),
                    workFeed(),
                    myServiceRequests(),
                    myJobs(),
                ]);
                if (!cancelled) {
                    setMeView(fetchedMe);
                    setProfile(fetchedProfile);
                    setWorkRequests(fetchedWorkFeed);
                    setMyRequests(fetchedMine);
                    setJobs(fetchedJobs);
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

    const canOffer = useMemo(
        () =>
            profile?.capabilities.some(
                (capability) =>
                    capability.status === "ENABLED" &&
                    (capability.capability === "INDIVIDUAL_PROVIDER" || capability.capability === "BUSINESS"),
            ) ?? false,
        [profile],
    );

    async function refreshLists() {
        const [fetchedWorkFeed, fetchedMine, fetchedJobs] = await Promise.all([
            workFeed(),
            myServiceRequests(),
            myJobs(),
        ]);
        setWorkRequests(fetchedWorkFeed);
        setMyRequests(fetchedMine);
        setJobs(fetchedJobs);
    }

    async function run(action: () => Promise<void>) {
        setBusy(true);
        setMessage(null);
        try {
            await action();
            setMessage(t.marketplace.saved);
        } finally {
            setBusy(false);
        }
    }

    if (status !== "ready") {
        return (
            <main className={styles.loading}>
                <span>{t.common.loading}</span>
            </main>
        );
    }

    return (
        <>
            <AppHeader me={meView} onLogout={() => setMeView(null)} />
            <main className={styles.page}>
                <Container>
                    <header className={styles.hero}>
                        <p className={styles.eyebrow}>{t.marketplace.eyebrow}</p>
                        <h1>{t.marketplace.title}</h1>
                        <p>{t.marketplace.subtitle}</p>
                    </header>

                    <section className={styles.layout}>
                        {message && <p className={styles.message}>{message}</p>}

                        <div className={styles.grid}>
                            <CreateRequestCard
                                busy={busy}
                                onCreate={(payload) =>
                                    run(async () => {
                                        await createServiceRequest(payload);
                                        await refreshLists();
                                    })
                                }
                            />
                            <article className={styles.card}>
                                <div className={styles.cardHeader}>
                                    <h2>{t.marketplace.openTitle}</h2>
                                </div>
                                <p className={styles.muted}>{t.marketplace.workFeedHint}</p>
                                <div id="open" className={styles.stack}>
                                    {workRequests.length === 0 ? (
                                        <p className={styles.empty}>{t.marketplace.noOpen}</p>
                                    ) : (
                                        workRequests.map((request) => (
                                            <OpenRequestCard
                                                key={request.id}
                                                request={request}
                                                busy={busy}
                                                canOffer={canOffer}
                                                onSubmit={(amountMinor, note) =>
                                                    run(async () => {
                                                        await submitOffer(request.id, amountMinor, "TRY", note);
                                                    })
                                                }
                                            />
                                        ))
                                    )}
                                </div>
                            </article>
                        </div>

                        <div className={styles.grid}>
                            <article className={styles.card}>
                                <div className={styles.cardHeader}>
                                    <h2>{t.marketplace.mineTitle}</h2>
                                </div>
                                <p className={styles.muted}>{t.marketplace.helper}</p>
                                <div className={styles.stack}>
                                    {myRequests.length === 0 ? (
                                        <p className={styles.empty}>{t.marketplace.noMine}</p>
                                    ) : (
                                        myRequests.map((request) => (
                                            <MyRequestCard
                                                key={request.id}
                                                request={request}
                                                offers={offersByRequest[request.id]}
                                                busy={busy}
                                                onLoadOffers={() =>
                                                    run(async () => {
                                                        const offers = await listOffers(request.id);
                                                        setOffersByRequest((current) => ({
                                                            ...current,
                                                            [request.id]: offers,
                                                        }));
                                                    })
                                                }
                                                onAccept={(offerId) =>
                                                    run(async () => {
                                                        await acceptOffer(request.id, offerId);
                                                        await refreshLists();
                                                        const offers = await listOffers(request.id);
                                                        setOffersByRequest((current) => ({
                                                            ...current,
                                                            [request.id]: offers,
                                                        }));
                                                    })
                                                }
                                            />
                                        ))
                                    )}
                                </div>
                            </article>

                            <article className={styles.card}>
                                <div className={styles.cardHeader}>
                                    <h2>{t.marketplace.jobsTitle}</h2>
                                </div>
                                <div className={styles.stack}>
                                    {jobs.length === 0 ? (
                                        <p className={styles.empty}>{t.marketplace.noJobs}</p>
                                    ) : (
                                        jobs.map((job) => (
                                            <JobCard
                                                key={job.id}
                                                job={job}
                                                meId={meView?.id}
                                                busy={busy}
                                                onStart={() =>
                                                    run(async () => {
                                                        await startJob(job.id);
                                                        await refreshLists();
                                                    })
                                                }
                                                onComplete={() =>
                                                    run(async () => {
                                                        await completeJob(job.id);
                                                        await refreshLists();
                                                    })
                                                }
                                            />
                                        ))
                                    )}
                                </div>
                            </article>
                        </div>
                    </section>
                </Container>
            </main>
        </>
    );
}

function CreateRequestCard({
    busy,
    onCreate,
}: {
    busy: boolean;
    onCreate: (payload: {
        title: string;
        description: string;
        category: ServiceCategory;
        budgetMinAmountMinor?: number;
        budgetMaxAmountMinor?: number;
        remoteAllowed: boolean;
        district?: string;
        city?: string;
    }) => Promise<void>;
}) {
    const { t } = useTranslation();
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [category, setCategory] = useState<ServiceCategory>("LOCAL_HELP");
    const [budgetMin, setBudgetMin] = useState("");
    const [budgetMax, setBudgetMax] = useState("");
    const [remoteAllowed, setRemoteAllowed] = useState(false);
    const [district, setDistrict] = useState("");
    const [city, setCity] = useState("");

    return (
        <article id="create" className={styles.card}>
            <div className={styles.cardHeader}>
                <h2>{t.marketplace.createTitle}</h2>
            </div>
            <div className={styles.formGrid}>
                <Field label={t.marketplace.titleLabel} value={title} onChange={setTitle} />
                <Field label={t.marketplace.descriptionLabel} value={description} onChange={setDescription} textarea />
                <label className={styles.field}>
                    <span>{t.marketplace.categoryLabel}</span>
                    <select value={category} onChange={(event) => setCategory(event.target.value as ServiceCategory)}>
                        {CATEGORIES.map((item) => (
                            <option key={item} value={item}>
                                {t.marketplace.categoryNames[item]}
                            </option>
                        ))}
                    </select>
                </label>
                <Field label={t.marketplace.budgetMinLabel} value={budgetMin} onChange={setBudgetMin} />
                <Field label={t.marketplace.budgetMaxLabel} value={budgetMax} onChange={setBudgetMax} />
                <Field label={t.marketplace.districtLabel} value={district} onChange={setDistrict} />
                <Field label={t.marketplace.cityLabel} value={city} onChange={setCity} />
                <label className={styles.checkbox}>
                    <input
                        type="checkbox"
                        checked={remoteAllowed}
                        onChange={(event) => setRemoteAllowed(event.target.checked)}
                    />
                    <span>{t.marketplace.remoteAllowedLabel}</span>
                </label>
            </div>
            <Button
                disabled={busy || !title.trim() || !description.trim()}
                onClick={() =>
                    onCreate({
                        title,
                        description,
                        category,
                        budgetMinAmountMinor: toMinor(budgetMin),
                        budgetMaxAmountMinor: toMinor(budgetMax),
                        remoteAllowed,
                        district: district || undefined,
                        city: city || undefined,
                    })
                }
            >
                {t.marketplace.create}
            </Button>
        </article>
    );
}

function OpenRequestCard({
    request,
    busy,
    canOffer,
    onSubmit,
}: {
    request: ServiceRequestView;
    busy: boolean;
    canOffer: boolean;
    onSubmit: (amountMinor: number, note: string) => Promise<void>;
}) {
    const { t } = useTranslation();
    const [amount, setAmount] = useState("");
    const [note, setNote] = useState("");

    return (
        <article className={styles.requestCard}>
            <div className={styles.cardHeader}>
                <h3>{request.title}</h3>
                <span className={styles.meta}>{request.status}</span>
            </div>
            <p>{request.description}</p>
            <p className={styles.meta}>
                {t.marketplace.categoryNames[request.category]} · {formatBudget(request)}
            </p>
            <div className={styles.formGrid}>
                <Field label={t.marketplace.amount} value={amount} onChange={setAmount} />
                <Field label={t.marketplace.offerMessageLabel} value={note} onChange={setNote} />
            </div>
            <Button
                variant="outline"
                disabled={busy || !canOffer || !amount.trim()}
                onClick={() => onSubmit(toMinor(amount) ?? 0, note)}
            >
                {t.marketplace.submitOffer}
            </Button>
        </article>
    );
}

function MyRequestCard({
    request,
    offers,
    busy,
    onLoadOffers,
    onAccept,
}: {
    request: ServiceRequestView;
    offers?: OfferView[];
    busy: boolean;
    onLoadOffers: () => Promise<void>;
    onAccept: (offerId: string) => Promise<void>;
}) {
    const { t } = useTranslation();
    return (
        <article className={styles.requestCard}>
            <div className={styles.cardHeader}>
                <h3>{request.title}</h3>
                <span className={styles.meta}>{request.status}</span>
            </div>
            <p className={styles.meta}>{formatBudget(request)}</p>
            <Button variant="outline" disabled={busy} onClick={onLoadOffers}>
                {t.marketplace.loadOffers}
            </Button>
            {offers && (
                <div className={styles.offerList}>
                    {offers.map((offer) => (
                        <div key={offer.id} className={styles.offer}>
                            <div className={styles.offerRow}>
                                <strong>{formatMoney(offer.amountMinor, offer.currency)}</strong>
                                <span className={styles.meta}>{offer.status}</span>
                            </div>
                            {offer.message && <span>{offer.message}</span>}
                            {offer.status === "SUBMITTED" && request.status === "OPEN" && (
                                <Button disabled={busy} onClick={() => onAccept(offer.id)}>
                                    {t.marketplace.acceptOffer}
                                </Button>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </article>
    );
}

function JobCard({
    job,
    meId,
    busy,
    onStart,
    onComplete,
}: {
    job: JobView;
    meId?: string;
    busy: boolean;
    onStart: () => Promise<void>;
    onComplete: () => Promise<void>;
}) {
    const { t } = useTranslation();
    const isProvider = meId === job.providerAccountId;
    const isRequester = meId === job.requesterAccountId;

    return (
        <article className={styles.jobCard}>
            <div className={styles.cardHeader}>
                <h3>{formatMoney(job.agreedAmountMinor, job.currency)}</h3>
                <span className={styles.meta}>{job.status}</span>
            </div>
            <p className={styles.meta}>
                {t.marketplace.requester}: {shortId(job.requesterAccountId)} · {t.marketplace.provider}:{" "}
                {shortId(job.providerAccountId)}
            </p>
            <div className={styles.actions}>
                {isProvider && job.status === "AGREED" && (
                    <Button variant="outline" disabled={busy} onClick={onStart}>
                        {t.marketplace.startJob}
                    </Button>
                )}
                {isRequester && job.status === "IN_PROGRESS" && (
                    <Button disabled={busy} onClick={onComplete}>
                        {t.marketplace.completeJob}
                    </Button>
                )}
            </div>
        </article>
    );
}

function Field({
    label,
    value,
    onChange,
    textarea = false,
}: {
    label: string;
    value: string;
    onChange: (value: string) => void;
    textarea?: boolean;
}) {
    return (
        <label className={styles.field}>
            <span>{label}</span>
            {textarea ? (
                <textarea value={value} onChange={(event) => onChange(event.target.value)} />
            ) : (
                <input value={value} onChange={(event) => onChange(event.target.value)} />
            )}
        </label>
    );
}

function toMinor(value: string): number | undefined {
    if (!value.trim()) return undefined;
    const normalized = Number(value.replace(",", "."));
    if (!Number.isFinite(normalized) || normalized < 0) return undefined;
    return Math.round(normalized * 100);
}

function formatMoney(amountMinor: number, currency: string): string {
    return new Intl.NumberFormat("tr-TR", { style: "currency", currency }).format(amountMinor / 100);
}

function formatBudget(request: ServiceRequestView): string {
    if (request.budgetMinAmountMinor == null && request.budgetMaxAmountMinor == null) {
        return "Bütçe belirtilmedi";
    }
    if (request.budgetMinAmountMinor != null && request.budgetMaxAmountMinor != null) {
        return `${formatMoney(request.budgetMinAmountMinor, request.currency)} – ${formatMoney(
            request.budgetMaxAmountMinor,
            request.currency,
        )}`;
    }
    if (request.budgetMinAmountMinor != null) {
        return `≥ ${formatMoney(request.budgetMinAmountMinor, request.currency)}`;
    }
    return `≤ ${formatMoney(request.budgetMaxAmountMinor ?? 0, request.currency)}`;
}

function shortId(value: string): string {
    return value.slice(0, 8);
}
