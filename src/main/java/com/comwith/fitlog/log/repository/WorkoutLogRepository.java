package com.comwith.fitlog.log.repository;

import com.comwith.fitlog.log.entity.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {

    // 날짜로 단일 운동기록 묶음 조회 (중복 데이터 없다고 가정)
    Optional<WorkoutLog> findByDate(LocalDate date);

    // 기간 내 날짜별 운동기록 묶음 리스트 조회
    List<WorkoutLog> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);


    // 날짜별 평균 점수 및 운동기록 수(운동 항목 단위) 통계 (예시)
    @Query("SELECT new map(" +
            "w.date as date, " +
            "CAST(AVG(i.score) AS integer) as averageScore, " +
            "CAST(COUNT(i) AS integer) as totalWorkouts) " +
            "FROM WorkoutLog w JOIN w.items i " +
            "WHERE w.date BETWEEN :startDate AND :endDate " +
            "GROUP BY w.date " +
            "ORDER BY w.date ASC")
    List<Map<String, Object>> findDailyStatsInPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
