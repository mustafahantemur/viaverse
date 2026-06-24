package app.viaverse.mockwebbff.app.infrastructure.web;

import app.viaverse.mockwebbff.app.AppDtos.CreateOfferRequest;
import app.viaverse.mockwebbff.app.AppDtos.CreateServiceRequestRequest;
import app.viaverse.mockwebbff.app.AppDtos.OfferAcceptanceView;
import app.viaverse.mockwebbff.app.AppDtos.OfferView;
import app.viaverse.mockwebbff.app.AppDtos.OpportunityView;
import app.viaverse.mockwebbff.app.AppDtos.ServiceRequestView;
import app.viaverse.mockwebbff.app.MockRequestService;
import app.viaverse.mockwebbff.shared.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
public class RequestOfferController {

    private final MockRequestService service;

    public RequestOfferController(MockRequestService service) {
        this.service = service;
    }

    @GetMapping("/requests/mine")
    ApiResponse<List<ServiceRequestView>> myRequests() {
        return ApiResponse.success(service.myRequests());
    }

    @PostMapping("/requests")
    ApiResponse<ServiceRequestView> createRequest(@RequestBody CreateServiceRequestRequest request) {
        return ApiResponse.success(service.createRequest(request));
    }

    @GetMapping("/opportunities")
    ApiResponse<List<OpportunityView>> opportunities() {
        return ApiResponse.success(service.opportunities());
    }

    @PostMapping("/offers")
    ApiResponse<OfferView> createOffer(@RequestBody CreateOfferRequest request) {
        return ApiResponse.success(service.createOffer(request));
    }

    @GetMapping("/offers/mine")
    ApiResponse<List<OfferView>> myOffers() {
        return ApiResponse.success(service.myOffers());
    }

    @GetMapping("/requests/{requestId}/offers")
    ApiResponse<List<OfferView>> requestOffers(@PathVariable String requestId) {
        return ApiResponse.success(service.offersForRequest(requestId));
    }

    @PostMapping("/offers/{offerId}/accept")
    ApiResponse<OfferAcceptanceView> acceptOffer(@PathVariable String offerId) {
        return ApiResponse.success(service.acceptOffer(offerId));
    }
}
