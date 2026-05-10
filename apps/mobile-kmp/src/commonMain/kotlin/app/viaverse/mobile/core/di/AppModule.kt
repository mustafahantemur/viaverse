package app.viaverse.mobile.core.di

import app.viaverse.mobile.core.config.ApiConfig
import app.viaverse.mobile.core.logging.AppLogger
import app.viaverse.mobile.core.logging.ConsoleAppLogger
import app.viaverse.mobile.core.network.HealthCheckClient
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

fun appModule(): Module = module {
    single { ApiConfig.local() }
    single<AppLogger> { ConsoleAppLogger() }
    single { HttpClient() }
    single { HealthCheckClient(get(), get()) }
}

