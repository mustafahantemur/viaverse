"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import { Car, Droplets, Flame, Image, MapPinned, ShieldAlert, TriangleAlert, Upload, Video, Volume2, Wrench, X, Zap, type LucideIcon } from "lucide-react";
import { Button } from "@/components/primitives/Button";
import { TextField } from "@/components/product/ProductControls";
import styles from "@/components/product/ProductPages.module.css";
import { mockAppApi, type FeedItemView, type HashtagSuggestionView } from "@/lib/mockAppClient";
import { LocationAutocomplete } from "./LocationAutocomplete";
import { TagAutocomplete } from "./TagAutocomplete";
import {
    announcementKindFor,
    announcementMeta,
    announcementTypes,
    cleanTagInput,
    isVideoMedia,
    type AnnouncementKind,
} from "./feedModel";

type ComposerType = "POST" | "ANNOUNCEMENT" | "EVENT";

const typeOptions: Array<{ value: ComposerType; label: string; helper: string }> = [
    { value: "POST", label: "Genel", helper: "Günlük paylaşım, soru veya gözlem" },
    { value: "ANNOUNCEMENT", label: "Duyuru", helper: "Yakındaki kritik veya faydalı durum" },
    { value: "EVENT", label: "Etkinlik", helper: "Katılım ve tarih odaklı paylaşım" },
];

const announcementIcons = {
    POWER_OUTAGE: Zap,
    WATER_OUTAGE: Droplets,
    TRAFFIC: Car,
    FIRE: Flame,
    ACCIDENT: TriangleAlert,
    NOISE: Volume2,
    SAFETY: ShieldAlert,
    MUNICIPALITY: Wrench,
    LOST: MapPinned,
} satisfies Record<AnnouncementKind, LucideIcon>;

const mapPoints = [
    { id: "point-center", label: "Merkez sokak", left: 52, top: 45 },
    { id: "point-park", label: "Park çevresi", left: 28, top: 58 },
    { id: "point-main", label: "Ana cadde", left: 70, top: 31 },
    { id: "point-shore", label: "Sahil hattı", left: 37, top: 24 },
];

