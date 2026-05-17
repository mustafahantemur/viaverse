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

export type Theme = "light" | "dark";

const COOKIE_NAME = "vv_theme";
const COOKIE_MAX_AGE_DAYS = 365;

interface ThemeContextValue {
    theme: Theme;
    setTheme: (next: Theme) => void;
    toggleTheme: () => void;
}

const ThemeContext = createContext<ThemeContextValue | null>(null);

function readCookieTheme(): Theme | null {
    if (typeof document === "undefined") return null;
    const entry = document.cookie
        .split(";")
        .map((s) => s.trim())
        .find((s) => s.startsWith(`${COOKIE_NAME}=`));
    if (!entry) return null;
    const value = entry.slice(COOKIE_NAME.length + 1);
    return value === "dark" || value === "light" ? value : null;
}

function writeCookieTheme(theme: Theme): void {
    if (typeof document === "undefined") return;
    const maxAge = COOKIE_MAX_AGE_DAYS * 24 * 60 * 60;
    document.cookie = `${COOKIE_NAME}=${theme}; path=/; max-age=${maxAge}; SameSite=Lax`;
}

function applyThemeAttribute(theme: Theme): void {
    if (typeof document === "undefined") return;
    document.documentElement.setAttribute("data-theme", theme);
}

export function ThemeProvider({
    initialTheme,
    children,
}: {
    initialTheme?: Theme;
    children: ReactNode;
}) {
    const [theme, setThemeState] = useState<Theme>(initialTheme ?? "light");

    useEffect(() => {
        const stored = readCookieTheme();
        const initial = stored ?? "light";
        if (initial !== theme) {
            setThemeState(initial);
        }
        applyThemeAttribute(initial);
        // Mount-only sync; subsequent changes go through setTheme.
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const setTheme = useCallback((next: Theme) => {
        setThemeState(next);
        writeCookieTheme(next);
        applyThemeAttribute(next);
    }, []);

    const toggleTheme = useCallback(() => {
        setTheme(theme === "dark" ? "light" : "dark");
    }, [theme, setTheme]);

    const value = useMemo<ThemeContextValue>(
        () => ({ theme, setTheme, toggleTheme }),
        [theme, setTheme, toggleTheme],
    );

    return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
}

export function useTheme(): ThemeContextValue {
    const ctx = useContext(ThemeContext);
    if (!ctx) {
        throw new Error("useTheme must be used inside <ThemeProvider>");
    }
    return ctx;
}
