"use client";

import { useEffect, useState } from "react";
import { getRequiredConsents, type RequiredConsents } from "@/lib/authClient";

/**
 * Fetches the server's published required-consents catalogue once on mount.
 * Returned versions are stamped server-side at acceptance time, so they're
 * intentionally NOT shown to the user — the UI shows the human-readable
 * document title and links to the legal page, nothing more.
 */
export function useRequiredConsents(): {
    consents: RequiredConsents | null;
    loadError: string | null;
} {
    const [consents, setConsents] = useState<RequiredConsents | null>(null);
    const [loadError, setLoadError] = useState<string | null>(null);

    useEffect(() => {
        let cancelled = false;
        getRequiredConsents()
            .then((response) => {
                if (!cancelled) setConsents(response);
            })
            .catch((caught: unknown) => {
                if (!cancelled) {
                    setLoadError(
                        caught instanceof Error ? caught.message : "Couldn't load consents",
                    );
                }
            });
        return () => {
            cancelled = true;
        };
    }, []);

    return { consents, loadError };
}
