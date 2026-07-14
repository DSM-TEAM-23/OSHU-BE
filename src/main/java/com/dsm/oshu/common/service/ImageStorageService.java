package com.dsm.oshu.common.service;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;

@Service
public class ImageStorageService {
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final S3Client s3Client;
    private final String bucket;

    public ImageStorageService(S3Client s3Client, @Value("${oshu.storage.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 이미지 파일을 선택해주세요.");
        }
        if (file.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("jpg, png, webp, gif 형식의 이미지만 업로드할 수 있습니다.");
        }

        try {
            String key = "uploads/" + UUID.randomUUID() + extensionOf(file.getOriginalFilename());
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .serverSideEncryption(ServerSideEncryption.AES256)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            URL url = s3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
            return url.toExternalForm();
        } catch (IOException | S3Exception exception) {
            throw new IllegalStateException("이미지 업로드에 실패했습니다.", exception);
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".bin";
        }
        return filename.substring(filename.lastIndexOf('.')).toLowerCase();
    }
}
