/**
 * Auth bounded-context domain models — pure Java, no framework/JPA annotations.
 * State transitions for login flow, OTP challenge, refresh token, and identifier verification
 * are encapsulated here; persistence adapters use mappers to translate to/from JPA entities.
 */
package app.viaverse.identity.auth.domain.model;