export function CreatePostModal({
    open,
    currentUserInitials,
    currentUserName,
    defaultLocation,
    initialType = "POST",
    editingPost,
    onClose,
    onSaved,
}: {
    open: boolean;
    currentUserInitials: string;
    currentUserName: string;
    defaultLocation: string;
    initialType?: ComposerType;
    editingPost?: FeedItemView | null;
    onClose: () => void;
    onSaved: (item: FeedItemView) => void;
}) {
    const [composerType, setComposerType] = useState<ComposerType>("POST");
    const [announcementKind, setAnnouncementKind] = useState<AnnouncementKind>("POWER_OUTAGE");
    const [title, setTitle] = useState("");
    const [body, setBody] = useState("");
    const [location, setLocation] = useState("");
    const [scopeMode, setScopeMode] = useState<"EXACT" | "APPROXIMATE">("APPROXIMATE");
    const [selectedPointId, setSelectedPointId] = useState<string | null>(null);
    const [tagDraft, setTagDraft] = useState("");
    const [selectedTags, setSelectedTags] = useState<string[]>([]);
    const [suggestions, setSuggestions] = useState<HashtagSuggestionView[]>([]);
    const [selectedMedia, setSelectedMedia] = useState<{ url: string; type: "IMAGE" | "VIDEO"; name: string } | null>(null);
    const [mediaNotice, setMediaNotice] = useState<string | null>(null);
    const [busy, setBusy] = useState(false);
    const mediaInputRef = useRef<HTMLInputElement | null>(null);

    const selectedPoint = mapPoints.find((point) => point.id === selectedPointId) ?? null;
    const announcement = useMemo(() => announcementMeta(announcementKind), [announcementKind]);

    useEffect(() => {
        if (!open) return;
        if (editingPost) {
            const isAnnouncement = ["ANNOUNCEMENT", "TRAFFIC", "UTILITY"].includes(editingPost.type);
            setComposerType(isAnnouncement ? "ANNOUNCEMENT" : editingPost.type === "EVENT" ? "EVENT" : "POST");
            setAnnouncementKind(announcementKindFor(editingPost) ?? "POWER_OUTAGE");
            setTitle(editingPost.title);
            setBody(editingPost.body);
            setLocation(editingPost.locationLabel);
            setSelectedTags(editingPost.hashtags ?? []);
            setSelectedMedia(editingPost.mediaUrl ? {
                url: editingPost.mediaUrl,
                type: isVideoMedia(editingPost.mediaUrl, editingPost.mediaType) ? "VIDEO" : "IMAGE",
                name: "Mevcut medya",
            } : null);
            setMediaNotice(null);
            setSelectedPointId(null);
            setScopeMode("APPROXIMATE");
            setTagDraft("");
            return;
        }
        setComposerType(initialType);
        setAnnouncementKind("POWER_OUTAGE");
        setTitle("");
        setBody("");
        setLocation(defaultLocation);
        setSelectedTags([]);
        setSelectedMedia(null);
        setMediaNotice(null);
        setSelectedPointId(null);
        setScopeMode("APPROXIMATE");
        setTagDraft("");
    }, [defaultLocation, editingPost, initialType, open]);

    useEffect(() => {
        if (!open) return;
        let cancelled = false;
        async function loadSuggestions() {
            const next = await mockAppApi.hashtagSuggestions(tagDraft.trim() || undefined);
            if (!cancelled) setSuggestions(next);
        }
        loadSuggestions();
        return () => {
            cancelled = true;
        };
    }, [open, tagDraft]);

    function addTag(tag: string) {
        const cleanTag = cleanTagInput(tag);
        if (!cleanTag) return;
        setSelectedTags((current) => (current.includes(cleanTag) ? current : [...current, cleanTag]));
        setTagDraft("");
    }

    function removeTag(tag: string) {
        setSelectedTags((current) => current.filter((item) => item !== tag));
    }

    function selectPoint(pointId: string) {
        const point = mapPoints.find((item) => item.id === pointId);
        if (!point) return;
        setSelectedPointId(point.id);
        setLocation(`${point.label}, ${defaultLocation || "yakın çevre"}`);
    }

    function postTypeForSubmit(): FeedItemView["type"] {
        if (composerType === "EVENT") return "EVENT";
        if (composerType === "ANNOUNCEMENT") {
            if (announcementKind === "TRAFFIC") return "TRAFFIC";
            if (["POWER_OUTAGE", "WATER_OUTAGE", "MUNICIPALITY"].includes(announcementKind)) return "UTILITY";
            return "ANNOUNCEMENT";
        }
        return "POST";
    }

    function onMediaSelected(file: File | null) {
        setMediaNotice(null);
        if (!file) return;
        const mediaType = file.type.startsWith("video/") ? "VIDEO" : file.type.startsWith("image/") ? "IMAGE" : null;
        if (!mediaType) {
            setMediaNotice("Yalnızca görsel veya en fazla 1 dakikalık kısa video eklenebilir.");
            return;
        }
        const reader = new FileReader();
        reader.onload = () => {
            if (typeof reader.result !== "string") return;
            if (mediaType === "VIDEO") {
                const video = document.createElement("video");
                video.preload = "metadata";
                video.onloadedmetadata = () => {
                    window.URL.revokeObjectURL(video.src);
                    if (video.duration > 60) {
                        setMediaNotice("Video 1 dakikadan uzun olmamalı.");
                        return;
                    }
                    setSelectedMedia({ url: reader.result as string, type: "VIDEO", name: file.name });
                };
                video.onerror = () => {
                    window.URL.revokeObjectURL(video.src);
                    setSelectedMedia({ url: reader.result as string, type: "VIDEO", name: file.name });
                };
                video.src = URL.createObjectURL(file);
                return;
            }
            setSelectedMedia({ url: reader.result, type: "IMAGE", name: file.name });
        };
        reader.readAsDataURL(file);
    }

    async function submit() {
        if (!body.trim()) return;
        setBusy(true);
        try {
            const payload = {
                type: postTypeForSubmit(),
                title: title.trim() || (composerType === "ANNOUNCEMENT" ? `${announcement.label} bildirimi` : ""),
                body,
                categoryId: composerType === "ANNOUNCEMENT" ? `announcement-${announcementKind.toLowerCase()}` : "local-info",
                locationScope: location.trim() || defaultLocation,
                hashtags: selectedTags,
                mediaUrl: selectedMedia?.url,
                mediaType: selectedMedia?.type,
            };
            const saved = editingPost
                ? await mockAppApi.updatePost(editingPost.id, payload)
                : await mockAppApi.createPost(payload);
            onSaved(saved);
            onClose();
        } finally {
            setBusy(false);
        }
    }

    if (!open) return null;

    return (
        <div className={styles.modalOverlay} role="presentation" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
            <section className={styles.createPostModal} role="dialog" aria-modal="true" aria-label={editingPost ? "Paylaşımı düzenle" : "Paylaşım oluştur"}>
                <header className={styles.modalHeader}>
                    <div className={styles.modalAuthor}>
                        <span>{currentUserInitials}</span>
                        <div>
                            <strong>{editingPost ? "Paylaşımı düzenle" : "Bir şey paylaş"}</strong>
                            <small>{currentUserName} olarak yayınlanır</small>
                        </div>
                    </div>
                    <button type="button" onClick={onClose} aria-label="Pencereyi kapat">
                        <X size={18} aria-hidden />
                    </button>
                </header>

                <div className={styles.composerTypeGrid}>
                    {typeOptions.map((option) => (
                        <button
                            key={option.value}
                            type="button"
                            className={composerType === option.value ? styles.composerTypeActive : ""}
                            onClick={() => setComposerType(option.value)}
                        >
                            <strong>{option.label}</strong>
                            <span>{option.helper}</span>
                        </button>
                    ))}
                </div>

                {composerType === "ANNOUNCEMENT" && (
                    <section className={styles.announcementComposer}>
                        <div className={styles.announcementTypeGrid}>
                            {announcementTypes.map((item) => {
                                const Icon = announcementIcons[item.kind];
                                const active = announcementKind === item.kind;
                                return (
                                    <button
                                        key={item.kind}
                                        type="button"
                                        className={[styles.announcementTypeChip, active && styles.announcementTypeChipActive].filter(Boolean).join(" ")}
                                        data-tone={item.tone}
                                        onClick={() => setAnnouncementKind(item.kind)}
                                    >
                                        <Icon size={16} aria-hidden />
                                        <span>{item.shortLabel}</span>
                                    </button>
                                );
                            })}
                        </div>
                        <div className={styles.mapPicker}>
                            <div className={styles.mockMap} aria-label="Duyuru konumu seç">
                                <span className={styles.mapRoad} />
                                <span className={styles.mapRoadAlt} />
                                {mapPoints.map((point) => (
                                    <button
                                        key={point.id}
                                        type="button"
                                        className={selectedPointId === point.id ? styles.mapPickerPointActive : styles.mapPickerPoint}
                                        style={{ left: `${point.left}%`, top: `${point.top}%` }}
                                        onClick={() => selectPoint(point.id)}
                                        aria-label={`${point.label} noktasını seç`}
                                    />
                                ))}
                            </div>
                            <div className={styles.mapPickerDetails}>
                                <strong>{selectedPoint ? selectedPoint.label : "Haritadan nokta seç"}</strong>
                                <span>{selectedPoint ? `${location} otomatik dolduruldu gibi gösteriliyor.` : "Gerçek harita servisi bağlandığında burası koordinat ve adres çözümlemesi alacak."}</span>
                                <div className={styles.segmented}>
                                    <button type="button" className={scopeMode === "EXACT" ? styles.segmentActive : ""} onClick={() => setScopeMode("EXACT")}>
                                        Tam burada
                                    </button>
                                    <button type="button" className={scopeMode === "APPROXIMATE" ? styles.segmentActive : ""} onClick={() => setScopeMode("APPROXIMATE")}>
                                        Yaklaşık bölgede
                                    </button>
                                </div>
                            </div>
                        </div>
                    </section>
                )}

                <div className={styles.modalFormGrid}>
                    <TextField label="Başlık" value={title} onChange={setTitle} placeholder={composerType === "ANNOUNCEMENT" ? announcement.label : "Kısa ve anlaşılır bir başlık"} />
                    <TextField
                        label="Metin"
                        value={body}
                        onChange={setBody}
                        textarea
                        placeholder={composerType === "ANNOUNCEMENT" ? "Ne oldu, ne kadar güncel, kimleri etkiliyor?" : "Paylaşımını yaz. Etiketleri ayrı alandan ekleyebilirsin."}
                    />
                    <TagAutocomplete
                        label="Etiketler"
                        value={tagDraft}
                        onValueChange={setTagDraft}
                        selectedTags={selectedTags}
                        suggestions={suggestions}
                        onSelect={addTag}
                        onRemove={removeTag}
                        placeholder="Etiket ara veya yeni etiket yaz"
                    />
                    <div className={styles.mediaAttachmentField}>
                        <span className={styles.inlineFilterLabel}>Medya</span>
                        <input
                            ref={mediaInputRef}
                            type="file"
                            accept="image/*,video/*"
                            className={styles.hiddenInput}
                            onChange={(event) => onMediaSelected(event.target.files?.[0] ?? null)}
                        />
                        {selectedMedia ? (
                            <div className={styles.mediaAttachmentPreview}>
                                {selectedMedia.type === "VIDEO" ? (
                                    <video src={selectedMedia.url} muted controls preload="metadata" />
                                ) : (
                                    <img src={selectedMedia.url} alt="" />
                                )}
                                <div>
                                    <span>{selectedMedia.type === "VIDEO" ? <Video size={15} aria-hidden /> : <Image size={15} aria-hidden />} {selectedMedia.name}</span>
                                    <button type="button" onClick={() => setSelectedMedia(null)}>Kaldır</button>
                                </div>
                            </div>
                        ) : (
                            <button type="button" className={styles.mediaAttachmentButton} onClick={() => mediaInputRef.current?.click()}>
                                <Upload size={17} aria-hidden />
                                <span>Görsel veya en fazla 1 dakikalık video ekle</span>
                            </button>
                        )}
                        {mediaNotice && <small className={styles.fieldHint}>{mediaNotice}</small>}
                    </div>
                    <LocationAutocomplete
                        label="Konum"
                        value={location}
                        onValueChange={setLocation}
                        placeholder="Konum seç veya yaz"
                    />
                </div>

                <footer className={styles.modalActions}>
                    <Button variant="outline" onClick={onClose}>Vazgeç</Button>
                    <Button disabled={busy || !body.trim()} onClick={submit}>
                        {busy ? "Kaydediliyor..." : editingPost ? "Güncelle" : "Yayınla"}
                    </Button>
                </footer>
            </section>
        </div>
    );
}
