package app.viaverse.identity.config;

import app.viaverse.identity.auth.domain.enums.OtpDeliveryProviderEnum;
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
    private final Social social = new Social();
    private final Debug debug = new Debug();
    private final RateLimit rateLimit = new RateLimit();
    private final Consent consent = new Consent();

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

    public static class Consent {
        private String marketingVersion = "v1";

        public String getMarketingVersion() {
            return marketingVersion;
        }

        public void setMarketingVersion(String marketingVersion) {
            this.marketingVersion = marketingVersion;
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
        private final Delivery delivery = new Delivery();

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

        public Delivery getDelivery() {
            return delivery;
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

    public static class Delivery {
        private OtpDeliveryProviderEnum provider = OtpDeliveryProviderEnum.DEBUG;

        public OtpDeliveryProviderEnum getProvider() {
            return provider;
        }

        public void setProvider(OtpDeliveryProviderEnum provider) {
            this.provider = provider;
        }
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

    public static class Debug {
        private boolean enabled;
        private String fixedOtp = "";
        private boolean seedTestUsers;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getFixedOtp() {
            return fixedOtp;
        }

        public void setFixedOtp(String fixedOtp) {
            this.fixedOtp = fixedOtp;
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

        public Resend getResend() {
            return resend;
        }

        public Lockout getLockout() {
            return lockout;
        }
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
