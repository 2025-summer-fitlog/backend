package com.comwith.fitlog.log.dto;

import com.comwith.fitlog.log.entity.WorkoutRecord;
import com.comwith.fitlog.log.entity.WorkoutStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRecordResponse {
    private Long id;
    private LocalDate date;
    private String workoutName;
    private WorkoutStatus status;
    private String statusSymbol;  // O, △, X
    private Integer score;
    private String memo;
    private List<String> photoUrls;
    private LocalDateTime createdAt;

    public static WorkoutRecordResponse from(WorkoutRecord entity) {
        return WorkoutRecordResponse.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .workoutName(entity.getWorkoutName())
                .status(entity.getStatus())
                .statusSymbol(entity.getStatus().getSymbol())
                .score(entity.getScore())
                .memo(entity.getMemo())
                .photoUrls(entity.getPhotoUrls())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
