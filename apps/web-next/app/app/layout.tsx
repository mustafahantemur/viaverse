import type { ReactNode } from "react";
import { ProductAppShell } from "@/components/product/ProductAppShell";

export default function AuthenticatedAppLayout({ children }: { children: ReactNode }) {
    return <ProductAppShell>{children}</ProductAppShell>;
}
