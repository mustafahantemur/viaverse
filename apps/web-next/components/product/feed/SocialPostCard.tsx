"use client";

import { useState } from "react";
import { MoreHorizontal, Send } from "lucide-react";
import { CompactPostActions } from "@/components/product/feed/CompactPostActions";
import { ListingCard, listingCardStyles as lc } from "@/components/product/ListingCard";
import {
    announcementKindFor,
    announcementMeta,
    displayTypeLabel,
    initials,
    isVideoMedia,
} from "@/components/product/feed/feedModel";
import {
    formatRelative,
    mockAppApi,
    type FeedItemView,
    type PostCommentView,
} from "@/lib/mockAppClient";
import styles from "@/components/product/ProductPages.module.css";

type Props = {
    item: FeedItemView;
    currentUserName: string;
    focused: boolean;
    onEdit: (item: FeedItemView) => void;
    onPostChanged: (item: FeedItemView) => void;
};

export function SocialPostCard({ item, currentUserName, focused, onEdit, onPostChanged }: Props) {
    const [commentsOpen, setCommentsOpen] = useState(false);
    const [comments, setComments] = useState<PostCommentView[]>([]);
    const [commentDraft, setCommentDraft] = useState("");
    const [busy, setBusy] = useState(false);
    const [menuOpen, setMenuOpen] = useState(false);

    const ownPost = item.authorName === currentUserName;
    const announcementKind = announcementKindFor(item);
    const announcement = announcementMeta(announcementKind);

    async function openComments() {
        const nextOpen = !commentsOpen;
        setCommentsOpen(nextOpen);
        if (nextOpen) setComments(await mockAppApi.comments(item.id));
    }

    async function like() { onPostChanged(await mockAppApi.likePost(item.id)); }
    async function save() { onPostChanged(await mockAppApi.savePost(item.id)); }
    async function share() { onPostChanged(await mockAppApi.sharePost(item.id)); }

    async function comment() {
        if (!commentDraft.trim()) return;
        setBusy(true);
        try {
            await mockAppApi.createComment(item.id, commentDraft);
            setCommentDraft("");
            const [nextPost, nextComments] = await Promise.all([
                mockAppApi.feed("SOCIAL").then((items) => items.find((p) => p.id === item.id) ?? item),
                mockAppApi.comments(item.id),
            ]);
            onPostChanged(nextPost);
            setComments(nextComments);
        } finally {
            setBusy(false);
        }
    }

    const titleAside = (
        <>
            {announcementKind ? (
                <span className={styles.announcementBadge} data-tone={announcement.tone}>
                    {announcement.shortLabel}
                </span>
            ) : (
                <span className={styles.softBadge}>{displayTypeLabel(item)}</span>
            )}
            {ownPost && (
                <div className={styles.postMenu}>
                    <button
                        type="button"
                        className={styles.postMenuButton}
                        onClick={() => setMenuOpen((v) => !v)}
                        aria-label="Paylaşım seçenekleri"
                    >
                        <MoreHorizontal size={18} aria-hidden />
                    </button>
                    {menuOpen && (
                        <div className={styles.postMenuDropdown}>
                            <button type="button" onClick={() => { setMenuOpen(false); onEdit(item); }}>
                                Düzenle
                            </button>
                        </div>
                    )}
                </div>
            )}
        </>
    );

    return (
        <ListingCard
            id={`post-${item.id}`}
            hoverable
            focused={focused}
            avatar={<span className={lc.avatar}>{initials(item.authorName)}</span>}
            title={item.authorName}
            titleAside={titleAside}
            subtitle={`${item.locationLabel} · ${formatRelative(item.createdAt)}`}
            media={item.mediaUrl ? (
                isVideoMedia(item.mediaUrl, item.mediaType)
                    ? <video src={item.mediaUrl} controls muted preload="metadata" />
                    : <img src={item.mediaUrl} alt="" />
            ) : undefined}
            description={(
                <>
                    <h3>{item.title}</h3>
                    <p>{item.body}</p>
                </>
            )}
            chips={item.hashtags?.length > 0
                ? item.hashtags.map((tag) => <span key={tag} className={lc.chip}>#{tag}</span>)
                : undefined}
            footerFlush
            footer={(
                <CompactPostActions
                    liked={item.liked}
                    saved={item.saved}
                    likeCount={item.likeCount}
                    commentCount={item.commentCount}
                    shareCount={item.shareCount}
                    onLike={like}
                    onComment={openComments}
                    onShare={share}
                    onSave={save}
                />
            )}
            below={commentsOpen ? (
                <div className={styles.commentPanel}>
                    {comments.map((c) => (
                        <div key={c.id} className={styles.commentItem}>
                            <strong>{c.authorName}</strong>
                            <span>{c.body}</span>
                            <small>{formatRelative(c.createdAt)}</small>
                        </div>
                    ))}
                    <div className={styles.commentComposer}>
                        <input
                            value={commentDraft}
                            onChange={(e) => setCommentDraft(e.target.value)}
                            placeholder="Yorum yaz"
                        />
                        <button type="button" disabled={busy || !commentDraft.trim()} onClick={comment}>
                            <Send size={16} aria-hidden />
                        </button>
                    </div>
                </div>
            ) : undefined}
        />
    );
}
