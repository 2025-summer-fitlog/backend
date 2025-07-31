package com.comwith.fitlog.init.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PhysicalInfoRequest {
    @NotNull(message = "키를 입력해주세요.")
    @Min(value = 50, message = "키는 50cm 이상이어야 합니다.")
    private Integer height; // 키 (cm)

    @NotNull(message = "몸무게를 입력해주세요.")
    @Min(value = 10, message = "몸무게는 10kg 이상이어야 합니다.")
    private Integer weight; // 몸무게 (kg)

    @NotNull(message = "연령을 입력해주세요.")
    @Min(value = 1, message = "연령은 1세 이상이어야 합니다.")
    private Integer age; // 연령

    @NotBlank(message = "성별을 입력해주세요.")
    private String gender; // 성별 (예: "MALE", "FEMALE", "OTHER")

    @NotBlank(message = "운동 경력을 입력해주세요.")
    private String workoutExperience; // 운동경력 (예: "BEGINNER", "INTERMEDIATE", "ADVANCED")

    // Getters and Setters
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getWorkoutExperience() { return workoutExperience; }
    public void setWorkoutExperience(String workoutExperience) { this.workoutExperience = workoutExperience; }
}