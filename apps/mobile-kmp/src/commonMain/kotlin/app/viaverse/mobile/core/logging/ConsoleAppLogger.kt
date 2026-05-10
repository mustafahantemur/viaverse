package app.viaverse.mobile.core.logging

class ConsoleAppLogger : AppLogger {
    override fun info(message: String) {
        println("INFO: $message")
    }

    override fun warn(message: String, cause: Throwable?) {
        println("WARN: $message")
        cause?.printStackTrace()
    }

    override fun error(message: String, cause: Throwable?) {
        println("ERROR: $message")
        cause?.printStackTrace()
    }
}

