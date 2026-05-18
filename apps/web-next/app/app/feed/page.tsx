"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { AppHeader } from "@/components/app/AppHeader";
import { Button } from "@/components/primitives/Button";
import { Container } from "@/components/primitives/Container";
import {
    completeUpload,
    createContentPost,
    createUploadSession,
    currentProfile,
    getAccessToken,
    me,
    publishedPosts,
    refresh,
    setAccessToken,
    type ContentAuthorMode,
    type ContentPostType,
    type ContentPostView,
    type MeView,
} from "@/lib/authClient";
import styles from "./FeedPage.module.css";

const POST_TYPES: ContentPostType[] = [
    "LOCAL_UPDATE",
    "ANNOUNCEMENT",
    "EVENT",
    "ADVICE",
    "BUSINESS_PROMOTION",
];

export default function FeedPage() {
    const router = useRouter();
    const [meView, setMeView] = useState<MeView | null>(null);
    const [authorMode, setAuthorMode] = useState<ContentAuthorMode>("CUSTOMER");
    const [posts, setPosts] = useState<ContentPostView[]>([]);
    const [status, setStatus] = useState<"loading" | "ready">("loading");
    const [busy, setBusy] = useState(false);

    useEffect(() => {
        let cancelled = false;
        async function bootstrap() {
            try {
                if (!getAccessToken()) await refresh();
                const [fetchedMe, profile, fetchedPosts] = await Promise.all([me(), currentProfile(), publishedPosts()]);
                if (!cancelled) {
                    setMeView(fetchedMe);
                    setAuthorMode(profile.activeMode);
                    setPosts(fetchedPosts);
                    setStatus("ready");
                }
            } catch {
                if (!cancelled) {
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

    async function reload() {
        setPosts(await publishedPosts());
    }

    if (status !== "ready") {
        return (
            <main className={styles.loading}>
                <span>…</span>
            </main>
        );
    }

    return (
        <>
            <AppHeader me={meView} onLogout={() => setMeView(null)} />
            <main className={styles.page}>
                <Container>
                    <header className={styles.hero}>
                        <p className={styles.eyebrow}>Sosyal akış</p>
                        <h1>Çevrende neler oluyor?</h1>
                        <p>Duyuru, etkinlik, trafik notu ve organik işletme paylaşımı burada; iş akışı ayrı kalır.</p>
                    </header>
                    <section className={styles.layout}>
                        <CreatePostCard
                            authorMode={authorMode}
                            busy={busy}
                            onCreate={async (payload) => {
                                setBusy(true);
                                try {
                                    await createContentPost(payload);
                                    await reload();
                                } finally {
                                    setBusy(false);
                                }
                            }}
                        />
                        <div className={styles.stack}>
                            {posts.map((post) => (
                                <article key={post.id} className={styles.postCard}>
                                    <span className={styles.pill}>{post.postType}</span>
                                    <h3>{post.title || "Başlıksız paylaşım"}</h3>
                                    <p>{post.body}</p>
                                    <p className={styles.meta}>
                                        {post.authorMode} · {[post.district, post.city].filter(Boolean).join(", ") || "Konum yok"}
                                    </p>
                                </article>
                            ))}
                        </div>
                    </section>
                </Container>
            </main>
        </>
    );
}

function CreatePostCard({
    authorMode,
    busy,
    onCreate,
}: {
    authorMode: ContentAuthorMode;
    busy: boolean;
    onCreate: (payload: {
        authorMode: ContentAuthorMode;
        postType: ContentPostType;
        title?: string;
        body: string;
        city?: string;
        district?: string;
        eventStartsAt?: string;
        eventEndsAt?: string;
        mediaAssetIds?: string[];
    }) => Promise<void>;
}) {
    const [postType, setPostType] = useState<ContentPostType>("LOCAL_UPDATE");
    const [title, setTitle] = useState("");
    const [body, setBody] = useState("");
    const [city, setCity] = useState("");
    const [district, setDistrict] = useState("");
    const [eventStartsAt, setEventStartsAt] = useState("");
    const [eventEndsAt, setEventEndsAt] = useState("");
    const [mediaAssetIds, setMediaAssetIds] = useState<string[]>([]);

    async function attach(file: File | null) {
        if (!file) return;
        const session = await createUploadSession(
            file.type.startsWith("video/") ? "VIDEO" : "IMAGE",
            file.type || "application/octet-stream",
            file.name,
        );
        await fetch(session.uploadUrl, {
            method: "PUT",
            headers: session.requiredHeaders,
            body: file,
        });
        const asset = await completeUpload(session.assetId);
        setMediaAssetIds((current) => [...current, asset.id]);
    }

    return (
        <article className={styles.card}>
            <h2>Paylaşım oluştur</h2>
            <div className={styles.form}>
                <label className={styles.field}>
                    <span>Tür</span>
                    <select value={postType} onChange={(event) => setPostType(event.target.value as ContentPostType)}>
                        {POST_TYPES.filter((type) => type !== "BUSINESS_PROMOTION" || authorMode === "BUSINESS").map((type) => (
                            <option key={type} value={type}>
                                {type}
                            </option>
                        ))}
                    </select>
                </label>
                <Field label="Başlık" value={title} onChange={setTitle} />
                <Field label="Metin" value={body} onChange={setBody} textarea />
                <Field label="Şehir" value={city} onChange={setCity} />
                <Field label="İlçe" value={district} onChange={setDistrict} />
                {postType === "EVENT" && (
                    <>
                        <Field label="Başlangıç" value={eventStartsAt} onChange={setEventStartsAt} type="datetime-local" />
                        <Field label="Bitiş" value={eventEndsAt} onChange={setEventEndsAt} type="datetime-local" />
                    </>
                )}
                <label className={styles.field}>
                    <span>Görsel / video</span>
                    <input type="file" accept="image/*,video/*" onChange={(event) => attach(event.target.files?.[0] ?? null)} />
                </label>
                <p className={styles.meta}>{mediaAssetIds.length} medya eklendi.</p>
            </div>
            <Button
                disabled={busy || !body.trim() || (postType === "EVENT" && !eventStartsAt)}
                onClick={() =>
                    onCreate({
                        authorMode,
                        postType,
                        title: title || undefined,
                        body,
                        city: city || undefined,
                        district: district || undefined,
                        eventStartsAt: eventStartsAt ? new Date(eventStartsAt).toISOString() : undefined,
                        eventEndsAt: eventEndsAt ? new Date(eventEndsAt).toISOString() : undefined,
                        mediaAssetIds,
                    })
                }
            >
                Paylaş
            </Button>
        </article>
    );
}

function Field({
    label,
    value,
    onChange,
    textarea = false,
    type = "text",
}: {
    label: string;
    value: string;
    onChange: (value: string) => void;
    textarea?: boolean;
    type?: string;
}) {
    return (
        <label className={styles.field}>
            <span>{label}</span>
            {textarea ? (
                <textarea value={value} onChange={(event) => onChange(event.target.value)} />
            ) : (
                <input type={type} value={value} onChange={(event) => onChange(event.target.value)} />
            )}
        </label>
    );
}
