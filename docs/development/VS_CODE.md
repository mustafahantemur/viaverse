# VS Code

Open the repository root:

```text
C:\Projects\Viaverse\Viaverse
```

Install the recommended extensions when VS Code prompts for them.

## One-Click Launches

Use `Run and Debug`:

- `docker initial setup`: starts Docker Desktop if possible, starts core local containers, waits for PostgreSQL, and creates missing service databases.
- `debug <service>`: starts required backend dependencies, then launches one Spring Boot service with the VS Code Java debugger.
- `debug backend: all services`: launches all Spring Boot service skeletons.
- `run web-next`: installs npm dependencies if needed, then runs the public Next.js app on port `3000`.
- `run admin-next`: installs npm dependencies if needed, then runs the admin Next.js app on port `3001`.
- `run web apps`: starts both Next.js apps.
- `run mobile-kmp desktop`: runs the current Compose Multiplatform desktop target.
- `run mobile-kmp android`: checks Android SDK/emulator readiness and installs the APK when the Android application target exists.
- `run local stack: backend + web apps`: starts backend services and both web apps.

## Tasks

Use `Terminal > Run Task...`:

- `gradle: clean`
- `gradle: projects`
- `gradle: check all`
- `docker: initial setup`
- `backend: ready`
- `infra: stop`
- `mobile-kmp: check`
- `mobile-kmp: desktop`
- `mobile-kmp: android`
- `web-next: dev`
- `admin-next: dev`

## Attach Configurations

Attach configurations are intentionally not kept in the default launch list. Attach is useful when a JVM is already running with a debug port open and VS Code only connects to it. For this repository, launch configurations are simpler because they start the service and attach the debugger in one step.

## Troubleshooting

If Java classes look unresolved after pulling the repository:

1. Run `Java: Clean Java Language Server Workspace`.
2. Reload VS Code.
3. Run `gradle: check all`.

If Docker-related startup fails, open Docker Desktop once and run `docker initial setup`.

The current mobile skeleton has a desktop Compose target. Android emulator automation is prepared, but APK install requires the Android application Gradle target to be enabled in a later mobile Gradle setup task.

Do not commit local secrets, `.env` files, `local.properties`, Gradle caches, or build outputs.
