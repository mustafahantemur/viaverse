"use client";

import { useCallback, useState } from "react";

/** Open/close tuple for modals, dropdowns, drawers. */
export function useDisclosure(initial = false) {
    const [isOpen, setOpen] = useState(initial);
    const open = useCallback(() => setOpen(true), []);
    const close = useCallback(() => setOpen(false), []);
    const toggle = useCallback(() => setOpen((value) => !value), []);
    return { isOpen, open, close, toggle };
}
