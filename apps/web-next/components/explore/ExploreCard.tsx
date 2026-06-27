"use client";

import { Clock3, MapPin, Star, Zap } from "lucide-react";
import { ListingCard, listingCardStyles as lc } from "@/components/product/ListingCard";
import type { BusinessView, ProviderView, ServiceCategoryView } from "@/lib/mockAppClient";

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
    const name = result.kind === "provider" ? result.item.displayName : result.item.tradeName;
    const subtitle = result.kind === "provider" ? result.item.headline : result.item.sector;
    const isNearby =
        result.kind === "provider"
            ? result.item.servesNearby
            : result.item.categoryIds.some((id) => id === "local-help" || id === "delivery");

    const item = result.item;
    const distanceLabel =
        item.distanceKm < 1
            ? `${Math.round(item.distanceKm * 1000)} m`
            : `${item.distanceKm.toFixed(1)} km`;

    return (
        <ListingCard
            interactive
            selected={selected}
            onClick={onSelect}
            media={(
                <img
                    src={item.photoUrl || `https://picsum.photos/seed/${item.id}-cover/600/338`}
                    alt={name}
                    loading="lazy"
                />
            )}
            mediaBadges={(
                <>
                    <span className={lc.badge}>{result.kind === "provider" ? "Serbest" : "İşletme"}</span>
                    {isNearby && (
                        <span className={`${lc.badge} ${lc.badgeRight}`}>
                            <Zap size={9} />
                            Yakında
                        </span>
                    )}
                </>
            )}
            title={name}
            titleAside={(
                <span className={lc.rating}>
                    <Star size={12} fill="currentColor" />
                    {item.rating.toFixed(1)}
                </span>
            )}
            subtitle={subtitle}
            clampSubtitle
            meta={(
                <>
                    <span className={lc.price}>{item.priceRange}</span>
                    <span><MapPin size={11} />{distanceLabel}</span>
                    <span><Clock3 size={11} />{item.responseTime}</span>
                </>
            )}
            chips={item.categoryIds.slice(0, 3).map((id) => {
                const cat = categoryById.get(id);
                return cat ? <span key={id} className={lc.chip}>{cat.label}</span> : null;
            })}
            footer={(
                <>
                    <span>{item.completedJobs} tamamlanan iş</span>
                    <button
                        type="button"
                        className={lc.ctaBtn}
                        onClick={(e) => { e.stopPropagation(); onRequest(); }}
                    >
                        Talep Başlat
                    </button>
                </>
            )}
        />
    );
}
