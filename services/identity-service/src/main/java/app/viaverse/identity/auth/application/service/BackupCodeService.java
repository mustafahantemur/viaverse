package app.viaverse.identity.auth.application.service;

import app.viaverse.identity.account.application.port.out.BackupCodeRepository;
import app.viaverse.identity.account.domain.model.BackupCode;
import app.viaverse.identity.auth.infrastructure.security.TokenHasher;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Issues / verifies 2FA backup codes. Plaintext codes are only ever returned
 * to the user at issuance time; the database only stores HMAC hashes. We
 * generate 10 codes per enrollment, each 10 base32 characters
 * (5 bytes of entropy ≈ 40 bits) — Google's pattern.
 */
@Service
public class BackupCodeService {
    public static final int CODE_COUNT = 10;
    private static final int CODE_BYTES = 5;
    private static final char[] ALPHABET =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private final SecureRandom secureRandom;
    private final TokenHasher tokenHasher;
    private final BackupCodeRepository repository;

    public BackupCodeService(
            SecureRandom secureRandom,
            TokenHasher tokenHasher,
            BackupCodeRepository repository
    ) {
        this.secureRandom = secureRandom;
        this.tokenHasher = tokenHasher;
        this.repository = repository;
    }

    @Transactional
    public List<String> issueBatch(UUID accountId, Instant now) {
        repository.deleteByAccountId(accountId);
        List<String> plain = new ArrayList<>(CODE_COUNT);
        for (int i = 0; i < CODE_COUNT; i++) {
            String code = generateCode();
            plain.add(code);
            repository.save(BackupCode.issue(UUID.randomUUID(), accountId, tokenHasher.hash(code), now));
        }
        return plain;
    }

    @Transactional
    public boolean consume(UUID accountId, String submittedCode, Instant now) {
        if (submittedCode == null || submittedCode.isBlank()) {
            return false;
        }
        String normalized = submittedCode.trim().toUpperCase().replace("-", "").replace(" ", "");
        return repository.findByCodeHash(tokenHasher.hash(normalized))
                .filter(code -> code.getAccountId().equals(accountId) && !code.isUsed())
                .map(code -> {
                    code.markUsed(now);
                    repository.save(code);
                    return true;
                })
                .orElse(false);
    }

    private String generateCode() {
        char[] buf = new char[CODE_BYTES * 2];
        byte[] bytes = new byte[CODE_BYTES];
        secureRandom.nextBytes(bytes);
        for (int i = 0; i < CODE_BYTES; i++) {
            buf[i * 2] = ALPHABET[(bytes[i] >>> 4) & 0x0F];
            buf[i * 2 + 1] = ALPHABET[bytes[i] & 0x0F];
        }
        return new String(buf);
    }
}
