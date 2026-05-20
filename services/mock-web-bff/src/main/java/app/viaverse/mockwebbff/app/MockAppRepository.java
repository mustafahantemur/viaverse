package app.viaverse.mockwebbff.app;

import java.util.function.Supplier;

public interface MockAppRepository {

    MockAppState loadOrSeed(Supplier<MockAppState> seedSupplier);

    void save(MockAppState state);

    MockAppState reset(Supplier<MockAppState> seedSupplier);
}
