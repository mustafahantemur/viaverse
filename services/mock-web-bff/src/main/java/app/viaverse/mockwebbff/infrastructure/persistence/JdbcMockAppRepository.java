package app.viaverse.mockwebbff.infrastructure.persistence;

import app.viaverse.mockwebbff.app.MockAppRepository;
import app.viaverse.mockwebbff.app.MockAppState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcMockAppRepository implements MockAppRepository {

    private static final String DOCUMENT_KEY = "app-state-v1";

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public JdbcMockAppRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        ensureSchema();
    }

    @Override
    public synchronized MockAppState loadOrSeed(Supplier<MockAppState> seedSupplier) {
        List<String> rows = jdbcTemplate.query(
            "select document_json from mock_app_document where document_key = ?",
            (rs, rowNum) -> rs.getString("document_json"),
            DOCUMENT_KEY
        );
        if (!rows.isEmpty()) {
            return read(rows.getFirst());
        }
        MockAppState seeded = seedSupplier.get();
        save(seeded);
        return seeded;
    }

    @Override
    public synchronized void save(MockAppState state) {
        String json = write(state);
        int updated = jdbcTemplate.update(
            "update mock_app_document set document_json = ?, updated_at = current_timestamp where document_key = ?",
            json,
            DOCUMENT_KEY
        );
        if (updated == 0) {
            jdbcTemplate.update(
                "insert into mock_app_document(document_key, document_json, updated_at) values (?, ?, current_timestamp)",
                DOCUMENT_KEY,
                json
            );
        }
    }

    @Override
    public synchronized MockAppState reset(Supplier<MockAppState> seedSupplier) {
        jdbcTemplate.update("delete from mock_app_document where document_key = ?", DOCUMENT_KEY);
        MockAppState seeded = seedSupplier.get();
        save(seeded);
        return seeded;
    }

    private void ensureSchema() {
        jdbcTemplate.execute("""
            create table if not exists mock_app_document (
                document_key varchar(80) primary key,
                document_json clob not null,
                updated_at timestamp not null
            )
            """);
    }

    private MockAppState read(String json) {
        try {
            return objectMapper.readValue(json, MockAppState.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Mock app state document cannot be read", ex);
        }
    }

    private String write(MockAppState state) {
        try {
            return objectMapper.writeValueAsString(state);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Mock app state document cannot be written", ex);
        }
    }
}
