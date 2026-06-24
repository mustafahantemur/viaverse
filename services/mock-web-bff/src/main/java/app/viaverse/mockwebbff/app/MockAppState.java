package app.viaverse.mockwebbff.app;

import app.viaverse.mockwebbff.app.AppDtos.BusinessView;
import app.viaverse.mockwebbff.app.AppDtos.ConversationView;
import app.viaverse.mockwebbff.app.AppDtos.FeedItemView;
import app.viaverse.mockwebbff.app.AppDtos.IdentityAccountView;
import app.viaverse.mockwebbff.app.AppDtos.MessageView;
import app.viaverse.mockwebbff.app.AppDtos.NotificationView;
import app.viaverse.mockwebbff.app.AppDtos.OfferView;
import app.viaverse.mockwebbff.app.AppDtos.PostCommentView;
import app.viaverse.mockwebbff.app.AppDtos.ProfileView;
import app.viaverse.mockwebbff.app.AppDtos.ProviderView;
import app.viaverse.mockwebbff.app.AppDtos.RegistrationDraftView;
import app.viaverse.mockwebbff.app.AppDtos.SavedSearchView;
import app.viaverse.mockwebbff.app.AppDtos.ServiceCategoryView;
import app.viaverse.mockwebbff.app.AppDtos.ServiceRequestView;
import app.viaverse.mockwebbff.app.AppDtos.SettingsView;
import app.viaverse.mockwebbff.app.AppDtos.TransactionView;
import app.viaverse.mockwebbff.app.AppDtos.UserView;
import java.util.List;

public record MockAppState(
    String currentUserId,
    List<IdentityAccountView> identityAccounts,
    List<RegistrationDraftView> registrationDrafts,
    List<UserView> users,
    List<ProfileView> profiles,
    SettingsView settings,
    List<ServiceCategoryView> categories,
    List<ProviderView> providers,
    List<BusinessView> businesses,
    List<FeedItemView> feedItems,
    List<PostCommentView> postComments,
    List<ServiceRequestView> serviceRequests,
    List<OfferView> offers,
    List<ConversationView> conversations,
    List<MessageView> messages,
    List<TransactionView> transactions,
    List<NotificationView> notifications,
    List<SavedSearchView> savedSearches
) {
}
