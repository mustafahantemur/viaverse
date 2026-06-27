"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import { useSearchParams } from "next/navigation";
import { Map as MapIcon, PenLine, Search, SlidersHorizontal } from "lucide-react";
import { AnnouncementMap } from "@/components/product/feed/AnnouncementMap";
import { CompactPostActions as _CompactPostActions } from "@/components/product/feed/CompactPostActions";
import { CreatePostModal } from "@/components/product/feed/CreatePostModal";
import { FeedAdsRail } from "@/components/product/feed/FeedAdsRail";
import { FilterPanel } from "@/components/product/feed/FilterPanel";
import { SocialPostCard } from "@/components/product/feed/SocialPostCard";
import {
    announcementKindFor,
    cleanTagInput,
    distanceWithinRadius,
    feedFilters,
    isAnnouncementItem,
    matchesSearch,
    normalizeAnnouncementKind,
    type AnnouncementKind,
    type DistanceValue,
    type FeedFilter,
} from "@/components/product/feed/feedModel";
import styles from "@/components/product/ProductPages.module.css";
import { useAppSession } from "@/components/product/ProductAppShell";
import {
    mockAppApi,
    type AnnouncementIncidentView,
    type FeedItemView,
    type HashtagSuggestionView,
    type SponsoredAdView,
} from "@/lib/mockAppClient";

const FEED_BATCH_SIZE = 8;

type ComposerType = "POST" | "ANNOUNCEMENT" | "EVENT";

