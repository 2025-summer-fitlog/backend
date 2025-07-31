// src/main/java/com/comwith/fitlog/initialization/dto/GoalRequest.java
package com.comwith.fitlog.init.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class GoalRequest {
    @NotEmpty(message = "운동 목적을 하나 이상 선택해주세요.")
    private List<String> goals; // 운동 목적 (예: "REHABILITATION", "STRENGTH", "DIET", "VITALITY", "OTHER")

    // Getters and Setters
    public List<String> getGoals() { return goals; }
    public void setGoals(List<String> goals) { this.goals = goals; }
}