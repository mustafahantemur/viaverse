"use client";

import { useCallback, useState } from "react";
import { formatError } from "@/lib/formatError";

/**
 * Wraps an async function with {@code pending} + {@code error} state and
 * a guard against overlapping in-flight calls. Returns the same callback
 * type so it drops in unchanged where you previously called the async fn.
 *
 * `cause` exposes the raw thrown value so callers can run their own
 * localized formatter via {@code describeError}; `error` is the legacy
 * server-supplied detail kept around for back-compat.
 */
export function useAsyncCallback<TArgs extends unknown[], TResult>(
    fn: (...args: TArgs) => Promise<TResult>,
): {
    run: (...args: TArgs) => Promise<TResult | undefined>;
    pending: boolean;
    error: string | null;
    cause: unknown;
    reset: () => void;
} {
    const [pending, setPending] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [cause, setCause] = useState<unknown>(null);

    const run = useCallback(
        async (...args: TArgs) => {
            if (pending) return undefined;
            setPending(true);
            setError(null);
            setCause(null);
            try {
                return await fn(...args);
            } catch (caught) {
                setError(formatError(caught));
                setCause(caught);
                return undefined;
            } finally {
                setPending(false);
            }
        },
        [fn, pending],
    );

    const reset = useCallback(() => {
        setError(null);
        setCause(null);
    }, []);

    return { run, pending, error, cause, reset };
}
