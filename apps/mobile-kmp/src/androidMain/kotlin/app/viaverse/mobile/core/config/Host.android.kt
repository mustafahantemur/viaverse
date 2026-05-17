package app.viaverse.mobile.core.config

/**
 * Android emulator routes `10.0.2.2` to the host machine's loopback.
 * A real physical device would need the host's LAN IP (or a tunnel)
 * — that's a problem for whoever wires the staging build, not us.
 */
actual fun localhostForHost(): String = "10.0.2.2"
