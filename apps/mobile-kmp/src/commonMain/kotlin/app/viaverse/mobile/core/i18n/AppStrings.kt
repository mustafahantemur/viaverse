package app.viaverse.mobile.core.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Centralized UI strings for the auth surface. Turkish is primary;
 * English mirrors it. Each property returns the active locale's value,
 * read from {@link LocalAppLanguage}.
 *
 * String selection is intentionally compile-time — no lookup table,
 * no key collisions — so renames/typos break at the call site.
 */
object AppStrings {

    @Composable @ReadOnlyComposable
    fun signIn(): String = pick("Giriş yap", "Sign in")

    @Composable @ReadOnlyComposable
    fun createAccount(): String = pick("Hesap oluştur", "Create account")

    @Composable @ReadOnlyComposable
    fun emailOrPhone(): String = pick("E-posta veya telefon", "Email or phone")

    @Composable @ReadOnlyComposable
    fun password(): String = pick("Parola", "Password")

    @Composable @ReadOnlyComposable
    fun confirmPassword(): String = pick("Parola (tekrar)", "Password (again)")

    @Composable @ReadOnlyComposable
    fun forgotPassword(): String = pick("Parolanı mı unuttun?", "Forgot password?")

    @Composable @ReadOnlyComposable
    fun signingIn(): String = pick("Giriş yapılıyor…", "Signing in…")

    @Composable @ReadOnlyComposable
    fun verify(): String = pick("Doğrula", "Verify")

    @Composable @ReadOnlyComposable
    fun verifying(): String = pick("Doğrulanıyor…", "Verifying…")

    @Composable @ReadOnlyComposable
    fun totpHelp(): String =
        pick("Authenticator uygulamandaki 6 haneli kodu gir.",
             "Enter the 6-digit code from your authenticator app.")

    @Composable @ReadOnlyComposable
    fun noAccountCreateOne(): String =
        pick("Hesabın yok mu? Hesap oluştur", "No account? Create one")

    @Composable @ReadOnlyComposable
    fun alreadyHaveAccountSignIn(): String =
        pick("Zaten hesabın var mı? Giriş yap", "Already have an account? Sign in")

    // Register
    @Composable @ReadOnlyComposable
    fun firstName(): String = pick("Ad", "First name")

    @Composable @ReadOnlyComposable
    fun lastName(): String = pick("Soyad", "Last name")

    @Composable @ReadOnlyComposable
    fun displayName(): String = pick("Görünen ad", "Display name")

    @Composable @ReadOnlyComposable
    fun email(): String = pick("E-posta", "Email")

    @Composable @ReadOnlyComposable
    fun phoneOptional(): String = pick("Telefon (isteğe bağlı)", "Phone (optional)")

    @Composable @ReadOnlyComposable
    fun passwordHint(): String =
        pick("En az 10 karakter, harf ve rakam içermeli.",
             "At least 10 characters, including letters and digits.")

    @Composable @ReadOnlyComposable
    fun passwordsDontMatch(): String =
        pick("Parolalar uyuşmuyor.", "Passwords do not match.")

    @Composable @ReadOnlyComposable
    fun continueLabel(): String = pick("Devam et", "Continue")

    @Composable @ReadOnlyComposable
    fun submitting(): String = pick("Gönderiliyor…", "Submitting…")

    @Composable @ReadOnlyComposable
    fun creatingAccount(): String = pick("Hesap oluşturuluyor…", "Creating account…")

    // OTP
    @Composable @ReadOnlyComposable
    fun verificationCode(): String = pick("Doğrulama kodu", "Verification code")

    @Composable @ReadOnlyComposable
    fun emailOtpSubtitle(email: String): String =
        pick("$email adresine 6 haneli kod gönderdik.",
             "We sent a 6-digit code to $email.")

    @Composable @ReadOnlyComposable
    fun phoneOtpSubtitle(phone: String): String =
        pick("$phone numarasına SMS gönderdik.",
             "We sent an SMS to $phone.")

    @Composable @ReadOnlyComposable
    fun mailpitDevHint(): String =
        pick("Yerel geliştirmede Mailpit'i aç: http://localhost:8025",
             "In local dev, open Mailpit at http://localhost:8025")

    // Forgot
    @Composable @ReadOnlyComposable
    fun resetPassword(): String = pick("Parolanı sıfırla", "Reset password")

    @Composable @ReadOnlyComposable
    fun sendCode(): String = pick("Kod gönder", "Send code")

    @Composable @ReadOnlyComposable
    fun resetPasswordCta(): String = pick("Parolayı sıfırla", "Reset password")

    @Composable @ReadOnlyComposable
    fun backToLogin(): String = pick("Girişe dön", "Back to sign in")

    // Misc
    @Composable @ReadOnlyComposable
    fun optionalMarketing(): String =
        pick("(İsteğe bağlı) Kampanya ve duyuru e-postaları al",
             "(Optional) Send me product updates")

    @Composable @ReadOnlyComposable
    fun logOut(): String = pick("Çıkış yap", "Log out")

    @Composable @ReadOnlyComposable
    fun signedIn(): String = pick("Giriş yapıldı", "Signed in")

    @Composable @ReadOnlyComposable
    fun toggleLanguage(): String = pick("English", "Türkçe")

    @Composable @ReadOnlyComposable
    fun toggleTheme(): String = pick("Tema değiştir", "Toggle theme")

    @Composable @ReadOnlyComposable
    fun splashTapToContinue(): String =
        pick("Devam etmek için dokun", "Tap to continue")

    @Composable @ReadOnlyComposable
    fun loginSubtitle(): String =
        pick("E-posta veya telefonun ve parolanla devam et.",
             "Continue with your email or phone and password.")

    @Composable @ReadOnlyComposable
    fun registerSubtitle(): String =
        pick("Bilgilerini gir; ardından e-posta ile doğrulayacağız.",
             "Fill in your details; we'll verify your email next.")

    @Composable @ReadOnlyComposable
    fun forgotSubtitle(): String =
        pick("E-posta veya telefonunu gir; doğrulama kodu gönderelim.",
             "Enter your email or phone; we'll send a code.")

    @Composable @ReadOnlyComposable
    fun emailOrPhonePlaceholder(): String =
        pick("ornek@viaverse.app veya 5XXXXXXXXX",
             "you@example.com or 5XXXXXXXXX")

    @Composable @ReadOnlyComposable
    fun showPasswordA11y(): String =
        pick("Parolayı göster", "Show password")

    @Composable @ReadOnlyComposable
    fun hidePasswordA11y(): String =
        pick("Parolayı gizle", "Hide password")

    @Composable @ReadOnlyComposable
    private fun pick(tr: String, en: String): String =
        if (LocalAppLanguage.current.value == AppLanguage.TR) tr else en
}
