import type { FeedItemView } from "@/lib/mockAppClient";

export type FeedFilter = "ALL" | "POST" | "ANNOUNCEMENT" | "EVENT";
export type DistanceValue = number | "MAX";

export type AnnouncementKind =
    | "POWER_OUTAGE"
    | "WATER_OUTAGE"
    | "TRAFFIC"
    | "FIRE"
    | "ACCIDENT"
    | "NOISE"
    | "SAFETY"
    | "MUNICIPALITY"
    | "LOST";

export type LocationSuggestion = {
    id: string;
    label: string;
    level: "Mahalle" | "Semt" | "Ilce" | "Sehir";
    parent: string;
};

export const feedFilters: Array<{ value: FeedFilter; label: string; types: FeedItemView["type"][] }> = [
    { value: "ALL", label: "Genel", types: ["POST", "INFO", "ANNOUNCEMENT", "EVENT", "TRAFFIC", "UTILITY"] },
    { value: "POST", label: "Paylaşımlar", types: ["POST", "INFO"] },
    { value: "ANNOUNCEMENT", label: "Duyurular", types: ["ANNOUNCEMENT", "TRAFFIC", "UTILITY"] },
    { value: "EVENT", label: "Etkinlikler", types: ["EVENT"] },
];

export const announcementTypes: Array<{
    kind: AnnouncementKind;
    label: string;
    shortLabel: string;
    tone: string;
    marker: string;
}> = [
    { kind: "POWER_OUTAGE", label: "Elektrik kesintisi", shortLabel: "Elektrik", tone: "amber", marker: "bolt" },
    { kind: "WATER_OUTAGE", label: "Su kesintisi", shortLabel: "Su", tone: "blue", marker: "drop" },
    { kind: "TRAFFIC", label: "Trafik / yol kapanması", shortLabel: "Trafik", tone: "orange", marker: "traffic" },
    { kind: "FIRE", label: "Yangın / duman", shortLabel: "Yangın", tone: "red", marker: "flame" },
    { kind: "ACCIDENT", label: "Kaza", shortLabel: "Kaza", tone: "rose", marker: "alert" },
    { kind: "NOISE", label: "Gürültü / olağandışı durum", shortLabel: "Gürültü", tone: "violet", marker: "waves" },
    { kind: "SAFETY", label: "Güvenlik uyarısı", shortLabel: "Güvenlik", tone: "slate", marker: "shield" },
    { kind: "MUNICIPALITY", label: "Belediye / altyapı çalışması", shortLabel: "Altyapı", tone: "green", marker: "wrench" },
    { kind: "LOST", label: "Kayıp eşya / hayvan", shortLabel: "Kayıp", tone: "pink", marker: "pin" },
];

export const locationSuggestions: LocationSuggestion[] = [
    { id: "loc-moda", label: "Moda", level: "Mahalle", parent: "Kadıköy, İstanbul" },
    { id: "loc-caferaga", label: "Caferağa", level: "Mahalle", parent: "Kadıköy, İstanbul" },
    { id: "loc-kadikoy", label: "Kadıköy", level: "Ilce", parent: "İstanbul" },
    { id: "loc-fenerbahce", label: "Fenerbahçe", level: "Semt", parent: "Kadıköy, İstanbul" },
    { id: "loc-acibadem", label: "Acıbadem", level: "Semt", parent: "Kadıköy / Üsküdar, İstanbul" },
    { id: "loc-uskudar", label: "Üsküdar", level: "Ilce", parent: "İstanbul" },
    { id: "loc-besiktas", label: "Beşiktaş", level: "Ilce", parent: "İstanbul" },
    { id: "loc-atasehir", label: "Ataşehir", level: "Ilce", parent: "İstanbul" },
    { id: "loc-cankaya", label: "Çankaya", level: "Ilce", parent: "Ankara" },
    { id: "loc-ankara", label: "Ankara", level: "Sehir", parent: "Türkiye" },
];

export function displayTypeLabel(item: FeedItemView): string {
    if (item.type === "POST" || item.type === "INFO") return "Genel";
    if (isAnnouncementItem(item)) return "Duyuru";
    return item.typeLabel;
}

