"use client";

import type { RequiredConsents, RequiredConsentDocument } from "@/lib/authClient";

interface Props {
    consents: RequiredConsents;
    accepted: Record<string, boolean>;
    onToggle: (type: string, accepted: boolean) => void;
    marketingAccepted: boolean;
    onMarketingToggle: (accepted: boolean) => void;
}

/**
 * Renders the legal documents the user must accept to register, plus the
 * optional marketing opt-in. **Document versions are intentionally not
 * shown** — they're a server-side stamping concern, not user-facing
 * information. Each checkbox label links to the legal page so the user
 * can read the actual document if they want.
 */
export function ConsentList({
    consents,
    accepted,
    onToggle,
    marketingAccepted,
    onMarketingToggle,
}: Props) {
    return (
        <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
            {consents.required.map((doc) => (
                <ConsentRow
                    key={doc.type}
                    doc={doc}
                    accepted={!!accepted[doc.type]}
                    onChange={(value) => onToggle(doc.type, value)}
                />
            ))}
            <ConsentRow
                doc={consents.marketing}
                accepted={marketingAccepted}
                onChange={onMarketingToggle}
                optional
            />
        </div>
    );
}

function ConsentRow({
    doc,
    accepted,
    onChange,
    optional = false,
}: {
    doc: RequiredConsentDocument;
    accepted: boolean;
    onChange: (accepted: boolean) => void;
    optional?: boolean;
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
                {optional && (
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
                        Optional
                    </span>
                )}
                {labelFor(doc)}{" "}
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
                    Read
                </a>
            </span>
        </label>
    );
}

function labelFor(doc: RequiredConsentDocument): string {
    switch (doc.type) {
        case "TERMS_OF_SERVICE":
            return "I accept the Terms of Service.";
        case "PERSONAL_DATA_PROTECTION_LAW":
            return "I accept the Personal Data Protection Notice.";
        case "MARKETING_COMMUNICATION":
            return "Send me product updates and offers.";
        default:
            return `I accept the ${humanize(doc.type)}.`;
    }
}

function humanize(type: string): string {
    return type
        .toLowerCase()
        .split("_")
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ");
}
