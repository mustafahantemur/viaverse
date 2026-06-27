const MOCK_APP_BFF_BASE_URL =
    process.env.NEXT_PUBLIC_MOCK_APP_BFF_BASE_URL ?? "http://localhost:8120";

export class MockAppError extends Error {
    constructor(
        message: string,
        public readonly status: number,
        public readonly raw?: unknown,
    ) {
        super(message);
    }
}

async function call<T>(path: string, init: RequestInit = {}): Promise<T> {
    const response = await fetch(`${MOCK_APP_BFF_BASE_URL}${path}`, {
        ...init,
        headers: {
            "Content-Type": "application/json",
            ...(init.headers as Record<string, string> | undefined),
        },
    });
    const text = await response.text();
    const parsed = text ? JSON.parse(text) : null;
    if (!response.ok) {
        throw new MockAppError(parsed?.detail ?? parsed?.message ?? "Mock app API call failed", response.status, parsed);
    }
    if (parsed && typeof parsed === "object" && "success" in parsed && "data" in parsed) {
        return parsed.data as T;
    }
    return parsed as T;
}

export type CapabilityView = {
    key: "STANDARD" | "INDIVIDUAL_PROVIDER" | "BUSINESS";
    label: string;
    enabled: boolean;
    status: string;
    summary: string;
};

export type UserView = {
    id: string;
    displayName: string;
    firstName: string;
    lastName: string;
    initials: string;
    activeCapability: CapabilityView["key"];
    activeCapabilityLabel: string;
    locationLabel: string;
    capabilities: CapabilityView[];
};

export type SessionView = {
    currentUser: UserView;
    personas: UserView[];
};

export type ServiceCategoryView = {
    id: string;
    label: string;
    description: string;
    lane: string;
    icon: string;
    preferredProviderType: string;
    subCategories: string[];
};

export type FeedItemView = {
    id: string;
    type: "POST" | "ANNOUNCEMENT" | "EVENT" | "REQUEST" | "OPPORTUNITY" | "INFO" | "TRAFFIC" | "UTILITY";
    typeLabel: string;
    title: string;
    body: string;
    authorName: string;
    authorType: string;
    locationLabel: string;
    categoryId: string;
    createdAt: string;
    likeCount: number;
    liked: boolean;
    commentCount: number;
    shareCount: number;
    saved: boolean;
    highlight: string;
    relatedRequestId?: string | null;
    hashtags: string[];
    mediaUrl?: string | null;
    mediaType?: "IMAGE" | "VIDEO" | null;
};

export type CreatePostPayload = {
    type: FeedItemView["type"];
    title: string;
    body: string;
    categoryId?: string;
    locationScope?: string;
    eventStartsAt?: string;
    hashtags?: string[];
    mediaUrl?: string;
    mediaType?: "IMAGE" | "VIDEO";
};

export type UpdatePostPayload = Omit<CreatePostPayload, "eventStartsAt">;

export type PostCommentView = {
    id: string;
    postId: string;
    authorId: string;
    authorName: string;
    body: string;
    createdAt: string;
};

export type IncidentUpdateView = {
    id: string;
    authorName: string;
    body: string;
    createdAt: string;
    mediaUrl?: string | null;
    mediaType?: "IMAGE" | "VIDEO" | null;
};

export type AnnouncementIncidentView = {
    id: string;
    kind: string;
    title: string;
    summary: string;
    locationLabel: string;
    latitude: number;
    longitude: number;
    createdAt: string;
    relatedPostIds: string[];
    updates: IncidentUpdateView[];
};

export type ProviderView = {
    id: string;
    displayName: string;
    providerType: "Serbest Uzman" | "İşletme";
    headline: string;
    summary: string;
    categoryIds: string[];
    locationScope: string;
    rating: number;
    completedJobs: number;
    responseTime: string;
    servesNearby: boolean;
    tags: string[];
    photoUrl: string;
    priceRange: string;
    distanceKm: number;
};

export type BusinessView = {
    id: string;
    tradeName: string;
    providerType: "İşletme";
    sector: string;
    summary: string;
    categoryIds: string[];
    locationScope: string;
    rating: number;
    completedJobs: number;
    responseTime: string;
    verificationStatus: string;
    photoUrl: string;
    priceRange: string;
    distanceKm: number;
};

export type ServiceRequestView = {
    id: string;
    requesterId: string;
    requesterName: string;
    title: string;
    categoryId: string;
    categoryLabel: string;
    description: string;
    locationScope: string;
    timing: string;
    budgetExpectation: string;
    status: "OPEN" | "MATCHED" | "CANCELLED" | "COMPLETED";
    offerCount: number;
    acceptedOfferId?: string | null;
    conversationId?: string | null;
    createdAt: string;
    updatedAt: string;
};

export type CreateServiceRequestPayload = {
    title: string;
    categoryId: string;
    description: string;
    locationScope: string;
    timing: string;
    budgetExpectation: string;
};

export type OpportunityView = {
    request: ServiceRequestView;
    matchReason: string;
    fitScore: number;
};

