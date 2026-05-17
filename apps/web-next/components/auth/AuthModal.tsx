"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { Modal } from "./Modal";
import { LoginFlow } from "./LoginFlow";
import { RegisterFlow } from "./RegisterFlow";
import { ForgotPasswordFlow } from "./ForgotPasswordFlow";

export type AuthView = "login" | "register" | "forgot";

interface Props {
    isOpen: boolean;
    onClose: () => void;
    initialView: AuthView;
}

/**
 * Single-modal host for the entire auth journey. Owns which flow is
 * currently mounted; each flow signals back via callbacks when the user
 * wants to switch to another flow or has completed the journey.
 *
 * On success the user is sent to {@code /app} (placeholder) — wire that
 * to the real signed-in surface when it exists.
 */
export function AuthModal({ isOpen, onClose, initialView }: Props) {
    const router = useRouter();
    const [view, setView] = useState<AuthView>(initialView);
    const [seedIdentifier, setSeedIdentifier] = useState("");

    // Reset to the requested view every time the modal opens.
    function handleOpenChange() {
        if (isOpen) {
            setView(initialView);
        }
    }

    if (isOpen && view !== initialView && seedIdentifier === "") {
        // initialView changed externally while still in the same modal session —
        // align the inner view. (Cheap effect; this branch is rarely hit.)
        handleOpenChange();
    }

    function onAuthenticated() {
        onClose();
        router.push("/app");
    }

    return (
        <Modal isOpen={isOpen} onClose={onClose} labelledBy="auth-modal-title">
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
                    initialIdentifier={seedIdentifier}
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
