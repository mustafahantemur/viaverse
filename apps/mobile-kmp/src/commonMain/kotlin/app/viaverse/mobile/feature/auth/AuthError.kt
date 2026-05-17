package app.viaverse.mobile.feature.auth

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Pulls a human-readable message out of the JSON Problem Details body the
 * BFF surfaces. Prefers {@code detail} → {@code identityCode} → fallback.
 */
object AuthError {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    fun format(throwable: Throwable): String {
        if (throwable is AuthApiException) {
            return try {
                val body = json.parseToJsonElement(throwable.rawBody).jsonObject
                body["detail"]?.jsonPrimitive?.content
                    ?: body["identityCode"]?.jsonPrimitive?.content
                    ?: body["code"]?.jsonPrimitive?.content
                    ?: "Request failed (${throwable.status})"
            } catch (_: Throwable) {
                "Request failed (${throwable.status})"
            }
        }
        return throwable.message ?: "Something went wrong"
    }
}
