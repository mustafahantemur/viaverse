"use client";

import { useEffect, useState } from "react";
import { Send } from "lucide-react";
import { Button } from "@/components/primitives/Button";
import { TextField } from "@/components/product/ProductControls";
import styles from "@/components/product/ProductPages.module.css";
import { useAppSession } from "@/components/product/ProductAppShell";
import {
    formatDateTime,
    formatRelative,
    mockAppApi,
    type ConversationView,
    type MessageView,
} from "@/lib/mockAppClient";

export default function MessagesPage() {
    const { session } = useAppSession();
    const [conversations, setConversations] = useState<ConversationView[]>([]);
    const [selectedId, setSelectedId] = useState<string | null>(null);
    const [messages, setMessages] = useState<MessageView[]>([]);
    const [draft, setDraft] = useState("");
    const [editingId, setEditingId] = useState<string | null>(null);
    const [editDraft, setEditDraft] = useState("");
    const selected = conversations.find((conversation) => conversation.id === selectedId) ?? null;

    async function loadConversations() {
        const next = await mockAppApi.conversations();
        setConversations(next);
        setSelectedId((current) => current ?? next[0]?.id ?? null);
    }

    async function loadMessages(conversationId: string) {
        setMessages(await mockAppApi.messages(conversationId));
    }

    useEffect(() => {
        loadConversations();
    }, []);

    useEffect(() => {
        if (selectedId) loadMessages(selectedId);
    }, [selectedId]);

    async function send() {
        if (!selectedId || !draft.trim()) return;
        await mockAppApi.sendMessage(selectedId, draft);
        setDraft("");
        await Promise.all([loadConversations(), loadMessages(selectedId)]);
    }

    async function saveEdit() {
        if (!selectedId || !editingId || !editDraft.trim()) return;
        await mockAppApi.updateMessage(selectedId, editingId, editDraft);
        setEditingId(null);
        setEditDraft("");
        await Promise.all([loadConversations(), loadMessages(selectedId)]);
    }

    return (
        <>
            <section className={styles.intro}>
                <div>
                    <p className={styles.eyebrow}>Mesajlar</p>
                    <h2>Kabul edilen teklif ve doğrudan ilgi akışları konuşmaya dönüşür.</h2>
                    <p>
                        Gerçek zamanlı chat yok; ama konuşma, mesaj, ilgili talep ve ilgili teklif ilişkisi
                        BFF kontratında hazır.
                    </p>
                </div>
            </section>

            <section className={styles.conversationLayout}>
                <article className={styles.card}>
                    <div className={styles.cardHeader}>
                        <h2>Gelen kutusu</h2>
                        <span className={styles.badge}>{conversations.length}</span>
                    </div>
                    <div className={styles.stack}>
                        {conversations.map((conversation) => (
                            <button
                                key={conversation.id}
                                type="button"
                                className={styles.feedCard}
                                onClick={() => setSelectedId(conversation.id)}
                                style={{ textAlign: "left", cursor: "pointer" }}
                            >
                                <div className={styles.cardHeader}>
                                    <div>
                                        <h3>{conversation.title}</h3>
                                        <p className={styles.muted}>{conversation.lastMessage}</p>
                                    </div>
                                    {conversation.unreadCount > 0 && <span className={styles.badge}>{conversation.unreadCount}</span>}
                                </div>
                                <div className={styles.badgeRow}>
                                    <span className={styles.softBadge}>{conversation.contextLabel}</span>
                                    <span className={styles.softBadge}>{conversation.participantType}</span>
                                    <span className={styles.softBadge}>{formatRelative(conversation.lastMessageAt)}</span>
                                </div>
                            </button>
                        ))}
                    </div>
                </article>

                <article className={[styles.card, styles.messageThread].join(" ")}>
                    {selected ? (
                        <>
                            <div className={styles.cardHeader}>
                                <div>
                                    <h2>{selected.title}</h2>
                                    <p className={styles.muted}>{selected.participantName} · {selected.contextLabel}</p>
                                </div>
                                <span className={styles.statusBadge}>{selected.participantType}</span>
                            </div>
                            <div className={styles.messages}>
                                {messages.map((message) => {
                                    const mine = message.senderId === session.currentUser.id;
                                    return (
                                        <div
                                            key={message.id}
                                            className={[
                                                styles.message,
                                                mine && styles.messageMine,
                                                message.system && styles.messageSystem,
                                            ].filter(Boolean).join(" ")}
                                        >
                                            <strong>{message.senderName}</strong>
                                            {editingId === message.id ? (
                                                <div className={styles.messageEditRow}>
                                                    <TextField label="Mesajı düzenle" value={editDraft} onChange={setEditDraft} />
                                                    <div className={styles.actions}>
                                                        <Button onClick={saveEdit} disabled={!editDraft.trim()}>Kaydet</Button>
                                                        <Button variant="outline" onClick={() => setEditingId(null)}>Vazgeç</Button>
                                                    </div>
                                                </div>
                                            ) : (
                                                <p className={styles.muted}>{message.body}</p>
                                            )}
                                            <div className={styles.messageMeta}>
                                                <span className={styles.softBadge}>{formatDateTime(message.createdAt)}</span>
                                                {message.updatedAt !== message.createdAt && (
                                                    <span className={styles.softBadge}>düzenlendi</span>
                                                )}
                                                <span className={styles.softBadge}>{formatRelative(message.createdAt)}</span>
                                                {!message.system && mine && editingId !== message.id && (
                                                    <button
                                                        type="button"
                                                        className={styles.messageEditButton}
                                                        onClick={() => {
                                                            setEditingId(message.id);
                                                            setEditDraft(message.body);
                                                        }}
                                                    >
                                                        Düzenle
                                                    </button>
                                                )}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                            <div className={styles.form} style={{ marginTop: 12 }}>
                                <TextField label="Mesaj" value={draft} onChange={setDraft} placeholder="Yanıt yaz" />
                                <div className={styles.actions}>
                                    <Button disabled={!draft.trim()} onClick={send} leadingIcon={<Send size={16} />}>Gönder</Button>
                                </div>
                            </div>
                        </>
                    ) : (
                        <div className={styles.empty}>Konuşma seç.</div>
                    )}
                </article>
            </section>
        </>
    );
}
