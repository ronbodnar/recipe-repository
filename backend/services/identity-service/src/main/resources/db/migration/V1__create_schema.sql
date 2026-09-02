CREATE TABLE `user_account` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `idp_subject` VARCHAR(255) NOT NULL,
    `display_name` VARCHAR(50) NOT NULL,
    `profile_image_id` binary(16) DEFAULT NULL,
    `created_at` datetime(6) NOT NULL,
    `last_modified_at` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_account_idp_subject` (`idp_subject`),
    CONSTRAINT `chk_user_account_display_name_length` CHECK (CHAR_LENGTH(`display_name`) >= 2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci