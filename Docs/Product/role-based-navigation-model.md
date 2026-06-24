# Role-based navigation model

Status date: **2026-06-24**

How Viaverse's navigation, dashboards, available actions, and route access change with the user's **active
mode**. This formalizes the existing capability model (`STANDARD` / `INDIVIDUAL_PROVIDER` / `BUSINESS` in
`lib/mockAppClient.ts`) into deliberate per-mode navigation, and replaces the top-bar persona dropdown with
profile-driven mode switching.

See [ADR-0001](../Architecture/decisions/0001-role-based-navigation-and-mode-model.md) (mode model + dropdown
removal) and [ADR-0005](../Architecture/decisions/0005-onboarding-and-mode-setup-gating.md) (setup gating).
Parent roadmap: [viaverse-ux-overhaul-roadmap.md](./viaverse-ux-overhaul-roadmap.md).

---

## Principles

- **Mode is identity, not a quick toggle.** Each mode changes the user's available workflows, so switching is
  deliberate and server-persisted (`activeCapability`) — never a top-bar dropdown.
- **One source of truth.** Navigation set, default landing route, CTAs, empty states, and route guards all
  derive from `session.currentUser.activeCapability` plus each capability's `enabled` / `setupStatus`. No local
  toggles, no hardcoded role flags in components.
- **Show, don't switch.** The active mode is visible as a small read-only chip on the profile/user pill (plus a
  colored accent). It is not interactive.

---

## Information architecture (route trees)

### Public / guest
```
/                     Landing (redesigned) — CTAs: Sign In · Register · Create Request · Offer Services
(AuthModal)           login · register · forgot-password
/onboarding           post-register survey (gated: only immediately after first register)
```

### Service Seeker (`STANDARD`)
```
/app                  Feed (Ana akış)
/app/explore          Explore — provider & business profiles  (replaces "services")
/app/requests/new     Create Service Request
/app/requests         My Requests
/app/requests/[id]    Request Detail (+ offers)
/app/messages         Messages
/app/profile          Profile (+ Profiller ve Modlar)
/app/settings         Settings
```

### Service Provider (`INDIVIDUAL_PROVIDER`)
```
/app                  Feed
/app/provider         Provider Dashboard
/app/provider/jobs    Matched Jobs  →  /app/provider/jobs/[id]  detail + offer
/app/provider/offers       My Offers
/app/provider/history      Job History
/app/provider/performance  Performance / Ratings
/app/provider/profile      Provider Profile
/app/provider/setup        Provider Setup wizard (gate)
/app/messages, /app/settings
```

### Business Profile (`BUSINESS`)
```
/app                        Feed
/app/business               Business Dashboard
/app/business/requests      Incoming Requests
/app/business/catalog       Services / Products
/app/business/campaigns     Campaigns
/app/business/appointments  Appointments / Reservations
/app/business/reviews       Reviews
/app/business/page          Business Page management
/app/business/team          Team / Staff
/app/business/setup         Business Setup wizard (gate)
/app/messages, /app/settings
```

Cross-cutting (all authed): `/app/activity` (notifications), `/app/payments`, `/app/support` (Phase 7).

---

## Per-mode navigation sets

**Shared across all modes:** Feed, Messages, Profile, Settings.

### Service Seeker
`Ana akış` · `Keşfet` · `Talep Oluştur` · `Taleplerim` · `Mesajlar` · `Profil` · `Ayarlar`

### Service Provider
`Ana akış` · `Panel` · `Bana Uygun İşler` · `Tekliflerim` · `İş Geçmişi` · `Performans` · `Mesajlar` · `Hizmet Veren Profili` · `Ayarlar`

### Business Profile
`Ana akış` · `İşletme Paneli` · `Gelen Talepler` · `Hizmetler / Ürünler` · `Kampanyalar` · `Randevular / Rezervasyonlar` · `Değerlendirmeler` · `İşletme Sayfası` · `Ekip / Personel` · `Mesajlar` · `Ayarlar`

### Default landing route per mode
| Mode | Lands on |
|---|---|
| Seeker | `/app` (Feed) |
| Provider | `/app/provider` (Dashboard) |
| Business | `/app/business` (Dashboard) |

Rationale: the seeker's home is discovery/engagement (feed), while provider/business are operational tools
(dashboard-first).

---

## Mode switching UX

Switching lives only in **Profile → "Profiller ve Modlar"**.

- The section lists the three modes, each with a status: **Aktif** (current), **Hazır** (set up, can activate),
  or **Kurulmadı** (not set up).
- Per card: **"Aktif moda al"** (if set up & not active) or **"Kurulumu başlat"** (if not set up).
- Switching calls `setActiveMode(capabilityKey)` (server-persisted). On success the app updates: nav set,
  default route, dashboards, available actions, empty states, CTAs, and the read-only mode chip.
- The active mode is shown as a small chip on the profile pill (e.g. "Hizmet Veren modu") with a colored accent
  — **read-only**, never a dropdown.

---

## Setup gating rules

See [ADR-0005](../Architecture/decisions/0005-onboarding-and-mode-setup-gating.md).

- **Required setup before activation:**
  - Provider → 4-step wizard: **Type → Categories → Profile → Work Area** (ported from the prototype).
  - Business → wizard: **Business identity → Catalog basics → Hours/Location → Verification**.
- **Unset modes are hidden** from navigation entirely.
- **Direct URL access to a mode that isn't set up** → redirect to that mode's setup wizard (a "set up to
  continue" gate), not a 404.
- **Capability `setupStatus`** (`NOT_STARTED` / `INCOMPLETE` / `COMPLETE`) drives both the Profile cards and the
  route guards. It is server state, surfaced through the session/profile contracts.

---

## How active mode drives the app

| Surface | Driven by |
|---|---|
| Sidebar / top nav items | `activeCapability` → nav-config map |
| Default route after login / switch | `activeCapability` |
| Dashboard & cards loaded | `activeCapability` |
| Primary CTAs | `activeCapability` (e.g. seeker "Talep Oluştur" vs provider "Teklif Ver") |
| Empty states | `activeCapability` (mode-appropriate copy + action) |
| Route access | `activeCapability` + capability `enabled` / `setupStatus` (guards) |

Implementation note: a single nav-config module (Phase 0, batch 0.1) maps each capability to its ordered nav
items + default route; components read it rather than branching ad-hoc. This keeps role logic in one place and
backend-driven (see [frontend/Mock-BFF contract](./frontend-mock-bff-contract.md)).
