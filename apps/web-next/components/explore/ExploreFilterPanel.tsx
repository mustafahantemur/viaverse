"use client";

import { useState } from "react";
import { ChevronDown, ChevronRight, RotateCcw, SlidersHorizontal } from "lucide-react";
import type { ServiceCategoryView } from "@/lib/mockAppClient";
import type { ProviderTypeFilter, ResponseFilter } from "./ExploreFilterBar";
import styles from "./explore.module.css";

export type DynamicCategory = { id: string; label: string };

type Props = {
    categories: ServiceCategoryView[];
    dynamicCategories: DynamicCategory[];
    selectedCategory: string;
    selectedSubcat: string | null;
    providerType: ProviderTypeFilter;
    minRating: string;
    radiusKm: string;
    responseFilter: ResponseFilter;
    priceMin: string;
    priceMax: string;
    totalResults: number;
    onCategoryChange: (id: string) => void;
    onSubcatChange: (sub: string | null) => void;
    onProviderTypeChange: (t: ProviderTypeFilter) => void;
    onMinRatingChange: (v: string) => void;
    onRadiusKmChange: (v: string) => void;
    onResponseFilterChange: (v: ResponseFilter) => void;
    onPriceMinChange: (v: string) => void;
    onPriceMaxChange: (v: string) => void;
    onClear: () => void;
};

const RATINGS: [string, string][] = [
    ["0", "Tüm puanlar"],
    ["3", "3 ★ ve üzeri"],
    ["4", "4 ★ ve üzeri"],
    ["4.5", "4.5 ★ ve üzeri"],
    ["4.8", "Üst düzey 4.8+"],
];

const RESPONSE_SPEEDS: [ResponseFilter, string][] = [
    ["ALL", "Fark etmez"],
    ["RECENT", "Yakın zamanda aktif"],
    ["FLEXIBLE", "Esnek zamanlı"],
];

type SmartPick = { icon: string; title: string; hint: string; apply: () => void };

// Non-linear distance stops shown on the slider.
const DIST_STOPS = [1, 5, 10, 25, 50, 100];
function distLabel(km: number): string {
    return km >= 100 ? "Tüm şehir" : `${km} km`;
}

type PriceSuggestion = { label: string; min: string; max: string };

// Category-aware suggested price ranges (hourly / session / project pricing).
function priceSuggestions(catId: string): { unit: string; ranges: PriceSuggestion[] } {
    const hourly = catId === "education" || catId === "beauty";
    const project =
        catId === "digital" || catId === "creative" || catId === "consulting" ||
        catId === "events" || catId === "muzik-produksiyon" || catId === "drone-cekim";
    if (hourly) {
        return {
            unit: "saatlik",
            ranges: [
                { label: "≤ ₺400", min: "", max: "400" },
                { label: "₺400 – ₺700", min: "400", max: "700" },
                { label: "₺700+", min: "700", max: "" },
            ],
        };
    }
    if (project) {
        return {
            unit: "proje başı",
            ranges: [
                { label: "≤ ₺3.000", min: "", max: "3000" },
                { label: "₺3.000 – ₺10.000", min: "3000", max: "10000" },
                { label: "₺10.000+", min: "10000", max: "" },
            ],
        };
    }
    return {
        unit: "iş / seans başı",
        ranges: [
            { label: "≤ ₺500", min: "", max: "500" },
            { label: "₺500 – ₺1.500", min: "500", max: "1500" },
            { label: "₺1.500+", min: "1500", max: "" },
        ],
    };
}

