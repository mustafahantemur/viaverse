---
name: viaverse-design
description: Use this skill to generate well-branded interfaces and assets for Viaverse — a hyperlocal social, services and small-business marketplace ("Nextdoor × Fiverr" with warm-ivory + orange + deep-green colorway, claymation category illustrations, Plus Jakarta Sans + Open Sans typography). Works for production code (Compose Multiplatform mobile, Next.js web + admin) and for throwaway prototypes/mocks/decks/etc.
user-invocable: true
---

Read `README.md` first — it's the single source of truth for voice, colors, type, motion, iconography and screen vocabulary. Then explore the other files as needed.

## Folder map

- `README.md` — full design system writeup (Visual Foundations, Content Fundamentals, Iconography).
- `colors_and_type.css` — all design tokens as CSS custom properties (`--vv-bg`, `--vv-primary`, `--vv-fg`, type scale, radii, shadows). Self-hosted fonts live in `fonts/` and are wired up via `@font-face`. **Link this file in every HTML artifact.**
- `fonts/` — Plus Jakarta Sans (variable + static) + Open Sans (variable + static, includes Condensed and SemiCondensed widths).
- `assets/` — official brand marks (`viaverse_icon.png` clay V, `viaverse_icon_silver_green.png` silver-clay V for on-orange use, wordmarks, full lockup) + 15 claymation category illustrations under `assets/categories/`.
- `preview/` — small HTML cards documenting each token / component (Type, Colors, Spacing, Components, Brand). Open one as a worked example.
- `ui_kits/mobile/` — React recreation of the KMP/Compose mobile client (auth → home → jobs → chat → profile → provider welcome).
- `ui_kits/web/` — React recreation of the marketing site + SSR web app.
- `ui_kits/admin/` — React recreation of the internal admin console (verification, moderation, disputes, analytics).

## How to use this skill

If creating visual artifacts (slides, mocks, throwaway prototypes, etc):
1. Copy out assets you need (`assets/viaverse_*.png`, `assets/categories/*.png`, fonts).
2. Reference `colors_and_type.css` and use semantic tokens (`var(--vv-bg)`, `var(--vv-primary)`, etc) — never raw hex.
3. Mirror an existing UI kit screen as a starting point instead of inventing layouts.
4. Output static HTML for the user to view.

If working on production code:
1. Read `README.md` end-to-end so you absorb the voice + visual rules.
2. Pull tokens from `colors_and_type.css` into the target system (Compose theme, Tailwind config, etc).
3. Follow the **Components rules** from the parent README — wrap primitives into `Viaverse*` components, never scatter raw Material/HTML elements.

## When invoked without specific guidance

Ask the user:
- What surface? (mobile / web / admin / slides / one-off mock)
- What screen or component? (or "design exploration")
- Personal / Work / Business mode (mobile surfaces only)?
- Light or dark?
- Variations or single solution?

Then act as an expert Viaverse designer. Outputs are HTML artifacts unless they explicitly ask for production code.

## Voice cheatsheet

- Turkish-first product; informal `sen` ("you"). Sentence case in headings, **lowercase** for chips/tabs, **UPPERCASE + wide tracking** for eyebrows ("VEYA", "HIZMET VEREN MODU").
- Playful but professional — calm, helpful neighbour energy, no exclamation marks, no emoji, no "let's get started!" boilerplate.
- Trust microcopy is explicit ("Viaverse üzerinden verilen tekliflerde tutarından sadece %10 komisyon kesilir.") — never euphemism.

## Common pitfalls to avoid

- Don't flatten the claymation category PNGs to monoline SVG icons.
- Don't use pure white — always warm ivory (`var(--vv-bg)` or `var(--vv-surface)`).
- Don't put gradients on backgrounds. The only gradients are inside the brand V mark itself, and a soft glow blob behind the splash/hero.
- Don't add hover ripples or opacity flashes on buttons — use `transform: scale(0.98)` on press.
- Don't multiply CTAs — exactly one orange button per surface, everything else is neutral/ghost.
- Don't ship the wordmark in fallback text when the PNG is available — the lockup's 3D V mark is irreplaceable.

## Companion repo

The original mobile prototype: <https://github.com/han-878/viaverseUIprototype> — explore it for ground-truth component code (React + Tailwind 4 + Motion) when extending the system.
