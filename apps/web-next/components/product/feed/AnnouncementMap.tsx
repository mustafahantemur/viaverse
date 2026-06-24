"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import { Car, Droplets, Flame, MapPin, Minus, Plus, ShieldAlert, TriangleAlert, Volume2, Wrench, Zap, type LucideIcon } from "lucide-react";
import styles from "@/components/product/ProductPages.module.css";
import { formatRelative, type AnnouncementIncidentView, type FeedItemView, type IncidentUpdateView } from "@/lib/mockAppClient";
import { announcementMeta, displayTypeLabel, isVideoMedia, normalizeAnnouncementKind, type AnnouncementKind } from "./feedModel";

const announcementIcons = {
    POWER_OUTAGE: Zap,
    WATER_OUTAGE: Droplets,
    TRAFFIC: Car,
    FIRE: Flame,
    ACCIDENT: TriangleAlert,
    NOISE: Volume2,
    SAFETY: ShieldAlert,
    MUNICIPALITY: Wrench,
    LOST: MapPin,
} satisfies Record<AnnouncementKind, LucideIcon>;

type LeafletRuntime = typeof import("leaflet");
type LeafletMap = import("leaflet").Map;
type LeafletMarker = import("leaflet").Marker;

const KIND_ICON_PATHS: Record<AnnouncementKind, string> = {
    POWER_OUTAGE: '<polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>',
    WATER_OUTAGE: '<path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z"/>',
    TRAFFIC: '<rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/>',
    FIRE: '<path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"/>',
    ACCIDENT: '<path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>',
    NOISE: '<polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><path d="M15.54 8.46a5 5 0 0 1 0 7.07"/><path d="M19.07 4.93a10 10 0 0 1 0 14.14"/>',
    SAFETY: '<path d="M12 22s8-4 8-11V5l-8-3-8 3v6c0 7 8 11 8 11z"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>',
    MUNICIPALITY: '<path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/>',
    LOST: '<path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>',
};

function incidentMarkerHtml(cssClass: string, tone: string, iconPath: string): string {
    return `<span class="${cssClass}" data-tone="${tone}"><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">${iconPath}</svg></span>`;
}

const FEED_ITEM_ICON_PATHS: Record<string, string> = {
    EVENT: '<rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>',
    POST: '<path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>',
    ANNOUNCEMENT: '<path d="M3 11l19-9-9 19-2-8-8-2z"/>',
    TRAFFIC: '<rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/>',
    UTILITY: '<path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z"/>',
};

function feedItemMarkerHtml(cssClass: string, type: string): string {
    const path = FEED_ITEM_ICON_PATHS[type] ?? FEED_ITEM_ICON_PATHS.POST!;
    return `<span class="${cssClass}" data-type="${type.toLowerCase()}"><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">${path}</svg></span>`;
}

function labelToLatLng(label: string): [number, number] {
    let hash = 5381;
    for (let i = 0; i < label.length; i++) {
        hash = ((hash << 5) + hash) ^ label.charCodeAt(i);
        hash = hash & 0x7fffffff;
    }
    const lat = 40.90 + ((hash % 1000) / 1000) * 0.15;
    const lng = 28.85 + (((hash >> 10) % 1000) / 1000) * 0.35;
    return [lat, lng];
}

