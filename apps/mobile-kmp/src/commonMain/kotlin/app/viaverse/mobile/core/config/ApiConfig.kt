package app.viaverse.mobile.core.config

data class ApiConfig(
    val baseUrl: String,
    val environment: AppEnvironment,
) {
    companion object {
        fun local(): ApiConfig = ApiConfig(
            baseUrl = "http://localhost:8101",
            environment = AppEnvironment.Local,
        )
    }
}

