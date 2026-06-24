package app.viaverse.identity.config;

import app.viaverse.identity.auth.domain.enums.EmailProviderEnum;
import app.viaverse.identity.auth.domain.enums.SmsProviderEnum;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "viaverse.auth")
public class AuthProperties {
    private final Jwt jwt = new Jwt();
    private Duration refreshTokenTtl = Duration.ofDays(30);
    private final Otp otp = new Otp();
    private final RegistrationToken registrationToken = new RegistrationToken();
    private final AdminInvitation adminInvitation = new AdminInvitation();
    private final Sms sms = new Sms();
    private final Email email = new Email();
    private final Social social = new Social();
    private final Debug debug = new Debug();
    private final RateLimit rateLimit = new RateLimit();
    private final Consent consent = new Consent();
    private final Internal internal = new Internal();

    public Email getEmail() {
        return email;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public Duration getRefreshTokenTtl() {
        return refreshTokenTtl;
    }

    public void setRefreshTokenTtl(Duration refreshTokenTtl) {
        this.refreshTokenTtl = refreshTokenTtl;
    }

    public Otp getOtp() {
        return otp;
    }

    public RegistrationToken getRegistrationToken() {
        return registrationToken;
    }

    public AdminInvitation getAdminInvitation() {
        return adminInvitation;
    }

    public Sms getSms() {
        return sms;
    }

    public Social getSocial() {
        return social;
    }

    public Debug getDebug() {
        return debug;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public Consent getConsent() {
        return consent;
    }

    public Internal getInternal() {
        return internal;
    }

    /**
     * Required-consent document registry. The server owns versions — clients only
     * acknowledge by {@code type}; the current {@code version} is resolved here
     * and persisted on the {@code consent_record} row. To roll a document
     * (e.g. new ToS), bump the version here so any prior acceptance is treated
     * as stale and the user is re-prompted on next sensitive action.
     */
    public static class Consent {
        private String termsOfServiceVersion = "v1";
        private String personalDataProtectionLawVersion = "v1";
        private String marketingVersion = "v1";
        private String providerTermsVersion = "v1";
        private String businessTermsVersion = "v1";
        private String termsOfServiceUrl = "https://viaverse.app/legal/terms";
        private String personalDataProtectionLawUrl = "https://viaverse.app/legal/kvkk";
        private String marketingUrl = "https://viaverse.app/legal/marketing";
        private String providerTermsUrl = "https://viaverse.app/legal/provider-terms";
        private String businessTermsUrl = "https://viaverse.app/legal/business-terms";

        public String getTermsOfServiceVersion() {
            return termsOfServiceVersion;
        }

        public void setTermsOfServiceVersion(String termsOfServiceVersion) {
            this.termsOfServiceVersion = termsOfServiceVersion;
        }

        public String getPersonalDataProtectionLawVersion() {
            return personalDataProtectionLawVersion;
        }

        public void setPersonalDataProtectionLawVersion(String personalDataProtectionLawVersion) {
            this.personalDataProtectionLawVersion = personalDataProtectionLawVersion;
        }

        public String getMarketingVersion() {
            return marketingVersion;
        }

        public void setMarketingVersion(String marketingVersion) {
            this.marketingVersion = marketingVersion;
        }

        public String getProviderTermsVersion() {
            return providerTermsVersion;
        }

        public void setProviderTermsVersion(String providerTermsVersion) {
            this.providerTermsVersion = providerTermsVersion;
        }

        public String getBusinessTermsVersion() {
            return businessTermsVersion;
        }

        public void setBusinessTermsVersion(String businessTermsVersion) {
            this.businessTermsVersion = businessTermsVersion;
        }

        public String getTermsOfServiceUrl() {
            return termsOfServiceUrl;
        }

        public void setTermsOfServiceUrl(String termsOfServiceUrl) {
            this.termsOfServiceUrl = termsOfServiceUrl;
        }

        public String getPersonalDataProtectionLawUrl() {
            return personalDataProtectionLawUrl;
        }

        public void setPersonalDataProtectionLawUrl(String personalDataProtectionLawUrl) {
            this.personalDataProtectionLawUrl = personalDataProtectionLawUrl;
        }

        public String getMarketingUrl() {
            return marketingUrl;
        }

        public void setMarketingUrl(String marketingUrl) {
            this.marketingUrl = marketingUrl;
        }

        public String getProviderTermsUrl() {
            return providerTermsUrl;
        }

        public void setProviderTermsUrl(String providerTermsUrl) {
            this.providerTermsUrl = providerTermsUrl;
        }

        public String getBusinessTermsUrl() {
            return businessTermsUrl;
        }

        public void setBusinessTermsUrl(String businessTermsUrl) {
            this.businessTermsUrl = businessTermsUrl;
        }
    }

    public static class Internal {
        private String apiToken = "local-dev-internal-token-change-me";

        public String getApiToken() {
            return apiToken;
        }

        public void setApiToken(String apiToken) {
            this.apiToken = apiToken;
        }
    }

    public static class Jwt {
        private String secret = "";
        private List<String> previousSecrets = new ArrayList<>();
        private Duration accessTokenTtl = Duration.ofMinutes(15);

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public List<String> getPreviousSecrets() {
            return previousSecrets;
        }

        public void setPreviousSecrets(List<String> previousSecrets) {
            this.previousSecrets = previousSecrets;
        }

        public Duration getAccessTokenTtl() {
            return accessTokenTtl;
        }

        public void setAccessTokenTtl(Duration accessTokenTtl) {
            this.accessTokenTtl = accessTokenTtl;
        }
    }

    public static class Otp {
        private Duration ttl = Duration.ofMinutes(5);
        private int maxAttempts = 5;

        public Duration getTtl() {
            return ttl;
        }

        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }
    }

    public static class RegistrationToken {
        private Duration ttl = Duration.ofMinutes(30);

        public Duration getTtl() {
            return ttl;
        }

        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }
    }

