package app.viaverse.mockwebbff.app.infrastructure.web;

import app.viaverse.mockwebbff.app.AppDtos.ConversationView;
import app.viaverse.mockwebbff.app.AppDtos.MessageView;
import app.viaverse.mockwebbff.app.AppDtos.SendMessageRequest;
import app.viaverse.mockwebbff.app.AppDtos.UpdateMessageRequest;
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
@RequestMapping("/api/app")
public class MessagingController {

    private final MockAppService service;

    public MessagingController(MockAppService service) {
        this.service = service;
    }

    @GetMapping("/conversations")
    ApiResponse<List<ConversationView>> conversations() {
        return ApiResponse.success(service.conversations());
    }

    @GetMapping("/conversations/{conversationId}/messages")
    ApiResponse<List<MessageView>> messages(@PathVariable String conversationId) {
        return ApiResponse.success(service.messages(conversationId));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    ApiResponse<MessageView> sendMessage(
        @PathVariable String conversationId,
        @RequestBody SendMessageRequest request
    ) {
        return ApiResponse.success(service.sendMessage(conversationId, request));
    }

    @PatchMapping("/conversations/{conversationId}/messages/{messageId}")
    ApiResponse<MessageView> updateMessage(
        @PathVariable String conversationId,
        @PathVariable String messageId,
        @RequestBody UpdateMessageRequest request
    ) {
        return ApiResponse.success(service.updateMessage(conversationId, messageId, request));
    }
}
