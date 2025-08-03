package com.comwith.fitlog.log.dto;

import com.comwith.fitlog.log.entity.WorkoutStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class WorkoutLogRequest {
    @NotNull
    private LocalDate date;

    @Size(max = 200)
    private String memo;

    private List<String> photoUrls; // 파일 업로드 후 URL 전달

    @NotNull @Size(min = 1)
    private List<WorkoutItemRequest> workouts;
}


