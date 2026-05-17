"use client";

import { useEffect, useRef, useState } from "react";
import type { CSSProperties, ReactNode } from "react";

/**
 * Fades a child in + slides it up the first time it intersects the
 * viewport. Cheap IntersectionObserver — no framer-motion dependency.
 * Respects {@code prefers-reduced-motion} by skipping the animation
 * (the global stylesheet enforces the same cutoff).
 */
export function Reveal({
    children,
    delayMs = 0,
    asTag: Tag = "div",
    className,
}: {
    children: ReactNode;
    /** Stagger the reveal — used to cascade siblings. */
    delayMs?: number;
    asTag?: "div" | "section" | "article";
    className?: string;
}) {
    const ref = useRef<HTMLDivElement | null>(null);
    const [visible, setVisible] = useState(false);

    useEffect(() => {
        const node = ref.current;
        if (!node) return;
        const observer = new IntersectionObserver(
            (entries) => {
                entries.forEach((entry) => {
                    if (entry.isIntersecting) {
                        setVisible(true);
                        observer.unobserve(entry.target);
                    }
                });
            },
            { threshold: 0.15, rootMargin: "0px 0px -10% 0px" },
        );
        observer.observe(node);
        return () => observer.disconnect();
    }, []);

    const style: CSSProperties = {
        opacity: visible ? 1 : 0,
        transform: visible ? "translateY(0)" : "translateY(16px)",
        transition: `opacity 540ms cubic-bezier(0.22, 1, 0.36, 1) ${delayMs}ms, transform 640ms cubic-bezier(0.22, 1, 0.36, 1) ${delayMs}ms`,
        willChange: "transform, opacity",
    };

    return (
        <Tag ref={ref as never} className={className} style={style}>
            {children}
        </Tag>
    );
}
