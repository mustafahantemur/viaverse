# Viaverse Design System

> Warm, trustworthy and playful-but-professional design system for **Viaverse** — a hyperlocal social, services & small-business marketplace.

Viaverse is a Nextdoor-meets-Fiverr style platform where neighbours can:

- **Share** locally — help requests, lost-pet announcements, small-job posts, advisory questions ("Dinamik çevren" / your dynamic neighbourhood feed)
- **Hire & be hired** — book pros (plumbers, lawyers, pet care, web designers) or pick up gig work
- **Run a business** — list services, set hours, manage subscriptions, get verified
- **Stay aware** — see nearby pharmacies/barbers, get real-time alerts (power outages, traffic, fires)

Three modes share one account: **Personal** (default) → **Work** (lightweight individual provider) → **Business** (separate workspace, verified).

---

## Products in this system

| Surface          | Stack                                     | Folder                    |
|------------------|-------------------------------------------|---------------------------|
| Mobile client    | Kotlin Multiplatform + Compose Multiplatform | `ui_kits/mobile/`        |
| Web (marketing + app) | React + TypeScript + Next.js (SSR/SSG) | `ui_kits/web/`           |
| Admin console    | React + TypeScript + Next.js (RBAC)       | `ui_kits/admin/`         |

The HTML kits in `ui_kits/` are **visual recreations** of these surfaces — production code lives elsewhere; this folder is the source of truth for *look, feel, and component vocabulary*.

---

## Sources used to build this system

