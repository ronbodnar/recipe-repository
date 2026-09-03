package com.ronbodnar.recipes.image;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "image")
@EntityListeners(AuditingEntityListener.class)
public class Image {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose")
    private ImagePurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ImageStatus status;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "storage_key")
    private String storageKey;

    @Column(name = "file_hash")
    private String fileHash;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private Path tempFilePath;

    @Transient
    private Long size;
}