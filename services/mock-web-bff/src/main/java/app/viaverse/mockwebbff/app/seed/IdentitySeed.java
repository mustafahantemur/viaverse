package app.viaverse.mockwebbff.app.seed;

import app.viaverse.mockwebbff.app.AppDtos.IdentityAccountView;
import app.viaverse.mockwebbff.app.AppDtos.RegistrationDraftView;
import java.util.ArrayList;
import java.util.List;

public final class IdentitySeed {

    private IdentitySeed() {
    }

    public static ArrayList<IdentityAccountView> accounts() {
        return new ArrayList<>(List.of(
            new IdentityAccountView("identity-deniz", "user-standard", "deniz@viaverse.test", "+905551110001", "Password123!", "Deniz Arslan", "ACTIVE"),
            new IdentityAccountView("identity-ece", "user-provider", "ece@viaverse.test", "+905551110002", "Password123!", "Ece Kaya", "ACTIVE"),
            new IdentityAccountView("identity-mert", "user-business", "mert@viaverse.test", "+905551110003", "Password123!", "Mert Çınar", "ACTIVE")
        ));
    }

    public static ArrayList<RegistrationDraftView> registrationDrafts() {
        return new ArrayList<>();
    }
}
