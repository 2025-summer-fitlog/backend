package com.comwith.fitlog.log.dto;

import com.comwith.fitlog.log.entity.WorkoutStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class WorkoutLogResponse {
    private Long id;
    private LocalDate date;
    private String memo;
    private List<String> photoUrls;
    private List<WorkoutItemResponse> workouts;
}


