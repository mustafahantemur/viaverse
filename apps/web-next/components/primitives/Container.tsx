import type { ReactNode } from "react";
import styles from "./Container.module.css";

/** Caps content at the design system shell width and applies the responsive gutters. */
export function Container({
    children,
    className,
}: {
    children: ReactNode;
    className?: string;
}) {
    return <div className={[styles.shell, className].filter(Boolean).join(" ")}>{children}</div>;
}
