package app.viaverse.identity.account.infrastructure.adapter.in.web.dto.response;

import java.util.List;

public record TwoFactorBackupCodesResponse(List<String> backupCodes) {
}
