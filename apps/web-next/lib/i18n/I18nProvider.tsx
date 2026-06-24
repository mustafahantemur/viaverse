"use client";

import {
    createContext,
    useCallback,
    useContext,
    useEffect,
    useMemo,
    useState,
} from "react";
import type { ReactNode } from "react";
import {
    DEFAULT_LOCALE,
    SUPPORTED_LOCALES,
    messages,
    type Locale,
    type Translations,
} from "./messages";

const COOKIE_NAME = "vv_lang";
const COOKIE_MAX_AGE_DAYS = 365;

interface I18nContextValue {
    locale: Locale;
    setLocale: (next: Locale) => void;
    t: Translations;
    /** Helper for strings with {placeholders}. */
    format: (template: string, values: Record<string, string>) => string;
}

const I18nContext = createContext<I18nContextValue | null>(null);

function readCookieLocale(): Locale | null {
    if (typeof document === "undefined") return null;
    const entry = document.cookie
        .split(";")
        .map((s) => s.trim())
        .find((s) => s.startsWith(`${COOKIE_NAME}=`));
    if (!entry) return null;
    const value = entry.slice(COOKIE_NAME.length + 1) as Locale;
    return SUPPORTED_LOCALES.includes(value) ? value : null;
}

function writeCookieLocale(locale: Locale): void {
    if (typeof document === "undefined") return;
    const maxAge = COOKIE_MAX_AGE_DAYS * 24 * 60 * 60;
    document.cookie = `${COOKIE_NAME}=${locale}; path=/; max-age=${maxAge}; SameSite=Lax`;
}

export function I18nProvider({
    initialLocale,
    children,
}: {
    initialLocale?: Locale;
    children: ReactNode;
}) {
    const [locale, setLocaleState] = useState<Locale>(initialLocale ?? DEFAULT_LOCALE);

    useEffect(() => {
        const stored = readCookieLocale();
        if (stored && stored !== locale) setLocaleState(stored);
        // Run only on mount; persist via setLocale handler.
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        if (typeof document !== "undefined") {
            document.documentElement.lang = locale;
        }
    }, [locale]);

    const setLocale = useCallback((next: Locale) => {
        setLocaleState(next);
        writeCookieLocale(next);
    }, []);

    const value = useMemo<I18nContextValue>(() => {
        const t = messages[locale];
        return {
            locale,
            setLocale,
            t,
            format: (template, values) =>
                template.replace(/\{(\w+)\}/g, (_, key) => values[key] ?? `{${key}}`),
        };
    }, [locale, setLocale]);

    return <I18nContext.Provider value={value}>{children}</I18nContext.Provider>;
}

export function useTranslation(): I18nContextValue {
    const ctx = useContext(I18nContext);
    if (!ctx) {
        throw new Error("useTranslation must be used inside <I18nProvider>");
    }
    return ctx;
}
