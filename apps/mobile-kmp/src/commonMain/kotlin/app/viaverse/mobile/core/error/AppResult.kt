package app.viaverse.mobile.core.error

sealed interface AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>

    data class Failure(val error: AppClientError) : AppResult<Nothing>
}

