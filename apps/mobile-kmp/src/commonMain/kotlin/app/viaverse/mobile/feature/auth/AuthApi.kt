package app.viaverse.mobile.feature.auth

import app.viaverse.mobile.core.config.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
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

    /**
     * Form-first registration. The server stashes the form data as a
     * server-side draft (Valkey-backed, ~30 min TTL) and dispatches an
     * email OTP. Phone OTP is requested in a separate step only when
     * the user supplied a phone number.
     */
    suspend fun registerStart(
        email: String,
        phone: String?,
        displayName: String,
        firstName: String?,
        lastName: String?,
        password: String,
        acceptedRequiredConsents: List<String>,
        marketingConsentAccepted: Boolean,
    ): JsonObject = postJson("/api/auth/register/start", buildJsonObject {
        put("email", JsonPrimitive(email))
        phone?.let { put("phone", JsonPrimitive(it)) }
        put("displayName", JsonPrimitive(displayName))
        firstName?.let { put("firstName", JsonPrimitive(it)) }
        lastName?.let { put("lastName", JsonPrimitive(it)) }
        put("password", JsonPrimitive(password))
        put("acceptedRequiredConsents", buildJsonArray {
            acceptedRequiredConsents.forEach { add(JsonPrimitive(it)) }
        })
        put("marketingConsentAccepted", JsonPrimitive(marketingConsentAccepted))
    })

    suspend fun registerVerifyEmail(draftId: String, otp: String): JsonObject {
        val response = postJson("/api/auth/register/verify-email", buildJsonObject {
            put("draftId", JsonPrimitive(draftId))
            put("otp", JsonPrimitive(otp))
        })
        storeTokens(response)
        return response
    }

    suspend fun registerVerifyPhone(draftId: String, otp: String): JsonObject {
        val response = postJson("/api/auth/register/verify-phone", buildJsonObject {
            put("draftId", JsonPrimitive(draftId))
            put("otp", JsonPrimitive(otp))
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

    /**
     * Self read for the signed-in user. Reads through the BFF, which
     * forwards to identity-service's `/me` endpoint. The access token
     * is attached automatically by {@link #getJsonAuthed}.
     */
    suspend fun me(): JsonObject = getJsonAuthed("/api/me")

    /**
     * Rich self profile read from profile-service via web-bff.
     */
    suspend fun profile(): JsonObject = getJsonAuthed("/api/me/profile")

    // ---- Profile: capabilities + active mode ----

    suspend fun enableIndividualProvider(
        termsVersion: String,
        serviceBlurb: String?,
    ): JsonObject = postJson("/api/me/capabilities/individual-provider/enable", buildJsonObject {
        put("acceptedProviderTermsVersion", JsonPrimitive(termsVersion))
        serviceBlurb?.let { put("serviceBlurb", JsonPrimitive(it)) }
    })

    suspend fun updateActiveMode(mode: String): JsonObject =
        patchJson("/api/me/active-mode", buildJsonObject {
            put("activeMode", JsonPrimitive(mode))
        })

    suspend fun capabilityTerms(): JsonObject = getJsonAuthed("/api/auth/capability-terms")

    // ---- Marketplace ----

    suspend fun openServiceRequests(): JsonArray = getJsonArrayAuthed("/api/requests/open")
    suspend fun myServiceRequests(): JsonArray = getJsonArrayAuthed("/api/me/requests")
    suspend fun workFeed(): JsonArray = getJsonArrayAuthed("/api/feed/work")
    suspend fun myOffers(): JsonArray = getJsonArrayAuthed("/api/me/offers")
    suspend fun myJobs(): JsonArray = getJsonArrayAuthed("/api/me/jobs")

    suspend fun createServiceRequest(
        title: String,
        description: String,
        category: String,
        budgetMinAmountMinor: Long?,
        budgetMaxAmountMinor: Long?,
        remoteAllowed: Boolean,
        district: String?,
        city: String?,
    ): JsonObject = postJson("/api/requests", buildJsonObject {
        put("title", JsonPrimitive(title))
        put("description", JsonPrimitive(description))
        put("category", JsonPrimitive(category))
        budgetMinAmountMinor?.let { put("budgetMinAmountMinor", JsonPrimitive(it)) }
        budgetMaxAmountMinor?.let { put("budgetMaxAmountMinor", JsonPrimitive(it)) }
        put("currency", JsonPrimitive("TRY"))
        put("remoteAllowed", JsonPrimitive(remoteAllowed))
        district?.let { put("district", JsonPrimitive(it)) }
        city?.let { put("city", JsonPrimitive(it)) }
    })

    suspend fun submitOffer(
        requestId: String,
        amountMinor: Long,
        currency: String,
        message: String?,
    ): JsonObject = postJson("/api/requests/$requestId/offers", buildJsonObject {
        put("amountMinor", JsonPrimitive(amountMinor))
        put("currency", JsonPrimitive(currency))
        message?.let { put("message", JsonPrimitive(it)) }
    })

    suspend fun withdrawOffer(offerId: String): JsonObject =
        postJson("/api/offers/$offerId/withdraw", buildJsonObject {})

    suspend fun cancelServiceRequest(requestId: String): JsonObject =
        postJson("/api/requests/$requestId/cancel", buildJsonObject {})

    suspend fun startJob(jobId: String): JsonObject =
        postJson("/api/jobs/$jobId/start", buildJsonObject {})

    suspend fun completeJob(jobId: String): JsonObject =
        postJson("/api/jobs/$jobId/complete", buildJsonObject {})

    // ---- Content (social feed) ----

    suspend fun socialFeed(city: String? = null, district: String? = null): JsonArray {
        val query = buildList {
            city?.let { add("city=$it") }
            district?.let { add("district=$it") }
        }.joinToString("&")
        val suffix = if (query.isNotEmpty()) "?$query" else ""
        return getJsonArrayAuthed("/api/feed/social$suffix")
    }

    suspend fun publishedPosts(): JsonArray = getJsonArrayAuthed("/api/posts/published")
    suspend fun myPosts(): JsonArray = getJsonArrayAuthed("/api/me/posts")

    suspend fun createContentPost(
        postType: String,
        authorMode: String,
        title: String?,
        body: String,
        city: String?,
        district: String?,
    ): JsonObject = postJson("/api/posts", buildJsonObject {
        put("postType", JsonPrimitive(postType))
        put("authorMode", JsonPrimitive(authorMode))
        title?.let { put("title", JsonPrimitive(it)) }
        put("body", JsonPrimitive(body))
        city?.let { put("city", JsonPrimitive(it)) }
        district?.let { put("district", JsonPrimitive(it)) }
    })

    suspend fun recordContentInteraction(postId: String, kind: String): JsonObject =
        postJson("/api/posts/$postId/interactions", buildJsonObject {
            put("kind", JsonPrimitive(kind))
        })

    private suspend fun getJsonAuthed(path: String): JsonObject {
        val response = httpClient.get(apiConfig.baseUrl + path) {
            val token = AuthTokens.accessToken
            if (token != null) {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }
        }
        val text = response.bodyAsText()
        if (!response.status.isSuccess()) {
            throw AuthApiException(response.status.value, text)
        }
        return unwrapData(text)
    }

    /**
     * GET endpoints that return a list — the BFF envelope wraps an array
     * under `data`. We unwrap the same way as object responses.
     */
    private suspend fun getJsonArrayAuthed(path: String): JsonArray {
        val response = httpClient.get(apiConfig.baseUrl + path) {
            val token = AuthTokens.accessToken
            if (token != null) {
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }
        }
        val text = response.bodyAsText()
        if (!response.status.isSuccess()) {
            throw AuthApiException(response.status.value, text)
        }
        if (text.isBlank()) return JsonArray(emptyList())
        val element = json.parseToJsonElement(text)
        val obj = element as? JsonObject
        return when {
            obj == null -> element as JsonArray
            obj["data"] is JsonArray -> obj["data"] as JsonArray
            else -> JsonArray(emptyList())
        }
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

    private suspend fun patchJson(path: String, body: JsonObject): JsonObject {
        val response = httpClient.patch(apiConfig.baseUrl + path) {
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
