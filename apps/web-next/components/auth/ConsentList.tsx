"use client";

import type { RequiredConsents, RequiredConsentDocument } from "@/lib/authClient";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import type { Locale } from "@/lib/i18n/messages";

interface Props {
    consents: RequiredConsents;
    accepted: Record<string, boolean>;
    onToggle: (type: string, accepted: boolean) => void;
    marketingAccepted: boolean;
    onMarketingToggle: (accepted: boolean) => void;
}

/**
 * Renders the legal documents the user must accept to register, plus the
 * optional marketing opt-in. Each checkbox label links to the legal page
 * so the user can read the actual document; labels are localized via the
 * active locale.
 */
export function ConsentList({
    consents,
    accepted,
    onToggle,
    marketingAccepted,
    onMarketingToggle,
}: Props) {
    const { locale, t } = useTranslation();
    return (
        <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
            <span
                style={{
                    fontSize: 12,
                    color: "var(--vv-fg-muted)",
                    lineHeight: 1.4,
                }}
            >
                {t.auth.consents.heading}
            </span>
            {consents.required.map((doc) => (
                <ConsentRow
                    key={doc.type}
                    doc={doc}
                    locale={locale}
                    accepted={!!accepted[doc.type]}
                    onChange={(value) => onToggle(doc.type, value)}
                    viewLabel={t.auth.consents.view}
                />
            ))}
            <ConsentRow
                doc={consents.marketing}
                locale={locale}
                accepted={marketingAccepted}
                onChange={onMarketingToggle}
                optional
                optionalLabel={t.common.optional}
                viewLabel={t.auth.consents.view}
                marketingOverride={t.auth.consents.marketingLabel}
            />
        </div>
    );
}

function ConsentRow({
    doc,
    locale,
    accepted,
    onChange,
    optional = false,
    optionalLabel,
    viewLabel,
    marketingOverride,
}: {
    doc: RequiredConsentDocument;
    locale: Locale;
    accepted: boolean;
    onChange: (accepted: boolean) => void;
    optional?: boolean;
    optionalLabel?: string;
    viewLabel: string;
    marketingOverride?: string;
}) {
    return (
        <label
            style={{
                display: "flex",
                gap: 10,
                alignItems: "flex-start",
                fontSize: 13,
                lineHeight: 1.5,
                color: "var(--vv-fg)",
                cursor: "pointer",
            }}
        >
            <input
                type="checkbox"
                checked={accepted}
                onChange={(event) => onChange(event.target.checked)}
                style={{ marginTop: 3, accentColor: "var(--vv-primary)" }}
            />
            <span>
                {optional && optionalLabel && (
                    <span
                        style={{
                            display: "inline-block",
                            fontSize: 10,
                            fontWeight: 800,
                            color: "var(--vv-fg-muted)",
                            textTransform: "uppercase",
                            letterSpacing: "0.12em",
                            marginRight: 6,
                        }}
                    >
                        {optionalLabel}
                    </span>
                )}
                {marketingOverride ?? labelFor(doc, locale)}{" "}
                <a
                    href={doc.url}
                    target="_blank"
                    rel="noreferrer"
                    style={{
                        color: "var(--vv-primary)",
                        textDecoration: "underline",
                        textUnderlineOffset: 2,
                    }}
                >
                    {viewLabel}
                </a>
            </span>
        </label>
    );
}

function labelFor(doc: RequiredConsentDocument, locale: Locale): string {
    const tr = locale === "tr";
    switch (doc.type) {
        case "TERMS_OF_SERVICE":
            return tr ? "Kullanım Koşulları'nı kabul ediyorum." : "I accept the Terms of Service.";
        case "PERSONAL_DATA_PROTECTION_LAW":
            return tr
                ? "Kişisel Verilerin Korunması bildirimini kabul ediyorum."
                : "I accept the Personal Data Protection Notice.";
        case "MARKETING_COMMUNICATION":
            return tr
                ? "Kampanya ve duyuru e-postalarını almak istiyorum."
                : "I want to receive marketing emails.";
        default:
            return tr ? `${humanize(doc.type)} belgesini kabul ediyorum.` : `I accept the ${humanize(doc.type)}.`;
    }
}

function humanize(type: string): string {
    return type
        .toLowerCase()
        .split("_")
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ");
}
