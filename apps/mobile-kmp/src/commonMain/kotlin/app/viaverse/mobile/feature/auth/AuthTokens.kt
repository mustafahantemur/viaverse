package app.viaverse.mobile.feature.auth

/**
 * Mobile keeps both tokens in memory for now; persisting them to
 * Keychain / EncryptedSharedPreferences is platform-specific work
 * that lives outside this scaffolding screen set. The BFF still
 * sets a HttpOnly refresh cookie on every credential-issuing call,
 * so the same backend serves a future web-style mobile webview
 * without changes.
 */
object AuthTokens {
    @Volatile
    var accessToken: String? = null
        private set

    @Volatile
    var refreshToken: String? = null
        private set

    fun store(access: String?, refresh: String?) {
        if (access != null) accessToken = access
        if (refresh != null) refreshToken = refresh
    }

    fun clear() {
        accessToken = null
        refreshToken = null
    }
}
