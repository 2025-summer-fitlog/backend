package com.comwith.fitlog.log.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIFeedbackResponse {
    private LocalDate date;
    private Integer averageScore;
    private String feedback;
    private String grade;
}
