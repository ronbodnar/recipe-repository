package com.ronbodnar.recipes.image.dto;

import com.ronbodnar.recipes.image.ImagePurpose;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ImageUploadRequest(List<MultipartFile> files, ImagePurpose purpose) {}