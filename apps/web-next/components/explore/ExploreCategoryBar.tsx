"use client";

import { Fragment } from "react";
import type { ServiceCategoryView } from "@/lib/mockAppClient";
import styles from "./explore.module.css";

type Props = {
    categories: ServiceCategoryView[];
    selected: string;
    selectedSubcat: string | null;
    onSelect: (id: string) => void;
    onSelectSubcat: (sub: string | null) => void;
};

export function ExploreCategoryBar({
    categories,
    selected,
    selectedSubcat,
    onSelect,
    onSelectSubcat,
}: Props) {
    const laneMap = new Map<string, ServiceCategoryView[]>();
    for (const cat of categories) {
        const list = laneMap.get(cat.lane) ?? [];
        list.push(cat);
        laneMap.set(cat.lane, list);
    }

    const activeCat = categories.find((c) => c.id === selected);
    const subCats = activeCat?.subCategories ?? [];

    return (
        <div className={styles.exploreCategoryBarWrapper} role="navigation" aria-label="Kategori filtresi">
            {/* ── Main category row ─────────────────────────────── */}
            <div className={styles.exploreCategoryBar}>
                <button
                    type="button"
                    className={[styles.exploreCategoryChip, selected === "ALL" && styles.exploreCategoryChipActive].filter(Boolean).join(" ")}
                    onClick={() => { onSelect("ALL"); onSelectSubcat(null); }}
                >
                    Tümü
                </button>

                {Array.from(laneMap.entries()).map(([lane, cats]) => (
                    <Fragment key={lane}>
                        <span className={styles.exploreCategoryLane} aria-hidden>{lane}</span>
                        {cats.map((cat) => (
                            <button
                                key={cat.id}
                                type="button"
                                className={[styles.exploreCategoryChip, selected === cat.id && styles.exploreCategoryChipActive].filter(Boolean).join(" ")}
                                onClick={() => {
                                    if (selected === cat.id) {
                                        onSelect("ALL");
                                        onSelectSubcat(null);
                                    } else {
                                        onSelect(cat.id);
                                        onSelectSubcat(null);
                                    }
                                }}
                            >
                                {cat.label}
                            </button>
                        ))}
                    </Fragment>
                ))}
            </div>

            {/* ── Subcategory row ───────────────────────────────── */}
            {selected !== "ALL" && subCats.length > 0 && (
                <div className={styles.exploreSubcatBar} aria-label="Alt kategori filtresi">
                    <button
                        type="button"
                        className={[styles.exploreSubcatChip, selectedSubcat === null && styles.exploreSubcatChipActive].filter(Boolean).join(" ")}
                        onClick={() => onSelectSubcat(null)}
                    >
                        Tüm alt kategoriler
                    </button>
                    {subCats.map((sub) => (
                        <button
                            key={sub}
                            type="button"
                            className={[styles.exploreSubcatChip, selectedSubcat === sub && styles.exploreSubcatChipActive].filter(Boolean).join(" ")}
                            onClick={() => onSelectSubcat(selectedSubcat === sub ? null : sub)}
                        >
                            {sub}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}
