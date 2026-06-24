"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import { useSearchParams } from "next/navigation";
import { Bell, CalendarDays, LayoutGrid, MoreHorizontal, Newspaper, Search, Send, SlidersHorizontal } from "lucide-react";
import { AnnouncementMap } from "@/components/product/feed/AnnouncementMap";
import { CompactPostActions } from "@/components/product/feed/CompactPostActions";
import { CreatePostModal } from "@/components/product/feed/CreatePostModal";
import { FilterPanel } from "@/components/product/feed/FilterPanel";
import {
    announcementKindFor,
    announcementMeta,
    cleanTagInput,
    displayTypeLabel,
    distanceWithinRadius,
    feedFilters,
    initials,
    isAnnouncementItem,
    isVideoMedia,
    matchesSearch,
    normalizeAnnouncementKind,
    type AnnouncementKind,
    type DistanceValue,
    type FeedFilter,
} from "@/components/product/feed/feedModel";
import styles from "@/components/product/ProductPages.module.css";
import { useAppSession } from "@/components/product/ProductAppShell";
import {
    formatRelative,
    mockAppApi,
    type FeedItemView,
    type HashtagSuggestionView,
    type AnnouncementIncidentView,
    type PostCommentView,
    type SponsoredAdView,
} from "@/lib/mockAppClient";

const FEED_BATCH_SIZE = 8;

type ComposerType = "POST" | "ANNOUNCEMENT" | "EVENT";

const fallbackAd: SponsoredAdView = {
    id: "feed-ad-placeholder",
    title: "Yakınındaki işletmeler için vitrin alanı",
    body: "Bu alan ileride kullanıcı çerezleri, konum ve ilgi sinyalleriyle kişiselleştirilecek reklam/sponsorlu içerik kartı olarak çalışacak.",
    advertiser: "Viaverse",
    imageUrl: "/brand/assets/categories/advisory.png",
    displayUrl: "viaverse.app",
    reason: "Konum ve ilgi sinyallerine göre",
};

