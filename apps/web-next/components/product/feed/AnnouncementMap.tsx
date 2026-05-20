"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import { Car, Droplets, Flame, MapPin, Minus, Plus, ShieldAlert, TriangleAlert, Volume2, Wrench, Zap, type LucideIcon } from "lucide-react";
import styles from "@/components/product/ProductPages.module.css";
import { formatRelative, type AnnouncementIncidentView, type FeedItemView, type IncidentUpdateView } from "@/lib/mockAppClient";
import { announcementMeta, isVideoMedia, normalizeAnnouncementKind, type AnnouncementKind } from "./feedModel";

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

export function AnnouncementMap({
    incidents,
    postsById,
    selectedKinds,
    onOpenPost,
}: {
    incidents: AnnouncementIncidentView[];
    postsById: Map<string, FeedItemView>;
    selectedKinds: AnnouncementKind[];
    onOpenPost?: (item: FeedItemView) => void;
}) {
    const mapContainerRef = useRef<HTMLDivElement | null>(null);
    const mapRef = useRef<LeafletMap | null>(null);
    const markersRef = useRef<LeafletMarker[]>([]);
    const leafletRef = useRef<LeafletRuntime | null>(null);
    const [selectedId, setSelectedId] = useState<string | null>(incidents[0]?.id ?? null);
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
        markersRef.current = visibleIncidents.map((incident) => {
            const kind = normalizeAnnouncementKind(incident.kind) ?? "POWER_OUTAGE";
            const meta = announcementMeta(kind);
            const marker = L.marker([incident.latitude, incident.longitude], {
                icon: L.divIcon({
                    className: "",
                    html: `<span class="${styles.leafletIncidentMarker}" data-tone="${meta.tone}">${meta.shortLabel.slice(0, 1)}</span>`,
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
    }, [mapReady, visibleIncidents]);

    useEffect(() => {
        if (!selectedIncident || !mapRef.current) return;
        mapRef.current.flyTo([selectedIncident.latitude, selectedIncident.longitude], Math.max(mapRef.current.getZoom(), 15), {
            duration: 0.5,
        });
    }, [selectedIncident]);

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
                    {visibleIncidents.length === 0 && <div className={styles.mapEmpty}>Bu filtrelere uygun duyuru bulunamadı.</div>}
                </div>
                <aside className={styles.incidentSidePanel}>
                    {selectedIncident ? (
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
