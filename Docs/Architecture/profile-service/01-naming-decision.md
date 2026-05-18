# Naming decision: `profile-service`

## Choice

**`profile-service`** — not `account-service`, not `user-service`.

## Why not `account-service`

`identity-service` already owns the `identity_account` table and the `Account` aggregate. It is the system of record for "what is an account" in the credential sense — status (active/suspended), roles, password, 2FA, identifiers. Adding `account-service` as a second service would collide on the noun and split a single aggregate across two services.

## Why not `user-service`

"User" is a UI word, not a domain word. Every service has a `user-facing-something`. Naming a microservice `user-` makes ownership opaque ("which user-thing? auth? profile? preferences?").

## Why `profile-service` is correct

The next slice is about the **public + self-managed facet of an account**: who I am to others, what I look like, what capabilities I have enabled, what I prefer, who I have blocked. None of that is credential state.

| Bucket | Belongs in | Reason |
|---|---|---|
| `password_hash`, `two_factor_secret`, identifiers, sessions, OTP flows | `identity-service` | Credential / auth-flow primitives. Already there. |
| `display_name`, `first_name`, `last_name`, `avatar_url`, capability flags, preferences, blocks, public visibility | `profile-service` | User-presented identity, not authentication. |
| Trust scaffolding (completeness %, verified-badges) | `profile-service` (read-through to other services later) | The data is owned here; specialized signals (reviews, dispute history) come from other services and we aggregate. |

## Aggregate boundary, plain English

- **Identity** answers "is this person who they claim to be, and can they log in?"
- **Profile** answers "who is this person, what can they do in the app today, and how do they appear to others?"

That separation also matches the *capability* concept Viaverse is built around: enabling **individual provider** mode is a profile change, not an auth change.

## Migration note (out of scope for Phase 1)

`identity_account` today carries `display_name / first_name / last_name / profile_completed`. These are profile fields living on the identity row for historical reasons. Phase 1 of profile-service **does not migrate them yet** — it owns its own new tables and references `account_id`. A later phase removes the duplicated columns from identity-service. The compatibility story is covered in [06-integration.md](06-integration.md).
