"use client";

import { Clock3, MapPin, Star, Zap } from "lucide-react";
import type { BusinessView, ProviderView, ServiceCategoryView } from "@/lib/mockAppClient";
import styles from "./explore.module.css";

export type ExploreResult =
    | { kind: "provider"; item: ProviderView }
    | { kind: "business"; item: BusinessView };

type Props = {
    result: ExploreResult;
    categoryById: Map<string, ServiceCategoryView>;
    selected: boolean;
    onSelect: () => void;
    onRequest: () => void;
};

export function ExploreCard({ result, categoryById, selected, onSelect, onRequest }: Props) {
    // Discriminated-union-specific properties resolved via narrowing
    const name = result.kind === "provider" ? result.item.displayName : result.item.tradeName;
    const subtitle = result.kind === "provider" ? result.item.headline : result.item.sector;
    const isNearby =
        result.kind === "provider"
            ? result.item.servesNearby
            : result.item.categoryIds.some((id) => id === "local-help" || id === "delivery");

    // Shared properties (present on both ProviderView and BusinessView)
    const item = result.item;
    const distanceLabel =
        item.distanceKm < 1
            ? `${Math.round(item.distanceKm * 1000)} m`
            : `${item.distanceKm.toFixed(1)} km`;

    return (
        <article
            className={`${styles.exploreCard} ${selected ? styles.exploreCardSelected : ""}`}
            onClick={onSelect}
            role="button"
            tabIndex={0}
            aria-pressed={selected}
            onKeyDown={(e) => e.key === "Enter" && onSelect()}
        >
            <div className={styles.exploreCardPhoto}>
                {item.photoUrl ? (
                    <img src={item.photoUrl} alt={name} loading="lazy" />
                ) : (
                    <div className={styles.exploreCardPhotoFallback}>
                        {result.kind === "provider" ? "👤" : "🏢"}
                    </div>
                )}
                <span className={styles.exploreCardTypeBadge}>
                    {result.kind === "provider" ? "Bireysel" : "İşletme"}
                </span>
                {isNearby && (
                    <span className={styles.exploreCardNearbyBadge}>
                        <Zap size={9} />
                        Yakında
                    </span>
                )}
            </div>

            <div className={styles.exploreCardBody}>
                <div className={styles.exploreCardHeader}>
                    <h3>{name}</h3>
                    <span className={styles.exploreCardRating}>
                        <Star size={12} fill="currentColor" />
                        {item.rating.toFixed(1)}
                    </span>
                </div>

                <p className={styles.exploreCardSubtitle}>{subtitle}</p>

                <div className={styles.exploreCardMeta}>
                    <span className={styles.exploreCardPrice}>{item.priceRange}</span>
                    <span>
                        <MapPin size={11} />
                        {distanceLabel}
                    </span>
                    <span>
                        <Clock3 size={11} />
                        {item.responseTime}
                    </span>
                </div>

                <div className={styles.exploreCardCategories}>
                    {item.categoryIds.slice(0, 3).map((id) => {
                        const cat = categoryById.get(id);
                        return cat ? (
                            <span key={id} className={styles.exploreCardCategoryChip}>
                                {cat.label}
                            </span>
                        ) : null;
                    })}
                </div>
            </div>

            <div className={styles.exploreCardFooter}>
                <span>{item.completedJobs} tamamlanan iş</span>
                <button
                    type="button"
                    className={styles.exploreCardCtaBtn}
                    onClick={(e) => {
                        e.stopPropagation();
                        onRequest();
                    }}
                >
                    Talep Başlat
                </button>
            </div>
        </article>
    );
}
