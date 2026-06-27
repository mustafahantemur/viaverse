"use client";

import { useCallback, useMemo, useState } from "react";
import { FilterX, Search, SearchX, SlidersHorizontal } from "lucide-react";
import { ExploreCard, type ExploreResult } from "@/components/explore/ExploreCard";
import { ExploreDetailModal, type ExploreSelection } from "@/components/explore/ExploreDetailModal";
import { ExploreFilterPanel, type DynamicCategory } from "@/components/explore/ExploreFilterPanel";
import {
    type ProviderTypeFilter,
    type ResponseFilter,
} from "@/components/explore/ExploreFilterBar";
import { FeedAdsRail } from "@/components/product/feed/FeedAdsRail";
import { useSidebarSlot } from "@/components/product/ProductAppShell";
import type { ServiceCategoryView } from "@/lib/mockAppClient";
import { DYNAMIC_CATEGORY_LABELS } from "./exploreStaticData";
import styles from "@/components/explore/explore.module.css";
import sharedStyles from "@/components/product/ProductPages.module.css";
import { useExploreData } from "./useExploreData";

type SortMode = "RECOMMENDED" | "RATING" | "COMPLETED";

function prettifyId(id: string): string {
    return id.split(/[-_]/).map((w) => w.charAt(0).toUpperCase() + w.slice(1)).join(" ");
}

// Parse a free-text response label ("Genelde 12 dk içinde", "aynı gün", "randevulu") to minutes.
function responseMinutes(rt: string): number {
    const s = rt.toLocaleLowerCase("tr-TR");
    const dk = s.match(/(\d+)\s*dk/);
    const saat = s.match(/(\d+)\s*saat/);
    const gun = s.match(/(\d+)\s*gün/);
    if (dk) return parseInt(dk[1], 10);
    if (saat) return parseInt(saat[1], 10) * 60;
    if (gun) return parseInt(gun[1], 10) * 1440;
    if (s.includes("aynı gün") || s.includes("bugün")) return 480;
    if (s.includes("randevu")) return 2880;
    if (s.includes("hafta")) return 10080;
    return 1440;
}

function matchesResponse(rf: ResponseFilter, mins: number): boolean {
    switch (rf) {
        case "RECENT": return mins <= 1440;   // yakın zamanda aktif (aynı gün içi)
        case "FLEXIBLE": return mins > 1440;  // esnek / randevulu / çok günlük
        default: return true;
    }
}

