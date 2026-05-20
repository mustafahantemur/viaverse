import type { Metadata } from "next";
import { cookies } from "next/headers";
import "./globals.css";
import "leaflet/dist/leaflet.css";
import { I18nProvider } from "@/lib/i18n/I18nProvider";
import {
    DEFAULT_LOCALE,
    SUPPORTED_LOCALES,
    type Locale,
} from "@/lib/i18n/messages";
import { ThemeProvider, type Theme } from "@/lib/theme/ThemeProvider";

export const metadata: Metadata = {
    title: "Viaverse — yakınındaki paylaşımlar, talepler ve hizmetler",
    description:
        "Yakınındaki paylaşımları, hizmetleri, talepleri ve işletmeleri tek üründe keşfet.",
};

export default async function RootLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    const cookieStore = await cookies();
    const rawLang = cookieStore.get("vv_lang")?.value as Locale | undefined;
    const initialLocale: Locale =
        rawLang && SUPPORTED_LOCALES.includes(rawLang) ? rawLang : DEFAULT_LOCALE;
    const rawTheme = cookieStore.get("vv_theme")?.value;
    const initialTheme: Theme = rawTheme === "dark" ? "dark" : "light";

    return (
        <html lang={initialLocale} data-theme={initialTheme}>
            <body>
                <ThemeProvider initialTheme={initialTheme}>
                    <I18nProvider initialLocale={initialLocale}>{children}</I18nProvider>
                </ThemeProvider>
            </body>
        </html>
    );
}