export function ExploreFilterPanel({
    categories, dynamicCategories,
    selectedCategory, selectedSubcat,
    providerType,
    minRating, radiusKm, responseFilter,
    priceMin, priceMax,
    totalResults,
    onCategoryChange, onSubcatChange,
    onProviderTypeChange,
    onMinRatingChange, onRadiusKmChange,
    onResponseFilterChange,
    onPriceMinChange, onPriceMaxChange,
    onClear,
}: Props) {
    const [expandedCats, setExpandedCats] = useState<Set<string>>(new Set(
        selectedCategory !== "ALL" ? [selectedCategory] : []
    ));

    function toggleCat(id: string) {
        setExpandedCats((prev) => {
            const next = new Set(prev);
            if (next.has(id)) next.delete(id); else next.add(id);
            return next;
        });
    }

    const hasActive = selectedCategory !== "ALL" || selectedSubcat !== null ||
        providerType !== "ALL" ||
        Number(minRating) > 0 || Number(radiusKm) < 100 ||
        responseFilter !== "ALL" || Boolean(priceMin) || Boolean(priceMax);

    const distIdx = (() => {
        const i = DIST_STOPS.indexOf(Number(radiusKm));
        return i === -1 ? DIST_STOPS.length - 1 : i;
    })();

    const { unit: priceUnit, ranges: priceRanges } = priceSuggestions(selectedCategory);

    const smartPicks: SmartPick[] = [
        {
            icon: "✨", title: "Pırıl pırıl ekipler", hint: "Temizlik · 4.7+ puan",
            apply: () => { onCategoryChange("cleaning"); onSubcatChange(null); onMinRatingChange("4.7"); },
        },
        {
            icon: "⭐", title: "En yüksek puanlı", hint: "4.8+ üst düzey profiller",
            apply: () => { onMinRatingChange("4.8"); },
        },
        {
            icon: "📍", title: "Sana en yakın", hint: "5 km içinde",
            apply: () => { onRadiusKmChange("5"); },
        },
        {
            icon: "🏢", title: "Kurumsal işletmeler", hint: "Onaylı işletme profilleri",
            apply: () => { onProviderTypeChange("BUSINESS"); },
        },
        {
            icon: "⚡", title: "Yakın zamanda aktif", hint: "Hızlı dönüş alın",
            apply: () => { onResponseFilterChange("RECENT"); },
        },
    ];

    return (
        <div className={styles.filterSidebar}>
            {/* ─ Header ────────────────────────────────────────────── */}
            <div className={styles.filterSidebarHead}>
                <SlidersHorizontal size={15} aria-hidden />
                <span>Filtreler</span>
                {hasActive && (
                    <button type="button" className={styles.filterPanelClear} onClick={onClear}>
                        <RotateCcw size={11} />
                        Sıfırla
                    </button>
                )}
            </div>

            <div className={styles.filterSidebarScroll}>
                {/* ─ Category tree ─────────────────────────────────── */}
                <div className={styles.filterSection}>
                    <p className={styles.filterSectionLabel}>Kategori</p>
                    <div className={styles.catTree}>
                        <button
                            type="button"
                            className={[styles.catTreeRoot, selectedCategory === "ALL" && styles.catTreeRootActive].filter(Boolean).join(" ")}
                            onClick={() => { onCategoryChange("ALL"); onSubcatChange(null); }}
                        >
                            Tüm Kategoriler
                        </button>

                        {categories.map((cat) => (
                            <CategoryNode
                                key={cat.id}
                                label={cat.label}
                                subCategories={cat.subCategories ?? []}
                                active={selectedCategory === cat.id}
                                open={expandedCats.has(cat.id) || selectedCategory === cat.id}
                                selectedSubcat={selectedSubcat}
                                totalResults={totalResults}
                                onToggle={() => toggleCat(cat.id)}
                                onSelectCat={() => { onCategoryChange(cat.id); onSubcatChange(null); if (!expandedCats.has(cat.id)) toggleCat(cat.id); }}
                                onSelectSub={(sub) => { onCategoryChange(cat.id); onSubcatChange(sub); }}
                            />
                        ))}
                    </div>
                </div>

                {/* ─ Dynamic / user-created categories ──────────────── */}
                {dynamicCategories.length > 0 && (
                    <>
                        <div className={styles.filterDivider} />
                        <div className={styles.filterSection}>
                            <p className={styles.filterSectionLabel}>
                                Diğer Hizmetler
                                <span className={styles.filterSectionHint}>Topluluk kategorileri</span>
                            </p>
                            <div className={styles.catTree}>
                                {dynamicCategories.map((cat) => (
                                    <button
                                        key={cat.id}
                                        type="button"
                                        className={[styles.catTreeItem, selectedCategory === cat.id && styles.catTreeItemActive].filter(Boolean).join(" ")}
                                        onClick={() => { onCategoryChange(cat.id); onSubcatChange(null); }}
                                    >
                                        {cat.label}
                                    </button>
                                ))}
                            </div>
                        </div>
                    </>
                )}

                <div className={styles.filterDivider} />

                {/* ─ Profile type ───────────────────────────────────── */}
                <div className={styles.filterSection}>
                    <p className={styles.filterSectionLabel}>Profil Türü</p>
                    {(["ALL", "INDIVIDUAL", "BUSINESS"] as const).map((t) => (
                        <label key={t} className={styles.filterRadio}>
                            <input type="radio" name="providerType" checked={providerType === t}
                                onChange={() => onProviderTypeChange(t)} />
                            <span>{t === "ALL" ? "Tümü" : t === "INDIVIDUAL" ? "Serbest" : "İşletme"}</span>
                        </label>
                    ))}
                </div>

                <div className={styles.filterDivider} />

                {/* ─ Rating ──────────────────────────────────────────── */}
                <div className={styles.filterSection}>
                    <p className={styles.filterSectionLabel}>Minimum Puan</p>
                    {RATINGS.map(([val, label]) => (
                        <label key={val} className={styles.filterRadio}>
                            <input type="radio" name="minRating" checked={minRating === val}
                                onChange={() => onMinRatingChange(val)} />
                            <span>{label}</span>
                        </label>
                    ))}
                </div>

                <div className={styles.filterDivider} />

                {/* ─ Distance slider ─────────────────────────────────── */}
                <div className={styles.filterSection}>
                    <div className={styles.distanceSliderHead}>
                        <p className={styles.filterSectionLabel}>Mesafe</p>
                        <span className={styles.distanceValue}>{distLabel(DIST_STOPS[distIdx])}</span>
                    </div>
                    <input
                        type="range"
                        className={styles.distanceSlider}
                        min={0}
                        max={DIST_STOPS.length - 1}
                        step={1}
                        value={distIdx}
                        onChange={(e) => onRadiusKmChange(String(DIST_STOPS[Number(e.target.value)]))}
                        aria-label="Mesafe (km)"
                    />
                    <div className={styles.distanceTicks}>
                        {DIST_STOPS.map((km) => (
                            <span key={km}>{km >= 100 ? "100+" : km}</span>
                        ))}
                    </div>
                </div>

                <div className={styles.filterDivider} />

                {/* ─ Response speed ──────────────────────────────────── */}
                <div className={styles.filterSection}>
                    <p className={styles.filterSectionLabel}>Yanıt Hızı</p>
                    {RESPONSE_SPEEDS.map(([val, label]) => (
                        <label key={val} className={styles.filterRadio}>
                            <input type="radio" name="responseFilter" checked={responseFilter === val}
                                onChange={() => onResponseFilterChange(val)} />
                            <span>{label}</span>
                        </label>
                    ))}
                </div>

                <div className={styles.filterDivider} />

                {/* ─ Price range (dynamic + category-aware) ─────────── */}
                <div className={styles.filterSection}>
                    <p className={styles.filterSectionLabel}>
                        Fiyat Aralığı
                        <span className={styles.filterSectionHint}>{priceUnit}</span>
                    </p>
                    <div className={styles.priceRangeRow}>
                        <div className={styles.priceInputBox}>
                            <span className={styles.priceInputLabel}>Min ₺</span>
                            <input type="number" min="0" placeholder="0" value={priceMin}
                                onChange={(e) => onPriceMinChange(e.target.value)} className={styles.priceInput} />
                        </div>
                        <span className={styles.priceRangeDash}>—</span>
                        <div className={styles.priceInputBox}>
                            <span className={styles.priceInputLabel}>Maks ₺</span>
                            <input type="number" min="0" placeholder="∞" value={priceMax}
                                onChange={(e) => onPriceMaxChange(e.target.value)} className={styles.priceInput} />
                        </div>
                    </div>
                    <div className={styles.priceSuggestions}>
                        {priceRanges.map((r) => {
                            const active = priceMin === r.min && priceMax === r.max;
                            return (
                                <button
                                    key={r.label}
                                    type="button"
                                    className={[styles.priceSuggestionBtn, active && styles.priceSuggestionBtnActive].filter(Boolean).join(" ")}
                                    onClick={() => {
                                        if (active) { onPriceMinChange(""); onPriceMaxChange(""); }
                                        else { onPriceMinChange(r.min); onPriceMaxChange(r.max); }
                                    }}
                                >
                                    {r.label}
                                </button>
                            );
                        })}
                    </div>
                </div>

                <div className={styles.filterDivider} />

                {/* ─ Smart picks (curated quick filters) — at the bottom ─ */}
                <div className={styles.filterSection}>
                    <p className={styles.filterSectionLabel}>
                        Akıllı Seçimler
                        <span className={styles.filterSectionHint}>tek dokunuş</span>
                    </p>
                    <div className={styles.smartPicks}>
                        {smartPicks.map((pick) => (
                            <button
                                key={pick.title}
                                type="button"
                                className={styles.smartPick}
                                onClick={pick.apply}
                            >
                                <span className={styles.smartPickIcon} aria-hidden>{pick.icon}</span>
                                <span className={styles.smartPickText}>
                                    <strong>{pick.title}</strong>
                                    <small>{pick.hint}</small>
                                </span>
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            {/* ─ Footer ────────────────────────────────────────────── */}
            <div className={styles.filterSidebarFooter}>
                <strong>{totalResults}</strong> sonuç bulundu
            </div>
        </div>
    );
}

type NodeProps = {
    label: string;
    subCategories: string[];
    active: boolean;
    open: boolean;
    selectedSubcat: string | null;
    totalResults: number;
    onToggle: () => void;
    onSelectCat: () => void;
    onSelectSub: (sub: string) => void;
};

function CategoryNode({
    label, subCategories, active, open, selectedSubcat, totalResults,
    onToggle, onSelectCat, onSelectSub,
}: NodeProps) {
    const hasSubs = subCategories.length > 0;
    return (
        <div className={styles.catTreeGroup}>
            <div className={styles.catTreeRow}>
                <button
                    type="button"
                    className={[styles.catTreeItem, active && styles.catTreeItemActive].filter(Boolean).join(" ")}
                    onClick={onSelectCat}
                >
                    {label}
                    {active && totalResults > 0 && (
                        <span className={styles.catTreeCount}>{totalResults}</span>
                    )}
                </button>
                {hasSubs && (
                    <button
                        type="button"
                        className={styles.catTreeToggle}
                        onClick={onToggle}
                        aria-label={open ? "Daralt" : "Genişlet"}
                    >
                        {open ? <ChevronDown size={13} /> : <ChevronRight size={13} />}
                    </button>
                )}
            </div>
            {open && hasSubs && (
                <div className={styles.catTreeSubs}>
                    {subCategories.map((sub) => (
                        <button
                            key={sub}
                            type="button"
                            className={[styles.catTreeSub, active && selectedSubcat === sub && styles.catTreeSubActive].filter(Boolean).join(" ")}
                            onClick={() => onSelectSub(sub)}
                        >
                            {sub}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}
