package com.dsm.oshu.common.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;

@Service
public class ImageStorageService {
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final String KEY_PREFIX = "uploads/";

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
            String filename = UUID.randomUUID() + extensionOf(file.getOriginalFilename());
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(KEY_PREFIX + filename)
                    .contentType(file.getContentType())
                    .serverSideEncryption(ServerSideEncryption.AES256)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            return "/uploads/" + filename;
        } catch (IOException | S3Exception exception) {
            throw new IllegalStateException("이미지 업로드에 실패했습니다.", exception);
        }
    }

    public StoredImage load(String filename) {
        validateFilename(filename);

        try {
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(KEY_PREFIX + filename)
                    .build());
            String contentType = objectBytes.response().contentType();
            return new StoredImage(
                    contentType == null || contentType.isBlank() ? "application/octet-stream" : contentType,
                    objectBytes.asByteArray());
        } catch (NoSuchKeyException exception) {
            throw new IllegalArgumentException("이미지를 찾을 수 없습니다.");
        } catch (S3Exception exception) {
            throw new IllegalStateException("이미지 조회에 실패했습니다.", exception);
        }
    }

    private void validateFilename(String filename) {
        if (filename == null || filename.isBlank() || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".bin";
        }
        return filename.substring(filename.lastIndexOf('.')).toLowerCase();
    }

    public record StoredImage(String contentType, byte[] content) {
    }
}
