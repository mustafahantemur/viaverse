"use client";

import { BadgeCheck, Building2, ChevronRight, Clock3, MapPin, Sparkles, Star, UserRound } from "lucide-react";
import { Button } from "@/components/primitives/Button";
import { categoryIconPath } from "@/components/product/ProductControls";
import type { BusinessView, ProviderView, ServiceCategoryView } from "@/lib/mockAppClient";
import styles from "./explore.module.css";

export type ExploreSelection =
    | { kind: "provider"; value: ProviderView }
    | { kind: "business"; value: BusinessView };

type Props = {
    selection: ExploreSelection;
    categoryById: Map<string, ServiceCategoryView>;
};

export function ExploreDetailPanel({ selection, categoryById }: Props) {
    // Discriminated-union-specific properties resolved via narrowing
    const name = selection.kind === "provider" ? selection.value.displayName : selection.value.tradeName;
    const subtitle = selection.kind === "provider" ? selection.value.headline : selection.value.sector;
    const isVerified =
        selection.kind === "business" && selection.value.verificationStatus === "APPROVED";
    const isNearby =
        selection.kind === "provider"
            ? selection.value.servesNearby
            : selection.value.categoryIds.some((id) => id === "local-help" || id === "delivery");
    const tags: string[] =
        selection.kind === "provider"
            ? selection.value.tags
            : [
                  selection.value.verificationStatus === "APPROVED"
                      ? "Onaylı işletme"
                      : selection.value.verificationStatus,
                  selection.value.sector,
              ];

    // Shared properties (present on both ProviderView and BusinessView)
    const item = selection.value;
    const distanceLabel =
        item.distanceKm < 1
            ? `${Math.round(item.distanceKm * 1000)} m uzakta`
            : `${item.distanceKm.toFixed(1)} km uzakta`;

    return (
        <div className={styles.exploreDetail}>
            <div className={styles.exploreDetailPhoto}>
                {item.photoUrl ? (
                    <img src={item.photoUrl} alt={name} />
                ) : (
                    <div className={styles.exploreDetailPhotoFallback}>
                        {selection.kind === "provider" ? <UserRound size={34} aria-hidden /> : <Building2 size={34} aria-hidden />}
                    </div>
                )}
            </div>

            <div className={styles.exploreDetailHeader}>
                <div className={styles.exploreDetailTitleRow}>
                    <h2>{name}</h2>
                    {isVerified && (
                        <BadgeCheck size={20} className={styles.exploreDetailVerifiedIcon} aria-label="Onaylı" />
                    )}
                    <span className={styles.exploreDetailTypeBadge}>{item.providerType}</span>
                </div>
                <div className={styles.exploreDetailSubRow}>
                    <span>{subtitle}</span>
                    <span className={styles.exploreDetailPrice}>{item.priceRange}</span>
                </div>
            </div>

            <div className={styles.exploreDetailMetrics}>
                <div className={styles.exploreDetailMetric}>
                    <strong>
                        <Star size={14} fill="currentColor" style={{ color: "#f59e0b" }} />
                        {item.rating.toFixed(1)}
                    </strong>
                    <span>Puan</span>
                </div>
                <div className={styles.exploreDetailMetric}>
                    <strong>{item.completedJobs}</strong>
                    <span>Tamamlanan</span>
                </div>
                <div className={styles.exploreDetailMetric}>
                    <strong>
                        <Clock3 size={13} />
                        <span style={{ fontSize: 11 }}>{item.responseTime.replace("Genelde ", "")}</span>
                    </strong>
                    <span>Yanıt</span>
                </div>
            </div>

            <p className={styles.exploreDetailSummary}>{item.summary}</p>

            <div className={styles.exploreDetailBadges}>
                <span className={styles.exploreDetailBadge}>
                    <MapPin size={12} />
                    {item.locationScope} · {distanceLabel}
                </span>
                {isVerified && (
                    <span className={`${styles.exploreDetailBadge} ${styles.exploreDetailBadgeVerified}`}>
                        <BadgeCheck size={12} />
                        Onaylı işletme
                    </span>
                )}
                {isNearby && (
                    <span className={`${styles.exploreDetailBadge} ${styles.exploreDetailBadgeNearby}`}>
                        <Sparkles size={12} />
                        Yakın mesafeye uygun
                    </span>
                )}
            </div>

            <div className={styles.exploreDetailCategories}>
                {item.categoryIds.map((id) => {
                    const cat = categoryById.get(id);
                    return (
                        <div key={id} className={styles.exploreDetailCategoryItem}>
                            {cat && <img src={categoryIconPath(cat.icon)} alt="" />}
                            <div className={styles.exploreDetailCategoryText}>
                                <strong>{cat?.label ?? id}</strong>
                                {cat?.description && <span>{cat.description}</span>}
                            </div>
                        </div>
                    );
                })}
            </div>

            {tags.length > 0 && (
                <div className={styles.exploreDetailTags}>
                    {tags.map((tag) => (
                        <span key={tag} className={styles.exploreDetailTag}>
                            #{tag.replace(/\s+/g, "")}
                        </span>
                    ))}
                </div>
            )}

            <div className={styles.exploreDetailActions}>
                <Button fullWidth>Talep başlat</Button>
                <Button variant="outline" fullWidth leadingIcon={<ChevronRight size={15} />}>
                    Profili aç
                </Button>
            </div>
        </div>
    );
}
