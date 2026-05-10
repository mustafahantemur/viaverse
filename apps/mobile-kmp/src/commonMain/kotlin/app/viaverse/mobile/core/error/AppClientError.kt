package app.viaverse.mobile.core.error

sealed interface AppClientError {
    data class Network(val message: String) : AppClientError

    data class Unexpected(val message: String) : AppClientError
}

