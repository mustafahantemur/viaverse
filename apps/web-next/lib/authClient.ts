/**
 * Thin BFF client.
 *
 * Web flow:
 *   - access token lives in memory (this module's local state) — lost on
 *     refresh, re-fetched via /api/auth/refresh which reads the HttpOnly
 *     cookie set by the BFF.
 *   - refresh token is never in JS; the browser sends it automatically
 *     via cookie on /api/auth/refresh and /api/auth/logout.
 *   - `credentials: 'include'` on every call so the cookie travels.
 *
 * The BFF base URL points at our Spring Boot service on :8001 in local;
 * override with NEXT_PUBLIC_BFF_BASE_URL in deployed environments.
 */

const BFF_BASE_URL =
    process.env.NEXT_PUBLIC_BFF_BASE_URL ?? "http://localhost:8001";

let accessToken: string | null = null;

export function getAccessToken(): string | null {
    return accessToken;
}

export function setAccessToken(token: string | null): void {
    accessToken = token;
}

export type ApiError = {
    status: number;
    code?: string;
    identityCode?: string;
    detail?: string;
    fieldErrors?: Record<string, string>;
    raw?: unknown;
};

export class ApiCallError extends Error {
    constructor(public readonly error: ApiError) {
        super(error.detail ?? error.code ?? `Request failed with status ${error.status}`);
    }
}

async function call<T>(
    path: string,
    init: RequestInit & { authed?: boolean } = {},
): Promise<T> {
    const headers: Record<string, string> = {
        "Content-Type": "application/json",
        ...(init.headers as Record<string, string> | undefined),
    };
    if (init.authed && accessToken) {
        headers.Authorization = `Bearer ${accessToken}`;
    }
    const response = await fetch(`${BFF_BASE_URL}${path}`, {
        ...init,
        headers,
        credentials: "include",
    });
    const text = await response.text();
    const parsed = text ? JSON.parse(text) : null;
    if (!response.ok) {
        throw new ApiCallError({
            status: response.status,
            code: parsed?.code,
            identityCode: parsed?.identityCode,
            detail: parsed?.detail ?? parsed?.message,
            fieldErrors: parsed?.fieldErrors,
            raw: parsed,
        });
    }
    if (parsed && typeof parsed === "object" && "success" in parsed && "data" in parsed) {
        return parsed.data as T;
    }
    return parsed as T;
}

// ---- Auth ----

export type StartResult = {
    flowId?: string;
    identifierType: "EMAIL" | "PHONE";
    nextStep: "PASSWORD_REQUIRED" | "OTP_REQUIRED";
    expiresAt?: string;
};

export function start(identifier: string): Promise<StartResult> {
    return call("/api/auth/start", { method: "POST", body: JSON.stringify({ identifier }) });
}

export type AuthSession = {
    nextStep: string;
    accessToken?: string;
    accessTokenExpiresAt?: string;
    refreshToken?: string;
    refreshTokenExpiresAt?: string;
    partialAuthToken?: string;
    partialAuthExpiresAt?: string;
    registrationToken?: string;
    registrationExpiresAt?: string;
    account?: unknown;
};

function storeAccessTokenFromResult(result: AuthSession): AuthSession {
    if (result.accessToken) {
        setAccessToken(result.accessToken);
    }
    return result;
}

export async function passwordLogin(identifier: string, password: string): Promise<AuthSession> {
    const result = await call<AuthSession>("/api/auth/password-login", {
        method: "POST",
        body: JSON.stringify({ identifier, password }),
    });
    return storeAccessTokenFromResult(result);
}

export async function verifyTotp(partialAuthToken: string, totpCode: string): Promise<AuthSession> {
    const result = await call<AuthSession>("/api/auth/verify-totp", {
        method: "POST",
        body: JSON.stringify({ partialAuthToken, totpCode }),
    });
    return storeAccessTokenFromResult(result);
}

export type VerifyOtpResult = {
    nextStep: "REGISTRATION_REQUIRED";
    registrationToken: string;
    registrationExpiresAt: string;
};

export function verifyOtp(flowId: string, otp: string): Promise<VerifyOtpResult> {
    return call("/api/auth/verify-otp", {
        method: "POST",
        body: JSON.stringify({ flowId, otp }),
    });
}

