package com.comwith.fitlog.log.service;

import com.comwith.fitlog.log.dto.AIFeedbackResponse;
import com.comwith.fitlog.log.dto.WorkoutStatsResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AIFeedbackService {

    private final WorkoutRecordService workoutRecordService;

    public AIFeedbackService(WorkoutRecordService workoutRecordService) {
        this.workoutRecordService = workoutRecordService;
    }

    public AIFeedbackResponse generateDailyFeedback(LocalDate date) {
        WorkoutStatsResponse stats = workoutRecordService.getDailyStats(date);

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
        if (averageScore >= 80) {
            return "🎉 훌륭합니다! 거의 모든 운동을 완벽하게 수행하셨네요!";
        } else if (averageScore >= 60) {
            return "👍 좋은 성과입니다! 대부분의 운동을 잘 해내셨어요!";
        } else if (averageScore >= 40) {
            return "💪 괜찮은 시작이에요! 꾸준히 하다보면 더 좋은 결과가 있을 거예요!";
        } else if (averageScore >= 20) {
            return "🌱 시작이 반이에요! 조금씩이라도 꾸준히 하는 것이 중요합니다!";
        } else {
            return "🔥 괜찮아요, 누구나 힘든 날이 있어요!";
        }
    }

    private String generateGrade(int averageScore) {
        if (averageScore >= 80) return "🎉";
        else if (averageScore >= 60) return "👍";
        else if (averageScore >= 40) return "💪";
        else if (averageScore >= 20) return "🌱";
        else return "🔥";
    }
}
