"use client";

import { useEffect, useMemo, useState } from "react";
import {
    BadgeCheck,
    Bookmark,
    Building2,
    ChevronRight,
    Clock3,
    FilterX,
    MapPin,
    Search,
    SlidersHorizontal,
    Sparkles,
    Star,
    UserRoundCheck,
} from "lucide-react";
import { Button } from "@/components/primitives/Button";
import { categoryIconPath } from "@/components/product/ProductControls";
import styles from "@/components/product/ProductPages.module.css";
import {
    mockAppApi,
    type BusinessView,
    type ProviderView,
    type SavedSearchView,
    type ServiceCategoryView,
} from "@/lib/mockAppClient";

type Selection =
    | { kind: "provider"; value: ProviderView }
    | { kind: "business"; value: BusinessView };

type ProviderTypeFilter = "ALL" | "INDIVIDUAL" | "BUSINESS";
type ResponseFilter = "ALL" | "FAST" | "SAME_DAY";
type SortMode = "RECOMMENDED" | "RATING" | "COMPLETED";

type ServiceResult =
    | { kind: "provider"; item: ProviderView }
    | { kind: "business"; item: BusinessView };

export default function ServicesPage() {
    const [categories, setCategories] = useState<ServiceCategoryView[]>([]);
    const [providers, setProviders] = useState<ProviderView[]>([]);
    const [businesses, setBusinesses] = useState<BusinessView[]>([]);
    const [query, setQuery] = useState("");
    const [providerType, setProviderType] = useState<ProviderTypeFilter>("ALL");
    const [categoryId, setCategoryId] = useState("ALL");
    const [locationScope, setLocationScope] = useState("");
    const [responseFilter, setResponseFilter] = useState<ResponseFilter>("ALL");
    const [minRating, setMinRating] = useState("0");
    const [nearbyOnly, setNearbyOnly] = useState(false);
    const [radiusKm, setRadiusKm] = useState("10");
    const [sortMode, setSortMode] = useState<SortMode>("RECOMMENDED");
    const [savedSearches, setSavedSearches] = useState<SavedSearchView[]>([]);
    const [selected, setSelected] = useState<Selection | null>(null);

    useEffect(() => {
        async function load() {
            const [nextCategories, nextProviders, nextBusinesses, nextSaved] = await Promise.all([
                mockAppApi.categories(),
                mockAppApi.providers(),
                mockAppApi.businesses(),
                mockAppApi.savedSearches("services"),
            ]);
            setCategories(nextCategories);
            setProviders(nextProviders);
            setBusinesses(nextBusinesses);
            setSavedSearches(nextSaved);
            setSelected({ kind: "provider", value: nextProviders[0] });
        }
        load();
    }, []);

    const categoryById = useMemo(
        () => new Map(categories.map((category) => [category.id, category])),
        [categories],
    );

    const results = useMemo(() => {
        const normalizedQuery = normalize(query);
        const normalizedLocation = normalize(locationScope);
        const minimumRating = Number(minRating);
        const providerResults: ServiceResult[] = providers.map((item) => ({ kind: "provider", item }));
        const businessResults: ServiceResult[] = businesses.map((item) => ({ kind: "business", item }));

        return [...providerResults, ...businessResults]
            .filter((result) => {
                const item = result.item;
                const typeMatches =
                    providerType === "ALL" ||
                    (providerType === "INDIVIDUAL" && result.kind === "provider") ||
                    (providerType === "BUSINESS" && result.kind === "business");
                const categoryMatches = categoryId === "ALL" || item.categoryIds.includes(categoryId);
                const ratingMatches = item.rating >= minimumRating;
                const locationMatches = !normalizedLocation || normalize(item.locationScope).includes(normalizedLocation);
                const nearbyMatches =
                    !nearbyOnly ||
                    (result.kind === "provider"
                        ? result.item.servesNearby
                        : result.item.categoryIds.some((id) => id === "local-help" || id === "delivery"));
                const responseMatches =
                    responseFilter === "ALL" ||
                    (responseFilter === "FAST" && normalize(item.responseTime).includes("dk")) ||
                    (responseFilter === "SAME_DAY" && normalize(item.responseTime).includes("aynı gün"));
                const textMatches =
                    !normalizedQuery ||
                    normalize(searchText(result, categoryById)).includes(normalizedQuery);

                return (
                    typeMatches &&
                    categoryMatches &&
                    ratingMatches &&
                    locationMatches &&
                    nearbyMatches &&
                    responseMatches &&
                    textMatches
                );
            })
            .sort((left, right) => {
                if (sortMode === "RATING") return right.item.rating - left.item.rating;
                if (sortMode === "COMPLETED") return right.item.completedJobs - left.item.completedJobs;
                const rightNearby = resultNearbyScore(right);
                const leftNearby = resultNearbyScore(left);
                return rightNearby - leftNearby || right.item.rating - left.item.rating;
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
            name: query.trim() ? `Hizmet: ${query.trim()}` : "Hizmet filtresi",
            filters: {
                query,
                providerType,
                categoryId,
                locationScope,
                responseFilter,
                minRating,
                radiusKm,
            },
        });
        setSavedSearches((current) => [saved, ...current]);
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

    const searchSuggestions = useMemo(() => {
        const normalized = normalize(query);
        if (!normalized) return [];
        return [
            ...categories.map((category) => category.label),
            ...providers.flatMap((provider) => [provider.displayName, provider.headline, ...provider.tags]),
            ...businesses.flatMap((business) => [business.tradeName, business.sector]),
        ]
            .filter((value, index, all) => normalize(value).includes(normalized) && all.indexOf(value) === index)
            .slice(0, 8);
    }, [businesses, categories, providers, query]);

    return (
        <section className={styles.marketplacePage}>
            <aside className={styles.marketplaceFilters} aria-label="Hizmet filtreleri">
                <div className={styles.filterHeader}>
                    <div>
                        <h1>Hizmetler</h1>
                        <p>Bireysel hizmet veren ve işletme profillerini keşfet.</p>
                    </div>
                    <SlidersHorizontal size={20} aria-hidden />
                </div>

                <label className={styles.searchBox}>
                    <Search size={17} aria-hidden />
                    <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Hizmetlerde ara" />
                </label>
                {searchSuggestions.length > 0 && (
                    <div className={styles.suggestionBar}>
                        {searchSuggestions.map((item) => (
                            <button key={item} type="button" onClick={() => setQuery(item)}>
                                {item}
                            </button>
                        ))}
                    </div>
                )}

                <div className={styles.filterGroup}>
                    <span>Profil türü</span>
                    <div className={styles.segmented}>
                        <button type="button" className={providerType === "ALL" ? styles.segmentActive : ""} onClick={() => setProviderType("ALL")}>
                            Tümü
                        </button>
                        <button type="button" className={providerType === "INDIVIDUAL" ? styles.segmentActive : ""} onClick={() => setProviderType("INDIVIDUAL")}>
                            Bireysel
                        </button>
                        <button type="button" className={providerType === "BUSINESS" ? styles.segmentActive : ""} onClick={() => setProviderType("BUSINESS")}>
                            İşletme
                        </button>
                    </div>
                </div>

                <label className={styles.field}>
                    <span>Kategori</span>
                    <select value={categoryId} onChange={(event) => setCategoryId(event.target.value)}>
                        <option value="ALL">Tüm kategoriler</option>
                        {categories.map((category) => (
                            <option key={category.id} value={category.id}>
                                {category.label}
                            </option>
                        ))}
                    </select>
                </label>

                <label className={styles.field}>
                    <span>Konum kapsamı</span>
                    <input
                        value={locationScope}
                        onChange={(event) => setLocationScope(event.target.value)}
                        placeholder="Kadıköy, Moda, Üsküdar..."
                    />
                </label>

                <label className={styles.field}>
                    <span>Yanıt hızı</span>
                    <select value={responseFilter} onChange={(event) => setResponseFilter(event.target.value as ResponseFilter)}>
                        <option value="ALL">Tümü</option>
                        <option value="FAST">Dakikalar içinde</option>
                        <option value="SAME_DAY">Aynı gün</option>
                    </select>
                </label>

                <label className={styles.field}>
                    <span>Minimum puan</span>
                    <select value={minRating} onChange={(event) => setMinRating(event.target.value)}>
                        <option value="0">Tüm puanlar</option>
                        <option value="4.5">4.5+</option>
                        <option value="4.7">4.7+</option>
                        <option value="4.8">4.8+</option>
                    </select>
                </label>

                <label className={styles.field}>
                    <span>Mesafe</span>
                    <select value={radiusKm} onChange={(event) => setRadiusKm(event.target.value)}>
                        <option value="3">3 km içinde</option>
                        <option value="5">5 km içinde</option>
                        <option value="10">10 km içinde</option>
                        <option value="25">25 km içinde</option>
                    </select>
                </label>

                <label className={styles.toggleLine}>
                    <input type="checkbox" checked={nearbyOnly} onChange={(event) => setNearbyOnly(event.target.checked)} />
                    <span>Mesafe ve konum uyumu yüksek profiller</span>
                </label>

                <button type="button" className={styles.clearFilters} onClick={saveSearch}>
                    <Bookmark size={16} aria-hidden />
                    Aramayı kaydet
                </button>

                <button type="button" className={styles.clearFilters} onClick={clearFilters}>
                    <FilterX size={16} aria-hidden />
                    Filtreleri temizle
                </button>

                {savedSearches.length > 0 && (
                    <div className={styles.filterGroup}>
                        <span>Kayıtlı aramalar</span>
                        <div className={styles.savedSearchList}>
                            {savedSearches.map((search) => (
                                <button key={search.id} type="button" onClick={() => applySavedSearch(search)}>
                                    <strong>{search.name}</strong>
                                    <span>{search.filters.categoryId || search.filters.query || "Filtre"}</span>
                                </button>
                            ))}
                        </div>
                    </div>
                )}
            </aside>

            <main className={styles.marketplaceContent}>
                <div className={styles.marketplaceToolbar}>
                    <div>
                        <p className={styles.eyebrow}>Keşif alanı</p>
                        <h2>Yakındaki hizmet profilleri</h2>
                        <span>{results.length} sonuç · Bireysel hizmet veren ve işletme ayrımı korunur</span>
                    </div>
                    <label className={styles.sortSelect}>
                        <span>Sıralama</span>
                        <select value={sortMode} onChange={(event) => setSortMode(event.target.value as SortMode)}>
                            <option value="RECOMMENDED">Önerilen</option>
                            <option value="RATING">Puana göre</option>
                            <option value="COMPLETED">Tamamlanan işe göre</option>
                        </select>
                    </label>
                </div>

                <section className={styles.categoryScroller} aria-label="Hızlı kategori filtreleri">
                    <button type="button" className={categoryId === "ALL" ? styles.categoryChipActive : ""} onClick={() => setCategoryId("ALL")}>
                        Tümü
                    </button>
                    {categories.map((category) => (
                        <button
                            key={category.id}
                            type="button"
                            className={categoryId === category.id ? styles.categoryChipActive : ""}
                            onClick={() => setCategoryId(category.id)}
                        >
                            <img src={categoryIconPath(category.icon)} alt="" />
                            {category.label}
                        </button>
                    ))}
                </section>

                <section className={styles.serviceResultsLayout}>
                    <div className={styles.serviceGrid}>
                        {results.map((result) => (
                            <ServiceTile
                                key={`${result.kind}-${result.item.id}`}
                                result={result}
                                categoryById={categoryById}
                                onSelect={() =>
                                    setSelected(
                                        result.kind === "provider"
                                            ? { kind: "provider", value: result.item }
                                            : { kind: "business", value: result.item },
                                    )
                                }
                            />
                        ))}
                        {results.length === 0 && <div className={styles.empty}>Bu filtrelere uygun hizmet profili bulunamadı.</div>}
                    </div>

                    <aside className={styles.serviceDetailPanel}>
                        {selected ? (
                            <DetailPanel selection={selected} categoryById={categoryById} />
                        ) : (
                            <div className={styles.empty}>Detay görmek için bir profil seç.</div>
                        )}
                    </aside>
                </section>
            </main>
        </section>
    );
}

function ServiceTile({
    result,
    categoryById,
    onSelect,
}: {
    result: ServiceResult;
    categoryById: Map<string, ServiceCategoryView>;
    onSelect: () => void;
}) {
    const item = result.item;
    const title = result.kind === "provider" ? result.item.displayName : result.item.tradeName;
    const subtitle = result.kind === "provider" ? result.item.headline : result.item.sector;
    const imageIcon = categoryById.get(item.categoryIds[0])?.icon ?? "local_help.png";
    const nearby = result.kind === "provider" ? result.item.servesNearby : item.categoryIds.includes("local-help");

    return (
        <article className={styles.serviceTile}>
            <button type="button" className={styles.serviceTileBody} onClick={onSelect}>
                <div className={styles.serviceTileMedia}>
                    <img src={categoryIconPath(imageIcon)} alt="" />
                    <span>{item.providerType}</span>
                </div>
                <div className={styles.serviceTileContent}>
                    <h3>{title}</h3>
                    <p>{subtitle}</p>
                    <div className={styles.serviceMeta}>
                        <span><Star size={14} /> {item.rating}</span>
                        <span><Clock3 size={14} /> {item.responseTime}</span>
                    </div>
                    <div className={styles.serviceLocation}>
                        <MapPin size={14} aria-hidden />
                        {item.locationScope}
                    </div>
                    <div className={styles.hashtagRow}>
                        {item.categoryIds.slice(0, 3).map((id) => (
                            <span key={id}>{categoryById.get(id)?.label ?? id}</span>
                        ))}
                    </div>
                </div>
            </button>
            <footer className={styles.serviceTileFooter}>
                <span>{item.completedJobs} tamamlanan iş</span>
                {nearby && <strong>Yakın mesafe</strong>}
            </footer>
        </article>
    );
}

function DetailPanel({
    selection,
    categoryById,
}: {
    selection: Selection;
    categoryById: Map<string, ServiceCategoryView>;
}) {
    const item = selection.value;
    const title = selection.kind === "provider" ? selection.value.displayName : selection.value.tradeName;
    const subtitle = selection.kind === "provider" ? selection.value.headline : selection.value.sector;
    const tags = selection.kind === "provider" ? selection.value.tags : [selection.value.verificationStatus, selection.value.sector];

    return (
        <>
            <div className={styles.detailHero}>
                <span>{selection.kind === "provider" ? <UserRoundCheck size={24} /> : <Building2 size={24} />}</span>
                <div>
                    <h2>{title}</h2>
                    <p>{subtitle}</p>
                </div>
                <strong>{item.providerType}</strong>
            </div>
            <p className={styles.detailText}>{item.summary}</p>
            <div className={styles.metricRow}>
                <div className={styles.metric}>
                    <strong><Star size={16} /> {item.rating}</strong>
                    <span>Puan</span>
                </div>
                <div className={styles.metric}>
                    <strong>{item.completedJobs}</strong>
                    <span>Tamamlanan iş</span>
                </div>
                <div className={styles.metric}>
                    <strong>{item.responseTime}</strong>
                    <span>Yanıt</span>
                </div>
            </div>
            <div className={styles.badgeRow}>
                <span className={styles.softBadge}><MapPin size={13} /> {item.locationScope}</span>
                {selection.kind === "business" && (
                    <span className={styles.statusBadge}><BadgeCheck size={13} /> {selection.value.verificationStatus}</span>
                )}
                {selection.kind === "provider" && selection.value.servesNearby && (
                    <span className={styles.statusBadge}><Sparkles size={13} /> Yakın mesafeye uygun</span>
                )}
            </div>
            <div className={styles.detailTags}>
                {tags.map((tag) => (
                    <span key={tag}>#{tag.replace(/\s+/g, "")}</span>
                ))}
            </div>
            <div className={styles.detailCategories}>
                {item.categoryIds.map((id) => {
                    const category = categoryById.get(id);
                    return (
                        <div key={id}>
                            {category && <img src={categoryIconPath(category.icon)} alt="" />}
                            <strong>{category?.label ?? id}</strong>
                            <span>{category?.description}</span>
                        </div>
                    );
                })}
            </div>
            <div className={styles.actions}>
                <Button>Talep başlat</Button>
                <Button variant="outline" leadingIcon={<ChevronRight size={16} />}>Profili aç</Button>
            </div>
        </>
    );
}

function normalize(value: string): string {
    return value.trim().toLocaleLowerCase("tr-TR");
}

function searchText(result: ServiceResult, categoryById: Map<string, ServiceCategoryView>): string {
    const item = result.item;
    const categoryText = item.categoryIds.map((id) => categoryById.get(id)?.label ?? id).join(" ");
    if (result.kind === "provider") {
        return [
            result.item.displayName,
            result.item.providerType,
            result.item.headline,
            result.item.summary,
            result.item.locationScope,
            result.item.responseTime,
            result.item.tags.join(" "),
            categoryText,
        ].join(" ");
    }
    return [
        result.item.tradeName,
        result.item.providerType,
        result.item.sector,
        result.item.summary,
        result.item.locationScope,
        result.item.responseTime,
        result.item.verificationStatus,
        categoryText,
    ].join(" ");
}

function resultNearbyScore(result: ServiceResult): number {
    if (result.kind === "provider") return result.item.servesNearby ? 2 : 0;
    return result.item.categoryIds.includes("local-help") || result.item.categoryIds.includes("delivery") ? 1 : 0;
}