export default function AppHomePage() {
    const { session } = useAppSession();
    const searchParams = useSearchParams();
    const [filter, setFilter] = useState<FeedFilter>("ALL");
    const [query, setQuery] = useState("");
    const [filtersOpen, setFiltersOpen] = useState(false);
    const [viewMode, setViewMode] = useState<"feed" | "map">("feed");
    const [tagDraft, setTagDraft] = useState("");
    const [selectedTags, setSelectedTags] = useState<string[]>([]);
    const [locationQuery, setLocationQuery] = useState("");
    const [distance, setDistance] = useState<DistanceValue>(8);
    const [selectedAnnouncementKinds, setSelectedAnnouncementKinds] = useState<AnnouncementKind[]>([]);
    const [feed, setFeed] = useState<FeedItemView[]>([]);
    const [ads, setAds] = useState<SponsoredAdView[]>([]);
    const [incidents, setIncidents] = useState<AnnouncementIncidentView[]>([]);
    const [hashtags, setHashtags] = useState<HashtagSuggestionView[]>([]);
    const [status, setStatus] = useState<"loading" | "ready" | "error">("loading");
    const [notice, setNotice] = useState<string | null>(null);
    const [visibleCount, setVisibleCount] = useState(FEED_BATCH_SIZE);
    const [pullDistance, setPullDistance] = useState(0);
    const [refreshing, setRefreshing] = useState(false);
    const [composerOpen, setComposerOpen] = useState(false);
    const [composerInitialType, setComposerInitialType] = useState<ComposerType>("POST");
    const [editingPost, setEditingPost] = useState<FeedItemView | null>(null);
    const [focusedPostId, setFocusedPostId] = useState<string | null>(null);

    const loadMoreRef = useRef<HTMLDivElement | null>(null);
    const pullStartYRef = useRef<number | null>(null);

    useEffect(() => {
        const type = searchParams.get("type")?.toUpperCase();
        if (type && feedFilters.some((item) => item.value === type)) {
            setFilter(type as FeedFilter);
        }
    }, [searchParams]);

    async function load() {
        setStatus("loading");
        try {
            const [nextFeed, nextAds, nextIncidents] = await Promise.all([
                mockAppApi.feed("SOCIAL"),
                mockAppApi.sponsoredAds("home"),
                mockAppApi.announcementIncidents(),
            ]);
            setFeed(nextFeed);
            setAds(nextAds.length > 0 ? nextAds : [fallbackAd]);
            setIncidents(nextIncidents);
            setStatus("ready");
        } catch {
            setStatus("error");
        }
    }

    useEffect(() => {
        load();
    }, []);

    useEffect(() => {
        let cancelled = false;
        async function loadHashtags() {
            try {
                const next = await mockAppApi.hashtagSuggestions(tagDraft.trim() || undefined);
                if (!cancelled) setHashtags(next);
            } catch {
                // non-critical
            }
        }
        loadHashtags();
        return () => {
            cancelled = true;
        };
    }, [tagDraft]);

    const visibleFeed = useMemo(() => {
        const normalizedLocation = locationQuery.trim().toLocaleLowerCase("tr-TR");
        const allowedTypes = new Set(feedFilters.find((item) => item.value === filter)?.types ?? []);
        return feed.filter((item) => {
            const typeMatches = filter === "ALL" || allowedTypes.has(item.type);
            const locationMatches =
                !normalizedLocation ||
                item.locationLabel.toLocaleLowerCase("tr-TR").includes(normalizedLocation) ||
                item.body.toLocaleLowerCase("tr-TR").includes(normalizedLocation);
            const distanceMatches = distanceWithinRadius(item.locationLabel, distance);
            const tagMatches = selectedTags.every((tag) => item.hashtags?.includes(tag));
            const textMatches = matchesSearch(item, query);
            const announcementKind = announcementKindFor(item);
            const announcementMatches =
                filter !== "ANNOUNCEMENT" ||
                selectedAnnouncementKinds.length === 0 ||
                (announcementKind !== null && selectedAnnouncementKinds.includes(announcementKind));

            return typeMatches && locationMatches && distanceMatches && tagMatches && textMatches && announcementMatches;
        });
    }, [distance, feed, filter, locationQuery, query, selectedAnnouncementKinds, selectedTags]);

    const postsById = useMemo(() => new Map(feed.map((item) => [item.id, item])), [feed]);
    const visibleIncidents = useMemo(() => {
        const normalizedLocation = locationQuery.trim().toLocaleLowerCase("tr-TR");
        return incidents.filter((incident) => {
            const kind = normalizeAnnouncementKind(incident.kind);
            const kindMatches = kind && (selectedAnnouncementKinds.length === 0 || selectedAnnouncementKinds.includes(kind));
            const locationMatches = !normalizedLocation || incident.locationLabel.toLocaleLowerCase("tr-TR").includes(normalizedLocation);
            const distanceMatches = distanceWithinRadius(incident.locationLabel, distance);
            return Boolean(kindMatches) && locationMatches && distanceMatches;
        });
    }, [distance, incidents, locationQuery, selectedAnnouncementKinds]);

    const renderedFeed = useMemo(() => {
        if (visibleFeed.length === 0) return [];
        return Array.from({ length: visibleCount }, (_, index) => {
            const item = visibleFeed[index % visibleFeed.length];
            return {
                item,
                renderKey: `${item.id}-${Math.floor(index / visibleFeed.length)}`,
            };
        });
    }, [visibleCount, visibleFeed]);

    const activeFilterCount = [
        selectedTags.length > 0,
        Boolean(locationQuery.trim()),
        distance !== 8,
        filter === "ANNOUNCEMENT" && selectedAnnouncementKinds.length > 0,
    ].filter(Boolean).length;

    useEffect(() => {
        if (filter !== "ANNOUNCEMENT" && viewMode === "map") {
            setViewMode("feed");
        }
    }, [filter, viewMode]);

    useEffect(() => {
        setVisibleCount(visibleFeed.length === 0 ? 0 : FEED_BATCH_SIZE);
    }, [visibleFeed]);

    useEffect(() => {
        const node = loadMoreRef.current;
        if (!node || visibleFeed.length === 0 || viewMode !== "feed") return;

        const observer = new IntersectionObserver(
            (entries) => {
                if (entries.some((entry) => entry.isIntersecting)) {
                    setVisibleCount((current) => current + FEED_BATCH_SIZE);
                }
            },
            { rootMargin: "600px 0px" },
        );

        observer.observe(node);
        return () => observer.disconnect();
    }, [viewMode, visibleFeed.length]);

    useEffect(() => {
        function handleTouchStart(event: TouchEvent) {
            if (window.scrollY > 0 || refreshing) return;
            pullStartYRef.current = event.touches[0]?.clientY ?? null;
        }

        function handleTouchMove(event: TouchEvent) {
            if (pullStartYRef.current === null || window.scrollY > 0) return;
            const delta = (event.touches[0]?.clientY ?? 0) - pullStartYRef.current;
            if (delta <= 0) {
                setPullDistance(0);
                return;
            }
            setPullDistance(Math.min(delta, 110));
        }

        async function handleTouchEnd() {
            if (pullDistance >= 76 && !refreshing) {
                setRefreshing(true);
                setNotice("Akış yenileniyor...");
                await load();
                setNotice("Akış yenilendi.");
                setVisibleCount(FEED_BATCH_SIZE);
                setRefreshing(false);
            }
            pullStartYRef.current = null;
            setPullDistance(0);
        }

        window.addEventListener("touchstart", handleTouchStart, { passive: true });
        window.addEventListener("touchmove", handleTouchMove, { passive: true });
        window.addEventListener("touchend", handleTouchEnd);

        return () => {
            window.removeEventListener("touchstart", handleTouchStart);
            window.removeEventListener("touchmove", handleTouchMove);
            window.removeEventListener("touchend", handleTouchEnd);
        };
    }, [pullDistance, refreshing]);

    function replacePost(next: FeedItemView) {
        setFeed((current) => current.map((item) => (item.id === next.id ? next : item)));
    }

    function upsertPost(next: FeedItemView) {
        setFeed((current) => {
            const exists = current.some((item) => item.id === next.id);
            return exists ? current.map((item) => (item.id === next.id ? next : item)) : [next, ...current];
        });
        setNotice(editingPost ? "Paylaşım güncellendi." : "Paylaşım yayınlandı.");
        setEditingPost(null);
    }

    function addSelectedTag(tag: string) {
        const cleanTag = cleanTagInput(tag);
        if (!cleanTag) return;
        setSelectedTags((current) => (current.includes(cleanTag) ? current : [...current, cleanTag]));
        setTagDraft("");
    }

    function removeSelectedTag(tag: string) {
        setSelectedTags((current) => current.filter((item) => item !== tag));
    }

    function toggleAnnouncementKind(kind: AnnouncementKind) {
        setSelectedAnnouncementKinds((current) =>
            current.includes(kind) ? current.filter((item) => item !== kind) : [...current, kind],
        );
    }

    function clearFilters() {
        setQuery("");
        setTagDraft("");
        setSelectedTags([]);
        setLocationQuery("");
        setDistance(8);
        setSelectedAnnouncementKinds([]);
    }

    function openComposer(type: ComposerType, post: FeedItemView | null = null) {
        setComposerInitialType(type);
        setEditingPost(post);
        setComposerOpen(true);
    }

    function openPostFromMap(item: FeedItemView) {
        setFilter(isAnnouncementItem(item) ? "ANNOUNCEMENT" : item.type === "EVENT" ? "EVENT" : "ALL");
        setViewMode("feed");
        setFocusedPostId(item.id);
        window.setTimeout(() => {
            document.getElementById(`post-${item.id}`)?.scrollIntoView({ behavior: "smooth", block: "center" });
        }, 80);
    }

    return (
        <section className={styles.socialHome}>
            <FeedModeRail
                selected={filter}
                feed={feed}
                onSelect={(next) => {
                    setFilter(next);
                    setViewMode("feed");
                }}
            />
            <main className={styles.homeFeedColumn}>
                <div
                    className={styles.pullRefresh}
                    style={{ height: pullDistance > 0 || refreshing ? Math.max(44, pullDistance) : 0 }}
                >
                    <span>{refreshing ? "Akış yenileniyor..." : pullDistance >= 76 ? "Bırak ve yenile" : "Yenilemek için aşağı çek"}</span>
                </div>

                <section className={styles.feedHeroSurface}>
                    <button type="button" className={styles.shareTrigger} onClick={() => openComposer("POST")}>
                        <span>{session.currentUser.initials}</span>
                        <strong>Bir şey paylaş...</strong>
                    </button>
                </section>

                <section className={styles.feedCommandSurface}>
                    <div className={styles.feedSearchRow}>
                        <label className={styles.searchBox}>
                            <Search size={17} aria-hidden />
                            <input
                                value={query}
                                onChange={(event) => setQuery(event.target.value)}
                                placeholder="Kişi, etiket veya paylaşım içeriği ara"
                            />
                        </label>
                        <button
                            type="button"
                            className={[styles.filterButton, filtersOpen && styles.filterButtonActive].filter(Boolean).join(" ")}
                            onClick={() => setFiltersOpen((current) => !current)}
                        >
                            <SlidersHorizontal size={17} aria-hidden />
                            Filtrele
                            {activeFilterCount > 0 && <span>{activeFilterCount}</span>}
                        </button>
                    </div>
                    {filter === "ANNOUNCEMENT" && (
                        <div className={styles.feedViewSwitch}>
                            <button type="button" className={viewMode === "feed" ? styles.segmentActive : ""} onClick={() => setViewMode("feed")}>
                                Duyuru akışı
                            </button>
                            <button type="button" className={viewMode === "map" ? styles.segmentActive : ""} onClick={() => setViewMode("map")}>
                                Harita
                            </button>
                        </div>
                    )}
                    <FilterPanel
                        open={filtersOpen}
                        selectedFilter={filter}
                        tagDraft={tagDraft}
                        onTagDraftChange={setTagDraft}
                        selectedTags={selectedTags}
                        tagSuggestions={hashtags}
                        onAddTag={addSelectedTag}
                        onRemoveTag={removeSelectedTag}
                        locationQuery={locationQuery}
                        onLocationChange={setLocationQuery}
                        distance={distance}
                        onDistanceChange={setDistance}
                        selectedAnnouncementKinds={selectedAnnouncementKinds}
                        onToggleAnnouncementKind={toggleAnnouncementKind}
                        onClear={clearFilters}
                        onClose={() => setFiltersOpen(false)}
                    />
                </section>

                {notice && <p className={styles.statusBadge}>{notice}</p>}
                {status === "loading" && <div className={styles.empty}>Akış yükleniyor...</div>}
                {status === "error" && <div className={styles.empty}>Akış alınamadı.</div>}
                {status === "ready" && filter === "ANNOUNCEMENT" && viewMode === "map" && (
                    <AnnouncementMap
                        incidents={visibleIncidents}
                        postsById={postsById}
                        selectedKinds={selectedAnnouncementKinds}
                        onOpenPost={openPostFromMap}
                    />
                )}
                {status === "ready" && viewMode === "feed" && (
                    <div className={styles.feedGrid}>
                        {renderedFeed.map(({ item, renderKey }) => (
                            <SocialPostCard
                                key={renderKey}
                                item={item}
                                currentUserName={session.currentUser.displayName}
                                focused={focusedPostId === item.id}
                                onEdit={(post) => openComposer(isAnnouncementItem(post) ? "ANNOUNCEMENT" : post.type === "EVENT" ? "EVENT" : "POST", post)}
                                onPostChanged={replacePost}
                            />
                        ))}
                        {visibleFeed.length === 0 && <div className={styles.empty}>Bu aramaya uygun paylaşım yok.</div>}
                    </div>
                )}
                {status === "ready" && viewMode === "feed" && visibleFeed.length > 0 && (
                    <div ref={loadMoreRef} className={styles.feedLoader}>
                        <span>Daha fazla paylaşım yükleniyor...</span>
                    </div>
                )}
            </main>
            <CreatePostModal
                open={composerOpen}
                currentUserInitials={session.currentUser.initials}
                currentUserName={session.currentUser.displayName}
                defaultLocation={session.currentUser.locationLabel}
                initialType={composerInitialType}
                editingPost={editingPost}
                onClose={() => {
                    setComposerOpen(false);
                    setEditingPost(null);
                }}
                onSaved={upsertPost}
            />
            <FeedAdsRail ads={ads.length > 0 ? ads : [fallbackAd]} />
        </section>
    );
}

