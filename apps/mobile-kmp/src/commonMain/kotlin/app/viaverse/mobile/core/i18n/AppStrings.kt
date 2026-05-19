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

    // Home / post-auth welcome surface
    @Composable @ReadOnlyComposable
    fun welcomeHeading(name: String): String =
        pick("Hoş geldin, $name", "Welcome, $name")

    @Composable @ReadOnlyComposable
    fun welcomeSubtitleNew(): String =
        pick("Hesabın hazır. Aşağıdan başlayabilirsin.",
             "Your account is ready. Start with one of the cards below.")

    @Composable @ReadOnlyComposable
    fun welcomeSubtitleReturning(): String =
        pick("Tekrar hoş geldin. Bugün ne yapmak istersin?",
             "Welcome back. What would you like to do today?")

    @Composable @ReadOnlyComposable
    fun customerModeBadge(): String = pick("Müşteri modu", "Customer mode")

    @Composable @ReadOnlyComposable
    fun activeModeBadge(activeMode: String): String = when (activeMode) {
        "INDIVIDUAL_PROVIDER" -> pick("Hizmet veren modu", "Provider mode")
        "BUSINESS" -> pick("İşletme modu", "Business mode")
        else -> customerModeBadge()
    }

    // Bottom nav
    @Composable @ReadOnlyComposable
    fun navHome(): String = pick("Ana sayfa", "Home")

    @Composable @ReadOnlyComposable
    fun navFeed(): String = pick("Akış", "Feed")

    @Composable @ReadOnlyComposable
    fun navMarketplace(): String = pick("İşler", "Jobs")

    @Composable @ReadOnlyComposable
    fun navProfile(): String = pick("Profil", "Profile")

    // Profile screen
    @Composable @ReadOnlyComposable
    fun profileTitle(): String = pick("Profil ve modlar", "Profile & modes")

    @Composable @ReadOnlyComposable
    fun profileSubtitle(): String =
        pick("Aynı hesapla müşteri, hizmet veren ve işletme yüzlerini yönet.",
             "Same account, three faces — customer, provider, business.")

    @Composable @ReadOnlyComposable
    fun providerCapability(): String = pick("Hizmet veren modu", "Provider mode")

    @Composable @ReadOnlyComposable
    fun providerEnableCta(): String =
        pick("Hizmet vermeye başla", "Become a provider")

    @Composable @ReadOnlyComposable
    fun providerEnabled(): String = pick("Açık", "Enabled")

    @Composable @ReadOnlyComposable
    fun businessCapability(): String = pick("İşletme modu", "Business mode")

    @Composable @ReadOnlyComposable
    fun businessOnboardingHint(): String =
        pick("İşletme açmak için web tarafından devam et.",
             "Continue business onboarding on the web for now.")

    @Composable @ReadOnlyComposable
    fun modeSwitchHint(): String =
        pick("İşletme hesabın olsa bile istediğin an müşteri moduna dönebilirsin.",
             "Even with a business account, you can drop back to customer mode anytime.")

    @Composable @ReadOnlyComposable
    fun completenessLabel(): String = pick("Profil tamamlanma", "Profile completeness")

    // Marketplace screen
    @Composable @ReadOnlyComposable
    fun marketplaceTitle(): String = pick("İşler", "Marketplace")

    @Composable @ReadOnlyComposable
    fun marketplaceSubtitle(): String =
        pick("Talep oluştur, açık işlere teklif ver, kabul edilen işleri takip et.",
             "Create requests, send offers on open jobs, track accepted work.")

    @Composable @ReadOnlyComposable
    fun marketplaceOpen(): String = pick("Açık talepler", "Open requests")

    @Composable @ReadOnlyComposable
    fun marketplaceMine(): String = pick("Benim taleplerim", "My requests")

    @Composable @ReadOnlyComposable
    fun marketplaceMyOffers(): String = pick("Benim tekliflerim", "My offers")

    @Composable @ReadOnlyComposable
    fun marketplaceMyJobs(): String = pick("Aktif işlerim", "My jobs")

    @Composable @ReadOnlyComposable
    fun marketplaceNoOpen(): String =
        pick("Şu an açık talep yok.", "No open requests right now.")

    @Composable @ReadOnlyComposable
    fun marketplaceNoMine(): String =
        pick("Henüz bir talep oluşturmadın.", "You haven't posted any requests yet.")

    @Composable @ReadOnlyComposable
    fun marketplaceNoOffers(): String =
        pick("Aktif teklifin yok.", "You have no active offers.")

    @Composable @ReadOnlyComposable
    fun marketplaceNoJobs(): String =
        pick("Aktif işin yok.", "No active jobs.")

    // Feed
    @Composable @ReadOnlyComposable
    fun feedTitle(): String = pick("Sosyal akış", "Social feed")

    @Composable @ReadOnlyComposable
    fun feedSubtitle(): String =
        pick("Çevrendeki duyurular, etkinlikler ve organik paylaşımlar.",
             "Announcements, events, and organic posts from your area.")

    @Composable @ReadOnlyComposable
    fun feedEmpty(): String =
        pick("Çevrende henüz paylaşım yok.", "Nothing posted around you yet.")

    @Composable @ReadOnlyComposable
    fun feedCreate(): String = pick("Yeni paylaşım", "New post")

    @Composable @ReadOnlyComposable
    fun feedPostBody(): String = pick("Ne paylaşmak istersin?", "What's on your mind?")

    @Composable @ReadOnlyComposable
    fun feedPostSubmit(): String = pick("Paylaş", "Post")

    @Composable @ReadOnlyComposable
    fun emptyFeed(): String =
        pick("Çevrende henüz aktivite yok — ilk talebi sen oluştur.",
             "Nothing happening nearby yet — be the first to post a request.")

    @Composable @ReadOnlyComposable
    fun actionPostRequestTitle(): String = pick("Talep oluştur", "Post a request")

    @Composable @ReadOnlyComposable
    fun actionPostRequestDesc(): String =
        pick("Yakındaki birinden yardım iste.", "Ask for help from someone near you.")

    @Composable @ReadOnlyComposable
    fun actionBrowseJobsTitle(): String = pick("Açık işlere göz at", "Browse open jobs")

    @Composable @ReadOnlyComposable
    fun actionBrowseJobsDesc(): String =
        pick("Çevrendeki taleplere teklif ver.", "Send offers on local requests.")

    @Composable @ReadOnlyComposable
    fun actionBecomeProviderTitle(): String =
        pick("Hizmet vermeye başla", "Become a provider")

    @Composable @ReadOnlyComposable
    fun actionBecomeProviderDesc(): String =
        pick("Tek tıkla provider modunu aç.", "Turn on provider mode in one tap.")

    @Composable @ReadOnlyComposable
    fun actionSettingsTitle(): String = pick("Profil ve ayarlar", "Profile & settings")

    @Composable @ReadOnlyComposable
    fun actionSettingsDesc(): String =
        pick("Görünen ad, dil, tema ve gizlilik.",
             "Display name, language, theme, privacy.")

    @Composable @ReadOnlyComposable
    private fun pick(tr: String, en: String): String =
        if (LocalAppLanguage.current.value == AppLanguage.TR) tr else en
}
