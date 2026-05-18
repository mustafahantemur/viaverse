package app.viaverse.media.asset.application.port.out;

import app.viaverse.media.asset.domain.model.MediaUploadSession;

public interface MediaUploadSessionRepository {
    MediaUploadSession save(MediaUploadSession session);
}
