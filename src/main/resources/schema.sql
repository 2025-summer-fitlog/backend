DROP TABLE IF EXISTS `user_saved_videos`;
DROP TABLE IF EXISTS `user_watched_videos`;
DROP TABLE IF EXISTS `user_entity`;
DROP TABLE IF EXISTS `social_type`;
DROP TABLE IF EXISTS `recommendation_entity`;
DROP TABLE IF EXISTS `place`;
DROP TABLE IF EXISTS `musictag_entity`;
DROP TABLE IF EXISTS `music_entity`;
DROP TABLE IF EXISTS `exercise_keyword`;
DROP TABLE IF EXISTS `exerciseinfo_entity`;


-- exerciseinfo_entity (부모 테이블)
CREATE TABLE `exerciseinfo_entity` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `category` varchar(255) DEFAULT NULL,
                                       `exercise_name` varchar(255) DEFAULT NULL,
                                       `place_id` bigint(20) DEFAULT NULL,
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- exercise_keyword (자식 테이블)
CREATE TABLE `exercise_keyword` (
                                    `exercise_id` bigint(20) NOT NULL,
                                    `keyword` varchar(255) DEFAULT NULL,
                                    KEY `FKoly5txirpjufnkmruqvqrttsi` (`exercise_id`),
                                    CONSTRAINT `FKoly5txirpjufnkmruqvqrttsi` FOREIGN KEY (`exercise_id`) REFERENCES `exerciseinfo_entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- music_entity
CREATE TABLE `music_entity` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `music_path` varchar(255) DEFAULT NULL,
                                `music_tag_id` bigint(20) DEFAULT NULL,
                                `music_title` varchar(255) DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- musictag_entity
CREATE TABLE `musictag_entity` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                   `tag_name` varchar(255) DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- place
CREATE TABLE `place` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `place_name` varchar(255) DEFAULT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- recommendation_entity
CREATE TABLE `recommendation_entity` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `caution` varchar(255) DEFAULT NULL,
                                         `effect` varchar(255) DEFAULT NULL,
                                         `equipment` varchar(255) DEFAULT NULL,
                                         `name` varchar(255) DEFAULT NULL,
                                         `target` varchar(255) DEFAULT NULL,
                                         `thumbnail` varchar(255) DEFAULT NULL,
                                         `tip` varchar(255) DEFAULT NULL,
                                         `warmup` varchar(255) DEFAULT NULL,
                                         `workout_program` varchar(255) DEFAULT NULL,
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- social_type
CREATE TABLE `social_type` (
                               `social_type_id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `social_type_name` varchar(255) DEFAULT NULL,
                               PRIMARY KEY (`social_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- user_entity
CREATE TABLE `user_entity` (
                               `user_id` varchar(255) NOT NULL,
                               `email` varchar(255) DEFAULT NULL,
                               `join_date` datetime(6) DEFAULT NULL,
                               `nickname` varchar(255) DEFAULT NULL,
                               `profile_image_url` varchar(255) DEFAULT NULL,
                               `role` varchar(255) DEFAULT NULL,
                               `social_type_id` bigint(20) DEFAULT NULL,
                               PRIMARY KEY (`user_id`),
                               KEY `FKq0a92d4k48w8018h2n3m29u56` (`social_type_id`),
                               CONSTRAINT `FKq0a92d4k48w8018h2n3m29u56` FOREIGN KEY (`social_type_id`) REFERENCES `social_type` (`social_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- user_watched_videos
CREATE TABLE `user_watched_videos` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `watched_at` datetime(6) DEFAULT NULL,
                                       `recommendation_id` bigint(20) DEFAULT NULL,
                                       `user_id` varchar(255) DEFAULT NULL,
                                       PRIMARY KEY (`id`),
                                       KEY `FKhq2558w96y8j965t0x3p5j5w` (`recommendation_id`),
                                       KEY `FKk1057k1g50s5d3h3s5h83g0b` (`user_id`),
                                       CONSTRAINT `FKhq2558w96y8j965t0x3p5j5w` FOREIGN KEY (`recommendation_id`) REFERENCES `recommendation_entity` (`id`),
                                       CONSTRAINT `FKk1057k1g50s5d3h3s5h83g0b` FOREIGN KEY (`user_id`) REFERENCES `user_entity` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- user_saved_videos
CREATE TABLE `user_saved_videos` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                     `saved_at` datetime(6) DEFAULT NULL,
                                     `recommendation_id` bigint(20) DEFAULT NULL,
                                     `user_id` varchar(255) DEFAULT NULL,
                                     PRIMARY KEY (`id`),
                                     KEY `FKi2560i2g56c80c2f4r6p8l9e` (`recommendation_id`),
                                     KEY `FKs5769o9s54j4r4h2t1h2v3w1` (`user_id`),
                                     CONSTRAINT `FKi2560i2g56c80c2f4r6p8l9e` FOREIGN KEY (`recommendation_id`) REFERENCES `recommendation_entity` (`id`),
                                     CONSTRAINT `FKs5769o9s54j4r4h2t1h2v3w1` FOREIGN KEY (`user_id`) REFERENCES `user_entity` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;