DROP TABLE IF EXISTS `exercise_keyword`;

DROP TABLE IF EXISTS `exerciseinfo_entity`;

DROP TABLE IF EXISTS `music_entity`;

DROP TABLE IF EXISTS `musictag_entity`;

DROP TABLE IF EXISTS `place`;

DROP TABLE IF EXISTS `place_entity`;

DROP TABLE IF EXISTS `recommendation_entity`;

DROP TABLE IF EXISTS `recommendation_keywords`;

DROP TABLE IF EXISTS `recommendations`;

DROP TABLE IF EXISTS `user_entity`;

DROP TABLE IF EXISTS `user_saved_videos`;



-- `exercise_keyword`
CREATE TABLE `exercise_keyword` (
                                    `exercise_id` bigint(20) NOT NULL,
                                    `keyword` varchar(255) DEFAULT NULL,
                                    KEY `FKoly5txirpjufnkmruqvqrttsi` (`exercise_id`),
                                    CONSTRAINT `FKoly5txirpjufnkmruqvqrttsi` FOREIGN KEY (`exercise_id`) REFERENCES `exerciseinfo_entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- `exerciseinfo_entity`
CREATE TABLE `exerciseinfo_entity` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `name` varchar(255) DEFAULT NULL,
                                       `place_id` bigint(20) DEFAULT NULL,
                                       PRIMARY KEY (`id`),
                                       KEY `FKkennppgjcdn2qr8v3xu1dcw8w` (`place_id`),
                                       CONSTRAINT `FKkennppgjcdn2qr8v3xu1dcw8w` FOREIGN KEY (`place_id`) REFERENCES `place` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- `music_entity`
CREATE TABLE `music_entity` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `title` varchar(255) DEFAULT NULL,
                                `tag_id` bigint(20) DEFAULT NULL,
                                `url` varchar(255) DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `FKahc3cw0boj3abvkkpxplt8d53` (`tag_id`),
                                CONSTRAINT `FKahc3cw0boj3abvkkpxplt8d53` FOREIGN KEY (`tag_id`) REFERENCES `musictag_entity` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- `musictag_entity`
CREATE TABLE `musictag_entity` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                   `name` varchar(255) DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- `place`
CREATE TABLE `place` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `name` varchar(255) DEFAULT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- `place_entity`
CREATE TABLE `place_entity` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `name` varchar(255) DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- `recommendation_entity`
CREATE TABLE `recommendation_entity` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `description` varchar(255) DEFAULT NULL,
                                         `effect` longtext DEFAULT NULL,
                                         `equipment` longtext DEFAULT NULL,
                                         `precautions` longtext DEFAULT NULL,
                                         `preparation` longtext DEFAULT NULL,
                                         `title` varchar(255) DEFAULT NULL,
                                         `type` varchar(255) DEFAULT NULL,
                                         `url` varchar(255) DEFAULT NULL,
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- `recommendation_keywords`
CREATE TABLE `recommendation_keywords` (
                                           `recommendation_id` bigint(20) NOT NULL,
                                           `keyword` varchar(255) DEFAULT NULL,
                                           KEY `FKfbl540uuityxctt1a420yrg2b` (`recommendation_id`),
                                           CONSTRAINT `FKfbl540uuityxctt1a420yrg2b` FOREIGN KEY (`recommendation_id`) REFERENCES `recommendation_entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- `recommendations`
CREATE TABLE `recommendations` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                   `effect` tinytext DEFAULT NULL,
                                   `equipment` tinytext DEFAULT NULL,
                                   `precautions` tinytext DEFAULT NULL,
                                   `preparation` tinytext DEFAULT NULL,
                                   `title` varchar(255) DEFAULT NULL,
                                   `type` varchar(255) DEFAULT NULL,
                                   `url` varchar(255) DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- `user_entity`
CREATE TABLE `user_entity` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `email` varchar(255) DEFAULT NULL,
                               `password` varchar(255) DEFAULT NULL,
                               `username` varchar(255) DEFAULT NULL,
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- `user_saved_videos`
CREATE TABLE `user_saved_videos` (
                                     `user_id` bigint(20) NOT NULL,
                                     `recommendation_id` bigint(20) NOT NULL,
                                     KEY `FKmayrq051s0j1tmbg5i110b7pu` (`recommendation_id`),
                                     KEY `FKr00dv657l8fy8hyy55dohfl23` (`user_id`),
                                     CONSTRAINT `FKmayrq051s0j1tmbg5i110b7pu` FOREIGN KEY (`recommendation_id`) REFERENCES `recommendation_entity` (`id`),
                                     CONSTRAINT `FKr00dv657l8fy8hyy55dohfl23` FOREIGN KEY (`user_id`) REFERENCES `user_entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;