--
-- Table structures for 'recipe' module
--
CREATE TABLE `recipe` (
    `id` BINARY(16) NOT NULL,
    `author_subject` VARCHAR(255) NOT NULL,
    `title` VARCHAR(50) NOT NULL,
    `description` VARCHAR(500) DEFAULT NULL,
    `source` VARCHAR(255) DEFAULT NULL,
    `visibility` ENUM(
        'PUBLIC','GROUP','PRIVATE'
    ) NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `chk_recipe_title_length` CHECK (CHAR_LENGTH(`title`) >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `recipe_course` (
    `recipe_id` BINARY(16) NOT NULL,
    `course` ENUM(
        'APPETIZER','BEVERAGE','DESSERT','MAIN_COURSE','SIDE_DISH'
    ) DEFAULT NULL,
    UNIQUE KEY `uk_recipe_course_recipe_id_course` (`recipe_id`,`course`),
    CONSTRAINT `fk_recipe_course_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `recipe_cuisine` (
    `recipe_id` BINARY(16) NOT NULL,
    `cuisine` ENUM(
        'AMERICAN','CHINESE','FRENCH','GERMAN','GREEK','INDIAN',
        'ITALIAN','JAPANESE','KOREAN','MEDITERRANEAN','MEXICAN',
        'SALVADORAN','SPANISH'
    ) DEFAULT NULL,
    UNIQUE KEY `uk_recipe_cuisine_recipe_id_cuisine` (`recipe_id`,`cuisine`),
    CONSTRAINT `fk_recipe_cuisine_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `recipe_diet_type` (
    `recipe_id` BINARY(16) NOT NULL,
    `diet_type` ENUM(
        'DAIRY_FREE','GLUTEN_FREE','KETO','LOW_CARB','VEGAN','VEGETARIAN'
    ) DEFAULT NULL,
    UNIQUE KEY `uk_recipe_diet_type_recipe_id_diet_type` (`recipe_id`,`diet_type`),
    CONSTRAINT `fk_recipe_diet_type_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `recipe_meal_type` (
    `recipe_id` BINARY(16) NOT NULL,
    `meal_type` ENUM(
        'BREAKFAST','BRUNCH','DINNER','LUNCH','SNACK'
    ) DEFAULT NULL,
    UNIQUE KEY `uk_recipe_meal_type_recipe_id_meal_type` (`recipe_id`,`meal_type`),
    CONSTRAINT `fk_recipe_meal_type_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `recipe_image` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `recipe_id` BINARY(16) NOT NULL,
    `image_id` BINARY(16) NOT NULL,
    `display_order` INT NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_recipe_image_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `recipe_variant` (
    `id` BINARY(16) NOT NULL,
    `name` VARCHAR(50) DEFAULT NULL,
    `cook_time` INT DEFAULT NULL,
    `cooking_method` ENUM(
        'AIR_FRYER','BARBECUE','GRILL','MICROWAVE','NO_COOK','OVEN',
        'PRESSURE_COOKER','SLOW_COOKER','SMOKER','STOVETOP'
    ) NOT NULL,
    `prep_time` INT DEFAULT NULL,
    `yield` VARCHAR(50) DEFAULT NULL,
    `total_time` INT DEFAULT NULL,
    `recipe_id` BINARY(16) NOT NULL,
    `display_order` INT DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_recipe_variant_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`),
    CONSTRAINT `chk_recipe_variant_name_length` CHECK (CHAR_LENGTH(`name`) >= 0),
    CONSTRAINT `chk_recipe_variant_yield_length` CHECK (CHAR_LENGTH(`yield`) >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `recipe_variant_ingredient` (
    `recipe_variant_id` BINARY(16) NOT NULL,
    `text` TEXT NOT NULL,
    `display_order` INT NOT NULL,
    PRIMARY KEY (`recipe_variant_id`, `display_order`),
    CONSTRAINT `fk_recipe_variant_ingredient_variant` FOREIGN KEY (`recipe_variant_id`) REFERENCES `recipe_variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `recipe_variant_instruction` (
    `recipe_variant_id` BINARY(16) NOT NULL,
    `text` TEXT NOT NULL,
    `display_order` INT NOT NULL,
    PRIMARY KEY (`recipe_variant_id`, `display_order`),
    CONSTRAINT `fk_recipe_variant_instruction_variant` FOREIGN KEY (`recipe_variant_id`) REFERENCES `recipe_variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `recipe_variant_note` (
    `recipe_variant_id` BINARY(16) NOT NULL,
    `text` TEXT NOT NULL,
    `display_order` INT NOT NULL,
    PRIMARY KEY (`recipe_variant_id`, `display_order`),
    CONSTRAINT `fk_recipe_variant_note_variant` FOREIGN KEY (`recipe_variant_id`) REFERENCES `recipe_variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for 'image' module
--
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

--
-- Table structure for 'identity' module
--
CREATE TABLE `role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(30) NOT NULL,
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_role` (

)

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