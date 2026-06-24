"use client";

import { useState, type KeyboardEvent } from "react";
import { Hash, X } from "lucide-react";
import styles from "@/components/product/ProductPages.module.css";
import type { HashtagSuggestionView } from "@/lib/mockAppClient";
import { cleanTagInput } from "./feedModel";

export function TagAutocomplete({
    label = "Etiket",
    value,
    onValueChange,
    selectedTags,
    suggestions,
    onSelect,
    onRemove,
    placeholder = "Etiket ara",
}: {
    label?: string;
    value: string;
    onValueChange: (value: string) => void;
    selectedTags: string[];
    suggestions: HashtagSuggestionView[];
    onSelect: (tag: string) => void;
    onRemove: (tag: string) => void;
    placeholder?: string;
}) {
    const [focused, setFocused] = useState(false);
    const cleanValue = cleanTagInput(value);
    const availableSuggestions = suggestions.filter((item) => !selectedTags.includes(item.tag));
    const exactMatch = availableSuggestions.some((item) => item.tag === cleanValue);
    const dropdownOpen = focused && (availableSuggestions.length > 0 || cleanValue.length > 0);

    function selectTag(tag: string) {
        const cleanTag = cleanTagInput(tag);
        if (!cleanTag) return;
        onSelect(cleanTag);
        onValueChange("");
    }

    function handleKeyDown(event: KeyboardEvent<HTMLInputElement>) {
        if (event.key !== "Enter") return;
        event.preventDefault();
        if (availableSuggestions[0] && !cleanValue) {
            selectTag(availableSuggestions[0].tag);
            return;
        }
        selectTag(cleanValue || availableSuggestions[0]?.tag || "");
    }

    return (
        <div className={styles.autocompleteField}>
            <span className={styles.inlineFilterLabel}>{label}</span>
            <label className={styles.searchBox}>
                <Hash size={17} aria-hidden />
                <input
                    value={value}
                    onChange={(event) => onValueChange(cleanTagInput(event.target.value))}
                    onFocus={() => setFocused(true)}
                    onBlur={() => window.setTimeout(() => setFocused(false), 120)}
                    onKeyDown={handleKeyDown}
                    placeholder={placeholder}
                />
            </label>
            {selectedTags.length > 0 && (
                <div className={styles.selectedTagRow}>
                    {selectedTags.map((tag) => (
                        <button key={tag} type="button" className={styles.selectedTagChip} onClick={() => onRemove(tag)}>
                            #{tag}
                            <X size={12} aria-hidden />
                        </button>
                    ))}
                </div>
            )}
            {dropdownOpen && (
                <div className={styles.autocompleteMenu}>
                    {availableSuggestions.map((item) => (
                        <button key={item.tag} type="button" onMouseDown={(event) => event.preventDefault()} onClick={() => selectTag(item.tag)}>
                            <strong>#{item.tag}</strong>
                            <span>{item.usageCount} paylaşım · {item.sampleTitle}</span>
                        </button>
                    ))}
                    {cleanValue && !exactMatch && (
                        <button type="button" onMouseDown={(event) => event.preventDefault()} onClick={() => selectTag(cleanValue)}>
                            <strong>#{cleanValue}</strong>
                            <span>Yeni etiket olarak kullanıma hazır</span>
                        </button>
                    )}
                </div>
            )}
        </div>
    );
}
