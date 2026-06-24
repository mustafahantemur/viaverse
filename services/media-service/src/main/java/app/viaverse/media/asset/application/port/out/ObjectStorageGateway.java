package app.viaverse.media.asset.application.port.out;

import app.viaverse.media.asset.domain.model.MediaAsset;
import java.net.URI;
import java.time.Duration;
import java.util.Map;

public interface ObjectStorageGateway {
    UploadTarget createPresignedUpload(MediaAsset asset, Duration ttl);
    UploadedObject inspectUploadedObject(MediaAsset asset);

    record UploadTarget(URI url, Map<String, String> requiredHeaders) {
    }

    record UploadedObject(long byteSize) {
    }
}
