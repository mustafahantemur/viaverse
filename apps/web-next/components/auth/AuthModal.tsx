"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { Modal } from "./Modal";
import { LoginFlow } from "./LoginFlow";
import { RegisterFlow } from "./RegisterFlow";
import { ForgotPasswordFlow } from "./ForgotPasswordFlow";
import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./AuthModal.module.css";

export type AuthView = "login" | "register" | "forgot";

interface Props {
    isOpen: boolean;
    onClose: () => void;
    initialView: AuthView;
}

/**
 * Tabbed auth modal hosting login + register + forgot-password. Closes
 * only via the X button or Escape — the underlying {@link Modal}
 * intentionally ignores overlay clicks so an accidental click outside
 * doesn't lose a half-filled signup form.
 */
export function AuthModal({ isOpen, onClose, initialView }: Props) {
    const router = useRouter();
    const { t } = useTranslation();
    const [view, setView] = useState<AuthView>(initialView);
    const [seedIdentifier, setSeedIdentifier] = useState("");

    // Re-align internal view whenever the modal is reopened with a new request.
    useEffect(() => {
        if (isOpen) {
            setView(initialView);
            setSeedIdentifier("");
        }
    }, [isOpen, initialView]);

    function onAuthenticated() {
        onClose();
        router.push("/app");
    }

    const showTabs = view === "login" || view === "register";

    return (
        <Modal isOpen={isOpen} onClose={onClose} labelledBy="auth-modal-title">
            {showTabs && (
                <div className={styles.tabs} role="tablist">
                    <button
                        type="button"
                        role="tab"
                        aria-selected={view === "login"}
                        className={[styles.tab, view === "login" && styles.tabActive]
                            .filter(Boolean)
                            .join(" ")}
                        onClick={() => setView("login")}
                    >
                        {t.auth.modal.tabSignIn}
                    </button>
                    <button
                        type="button"
                        role="tab"
                        aria-selected={view === "register"}
                        className={[styles.tab, view === "register" && styles.tabActive]
                            .filter(Boolean)
                            .join(" ")}
                        onClick={() => setView("register")}
                    >
                        {t.auth.modal.tabCreate}
                    </button>
                </div>
            )}

            {view === "login" && (
                <LoginFlow
                    initialIdentifier={seedIdentifier}
                    onAuthenticated={onAuthenticated}
                    onSwitchToRegister={(identifier) => {
                        setSeedIdentifier(identifier);
                        setView("register");
                    }}
                    onForgotPassword={(identifier) => {
                        setSeedIdentifier(identifier);
                        setView("forgot");
                    }}
                />
            )}
            {view === "register" && (
                <RegisterFlow
                    onRegistered={onAuthenticated}
                    onSwitchToLogin={(identifier) => {
                        setSeedIdentifier(identifier);
                        setView("login");
                    }}
                />
            )}
            {view === "forgot" && (
                <ForgotPasswordFlow
                    initialIdentifier={seedIdentifier}
                    onDone={() => setView("login")}
                    onBackToLogin={(identifier) => {
                        setSeedIdentifier(identifier);
                        setView("login");
                    }}
                />
            )}
        </Modal>
    );
}