    public static class AdminInvitation {
        private Duration ttl = Duration.ofDays(7);

        public Duration getTtl() {
            return ttl;
        }

        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }
    }

    public static class Email {
        private EmailProviderEnum provider = EmailProviderEnum.NONE;
        private final Smtp smtp = new Smtp();

        public EmailProviderEnum getProvider() {
            return provider;
        }

        public void setProvider(EmailProviderEnum provider) {
            this.provider = provider;
        }

        public Smtp getSmtp() {
            return smtp;
        }
    }

    public static class Smtp {
        private String host = "localhost";
        private int port = 1025;
        private String username = "";
        private String password = "";
        private String fromAddress = "noreply@viaverse.app";
        private String fromName = "Viaverse";
        private String subjectTemplate = "Viaverse verification code: %s";
        private String bodyTemplate = "Your Viaverse verification code is %s. It expires in 5 minutes.";
        private boolean startTlsEnabled = false;
        private boolean startTlsRequired = false;
        private boolean authEnabled = false;
        private Duration connectionTimeout = Duration.ofSeconds(5);
        private Duration writeTimeout = Duration.ofSeconds(5);

        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFromAddress() { return fromAddress; }
        public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }
        public String getFromName() { return fromName; }
        public void setFromName(String fromName) { this.fromName = fromName; }
        public String getSubjectTemplate() { return subjectTemplate; }
        public void setSubjectTemplate(String subjectTemplate) { this.subjectTemplate = subjectTemplate; }
        public String getBodyTemplate() { return bodyTemplate; }
        public void setBodyTemplate(String bodyTemplate) { this.bodyTemplate = bodyTemplate; }
        public boolean isStartTlsEnabled() { return startTlsEnabled; }
        public void setStartTlsEnabled(boolean startTlsEnabled) { this.startTlsEnabled = startTlsEnabled; }
        public boolean isStartTlsRequired() { return startTlsRequired; }
        public void setStartTlsRequired(boolean startTlsRequired) { this.startTlsRequired = startTlsRequired; }
        public boolean isAuthEnabled() { return authEnabled; }
        public void setAuthEnabled(boolean authEnabled) { this.authEnabled = authEnabled; }
        public Duration getConnectionTimeout() { return connectionTimeout; }
        public void setConnectionTimeout(Duration connectionTimeout) { this.connectionTimeout = connectionTimeout; }
        public Duration getWriteTimeout() { return writeTimeout; }
        public void setWriteTimeout(Duration writeTimeout) { this.writeTimeout = writeTimeout; }
    }

    public static class Sms {
        private SmsProviderEnum provider = SmsProviderEnum.NONE;
        private final Netgsm netgsm = new Netgsm();

        public SmsProviderEnum getProvider() {
            return provider;
        }

        public void setProvider(SmsProviderEnum provider) {
            this.provider = provider;
        }

        public Netgsm getNetgsm() {
            return netgsm;
        }
    }

    public static class Netgsm {
        private String endpoint = "https://api.netgsm.com.tr/sms/send/get";
        private String username = "";
        private String password = "";
        private String header = "";
        private String messageTemplate = "Viaverse verification code: %s";

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getMessageTemplate() {
            return messageTemplate;
        }

        public void setMessageTemplate(String messageTemplate) {
            this.messageTemplate = messageTemplate;
        }
    }

    public static class Social {
        private final SocialProvider google = new SocialProvider();
        private final SocialProvider apple = new SocialProvider();

        public SocialProvider getGoogle() {
            return google;
        }

        public SocialProvider getApple() {
            return apple;
        }
    }

    public static class SocialProvider {
        private boolean enabled;
        private String clientId = "";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }
    }

    /**
     * Local/test convenience flags. {@code enabled} wires the dev OTP delivery
     * adapter that logs OTPs to the console and forwards SMS OTPs to Mailpit
     * so developers can copy them from a single inbox; OTPs are always
     * cryptographically random regardless of this flag. {@code seedTestUsers}
     * pre-populates two known accounts so integration tests can sign in
     * without going through the registration flow. Both are blocked outside
     * the {@code local}/{@code test} profiles by AuthConfiguration.
     */
    public static class Debug {
        private boolean enabled;
        private boolean seedTestUsers;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isSeedTestUsers() {
            return seedTestUsers;
        }

        public void setSeedTestUsers(boolean seedTestUsers) {
            this.seedTestUsers = seedTestUsers;
        }
    }

    public static class RateLimit {
        private boolean enabled = true;
        private final AuthStart authStart = new AuthStart();
        private final OtpVerify otpVerify = new OtpVerify();
        private final PasswordLogin passwordLogin = new PasswordLogin();
        private final Resend resend = new Resend();
        private final Lockout lockout = new Lockout();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public AuthStart getAuthStart() {
            return authStart;
        }

        public OtpVerify getOtpVerify() {
            return otpVerify;
        }

        public PasswordLogin getPasswordLogin() {
            return passwordLogin;
        }

        public Resend getResend() {
            return resend;
        }

        public Lockout getLockout() {
            return lockout;
        }
    }

    public static class PasswordLogin {
        // Per-identifier window protects the targeted account; per-IP window
        // protects everyone else from one noisy host. We raised the
        // per-identifier ceiling above the classic OWASP "5 in 5 min" because
        // gate-then-record only counts confirmed failures: legitimate users
        // mistyping a password a few times in a row used to land them at the
        // limit before they realized they had CapsLock on. 10 leaves
        // breathing room while still bounding brute force well within the
        // 300s window.
        private long identifierWindowSeconds = 300;
        private int identifierMaxAttempts = 10;
        private long ipWindowSeconds = 60;
        private int ipMaxAttempts = 30;

        public long getIdentifierWindowSeconds() { return identifierWindowSeconds; }
        public void setIdentifierWindowSeconds(long v) { this.identifierWindowSeconds = v; }
        public int getIdentifierMaxAttempts() { return identifierMaxAttempts; }
        public void setIdentifierMaxAttempts(int v) { this.identifierMaxAttempts = v; }
        public long getIpWindowSeconds() { return ipWindowSeconds; }
        public void setIpWindowSeconds(long v) { this.ipWindowSeconds = v; }
        public int getIpMaxAttempts() { return ipMaxAttempts; }
        public void setIpMaxAttempts(int v) { this.ipMaxAttempts = v; }
    }

    public static class AuthStart {
        private long identifierWindowSeconds = 300;
        private int identifierMaxAttempts = 3;
        private long ipWindowSeconds = 60;
        private int ipMaxAttempts = 20;
        private long deviceWindowSeconds = 60;
        private int deviceMaxAttempts = 20;

        public long getIdentifierWindowSeconds() {
            return identifierWindowSeconds;
        }

        public void setIdentifierWindowSeconds(long identifierWindowSeconds) {
            this.identifierWindowSeconds = identifierWindowSeconds;
        }

        public int getIdentifierMaxAttempts() {
            return identifierMaxAttempts;
        }

        public void setIdentifierMaxAttempts(int identifierMaxAttempts) {
            this.identifierMaxAttempts = identifierMaxAttempts;
        }

        public long getIpWindowSeconds() {
            return ipWindowSeconds;
        }

        public void setIpWindowSeconds(long ipWindowSeconds) {
            this.ipWindowSeconds = ipWindowSeconds;
        }

        public int getIpMaxAttempts() {
            return ipMaxAttempts;
        }

        public void setIpMaxAttempts(int ipMaxAttempts) {
            this.ipMaxAttempts = ipMaxAttempts;
        }

        public long getDeviceWindowSeconds() {
            return deviceWindowSeconds;
        }

        public void setDeviceWindowSeconds(long deviceWindowSeconds) {
            this.deviceWindowSeconds = deviceWindowSeconds;
        }

        public int getDeviceMaxAttempts() {
            return deviceMaxAttempts;
        }

        public void setDeviceMaxAttempts(int deviceMaxAttempts) {
            this.deviceMaxAttempts = deviceMaxAttempts;
        }
    }

    public static class OtpVerify {
        private long flowWindowSeconds = 300;
        private int flowMaxAttempts = 5;
        private long ipWindowSeconds = 60;
        private int ipMaxAttempts = 30;

        public long getFlowWindowSeconds() {
            return flowWindowSeconds;
        }

        public void setFlowWindowSeconds(long flowWindowSeconds) {
            this.flowWindowSeconds = flowWindowSeconds;
        }

        public int getFlowMaxAttempts() {
            return flowMaxAttempts;
        }

        public void setFlowMaxAttempts(int flowMaxAttempts) {
            this.flowMaxAttempts = flowMaxAttempts;
        }

        public long getIpWindowSeconds() {
            return ipWindowSeconds;
        }

        public void setIpWindowSeconds(long ipWindowSeconds) {
            this.ipWindowSeconds = ipWindowSeconds;
        }

        public int getIpMaxAttempts() {
            return ipMaxAttempts;
        }

        public void setIpMaxAttempts(int ipMaxAttempts) {
            this.ipMaxAttempts = ipMaxAttempts;
        }
    }

    public static class Resend {
        private long cooldownSeconds = 60;

        public long getCooldownSeconds() {
            return cooldownSeconds;
        }

        public void setCooldownSeconds(long cooldownSeconds) {
            this.cooldownSeconds = cooldownSeconds;
        }
    }

    public static class Lockout {
        private long durationSeconds = 900;

        public long getDurationSeconds() {
            return durationSeconds;
        }

        public void setDurationSeconds(long durationSeconds) {
            this.durationSeconds = durationSeconds;
        }
    }
}
