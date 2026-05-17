package app.viaverse.mobile.core.config

data class ApiConfig(
    val baseUrl: String,
    val environment: AppEnvironment,
) {
    companion object {
        // Mobile clients hit the BFF (web-bff) rather than identity-service
        // directly. BFF runs on :8001 in local dev.
        fun local(): ApiConfig = ApiConfig(
            baseUrl = "http://localhost:8001",
            environment = AppEnvironment.Local,
        )
    }
}