export type OfferView = {
    id: string;
    requestId: string;
    providerId: string;
    providerName: string;
    providerType: string;
    amountExpectation: string;
    message: string;
    status: "SUBMITTED" | "ACCEPTED" | "REJECTED" | "WITHDRAWN";
    conversationId?: string | null;
    createdAt: string;
    updatedAt: string;
};

export type CreateOfferPayload = {
    requestId: string;
    amountExpectation: string;
    message: string;
};

export type ConversationView = {
    id: string;
    title: string;
    contextLabel: string;
    participantName: string;
    participantType: string;
    lastMessage: string;
    lastMessageAt: string;
    unreadCount: number;
    relatedRequestId?: string | null;
    relatedOfferId?: string | null;
};

export type MessageView = {
    id: string;
    conversationId: string;
    senderId: string;
    senderName: string;
    body: string;
    system: boolean;
    createdAt: string;
    updatedAt: string;
};

export type ProviderProfileView = {
    providerType: "Bireysel hizmet veren";
    serviceBlurb: string;
    availabilitySummary: string;
    serviceCategoryIds: string[];
    acceptsRemote: boolean;
    locationScope: string;
};

export type BusinessProfileView = {
    providerType: "İşletme";
    tradeName: string;
    legalName: string;
    sector: string;
    addressLine: string;
    publicPhone: string;
    publicEmail: string;
    serviceCategoryIds: string[];
    verificationStatus: string;
};

export type ProfileView = {
    accountId: string;
    displayName: string;
    headline: string;
    bio: string;
    locationLabel: string;
    activeCapability: CapabilityView["key"];
    capabilities: CapabilityView[];
    individualProviderProfile?: ProviderProfileView | null;
    businessProfile?: BusinessProfileView | null;
    trustScore: number;
    completionScore: number;
};

export type PatchProfilePayload = Partial<Pick<ProfileView, "displayName" | "headline" | "bio" | "locationLabel" | "activeCapability">>;

export type SettingsView = {
    pushNotifications: boolean;
    emailDigest: boolean;
    requestUpdates: boolean;
    privacyLevel: string;
    language: string;
    theme: string;
};

export type TransactionView = {
    id: string;
    type: string;
    title: string;
    amountMinor: number;
    currency: string;
    status: "PENDING" | "COMPLETED" | "FAILED" | "CANCELLED";
    description: string;
    relatedRequestId?: string | null;
    createdAt: string;
    updatedAt: string;
};

export type CreatePaymentIntentPayload = {
    title: string;
    amountMinor: number;
    currency: string;
    relatedRequestId?: string;
};

export type NotificationView = {
    id: string;
    title: string;
    body: string;
    type: string;
    read: boolean;
    createdAt: string;
};

export type HashtagSuggestionView = {
    tag: string;
    usageCount: number;
    sampleTitle: string;
};

export type MockPhotoView = {
    id: string;
    url: string;
    alt: string;
    sourceLabel: string;
    sourceUrl: string;
    tags: string[];
};

export type SponsoredAdView = {
    id: string;
    title: string;
    body: string;
    advertiser: string;
    imageUrl: string;
    displayUrl: string;
    reason: string;
};

export type SavedSearchView = {
    id: string;
    ownerId: string;
    surface: string;
    name: string;
    filters: Record<string, string>;
    createdAt: string;
};

export type CreateSavedSearchPayload = {
    surface: string;
    name: string;
    filters: Record<string, string>;
};

