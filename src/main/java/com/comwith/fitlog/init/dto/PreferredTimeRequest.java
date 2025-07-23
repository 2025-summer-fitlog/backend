package com.comwith.fitlog.init.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PreferredTimeRequest {
    @NotNull(message = "희망 운동 시간을 입력해주세요.")
    @Min(value = 0, message = "시간은 0 이상이어야 합니다.")
    private Integer hours; // 시간

    @NotNull(message = "희망 운동 분을 입력해주세요.")
    @Min(value = 0, message = "분은 0 이상이어야 합니다.")
    private Integer minutes; // 분

    // Getters and Setters
    public Integer getHours() { return hours; }
    public void setHours(Integer hours) { this.hours = hours; }
    public Integer getMinutes() { return minutes; }
    public void setMinutes(Integer minutes) { this.minutes = minutes; }
}