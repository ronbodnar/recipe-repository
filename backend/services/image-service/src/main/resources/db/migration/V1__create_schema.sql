CREATE TABLE `image` (
    `id` binary(16) NOT NULL,
    `content_type` varchar(255) DEFAULT NULL,
    `file_hash` varchar(255) DEFAULT NULL,
    `purpose` enum('PROFILE','RECIPE') DEFAULT NULL,
    `status` enum('ATTACHED','STAGED','MARKED_FOR_DELETION') DEFAULT NULL,
    `created_at` datetime(6) NOT NULL,
    `storage_key` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci