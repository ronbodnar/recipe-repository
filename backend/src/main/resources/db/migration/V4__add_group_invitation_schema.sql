CREATE TABLE `user_group_invitation`
(
    `id`                 BINARY(16) NOT NULL,
    `group_id`           BIGINT NOT NULL,
    `invited_user_id`    BIGINT NOT NULL,
    `invited_by_user_id` BIGINT NOT NULL,
    `invite_code`        BINARY(16) NOT NULL,
    `status`             ENUM('PENDING','ACCEPTED','DECLINED') NOT NULL,
    `invite_date`        DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_group_invitation_group_id` FOREIGN KEY (`group_id`) REFERENCES `user_group` (`id`),
    CONSTRAINT `fk_group_invitation_invited_user_id` FOREIGN KEY (`invited_user_id`) REFERENCES `user_account` (`id`),
    CONSTRAINT `fk_group_invitation_invited_by_user_id` FOREIGN KEY (`invited_by_user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;