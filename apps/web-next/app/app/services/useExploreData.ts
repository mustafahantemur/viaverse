"use client";

import { useEffect, useState } from "react";
import {
    mockAppApi,
    type BusinessView,
    type ProviderView,
    type SavedSearchView,
    type ServiceCategoryView,
} from "@/lib/mockAppClient";

export type ExploreData = {
    categories: ServiceCategoryView[];
    providers: ProviderView[];
    businesses: BusinessView[];
    savedSearches: SavedSearchView[];
    setSavedSearches: React.Dispatch<React.SetStateAction<SavedSearchView[]>>;
    loading: boolean;
};

export function useExploreData(): ExploreData {
    const [categories, setCategories] = useState<ServiceCategoryView[]>([]);
    const [providers, setProviders] = useState<ProviderView[]>([]);
    const [businesses, setBusinesses] = useState<BusinessView[]>([]);
    const [savedSearches, setSavedSearches] = useState<SavedSearchView[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function load() {
            const [nextCategories, nextProviders, nextBusinesses, nextSaved] = await Promise.all([
                mockAppApi.categories(),
                mockAppApi.providers(),
                mockAppApi.businesses(),
                mockAppApi.savedSearches("services"),
            ]);
            setCategories(nextCategories);
            setProviders(nextProviders);
            setBusinesses(nextBusinesses);
            setSavedSearches(nextSaved);
            setLoading(false);
        }
        load();
    }, []);

    return { categories, providers, businesses, savedSearches, setSavedSearches, loading };
}