- **GitHub prototype:** [`han-878/viaverseUIprototype`](https://github.com/han-878/viaverseUIprototype) — a React+Vite prototype of the mobile client. All visual tokens, component patterns, copy, and icon assets in this design system trace back to this repo. Strongly recommended reading before extending the system.
- **Uploaded category illustrations** (`category_*.png`) — claymation-style 3D renders for the 11 service categories + 4 feed-post types. Used as-is in `assets/categories/`.
- **Brand wordmark + V mark** (`viaverse_wordmark.png`, `viaverse_icon.png`) — official lockups. The 3D V mark uses a glossy orange + dark-forest-green colorway with subtle ceramic texture.

---

## Index — what's where

```
Viaverse Design System/
├── README.md                    ← you are here
├── SKILL.md                     ← Claude/Agent skill metadata
├── colors_and_type.css          ← all design tokens (CSS vars)
├── fonts/                       ← self-hosted Plus Jakarta Sans + Open Sans (variable + static)
├── assets/
│   ├── viaverse_icon.png        ← 3D V mark (orange/green clay) — default brand mark
│   ├── viaverse_icon_silver_green.png  ← silver/green clay V — use on orange surfaces (FAB, CTA tiles)
│   ├── viaverse_wordmark.png    ← horizontal wordmark
│   ├── viaverse_text.png        ← text-only lockup
│   ├── viaverse_full_logo.png   ← full lockup
│   ├── logo_v_orange_green.svg  ← vector V (animated nav use)
│   ├── logo_v_silver_green.svg  ← vector V (smaller-resolution alt)
│   ├── ic_google.png            ← OAuth icon
│   ├── ic_apple.png             ← OAuth icon
│   └── categories/              ← 15 claymation category icons
├── preview/                     ← Design System tab cards (HTML)
└── ui_kits/
    ├── mobile/                  ← KMP/Compose mobile client recreation
    ├── web/                     ← Next.js marketing + web app
    └── admin/                   ← Next.js admin console
```

---

## Visual Foundations

### Color
- **Warm Ivory canvas** (`#FFF7ED` → `#F4ECE0`) is the default page background — *never* pure white. The slight warmth signals approachability and is the single most distinctive visual cue.
- **Deep forest** (`#0F172A` / `#022C22`) carries all body text and is the dark-mode background. The deep green carries the "neighborhood / outdoors / together" feel.
- **Primary CTA Orange** `#F97316` is reserved for the *single most important action* on a screen. Never decorative. Hover/press darkens to `#EA580C` → `#C2410C`.
- **Text on orange** uses `--vv-primary-foreground` (`#FEEFD4`) — a warm cream, **never pure white**. Apply this token to every label, badge digit, button text, or icon that sits on an orange surface. Keeping orange + cream consistent across surfaces is what gives the brand its "warm, not corporate" feel.
- **Trust Green** `#10B981` is for state ("Provider Mode" pill, success toasts, prices, verified badges). It is *the* signal that something is verified, complete, or trustworthy. The deeper `#047857` is used for trust-emphasizing CTAs (e.g. "Accept offer").
- **Category palette** (amber/blue/purple/indigo/cyan/emerald/rose/slate/orange/pink) lives on category chips/cards only, always at low opacity (10–20%) tint, never as full fills.

### Type
- **UI workhorse:** Plus Jakarta Sans (200–800) — friendly geometric sans with humanist warmth. Self-hosted from `fonts/PlusJakartaSans-VariableFont_wght.ttf`. Weights skew **heavier than typical**: 700/800 is normal for headings, 600/700 for buttons, 500 for body. This bold-leaning weight is part of the brand voice.
- **Long-form body / SEO content:** Open Sans (300–800, variable width axis 75–100%) — self-hosted from `fonts/OpenSans-VariableFont_wdth_wght.ttf`. Use for blog/guides/FAQ where reading length is longer than a UI screen. Token: `var(--vv-font-body)`.
- **Wordmark substrate:** the same Open Sans variable, set at `font-weight: 300; font-stretch: 75%` — approximates the thin monoline "viaverse" lockup when the PNG can't be used. Prefer the PNG wordmark wherever practical.
- **Sizing skews tight:** mobile UI runs 10–14px (this is a mobile-first design from a Turkish-language client where horizontal density matters). Numbers like prices and stats run 24–32px bold.
- **Letterspacing:** headings get `-0.02em`; eyebrow labels ("VEYA", mode pills) get `0.2em` UPPERCASE.

### Backgrounds, layout & motifs
- Backgrounds are **flat warm ivory** — no gradients, no patterns. Splash screens get optional **animated orange particles + soft glow blobs** for delight on intro.
- Layout is **mobile-first**, single-column, `max-w-md` (~448px) center-aligned even on web for the app surface. Marketing site is wider but uses the same warm canvas.
- Cards sit on the ivory ground with a **white-ivory surface** (`#FFFBF5`) and *very* subtle shadows. They're separated by `1px` hairlines (`rgba(15,23,42,0.04)`) more often than by spacing.
- Headers, sheets, and modals use **sticky/floating** containers with `backdrop-blur-md` and `bg-surface/90` translucency.
- Bottom sheets and modals use **24px top radius**, drag handle, full-bleed.

### Animation
- **Motion library:** `motion/react` (Framer Motion). Everything has at least subtle motion.
- **Springs over tweens:** sheets enter with `type: "spring", damping: 25, stiffness: 200`. Logo entrances use `type: "spring", bounce: 0.4`.
- **Constant gentle motion:** the central FAB V-logo rotates on its Y-axis at 8s linear loop, perpetually. Splash logo "breathes" by alternating scale [1, 1.2, 1] on glow blobs over 6–8s loops.
- **Tap feedback:** `active:scale-[0.98]` on every button. No ripple, no opacity flash. Compact, springy.
- **Toast pattern:** drops from top (`y: -20 → 30`), persists 3s, fades out.
- **Page transitions:** AnimatePresence with `opacity 0/1` + `y: ±10`, 200ms.

### Hover, press & focus
- **Buttons:** `hover:bg-[#EA580C]` for primary; `hover:bg-surface-hover` for neutral. `active:scale-[0.98]`. Disabled = `opacity-50` + muted surface.
- **Tabs/links:** inactive items sit at `opacity-40`, become `opacity-100` on hover. Active items use the orange under-bar (`border-b-[3px] border-[#F97316]`).
- **Inputs:** `focus:border-[#F97316] focus:ring-1 focus:ring-[#F97316]` — both border and 1px ring, never a glow.
- **Card press:** scale or no animation; cards mostly aren't directly tappable — buttons inside them are.

### Borders, shadows & elevation
- **Hairlines everywhere** — `border-border-subtle` (`rgba(15,23,42,0.06)`) splits feed items and list rows. This is core to the look.
- **Shadow ladder:** xs (1px), sm (rest cards), md (active cards), lg (sheets), cta (orange-tinted under primary buttons), nav (negative-y under bottom nav).
- **Primary buttons get a colored shadow:** `shadow-[#F97316]/20` — the CTA visually "lifts" off the page.
- **No inner shadows.** No neumorphism.

### Corner radii
- `pill (999px)` — search bars, mode pills, category chips, status badges, distance pills
- `xl (20–24px)` — bottom sheets, modals, large cards
- `md (14–16px)` — buttons, inputs, content cards
- `sm (10–12px)` — sub-cards, chips with content
- Avatars are always full circles. Category icons sit unclipped on tinted square backgrounds.

### Transparency & blur
- **Sticky headers** use `bg-surface/90 backdrop-blur-md` — translucent over scrolling content.
- **Modals** dim with `bg-black/40` overlay.
- **Mode pill** "HIZMET VEREN MODU" uses `bg-[#10B981]/10` with `border-[#10B981]/20` — colored translucency over the warm canvas.
- **Search input** in headers uses `bg-surface-muted` (a slightly tinted ivory).

### Imagery
- Photography is **warm, daylit, slice-of-life** — people doing things in their neighborhoods, pets, pros at work. Avoid corporate stock.
- **Illustration style:** the category PNGs are **claymation / 3D-rendered, soft-lit, glossy** with subtle texture — distinctive house style. Treat as untouchable; do not flatten to flat-icon equivalents in feature work.
- The 3D V mark matches that claymation/clay vocabulary — same lighting model.

---

## Content Fundamentals

### Voice
**Playful but professional.** Helpful neighbour, not corporate concierge. The product talks *to* the user using imperative-singular **Turkish** (`sen` / "you" informal): "Hizmet vermeye başla" (start providing service), "Talebini oluştur" (create your request), "Profilini oluştur" (create your profile). Localized clients translate this tone — keep it informal-second-person.

### Casing & punctuation
- Headings: **Sentence case** — "Hizmet vermeye başla", "Hangi hizmete ihtiyacın var?" (not "Hizmet Vermeye Başla").
- Buttons: **Sentence case**, often **lowercase** for chips and tab labels ("keşfet", "hizmet al", "ara", "teklif ver"). The lowercase is intentional — matches the lowercase wordmark.
- Eyebrow / status pills: **UPPERCASE + wide letterspacing** — "VEYA", "HIZMET VEREN MODU", "YARDIM", "DUYURU".
- Categories use **Title Case With & Symbol** — "Ev, Tamirat & Tadilat", "Dijital & Yazılım Hizmetleri", "Yaratıcı İşler & Medya".

### Copy patterns
- **Rotating headlines** in search/empty states — 3-4 prompts cycle every 4s:
  - *"Dinamik çevrende neler oluyor?"*
  - *"Yardım, duyuru, danışma veya küçük iş paylaş."*
  - *"Yakınındaki gündelik ihtiyaçları ve paylaşımları gör."*
- **Question + colored emphasis** for primary CTAs: *"Hizmet verenlere göz at veya **talep oluştur**"* — the action verb is colored orange inline.
- **Soft empty states:** "aradığın kritere uygun sonuç bulunamadı." — lowercase, no emoji, no exclamation. Calm.
- **Trust-forward microcopy under payment / commission:** *"Viaverse üzerinden verilen tekliflerde tutarından sadece %10 komisyon kesilir."* — explicit, no ambiguity. Trust is earned by transparency.
- **Conversational over functional:** "Modun her zaman belli olur" (your mode is always clear) over "Mode indicator visible". Speak to the user's experience.

### Emoji & ornament
- **No emoji in product surfaces.** The category illustrations *replace* the role emoji plays in lesser apps — same warmth, much higher craft.
- **Decorative dots, sparkles, stars, sparkles** — used sparingly: ⭐ verified-style icon on profiles (custom orange-stroked star SVG), pulse-dot inside mode pills.
- Numbers and badges (the "1" on İşlerim, "2" on Mesajlar) are **filled orange circles** with white digits — small, present, never noisy.

### Voice examples
| Don't                                     | Do                                               |
|-------------------------------------------|--------------------------------------------------|
| "Welcome to Viaverse! 🎉"                 | "Hizmet vermeye başla"                           |
| "Submit your offer"                       | "Teklifi gönder"                                 |
| "Successfully submitted!"                 | "Teklifin gönderildi. İşlerim'den takip edebilirsin." |
| "View all categories"                     | "Hizmet verenlere göz at veya talep oluştur"    |
| "Connect with experts now! ⚡"            | "İhtiyacını anlat, hizmet verenlerden teklif al." |

---

## Iconography

Viaverse uses **three distinct icon systems**, each with a specific role. Do not mix them.

### 1. Claymation category illustrations (`assets/categories/*.png`)
**The signature visual element.** 15 illustrations:

- **11 service categories:** home_repair, digital_software, creative_media, education, cleaning, logistics, care_health, professional_consulting, pets, events, local_help
- **4 feed-post types:** announcement (megaphone), advisory, work, megaphone

Style: 3D rendered, claymation-soft, glossy with subtle ceramic texture, warm rim lighting, transparent backgrounds. Always shown at native size — **never recolored, never flattened to SVG, never replaced with monoline icons.**

Use:
- Large in category lists and category chips (28–48px)
- Inline next to post-type labels in feeds (14–18px)
- As avatars/badges on cards

### 2. Lucide React monoline icons
Used for **UI affordances** — navigation, controls, form elements. From `lucide-react` (already in `package.json`):

- Stroke weight: `1.5` for inactive/passive states, `2` for active/emphasized
- Color: `text-content` (`#0F172A`) or `text-content-muted` (`#6B7280`); orange `#F97316` only when active/selected
- Common: `Compass`, `Briefcase`, `MessageCircle`, `User`, `ChevronLeft`, `ChevronRight`, `X`, `Search`, `Plus`, `Bell`, `MapPin`, `Star`, `Heart`, `Sun`, `Moon`, `Shield`, `ShieldCheck`, `Sparkles`, `Truck`, `PawPrint`, `Wrench`, `Code`, `Palette`, `GraduationCap`

In web HTML kits, mirror these via [Lucide CDN](https://unpkg.com/lucide@latest) — same vocabulary, same weights.

### 3. Brand logo marks (`assets/logo_*.svg`, `assets/viaverse_*.png`)
- `viaverse_icon.png` — full claymation V (use as primary brand mark anywhere you'd put a logo)
- `logo_v_orange_green.svg` — vector V, animated rotating on Y-axis inside CTA elements (auth screen, the central FAB)
- `logo_v_silver_green.svg` — vector V variant used *on top of the orange FAB* in the bottom nav (silver+green for contrast against orange)
- `viaverse_wordmark.png` / `viaverse_text.png` — horizontal text lockups

### Unicode & emoji
- **Emoji are not used** in product surfaces (see Content Fundamentals).
- **Star ⭐ glyph** is *not* used directly — instead a custom orange-stroked SVG star renders verified-style flair.
- **Pulse dots** (small filled circles) signal live state inside status pills.

---

## How to use this system

1. **Always start with `colors_and_type.css`.** Link it in every HTML file. Use `var(--vv-*)` tokens — never raw hex in feature code (the prototype's `bg-[#F97316]` is a *prototype convenience*; production should use semantic tokens).
2. **Reach for components, not primitives.** The mobile spec calls out `ViaverseButton`, `ViaverseCard`, `ViaverseChip` etc. — wrap Compose / React primitives, don't scatter them.
3. **Copy icons; never redraw them.** All category illustrations are in `assets/categories/`. The 3D V mark is in `assets/`. Lucide handles the rest.
4. **Pixel ratios from the prototype:** mobile screen container is `max-w-md` (~448px), bottom nav is `80px` tall with a `-mt-6` overlapping FAB, headers are `~76px` (top-bar + tabs row).
5. **For new screens:** mirror an existing one in `ui_kits/mobile/` first. Headers, search-pill pattern, and bottom-nav placement are non-negotiable.

### Real 3D assets (.glb / .gltf)

The web kit ships with [`@google/model-viewer`](https://modelviewer.dev/) loaded — drop a `.glb` or `.gltf` file into `assets/3d/` and replace any `<img src="...viaverse_icon.png">` with:

```html
<model-viewer src="../assets/3d/viaverse_v.glb"
              alt="Viaverse V mark"
              auto-rotate camera-controls
              shadow-intensity="1" exposure="1"
              style="width: 100%; height: 100%;"></model-viewer>
```

Use this anywhere the static PNG of the V mark currently lives (hero, provider CTA, FAB, splash). Upload a .glb whenever you have one — no other plumbing needed.

---

## Caveats & substitutions

- **Brand fonts are self-hosted.** Plus Jakarta Sans and Open Sans (both variable + static weight sets) ship in `fonts/`. Plus Jakarta Sans is the official workhorse; Open Sans is the official long-form body family and the substrate for wordmark reconstructions (its variable width axis gives us a condensed monoline form that approximates the "viaverse" lockup without shipping Montserrat).
- The system is built from a **single in-progress prototype** (mobile client). The web and admin kits in this repo are *extrapolated* from the same vocabulary — they will need real codebase grounding before production.

---

## Next steps for whoever opens this

- Connect the live Compose Multiplatform codebase so the mobile kit can ground in real components.
- Provide a Figma file if one exists — current kits are reverse-engineered from one prototype repo and screenshots.
- Define a payment-card visual treatment (`ViaversePaymentCard`) — not yet recreated.
