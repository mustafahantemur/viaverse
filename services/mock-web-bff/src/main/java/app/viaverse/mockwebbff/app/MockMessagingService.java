package app.viaverse.mockwebbff.app;

import app.viaverse.mockwebbff.app.AppDtos.ConversationView;
import app.viaverse.mockwebbff.app.AppDtos.MessageView;
import app.viaverse.mockwebbff.app.AppDtos.SendMessageRequest;
import app.viaverse.mockwebbff.app.AppDtos.UpdateMessageRequest;
import app.viaverse.mockwebbff.app.AppDtos.UserView;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MockMessagingService extends MockDomainService {

    public MockMessagingService(MockAppRepository repository) {
        super(repository);
    }

    public synchronized List<ConversationView> conversations() {
        return state().conversations().stream()
            .sorted(Comparator.comparing(ConversationView::lastMessageAt).reversed())
            .toList();
    }

    public synchronized List<MessageView> messages(String conversationId) {
        return state().messages().stream()
            .filter(message -> message.conversationId().equals(conversationId))
            .sorted(Comparator.comparing(MessageView::createdAt))
            .toList();
    }

    public synchronized MessageView sendMessage(String conversationId, SendMessageRequest request) {
        requireText(request == null ? null : request.body(), "body");
        MockAppState state = state();
        ConversationView conversation = findConversation(state, conversationId);
        UserView user = currentUser(state);
        String now = now();
        MessageView message = new MessageView(
            "message-" + UUID.randomUUID(), conversationId,
            user.id(), user.displayName(),
            request.body().trim(), false, now, now
        );
        state.messages().add(message);
        replaceConversation(state, new ConversationView(
            conversation.id(), conversation.title(), conversation.contextLabel(),
            conversation.participantName(), conversation.participantType(),
            message.body(), now, conversation.unreadCount(),
            conversation.relatedRequestId(), conversation.relatedOfferId()
        ));
        repository.save(state);
        return message;
    }

    public synchronized MessageView updateMessage(String conversationId, String messageId, UpdateMessageRequest request) {
        requireText(request == null ? null : request.body(), "body");
        MockAppState state = state();
        UserView user = currentUser(state);
        ConversationView conversation = findConversation(state, conversationId);
        MessageView current = findMessage(state, conversationId, messageId);
        if (current.system()) {
            throw badRequest("System messages cannot be edited");
        }
        if (!current.senderId().equals(user.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the sender can edit this message");
        }
        String now = now();
        MessageView updated = new MessageView(
            current.id(), current.conversationId(), current.senderId(), current.senderName(),
            request.body().trim(), false, current.createdAt(), now
        );
        replaceMessage(state, updated);
        MessageView latest = latestMessage(state, conversationId);
        if (latest != null && latest.id().equals(updated.id())) {
            replaceConversation(state, new ConversationView(
                conversation.id(), conversation.title(), conversation.contextLabel(),
                conversation.participantName(), conversation.participantType(),
                updated.body(), conversation.lastMessageAt(), conversation.unreadCount(),
                conversation.relatedRequestId(), conversation.relatedOfferId()
            ));
        }
        repository.save(state);
        return updated;
    }

    private ConversationView findConversation(MockAppState state, String conversationId) {
        return state.conversations().stream()
            .filter(conversation -> conversation.id().equals(conversationId))
            .findFirst()
            .orElseThrow(() -> notFound("Conversation not found"));
    }

    private MessageView findMessage(MockAppState state, String conversationId, String messageId) {
        return state.messages().stream()
            .filter(message -> message.conversationId().equals(conversationId))
            .filter(message -> message.id().equals(messageId))
            .findFirst()
            .orElseThrow(() -> notFound("Message not found"));
    }

    private void replaceConversation(MockAppState state, ConversationView updated) {
        for (int i = 0; i < state.conversations().size(); i++) {
            if (state.conversations().get(i).id().equals(updated.id())) {
                state.conversations().set(i, updated);
                return;
            }
        }
        throw notFound("Conversation not found");
    }

    private void replaceMessage(MockAppState state, MessageView updated) {
        for (int i = 0; i < state.messages().size(); i++) {
            if (state.messages().get(i).id().equals(updated.id())) {
                state.messages().set(i, updated);
                return;
            }
        }
        throw notFound("Message not found");
    }

    private MessageView latestMessage(MockAppState state, String conversationId) {
        return state.messages().stream()
            .filter(message -> message.conversationId().equals(conversationId))
            .max(Comparator.comparing(MessageView::createdAt))
            .orElse(null);
    }
}
