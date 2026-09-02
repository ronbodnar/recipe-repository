--
-- Table structure for table `recipe`
--
CREATE TABLE `recipe` (
    `id` binary(16) NOT NULL,
    `author_id` binary(16) DEFAULT NULL,
    `created_at` datetime(6) NOT NULL,
    `description` varchar(255) NOT NULL,
    `title` varchar(255) NOT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `recipe_course`
--
CREATE TABLE `recipe_course` (
    `recipe_id` binary(16) NOT NULL,
    `course` enum('APPETIZER','BEVERAGE','DESSERT','MAIN_COURSE','SIDE_DISH') DEFAULT NULL,
    UNIQUE KEY `uk_recipe_course_recipe_id_course` (`recipe_id`,`course`),
    CONSTRAINT `fk_recipe_course_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `recipe_cuisine`
--
CREATE TABLE `recipe_cuisine` (
    `recipe_id` binary(16) NOT NULL,
    `cuisine` enum('AMERICAN','CHINESE','FRENCH','GERMAN','GREEK','INDIAN','ITALIAN','JAPANESE','KOREAN','MEDITERRANEAN','MEXICAN','SALVADORAN','SPANISH') DEFAULT NULL,
    UNIQUE KEY `uk_recipe_cuisine_recipe_id_cuisine` (`recipe_id`,`cuisine`),
    CONSTRAINT `fk_recipe_cuisine_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `recipe_diet_type`
--
CREATE TABLE `recipe_diet_type` (
    `recipe_id` binary(16) NOT NULL,
    `diet_type` enum('DAIRY_FREE','GLUTEN_FREE','KETO','LOW_CARB','VEGAN','VEGETARIAN') DEFAULT NULL,
    UNIQUE KEY `uk_recipe_diet_type_recipe_id_diet_type` (`recipe_id`,`diet_type`),
    CONSTRAINT `fk_recipe_diet_type_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `recipe_meal_type`
--
CREATE TABLE `recipe_meal_type` (
    `recipe_id` binary(16) NOT NULL,
    `meal_type` enum('BREAKFAST','BRUNCH','DINNER','LUNCH','SNACK') DEFAULT NULL,
    UNIQUE KEY `uk_recipe_meal_type_recipe_id_meal_type` (`recipe_id`,`meal_type`),
    CONSTRAINT `fk_recipe_meal_type_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `recipe_image`
--
CREATE TABLE `recipe_image` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `recipe_id` binary(16) NOT NULL,
    `image_id` binary(16) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_recipe_image_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `recipe_variant`
--
CREATE TABLE `recipe_variant` (
    `id` binary(16) NOT NULL,
    `name` VARCHAR(255) DEFAULT NULL,
    `cook_time` int DEFAULT NULL,
    `cooking_method` enum('AIR_FRYER','BARBECUE','GRILL','MICROWAVE','NO_COOK','OVEN','PRESSURE_COOKER','SLOW_COOKER','SMOKER','STOVETOP') NOT NULL,
    `prep_time` int DEFAULT NULL,
    `num_servings` int DEFAULT NULL,
    `total_time` int DEFAULT NULL,
    `recipe_id` binary(16) DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_recipe_variant_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `recipe_variant_ingredient`
--
CREATE TABLE `recipe_variant_ingredient` (
    `recipe_variant_id` BINARY(16) NOT NULL,
    `display_order` INT NOT NULL,
    `text` TEXT NOT NULL,
    PRIMARY KEY (`recipe_variant_id`, `display_order`),
    CONSTRAINT `fk_recipe_variant_ingredient_variant`
    FOREIGN KEY (`recipe_variant_id`)
    REFERENCES `recipe_variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `recipe_variant_instruction`
--
CREATE TABLE `recipe_variant_instruction` (
    `recipe_variant_id` BINARY(16) NOT NULL,
    `display_order` INT NOT NULL,
    `text` TEXT NOT NULL,
    PRIMARY KEY (`recipe_variant_id`, `display_order`),
    CONSTRAINT `fk_recipe_variant_instruction_variant`
    FOREIGN KEY (`recipe_variant_id`)
    REFERENCES `recipe_variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `recipe_variant_note`
--
CREATE TABLE `recipe_variant_note` (
    `recipe_variant_id` BINARY(16) NOT NULL,
    `display_order` INT NOT NULL,
    `text` TEXT NOT NULL,
    PRIMARY KEY (`recipe_variant_id`, `display_order`),
    CONSTRAINT `fk_recipe_variant_note_variant`
    FOREIGN KEY (`recipe_variant_id`)
    REFERENCES `recipe_variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;