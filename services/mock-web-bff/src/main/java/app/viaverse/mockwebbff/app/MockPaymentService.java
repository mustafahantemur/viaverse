package app.viaverse.mockwebbff.app;

import app.viaverse.mockwebbff.app.AppDtos.CreatePaymentIntentRequest;
import app.viaverse.mockwebbff.app.AppDtos.PaymentStatusRequest;
import app.viaverse.mockwebbff.app.AppDtos.TransactionView;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MockPaymentService extends MockDomainService {

    public MockPaymentService(MockAppRepository repository) {
        super(repository);
    }

    public synchronized List<TransactionView> transactions() {
        return state().transactions().stream()
            .sorted(Comparator.comparing(TransactionView::createdAt).reversed())
            .toList();
    }

    public synchronized TransactionView createPaymentIntent(CreatePaymentIntentRequest request) {
        requireText(request == null ? null : request.title(), "title");
        if (request.amountMinor() <= 0) {
            throw badRequest("amountMinor must be positive");
        }
        MockAppState state = state();
        String now = now();
        TransactionView transaction = new TransactionView(
            "txn-" + UUID.randomUUID(),
            "MOCK_INTENT",
            request.title().trim(),
            request.amountMinor(),
            fallback(request.currency(), "TRY"),
            "PENDING",
            "Mock ödeme niyeti oluşturuldu; gerçek ödeme sağlayıcısı yok.",
            request.relatedRequestId(),
            now, now
        );
        state.transactions().add(transaction);
        repository.save(state);
        return transaction;
    }

    public synchronized TransactionView updatePaymentStatus(String transactionId, PaymentStatusRequest request) {
        MockAppState state = state();
        TransactionView current = state.transactions().stream()
            .filter(transaction -> transaction.id().equals(transactionId))
            .findFirst()
            .orElseThrow(() -> notFound("Transaction not found"));
        String status = normalizePaymentStatus(request == null ? null : request.status());
        TransactionView updated = new TransactionView(
            current.id(), current.type(), current.title(),
            current.amountMinor(), current.currency(),
            status, current.description(), current.relatedRequestId(),
            current.createdAt(), now()
        );
        replaceTransaction(state, updated);
        repository.save(state);
        return updated;
    }

    private String normalizePaymentStatus(String rawStatus) {
        if (isBlank(rawStatus)) throw badRequest("status is required");
        String status = rawStatus.trim().toUpperCase(Locale.ROOT);
        if (!List.of("PENDING", "COMPLETED", "FAILED", "CANCELLED").contains(status)) {
            throw badRequest("Unsupported payment status");
        }
        return status;
    }

    private void replaceTransaction(MockAppState state, TransactionView updated) {
        for (int i = 0; i < state.transactions().size(); i++) {
            if (state.transactions().get(i).id().equals(updated.id())) {
                state.transactions().set(i, updated);
                return;
            }
        }
        throw notFound("Transaction not found");
    }
}