function FeedModeRail({
    selected,
    feed,
    onSelect,
}: {
    selected: FeedFilter;
    feed: FeedItemView[];
    onSelect: (value: FeedFilter) => void;
}) {
    const icons = {
        ALL: LayoutGrid,
        POST: Newspaper,
        ANNOUNCEMENT: Bell,
        EVENT: CalendarDays,
    } satisfies Record<FeedFilter, typeof LayoutGrid>;
    const counts = useMemo(() => {
        return Object.fromEntries(feedFilters.map((mode) => [
            mode.value,
            feed.filter((item) => mode.value === "ALL" || mode.types.includes(item.type)).length,
        ])) as Record<FeedFilter, number>;
    }, [feed]);

    return (
        <aside className={styles.feedModeRail} aria-label="Akış modu">
            {feedFilters.map((mode) => {
                const Icon = icons[mode.value];
                return (
                    <button
                        key={mode.value}
                        type="button"
                        className={selected === mode.value ? styles.feedModeActive : ""}
                        onClick={() => onSelect(mode.value)}
                    >
                        <Icon size={17} aria-hidden />
                        <span>{mode.label}</span>
                        <small>{counts[mode.value]}</small>
                    </button>
                );
            })}
        </aside>
    );
}

function FeedAdsRail({ ads }: { ads: SponsoredAdView[] }) {
    return (
        <aside className={styles.feedAdsRail} aria-label="Sponsorlu alanlar">
            {ads.slice(0, 2).map((ad) => (
                <article key={ad.id} className={styles.stickyAdCard}>
                    <img src={ad.imageUrl} alt="" />
                    <div>
                        <small>{ad.advertiser}</small>
                        <strong>{ad.title}</strong>
                        <p>{ad.body}</p>
                        <span>{ad.displayUrl}</span>
                    </div>
                </article>
            ))}
        </aside>
    );
}

