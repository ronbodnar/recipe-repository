package com.ronbodnar.recipes.modules.image;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import org.apache.tika.Tika;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

@Component
public final class ImageFactory {

    private static final Tika TIKA = new Tika();

    private ImageFactory() {}

    public static Image createFromMultipartFile(MultipartFile file, ImagePurpose purpose) {
        try {
            Path tempFile = Files.createTempFile("upload-", ".tmp");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            try (InputStream is = file.getInputStream();
                 DigestInputStream dis = new DigestInputStream(is, digest);
                 OutputStream os = Files.newOutputStream(tempFile)) {

                dis.transferTo(os);
            }

            String hash = HexFormat.of().formatHex(digest.digest());

            Image image = new Image();
            image.setPurpose(purpose);
            image.setStatus(ImageStatus.STAGED);
            image.setId(UUID.randomUUID());
            image.setFileHash(hash);
            image.setTempFilePath(tempFile);
            image.setContentType(TIKA.detect(tempFile));
            image.setSize(file.getSize());

            return image;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED, "Failed to process temp image file: " + e.getMessage());
        }
    }
}