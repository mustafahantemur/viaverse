package app.viaverse.mockwebbff.app.seed;

import app.viaverse.mockwebbff.app.AppDtos.TransactionView;
import java.util.ArrayList;
import java.util.List;

public final class FinanceSeed {

    private FinanceSeed() {
    }

    public static ArrayList<TransactionView> transactions() {
        return new ArrayList<>(List.of(
            new TransactionView("txn-seed-hold", "MOCK_HOLD", "Paket teslimatı için mock ödeme kaydı", 20000, "TRY", "COMPLETED", "Kabul edilen teklif sonrası demo ödeme kaydı.", "request-accepted-delivery", SeedClock.minutesAgo(305), SeedClock.minutesAgo(250)),
            new TransactionView("txn-wallet-topup", "MOCK_TOPUP", "Demo cüzdan yüklemesi", 50000, "TRY", "COMPLETED", "Test verisi; gerçek ödeme sağlayıcısı yok.", null, SeedClock.minutesAgo(1440), SeedClock.minutesAgo(1440))
        ));
    }
}