function SocialPostCard({
    item,
    currentUserName,
    focused,
    onEdit,
    onPostChanged,
}: {
    item: FeedItemView;
    currentUserName: string;
    focused: boolean;
    onEdit: (item: FeedItemView) => void;
    onPostChanged: (item: FeedItemView) => void;
}) {
    const [commentsOpen, setCommentsOpen] = useState(false);
    const [comments, setComments] = useState<PostCommentView[]>([]);
    const [commentDraft, setCommentDraft] = useState("");
    const [busy, setBusy] = useState(false);
    const [menuOpen, setMenuOpen] = useState(false);
    const ownPost = item.authorName === currentUserName;
    const announcementKind = announcementKindFor(item);
    const announcement = announcementMeta(announcementKind);

    async function openComments() {
        const nextOpen = !commentsOpen;
        setCommentsOpen(nextOpen);
        if (nextOpen) {
            setComments(await mockAppApi.comments(item.id));
        }
    }

    async function like() {
        onPostChanged(await mockAppApi.likePost(item.id));
    }

    async function save() {
        onPostChanged(await mockAppApi.savePost(item.id));
    }

    async function share() {
        onPostChanged(await mockAppApi.sharePost(item.id));
    }

    async function comment() {
        if (!commentDraft.trim()) return;
        setBusy(true);
        try {
            await mockAppApi.createComment(item.id, commentDraft);
            setCommentDraft("");
            const [nextPost, nextComments] = await Promise.all([
                mockAppApi.feed("SOCIAL").then((items) => items.find((post) => post.id === item.id) ?? item),
                mockAppApi.comments(item.id),
            ]);
            onPostChanged(nextPost);
            setComments(nextComments);
        } finally {
            setBusy(false);
        }
    }

    return (
        <article id={`post-${item.id}`} className={[styles.socialPost, focused && styles.socialPostFocused].filter(Boolean).join(" ")}>
            <header className={styles.postHeader}>
                <span className={styles.postAvatar}>{initials(item.authorName)}</span>
                <div>
                    <strong>{item.authorName}</strong>
                    <small>{item.authorType} · {item.locationLabel} · {formatRelative(item.createdAt)}</small>
                </div>
                <div className={styles.postHeaderActions}>
                    {announcementKind ? (
                        <span className={styles.announcementBadge} data-tone={announcement.tone}>{announcement.shortLabel}</span>
                    ) : (
                        <span className={styles.softBadge}>{displayTypeLabel(item)}</span>
                    )}
                    {ownPost && (
                        <div className={styles.postMenu}>
                            <button type="button" className={styles.postMenuButton} onClick={() => setMenuOpen((current) => !current)} aria-label="Paylaşım seçenekleri">
                                <MoreHorizontal size={18} aria-hidden />
                            </button>
                            {menuOpen && (
                                <div className={styles.postMenuDropdown}>
                                    <button
                                        type="button"
                                        onClick={() => {
                                            setMenuOpen(false);
                                            onEdit(item);
                                        }}
                                    >
                                        Düzenle
                                    </button>
                                </div>
                            )}
                        </div>
                    )}
                </div>
            </header>
            <div className={styles.postBody}>
                <h3>{item.title}</h3>
                <p>{item.body}</p>
                {item.hashtags?.length > 0 && (
                    <div className={styles.hashtagRow}>
                        {item.hashtags.map((tag) => <span key={tag}>#{tag}</span>)}
                    </div>
                )}
            </div>
            <div className={item.mediaUrl ? styles.postMedia : styles.postMediaPlaceholder}>
                {item.mediaUrl ? (
                    <MediaPreview mediaUrl={item.mediaUrl} mediaType={item.mediaType} />
                ) : (
                    <span>{announcementKind ? announcement.label : displayTypeLabel(item)}</span>
                )}
            </div>
            <CompactPostActions
                liked={item.liked}
                saved={item.saved}
                likeCount={item.likeCount}
                commentCount={item.commentCount}
                shareCount={item.shareCount}
                onLike={like}
                onComment={openComments}
                onShare={share}
                onSave={save}
            />
            {commentsOpen && (
                <div className={styles.commentPanel}>
                    {comments.map((commentItem) => (
                        <div key={commentItem.id} className={styles.commentItem}>
                            <strong>{commentItem.authorName}</strong>
                            <span>{commentItem.body}</span>
                            <small>{formatRelative(commentItem.createdAt)}</small>
                        </div>
                    ))}
                    <div className={styles.commentComposer}>
                        <input value={commentDraft} onChange={(event) => setCommentDraft(event.target.value)} placeholder="Yorum yaz" />
                        <button type="button" disabled={busy || !commentDraft.trim()} onClick={comment}>
                            <Send size={16} aria-hidden />
                        </button>
                    </div>
                </div>
            )}
        </article>
    );
}

function MediaPreview({ mediaUrl, mediaType }: { mediaUrl: string; mediaType?: string | null }) {
    if (isVideoMedia(mediaUrl, mediaType)) {
        return <video src={mediaUrl} controls muted preload="metadata" />;
    }
    return <img src={mediaUrl} alt="" />;
}
