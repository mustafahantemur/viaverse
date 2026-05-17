"use client";

import { useCallback, useState } from "react";
import { formatError } from "@/lib/formatError";

/**
 * Wraps an async function with {@code pending} + {@code error} state and
 * a guard against overlapping in-flight calls. Returns the same callback
 * type so it drops in unchanged where you previously called the async fn.
 *
 * Pattern lifted instead of repeated in every screen — historically each
 * form re-implemented its own busy/error pair with subtle differences.
 */
export function useAsyncCallback<TArgs extends unknown[], TResult>(
    fn: (...args: TArgs) => Promise<TResult>,
): {
    run: (...args: TArgs) => Promise<TResult | undefined>;
    pending: boolean;
    error: string | null;
    reset: () => void;
} {
    const [pending, setPending] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const run = useCallback(
        async (...args: TArgs) => {
            if (pending) return undefined;
            setPending(true);
            setError(null);
            try {
                return await fn(...args);
            } catch (caught) {
                setError(formatError(caught));
                return undefined;
            } finally {
                setPending(false);
            }
        },
        [fn, pending],
    );

    const reset = useCallback(() => setError(null), []);

    return { run, pending, error, reset };
}
