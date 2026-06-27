"use client";

import type { ReactNode } from "react";
import styles from "./ListingCard.module.css";

export type ListingCardProps = {
    /** wrapper id (e.g. for scroll-into-view) */
    id?: string;
    onClick?: () => void;
    /** Makes the whole card a clickable button (cursor + keyboard + hover lift). */
    interactive?: boolean;
    /** Applies the hover lift + border highlight without button semantics (e.g. feed cards). */
    hoverable?: boolean;
    selected?: boolean;
    focused?: boolean;
    className?: string;

    /** Media block on top (img/video). Omit for text-only cards. */
    media?: ReactNode;
    /** Overlay badges rendered over the media. */
    mediaBadges?: ReactNode;

    /** Small circular avatar to the left of the header (feed posts). */
    avatar?: ReactNode;
    title: ReactNode;
    /** Right side of the title row (rating, type badge, menu). */
    titleAside?: ReactNode;
    subtitle?: ReactNode;
    /** Clamp the subtitle to two lines (listing headlines). */
    clampSubtitle?: boolean;

    /** Free body content (e.g. a post's title + text). */
    description?: ReactNode;
    /** Inline meta row (price · distance · response time). */
    meta?: ReactNode;
    /** Pill chips (categories / hashtags). */
    chips?: ReactNode;

    footer?: ReactNode;
    /** Footer without a top divider (when actions already feel attached). */
    footerFlush?: boolean;

    /** Content rendered below the card (e.g. a comment panel). */
    below?: ReactNode;
};

export function ListingCard({
    id, onClick, interactive, hoverable, selected, focused, className,
    media, mediaBadges,
    avatar, title, titleAside, subtitle, clampSubtitle,
    description, meta, chips,
    footer, footerFlush,
    below,
}: ListingCardProps) {
    const cardClass = [
        styles.card,
        interactive && styles.interactive,
        (interactive || hoverable) && styles.hoverable,
        selected && styles.selected,
        focused && styles.focused,
        className,
    ].filter(Boolean).join(" ");

    return (
        <div id={id} className={styles.wrapper}>
            <article
                className={cardClass}
                onClick={interactive ? onClick : undefined}
                role={interactive ? "button" : undefined}
                tabIndex={interactive ? 0 : undefined}
                onKeyDown={interactive ? (e) => { if (e.key === "Enter") onClick?.(); } : undefined}
            >
                {media && (
                    <div className={styles.media}>
                        {media}
                        {mediaBadges}
                    </div>
                )}

                <div className={styles.body}>
                    <div className={styles.header}>
                        {avatar}
                        <div className={styles.headerMain}>
                            <div className={styles.titleRow}>
                                <h3 className={styles.title}>{title}</h3>
                                {titleAside && <div className={styles.titleAside}>{titleAside}</div>}
                            </div>
                            {subtitle && (
                                <p className={[styles.subtitle, clampSubtitle && styles.clampSubtitle].filter(Boolean).join(" ")}>
                                    {subtitle}
                                </p>
                            )}
                        </div>
                    </div>

                    {description && <div className={styles.description}>{description}</div>}
                    {meta && <div className={styles.meta}>{meta}</div>}
                    {chips && <div className={styles.chips}>{chips}</div>}
                </div>

                {footer && (
                    <div className={[styles.footer, footerFlush && styles.footerFlush].filter(Boolean).join(" ")}>
                        {footer}
                    </div>
                )}
            </article>
            {below}
        </div>
    );
}

/** Exposed so callers can reuse the canonical badge / chip / rating styles. */
export const listingCardStyles = styles;
