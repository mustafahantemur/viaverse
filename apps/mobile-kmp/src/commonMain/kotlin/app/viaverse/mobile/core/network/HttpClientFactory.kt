package app.viaverse.mobile.core.network

import io.ktor.client.HttpClient

/**
 * Builds the platform-appropriate Ktor HTTP client. We declare it as
 * `expect` so each target picks an engine explicitly (CIO on desktop,
 * OkHttp on Android) — relying on Ktor's ServiceLoader auto-discovery
 * is fragile across builds and silently falls back to MockEngine if it
 * can't find one, which then breaks every API call without a useful
 * error.
 */
expect fun newHttpClient(): HttpClient
