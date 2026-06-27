"use client";

import { Bookmark, FilterX } from "lucide-react";
import type { SavedSearchView } from "@/lib/mockAppClient";
import styles from "./explore.module.css";

export type ProviderTypeFilter = "ALL" | "INDIVIDUAL" | "BUSINESS";
export type ResponseFilter = "ALL" | "RECENT" | "FLEXIBLE";
export type GenderFilter = "ALL" | "FEMALE" | "MALE";

type Props = {
    providerType: ProviderTypeFilter;
    onProviderTypeChange: (t: ProviderTypeFilter) => void;
    genderFilter: GenderFilter;
    onGenderFilterChange: (g: GenderFilter) => void;
    locationScope: string;
    onLocationScopeChange: (v: string) => void;
    responseFilter: ResponseFilter;
    onResponseFilterChange: (v: ResponseFilter) => void;
    minRating: string;
    onMinRatingChange: (v: string) => void;
    radiusKm: string;
    onRadiusKmChange: (v: string) => void;
    priceMax: string;
    onPriceMaxChange: (v: string) => void;
    savedSearches: SavedSearchView[];
    onSaveSearch: () => void;
    onApplySavedSearch: (s: SavedSearchView) => void;
    onClear: () => void;
};

export function ExploreFilterBar({
    providerType, onProviderTypeChange,
    genderFilter, onGenderFilterChange,
    locationScope, onLocationScopeChange,
    responseFilter, onResponseFilterChange,
    minRating, onMinRatingChange,
    radiusKm, onRadiusKmChange,
    priceMax, onPriceMaxChange,
    savedSearches, onSaveSearch, onApplySavedSearch, onClear,
}: Props) {
    return (
        <div className={styles.exploreFilterBar}>
            {/* Profil türü */}
            <div className={styles.exploreFilterGroup}>
                <span>Profil türü</span>
                <div className={styles.exploreSegmented}>
                    {(["ALL", "INDIVIDUAL", "BUSINESS"] as const).map((t) => (
                        <button
                            key={t}
                            type="button"
                            className={providerType === t ? styles.exploreSegmentActive : ""}
                            onClick={() => onProviderTypeChange(t)}
                        >
                            {t === "ALL" ? "Tümü" : t === "INDIVIDUAL" ? "Serbest Uzman" : "İşletme"}
                        </button>
                    ))}
                </div>
            </div>

            {/* Cinsiyet tercihi */}
            <div className={styles.exploreFilterGroup}>
                <span>Cinsiyet tercihi</span>
                <div className={styles.exploreSegmented}>
                    {(["ALL", "FEMALE", "MALE"] as const).map((g) => (
                        <button
                            key={g}
                            type="button"
                            className={genderFilter === g ? styles.exploreSegmentActive : ""}
                            onClick={() => onGenderFilterChange(g)}
                        >
                            {g === "ALL" ? "Fark etmez" : g === "FEMALE" ? "Kadın" : "Erkek"}
                        </button>
                    ))}
                </div>
            </div>

            {/* Konum */}
            <label className={styles.exploreFilterField}>
                <span>Konum</span>
                <input
                    value={locationScope}
                    onChange={(e) => onLocationScopeChange(e.target.value)}
                    placeholder="Kadıköy, Moda, Üsküdar…"
                />
            </label>

            {/* Min. puan */}
            <label className={styles.exploreFilterField}>
                <span>Min. puan</span>
                <select value={minRating} onChange={(e) => onMinRatingChange(e.target.value)}>
                    <option value="0">Tüm puanlar</option>
                    <option value="4">4+ yıldız</option>
                    <option value="4.5">4.5+ yıldız</option>
                    <option value="4.7">4.7+ yıldız</option>
                    <option value="4.8">4.8+ yıldız</option>
                </select>
            </label>

            {/* Yanıt hızı */}
            <label className={styles.exploreFilterField}>
                <span>Yanıt hızı</span>
                <select
                    value={responseFilter}
                    onChange={(e) => onResponseFilterChange(e.target.value as ResponseFilter)}
                >
                    <option value="ALL">Fark etmez</option>
                    <option value="RECENT">Yakın zamanda aktif</option>
                    <option value="FLEXIBLE">Esnek zamanlı</option>
                </select>
            </label>

            {/* Mesafe */}
            <label className={styles.exploreFilterField}>
                <span>Mesafe</span>
                <select value={radiusKm} onChange={(e) => onRadiusKmChange(e.target.value)}>
                    <option value="3">3 km içinde</option>
                    <option value="5">5 km içinde</option>
                    <option value="10">10 km içinde</option>
                    <option value="25">25 km içinde</option>
                    <option value="100">Tüm şehir</option>
                </select>
            </label>

            {/* Maks. fiyat */}
            <label className={styles.exploreFilterField}>
                <span>Maks. bütçe</span>
                <select value={priceMax} onChange={(e) => onPriceMaxChange(e.target.value)}>
                    <option value="">Fark etmez</option>
                    <option value="300">₺300 altı</option>
                    <option value="500">₺500 altı</option>
                    <option value="1000">₺1.000 altı</option>
                    <option value="2000">₺2.000 altı</option>
                </select>
            </label>

            {/* Aksiyonlar */}
            <div className={styles.exploreFilterActions}>
                <button type="button" onClick={onSaveSearch}>
                    <Bookmark size={13} aria-hidden />
                    Aramayı kaydet
                </button>
                <button type="button" onClick={onClear}>
                    <FilterX size={13} aria-hidden />
                    Temizle
                </button>
            </div>

            {savedSearches.length > 0 && (
                <div className={styles.exploreFilterSaved}>
                    <span>Kayıtlı aramalar</span>
                    {savedSearches.map((s) => (
                        <button key={s.id} type="button" onClick={() => onApplySavedSearch(s)}>
                            {s.name}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}
