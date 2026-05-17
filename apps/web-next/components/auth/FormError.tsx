import type { ReactNode } from "react";

export function FormError({ children }: { children: ReactNode }) {
    if (!children) return null;
    return (
        <div
            role="alert"
            style={{
                background: "rgba(239, 68, 68, 0.08)",
                color: "var(--vv-danger)",
                padding: "10px 12px",
                borderRadius: "var(--vv-radius-sm)",
                fontSize: 13,
                fontWeight: 500,
                lineHeight: 1.4,
            }}
        >
            {children}
        </div>
    );
}
