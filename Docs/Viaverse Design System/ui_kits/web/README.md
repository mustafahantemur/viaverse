# Viaverse Web UI Kit

Visual recreation of the **Viaverse marketing site + SSR web client** — Next.js/React in production, recreated here in HTML for design exploration.

## What's here

`index.html` is a single-page marketing site that demonstrates the **web** voice: warm ivory canvas scaled up from mobile, two-column hero with the claymation V, category grid, social-proof feed, footer.

Then click "Web uygulamasına geç" to see the **web app shell** — same vocabulary as mobile, but laid out in a desktop browser frame.

## Components

| File              | Renders                                                  |
|-------------------|----------------------------------------------------------|
| `WebNav.jsx`      | Top nav: wordmark, primary links, "Giriş Yap" CTA pill   |
| `WebHero.jsx`     | 2-col hero — claymation V + headline + dual CTAs         |
| `WebCategoryGrid.jsx` | 5-col category grid using claymation illustrations   |
| `WebFeedTeaser.jsx` | Live-feed-style strip of recent neighbour posts        |
| `WebHowItWorks.jsx` | 3-step "how it works" with numbered cards              |
| `WebProviderCTA.jsx` | "Become a provider" dark green section                 |
| `WebFooter.jsx`   | Footer with sitemap, legal, language picker              |
| `WebApp.jsx`      | Web app shell — sidebar + content (Keşfet/Hizmet al/Mesajlar) |

## Production notes

- SSR via Next.js — the marketing pages render fully at build/request time for SEO.
- Web app routes mirror mobile structure: `/keşfet`, `/hizmet-al`, `/islerim`, `/mesajlar`, `/profil`.
- Public profiles (`/u/[username]`) and service guides (`/rehber/[slug]`) are SSG.
