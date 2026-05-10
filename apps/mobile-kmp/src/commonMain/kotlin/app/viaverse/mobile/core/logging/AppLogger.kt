package app.viaverse.mobile.core.logging

interface AppLogger {
    fun info(message: String)

    fun warn(message: String, cause: Throwable? = null)

    fun error(message: String, cause: Throwable? = null)
}

