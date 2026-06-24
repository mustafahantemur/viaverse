[CmdletBinding()]
param(
    [switch] $SkipDockerInfra
)

$ErrorActionPreference = "Stop"

function Fail($message) {
    Write-Error $message
    exit 1
}

function Ensure-Command($name, $hint) {
    if (-not (Get-Command $name -ErrorAction SilentlyContinue)) {
        Fail "$name was not found. $hint"
    }
}

function Write-DefaultKubernetesEnv($path) {
    $content = @'
IDENTITY_SERVICE_BASE_URL=http://identity-service:8101
PROFILE_SERVICE_BASE_URL=http://profile-service:8111
CONTENT_SERVICE_BASE_URL=http://content-service:8112
MARKETPLACE_SERVICE_BASE_URL=http://marketplace-service:8102
MEDIA_SERVICE_BASE_URL=http://media-service:8105
NEXT_PUBLIC_BFF_BASE_URL=http://web-bff:8001
NEXT_PUBLIC_ADMIN_BFF_BASE_URL=http://admin-bff:8110
NEXT_PUBLIC_MOCK_APP_BFF_BASE_URL=http://mock-web-bff:8120
VIAVERSE_INTERNAL_API_TOKEN=local-dev-internal-token-change-me
IDENTITY_JWT_SECRET=local-dev-identity-jwt-secret-change-me
IDENTITY_JWT_PREVIOUS_SECRETS=
IDENTITY_SERVICE_DB_URL=jdbc:postgresql://host.docker.internal:5432/viaverse_identity
IDENTITY_SERVICE_DB_USERNAME=viaverse
IDENTITY_SERVICE_DB_PASSWORD=viaverse
PROFILE_SERVICE_DB_URL=jdbc:postgresql://host.docker.internal:5432/viaverse_profile
PROFILE_SERVICE_DB_USERNAME=viaverse
PROFILE_SERVICE_DB_PASSWORD=viaverse
CONTENT_SERVICE_DB_URL=jdbc:postgresql://host.docker.internal:5432/viaverse_content
CONTENT_SERVICE_DB_USERNAME=viaverse
CONTENT_SERVICE_DB_PASSWORD=viaverse
MARKETPLACE_SERVICE_DB_URL=jdbc:postgresql://host.docker.internal:5432/viaverse_marketplace
MARKETPLACE_SERVICE_DB_USERNAME=viaverse
MARKETPLACE_SERVICE_DB_PASSWORD=viaverse
PAYMENT_SERVICE_DB_URL=jdbc:postgresql://host.docker.internal:5432/viaverse_payment
PAYMENT_SERVICE_DB_USERNAME=viaverse
PAYMENT_SERVICE_DB_PASSWORD=viaverse
MESSAGING_SERVICE_DB_URL=jdbc:postgresql://host.docker.internal:5432/viaverse_messaging
MESSAGING_SERVICE_DB_USERNAME=viaverse
MESSAGING_SERVICE_DB_PASSWORD=viaverse
MEDIA_SERVICE_DB_URL=jdbc:postgresql://host.docker.internal:5432/viaverse_media
MEDIA_SERVICE_DB_USERNAME=viaverse
MEDIA_SERVICE_DB_PASSWORD=viaverse
NOTIFICATION_SERVICE_DB_URL=jdbc:postgresql://host.docker.internal:5432/viaverse_notification
NOTIFICATION_SERVICE_DB_USERNAME=viaverse
NOTIFICATION_SERVICE_DB_PASSWORD=viaverse
SEARCH_SERVICE_DB_URL=jdbc:postgresql://host.docker.internal:5432/viaverse_search
SEARCH_SERVICE_DB_USERNAME=viaverse
SEARCH_SERVICE_DB_PASSWORD=viaverse
TRUST_GAMIFICATION_SERVICE_DB_URL=jdbc:postgresql://host.docker.internal:5432/viaverse_trust_gamification
TRUST_GAMIFICATION_SERVICE_DB_USERNAME=viaverse
TRUST_GAMIFICATION_SERVICE_DB_PASSWORD=viaverse
ADS_MONETIZATION_SERVICE_DB_URL=jdbc:postgresql://host.docker.internal:5432/viaverse_ads_monetization
ADS_MONETIZATION_SERVICE_DB_USERNAME=viaverse
ADS_MONETIZATION_SERVICE_DB_PASSWORD=viaverse
MOCK_WEB_BFF_DB_URL=jdbc:h2:file:/tmp/mock-web-bff/mock-app-db;AUTO_SERVER=TRUE;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE
MOCK_WEB_BFF_DB_USERNAME=sa
MOCK_WEB_BFF_DB_PASSWORD=
IDENTITY_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092
PROFILE_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092
CONTENT_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092
MARKETPLACE_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092
MEDIA_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092
TRUST_GAMIFICATION_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092
IDENTITY_VALKEY_HOST=host.docker.internal
IDENTITY_VALKEY_PORT=6379
IDENTITY_VALKEY_DB=0
IDENTITY_VALKEY_COMMAND_TIMEOUT=1s
IDENTITY_VALKEY_CONNECT_TIMEOUT=2s
OBJECT_STORAGE_PROVIDER=seaweedfs
OBJECT_STORAGE_ENDPOINT=http://host.docker.internal:8333
OBJECT_STORAGE_REGION=local
OBJECT_STORAGE_ACCESS_KEY=viaverse
OBJECT_STORAGE_SECRET_KEY=viaverse-local-secret
OBJECT_STORAGE_PATH_STYLE_ACCESS=true
OBJECT_STORAGE_BUCKET_MEDIA=viaverse-media-local
SMTP_HOST=host.docker.internal
SMTP_PORT=1025
SMTP_USERNAME=
SMTP_PASSWORD=
SMTP_FROM_ADDRESS=noreply@viaverse.local
SMTP_AUTH_ENABLED=false
SMTP_STARTTLS_ENABLED=false
NETGSM_ENDPOINT=https://api.netgsm.com.tr/sms/send/get
NETGSM_USERNAME=
NETGSM_PASSWORD=
NETGSM_HEADER=
GOOGLE_OAUTH_CLIENT_ID=
APPLE_CLIENT_ID=
OTEL_EXPORTER_OTLP_ENDPOINT=http://host.docker.internal:4318/v1/traces
OTEL_EXPORTER_OTLP_ENDPOINT_METRICS=http://host.docker.internal:4318/v1/metrics
OTEL_EXPORTER_OTLP_LOGS_ENDPOINT=http://host.docker.internal:4318/v1/logs
'@

    Set-Content -LiteralPath $path -Value $content -Encoding ASCII
}

