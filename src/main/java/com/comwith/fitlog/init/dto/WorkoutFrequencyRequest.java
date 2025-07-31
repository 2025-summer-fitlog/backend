package com.comwith.fitlog.init.dto;

import jakarta.validation.constraints.NotBlank;

public class WorkoutFrequencyRequest {
    @NotBlank(message = "운동 횟수를 선택해주세요.")
    private String frequency; // 운동 횟수 (예: "1회", "2~3회", "4~5회", "6회이상")

    // Getters and Setters
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
}
