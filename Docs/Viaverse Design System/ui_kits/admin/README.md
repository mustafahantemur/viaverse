# Viaverse Admin Console UI Kit

Recreation of the **internal admin console** — Next.js + RBAC in production. Surfaces ops needs to keep the trust layer of Viaverse healthy:

- **Verification** queue (worker / business ID checks)
- **Moderation** (content, chat, reports)
- **Disputes** (payment / job)
- **Payments** ledger + payouts
- **Categories** taxonomy
- **SEO** metadata & sitemap
- **Subscriptions** (business plans)
- **Support** tickets
- **Analytics** dashboards
- **Audit logs**

## What's here

`index.html` boots a single dashboard view with **sidebar nav**, **top utility bar**, and a default landing on **Verification queue**. Side-nav items switch between Verification, Moderation, Disputes, Analytics — each just a tab swap on the main pane.

The look is deliberately *toned down from consumer*: ivory canvas stays, but density goes up, illustrations stay out, hairlines do the work. Orange is reserved for the **single primary action per screen**. Trust green carries verified / approved states. Red = needs attention.

## Components

| File                | Renders                                                  |
|---------------------|----------------------------------------------------------|
| `AdminShell.jsx`    | Sidebar + topbar layout                                  |
| `AdminTable.jsx`    | Sticky-header data table with sort + bulk selection      |
| `VerificationView.jsx` | Worker/business ID verification queue                 |
| `ModerationView.jsx`| Reported posts/chats triage list                         |
| `DisputeView.jsx`   | Payment / job dispute detail                             |
| `AnalyticsView.jsx` | KPI cards + line chart placeholder                       |
