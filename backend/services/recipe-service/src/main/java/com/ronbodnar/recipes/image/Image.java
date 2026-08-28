package com.ronbodnar.recipes.image;

import jakarta.persistence.*;
import lombok.Data;

import java.nio.file.Path;
import java.util.UUID;

@Data
@Entity
@Table(name = "image")
public class Image {

    @Id
    private UUID id;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "storage_key")
    private String storageKey;

    @Column(name = "file_hash")
    private String fileHash;

    @Transient
    private Path tempFilePath;

    @Transient
    private Long size;
}