package com.dsm.oshu.common.presentation;

import com.dsm.oshu.common.presentation.dto.ImageUploadResponse;
import com.dsm.oshu.common.service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Owner Uploads", description = "점주 이미지 업로드")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/owner/uploads")
public class OwnerUploadController {
    private final ImageStorageService imageStorageService;

    public OwnerUploadController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @Operation(summary = "이미지 업로드")
    @PostMapping("/images")
    @ResponseStatus(HttpStatus.CREATED)
    public ImageUploadResponse uploadImage(@RequestParam("image") MultipartFile image) {
        return new ImageUploadResponse(imageStorageService.store(image));
    }
}
