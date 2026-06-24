package app.viaverse.mobile.core.network

import app.viaverse.mobile.core.config.ApiConfig
import app.viaverse.mobile.core.error.AppClientError
import app.viaverse.mobile.core.error.AppResult
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class HealthCheckClient(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig,
) {
    suspend fun check(): AppResult<HealthStatus> {
        return try {
            val response = httpClient.get("${apiConfig.baseUrl}/actuator/health")
            AppResult.Success(HealthStatus(healthy = response.status.value in 200..299))
        } catch (exception: Exception) {
            AppResult.Failure(AppClientError.Network(exception.message ?: "Health check failed"))
        }
    }
}

