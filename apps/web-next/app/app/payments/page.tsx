"use client";

import { useEffect, useState } from "react";
import { CheckCircle2, CreditCard, XCircle } from "lucide-react";
import { Button } from "@/components/primitives/Button";
import { TextField } from "@/components/product/ProductControls";
import styles from "@/components/product/ProductPages.module.css";
import {
    formatMoneyMinor,
    formatRelative,
    mockAppApi,
    type TransactionView,
} from "@/lib/mockAppClient";

export default function PaymentsPage() {
    const [transactions, setTransactions] = useState<TransactionView[]>([]);
    const [title, setTitle] = useState("Demo hizmet ödeme kaydı");
    const [amount, setAmount] = useState("350");
    const [message, setMessage] = useState<string | null>(null);

    async function load() {
        setTransactions(await mockAppApi.transactions());
    }

    useEffect(() => {
        load();
    }, []);

    async function createIntent() {
        const amountMinor = Math.round(Number(amount.replace(",", ".")) * 100);
        if (!Number.isFinite(amountMinor) || amountMinor <= 0) return;
        await mockAppApi.createPaymentIntent({ title, amountMinor, currency: "TRY" });
        setMessage("Mock ödeme niyeti oluşturuldu.");
        await load();
    }

    async function update(transactionId: string, status: TransactionView["status"]) {
        await mockAppApi.updatePaymentStatus(transactionId, status);
        setMessage(`Mock ödeme ${status.toLocaleLowerCase("tr-TR")} durumuna alındı.`);
        await load();
    }

    return (
        <>
            <section className={styles.intro}>
                <div>
                    <p className={styles.eyebrow}>Ödemeler</p>
                    <h2>Gerçek sağlayıcı yok; ödeme benzeri durumlar Mock BFF test DB’ye yazılır.</h2>
                    <p>
                        Amaç cüzdan, ödeme niyeti, durum geçişi ve işlem geçmişinin web uygulaması tarafından
                        nasıl tüketileceğini erkenden görünür yapmak.
                    </p>
                </div>
            </section>

            {message && <p className={styles.statusBadge} style={{ marginBottom: 14 }}>{message}</p>}

            <section className={styles.layout2}>
                <article className={styles.card}>
                    <div className={styles.cardHeader}>
                        <h2>Mock ödeme niyeti</h2>
                        <CreditCard size={20} aria-hidden />
                    </div>
                    <div className={styles.form}>
                        <TextField label="Başlık" value={title} onChange={setTitle} />
                        <TextField label="Tutar (₺)" value={amount} onChange={setAmount} />
                    </div>
                    <div className={styles.actions}>
                        <Button disabled={!title.trim() || !amount.trim()} onClick={createIntent}>Ödeme niyeti oluştur</Button>
                    </div>
                </article>

                <article className={styles.card}>
                    <div className={styles.cardHeader}>
                        <h2>İşlem geçmişi</h2>
                        <span className={styles.badge}>{transactions.length}</span>
                    </div>
                    <div className={styles.stack}>
                        {transactions.map((transaction) => (
                            <div key={transaction.id} className={styles.feedCard}>
                                <div className={styles.cardHeader}>
                                    <div>
                                        <h3>{transaction.title}</h3>
                                        <p className={styles.muted}>{transaction.description}</p>
                                    </div>
                                    <span className={transaction.status === "COMPLETED" ? styles.statusBadge : styles.badge}>
                                        {transaction.status}
                                    </span>
                                </div>
                                <div className={styles.badgeRow}>
                                    <span className={styles.softBadge}>{formatMoneyMinor(transaction.amountMinor, transaction.currency)}</span>
                                    <span className={styles.softBadge}>{transaction.type}</span>
                                    <span className={styles.softBadge}>{formatRelative(transaction.createdAt)}</span>
                                </div>
                                {transaction.status === "PENDING" && (
                                    <div className={styles.actions}>
                                        <Button
                                            variant="outline"
                                            onClick={() => update(transaction.id, "COMPLETED")}
                                            leadingIcon={<CheckCircle2 size={16} />}
                                        >
                                            Tamamlandı yap
                                        </Button>
                                        <Button
                                            variant="outline"
                                            onClick={() => update(transaction.id, "FAILED")}
                                            leadingIcon={<XCircle size={16} />}
                                        >
                                            Başarısız yap
                                        </Button>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                </article>
            </section>
        </>
    );
}