export function AnnouncementMap({
    incidents,
    postsById,
    feedItems,
    activeFilter,
    selectedKinds,
    onOpenPost,
}: {
    incidents: AnnouncementIncidentView[];
    postsById: Map<string, FeedItemView>;
    feedItems?: FeedItemView[];
    activeFilter?: string;
    selectedKinds: AnnouncementKind[];
    onOpenPost?: (item: FeedItemView) => void;
}) {
    const showIncidents = !activeFilter || activeFilter === "ANNOUNCEMENT";
    const mapContainerRef = useRef<HTMLDivElement | null>(null);
    const mapRef = useRef<LeafletMap | null>(null);
    const markersRef = useRef<LeafletMarker[]>([]);
    const leafletRef = useRef<LeafletRuntime | null>(null);
    const [selectedId, setSelectedId] = useState<string | null>(incidents[0]?.id ?? null);
    const [selectedFeedItemId, setSelectedFeedItemId] = useState<string | null>(feedItems?.[0]?.id ?? null);
    const [confirmedIds, setConfirmedIds] = useState<string[]>([]);
    const [resolvedIds, setResolvedIds] = useState<string[]>([]);
    const [mapReady, setMapReady] = useState(false);

    const visibleIncidents = useMemo(() => {
        return incidents.filter((incident) => {
            const kind = normalizeAnnouncementKind(incident.kind);
            return kind && (selectedKinds.length === 0 || selectedKinds.includes(kind));
        });
    }, [incidents, selectedKinds]);

    const selectedIncident = visibleIncidents.find((incident) => incident.id === selectedId) ?? visibleIncidents[0] ?? null;
    const selectedKind = normalizeAnnouncementKind(selectedIncident?.kind) ?? "POWER_OUTAGE";
    const selectedMeta = announcementMeta(selectedKind);
    const relatedPosts = selectedIncident?.relatedPostIds.map((id) => postsById.get(id)).filter(Boolean) as FeedItemView[] | undefined;
    const selectedFeedItem = feedItems?.find((item) => item.id === selectedFeedItemId) ?? feedItems?.[0] ?? null;

    useEffect(() => {
        let cancelled = false;
        async function setupMap() {
            if (!mapContainerRef.current || mapRef.current) return;
            const L = await import("leaflet");
            if (cancelled || !mapContainerRef.current) return;
            leafletRef.current = L;
            const map = L.map(mapContainerRef.current, {
                center: [40.991, 29.025],
                zoom: 14,
                zoomControl: false,
                attributionControl: false,
            });
            L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
                maxZoom: 19,
                attribution: "OpenStreetMap",
            }).addTo(map);
            mapRef.current = map;
            setMapReady(true);
        }
        setupMap();
        return () => {
            cancelled = true;
        };
    }, []);

    useEffect(() => {
        const L = leafletRef.current;
        const map = mapRef.current;
        if (!mapReady || !L || !map) return;

        markersRef.current.forEach((marker) => marker.remove());

        if (showIncidents) {
            markersRef.current = visibleIncidents.map((incident) => {
                const kind = normalizeAnnouncementKind(incident.kind) ?? "POWER_OUTAGE";
                const meta = announcementMeta(kind);
                const iconPath = KIND_ICON_PATHS[kind] ?? KIND_ICON_PATHS.POWER_OUTAGE;
                const marker = L.marker([incident.latitude, incident.longitude], {
                    icon: L.divIcon({
                        className: "",
                        html: incidentMarkerHtml(styles.leafletIncidentMarker, meta.tone, iconPath),
                        iconSize: [36, 36],
                        iconAnchor: [18, 18],
                    }),
                }).addTo(map);
                marker.on("click", () => setSelectedId(incident.id));
                return marker;
            });

            if (visibleIncidents.length > 0) {
                const bounds = L.latLngBounds(visibleIncidents.map((incident) => [incident.latitude, incident.longitude]));
                map.fitBounds(bounds.pad(0.2), { animate: true, maxZoom: 15 });
            }
        } else {
            const items = feedItems ?? [];
            markersRef.current = items.map((item) => {
                const [lat, lng] = labelToLatLng(item.locationLabel);
                const markerType = item.type === "EVENT" ? "EVENT" : item.type === "TRAFFIC" ? "TRAFFIC" : item.type === "UTILITY" || item.type === "ANNOUNCEMENT" ? "ANNOUNCEMENT" : "POST";
                const marker = L.marker([lat, lng], {
                    icon: L.divIcon({
                        className: "",
                        html: feedItemMarkerHtml(styles.leafletFeedMarker, markerType),
                        iconSize: [36, 36],
                        iconAnchor: [18, 18],
                    }),
                }).addTo(map);
                marker.on("click", () => setSelectedFeedItemId(item.id));
                return marker;
            });

            if (items.length > 0) {
                const coords = items.map((item) => labelToLatLng(item.locationLabel));
                const bounds = L.latLngBounds(coords);
                map.fitBounds(bounds.pad(0.2), { animate: true, maxZoom: 14 });
            }
        }
    }, [mapReady, visibleIncidents, feedItems, showIncidents]);

    useEffect(() => {
        if (!selectedIncident || !mapRef.current || !showIncidents) return;
        mapRef.current.flyTo([selectedIncident.latitude, selectedIncident.longitude], Math.max(mapRef.current.getZoom(), 15), {
            duration: 0.5,
        });
    }, [selectedIncident, showIncidents]);

    useEffect(() => {
        if (!selectedFeedItem || !mapRef.current || showIncidents) return;
        const [lat, lng] = labelToLatLng(selectedFeedItem.locationLabel);
        mapRef.current.flyTo([lat, lng], Math.max(mapRef.current.getZoom(), 14), { duration: 0.5 });
    }, [selectedFeedItem, showIncidents]);

    function toggleConfirmed(id: string) {
        setConfirmedIds((current) => (current.includes(id) ? current.filter((item) => item !== id) : [...current, id]));
    }

    function toggleResolved(id: string) {
        setResolvedIds((current) => (current.includes(id) ? current.filter((item) => item !== id) : [...current, id]));
    }

    return (
        <section className={styles.announcementMapShell}>
            <div className={styles.announcementMapLayout}>
                <div className={styles.realMapShell}>
                    <div ref={mapContainerRef} className={styles.realMap} />
                    <div className={styles.mapControls}>
                        <button type="button" onClick={() => mapRef.current?.zoomIn()} aria-label="Haritayı yakınlaştır">
                            <Plus size={16} aria-hidden />
                        </button>
                        <button type="button" onClick={() => mapRef.current?.zoomOut()} aria-label="Haritayı uzaklaştır">
                            <Minus size={16} aria-hidden />
                        </button>
                    </div>
                    {showIncidents && visibleIncidents.length === 0 && <div className={styles.mapEmpty}>Bu filtrelere uygun duyuru bulunamadı.</div>}
                    {!showIncidents && (feedItems ?? []).length === 0 && <div className={styles.mapEmpty}>Haritada gösterilecek paylaşım yok.</div>}
                </div>
                <aside className={styles.incidentSidePanel}>
                    {showIncidents ? (
                        selectedIncident ? (
                            <>
                                <div className={styles.incidentSummary}>
                                    <span className={styles.announcementBadge} data-tone={selectedMeta.tone}>{selectedMeta.label}</span>
                                    <h3>{selectedIncident.title}</h3>
                                    <p>{selectedIncident.summary}</p>
                                    <div className={styles.markerMeta}>
                                        <span><MapPin size={14} aria-hidden /> {selectedIncident.locationLabel}</span>
                                        <span>{formatRelative(selectedIncident.createdAt)}</span>
                                    </div>
                                    <div className={styles.markerActions}>
                                        <button type="button" onClick={() => toggleConfirmed(selectedIncident.id)}>
                                            {confirmedIds.includes(selectedIncident.id) ? "Görüldü işaretlendi" : "Ben de görüyorum"}
                                        </button>
                                        <button type="button" onClick={() => toggleResolved(selectedIncident.id)}>
                                            {resolvedIds.includes(selectedIncident.id) ? "Geçerli say" : "Çözüldü / aktif değil"}
                                        </button>
                                    </div>
                                </div>
                                <div className={styles.incidentThread}>
                                    {selectedIncident.updates.map((update) => (
                                        <IncidentUpdateCard key={update.id} update={update} />
                                    ))}
                                    {relatedPosts?.map((post) => (
                                        <button key={post.id} type="button" className={styles.incidentPostLink} onClick={() => onOpenPost?.(post)}>
                                            <strong>{post.authorName}</strong>
                                            <span>{post.title}</span>
                                            <small>{formatRelative(post.createdAt)}</small>
                                        </button>
                                    ))}
                                </div>
                            </>
                        ) : (
                            <p>Haritadaki bir markerı seçerek yerel duyuru başlığını açabilirsin.</p>
                        )
                    ) : (
                        selectedFeedItem ? (
                            <div className={styles.incidentSummary}>
                                <span className={styles.softBadge}>{displayTypeLabel(selectedFeedItem)}</span>
                                <h3>{selectedFeedItem.title}</h3>
                                <p>{selectedFeedItem.body.length > 200 ? `${selectedFeedItem.body.slice(0, 200)}…` : selectedFeedItem.body}</p>
                                <div className={styles.markerMeta}>
                                    <span><MapPin size={14} aria-hidden /> {selectedFeedItem.locationLabel}</span>
                                    <span>{formatRelative(selectedFeedItem.createdAt)}</span>
                                </div>
                                <div className={styles.markerActions}>
                                    <button type="button" onClick={() => onOpenPost?.(selectedFeedItem)}>
                                        Akışta Göster
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <p>Haritadaki bir pini seçerek paylaşım detaylarını görüntüle.</p>
                        )
                    )}
                </aside>
            </div>
        </section>
    );
}

function IncidentUpdateCard({ update }: { update: IncidentUpdateView }) {
    return (
        <article className={styles.incidentUpdateCard}>
            <header>
                <strong>{update.authorName}</strong>
                <small>{formatRelative(update.createdAt)}</small>
            </header>
            <p>{update.body}</p>
            {update.mediaUrl && (
                <div className={styles.incidentMedia}>
                    {isVideoMedia(update.mediaUrl, update.mediaType) ? (
                        <video src={update.mediaUrl} controls muted preload="metadata" />
                    ) : (
                        <img src={update.mediaUrl} alt="" />
                    )}
                </div>
            )}
        </article>
    );
}
