"use client";

import { useMemo, useState } from "react";
import {
    Bookmark,
    FilterX,
    Search,
    SlidersHorizontal,
} from "lucide-react";
import { categoryIconPath } from "@/components/product/ProductControls";
import { ExploreCard, type ExploreResult } from "@/components/explore/ExploreCard";
import { ExploreDetailPanel, type ExploreSelection } from "@/components/explore/ExploreDetailPanel";
import sharedStyles from "@/components/product/ProductPages.module.css";
import { mockAppApi, type SavedSearchView } from "@/lib/mockAppClient";
import { useExploreData } from "./useExploreData";

type ProviderTypeFilter = "ALL" | "INDIVIDUAL" | "BUSINESS";
type ResponseFilter = "ALL" | "FAST" | "SAME_DAY";
type SortMode = "RECOMMENDED" | "RATING" | "COMPLETED";

export default function ServicesPage() {
    const { categories, providers, businesses, savedSearches, setSavedSearches, loading } =
        useExploreData();

    const [query, setQuery] = useState("");
    const [providerType, setProviderType] = useState<ProviderTypeFilter>("ALL");
    const [categoryId, setCategoryId] = useState("ALL");
    const [locationScope, setLocationScope] = useState("");
    const [responseFilter, setResponseFilter] = useState<ResponseFilter>("ALL");
    const [minRating, setMinRating] = useState("0");
    const [nearbyOnly, setNearbyOnly] = useState(false);
    const [radiusKm, setRadiusKm] = useState("10");
    const [sortMode, setSortMode] = useState<SortMode>("RECOMMENDED");
    const [selected, setSelected] = useState<ExploreSelection | null>(null);

    const categoryById = useMemo(
        () => new Map(categories.map((c) => [c.id, c])),
        [categories],
    );

    const results = useMemo<ExploreResult[]>(() => {
        const normalized = (s: string) => s.trim().toLocaleLowerCase("tr-TR");
        const normQuery = normalized(query);
        const normLocation = normalized(locationScope);
        const minR = Number(minRating);

        const all: ExploreResult[] = [
            ...providers.map((item): ExploreResult => ({ kind: "provider", item })),
            ...businesses.map((item): ExploreResult => ({ kind: "business", item })),
        ];

        return all
            .filter(({ kind, item }) => {
                if (providerType === "INDIVIDUAL" && kind !== "provider") return false;
                if (providerType === "BUSINESS" && kind !== "business") return false;
                if (categoryId !== "ALL" && !item.categoryIds.includes(categoryId)) return false;
                if (item.rating < minR) return false;
                if (normLocation && !normalized(item.locationScope).includes(normLocation)) return false;
                if (nearbyOnly) {
                    const near =
                        kind === "provider"
                            ? (item as typeof providers[0]).servesNearby
                            : item.categoryIds.some((id) => id === "local-help" || id === "delivery");
                    if (!near) return false;
                }
                if (responseFilter === "FAST" && !normalized(item.responseTime).includes("dk"))
                    return false;
                if (responseFilter === "SAME_DAY" && !normalized(item.responseTime).includes("aynı gün"))
                    return false;
                if (normQuery) {
                    const name = kind === "provider"
                        ? (item as typeof providers[0]).displayName
                        : (item as typeof businesses[0]).tradeName;
                    const text = [name, item.summary, item.locationScope,
                        item.categoryIds.map((id) => categoryById.get(id)?.label ?? id).join(" ")]
                        .join(" ");
                    if (!normalized(text).includes(normQuery)) return false;
                }
                return true;
            })
            .sort((a, b) => {
                if (sortMode === "RATING") return b.item.rating - a.item.rating;
                if (sortMode === "COMPLETED") return b.item.completedJobs - a.item.completedJobs;
                const nearScore = (r: ExploreResult) =>
                    r.kind === "provider" ? (r.item.servesNearby ? 2 : 0)
                        : r.item.categoryIds.some((id) => id === "local-help" || id === "delivery") ? 1 : 0;
                return nearScore(b) - nearScore(a) || b.item.rating - a.item.rating;
            });
    }, [
        businesses,
        categoryById,
        categoryId,
        locationScope,
        minRating,
        nearbyOnly,
        providerType,
        providers,
        query,
        responseFilter,
        sortMode,
    ]);

    const searchSuggestions = useMemo(() => {
        const norm = (s: string) => s.trim().toLocaleLowerCase("tr-TR");
        const normQ = norm(query);
        if (!normQ) return [];
        return [
            ...categories.map((c) => c.label),
            ...providers.flatMap((p) => [p.displayName, p.headline, ...p.tags]),
            ...businesses.flatMap((b) => [b.tradeName, b.sector]),
        ]
            .filter((v, i, arr) => norm(v).includes(normQ) && arr.indexOf(v) === i)
            .slice(0, 8);
    }, [businesses, categories, providers, query]);

    function clearFilters() {
        setQuery("");
        setProviderType("ALL");
        setCategoryId("ALL");
        setLocationScope("");
        setResponseFilter("ALL");
        setMinRating("0");
        setNearbyOnly(false);
        setRadiusKm("10");
        setSortMode("RECOMMENDED");
    }

    async function saveSearch() {
        const saved = await mockAppApi.createSavedSearch({
            surface: "services",
            name: query.trim() ? `Keşfet: ${query.trim()}` : "Keşfet filtresi",
            filters: { query, providerType, categoryId, locationScope, responseFilter, minRating, radiusKm },
        });
        setSavedSearches((prev) => [saved, ...prev]);
    }

    function applySavedSearch(search: SavedSearchView) {
        setQuery(search.filters.query ?? "");
        setProviderType((search.filters.providerType as ProviderTypeFilter) ?? "ALL");
        setCategoryId(search.filters.categoryId ?? "ALL");
        setLocationScope(search.filters.locationScope ?? "");
        setResponseFilter((search.filters.responseFilter as ResponseFilter) ?? "ALL");
        setMinRating(search.filters.minRating ?? "0");
        setRadiusKm(search.filters.radiusKm ?? "10");
    }

    function selectResult(result: ExploreResult) {
        setSelected(
            result.kind === "provider"
                ? { kind: "provider", value: result.item }
                : { kind: "business", value: result.item },
        );
    }

    return (
        <section className={sharedStyles.marketplacePage}>
            {/* ── Filter sidebar ─────────────────────────────────── */}
            <aside className={sharedStyles.marketplaceFilters} aria-label="Keşfet filtreleri">
                <div className={sharedStyles.filterHeader}>
                    <div>
                        <h1>Keşfet</h1>
                        <p>Bireysel hizmet veren ve işletme profillerini keşfet.</p>
                    </div>
                    <SlidersHorizontal size={20} aria-hidden />
                </div>

                <label className={sharedStyles.searchBox}>
                    <Search size={17} aria-hidden />
                    <input
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                        placeholder="Keşfette ara"
                    />
                </label>
                {searchSuggestions.length > 0 && (
                    <div className={sharedStyles.suggestionBar}>
                        {searchSuggestions.map((s) => (
                            <button key={s} type="button" onClick={() => setQuery(s)}>
                                {s}
                            </button>
                        ))}
                    </div>
                )}

                <div className={sharedStyles.filterGroup}>
                    <span>Profil türü</span>
                    <div className={sharedStyles.segmented}>
                        {(["ALL", "INDIVIDUAL", "BUSINESS"] as const).map((t) => (
                            <button
                                key={t}
                                type="button"
                                className={providerType === t ? sharedStyles.segmentActive : ""}
                                onClick={() => setProviderType(t)}
                            >
                                {t === "ALL" ? "Tümü" : t === "INDIVIDUAL" ? "Bireysel" : "İşletme"}
                            </button>
                        ))}
                    </div>
                </div>

                <label className={sharedStyles.field}>
                    <span>Kategori</span>
                    <select value={categoryId} onChange={(e) => setCategoryId(e.target.value)}>
                        <option value="ALL">Tüm kategoriler</option>
                        {categories.map((c) => (
                            <option key={c.id} value={c.id}>{c.label}</option>
                        ))}
                    </select>
                </label>

                <label className={sharedStyles.field}>
                    <span>Konum kapsamı</span>
                    <input
                        value={locationScope}
                        onChange={(e) => setLocationScope(e.target.value)}
                        placeholder="Kadıköy, Moda, Üsküdar…"
                    />
                </label>

                <label className={sharedStyles.field}>
                    <span>Yanıt hızı</span>
                    <select
                        value={responseFilter}
                        onChange={(e) => setResponseFilter(e.target.value as ResponseFilter)}
                    >
                        <option value="ALL">Tümü</option>
                        <option value="FAST">Dakikalar içinde</option>
                        <option value="SAME_DAY">Aynı gün</option>
                    </select>
                </label>

                <label className={sharedStyles.field}>
                    <span>Minimum puan</span>
                    <select value={minRating} onChange={(e) => setMinRating(e.target.value)}>
                        <option value="0">Tüm puanlar</option>
                        <option value="4.5">4.5+</option>
                        <option value="4.7">4.7+</option>
                        <option value="4.8">4.8+</option>
                    </select>
                </label>

                <label className={sharedStyles.field}>
                    <span>Mesafe</span>
                    <select value={radiusKm} onChange={(e) => setRadiusKm(e.target.value)}>
                        <option value="3">3 km içinde</option>
                        <option value="5">5 km içinde</option>
                        <option value="10">10 km içinde</option>
                        <option value="25">25 km içinde</option>
                    </select>
                </label>

                <label className={sharedStyles.toggleLine}>
                    <input
                        type="checkbox"
                        checked={nearbyOnly}
                        onChange={(e) => setNearbyOnly(e.target.checked)}
                    />
                    <span>Mesafe ve konum uyumu yüksek profiller</span>
                </label>

                <button type="button" className={sharedStyles.clearFilters} onClick={saveSearch}>
                    <Bookmark size={16} aria-hidden />
                    Aramayı kaydet
                </button>

                <button type="button" className={sharedStyles.clearFilters} onClick={clearFilters}>
                    <FilterX size={16} aria-hidden />
                    Filtreleri temizle
                </button>

                {savedSearches.length > 0 && (
                    <div className={sharedStyles.filterGroup}>
                        <span>Kayıtlı aramalar</span>
                        <div className={sharedStyles.savedSearchList}>
                            {savedSearches.map((s) => (
                                <button key={s.id} type="button" onClick={() => applySavedSearch(s)}>
                                    <strong>{s.name}</strong>
                                    <span>{s.filters.categoryId || s.filters.query || "Filtre"}</span>
                                </button>
                            ))}
                        </div>
                    </div>
                )}
            </aside>

            {/* ── Main content ───────────────────────────────────── */}
            <main className={sharedStyles.marketplaceContent}>
                <div className={sharedStyles.marketplaceToolbar}>
                    <div>
                        <p className={sharedStyles.eyebrow}>Keşif alanı</p>
                        <h2>Yakındaki hizmet profilleri</h2>
                        <span>
                            {loading ? "Yükleniyor…" : `${results.length} profil bulundu`}
                        </span>
                    </div>
                    <label className={sharedStyles.sortSelect}>
                        <span>Sıralama</span>
                        <select
                            value={sortMode}
                            onChange={(e) => setSortMode(e.target.value as SortMode)}
                        >
                            <option value="RECOMMENDED">Önerilen</option>
                            <option value="RATING">Puana göre</option>
                            <option value="COMPLETED">Tamamlanan işe göre</option>
                        </select>
                    </label>
                </div>

                {/* Category quick-filter */}
                <section
                    className={sharedStyles.categoryScroller}
                    aria-label="Hızlı kategori filtreleri"
                >
                    <button
                        type="button"
                        className={categoryId === "ALL" ? sharedStyles.categoryChipActive : ""}
                        onClick={() => setCategoryId("ALL")}
                    >
                        Tümü
                    </button>
                    {categories.map((c) => (
                        <button
                            key={c.id}
                            type="button"
                            className={categoryId === c.id ? sharedStyles.categoryChipActive : ""}
                            onClick={() => setCategoryId(c.id)}
                        >
                            <img src={categoryIconPath(c.icon)} alt="" />
                            {c.label}
                        </button>
                    ))}
                </section>

                {/* Results + detail */}
                <section className={sharedStyles.serviceResultsLayout}>
                    <div className={sharedStyles.serviceGrid}>
                        {loading &&
                            Array.from({ length: 6 }).map((_, i) => (
                                <div
                                    key={i}
                                    className={
                                        // inline import not possible across modules — use inline style
                                        undefined
                                    }
                                    style={{
                                        height: 280,
                                        borderRadius: "var(--vv-radius-md)",
                                        background: "var(--vv-surface-raised)",
                                        border: "1px solid var(--vv-border-subtle)",
                                        animation: "pulse 1.4s infinite",
                                    }}
                                />
                            ))}
                        {!loading &&
                            results.map((result) => (
                                <ExploreCard
                                    key={`${result.kind}-${result.item.id}`}
                                    result={result}
                                    categoryById={categoryById}
                                    selected={
                                        selected !== null &&
                                        selected.value.id === result.item.id
                                    }
                                    onSelect={() => selectResult(result)}
                                    onRequest={() => selectResult(result)}
                                />
                            ))}
                        {!loading && results.length === 0 && (
                            <div className={sharedStyles.empty}>
                                Bu filtrelere uygun hizmet profili bulunamadı.
                            </div>
                        )}
                    </div>

                    <aside className={sharedStyles.serviceDetailPanel}>
                        {selected ? (
                            <ExploreDetailPanel
                                selection={selected}
                                categoryById={categoryById}
                            />
                        ) : (
                            <div className={sharedStyles.empty}>
                                Detay görmek için bir profil seç.
                            </div>
                        )}
                    </aside>
                </section>
            </main>
        </section>
    );
}
