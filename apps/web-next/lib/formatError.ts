import { ApiCallError } from "./authClient";

/**
 * Turn whatever was thrown into a single user-facing string. Prefers the
 * server-supplied {@code detail}, falls back to {@code identityCode}, and
 * finally to a generic message. All UI surfaces should funnel through this
 * so error copy stays consistent.
 */
export function formatError(caught: unknown): string {
    if (caught instanceof ApiCallError) {
        return (
            caught.error.detail ??
            caught.error.identityCode ??
            caught.error.code ??
            caught.message
        );
    }
    if (caught instanceof Error) {
        return caught.message;
    }
    return "Something went wrong";
}
