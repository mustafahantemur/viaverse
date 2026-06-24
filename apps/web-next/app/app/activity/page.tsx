"use client";

import { useEffect, useState } from "react";
import { BellRing } from "lucide-react";
import styles from "@/components/product/ProductPages.module.css";
import {
    formatRelative,
    mockAppApi,
    type NotificationView,
} from "@/lib/mockAppClient";

export default function ActivityPage() {
    const [notifications, setNotifications] = useState<NotificationView[]>([]);

    useEffect(() => {
        mockAppApi.notifications().then(setNotifications);
    }, []);

    return (
        <>
            <section className={styles.intro}>
                <div>
                    <p className={styles.eyebrow}>Aktivite</p>
                    <h2>Bildirimler ve ürün olayları tek yüzeyde.</h2>
                    <p>
                        Bu mock alan teklif, etkinlik, ödeme ve mesaj olaylarının kullanıcıya nasıl döneceğini
                        gösterir; gerçek notification domain kararları için erken sinyal verir.
                    </p>
                </div>
            </section>

            <section className={styles.grid2}>
                {notifications.map((notification) => (
                    <article key={notification.id} className={styles.card}>
                        <div className={styles.cardHeader}>
                            <div>
                                <h2>{notification.title}</h2>
                                <p className={styles.muted}>{notification.body}</p>
                            </div>
                            <BellRing size={20} aria-hidden />
                        </div>
                        <div className={styles.badgeRow}>
                            <span className={notification.read ? styles.softBadge : styles.badge}>
                                {notification.read ? "Okundu" : "Yeni"}
                            </span>
                            <span className={styles.softBadge}>{notification.type}</span>
                            <span className={styles.softBadge}>{formatRelative(notification.createdAt)}</span>
                        </div>
                    </article>
                ))}
            </section>
        </>
    );
}
