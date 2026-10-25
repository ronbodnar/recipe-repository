CREATE TABLE `registration_invitation`
(
    `id` BIGINT AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL,
    `registration_code` BINARY(16) NOT NULL,
    `status` ENUM('PENDING','CLAIMED') NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;