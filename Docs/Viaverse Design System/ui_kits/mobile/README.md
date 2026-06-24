# Viaverse Mobile UI Kit

Visual recreation of the **Viaverse mobile client** — built originally as a KMP Compose Multiplatform app, recreated here in React/HTML for design exploration.

## What's here

`index.html` boots a clickable prototype covering the **Personal Mode** core flow:

1. **Splash** (tap to dismiss)
2. **Auth / Login** → **OTP**
3. **Home — Keşfet feed** (Yardım / Duyuru / İş posts)
4. **Home — Hizmet al** (service category browser)
5. **Jobs (İşlerim)** — incoming offers
6. **Chat list + chat thread**
7. **Profile** (personal + public profile sheet)
8. **Provider Welcome** (Work Mode entry point)

## Components

| File                  | What it renders                                                    |
|-----------------------|--------------------------------------------------------------------|
| `VVPrimitives.jsx`    | Tokens, button, input, chip, avatar, verified-star, mode-pill     |
| `VVHeader.jsx`        | Sticky top bar — back, rotating-search pill (with "ara" button), dark-mode toggle, mode-tab row (Keşfet / Hizmet al) |
| `VVBottomNav.jsx`     | 5-tab bottom nav with center FAB (rotating V mark)                |
| `VVFeedCard.jsx`      | Post card with author, type badge, body, like/comment, "teklif ver" |
| `VVCategoryRow.jsx`   | Expandable category in Hizmet al list                              |
| `VVOfferCard.jsx`     | Provider quote with rating, price, accept button                   |
| `VVChat.jsx`          | Chat list + thread bubbles                                         |
| `VVAuth.jsx`          | Login + OTP screens                                                |
| `VVProviderWelcome.jsx` | Work-mode onboarding screen with mode pill                      |
| `VVScreens.jsx`       | Wires the screens together                                         |

## Notes

- This is a **design recreation** in React, not the production Compose code. Production should mirror these visuals via `ViaverseButton`, `ViaverseCard`, etc. — see the parent `README.md` Components rules.
- All category illustrations are loaded from `../../assets/categories/`.
- Logos load from `../../assets/`.
- Real production should use semantic tokens (`var(--vv-*)`) — this kit uses them via `../../colors_and_type.css`.
