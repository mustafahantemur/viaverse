package app.viaverse.identity.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.viaverse.identity.account.domain.AccountStatusEnum;
import app.viaverse.identity.account.domain.AccountRoleEnum;
import app.viaverse.identity.account.domain.model.Account;
import app.viaverse.identity.auth.application.port.in.SocialSignInUseCase;
import app.viaverse.identity.auth.application.port.out.AuthLoginFlowRepository;
import app.viaverse.identity.auth.application.port.out.IdentifierRepository;
import app.viaverse.identity.auth.application.port.out.SocialAuthPort;
import app.viaverse.identity.auth.application.service.AuthAbuseProtectionService;
import app.viaverse.identity.auth.application.service.AuthSessionIssuer;
import app.viaverse.identity.auth.application.service.RegistrationTokenService;
import app.viaverse.identity.auth.domain.enums.AuthNextStepEnum;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.enums.LoginFlowStatusEnum;
import app.viaverse.identity.auth.domain.enums.SocialAuthProviderEnum;
import app.viaverse.identity.auth.domain.model.AuthLoginFlow;
import app.viaverse.identity.auth.domain.model.AuthSession;
import app.viaverse.identity.auth.domain.model.IdentityIdentifier;
import app.viaverse.identity.auth.domain.value.SocialIdentity;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.IdentityException;
import app.viaverse.identity.shared.normalization.IdentifierNormalizer;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SocialSignInUseCaseImplTest {

    private static final Instant NOW = Instant.parse("2026-05-16T12:00:00Z");
    private static final SocialIdentity GOOGLE_IDENTITY = new SocialIdentity(
            SocialAuthProviderEnum.GOOGLE, "subject-123", "user@example.com", true);

    private SocialAuthPort googlePort;
    private IdentifierRepository identifierRepository;
    private AuthLoginFlowRepository flowRepository;
    private RegistrationTokenService registrationTokenService;
    private AuthSessionIssuer sessionIssuer;
    private AuthAbuseProtectionService abuseProtectionService;
    private AuthProperties properties;

    private SocialSignInUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        googlePort = org.mockito.Mockito.mock(SocialAuthPort.class);
        identifierRepository = org.mockito.Mockito.mock(IdentifierRepository.class);
        flowRepository = org.mockito.Mockito.mock(AuthLoginFlowRepository.class);
        registrationTokenService = org.mockito.Mockito.mock(RegistrationTokenService.class);
        sessionIssuer = org.mockito.Mockito.mock(AuthSessionIssuer.class);
        abuseProtectionService = org.mockito.Mockito.mock(AuthAbuseProtectionService.class);

        properties = new AuthProperties();
        properties.getSocial().getGoogle().setEnabled(true);
        properties.getOtp().setTtl(Duration.ofMinutes(10));

        when(googlePort.supports(SocialAuthProviderEnum.GOOGLE)).thenReturn(true);

        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        useCase = new SocialSignInUseCaseImpl(
                clock,
                properties,
                List.of(googlePort),
                new IdentifierNormalizer(),
                identifierRepository,
                flowRepository,
                registrationTokenService,
                sessionIssuer,
                abuseProtectionService
        );
    }

    @Test
    void disabledProviderThrowsBeforeContactingPort() {
        properties.getSocial().getGoogle().setEnabled(false);

        assertThatThrownBy(() -> useCase.execute(command("id-token", "nonce-1")))
                .isInstanceOf(IdentityException.class);

        verify(googlePort, never()).verify(anyString(), anyString());
        verify(abuseProtectionService, never())
                .enforceSocialStart(any(), anyString(), anyString());
    }

    @Test
    void existingSocialIdentifierAuthenticatesDirectly() {
        UUID accountId = UUID.randomUUID();
        String normalized = GOOGLE_IDENTITY.normalizedIdentifier();
        when(googlePort.verify("id-token", "nonce-1")).thenReturn(GOOGLE_IDENTITY);
        when(identifierRepository.findByTypeAndValue(IdentifierTypeEnum.SOCIAL, normalized))
                .thenReturn(Optional.of(new IdentityIdentifier(
                        UUID.randomUUID(), accountId, IdentifierTypeEnum.SOCIAL, normalized, NOW, NOW)));

        Account account = stubAccount(accountId);
        AuthSession session = AuthSession.issue(
                UUID.randomUUID(), accountId, NOW.plusSeconds(900),
                "ua", null, null, null, "1.1.1.1", NOW);
        when(sessionIssuer.activeAccount(accountId)).thenReturn(account);
        when(sessionIssuer.issue(eq(account), anyString(), anyString(), eq(NOW)))
                .thenReturn(new AuthSessionIssuer.Issued(
                        session, "access", NOW.plusSeconds(900), "refresh", NOW.plusSeconds(3600)));

        SocialSignInUseCase.Result result = useCase.execute(command("id-token", "nonce-1"));

        assertThat(result.nextStep()).isEqualTo(AuthNextStepEnum.AUTHENTICATED);
        assertThat(result.accountId()).isEqualTo(accountId);
        assertThat(result.accessToken()).isEqualTo("access");
        verify(abuseProtectionService, times(1))
                .enforceSocialStart(any(), eq("1.1.1.1"), eq("fp-1"));
        verify(flowRepository, never()).save(any());
    }

    @Test
    void unknownSocialIdentifierIssuesRegistrationToken() {
        when(googlePort.verify("id-token", "nonce-1")).thenReturn(
                new SocialIdentity(SocialAuthProviderEnum.GOOGLE, "subject-new", null, false));
        when(identifierRepository.findByTypeAndValue(eq(IdentifierTypeEnum.SOCIAL), anyString()))
                .thenReturn(Optional.empty());

        ArgumentCaptor<AuthLoginFlow> flowCaptor = ArgumentCaptor.forClass(AuthLoginFlow.class);
        when(flowRepository.save(flowCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(registrationTokenService.requireRegistration(any(AuthLoginFlow.class), eq(NOW)))
                .thenReturn(new RegistrationTokenService.Issued("reg-token", NOW.plusSeconds(600)));

        SocialSignInUseCase.Result result = useCase.execute(command("id-token", "nonce-1"));

        assertThat(result.nextStep()).isEqualTo(AuthNextStepEnum.REGISTRATION_REQUIRED);
        assertThat(result.registrationToken()).isEqualTo("reg-token");
        assertThat(flowCaptor.getValue().getStatus()).isEqualTo(LoginFlowStatusEnum.EXTERNAL_VERIFIED);
        assertThat(flowCaptor.getValue().getIdentifierType()).isEqualTo(IdentifierTypeEnum.SOCIAL);
        verify(sessionIssuer, never()).issue(any(), anyString(), anyString(), any());
    }

    private SocialSignInUseCase.Command command(String idToken, String nonce) {
        return new SocialSignInUseCase.Command(
                SocialAuthProviderEnum.GOOGLE, idToken, nonce, "ua", "1.1.1.1", "fp-1");
    }

    private Account stubAccount(UUID accountId) {
        return new Account(
                accountId,
                AccountStatusEnum.ACTIVE,
                Set.of(AccountRoleEnum.USER),
                "display",
                "First",
                "Last",
                true,
                NOW,
                NOW
        );
    }
}
