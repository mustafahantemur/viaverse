"use client";

import { IconButton } from "@/components/primitives/IconButton";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import { useTheme } from "@/lib/theme/ThemeProvider";

/**
 * Theme + language toggles for the header. Theme flips on click;
 * language cycles between TR ↔ EN (we only ship two locales today).
 */
export function HeaderToggles() {
    const { theme, toggleTheme } = useTheme();
    const { locale, setLocale, t } = useTranslation();

    return (
        <>
            <IconButton
                label={locale === "tr" ? t.language.en : t.language.tr}
                onClick={() => setLocale(locale === "tr" ? "en" : "tr")}
            >
                {locale === "tr" ? "EN" : "TR"}
            </IconButton>
            <IconButton
                label={theme === "dark" ? t.theme.light : t.theme.dark}
                onClick={toggleTheme}
            >
                {theme === "dark" ? <SunIcon /> : <MoonIcon />}
            </IconButton>
        </>
    );
}

function SunIcon() {
    return (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden>
            <circle cx="12" cy="12" r="4" />
            <path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41" />
        </svg>
    );
}

function MoonIcon() {
    return (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden>
            <path d="M21 12.79A9 9 0 1 1 11.21 3a7 7 0 0 0 9.79 9.79z" />
        </svg>
    );
}
