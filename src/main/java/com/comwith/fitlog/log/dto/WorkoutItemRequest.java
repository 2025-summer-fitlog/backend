package com.comwith.fitlog.log.dto;

import com.comwith.fitlog.log.entity.WorkoutStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkoutItemRequest {
    @NotBlank
    @Size(max = 100)
    private String workoutName;

    @NotNull
    private WorkoutStatus status;
}
