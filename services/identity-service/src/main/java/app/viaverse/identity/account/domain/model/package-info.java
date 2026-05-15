/**
 * Account bounded-context domain models — pure Java, no framework/JPA annotations.
 * Account lifecycle state (registration, profile completion, rename, suspend/reactivate)
 * is encapsulated here; persistence adapters use mappers to translate to/from JPA entities.
 */
package app.viaverse.identity.account.domain.model;
