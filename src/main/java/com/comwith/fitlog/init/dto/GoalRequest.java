// src/main/java/com/comwith/fitlog/initialization/dto/GoalRequest.java
package com.comwith.fitlog.init.dto;

import jakarta.validation.constraints.NotBlank;

public class GoalRequest {
    @NotBlank(message = "운동 목적을 하나 이상 선택해주세요.")
    private String goals; // 운동 목적 (예: "REHABILITATION", "STRENGTH", "DIET", "VITALITY", "OTHER")

    // Getters and Setters
    public String getGoals() { return goals; }
    public void setGoals(String goals) { this.goals = goals; }
}