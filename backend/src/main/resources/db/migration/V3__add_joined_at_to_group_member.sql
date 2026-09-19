ALTER TABLE `user_group_member`
    ADD COLUMN `joined_at` DATETIME(6) NULL;

UPDATE `user_group_member`
SET `joined_at` = CURRENT_TIMESTAMP(6)
WHERE `joined_at` IS NULL;

ALTER TABLE `user_group_member`
    MODIFY COLUMN `joined_at` DATETIME(6) NOT NULL;