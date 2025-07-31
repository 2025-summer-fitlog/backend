package com.comwith.fitlog.init.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class MainPartRequest {
    @NotEmpty(message = "주요 운동 부위를 하나 이상 선택해주세요.")
    private List<String> mainParts; // 주요 부위 (예: "UPPER_BODY", "LOWER_BODY", "CORE", "FULL_BODY")

    // Getters and Setters
    public List<String> getMainParts() { return mainParts; }
    public void setMainParts(List<String> mainParts) { this.mainParts = mainParts; }
}