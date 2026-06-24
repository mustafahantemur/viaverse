package app.viaverse.mockwebbff.app;

import app.viaverse.mockwebbff.app.AppDtos.AuthSessionView;
import app.viaverse.mockwebbff.app.AppDtos.CapabilityTermsView;
import app.viaverse.mockwebbff.app.AppDtos.ConsentDocumentView;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordCompleteRequest;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordStartRequest;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordStartView;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordTokenView;
import app.viaverse.mockwebbff.app.AppDtos.ForgotPasswordVerifyRequest;
import app.viaverse.mockwebbff.app.AppDtos.IdentityAccountView;
import app.viaverse.mockwebbff.app.AppDtos.NotificationView;
import app.viaverse.mockwebbff.app.AppDtos.PasswordLoginRequest;
import app.viaverse.mockwebbff.app.AppDtos.ProfileView;
import app.viaverse.mockwebbff.app.AppDtos.RegisterStartRequest;
import app.viaverse.mockwebbff.app.AppDtos.RegisterStartView;
import app.viaverse.mockwebbff.app.AppDtos.RegisterVerifyEmailRequest;
import app.viaverse.mockwebbff.app.AppDtos.RegistrationDraftView;
import app.viaverse.mockwebbff.app.AppDtos.RequiredConsentsView;
import app.viaverse.mockwebbff.app.AppDtos.SessionView;
import app.viaverse.mockwebbff.app.AppDtos.SwitchPersonaRequest;
import app.viaverse.mockwebbff.app.AppDtos.UserView;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MockSessionService extends MockDomainService {

    private static final Logger log = LoggerFactory.getLogger(MockSessionService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public MockSessionService(MockAppRepository repository) {
        super(repository);
    }

    public synchronized RequiredConsentsView requiredConsents() {
        return new RequiredConsentsView(
            List.of(
                new ConsentDocumentView("TERMS", "LEGAL", "mock-2026-05", "/legal/terms"),
                new ConsentDocumentView("PRIVACY", "LEGAL", "mock-2026-05", "/legal/privacy")
            ),
            new ConsentDocumentView("MARKETING", "OPTIONAL", "mock-2026-05", "/legal/marketing")
        );
    }

    public synchronized CapabilityTermsView capabilityTerms() {
        return new CapabilityTermsView(List.of(
            new ConsentDocumentView("PROVIDER_TERMS", "CAPABILITY", "mock-provider-2026-05", "/legal/provider"),
            new ConsentDocumentView("BUSINESS_TERMS", "CAPABILITY", "mock-business-2026-05", "/legal/business")
        ));
    }

    public synchronized AuthSessionView passwordLogin(PasswordLoginRequest request) {
        requireText(request == null ? null : request.identifier(), "identifier");
        requireText(request.password(), "password");
        MockAppState state = state();
        IdentityAccountView account = state.identityAccounts().stream()
            .filter(identity -> matchesIdentifier(identity, request.identifier()))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mock identity account not found"));
        if (!account.password().equals(request.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mock identity password does not match");
        }
        MockAppState updated = withCurrentUser(state, account.accountId());
        repository.save(updated);
        return authSession(updated);
    }

    public synchronized AuthSessionView refreshAuth() {
        return authSession(state());
    }

    public synchronized void logoutAuth() {
        // No-op by design: mock identity does not manage secure cookies.
    }

    public synchronized RegisterStartView registerStart(RegisterStartRequest request) {
        requireText(request == null ? null : request.email(), "email");
        requireText(request.displayName(), "displayName");
        requireText(request.password(), "password");
        MockAppState state = state();
        boolean duplicate = state.identityAccounts().stream()
            .anyMatch(account -> account.email().equalsIgnoreCase(request.email().trim()));
        if (duplicate) {
            throw badRequest("This mock email is already registered");
        }
        String draftId = "draft-" + UUID.randomUUID();
        String otp = generateOtp();
        state.registrationDrafts().add(new RegistrationDraftView(
            draftId,
            request.email().trim().toLowerCase(Locale.ROOT),
            request.displayName().trim(),
            fallback(request.firstName(), ""),
            fallback(request.lastName(), ""),
            request.password(),
            now(),
            otp
        ));
        repository.save(state);
        sendEmail(
            request.email().trim(),
            "Viaverse - E-posta doğrulama kodunuz",
            "Merhaba " + request.displayName().trim() + ",\n\n"
                + "Doğrulama kodunuz: " + otp + "\n\n"
                + "Bu kod 15 dakika geçerlidir.\n\nViaverse"
        );
        return new RegisterStartView(draftId, "email-flow-" + draftId, Instant.now().plusSeconds(900).toString(), false);
    }

    public synchronized AuthSessionView registerVerifyEmail(RegisterVerifyEmailRequest request) {
        requireText(request == null ? null : request.draftId(), "draftId");
        requireText(request.otp(), "otp");
        MockAppState state = state();
        RegistrationDraftView draft = state.registrationDrafts().stream()
            .filter(item -> item.id().equals(request.draftId()))
            .findFirst()
            .orElseThrow(() -> badRequest("Registration draft expired"));
        if (draft.otp() != null && !draft.otp().equals(request.otp().trim())) {
            throw new ResponseStatusException(HttpStatus.valueOf(422), "Doğrulama kodu hatalı");
        }
        String accountId = "user-" + UUID.randomUUID();
        UserView user = new UserView(
            accountId,
            draft.displayName(),
            fallback(draft.firstName(), firstNameFromDisplay(draft.displayName())),
            fallback(draft.lastName(), ""),
            initials(draft.displayName()),
            "STANDARD",
            "Hizmet alan",
            "Konum seçilmedi",
            List.of(
                new AppDtos.CapabilityView("STANDARD", "Hizmet alan", true, "ENABLED", "Yakındaki akışı takip eder, paylaşım ve talep oluşturur."),
                new AppDtos.CapabilityView("INDIVIDUAL_PROVIDER", "Bireysel hizmet veren", false, "AVAILABLE", "İsterse hizmet veren görünümünü açabilir."),
                new AppDtos.CapabilityView("BUSINESS", "İşletme", false, "AVAILABLE", "İşletme profili başlatabilir.")
            )
        );
        state.users().add(user);
        state.identityAccounts().add(new IdentityAccountView(
            "identity-" + UUID.randomUUID(), accountId,
            draft.email(), null, draft.password(), draft.displayName(), "ACTIVE"
        ));
        state.profiles().add(new ProfileView(
            accountId, draft.displayName(),
            "Yeni Viaverse kullanıcısı",
            "Yakınındaki akışı ve hizmetleri yeni keşfediyor.",
            "Konum seçilmedi",
            "STANDARD",
            user.capabilities(), null, null, 48, 42
        ));
        state.registrationDrafts().removeIf(item -> item.id().equals(draft.id()));
        MockAppState updated = withCurrentUser(state, accountId);
        repository.save(updated);
        return authSession(updated);
    }

    public synchronized ForgotPasswordStartView forgotPasswordStart(ForgotPasswordStartRequest request) {
        requireText(request == null ? null : request.identifier(), "identifier");
        String otp = generateOtp();
        if (request.identifier().contains("@")) {
            sendEmail(
                request.identifier().trim(),
                "Viaverse - Şifre sıfırlama kodunuz",
                "Şifre sıfırlama kodunuz: " + otp + "\n\n"
                    + "Bu kod 15 dakika geçerlidir.\n\nViaverse"
            );
        }
        return new ForgotPasswordStartView(
            "forgot-" + UUID.randomUUID(),
            request.identifier().contains("@") ? "EMAIL" : "PHONE",
            Instant.now().plusSeconds(900).toString()
        );
    }

    public synchronized ForgotPasswordTokenView forgotPasswordVerify(ForgotPasswordVerifyRequest request) {
        requireText(request == null ? null : request.flowId(), "flowId");
        requireText(request.otp(), "otp");
        return new ForgotPasswordTokenView("reset-" + UUID.randomUUID(), Instant.now().plusSeconds(900).toString());
    }

    public synchronized void forgotPasswordComplete(ForgotPasswordCompleteRequest request) {
        requireText(request == null ? null : request.resetToken(), "resetToken");
        requireText(request.newPassword(), "newPassword");
    }

    public synchronized SessionView session() {
        MockAppState state = state();
        return new SessionView(currentUser(state), state.users());
    }

    public synchronized SessionView switchPersona(SwitchPersonaRequest request) {
        if (request == null || isBlank(request.personaId())) {
            throw badRequest("personaId is required");
        }
        MockAppState state = state();
        UserView next = findUser(state, request.personaId());
        MockAppState updated = withCurrentUser(state, next.id());
        repository.save(updated);
        return new SessionView(next, updated.users());
    }

    public synchronized List<NotificationView> notifications() {
        return state().notifications().stream()
            .sorted(Comparator.comparing(NotificationView::createdAt).reversed())
            .toList();
    }

    public synchronized SessionView reset() {
        MockAppState reset = repository.reset(SeedData::initialState);
        return new SessionView(currentUser(reset), reset.users());
    }

    private AuthSessionView authSession(MockAppState state) {
        return new AuthSessionView(
            "AUTHENTICATED",
            "mock-access-" + state.currentUserId(),
            Instant.now().plusSeconds(3600).toString(),
            "mock-refresh-" + state.currentUserId(),
            Instant.now().plusSeconds(2_592_000).toString(),
            currentUser(state)
        );
    }

    private boolean matchesIdentifier(IdentityAccountView identity, String identifier) {
        String normalized = identifier.trim().toLowerCase(Locale.ROOT);
        return identity.email().equalsIgnoreCase(normalized)
            || (identity.phone() != null && identity.phone().replace(" ", "").equals(identifier.replace(" ", "")));
    }

    private MockAppState withCurrentUser(MockAppState state, String currentUserId) {
        return new MockAppState(
            currentUserId,
            state.identityAccounts(), state.registrationDrafts(),
            state.users(), state.profiles(), state.settings(),
            state.categories(), state.providers(), state.businesses(),
            state.feedItems(), state.postComments(),
            state.serviceRequests(), state.offers(),
            state.conversations(), state.messages(),
            state.transactions(), state.notifications(), state.savedSearches()
        );
    }

    private String generateOtp() {
        return String.format("%06d", (int) (Math.random() * 900000) + 100000);
    }

    private void sendEmail(String to, String subject, String text) {
        if (mailSender == null) {
            log.debug("Mail sender not configured — skipping email to {}", to);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@viaverse.app");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.debug("Mock email sent to {}", to);
        } catch (Exception e) {
            log.warn("Mock email send failed to {}: {}", to, e.getMessage());
        }
    }
}
