package app.viaverse.mobile.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

actual fun newHttpClient(): HttpClient = HttpClient(CIO)
