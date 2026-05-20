"use client";

import { Car, Droplets, Flame, MapPin, ShieldAlert, SlidersHorizontal, TriangleAlert, Volume2, Wrench, X, Zap, type LucideIcon } from "lucide-react";
import styles from "@/components/product/ProductPages.module.css";
import type { HashtagSuggestionView } from "@/lib/mockAppClient";
import { DistanceSlider } from "./DistanceSlider";
import { LocationAutocomplete } from "./LocationAutocomplete";
import { TagAutocomplete } from "./TagAutocomplete";
import { announcementTypes, type AnnouncementKind, type DistanceValue, type FeedFilter } from "./feedModel";

const announcementIcons = {
    POWER_OUTAGE: Zap,
    WATER_OUTAGE: Droplets,
    TRAFFIC: Car,
    FIRE: Flame,
    ACCIDENT: TriangleAlert,
    NOISE: Volume2,
    SAFETY: ShieldAlert,
    MUNICIPALITY: Wrench,
    LOST: MapPin,
} satisfies Record<AnnouncementKind, LucideIcon>;

export function FilterPanel({
    open,
    selectedFilter,
    tagDraft,
    onTagDraftChange,
    selectedTags,
    tagSuggestions,
    onAddTag,
    onRemoveTag,
    locationQuery,
    onLocationChange,
    distance,
    onDistanceChange,
    selectedAnnouncementKinds,
    onToggleAnnouncementKind,
    onClear,
    onClose,
}: {
    open: boolean;
    selectedFilter: FeedFilter;
    tagDraft: string;
    onTagDraftChange: (value: string) => void;
    selectedTags: string[];
    tagSuggestions: HashtagSuggestionView[];
    onAddTag: (tag: string) => void;
    onRemoveTag: (tag: string) => void;
    locationQuery: string;
    onLocationChange: (value: string) => void;
    distance: DistanceValue;
    onDistanceChange: (value: DistanceValue) => void;
    selectedAnnouncementKinds: AnnouncementKind[];
    onToggleAnnouncementKind: (kind: AnnouncementKind) => void;
    onClear: () => void;
    onClose: () => void;
}) {
    const showAnnouncementFilters = selectedFilter === "ANNOUNCEMENT";

    return (
        <section className={[styles.filterPanel, open && styles.filterPanelOpen].filter(Boolean).join(" ")} aria-hidden={!open}>
            <div className={styles.filterPanelInner}>
                <header className={styles.filterPanelHeader}>
                    <div>
                        <span><SlidersHorizontal size={15} aria-hidden /> Akışı daralt</span>
                        <strong>Arama, etiket, konum ve mesafe tek yerden yönetilir.</strong>
                    </div>
                    <button type="button" onClick={onClose} aria-label="Filtreleri kapat">
                        <X size={17} aria-hidden />
                    </button>
                </header>
                <div className={styles.filterPanelGrid}>
                    <TagAutocomplete
                        label="Etiket"
                        value={tagDraft}
                        onValueChange={onTagDraftChange}
                        selectedTags={selectedTags}
                        suggestions={tagSuggestions}
                        onSelect={onAddTag}
                        onRemove={onRemoveTag}
                        placeholder="Etiket, konu veya # ara"
                    />
                    <LocationAutocomplete
                        label="Konum"
                        value={locationQuery}
                        onValueChange={onLocationChange}
                        placeholder="Semt, ilçe veya şehir ara"
                    />
                    <DistanceSlider value={distance} onChange={onDistanceChange} />
                </div>
                {showAnnouncementFilters && (
                    <div className={styles.announcementFilterBlock}>
                        <div>
                            <span className={styles.inlineFilterLabel}>Duyuru türleri</span>
                            <p>Seçimler duyuru listesi ve harita markerlarını birlikte sadeleştirir.</p>
                        </div>
                        <div className={styles.announcementTypeGrid}>
                            {announcementTypes.map((item) => {
                                const Icon = announcementIcons[item.kind];
                                const active = selectedAnnouncementKinds.includes(item.kind);
                                return (
                                    <button
                                        key={item.kind}
                                        type="button"
                                        className={[styles.announcementTypeChip, active && styles.announcementTypeChipActive].filter(Boolean).join(" ")}
                                        data-tone={item.tone}
                                        onClick={() => onToggleAnnouncementKind(item.kind)}
                                    >
                                        <Icon size={16} aria-hidden />
                                        <span>{item.shortLabel}</span>
                                    </button>
                                );
                            })}
                        </div>
                    </div>
                )}
                <footer className={styles.filterPanelFooter}>
                    <button type="button" onClick={onClear}>Temizle</button>
                    <button type="button" onClick={onClose}>Uygula</button>
                </footer>
            </div>
        </section>
    );
}
