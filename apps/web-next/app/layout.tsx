import type { Metadata } from "next";
import { cookies } from "next/headers";
import "./globals.css";
import { I18nProvider } from "@/lib/i18n/I18nProvider";
import {
    DEFAULT_LOCALE,
    SUPPORTED_LOCALES,
    type Locale,
} from "@/lib/i18n/messages";
import { ThemeProvider, type Theme } from "@/lib/theme/ThemeProvider";

export const metadata: Metadata = {
    title: "Viaverse — yakındaki yardımı, küçük işleri, hizmet verenleri buluştur",
    description:
        "Hyperlokal sosyal & küçük-iş ağı. Bir el iste, paylaş, teklif al — ya da yeteneğini işe dönüştür.",
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
