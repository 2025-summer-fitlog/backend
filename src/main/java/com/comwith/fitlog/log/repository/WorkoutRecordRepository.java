package com.comwith.fitlog.log.repository;

import com.comwith.fitlog.log.entity.WorkoutRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface WorkoutRecordRepository extends JpaRepository<WorkoutRecord, Long> {

    // 특정 날짜의 운동 기록 조회
    List<WorkoutRecord> findByDateOrderByCreatedAtDesc(LocalDate date);

    // 특정 기간의 운동 기록 조회
    List<WorkoutRecord> findByDateBetweenOrderByDateDescCreatedAtDesc(
            LocalDate startDate, LocalDate endDate);

    // 날짜별 평균 점수 및 개수 조회 (차트용)
    @Query("SELECT new map(" +
            "w.date as date, " +
            "CAST(AVG(w.score) AS integer) as averageScore, " +
            "CAST(COUNT(w) AS integer) as totalWorkouts) " +
            "FROM WorkoutRecord w " +
            "WHERE w.date BETWEEN :startDate AND :endDate " +
            "GROUP BY w.date " +
            "ORDER BY w.date ASC")
    List<Map<String, Object>> findDailyStatsInPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
