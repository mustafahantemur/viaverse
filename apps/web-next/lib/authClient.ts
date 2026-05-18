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
    serviceCategories: ServiceCategory[];
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
    serviceCategories: ServiceCategory[];
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
    trust: {
        score: number;
        level: "NONE" | "BASIC" | "VERIFIED_HUMAN" | "ENHANCED";
        badge: "NONE" | "BASIC" | "VERIFIED_HUMAN" | "ENHANCED";
        updatedAt?: string;
    };
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
    serviceCategories?: ServiceCategory[];
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

export type UpdateIndividualProviderProfilePayload = {
    serviceBlurb?: string;
    availabilitySummary?: string;
    acceptsRemote: boolean;
    serviceCategories?: ServiceCategory[];
};

export function updateIndividualProviderProfile(
    payload: UpdateIndividualProviderProfilePayload,
): Promise<IndividualProviderProfileView> {
    return call("/api/me/individual-provider-profile", {
        method: "PATCH",
        body: JSON.stringify(payload),
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

// ---- Marketplace ----

export type ServiceCategory =
    | "HOME_REPAIR"
    | "DIGITAL_SOFTWARE"
    | "CREATIVE_MEDIA"
    | "EDUCATION"
    | "CLEANING"
    | "LOGISTICS"
    | "CARE_HEALTH"
    | "PROFESSIONAL_CONSULTING"
    | "PETS"
    | "EVENTS"
    | "LOCAL_HELP";

export type ServiceRequestStatus = "OPEN" | "MATCHED" | "CANCELLED" | "COMPLETED";
export type OfferStatus = "SUBMITTED" | "ACCEPTED" | "REJECTED" | "WITHDRAWN";
export type JobStatus = "AGREED" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED" | "DISPUTED";

export type ServiceRequestView = {
    id: string;
    requesterAccountId: string;
    title: string;
    description: string;
    category: ServiceCategory;
    budgetMinAmountMinor?: number;
    budgetMaxAmountMinor?: number;
    currency: string;
    remoteAllowed: boolean;
    district?: string;
    city?: string;
    mediaAssetIds: string[];
    status: ServiceRequestStatus;
    createdAt: string;
    updatedAt: string;
};

export type OfferView = {
    id: string;
    requestId: string;
    providerAccountId: string;
    amountMinor: number;
    currency: string;
    message?: string;
    status: OfferStatus;
    createdAt: string;
    updatedAt: string;
};

export type JobView = {
    id: string;
    requestId: string;
    acceptedOfferId: string;
    requesterAccountId: string;
    providerAccountId: string;
    agreedAmountMinor: number;
    currency: string;
    status: JobStatus;
    createdAt: string;
    updatedAt: string;
};

export type CreateServiceRequestPayload = {
    title: string;
    description: string;
    category: ServiceCategory;
    budgetMinAmountMinor?: number;
    budgetMaxAmountMinor?: number;
    currency?: string;
    remoteAllowed: boolean;
    district?: string;
    city?: string;
    mediaAssetIds?: string[];
};

export function createServiceRequest(payload: CreateServiceRequestPayload): Promise<ServiceRequestView> {
    return call("/api/requests", {
        method: "POST",
        body: JSON.stringify(payload),
        authed: true,
    });
}

export function openServiceRequests(): Promise<ServiceRequestView[]> {
    return call("/api/requests/open", { method: "GET", authed: true });
}

export function workFeed(): Promise<ServiceRequestView[]> {
    return call("/api/feed/work", { method: "GET", authed: true });
}

export function myServiceRequests(): Promise<ServiceRequestView[]> {
    return call("/api/me/requests", { method: "GET", authed: true });
}

export function submitOffer(
    requestId: string,
    amountMinor: number,
    currency: string,
    message?: string,
): Promise<OfferView> {
    return call(`/api/requests/${requestId}/offers`, {
        method: "POST",
        body: JSON.stringify({ amountMinor, currency, message }),
        authed: true,
    });
}

export function listOffers(requestId: string): Promise<OfferView[]> {
    return call(`/api/requests/${requestId}/offers`, { method: "GET", authed: true });
}

export function acceptOffer(requestId: string, offerId: string): Promise<JobView> {
    return call(`/api/requests/${requestId}/offers/${offerId}/accept`, {
        method: "POST",
        body: JSON.stringify({}),
        authed: true,
    });
}

export function myJobs(): Promise<JobView[]> {
    return call("/api/me/jobs", { method: "GET", authed: true });
}

export function startJob(jobId: string): Promise<JobView> {
    return call(`/api/jobs/${jobId}/start`, {
        method: "POST",
        body: JSON.stringify({}),
        authed: true,
    });
}

export function completeJob(jobId: string): Promise<JobView> {
    return call(`/api/jobs/${jobId}/complete`, {
        method: "POST",
        body: JSON.stringify({}),
        authed: true,
    });
}

// ---- Content & media ----

export type ContentAuthorMode = "CUSTOMER" | "INDIVIDUAL_PROVIDER" | "BUSINESS";
export type ContentPostType = "LOCAL_UPDATE" | "ANNOUNCEMENT" | "EVENT" | "ADVICE" | "BUSINESS_PROMOTION";
export type ContentPostStatus = "PUBLISHED" | "WITHDRAWN" | "REJECTED";
export type ContentModerationStatus = "AUTO_APPROVED" | "PENDING_REVIEW" | "REJECTED";

export type ContentPostView = {
    id: string;
    authorAccountId: string;
    authorMode: ContentAuthorMode;
    postType: ContentPostType;
    title?: string;
    body: string;
    city?: string;
    district?: string;
    eventStartsAt?: string;
    eventEndsAt?: string;
    mediaAssetIds: string[];
    status: ContentPostStatus;
    moderationStatus: ContentModerationStatus;
    publishedAt: string;
    createdAt: string;
    updatedAt: string;
};

export type SocialFeedItemView = {
    post: ContentPostView;
    score: number;
    reason: string;
};

export type ContentSignalType =
    | "IMPRESSION"
    | "OPEN"
    | "DWELL"
    | "LIKE"
    | "SAVE"
    | "SHARE"
    | "HIDE"
    | "REPORT"
    | "VIDEO_START"
    | "VIDEO_25"
    | "VIDEO_50"
    | "VIDEO_75"
    | "VIDEO_COMPLETE";

export type CreateContentPostPayload = {
    authorMode: ContentAuthorMode;
    postType: ContentPostType;
    title?: string;
    body: string;
    city?: string;
    district?: string;
    eventStartsAt?: string;
    eventEndsAt?: string;
    mediaAssetIds?: string[];
};

export type MediaAssetKind = "IMAGE" | "VIDEO";
export type MediaAssetStatus = "INITIATED" | "READY" | "FAILED";

export type UploadSessionView = {
    assetId: string;
    uploadSessionId: string;
    uploadUrl: string;
    requiredHeaders: Record<string, string>;
    expiresAt: string;
};

export type MediaAssetView = {
    id: string;
    ownerAccountId: string;
    assetKind: MediaAssetKind;
    contentType: string;
    originalFileName?: string;
    objectKey: string;
    byteSize?: number;
    checksumSha256?: string;
    status: MediaAssetStatus;
    createdAt: string;
    updatedAt: string;
};

export function createContentPost(payload: CreateContentPostPayload): Promise<ContentPostView> {
    return call("/api/posts", {
        method: "POST",
        body: JSON.stringify(payload),
        authed: true,
    });
}

export function publishedPosts(city?: string, district?: string): Promise<ContentPostView[]> {
    const params = new URLSearchParams();
    if (city) params.set("city", city);
    if (district) params.set("district", district);
    const suffix = params.size ? `?${params.toString()}` : "";
    return call(`/api/posts/published${suffix}`, { method: "GET", authed: true });
}

export function myPosts(): Promise<ContentPostView[]> {
    return call("/api/me/posts", { method: "GET", authed: true });
}

export function socialFeed(city?: string, district?: string): Promise<SocialFeedItemView[]> {
    const params = new URLSearchParams();
    if (city) params.set("city", city);
    if (district) params.set("district", district);
    const suffix = params.size ? `?${params.toString()}` : "";
    return call(`/api/feed/social${suffix}`, { method: "GET", authed: true });
}

export function recordContentInteraction(
    postId: string,
    signalType: ContentSignalType,
    surface: string,
    options: {
        position?: number;
        dwellTimeMs?: number;
        sessionId?: string;
        occurredAt?: string;
    } = {},
): Promise<unknown> {
    return call(`/api/posts/${postId}/interactions`, {
        method: "POST",
        body: JSON.stringify({ signalType, surface, ...options }),
        authed: true,
    });
}

export function createUploadSession(
    assetKind: MediaAssetKind,
    contentType: string,
    originalFileName?: string,
): Promise<UploadSessionView> {
    return call("/api/assets/upload-sessions", {
        method: "POST",
        body: JSON.stringify({ assetKind, contentType, originalFileName }),
        authed: true,
    });
}

export function completeUpload(assetId: string, checksumSha256?: string): Promise<MediaAssetView> {
    return call(`/api/assets/${assetId}/complete`, {
        method: "POST",
        body: JSON.stringify({ checksumSha256 }),
        authed: true,
    });
}
