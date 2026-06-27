"use client";

import { useEffect } from "react";
import {
    BadgeCheck, ChevronRight, Clock3,
    MapPin, Sparkles, Star, X,
} from "lucide-react";
import { Button } from "@/components/primitives/Button";
import { categoryIconPath } from "@/components/product/ProductControls";
import type { BusinessView, ProviderView, ServiceCategoryView } from "@/lib/mockAppClient";
import styles from "./explore.module.css";

export type ExploreSelection =
    | { kind: "provider"; value: ProviderView }
    | { kind: "business"; value: BusinessView };

type Props = {
    selection: ExploreSelection | null;
    categoryById: Map<string, ServiceCategoryView>;
    onClose: () => void;
    onRequest: (selection: ExploreSelection) => void;
};

export function ExploreDetailModal({ selection, categoryById, onClose, onRequest }: Props) {
    useEffect(() => {
        if (!selection) return;
        function onKey(e: KeyboardEvent) { if (e.key === "Escape") onClose(); }
        document.addEventListener("keydown", onKey);
        return () => document.removeEventListener("keydown", onKey);
    }, [selection, onClose]);

    if (!selection) return null;

    const name = selection.kind === "provider" ? selection.value.displayName : selection.value.tradeName;
    const subtitle = selection.kind === "provider" ? selection.value.headline : selection.value.sector;
    const isVerified = selection.kind === "business" && selection.value.verificationStatus === "APPROVED";
    const isNearby =
        selection.kind === "provider"
            ? selection.value.servesNearby
            : selection.value.categoryIds.some((id) => id === "delivery" || id === "pets" || id === "events");
    const tags: string[] =
        selection.kind === "provider"
            ? selection.value.tags
            : [
                  isVerified ? "Onaylı işletme" : selection.value.verificationStatus,
                  selection.value.sector,
              ].filter(Boolean);
    const item = selection.value;
    const distanceLabel =
        item.distanceKm < 1
            ? `${Math.round(item.distanceKm * 1000)} m`
            : `${item.distanceKm.toFixed(1)} km`;

    return (
        <>
            <div className={styles.modalBackdrop} onClick={onClose} aria-hidden="true" />
            <div className={styles.modal} role="dialog" aria-modal aria-label={name}>
                {/* Photo — always a real image (same seed as the listing card) */}
                <div className={styles.modalPhoto}>
                    <img
                        src={item.photoUrl || `https://picsum.photos/seed/${item.id}-cover/600/338`}
                        alt={name}
                    />
                    <button
                        type="button"
                        className={styles.modalClose}
                        onClick={onClose}
                        aria-label="Kapat"
                    >
                        <X size={18} />
                    </button>
                    <span className={styles.modalTypeBadge}>
                        {selection.kind === "provider" ? "Serbest" : "İşletme"}
                    </span>
                    {isNearby && (
                        <span className={styles.modalNearbyBadge}>
                            <Sparkles size={11} />
                            Yakında
                        </span>
                    )}
                </div>

                <div className={styles.modalBody}>
                    {/* Title */}
                    <div className={styles.modalTitleRow}>
                        <h2>{name}</h2>
                        {isVerified && (
                            <BadgeCheck
                                size={20}
                                className={styles.modalVerifiedIcon}
                                aria-label="Onaylı işletme"
                            />
                        )}
                    </div>
                    <p className={styles.modalSubtitle}>{subtitle}</p>
                    <p className={styles.modalPrice}>{item.priceRange}</p>

                    {/* Metrics */}
                    <div className={styles.modalMetrics}>
                        <div className={styles.modalMetric}>
                            <strong>
                                <Star size={13} fill="currentColor" aria-hidden />
                                {item.rating.toFixed(1)}
                            </strong>
                            <span>Puan</span>
                        </div>
                        <div className={styles.modalMetric}>
                            <strong>{item.completedJobs}</strong>
                            <span>Tamamlanan</span>
                        </div>
                        <div className={styles.modalMetric}>
                            <strong>
                                <Clock3 size={12} aria-hidden />
                                {item.responseTime.replace("Genelde ", "")}
                            </strong>
                            <span>Yanıt süresi</span>
                        </div>
                    </div>

                    {/* Summary */}
                    <p className={styles.modalSummary}>{item.summary}</p>

                    {/* Location badges */}
                    <div className={styles.modalBadges}>
                        <span className={styles.modalBadge}>
                            <MapPin size={12} />
                            {item.locationScope} · {distanceLabel}
                        </span>
                        {isVerified && (
                            <span className={`${styles.modalBadge} ${styles.modalBadgeVerified}`}>
                                <BadgeCheck size={12} />
                                Onaylı işletme
                            </span>
                        )}
                        {isNearby && (
                            <span className={`${styles.modalBadge} ${styles.modalBadgeNearby}`}>
                                <Sparkles size={12} />
                                Yakın mesafeye uygun
                            </span>
                        )}
                    </div>

                    {/* Categories */}
                    <div className={styles.modalCategories}>
                        {item.categoryIds.map((id) => {
                            const cat = categoryById.get(id);
                            return cat ? (
                                <div key={id} className={styles.modalCategoryItem}>
                                    <img src={categoryIconPath(cat.icon)} alt="" aria-hidden />
                                    <div className={styles.modalCategoryText}>
                                        <strong>{cat.label}</strong>
                                        {cat.description && <span>{cat.description}</span>}
                                    </div>
                                </div>
                            ) : null;
                        })}
                    </div>

                    {/* Tags */}
                    {tags.length > 0 && (
                        <div className={styles.modalTags}>
                            {tags.map((tag) => (
                                <span key={tag} className={styles.modalTag}>
                                    #{tag.replace(/\s+/g, "")}
                                </span>
                            ))}
                        </div>
                    )}

                    {/* Actions */}
                    <div className={styles.modalActions}>
                        <Button fullWidth onClick={() => { onRequest(selection); onClose(); }}>
                            Talep Başlat
                        </Button>
                        <Button
                            variant="outline"
                            fullWidth
                            leadingIcon={<ChevronRight size={15} />}
                        >
                            Profili Görüntüle
                        </Button>
                    </div>
                </div>
            </div>
        </>
    );
}
