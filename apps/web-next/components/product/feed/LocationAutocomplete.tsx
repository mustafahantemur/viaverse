"use client";

import { useMemo, useState, type KeyboardEvent } from "react";
import { MapPin, X } from "lucide-react";
import styles from "@/components/product/ProductPages.module.css";
import { locationSuggestions, type LocationSuggestion } from "./feedModel";

function levelLabel(level: LocationSuggestion["level"]): string {
    if (level === "Ilce") return "İlçe";
    if (level === "Sehir") return "Şehir";
    return level;
}

export function LocationAutocomplete({
    label = "Konum",
    value,
    onValueChange,
    onSelect,
    onClear,
    placeholder = "Konum, ilçe veya semt ara",
    suggestions = locationSuggestions,
}: {
    label?: string;
    value: string;
    onValueChange: (value: string) => void;
    onSelect?: (suggestion: LocationSuggestion) => void;
    onClear?: () => void;
    placeholder?: string;
    suggestions?: LocationSuggestion[];
}) {
    const [focused, setFocused] = useState(false);
    const normalized = value.trim().toLocaleLowerCase("tr-TR");
    const matches = useMemo(() => {
        if (!normalized) return suggestions.slice(0, 6);
        return suggestions
            .filter((item) => `${item.label} ${item.parent} ${levelLabel(item.level)}`.toLocaleLowerCase("tr-TR").includes(normalized))
            .slice(0, 8);
    }, [normalized, suggestions]);
    const dropdownOpen = focused && matches.length > 0;

    function selectLocation(item: LocationSuggestion) {
        onValueChange(item.label);
        onSelect?.(item);
    }

    function handleKeyDown(event: KeyboardEvent<HTMLInputElement>) {
        if (event.key !== "Enter" || !matches[0]) return;
        event.preventDefault();
        selectLocation(matches[0]);
    }

    return (
        <div className={styles.autocompleteField}>
            <span className={styles.inlineFilterLabel}>{label}</span>
            <label className={styles.searchBox}>
                <MapPin size={17} aria-hidden />
                <input
                    value={value}
                    onChange={(event) => onValueChange(event.target.value)}
                    onFocus={() => setFocused(true)}
                    onBlur={() => window.setTimeout(() => setFocused(false), 120)}
                    onKeyDown={handleKeyDown}
                    placeholder={placeholder}
                />
                {value && (
                    <button
                        type="button"
                        className={styles.clearInlineButton}
                        onClick={(event) => {
                            event.preventDefault();
                            onValueChange("");
                            onClear?.();
                        }}
                        aria-label="Konumu temizle"
                    >
                        <X size={14} aria-hidden />
                    </button>
                )}
            </label>
            {dropdownOpen && (
                <div className={styles.autocompleteMenu}>
                    {matches.map((item) => (
                        <button
                            key={item.id}
                            type="button"
                            onMouseDown={(event) => event.preventDefault()}
                            onClick={() => selectLocation(item)}
                        >
                            <strong>{item.label}</strong>
                            <span>{levelLabel(item.level)} · {item.parent}</span>
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}
