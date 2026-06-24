package app.viaverse.media.asset.infrastructure.adapter.out.storage;

import app.viaverse.media.asset.application.port.out.ObjectStorageGateway;
import app.viaverse.media.asset.domain.model.MediaAsset;
import app.viaverse.media.config.ObjectStorageProperties;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
public class S3ObjectStorageGateway implements ObjectStorageGateway {
    private final ObjectStorageProperties properties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3ObjectStorageGateway(
            ObjectStorageProperties properties,
            S3Client s3Client,
            S3Presigner s3Presigner
    ) {
        this.properties = properties;
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public UploadTarget createPresignedUpload(MediaAsset asset, Duration ttl) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.getBuckets().getMedia())
                .key(asset.getObjectKey())
                .contentType(asset.getContentType())
                .build();
        var presigned = s3Presigner.presignPutObject(PutObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .putObjectRequest(putObjectRequest)
                .build());
        return new UploadTarget(
                URI.create(presigned.url().toString()),
                Map.of("Content-Type", asset.getContentType())
        );
    }

    @Override
    public UploadedObject inspectUploadedObject(MediaAsset asset) {
        var response = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(properties.getBuckets().getMedia())
                .key(asset.getObjectKey())
                .build());
        return new UploadedObject(response.contentLength());
    }
}
