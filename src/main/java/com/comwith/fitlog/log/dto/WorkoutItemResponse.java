package com.comwith.fitlog.log.dto;

import com.comwith.fitlog.log.entity.WorkoutStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkoutItemResponse {
    private String workoutName;
    private WorkoutStatus status;
    private String statusSymbol;
}
