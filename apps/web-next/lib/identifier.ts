/**
 * Client-side identifier normalization. Server still re-runs this via
 * IdentifierNormalizer (libphonenumber, default region TR) — this is
 * purely a UX helper so the user can type "5xx xxx xx xx" and we send
 * "+905xxxxxxxxx" upstream.
 */

const DEFAULT_DIAL_CODE = "+90";

/**
 * Returns the value we should send to the server. Email-shaped strings
 * are lower-cased + trimmed; anything that looks like a phone gets
 * normalized to E.164 with the default dial code.
 */
export function normalizeIdentifier(raw: string): string {
    const trimmed = raw.trim();
    if (!trimmed) return "";
    if (trimmed.includes("@")) {
        return trimmed.toLowerCase();
    }
    // Phone-shaped. Pull digits, then re-prefix.
    const digits = trimmed.replace(/[^0-9]/g, "");
    if (!digits) return trimmed;
    if (trimmed.startsWith("+")) {
        return `+${digits}`;
    }
    // Strip a domestic leading 0 (TR convention) before prefixing.
    const local = digits.replace(/^0+/, "");
    return `${DEFAULT_DIAL_CODE}${local}`;
}

export function looksLikeEmail(value: string): boolean {
    return value.includes("@");
}
