package app.viaverse.mobile.core.session

sealed interface SessionState {
    data object Anonymous : SessionState
}