export default function ExplorePage() {
    const { categories, providers, businesses, ads, loading } = useExploreData();

    const [query, setQuery] = useState("");
    const [providerType, setProviderType] = useState<ProviderTypeFilter>("ALL");
    const [categoryId, setCategoryId] = useState("ALL");
    const [subCategoryFilter, setSubCategoryFilter] = useState<string | null>(null);
    const [responseFilter, setResponseFilter] = useState<ResponseFilter>("ALL");
    const [minRating, setMinRating] = useState("0");
    const [radiusKm, setRadiusKm] = useState("100");
    const [priceMin, setPriceMin] = useState("");
    const [priceMax, setPriceMax] = useState("");
    const [sortMode, setSortMode] = useState<SortMode>("RECOMMENDED");
    const [selected, setSelected] = useState<ExploreSelection | null>(null);
    const [mobileFilterOpen, setMobileFilterOpen] = useState(false);

    // Categories present on listings but missing from the predefined tree → "Diğer Hizmetler".
    const dynamicCategories = useMemo<DynamicCategory[]>(() => {
        const known = new Set(categories.map((c) => c.id));
        const found = new Map<string, string>();
        [...providers, ...businesses].forEach((item) => {
            item.categoryIds.forEach((id) => {
                if (!known.has(id) && !found.has(id)) {
                    found.set(id, DYNAMIC_CATEGORY_LABELS[id] ?? prettifyId(id));
                }
            });
        });
        return [...found.entries()].map(([id, label]) => ({ id, label }));
    }, [categories, providers, businesses]);

    // Map for resolving category labels on cards/modal (real + dynamic).
    const categoryById = useMemo(() => {
        const m = new Map<string, ServiceCategoryView>(categories.map((c) => [c.id, c]));
        dynamicCategories.forEach((dc) => {
            if (!m.has(dc.id)) {
                m.set(dc.id, {
                    id: dc.id, label: dc.label, description: "", lane: "Topluluk",
                    icon: "", preferredProviderType: "", subCategories: [],
                });
            }
        });
        return m;
    }, [categories, dynamicCategories]);

    const results = useMemo<ExploreResult[]>(() => {
        const norm = (s: string) => s.trim().toLocaleLowerCase("tr-TR");
        const normQuery = norm(query);
        const minR = Number(minRating);
        const maxDist = Number(radiusKm);

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
                if (maxDist < 100 && item.distanceKm > maxDist) return false;
                if (priceMin || priceMax) {
                    const priceStr = item.priceRange?.replace(/[^\d]/g, "").slice(0, 6) ?? "0";
                    const price = parseFloat(priceStr);
                    if (price > 0) {
                        if (priceMin && price < Number(priceMin)) return false;
                        if (priceMax && price > Number(priceMax)) return false;
                    }
                }
                if (responseFilter !== "ALL" && !matchesResponse(responseFilter, responseMinutes(item.responseTime))) {
                    return false;
                }
                if (subCategoryFilter) {
                    const headline = kind === "provider" ? item.headline : "";
                    const tags = kind === "provider" ? item.tags : [];
                    const haystack = norm([item.summary, headline, ...tags].join(" "));
                    const firstWord = norm(subCategoryFilter).split(" ")[0];
                    if (firstWord && !haystack.includes(firstWord)) return false;
                }
                if (normQuery) {
                    const name = kind === "provider" ? item.displayName : item.tradeName;
                    const tags = kind === "provider" ? item.tags : [];
                    const text = [name, item.summary, item.locationScope,
                        item.categoryIds.map((id) => categoryById.get(id)?.label ?? id).join(" "),
                        ...tags].join(" ");
                    if (!norm(text).includes(normQuery)) return false;
                }
                return true;
            })
            .sort((a, b) => {
                if (sortMode === "RATING") return b.item.rating - a.item.rating;
                if (sortMode === "COMPLETED") return b.item.completedJobs - a.item.completedJobs;
                const nearScore = (r: ExploreResult) =>
                    r.kind === "provider" ? (r.item.servesNearby ? 2 : 0)
                        : r.item.categoryIds.some((id) => id === "delivery" || id === "pets") ? 1 : 0;
                return nearScore(b) - nearScore(a) || b.item.rating - a.item.rating;
            });
    }, [businesses, categoryById, categoryId, minRating, providerType, providers, priceMin, priceMax, query, radiusKm, responseFilter, sortMode, subCategoryFilter]);

    const clearFilters = useCallback(() => {
        setQuery("");
        setProviderType("ALL");
        setCategoryId("ALL");
        setSubCategoryFilter(null);
        setResponseFilter("ALL");
        setMinRating("0");
        setRadiusKm("100");
        setPriceMin("");
        setPriceMax("");
        setSortMode("RECOMMENDED");
    }, []);

    const activeFilterCount = [
        categoryId !== "ALL",
        subCategoryFilter !== null,
        providerType !== "ALL",
        responseFilter !== "ALL",
        Number(minRating) > 0,
        Number(radiusKm) < 100,
        Boolean(priceMin) || Boolean(priceMax),
    ].filter(Boolean).length;

    function openDetail(result: ExploreResult) {
        setSelected(result.kind === "provider"
            ? { kind: "provider", value: result.item }
            : { kind: "business", value: result.item });
    }

    // The marketplace filters live inside the shared shell sidebar (same as the feed).
    const filterPanel = useMemo(() => (
        <ExploreFilterPanel
            categories={categories}
            dynamicCategories={dynamicCategories}
            selectedCategory={categoryId}
            selectedSubcat={subCategoryFilter}
            providerType={providerType}
            minRating={minRating}
            radiusKm={radiusKm}
            responseFilter={responseFilter}
            priceMin={priceMin}
            priceMax={priceMax}
            totalResults={results.length}
            onCategoryChange={setCategoryId}
            onSubcatChange={setSubCategoryFilter}
            onProviderTypeChange={setProviderType}
            onMinRatingChange={setMinRating}
            onRadiusKmChange={setRadiusKm}
            onResponseFilterChange={setResponseFilter}
            onPriceMinChange={setPriceMin}
            onPriceMaxChange={setPriceMax}
            onClear={clearFilters}
        />
    ), [categories, dynamicCategories, categoryId, subCategoryFilter, providerType, minRating, radiusKm, responseFilter, priceMin, priceMax, results.length, clearFilters]);

    useSidebarSlot(filterPanel);

    const activeCategoryLabel = categoryId !== "ALL"
        ? (categoryById.get(categoryId)?.label ?? prettifyId(categoryId))
        : null;

    return (
        <section className={sharedStyles.socialHome}>
            <main className={sharedStyles.homeFeedColumn}>
                {/* Command surface: search + sort + mobile filter toggle */}
                <div className={styles.exploreMainHeader}>
                    <div className={styles.exploreMainTitle}>
                        <p>Hizmetler</p>
                        <h1>Yakındaki Hizmet Profilleri</h1>
                        <span>{loading ? "Yükleniyor…" : `${results.length} profil bulundu`}</span>
                    </div>
                    <div className={styles.exploreMainControls}>
                        <label className={sharedStyles.searchBox}>
                            <Search size={15} aria-hidden />
                            <input
                                value={query}
                                onChange={(e) => setQuery(e.target.value)}
                                placeholder="İsim, hizmet veya konum ara…"
                            />
                        </label>
                        <label className={styles.exploreSortSelect}>
                            <select value={sortMode} onChange={(e) => setSortMode(e.target.value as SortMode)}>
                                <option value="RECOMMENDED">Önerilen</option>
                                <option value="RATING">Puana göre</option>
                                <option value="COMPLETED">Tamamlanan işe göre</option>
                            </select>
                        </label>
                        <button
                            type="button"
                            className={[styles.mobileFilterBtn, mobileFilterOpen && styles.mobileFilterBtnActive].filter(Boolean).join(" ")}
                            onClick={() => setMobileFilterOpen((v) => !v)}
                            aria-label="Filtreleri aç"
                        >
                            <SlidersHorizontal size={15} aria-hidden />
                            Filtrele
                            {activeFilterCount > 0 && (
                                <span className={styles.exploreFilterBadge}>{activeFilterCount}</span>
                            )}
                        </button>
                    </div>
                </div>

                {/* Mobile filter drawer (shell sidebar is hidden on small screens) */}
                {mobileFilterOpen && (
                    <div className={styles.mobileFilterDrawer}>
                        {filterPanel}
                    </div>
                )}

                {/* Selected-filter chips — kept on a single line each */}
                {(activeCategoryLabel || subCategoryFilter || providerType !== "ALL" || responseFilter !== "ALL") && (
                    <div className={styles.exploreActiveFiltersRow}>
                        {activeCategoryLabel && (
                            <button type="button" className={styles.exploreActiveFilterChip}
                                onClick={() => { setCategoryId("ALL"); setSubCategoryFilter(null); }}>
                                <span className={styles.exploreActiveFilterText}>{activeCategoryLabel}</span>
                                <span className={styles.exploreActiveFilterX}>×</span>
                            </button>
                        )}
                        {subCategoryFilter && (
                            <button type="button" className={[styles.exploreActiveFilterChip, styles.exploreActiveFilterChipSub].filter(Boolean).join(" ")}
                                onClick={() => setSubCategoryFilter(null)}>
                                <span className={styles.exploreActiveFilterText}>{subCategoryFilter}</span>
                                <span className={styles.exploreActiveFilterX}>×</span>
                            </button>
                        )}
                        {providerType !== "ALL" && (
                            <button type="button" className={styles.exploreActiveFilterChip}
                                onClick={() => setProviderType("ALL")}>
                                <span className={styles.exploreActiveFilterText}>
                                    {providerType === "INDIVIDUAL" ? "Serbest" : "İşletme"}
                                </span>
                                <span className={styles.exploreActiveFilterX}>×</span>
                            </button>
                        )}
                        {responseFilter !== "ALL" && (
                            <button type="button" className={styles.exploreActiveFilterChip}
                                onClick={() => setResponseFilter("ALL")}>
                                <span className={styles.exploreActiveFilterText}>
                                    {responseFilter === "RECENT" ? "Yakın zamanda aktif" : "Esnek zamanlı"}
                                </span>
                                <span className={styles.exploreActiveFilterX}>×</span>
                            </button>
                        )}
                    </div>
                )}

                {/* Results grid */}
                <div className={styles.exploreResultsGrid}>
                    {loading && Array.from({ length: 8 }).map((_, i) => (
                        <div key={i} className={styles.exploreSkeleton} aria-hidden />
                    ))}
                    {!loading && results.map((result) => (
                        <ExploreCard
                            key={`${result.kind}-${result.item.id}`}
                            result={result}
                            categoryById={categoryById}
                            selected={selected !== null && selected.value.id === result.item.id}
                            onSelect={() => openDetail(result)}
                            onRequest={() => openDetail(result)}
                        />
                    ))}
                    {!loading && results.length === 0 && (
                        <div className={styles.exploreEmpty}>
                            <SearchX size={28} aria-hidden />
                            <p>Bu filtrelere uygun hizmet profili bulunamadı.</p>
                            <button type="button" onClick={clearFilters}>
                                <FilterX size={15} aria-hidden />
                                Filtreleri temizle
                            </button>
                        </div>
                    )}
                </div>
            </main>

            {/* Right rail — shared with the feed */}
            <FeedAdsRail ads={ads} />

            {/* Detail modal */}
            <ExploreDetailModal
                selection={selected}
                categoryById={categoryById}
                onClose={() => setSelected(null)}
                onRequest={() => setSelected(null)}
            />
        </section>
    );
}
