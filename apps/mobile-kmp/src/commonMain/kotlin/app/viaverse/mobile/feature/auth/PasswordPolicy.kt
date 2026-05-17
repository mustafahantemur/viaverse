package app.viaverse.mobile.feature.auth

/**
 * Client-side mirror of the server PasswordPolicy. Server is still the
 * final authority — we re-validate here only for early UX feedback so
 * the "Create account" button can light up the moment the user has
 * typed something acceptable.
 */
object PasswordPolicy {
    const val MIN_LENGTH = 10
    const val MAX_LENGTH = 128

    enum class Issue {
        TOO_SHORT,
        TOO_LONG,
        MISSING_LOWER,
        MISSING_UPPER,
        MISSING_DIGIT,
        MISSING_SYMBOL,
    }

    data class Evaluation(val issues: List<Issue>) {
        val isValid: Boolean get() = issues.isEmpty()
    }

    fun evaluate(password: String): Evaluation {
        val issues = buildList {
            if (password.length < MIN_LENGTH) add(Issue.TOO_SHORT)
            if (password.length > MAX_LENGTH) add(Issue.TOO_LONG)
            if (password.none(Char::isLowerCase)) add(Issue.MISSING_LOWER)
            if (password.none(Char::isUpperCase)) add(Issue.MISSING_UPPER)
            if (password.none(Char::isDigit)) add(Issue.MISSING_DIGIT)
            if (password.none { !it.isLetterOrDigit() && !it.isWhitespace() }) add(Issue.MISSING_SYMBOL)
        }
        return Evaluation(issues)
    }

    fun describe(issue: Issue): String = when (issue) {
        Issue.TOO_SHORT -> "At least $MIN_LENGTH characters"
        Issue.TOO_LONG -> "At most $MAX_LENGTH characters"
        Issue.MISSING_LOWER -> "A lowercase letter"
        Issue.MISSING_UPPER -> "An uppercase letter"
        Issue.MISSING_DIGIT -> "A digit"
        Issue.MISSING_SYMBOL -> "A symbol (e.g. !@#$)"
    }
}