export type RegisterPayload = {
    registrationToken: string;
    displayName: string;
    firstName?: string;
    lastName?: string;
    password: string;
    acceptedRequiredConsents: string[];
    marketingConsentAccepted: boolean;
};

export async function register(payload: RegisterPayload): Promise<AuthSession> {
    const result = await call<AuthSession>("/api/auth/register", {
        method: "POST",
        body: JSON.stringify(payload),
    });
    return storeAccessTokenFromResult(result);
}

// ---- Form-first registration (draft) ----

export type RegisterStartPayload = {
    email: string;
    phone?: string;
    displayName: string;
    firstName?: string;
    lastName?: string;
    password: string;
    acceptedRequiredConsents: string[];
    marketingConsentAccepted: boolean;
};

export type RegisterStartResult = {
    draftId: string;
    emailFlowId: string;
    emailExpiresAt: string;
    phoneVerificationPending: boolean;
};

export function registerStart(payload: RegisterStartPayload): Promise<RegisterStartResult> {
    return call("/api/auth/register/start", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}

export type RegisterVerifyEmailResult = AuthSession & {
    phoneFlowId?: string;
    phoneExpiresAt?: string;
};

export async function registerVerifyEmail(
    draftId: string,
    otp: string,
): Promise<RegisterVerifyEmailResult> {
    const result = await call<RegisterVerifyEmailResult>("/api/auth/register/verify-email", {
        method: "POST",
        body: JSON.stringify({ draftId, otp }),
    });
    return storeAccessTokenFromResult(result) as RegisterVerifyEmailResult;
}

export async function registerVerifyPhone(
    draftId: string,
    otp: string,
): Promise<AuthSession> {
    const result = await call<AuthSession>("/api/auth/register/verify-phone", {
        method: "POST",
        body: JSON.stringify({ draftId, otp }),
    });
    return storeAccessTokenFromResult(result);
}

export async function refresh(): Promise<AuthSession> {
    const result = await call<AuthSession>("/api/auth/refresh", {
        method: "POST",
        body: JSON.stringify({}),
    });
    return storeAccessTokenFromResult(result);
}

export async function logout(): Promise<void> {
    try {
        await call("/api/auth/logout", { method: "POST", body: JSON.stringify({}), authed: true });
    } finally {
        setAccessToken(null);
    }
}

// ---- Forgot password ----

export type ForgotStartResult = {
    flowId: string;
    identifierType: "EMAIL" | "PHONE";
    expiresAt: string;
};

export function forgotPasswordStart(identifier: string): Promise<ForgotStartResult> {
    return call("/api/auth/forgot-password/start", {
        method: "POST",
        body: JSON.stringify({ identifier }),
    });
}

export type ForgotTokenResult = { resetToken: string; expiresAt: string };

export function forgotPasswordVerifyOtp(flowId: string, otp: string): Promise<ForgotTokenResult> {
    return call("/api/auth/forgot-password/verify-otp", {
        method: "POST",
        body: JSON.stringify({ flowId, otp }),
    });
}

export function forgotPasswordComplete(resetToken: string, newPassword: string): Promise<void> {
    return call("/api/auth/forgot-password/complete", {
        method: "POST",
        body: JSON.stringify({ resetToken, newPassword }),
    });
}

// ---- Required consents ----

export type RequiredConsentDocument = {
    type: string;
    category: string;
    version: string;
    url: string;
};

export type RequiredConsents = {
    required: RequiredConsentDocument[];
    marketing: RequiredConsentDocument;
};

export function getRequiredConsents(): Promise<RequiredConsents> {
    return call("/api/auth/required-consents", { method: "GET" });
}

export type CapabilityTerms = {
    capabilityTerms: RequiredConsentDocument[];
};

export function getCapabilityTerms(): Promise<CapabilityTerms> {
    return call("/api/auth/capability-terms", { method: "GET" });
}

// ---- Me ----

export type MeView = {
    id: string;
    status: string;
    displayName: string;
    firstName?: string;
    lastName?: string;
    profileCompleted: boolean;
};

export function me(): Promise<MeView> {
    return call("/api/me", { method: "GET", authed: true });
}

export function changePassword(currentPassword: string | null, newPassword: string): Promise<void> {
    return call("/api/me/password", {
        method: "POST",
        body: JSON.stringify({ currentPassword: currentPassword ?? "", newPassword }),
        authed: true,
    });
}

// ---- Profile ----

export type ActiveMode = "CUSTOMER" | "INDIVIDUAL_PROVIDER" | "BUSINESS";
export type CapabilityStatus = "ENABLED" | "PENDING_REVIEW" | "SUSPENDED" | "DISABLED";
export type PublicVisibility = "PUBLIC" | "LIMITED" | "PRIVATE";
export type BusinessSector = "PHARMACY" | "CLINIC" | "AGENCY" | "SHOP" | "SOFTWARE" | "OTHER";
export type BusinessVerificationStatus = "DRAFT" | "SUBMITTED" | "APPROVED" | "REJECTED";

export type CapabilityView = {
    capability: ActiveMode;
    status: CapabilityStatus;
    verificationLevel?: string;
    enabledAt?: string;
    disabledAt?: string;
};

export type IndividualProviderProfileView = {
    serviceBlurb?: string;
    availabilitySummary?: string;
    acceptsRemote: boolean;
    providerTermsVersionAccepted?: string;
};

export type BusinessProfileView = {
    legalName?: string;
    tradeName?: string;
    sector?: BusinessSector;
    taxId?: string;
    addressLine?: string;
    district?: string;
    city?: string;
    country?: string;
    phone?: string;
    emailPublic?: string;
    logoMediaId?: string;
    openingHoursJson?: string;
    verificationStatus: BusinessVerificationStatus;
    businessTermsVersionAccepted?: string;
    rejectionReason?: string;
};

export type CurrentProfileView = {
    accountId: string;
    displayName: string;
    firstName?: string;
    lastName?: string;
    avatarMediaId?: string;
    headline?: string;
    bio?: string;
    locale: string;
    timezone: string;
    activeMode: ActiveMode;
    completenessScore: number;
    publicVisibility: PublicVisibility;
    capabilities: CapabilityView[];
    individualProviderProfile?: IndividualProviderProfileView;
    businessProfile?: BusinessProfileView;
};

export type UpdateProfilePayload = Partial<Pick<
    CurrentProfileView,
    "displayName" | "firstName" | "lastName" | "headline" | "bio" | "locale" | "timezone" | "publicVisibility"
>>;

export type UpdateBusinessDraftPayload = {
    legalName?: string;
    tradeName?: string;
    sector?: BusinessSector;
    taxId?: string;
    addressLine?: string;
    district?: string;
    city?: string;
    country?: string;
    phone?: string;
    emailPublic?: string;
    logoMediaId?: string;
    openingHoursJson?: string;
};

export function currentProfile(): Promise<CurrentProfileView> {
    return call("/api/me/profile", { method: "GET", authed: true });
}

export function updateProfile(payload: UpdateProfilePayload): Promise<CurrentProfileView> {
    return call("/api/me/profile", {
        method: "PATCH",
        body: JSON.stringify(payload),
        authed: true,
    });
}

export function enableIndividualProvider(
    acceptedProviderTermsVersion: string,
    serviceBlurb?: string,
): Promise<CurrentProfileView> {
    return call("/api/me/capabilities/individual-provider/enable", {
        method: "POST",
        body: JSON.stringify({ acceptedProviderTermsVersion, serviceBlurb }),
        authed: true,
    });
}

export function updateActiveMode(activeMode: ActiveMode): Promise<CurrentProfileView> {
    return call("/api/me/active-mode", {
        method: "PATCH",
        body: JSON.stringify({ activeMode }),
        authed: true,
    });
}

export function startBusinessOnboarding(): Promise<BusinessProfileView> {
    return call("/api/me/capabilities/business/start", {
        method: "POST",
        body: JSON.stringify({}),
        authed: true,
    });
}

export function updateBusinessDraft(payload: UpdateBusinessDraftPayload): Promise<BusinessProfileView> {
    return call("/api/me/business/draft", {
        method: "PATCH",
        body: JSON.stringify(payload),
        authed: true,
    });
}

export function submitBusinessOnboarding(
    acceptedBusinessTermsVersion: string,
): Promise<BusinessProfileView> {
    return call("/api/me/capabilities/business/submit", {
        method: "POST",
        body: JSON.stringify({ acceptedBusinessTermsVersion }),
        authed: true,
    });
}
