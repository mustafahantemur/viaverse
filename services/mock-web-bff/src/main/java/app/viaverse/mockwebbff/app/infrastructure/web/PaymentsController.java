package app.viaverse.mockwebbff.app.infrastructure.web;

import app.viaverse.mockwebbff.app.AppDtos.CreatePaymentIntentRequest;
import app.viaverse.mockwebbff.app.AppDtos.PaymentStatusRequest;
import app.viaverse.mockwebbff.app.AppDtos.TransactionView;
import app.viaverse.mockwebbff.app.MockAppService;
import app.viaverse.mockwebbff.shared.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/payments")
public class PaymentsController {

    private final MockAppService service;

    public PaymentsController(MockAppService service) {
        this.service = service;
    }

    @GetMapping("/transactions")
    ApiResponse<List<TransactionView>> transactions() {
        return ApiResponse.success(service.transactions());
    }

    @PostMapping("/mock-intents")
    ApiResponse<TransactionView> createIntent(@RequestBody CreatePaymentIntentRequest request) {
        return ApiResponse.success(service.createPaymentIntent(request));
    }

    @PatchMapping("/mock-intents/{transactionId}")
    ApiResponse<TransactionView> updateStatus(
        @PathVariable String transactionId,
        @RequestBody PaymentStatusRequest request
    ) {
        return ApiResponse.success(service.updatePaymentStatus(transactionId, request));
    }
}
