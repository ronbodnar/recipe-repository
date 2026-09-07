CREATE TABLE `user_account` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `idp_subject` VARCHAR(255) NOT NULL,
    `display_name` VARCHAR(50) NOT NULL,
    `profile_image_id` BINARY(16) DEFAULT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `last_modified_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_account_idp_subject` (`idp_subject`),
    CONSTRAINT `chk_user_account_display_name_length` CHECK (CHAR_LENGTH(`display_name`) >= 2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_group` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `created_at` datetime(6) NOT NULL,
    `last_modified_at` datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_group_member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `group_id` BIGINT NOT NULL,
    `user_account_id` BIGINT NOT NULL,
    `role` enum('OWNER','ADMIN','MEMBER') NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_group_member_group_user` (`group_id`,`user_account_id`),
    CONSTRAINT `fk_group_member_group` FOREIGN KEY (`group_id`) REFERENCES `user_group` (`id`),
    CONSTRAINT `fk_group_member_user_account` FOREIGN KEY (`user_account_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;