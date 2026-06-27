"use client";

import type { SponsoredAdView } from "@/lib/mockAppClient";
import styles from "@/components/product/ProductPages.module.css";

export function FeedAdsRail({ ads }: { ads: SponsoredAdView[] }) {
    return (
        <aside className={styles.feedAdsRail} aria-label="Sponsorlu alanlar">
            {ads.slice(0, 2).map((ad) => (
                <article key={ad.id} className={styles.stickyAdCard}>
                    <img src={ad.imageUrl} alt="" />
                    <div>
                        <small>{ad.advertiser}</small>
                        <strong>{ad.title}</strong>
                        <p>{ad.body}</p>
                        <span>{ad.displayUrl}</span>
                    </div>
                </article>
            ))}
        </aside>
    );
}
