"use client";

import { useTranslation } from "@/lib/i18n/I18nProvider";
import styles from "./SocialButtons.module.css";

interface Props {
    /** Surface this is rendered on; switches CTA strings. */
    variant: "login" | "register";
    onGoogle: () => void;
    onApple: () => void;
    disabled?: boolean;
}

/**
 * Google + Apple sign-in buttons. The actual OAuth dance happens on the
 * provider side (popup or full redirect) — these buttons just trigger
 * the host's handler so we can centralize where the popup opens.
 */
export function SocialButtons({ variant, onGoogle, onApple, disabled }: Props) {
    const { t } = useTranslation();
    const labels = variant === "login" ? t.auth.login : t.auth.register;
    const intro = labels.socialIntro;

    return (
        <div className={styles.wrap}>
            <div className={styles.divider} aria-hidden>
                <span className={styles.dividerText}>{intro}</span>
            </div>
            <div className={styles.row}>
                <button
                    type="button"
                    onClick={onGoogle}
                    disabled={disabled}
                    className={[styles.btn, styles.google].join(" ")}
                    aria-label={labels.googleCta}
                >
                    <GoogleGlyph />
                    <span>{labels.googleCta}</span>
                </button>
                <button
                    type="button"
                    onClick={onApple}
                    disabled={disabled}
                    className={[styles.btn, styles.apple].join(" ")}
                    aria-label={labels.appleCta}
                >
                    <AppleGlyph />
                    <span>{labels.appleCta}</span>
                </button>
            </div>
        </div>
    );
}

function GoogleGlyph() {
    return (
        <svg
            aria-hidden
            viewBox="0 0 18 18"
            className={styles.glyph}
            xmlns="http://www.w3.org/2000/svg"
        >
            <path
                fill="#4285F4"
                d="M17.64 9.205c0-.638-.057-1.252-.164-1.841H9v3.481h4.844c-.209 1.125-.843 2.078-1.796 2.717v2.258h2.908c1.702-1.566 2.684-3.874 2.684-6.615z"
            />
            <path
                fill="#34A853"
                d="M9 18c2.43 0 4.467-.806 5.956-2.18l-2.908-2.258c-.806.54-1.836.859-3.048.859-2.344 0-4.328-1.584-5.036-3.711H.957v2.332C2.438 15.983 5.482 18 9 18z"
            />
            <path
                fill="#FBBC05"
                d="M3.964 10.71A5.41 5.41 0 0 1 3.682 9c0-.593.102-1.17.282-1.71V4.958H.957A8.997 8.997 0 0 0 0 9c0 1.452.348 2.827.957 4.042l3.007-2.332z"
            />
            <path
                fill="#EA4335"
                d="M9 3.58c1.321 0 2.508.454 3.44 1.345l2.582-2.58C13.463.891 11.426 0 9 0 5.482 0 2.438 2.017.957 4.958L3.964 7.29C4.672 5.163 6.656 3.58 9 3.58z"
            />
        </svg>
    );
}

function AppleGlyph() {
    return (
        <svg
            aria-hidden
            viewBox="0 0 16 18"
            className={styles.glyph}
            xmlns="http://www.w3.org/2000/svg"
        >
            <path
                fill="currentColor"
                d="M13.62 9.43c-.02-2.11 1.72-3.12 1.8-3.17-.98-1.43-2.5-1.63-3.04-1.65-1.29-.13-2.52.76-3.17.76-.66 0-1.67-.74-2.74-.72-1.41.02-2.71.82-3.43 2.08-1.47 2.54-.37 6.29 1.04 8.36.7 1.01 1.52 2.14 2.6 2.1 1.04-.04 1.44-.67 2.7-.67 1.26 0 1.62.67 2.72.65 1.13-.02 1.84-1.02 2.52-2.04.8-1.17 1.13-2.31 1.14-2.37-.03-.01-2.18-.83-2.2-3.33zM11.5 3.27c.58-.7.97-1.66.86-2.62-.83.03-1.83.55-2.43 1.24-.53.61-.99 1.59-.87 2.53.92.07 1.86-.47 2.44-1.15z"
            />
        </svg>
    );
}