export function isAnnouncementItem(item: FeedItemView): boolean {
    return ["ANNOUNCEMENT", "TRAFFIC", "UTILITY"].includes(item.type);
}

export function announcementKindFor(item: FeedItemView): AnnouncementKind | null {
    if (!isAnnouncementItem(item)) return null;
    const text = `${item.type} ${item.title} ${item.body} ${(item.hashtags ?? []).join(" ")}`.toLocaleLowerCase("tr-TR");

    if (item.type === "TRAFFIC" || /trafik|yol|köprü|kapan|ulaşım/.test(text)) return "TRAFFIC";
    if (/su|şebeke|baraj|damla/.test(text)) return "WATER_OUTAGE";
    if (/yangın|duman|alev|itfaiye/.test(text)) return "FIRE";
    if (/kaza|çarpış|ambulans/.test(text)) return "ACCIDENT";
    if (/gürültü|olağandışı|ses/.test(text)) return "NOISE";
    if (/güvenlik|uyarı|şüpheli|risk/.test(text)) return "SAFETY";
    if (/belediye|altyapı|kazı|çalışma|bakım/.test(text)) return "MUNICIPALITY";
    if (/kayıp|eşya|hayvan|kedi|köpek|anahtar/.test(text)) return "LOST";
    return item.type === "UTILITY" ? "POWER_OUTAGE" : null;
}

export function announcementMeta(kind: AnnouncementKind | null) {
    return announcementTypes.find((item) => item.kind === kind) ?? announcementTypes[0];
}

export function normalizeAnnouncementKind(value: string | null | undefined): AnnouncementKind | null {
    return announcementTypes.some((item) => item.kind === value) ? (value as AnnouncementKind) : null;
}

export function isVideoMedia(mediaUrl?: string | null, mediaType?: string | null): boolean {
    if (mediaType === "VIDEO") return true;
    if (!mediaUrl) return false;
    const normalized = mediaUrl.toLocaleLowerCase("tr-TR");
    return normalized.startsWith("data:video") || /\.(mp4|webm|mov)(\?|$)/.test(normalized);
}

export function distanceWithinRadius(locationLabel: string, radiusKm: DistanceValue): boolean {
    if (radiusKm === "MAX") return true;
    const normalized = locationLabel.toLocaleLowerCase("tr-TR");
    const meterMatch = normalized.match(/(\d+)\s*m/);
    if (meterMatch) {
        return Number(meterMatch[1]) / 1000 <= radiusKm;
    }

    const kmMatch = normalized.match(/(\d+(?:[.,]\d+)?)\s*km/);
    if (!kmMatch) return true;
    return Number(kmMatch[1].replace(",", ".")) <= radiusKm;
}

export function cleanTagInput(value: string): string {
    return value.replace(/^#+/, "").replace(/[^\p{L}\p{N}_-]/gu, "").toLocaleLowerCase("tr-TR");
}

export function initials(value: string): string {
    const parts = value.trim().split(/\s+/);
    if (parts.length === 1) return parts[0].slice(0, 1).toLocaleUpperCase("tr-TR");
    return `${parts[0][0]}${parts[parts.length - 1][0]}`.toLocaleUpperCase("tr-TR");
}

export function matchesSearch(item: FeedItemView, query: string): boolean {
    const normalized = query.trim().toLocaleLowerCase("tr-TR");
    if (!normalized) return true;
    const tagQuery = normalized.startsWith("#") ? normalized.slice(1) : null;
    if (tagQuery) return item.hashtags?.some((tag) => tag.includes(tagQuery)) ?? false;
    return [item.title, item.body, item.authorName, item.locationLabel, displayTypeLabel(item), ...(item.hashtags ?? [])]
        .join(" ")
        .toLocaleLowerCase("tr-TR")
        .includes(normalized);
}

export function markerPosition(seed: string, index: number): { left: number; top: number } {
    const chars = Array.from(seed).reduce((sum, char) => sum + char.charCodeAt(0), 0);
    return {
        left: 12 + ((chars + index * 17) % 72),
        top: 16 + ((chars * 3 + index * 11) % 62),
    };
}
