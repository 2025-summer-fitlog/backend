-- place 테이블에 데이터 삽입
INSERT INTO `place` (`id`, `place_name`) VALUES (1, '집');
INSERT INTO `place` (`id`, `place_name`) VALUES (2, '야외');
INSERT INTO `place` (`id`, `place_name`) VALUES (3, '헬스장');

-- exerciseinfo_entity 테이블에 데이터 삽입
-- place_id가 1인 운동 정보
INSERT INTO `exerciseinfo_entity` (`id`, `category`, `exercise_name`, `place_id`) VALUES (1, '홈트', '푸쉬업', 1);
INSERT INTO `exerciseinfo_entity` (`id`, `category`, `exercise_name`, `place_id`) VALUES (2, '홈트', '플랭크', 1);

-- place_id가 2인 운동 정보
INSERT INTO `exerciseinfo_entity` (`id`, `category`, `exercise_name`, `place_id`) VALUES (3, '야외', '런지', 2);
INSERT INTO `exerciseinfo_entity` (`id`, `category`, `exercise_name`, `place_id`) VALUES (4, '야외', '달리기', 2);

-- place_id가 3인 운동 정보
INSERT INTO `exerciseinfo_entity` (`id`, `category`, `exercise_name`, `place_id`) VALUES (5, '헬스', '벤치프레스', 3);
INSERT INTO `exerciseinfo_entity` (`id`, `category`, `exercise_name`, `place_id`) VALUES (6, '헬스', '스쿼트', 3);
