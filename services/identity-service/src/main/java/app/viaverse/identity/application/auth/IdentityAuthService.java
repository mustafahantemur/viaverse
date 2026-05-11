package app.viaverse.identity.application.auth;

import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.domain.auth.AccountStatus;
import app.viaverse.identity.domain.auth.AuthNextStep;
import app.viaverse.identity.domain.auth.ConsentCategory;
import app.viaverse.identity.domain.auth.ConsentType;
import app.viaverse.identity.domain.auth.LoginFlowStatus;
import app.viaverse.identity.domain.auth.OtpChallengeStatus;
import app.viaverse.identity.domain.auth.RefreshTokenStatus;
import app.viaverse.identity.domain.auth.RateLimitScope;
import app.viaverse.identity.domain.auth.SessionStatus;
import app.viaverse.identity.infrastructure.persistence.AuthLoginFlowJpaEntity;
import app.viaverse.identity.infrastructure.persistence.AuthLoginFlowJpaRepository;
import app.viaverse.identity.infrastructure.persistence.AuthOtpChallengeJpaEntity;
import app.viaverse.identity.infrastructure.persistence.AuthOtpChallengeJpaRepository;
import app.viaverse.identity.infrastructure.persistence.AuthRefreshTokenJpaEntity;
import app.viaverse.identity.infrastructure.persistence.AuthRefreshTokenJpaRepository;
import app.viaverse.identity.infrastructure.persistence.AuthSessionJpaEntity;
import app.viaverse.identity.infrastructure.persistence.AuthSessionJpaRepository;
import app.viaverse.identity.infrastructure.persistence.ConsentRecordJpaEntity;
import app.viaverse.identity.infrastructure.persistence.ConsentRecordJpaRepository;
import app.viaverse.identity.infrastructure.persistence.IdentityAccountJpaEntity;
import app.viaverse.identity.infrastructure.persistence.IdentityAccountJpaRepository;
import app.viaverse.identity.infrastructure.persistence.IdentityIdentifierJpaEntity;
import app.viaverse.identity.infrastructure.persistence.IdentityIdentifierJpaRepository;
import app.viaverse.identity.infrastructure.security.JwtPrincipal;
import app.viaverse.identity.infrastructure.security.JwtTokenService;
import app.viaverse.identity.infrastructure.security.SecureTokenGenerator;
import app.viaverse.identity.infrastructure.security.TokenHasher;
import app.viaverse.observability.audit.AuditAction;
import app.viaverse.observability.audit.AuditActor;
import app.viaverse.observability.audit.AuditEvent;
import app.viaverse.observability.audit.AuditLogger;
import app.viaverse.shared.kernel.error.UnauthorizedException;
import app.viaverse.shared.kernel.error.ValidationException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdentityAuthService {
    private static final Set<ConsentType> REQUIRED_CONSENTS = Set.of(
            ConsentType.TERMS_OF_SERVICE,
            ConsentType.KVKK_CLARIFICATION
    );

    private final AuthProperties properties;
    private final IdentifierNormalizer identifierNormalizer;
    private final OtpService otpService;
    private final AuthRateLimiter rateLimiter;
    private final TokenHasher tokenHasher;
    private final SecureTokenGenerator tokenGenerator;
    private final JwtTokenService jwtTokenService;
    private final IdentityAccountJpaRepository accountRepository;
    private final IdentityIdentifierJpaRepository identifierRepository;
    private final AuthLoginFlowJpaRepository flowRepository;
    private final AuthOtpChallengeJpaRepository challengeRepository;
    private final AuthSessionJpaRepository sessionRepository;
    private final AuthRefreshTokenJpaRepository refreshTokenRepository;
    private final ConsentRecordJpaRepository consentRecordRepository;
    private final AuditLogger auditLogger;

    public IdentityAuthService(
            AuthProperties properties,
            IdentifierNormalizer identifierNormalizer,
            OtpService otpService,
            AuthRateLimiter rateLimiter,
            TokenHasher tokenHasher,
            SecureTokenGenerator tokenGenerator,
            JwtTokenService jwtTokenService,
            IdentityAccountJpaRepository accountRepository,
            IdentityIdentifierJpaRepository identifierRepository,
            AuthLoginFlowJpaRepository flowRepository,
            AuthOtpChallengeJpaRepository challengeRepository,
            AuthSessionJpaRepository sessionRepository,
            AuthRefreshTokenJpaRepository refreshTokenRepository,
            ConsentRecordJpaRepository consentRecordRepository,
            AuditLogger auditLogger
    ) {
        this.properties = properties;
        this.identifierNormalizer = identifierNormalizer;
        this.otpService = otpService;
        this.rateLimiter = rateLimiter;
        this.tokenHasher = tokenHasher;
        this.tokenGenerator = tokenGenerator;
        this.jwtTokenService = jwtTokenService;
        this.accountRepository = accountRepository;
        this.identifierRepository = identifierRepository;
        this.flowRepository = flowRepository;
        this.challengeRepository = challengeRepository;
        this.sessionRepository = sessionRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.consentRecordRepository = consentRecordRepository;
        this.auditLogger = auditLogger;
    }

    @Transactional(noRollbackFor = RateLimitExceededException.class)
    public StartAuthResult start(String identifier, String clientIp, String clientFingerprint) {
        Instant now = Instant.now();
        NormalizedIdentifier normalized = identifierNormalizer.normalize(identifier);
        enforceStartRateLimits(normalized, clientIp, clientFingerprint);
        enforceResendCooldown(normalized, now);

        UUID accountId = identifierRepository.findByIdentifierTypeAndNormalizedIdentifier(
                        normalized.type(),
                        normalized.value()
                )
                .map(IdentityIdentifierJpaEntity::getAccountId)
                .orElse(null);

        Instant expiresAt = now.plus(properties.getOtp().getTtl());
        AuthLoginFlowJpaEntity flow = flowRepository.save(new AuthLoginFlowJpaEntity(
                UUID.randomUUID(),
                normalized.type(),
                normalized.value(),
                accountId,
                LoginFlowStatus.OTP_REQUIRED,
                expiresAt,
                now
        ));

        String otp = otpService.generate();
        challengeRepository.save(new AuthOtpChallengeJpaEntity(
                UUID.randomUUID(),
                flow.getId(),
                tokenHasher.hash(otp),
                properties.getOtp().getMaxAttempts(),
                expiresAt,
                now
        ));
        otpService.deliver(new OtpDeliveryRequest(flow.getId(), normalized, otp, expiresAt));

        return new StartAuthResult(
                flow.getId(),
                normalized.type(),
                AuthNextStep.OTP_REQUIRED,
                expiresAt,
                otpService.debugOtp(otp)
        );
    }

    @Transactional(noRollbackFor = {UnauthorizedException.class, RateLimitExceededException.class})
    public Object verifyOtp(UUID flowId, String otp, String userAgent, String clientIp) {
        Instant now = Instant.now();
        AuthLoginFlowJpaEntity flow = flowRepository.findById(flowId)
                .orElseThrow(() -> new ValidationException("Invalid auth flow", Map.of("flowId", "is invalid")));
        AuthOtpChallengeJpaEntity challenge = challengeRepository.findTopByFlowIdOrderByCreatedAtDesc(flowId)
                .orElseThrow(() -> new ValidationException("Invalid auth flow", Map.of("flowId", "has no OTP")));

        enforceOtpRateLimits(flow, clientIp);
        validateChallenge(flow, challenge, now);
        if (!tokenHasher.matches(otp, challenge.getOtpHash())) {
            challenge.recordFailure();
            if (challenge.getStatus() == OtpChallengeStatus.LOCKED) {
                flow.fail(LoginFlowStatus.FAILED, now);
                softLockOtp(flow);
            }
            throw new UnauthorizedException("Invalid OTP");
        }

        challenge.verify(now);
        flow.markOtpVerified(now);
        if (flow.getAccountId() != null) {
            IdentityAccountJpaEntity account = activeAccount(flow.getAccountId());
            audit(account.getId(), "identity-login");
            return createAuthResult(account, userAgent, now);
        }

        String registrationToken = tokenGenerator.generateUrlToken();
        Instant registrationExpiresAt = now.plus(properties.getOtp().getTtl());
        flow.requireRegistration(tokenHasher.hash(registrationToken), registrationExpiresAt, now);
        return new RegistrationRequiredResult(
                AuthNextStep.REGISTRATION_REQUIRED,
                registrationToken,
                registrationExpiresAt
        );
    }

    @Transactional
    public AuthResult register(
            String registrationToken,
            String displayName,
            String firstName,
            String lastName,
            List<ConsentInput> requiredConsents,
            boolean marketingConsentAccepted,
            String userAgent
    ) {
        Instant now = Instant.now();
        validateProfile(displayName);
        validateRequiredConsents(requiredConsents);

        AuthLoginFlowJpaEntity flow = flowRepository.findByRegistrationTokenHash(tokenHasher.hash(registrationToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid registration token"));
        if (flow.getStatus() != LoginFlowStatus.REGISTRATION_REQUIRED
                || flow.getRegistrationExpiresAt() == null
                || flow.getRegistrationExpiresAt().isBefore(now)) {
            throw new UnauthorizedException("Registration token expired");
        }

        UUID accountId = UUID.randomUUID();
        IdentityAccountJpaEntity account = accountRepository.save(new IdentityAccountJpaEntity(
                accountId,
                AccountStatus.ACTIVE,
                displayName.trim(),
                normalizeOptional(firstName),
                normalizeOptional(lastName),
                true,
                now,
                now
        ));
        identifierRepository.save(new IdentityIdentifierJpaEntity(
                UUID.randomUUID(),
                accountId,
                flow.getIdentifierType(),
                flow.getNormalizedIdentifier(),
                now,
                now
        ));

        for (ConsentInput consent : requiredConsents) {
            consentRecordRepository.save(new ConsentRecordJpaEntity(
                    UUID.randomUUID(),
                    accountId,
                    consent.type(),
                    ConsentCategory.REQUIRED_LEGAL,
                    consent.version(),
                    true,
                    now,
                    "registration"
            ));
        }
        consentRecordRepository.save(new ConsentRecordJpaEntity(
                UUID.randomUUID(),
                accountId,
                ConsentType.MARKETING_COMMUNICATION,
                ConsentCategory.OPTIONAL_MARKETING,
                "v1",
                marketingConsentAccepted,
                now,
                "registration"
        ));
        flow.complete(accountId, now);
        audit(accountId, "identity-register");
        return createAuthResult(account, userAgent, now);
    }

    @Transactional(readOnly = true)
    public AccountView currentAccount(String authorizationHeader) {
        JwtPrincipal principal = authenticate(authorizationHeader);
        AuthSessionJpaEntity session = activeSession(principal.sessionId(), Instant.now());
        return accountView(activeAccount(session.getAccountId()));
    }

    @Transactional(noRollbackFor = UnauthorizedException.class)
    public AuthResult refresh(String refreshToken, String userAgent) {
        Instant now = Instant.now();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ValidationException("Refresh token is required", Map.of("refreshToken", "must not be blank"));
        }
        AuthRefreshTokenJpaEntity currentToken = refreshTokenRepository
                .findByTokenHashAndStatus(tokenHasher.hash(refreshToken), RefreshTokenStatus.ACTIVE)
                .orElseGet(() -> handleRefreshTokenReuse(refreshToken, now));
        if (currentToken.getExpiresAt().isBefore(now)) {
            currentToken.expire(now);
            throw new UnauthorizedException("Refresh token expired");
        }

        AuthSessionJpaEntity session = activeSession(currentToken.getSessionId(), now);
        session.touch(now);
        String replacementRawToken = tokenGenerator.generateUrlToken();
        AuthRefreshTokenJpaEntity replacement = refreshTokenRepository.save(new AuthRefreshTokenJpaEntity(
                UUID.randomUUID(),
                session.getId(),
                tokenHasher.hash(replacementRawToken),
                now,
                now.plus(properties.getRefreshTokenTtl())
        ));
        currentToken.rotate(replacement.getId(), now);
        IdentityAccountJpaEntity account = activeAccount(session.getAccountId());
        audit(account.getId(), "identity-refresh");
        return new AuthResult(
                AuthNextStep.AUTHENTICATED,
                jwtTokenService.issue(account.getId(), session.getId(), now),
                replacementRawToken,
                jwtTokenService.expiresInSeconds(),
                accountView(account)
        );
    }

    @Transactional
    public void logout(String authorizationHeader, String refreshToken) {
        Instant now = Instant.now();
        UUID sessionId = null;
        if (refreshToken != null && !refreshToken.isBlank()) {
            AuthRefreshTokenJpaEntity token = refreshTokenRepository
                    .findByTokenHashAndStatus(tokenHasher.hash(refreshToken), RefreshTokenStatus.ACTIVE)
                    .orElse(null);
            if (token != null) {
                token.revoke(now);
                sessionId = token.getSessionId();
            }
        }
        if (sessionId == null) {
            sessionId = authenticate(authorizationHeader).sessionId();
        }
        AuthSessionJpaEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new UnauthorizedException("Invalid session"));
        session.revoke(now);
        for (AuthRefreshTokenJpaEntity token : refreshTokenRepository.findBySessionIdAndStatus(
                session.getId(),
                RefreshTokenStatus.ACTIVE
        )) {
            token.revoke(now);
        }
        audit(session.getAccountId(), "identity-logout");
    }

    private void validateChallenge(AuthLoginFlowJpaEntity flow, AuthOtpChallengeJpaEntity challenge, Instant now) {
        if (flow.getStatus() != LoginFlowStatus.OTP_REQUIRED || challenge.getStatus() != OtpChallengeStatus.ACTIVE) {
            throw new ValidationException("Auth flow is not waiting for OTP", Map.of("flowId", "is not active"));
        }
        if (flow.getExpiresAt().isBefore(now) || challenge.getExpiresAt().isBefore(now)) {
            flow.fail(LoginFlowStatus.EXPIRED, now);
            challenge.expire();
            throw new UnauthorizedException("OTP expired");
        }
    }

    private void enforceStartRateLimits(
            NormalizedIdentifier normalized,
            String clientIp,
            String clientFingerprint
    ) {
        AuthProperties.AuthStart authStart = properties.getRateLimit().getAuthStart();
        rateLimiter.checkAndIncrement(
                RateLimitScope.AUTH_START_IDENTIFIER,
                normalized.type() + ":" + normalized.value(),
                authStart.getIdentifierWindowSeconds(),
                authStart.getIdentifierMaxAttempts()
        );
        rateLimiter.checkAndIncrement(
                RateLimitScope.AUTH_START_IP,
                clientIp,
                authStart.getIpWindowSeconds(),
                authStart.getIpMaxAttempts()
        );
        rateLimiter.checkAndIncrement(
                RateLimitScope.AUTH_START_DEVICE,
                clientFingerprint,
                authStart.getIpWindowSeconds(),
                authStart.getIpMaxAttempts()
        );
    }

    private void enforceResendCooldown(NormalizedIdentifier normalized, Instant now) {
        long cooldownSeconds = properties.getRateLimit().getResend().getCooldownSeconds();
        if (cooldownSeconds <= 0) {
            return;
        }
        flowRepository.findTopByIdentifierTypeAndNormalizedIdentifierAndStatusOrderByCreatedAtDesc(
                        normalized.type(),
                        normalized.value(),
                        LoginFlowStatus.OTP_REQUIRED
                )
                .flatMap(flow -> challengeRepository.findTopByFlowIdOrderByCreatedAtDesc(flow.getId()))
                .filter(challenge -> challenge.getStatus() == OtpChallengeStatus.ACTIVE)
                .filter(challenge -> challenge.getExpiresAt().isAfter(now))
                .map(AuthOtpChallengeJpaEntity::getCreatedAt)
                .map(createdAt -> createdAt.plusSeconds(cooldownSeconds))
                .filter(cooldownUntil -> cooldownUntil.isAfter(now))
                .ifPresent(cooldownUntil -> {
                    throw new RateLimitExceededException(cooldownUntil.getEpochSecond() - now.getEpochSecond());
                });
    }

    private void enforceOtpRateLimits(AuthLoginFlowJpaEntity flow, String clientIp) {
        AuthProperties.OtpVerify otpVerify = properties.getRateLimit().getOtpVerify();
        String flowKey = flow.getId().toString();
        String identifierKey = flow.getIdentifierType() + ":" + flow.getNormalizedIdentifier();
        rateLimiter.ensureNotLocked(RateLimitScope.OTP_VERIFY_FLOW, flowKey);
        rateLimiter.ensureNotLocked(RateLimitScope.OTP_VERIFY_IDENTIFIER, identifierKey);
        rateLimiter.checkAndIncrement(
                RateLimitScope.OTP_VERIFY_FLOW,
                flowKey,
                otpVerify.getFlowWindowSeconds(),
                otpVerify.getFlowMaxAttempts()
        );
        rateLimiter.checkAndIncrement(
                RateLimitScope.OTP_VERIFY_IDENTIFIER,
                identifierKey,
                otpVerify.getFlowWindowSeconds(),
                otpVerify.getFlowMaxAttempts()
        );
        rateLimiter.checkAndIncrement(
                RateLimitScope.OTP_VERIFY_IP,
                clientIp,
                otpVerify.getIpWindowSeconds(),
                otpVerify.getIpMaxAttempts()
        );
    }

    private void softLockOtp(AuthLoginFlowJpaEntity flow) {
        long lockoutSeconds = properties.getRateLimit().getLockout().getDurationSeconds();
        String identifierKey = flow.getIdentifierType() + ":" + flow.getNormalizedIdentifier();
        rateLimiter.lock(RateLimitScope.OTP_VERIFY_FLOW, flow.getId().toString(), lockoutSeconds);
        rateLimiter.lock(RateLimitScope.OTP_VERIFY_IDENTIFIER, identifierKey, lockoutSeconds);
    }

    private AuthRefreshTokenJpaEntity handleRefreshTokenReuse(String refreshToken, Instant now) {
        AuthRefreshTokenJpaEntity token = refreshTokenRepository.findByTokenHash(tokenHasher.hash(refreshToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        if (token.getStatus() == RefreshTokenStatus.ROTATED || token.getStatus() == RefreshTokenStatus.REVOKED) {
            sessionRepository.findById(token.getSessionId()).ifPresent(session -> {
                session.revoke(now);
                for (AuthRefreshTokenJpaEntity activeToken : refreshTokenRepository.findBySessionIdAndStatus(
                        session.getId(),
                        RefreshTokenStatus.ACTIVE
                )) {
                    activeToken.revoke(now);
                }
            });
        }
        throw new UnauthorizedException("Invalid refresh token");
    }

    private void validateProfile(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new ValidationException("Display name is required", Map.of("displayName", "must not be blank"));
        }
    }

    private void validateRequiredConsents(List<ConsentInput> requiredConsents) {
        if (requiredConsents == null || requiredConsents.isEmpty()) {
            throw new ValidationException("Required consents are missing", Map.of("requiredConsents", "are required"));
        }
        Set<ConsentType> acceptedTypes = requiredConsents.stream()
                .filter(consent -> consent.version() != null && !consent.version().isBlank())
                .map(ConsentInput::type)
                .collect(Collectors.toSet());
        if (!acceptedTypes.containsAll(REQUIRED_CONSENTS)) {
            throw new ValidationException("Required consents are missing", Map.of(
                    "requiredConsents",
                    "must include TERMS_OF_SERVICE and KVKK_CLARIFICATION"
            ));
        }
    }

    private AuthResult createAuthResult(IdentityAccountJpaEntity account, String userAgent, Instant now) {
        AuthSessionJpaEntity session = sessionRepository.save(new AuthSessionJpaEntity(
                UUID.randomUUID(),
                account.getId(),
                now,
                now.plus(properties.getRefreshTokenTtl()),
                normalizeOptional(userAgent)
        ));
        String refreshToken = tokenGenerator.generateUrlToken();
        refreshTokenRepository.save(new AuthRefreshTokenJpaEntity(
                UUID.randomUUID(),
                session.getId(),
                tokenHasher.hash(refreshToken),
                now,
                now.plus(properties.getRefreshTokenTtl())
        ));
        return new AuthResult(
                AuthNextStep.AUTHENTICATED,
                jwtTokenService.issue(account.getId(), session.getId(), now),
                refreshToken,
                jwtTokenService.expiresInSeconds(),
                accountView(account)
        );
    }

    private JwtPrincipal authenticate(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Bearer token is required");
        }
        return jwtTokenService.verify(authorizationHeader.substring("Bearer ".length()), Instant.now());
    }

    private AuthSessionJpaEntity activeSession(UUID sessionId, Instant now) {
        AuthSessionJpaEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new UnauthorizedException("Invalid session"));
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new UnauthorizedException("Session is not active");
        }
        if (session.getExpiresAt().isBefore(now)) {
            session.expire(now);
            throw new UnauthorizedException("Session expired");
        }
        return session;
    }

    private IdentityAccountJpaEntity activeAccount(UUID accountId) {
        IdentityAccountJpaEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new UnauthorizedException("Account not found"));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new UnauthorizedException("Account is not active");
        }
        return account;
    }

    private AccountView accountView(IdentityAccountJpaEntity account) {
        return new AccountView(
                account.getId(),
                account.getStatus(),
                account.getDisplayName(),
                account.getFirstName(),
                account.getLastName(),
                account.isProfileCompleted(),
                account.getCreatedAt()
        );
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void audit(UUID accountId, String source) {
        auditLogger.record(new AuditEvent(
                UUID.randomUUID(),
                Instant.now(),
                new AuditActor("ACCOUNT", accountId.toString()),
                AuditAction.TECHNICAL_ACCESS,
                "identity",
                accountId.toString(),
                null,
                Map.of("source", source)
        ));
    }
}