export const mockAppApi = {
    session: () => call<SessionView>("/api/app/me"),
    switchPersona: (personaId: string) =>
        call<SessionView>("/api/app/session/persona", { method: "POST", body: JSON.stringify({ personaId }) }),
    reset: () => call<SessionView>("/api/app/dev/reset", { method: "POST", body: JSON.stringify({}) }),
    feed: (type?: string) => call<FeedItemView[]>(`/api/app/feed${type ? `?type=${encodeURIComponent(type)}` : ""}`),
    createPost: (payload: CreatePostPayload) =>
        call<FeedItemView>("/api/app/posts", { method: "POST", body: JSON.stringify(payload) }),
    updatePost: (postId: string, payload: UpdatePostPayload) =>
        call<FeedItemView>(`/api/app/posts/${postId}`, { method: "PATCH", body: JSON.stringify(payload) }),
    likePost: (postId: string) =>
        call<FeedItemView>(`/api/app/posts/${postId}/like`, { method: "POST", body: JSON.stringify({}) }),
    savePost: (postId: string) =>
        call<FeedItemView>(`/api/app/posts/${postId}/save`, { method: "POST", body: JSON.stringify({}) }),
    sharePost: (postId: string) =>
        call<FeedItemView>(`/api/app/posts/${postId}/share`, { method: "POST", body: JSON.stringify({}) }),
    comments: (postId: string) => call<PostCommentView[]>(`/api/app/posts/${postId}/comments`),
    createComment: (postId: string, body: string) =>
        call<PostCommentView>(`/api/app/posts/${postId}/comments`, {
            method: "POST",
            body: JSON.stringify({ body }),
        }),
    hashtagSuggestions: (query?: string) =>
        call<HashtagSuggestionView[]>(`/api/app/feed/hashtags${query ? `?query=${encodeURIComponent(query)}` : ""}`),
    mockPhotos: (query?: string) =>
        call<MockPhotoView[]>(`/api/app/media/mock-photos${query ? `?query=${encodeURIComponent(query)}` : ""}`),
    sponsoredAds: (surface?: string) =>
        call<SponsoredAdView[]>(`/api/app/ads${surface ? `?surface=${encodeURIComponent(surface)}` : ""}`),
    events: () => call<FeedItemView[]>("/api/app/events"),
    announcements: () => call<FeedItemView[]>("/api/app/announcements"),
    announcementIncidents: () => call<AnnouncementIncidentView[]>("/api/app/announcements/incidents"),
    categories: () => call<ServiceCategoryView[]>("/api/app/services/categories"),
    providers: () => call<ProviderView[]>("/api/app/providers"),
    provider: (id: string) => call<ProviderView>(`/api/app/providers/${id}`),
    businesses: () => call<BusinessView[]>("/api/app/businesses"),
    business: (id: string) => call<BusinessView>(`/api/app/businesses/${id}`),
    savedSearches: (surface?: string) =>
        call<SavedSearchView[]>(`/api/app/searches/saved${surface ? `?surface=${encodeURIComponent(surface)}` : ""}`),
    createSavedSearch: (payload: CreateSavedSearchPayload) =>
        call<SavedSearchView>("/api/app/searches/saved", { method: "POST", body: JSON.stringify(payload) }),
    myRequests: () => call<ServiceRequestView[]>("/api/app/requests/mine"),
    createRequest: (payload: CreateServiceRequestPayload) =>
        call<ServiceRequestView>("/api/app/requests", { method: "POST", body: JSON.stringify(payload) }),
    opportunities: () => call<OpportunityView[]>("/api/app/opportunities"),
    createOffer: (payload: CreateOfferPayload) =>
        call<OfferView>("/api/app/offers", { method: "POST", body: JSON.stringify(payload) }),
    myOffers: () => call<OfferView[]>("/api/app/offers/mine"),
    requestOffers: (requestId: string) => call<OfferView[]>(`/api/app/requests/${requestId}/offers`),
    acceptOffer: (offerId: string) => call<{ offer: OfferView; conversation: ConversationView }>(`/api/app/offers/${offerId}/accept`, {
        method: "POST",
        body: JSON.stringify({}),
    }),
    conversations: () => call<ConversationView[]>("/api/app/conversations"),
    messages: (conversationId: string) => call<MessageView[]>(`/api/app/conversations/${conversationId}/messages`),
    sendMessage: (conversationId: string, body: string) =>
        call<MessageView>(`/api/app/conversations/${conversationId}/messages`, {
            method: "POST",
            body: JSON.stringify({ body }),
        }),
    updateMessage: (conversationId: string, messageId: string, body: string) =>
        call<MessageView>(`/api/app/conversations/${conversationId}/messages/${messageId}`, {
            method: "PATCH",
            body: JSON.stringify({ body }),
        }),
    profile: () => call<ProfileView>("/api/app/profile"),
    patchProfile: (payload: PatchProfilePayload) =>
        call<ProfileView>("/api/app/profile", { method: "PATCH", body: JSON.stringify(payload) }),
    settings: () => call<SettingsView>("/api/app/settings"),
    patchSettings: (payload: Partial<SettingsView>) =>
        call<SettingsView>("/api/app/settings", { method: "PATCH", body: JSON.stringify(payload) }),
    transactions: () => call<TransactionView[]>("/api/app/payments/transactions"),
    createPaymentIntent: (payload: CreatePaymentIntentPayload) =>
        call<TransactionView>("/api/app/payments/mock-intents", { method: "POST", body: JSON.stringify(payload) }),
    updatePaymentStatus: (transactionId: string, status: TransactionView["status"]) =>
        call<TransactionView>(`/api/app/payments/mock-intents/${transactionId}`, {
            method: "PATCH",
            body: JSON.stringify({ status }),
        }),
    notifications: () => call<NotificationView[]>("/api/app/notifications"),
};

export function formatRelative(value: string): string {
    const date = new Date(value);
    const diffMs = Date.now() - date.getTime();
    const minutes = Math.max(1, Math.round(diffMs / 60000));
    if (minutes < 60) return `${minutes} dk önce`;
    const hours = Math.round(minutes / 60);
    if (hours < 24) return `${hours} sa önce`;
    return new Intl.DateTimeFormat("tr-TR", { day: "numeric", month: "short" }).format(date);
}

export function formatDateTime(value: string): string {
    return new Intl.DateTimeFormat("tr-TR", {
        day: "numeric",
        month: "short",
        hour: "2-digit",
        minute: "2-digit",
    }).format(new Date(value));
}

export function formatMoneyMinor(amountMinor: number, currency: string): string {
    return new Intl.NumberFormat("tr-TR", { style: "currency", currency }).format(amountMinor / 100);
}
