package app.viaverse.mobile.core.config

/**
 * Hostname used to reach the host machine from the current platform.
 * - Desktop: `localhost` (same machine)
 * - Android emulator: `10.0.2.2` (special host-loopback alias)
 *
 * Implementations live in the per-platform source sets so the call
 * site stays clean.
 */
expect fun localhostForHost(): String
