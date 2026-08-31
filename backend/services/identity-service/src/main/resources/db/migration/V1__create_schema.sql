CREATE TABLE `user_account` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) NOT NULL,
    `email` varchar(100) NOT NULL,
    `family_name` varchar(30) DEFAULT NULL,
    `given_name` varchar(30) DEFAULT NULL,
    `keycloak_subject` binary(16) NOT NULL,
    `last_modified_at` datetime(6) NOT NULL,
    `profile_image_id` binary(16) DEFAULT NULL,
    `username` varchar(50) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_account_email` (`email`),
    UNIQUE KEY `uk_user_account_keycloak_subject` (`keycloak_subject`),
    UNIQUE KEY `uk_user_account_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci