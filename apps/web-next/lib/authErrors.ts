import { ApiCallError } from "./authClient";
import type { Translations } from "./i18n/messages";

/**
 * Translate server error envelopes into UI strings.
 *
 * The server returns either:
 *   - `code` (HTTP-style class — e.g. UNAUTHORIZED, RATE_LIMITED)
 *   - `identityCode` (domain-specific — e.g. AUTH_INVALID_CREDENTIALS)
 *
 * We map known identityCodes to localized strings; anything we don't
 * recognize falls through to the server's `detail` (already localized
 * by the backend in most cases) or a generic fallback.
 */
export function describeError(caught: unknown, t: Translations): string {
    if (caught instanceof ApiCallError) {
        const code = caught.error.identityCode ?? caught.error.code;
        switch (code) {
            case "AUTH_INVALID_CREDENTIALS":
                return t.auth.errors.invalidCredentials;
            case "AUTH_INVALID_OTP":
            case "AUTH_OTP_EXPIRED":
                return t.auth.errors.invalidOtp;
            case "AUTH_INVALID_TOTP":
            case "AUTH_TOTP_NOT_ENABLED":
                return t.auth.errors.invalidTotp;
            case "RATE_LIMITED":
            case "AUTH_RATE_LIMITED":
                return t.auth.errors.rateLimited;
            case "AUTH_EMAIL_ALREADY_REGISTERED":
                return t.auth.errors.emailAlreadyRegistered;
            case "AUTH_PHONE_ALREADY_REGISTERED":
                return t.auth.errors.phoneAlreadyRegistered;
            case "AUTH_REGISTRATION_DRAFT_MISSING":
            case "AUTH_REGISTRATION_DRAFT_WRONG_STAGE":
                return t.auth.errors.draftMissing;
        }
        return caught.error.detail ?? t.auth.errors.generic;
    }
    if (caught instanceof Error && caught.message.includes("fetch")) {
        return t.auth.errors.networkError;
    }
    return t.auth.errors.generic;
}
