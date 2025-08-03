package com.comwith.fitlog.log.service;

import com.comwith.fitlog.log.dto.AIFeedbackResponse;
import com.comwith.fitlog.log.dto.WorkoutStatsResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AIFeedbackService {

//    private final WorkoutRecordService workoutRecordService;
//
//    public AIFeedbackService(WorkoutRecordService workoutRecordService) {
//        this.workoutRecordService = workoutRecordService;
//    }

    private final WorkoutLogService workoutLogService;

    public AIFeedbackService(WorkoutLogService workoutLogService) {
        this.workoutLogService = workoutLogService;
    }

    public AIFeedbackResponse generateDailyFeedback(LocalDate date) {
        WorkoutStatsResponse stats = workoutLogService.getDailyStats(date);

        if (stats.getTotalWorkouts() == 0) {
            return AIFeedbackResponse.builder()
                    .date(date)
                    .averageScore(0)
                    .feedback("운동 기록이 없습니다.")
                    .grade("🤔")
                    .build();
        }

        int averageScore = stats.getAverageScore();
        String feedback = generateFeedbackMessage(averageScore);
        String grade = generateGrade(averageScore);

        return AIFeedbackResponse.builder()
                .date(date)
                .averageScore(averageScore)
                .feedback(feedback)
                .grade(grade)
                .build();
    }

    private String generateFeedbackMessage(int averageScore) {
        if (averageScore >= 90) {
            return "대단해요! 꾸준한 운동 습관이 형성되고 있어요. 지금 페이스를 유지해보세요!";
        } else if (averageScore >= 75) {
            return "좋은 흐름이에요! 약간의 아쉬운 날도 있지만 전체적으로 훌륭해요. 조금만 더 집중해봐요!";
        } else if (averageScore >= 60) {
            return "중간은 했어요! 그래도 개선 여지가 보여요. 실패한 날을 돌아보고 다음엔 성공해봐요.";
        } else if (averageScore >= 40) {
            return "아쉬운 한 주였어요. △를 O로 바꾸기 위한 전략을 세워보면 좋을 것 같아요.";
        } else if (averageScore >= 20) {
            return "약간 흔들렸네요. 쉬어가는 것도 좋지만 다시 루틴을 재정비해봐요. 짧은 목표부터 시작해도 좋아요!";
        } else {
            return "거의 운동을 하지 못했어요. 무리한 계획은 아니었나요? 작게라도 실천할 수 있는 계획을 세워보는 건 어때요?";
        }
    }

    private String generateGrade(int averageScore) {
        if (averageScore >= 90) return "매우 우수";
        else if (averageScore >= 75) return "우수";
        else if (averageScore >= 60) return "보통";
        else if (averageScore >= 40) return "부족";
        else if (averageScore >= 20) return "매우 부족";
        else return "미수행";
    }
}
