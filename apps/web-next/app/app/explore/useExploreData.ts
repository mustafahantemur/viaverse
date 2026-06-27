"use client";

import { useEffect, useState } from "react";
import {
    mockAppApi,
    type BusinessView,
    type ProviderView,
    type SavedSearchView,
    type ServiceCategoryView,
    type SponsoredAdView,
} from "@/lib/mockAppClient";
import { STATIC_CATEGORIES, STATIC_PROVIDERS, STATIC_BUSINESSES } from "./exploreStaticData";

const STATIC_CATEGORY_BY_ID = new Map(STATIC_CATEGORIES.map((c) => [c.id, c]));

// The BFF is the source of truth, but if its persisted seed predates the rich
// subcategory lists, backfill them from the static catalog so the tree always works.
function enrichCategories(categories: ServiceCategoryView[]): ServiceCategoryView[] {
    return categories.map((c) =>
        c.subCategories && c.subCategories.length > 0
            ? c
            : { ...c, subCategories: STATIC_CATEGORY_BY_ID.get(c.id)?.subCategories ?? [] },
    );
}

const EXPLORE_FALLBACK_AD: SponsoredAdView = {
    id: "explore-ad-fallback",
    title: "Profilinizi öne çıkarın",
    body: "Keşfet alanında sponsorlu vitrin. Yakındaki kullanıcılara öncelikli görünün.",
    advertiser: "Viaverse İşletme",
    imageUrl: "/brand/assets/categories/professional_consulting.png",
    displayUrl: "viaverse.app/business",
    reason: "Sponsorlu",
};

export type ExploreData = {
    categories: ServiceCategoryView[];
    providers: ProviderView[];
    businesses: BusinessView[];
    ads: SponsoredAdView[];
    savedSearches: SavedSearchView[];
    setSavedSearches: React.Dispatch<React.SetStateAction<SavedSearchView[]>>;
    loading: boolean;
    /** true when the BFF was unreachable and static fallback data is being shown */
    usingFallback: boolean;
};

export function useExploreData(): ExploreData {
    const [categories, setCategories] = useState<ServiceCategoryView[]>([]);
    const [providers, setProviders] = useState<ProviderView[]>([]);
    const [businesses, setBusinesses] = useState<BusinessView[]>([]);
    const [ads, setAds] = useState<SponsoredAdView[]>([EXPLORE_FALLBACK_AD]);
    const [savedSearches, setSavedSearches] = useState<SavedSearchView[]>([]);
    const [loading, setLoading] = useState(true);
    const [usingFallback, setUsingFallback] = useState(false);

    useEffect(() => {
        let cancelled = false;

        function applyFallback() {
            if (cancelled) return;
            setCategories(STATIC_CATEGORIES);
            setProviders(STATIC_PROVIDERS);
            setBusinesses(STATIC_BUSINESSES);
            setUsingFallback(true);
        }

        async function load() {
            try {
                const [nextCategories, nextProviders, nextBusinesses, nextSaved, nextAds] = await Promise.all([
                    mockAppApi.categories(),
                    mockAppApi.providers(),
                    mockAppApi.businesses(),
                    mockAppApi.savedSearches("services").catch(() => [] as SavedSearchView[]),
                    mockAppApi.sponsoredAds("explore").catch(() => [] as SponsoredAdView[]),
                ]);
                if (cancelled) return;

                // The BFF is the source of truth; only fall back when it returns nothing.
                const hasData = nextCategories.length > 0 && (nextProviders.length > 0 || nextBusinesses.length > 0);
                if (hasData) {
                    setCategories(enrichCategories(nextCategories));
                    setProviders(nextProviders);
                    setBusinesses(nextBusinesses);
                } else {
                    applyFallback();
                }
                setSavedSearches(nextSaved);
                setAds(nextAds.length > 0 ? nextAds : [EXPLORE_FALLBACK_AD]);
            } catch {
                // BFF unreachable — show a populated UI from the static fallback.
                applyFallback();
            } finally {
                if (!cancelled) setLoading(false);
            }
        }

        load();
        return () => { cancelled = true; };
    }, []);

    return { categories, providers, businesses, ads, savedSearches, setSavedSearches, loading, usingFallback };
}
