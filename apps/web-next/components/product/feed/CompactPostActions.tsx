"use client";

import { Bookmark, Heart, MessageCircle, Share2 } from "lucide-react";
import styles from "@/components/product/ProductPages.module.css";

export function CompactPostActions({
    liked,
    saved,
    likeCount,
    commentCount,
    shareCount,
    onLike,
    onComment,
    onShare,
    onSave,
}: {
    liked: boolean;
    saved: boolean;
    likeCount: number;
    commentCount: number;
    shareCount: number;
    onLike: () => void;
    onComment: () => void;
    onShare: () => void;
    onSave: () => void;
}) {
    return (
        <div className={styles.compactPostActions}>
            <button type="button" onClick={onLike} className={liked ? styles.postActionActive : ""} aria-label={liked ? "Beğeniyi kaldır" : "Beğen"}>
                <Heart size={18} fill={liked ? "currentColor" : "none"} aria-hidden />
                <span>{likeCount}</span>
            </button>
            <button type="button" onClick={onComment} aria-label="Yorumları aç">
                <MessageCircle size={18} aria-hidden />
                <span>{commentCount}</span>
            </button>
            <button type="button" onClick={onShare} aria-label="Paylaş">
                <Share2 size={18} aria-hidden />
                <span>{shareCount}</span>
            </button>
            <button type="button" onClick={onSave} className={saved ? styles.postActionActive : ""} aria-label={saved ? "Kaydı kaldır" : "Kaydet"}>
                <Bookmark size={18} fill={saved ? "currentColor" : "none"} aria-hidden />
            </button>
        </div>
    );
}
