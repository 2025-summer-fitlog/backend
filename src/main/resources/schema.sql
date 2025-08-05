SET FOREIGN_KEY_CHECKS = 0;

-- 자식 테이블 먼저 DROP
--DROP TABLE IF EXISTS user_saved_videos;
--DROP TABLE IF EXISTS user_watched_videos;
--DROP TABLE IF EXISTS exercise_keyword;

-- 그런 다음 중간 부모들
--DROP TABLE IF EXISTS recommendation_entity;
--DROP TABLE IF EXISTS exerciseinfo_entity;
--DROP TABLE IF EXISTS music_entity;
--DROP TABLE IF EXISTS user_entity;

-- 마지막으로 최상위 부모
--DROP TABLE IF EXISTS social_type;
--DROP TABLE IF EXISTS musictag_entity;
--DROP TABLE IF EXISTS place;

-- 1. 기본 테이블 (가장 독립적)
CREATE TABLE IF NOT EXISTS `place` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS`musictag_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `social_type` (
  `social_type_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `social_type_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`social_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `recommendation_keywords` (
  `recommendation_id` bigint(20) NOT NULL,
  `keyword` varchar(255) DEFAULT NULL,
  KEY `FKfbl540uuityxctt1a420yrg2b` (`recommendation_id`),
  CONSTRAINT `FKfbl540uuityxctt1a420yrg2b` FOREIGN KEY (`recommendation_id`) REFERENCES `recommendation_entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- 2. 중간 테이블 (위의 테이블을 FK로 가짐)
CREATE TABLE IF NOT EXISTS `user_entity` (
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

CREATE TABLE IF NOT EXISTS `exerciseinfo_entity` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `place_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkennppgjcdn2qr8v3xu1dcw8w` (`place_id`),
  CONSTRAINT `FKkennppgjcdn2qr8v3xu1dcw8w` FOREIGN KEY (`place_id`) REFERENCES `place` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `music_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `tag_id` bigint(20) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKahc3cw0boj3abvkkpxplt8d53` (`tag_id`),
  CONSTRAINT `FKahc3cw0boj3abvkkpxplt8d53` FOREIGN KEY (`tag_id`) REFERENCES `musictag_entity` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `recommendation_entity` (
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

-- 4. 사용자 활동 관련 연결 테이블
CREATE TABLE IF NOT EXISTS `user_watched_videos` (
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

CREATE TABLE IF NOT EXISTS `user_saved_videos` (
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

SET FOREIGN_KEY_CHECKS = 1;