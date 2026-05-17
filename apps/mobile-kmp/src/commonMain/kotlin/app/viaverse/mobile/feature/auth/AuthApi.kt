package app.viaverse.mobile.feature.auth

import app.viaverse.mobile.core.config.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Plain Ktor calls to the BFF (web-bff). We hand-build/parse JSON via
 * {@code JsonObject} so we don't need the kotlinx-serialization compiler
 * plugin in this module — a small surface, so the plugin is overkill.
 *
 * The BFF wraps successful responses in {@code { "success": true, "data": … }};
 * {@link #unwrapData} pulls {@code data} out so callers see the raw payload.
 * Tokens issued by credential endpoints are auto-stored in {@link AuthTokens}.
 */
class AuthApi(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig,
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun start(identifier: String): JsonObject =
        postJson("/api/auth/start", buildJsonObject { put("identifier", JsonPrimitive(identifier)) })

    suspend fun passwordLogin(identifier: String, password: String): JsonObject {
        val response = postJson("/api/auth/password-login", buildJsonObject {
            put("identifier", JsonPrimitive(identifier))
            put("password", JsonPrimitive(password))
        })
        storeTokens(response)
        return response
    }

    suspend fun verifyTotp(partialAuthToken: String, totpCode: String): JsonObject {
        val response = postJson("/api/auth/verify-totp", buildJsonObject {
            put("partialAuthToken", JsonPrimitive(partialAuthToken))
            put("totpCode", JsonPrimitive(totpCode))
        })
        storeTokens(response)
        return response
    }

    suspend fun verifyOtp(flowId: String, otp: String): JsonObject =
        postJson("/api/auth/verify-otp", buildJsonObject {
            put("flowId", JsonPrimitive(flowId))
            put("otp", JsonPrimitive(otp))
        })

    suspend fun register(
        registrationToken: String,
        displayName: String,
        firstName: String?,
        lastName: String?,
        password: String,
        acceptedRequiredConsents: List<String>,
        marketingConsentAccepted: Boolean,
    ): JsonObject {
        val response = postJson("/api/auth/register", buildJsonObject {
            put("registrationToken", JsonPrimitive(registrationToken))
            put("displayName", JsonPrimitive(displayName))
            firstName?.let { put("firstName", JsonPrimitive(it)) }
            lastName?.let { put("lastName", JsonPrimitive(it)) }
            put("password", JsonPrimitive(password))
            put("acceptedRequiredConsents", buildJsonArray {
                acceptedRequiredConsents.forEach { add(JsonPrimitive(it)) }
            })
            put("marketingConsentAccepted", JsonPrimitive(marketingConsentAccepted))
        })
        storeTokens(response)
        return response
    }

    suspend fun requiredConsents(): JsonObject {
        val response = httpClient.get(apiConfig.baseUrl + "/api/auth/required-consents")
        val text = response.bodyAsText()
        if (!response.status.isSuccess()) {
            throw AuthApiException(response.status.value, text)
        }
        return unwrapData(text)
    }

    private suspend fun postJson(path: String, body: JsonObject): JsonObject {
        val response = httpClient.post(apiConfig.baseUrl + path) {
            contentType(ContentType.Application.Json)
            val token = AuthTokens.accessToken
            if (token != null) {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }
            setBody(body.toString())
        }
        val text = response.bodyAsText()
        if (!response.status.isSuccess()) {
            throw AuthApiException(response.status.value, text)
        }
        return unwrapData(text)
    }

    private fun unwrapData(raw: String): JsonObject {
        if (raw.isBlank()) return buildJsonObject {}
        val element = json.parseToJsonElement(raw)
        val obj = element.jsonObject
        val data = obj["data"]
        if (data is JsonObject) return data
        return obj
    }

    private fun storeTokens(body: JsonObject) {
        val access = body["accessToken"]?.jsonPrimitive?.contentOrNullSafe()
        val refresh = body["refreshToken"]?.jsonPrimitive?.contentOrNullSafe()
        if (access != null || refresh != null) {
            AuthTokens.store(access, refresh)
        }
    }
}

private fun JsonPrimitive.contentOrNullSafe(): String? =
    if (isString) content else if (content == "null") null else content

class AuthApiException(val status: Int, val rawBody: String) : RuntimeException(rawBody)