Ensure-Command "kubectl" "Install kubectl and enable Kubernetes in Docker Desktop."

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Resolve-Path (Join-Path $scriptRoot "..\..")
$envFile = Join-Path $repoRoot ".env.k8s.local"

if (-not $SkipDockerInfra) {
    & "$scriptRoot\start-core-infra.ps1"
}

if (-not (Test-Path $envFile)) {
    Write-Host "Creating local Kubernetes env file: $envFile"
    Write-DefaultKubernetesEnv $envFile
}

$vaultToken = $env:VIAVERSE_VAULT_TOKEN
if ([string]::IsNullOrWhiteSpace($vaultToken)) {
    $vaultToken = "local-dev-vault-token-change-me"
}

Push-Location $repoRoot
try {
    kubectl apply -f infra/kubernetes/base/namespace.yml
    if ($LASTEXITCODE -ne 0) {
        Fail "Could not create Kubernetes namespace."
    }

    kubectl create secret generic viaverse-vault-token -n viaverse --from-literal=token="$vaultToken" --dry-run=client -o yaml | kubectl apply -f -
    if ($LASTEXITCODE -ne 0) {
        Fail "Could not create viaverse-vault-token secret."
    }

    kubectl create secret generic viaverse-bootstrap-secrets -n viaverse --from-env-file="$envFile" --dry-run=client -o yaml | kubectl apply -f -
    if ($LASTEXITCODE -ne 0) {
        Fail "Could not create viaverse-bootstrap-secrets secret."
    }

    kubectl apply -k infra/kubernetes/base
    if ($LASTEXITCODE -ne 0) {
        Fail "Could not apply Kubernetes base manifests."
    }

    Write-Host "Local Kubernetes resources are applied."
    Write-Host "Vault will be seeded by the vault-seed-runtime-secrets Job."
    Write-Host "Use kubectl get pods -n viaverse to watch startup."
}
finally {
    Pop-Location
}
