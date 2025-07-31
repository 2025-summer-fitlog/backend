package com.comwith.fitlog.log.dto;

import com.comwith.fitlog.log.entity.WorkoutStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRecordRequest {
    @NotNull(message = "날짜는 필수 입력값입니다.")
    private LocalDate date;

    @NotBlank(message = "운동명은 필수 입력값입니다.")
    @Size(max = 100, message = "운동명은 100자를 초과할 수 없습니다.")
    private String workoutName;

    @NotNull(message = "수행 상태는 필수 입력값입니다.")
    private WorkoutStatus status;

    @Size(max = 200, message = "메모는 200자를 초과할 수 없습니다.")
    private String memo;
}
