import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
    title: "Viaverse — yakındaki yardımı, küçük işleri, hizmet verenleri buluştur",
    description:
        "Hyperlokal sosyal & küçük-iş ağı. Bir el iste, paylaş, teklif al — ya da yeteneğini işe dönüştür.",
};

export default function RootLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html lang="tr">
            <body>{children}</body>
        </html>
    );
}
