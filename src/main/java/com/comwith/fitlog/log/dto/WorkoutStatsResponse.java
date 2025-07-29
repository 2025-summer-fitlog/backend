package com.comwith.fitlog.log.dto;

import com.comwith.fitlog.log.entity.WorkoutRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutStatsResponse {
    private LocalDate date;
    private Integer averageScore;
    private Integer totalWorkouts;
    private List<WorkoutRecordResponse> records;
}
