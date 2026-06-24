"use client";

import styles from "./ProductPages.module.css";

export function TextField({
    label,
    value,
    onChange,
    type = "text",
    textarea = false,
    placeholder,
}: {
    label: string;
    value: string;
    onChange: (value: string) => void;
    type?: string;
    textarea?: boolean;
    placeholder?: string;
}) {
    return (
        <label className={styles.field}>
            <span>{label}</span>
            {textarea ? (
                <textarea value={value} placeholder={placeholder} onChange={(event) => onChange(event.target.value)} />
            ) : (
                <input type={type} value={value} placeholder={placeholder} onChange={(event) => onChange(event.target.value)} />
            )}
        </label>
    );
}

export function SelectField<T extends string>({
    label,
    value,
    onChange,
    options,
}: {
    label: string;
    value: T;
    onChange: (value: T) => void;
    options: Array<{ value: T; label: string }>;
}) {
    return (
        <label className={styles.field}>
            <span>{label}</span>
            <select value={value} onChange={(event) => onChange(event.target.value as T)}>
                {options.map((option) => (
                    <option key={option.value} value={option.value}>
                        {option.label}
                    </option>
                ))}
            </select>
        </label>
    );
}

export function categoryIconPath(icon: string): string {
    return `/brand/assets/categories/${icon}`;
}
