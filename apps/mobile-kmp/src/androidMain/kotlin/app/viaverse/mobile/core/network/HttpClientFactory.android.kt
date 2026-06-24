package app.viaverse.mobile.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual fun newHttpClient(): HttpClient = HttpClient(OkHttp)