const fallbackAd: SponsoredAdView = {
    id: "feed-ad-placeholder",
    title: "Yakınındaki işletmeler için vitrin alanı",
    body: "Bu alan ileride kullanıcı çerezleri, konum ve ilgi sinyalleriyle kişiselleştirilecek sponsorlu içerik kartı olarak çalışacak.",
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
    const [mapVisible, setMapVisible] = useState(false);
    const [tagDraft, setTagDraft] = useState("");
    const [selectedTags, setSelectedTags] = useState<string[]>([]);
    const [locationQuery, setLocationQuery] = useState("");
    const [distance, setDistance] = useState<DistanceValue>(8);
    const [selectedAnnouncementKinds, setSelectedAnnouncementKinds] = useState<AnnouncementKind[]>([]);
    const [feed, setFeed] = useState<FeedItemView[]>([]);
    const [ads, setAds] = useState<SponsoredAdView[]>([fallbackAd]);
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

    useEffect(() => { load(); }, []);

    useEffect(() => {
        let cancelled = false;
        async function loadHashtags() {
            try {
                const next = await mockAppApi.hashtagSuggestions(tagDraft.trim() || undefined);
                if (!cancelled) setHashtags(next);
            } catch { /* non-critical */ }
        }
        loadHashtags();
        return () => { cancelled = true; };
    }, [tagDraft]);

    const visibleFeed = useMemo(() => {
        const normLocation = locationQuery.trim().toLocaleLowerCase("tr-TR");
        const allowedTypes = new Set(feedFilters.find((item) => item.value === filter)?.types ?? []);
        return feed.filter((item) => {
            const typeMatches = filter === "ALL" || allowedTypes.has(item.type);
            const locationMatches = !normLocation ||
                item.locationLabel.toLocaleLowerCase("tr-TR").includes(normLocation) ||
                item.body.toLocaleLowerCase("tr-TR").includes(normLocation);
            const distanceMatches = distanceWithinRadius(item.locationLabel, distance);
            const tagMatches = selectedTags.every((tag) => item.hashtags?.includes(tag));
            const textMatches = matchesSearch(item, query);
            const announcementKind = announcementKindFor(item);
            const announcementMatches = filter !== "ANNOUNCEMENT" || selectedAnnouncementKinds.length === 0 ||
                (announcementKind !== null && selectedAnnouncementKinds.includes(announcementKind));
            return typeMatches && locationMatches && distanceMatches && tagMatches && textMatches && announcementMatches;
        });
    }, [distance, feed, filter, locationQuery, query, selectedAnnouncementKinds, selectedTags]);

    const postsById = useMemo(() => new Map(feed.map((item) => [item.id, item])), [feed]);

    const visibleIncidents = useMemo(() => {
        const normLocation = locationQuery.trim().toLocaleLowerCase("tr-TR");
        return incidents.filter((incident) => {
            const kind = normalizeAnnouncementKind(incident.kind);
            const kindMatches = kind && (selectedAnnouncementKinds.length === 0 || selectedAnnouncementKinds.includes(kind));
            const locationMatches = !normLocation || incident.locationLabel.toLocaleLowerCase("tr-TR").includes(normLocation);
            const distanceMatches = distanceWithinRadius(incident.locationLabel, distance);
            return Boolean(kindMatches) && locationMatches && distanceMatches;
        });
    }, [distance, incidents, locationQuery, selectedAnnouncementKinds]);

    const renderedFeed = useMemo(() => {
        if (visibleFeed.length === 0) return [];
        return Array.from({ length: visibleCount }, (_, index) => {
            const item = visibleFeed[index % visibleFeed.length];
            return { item, renderKey: `${item.id}-${Math.floor(index / visibleFeed.length)}` };
        });
    }, [visibleCount, visibleFeed]);

    const activeFilterCount = [
        selectedTags.length > 0,
        Boolean(locationQuery.trim()),
        distance !== 8,
        filter === "ANNOUNCEMENT" && selectedAnnouncementKinds.length > 0,
    ].filter(Boolean).length;

    useEffect(() => {
        setVisibleCount(visibleFeed.length === 0 ? 0 : FEED_BATCH_SIZE);
    }, [visibleFeed]);

    useEffect(() => {
        const node = loadMoreRef.current;
        if (!node || visibleFeed.length === 0) return;
        const observer = new IntersectionObserver(
            (entries) => { if (entries.some((e) => e.isIntersecting)) setVisibleCount((c) => c + FEED_BATCH_SIZE); },
            { rootMargin: "600px 0px" },
        );
        observer.observe(node);
        return () => observer.disconnect();
    }, [visibleFeed.length]);

    useEffect(() => {
        function handleTouchStart(e: TouchEvent) {
            if (window.scrollY > 0 || refreshing) return;
            pullStartYRef.current = e.touches[0]?.clientY ?? null;
        }
        function handleTouchMove(e: TouchEvent) {
            if (pullStartYRef.current === null || window.scrollY > 0) return;
            const delta = (e.touches[0]?.clientY ?? 0) - pullStartYRef.current;
            setPullDistance(delta <= 0 ? 0 : Math.min(delta, 110));
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

    function openComposer(type: ComposerType, post: FeedItemView | null = null) {
        setComposerInitialType(type);
        setEditingPost(post);
        setComposerOpen(true);
    }

    function openPostFromMap(item: FeedItemView) {
        setFilter(isAnnouncementItem(item) ? "ANNOUNCEMENT" : item.type === "EVENT" ? "EVENT" : "ALL");
        setMapVisible(false);
        setFocusedPostId(item.id);
        window.setTimeout(() => {
            document.getElementById(`post-${item.id}`)?.scrollIntoView({ behavior: "smooth", block: "center" });
        }, 80);
    }

    return (
        <section className={styles.socialHome}>
            <main className={styles.homeFeedColumn}>
                <div
                    className={styles.pullRefresh}
                    style={{ height: pullDistance > 0 || refreshing ? Math.max(44, pullDistance) : 0 }}
                >
                    <span>
                        {refreshing ? "Akış yenileniyor..." : pullDistance >= 76 ? "Bırak ve yenile" : "Yenilemek için aşağı çek"}
                    </span>
                </div>

                <section className={styles.feedCommandSurface}>
                    <div className={styles.feedSearchRow}>
                        <label className={styles.searchBox}>
                            <Search size={17} aria-hidden />
                            <input
                                value={query}
                                onChange={(e) => setQuery(e.target.value)}
                                placeholder="Kişi, etiket veya paylaşım içeriği ara"
                            />
                        </label>
                        <button
                            type="button"
                            className={[styles.filterButton, filtersOpen && styles.filterButtonActive].filter(Boolean).join(" ")}
                            onClick={() => setFiltersOpen((v) => !v)}
                        >
                            <SlidersHorizontal size={17} aria-hidden />
                            Filtrele
                            {activeFilterCount > 0 && <span>{activeFilterCount}</span>}
                        </button>
                        <button
                            type="button"
                            className={[styles.mapToggleButton, mapVisible && styles.mapToggleButtonActive].filter(Boolean).join(" ")}
                            onClick={() => setMapVisible((v) => !v)}
                            aria-label="Haritayı göster/gizle"
                            title="Haritayı göster/gizle"
                        >
                            <MapIcon size={17} aria-hidden />
                        </button>
                        <button type="button" className={styles.shareButton} onClick={() => openComposer("POST")}>
                            <PenLine size={16} aria-hidden />
                            Paylaş
                        </button>
                    </div>
                    <FilterPanel
                        open={filtersOpen}
                        selectedFilter={filter}
                        tagDraft={tagDraft}
                        onTagDraftChange={setTagDraft}
                        selectedTags={selectedTags}
                        tagSuggestions={hashtags}
                        onAddTag={(tag) => {
                            const clean = cleanTagInput(tag);
                            if (clean) setSelectedTags((c) => c.includes(clean) ? c : [...c, clean]);
                            setTagDraft("");
                        }}
                        onRemoveTag={(tag) => setSelectedTags((c) => c.filter((t) => t !== tag))}
                        locationQuery={locationQuery}
                        onLocationChange={setLocationQuery}
                        distance={distance}
                        onDistanceChange={setDistance}
                        selectedAnnouncementKinds={selectedAnnouncementKinds}
                        onToggleAnnouncementKind={(kind) =>
                            setSelectedAnnouncementKinds((c) =>
                                c.includes(kind) ? c.filter((k) => k !== kind) : [...c, kind]
                            )
                        }
                        onClear={() => {
                            setQuery(""); setTagDraft(""); setSelectedTags([]);
                            setLocationQuery(""); setDistance(8); setSelectedAnnouncementKinds([]);
                        }}
                        onClose={() => setFiltersOpen(false)}
                    />
                </section>

                {notice && <p className={styles.statusBadge}>{notice}</p>}
                {status === "loading" && <div className={styles.empty}>Akış yükleniyor...</div>}
                {status === "error" && <div className={styles.empty}>Akış alınamadı.</div>}
                {status === "ready" && mapVisible && (
                    <AnnouncementMap
                        incidents={visibleIncidents}
                        postsById={postsById}
                        feedItems={visibleFeed}
                        activeFilter={filter}
                        selectedKinds={selectedAnnouncementKinds}
                        onOpenPost={openPostFromMap}
                    />
                )}
                {status === "ready" && (
                    <div className={styles.feedGrid}>
                        {renderedFeed.map(({ item, renderKey }) => (
                            <SocialPostCard
                                key={renderKey}
                                item={item}
                                currentUserName={session.currentUser.displayName}
                                focused={focusedPostId === item.id}
                                onEdit={(post) =>
                                    openComposer(
                                        isAnnouncementItem(post) ? "ANNOUNCEMENT" : post.type === "EVENT" ? "EVENT" : "POST",
                                        post,
                                    )
                                }
                                onPostChanged={replacePost}
                            />
                        ))}
                        {visibleFeed.length === 0 && <div className={styles.empty}>Bu aramaya uygun paylaşım yok.</div>}
                    </div>
                )}
                {status === "ready" && visibleFeed.length > 0 && (
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
                onClose={() => { setComposerOpen(false); setEditingPost(null); }}
                onSaved={upsertPost}
            />
            <FeedAdsRail ads={ads} />
        </section>
    );
}
