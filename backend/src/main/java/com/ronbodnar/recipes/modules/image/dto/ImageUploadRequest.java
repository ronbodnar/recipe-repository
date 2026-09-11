package com.ronbodnar.recipes.modules.image.dto;

import com.ronbodnar.recipes.modules.image.ImagePurpose;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ImageUploadRequest(List<MultipartFile> files, ImagePurpose purpose) {}