package com.comwith.fitlog.log.service;


import com.comwith.fitlog.log.dto.*;
import com.comwith.fitlog.log.entity.WorkoutItem;
import com.comwith.fitlog.log.entity.WorkoutLog;
import com.comwith.fitlog.log.entity.WorkoutStatus;
import com.comwith.fitlog.log.repository.WorkoutLogRepository;
import com.comwith.fitlog.log.dto.WorkoutStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutLogService {
    private final WorkoutLogRepository workoutLogRepository;

    @Transactional(readOnly = true)
    public WorkoutStatsResponse getDailyStats(LocalDate date) {
        Optional<WorkoutLog> logOpt = workoutLogRepository.findByDate(date);
        if (logOpt.isEmpty()) {
            return WorkoutStatsResponse.builder()
                    .date(date)
                    .averageScore(0)
                    .totalWorkouts(0)
                    .records(new ArrayList<>()) // 빈 리스트라도 넣어줌
                    .build();
        }
        WorkoutLog log = logOpt.get();
        int totalWorkouts = log.getItems().size();
        int averageScore = (int) log.getItems().stream()
                .mapToInt(WorkoutItem::getScore)
                .average()
                .orElse(0);

        return WorkoutStatsResponse.builder()
                .date(date)
                .averageScore(averageScore)
                .totalWorkouts(totalWorkouts)
                .records(new ArrayList<>()) // 필요에 따라 실제 운동 기록 리스트로 교체
                .build();
    }


    @Transactional
    public WorkoutLogResponse createLog(WorkoutLogRequest request) {
        // 같은 날짜에는 하나의 운동 묶음만 등록 가능 (운동 개수는 제한 x)
        if (workoutLogRepository.findByDate(request.getDate()).isPresent()) {
            throw new IllegalStateException("이미 해당 날짜에 운동기록이 존재합니다.");
        }

        // 입력 유효성 검사 (ex. 사진 5장 이하)
        if (request.getPhotoUrls() != null && request.getPhotoUrls().size() > 5) {
            throw new IllegalArgumentException("사진은 최대 5장까지 업로드할 수 있습니다.");
        }
        WorkoutLog log = WorkoutLog.builder()
                .date(request.getDate())
                .memo(request.getMemo())
                .photoUrls(request.getPhotoUrls())
                .build();

        // WorkoutItem 생성 및 설정
        List<WorkoutItem> items = request.getWorkouts().stream()
                .map(itemReq -> WorkoutItem.builder()
                        .workoutLog(log) // 연관관계 주입
                        .workoutName(itemReq.getWorkoutName())
                        .status(itemReq.getStatus())
                        .score(itemReq.getStatus().getScore())
                        .build())
                .collect(Collectors.toList());
        log.setItems(items);

        WorkoutLog saved = workoutLogRepository.save(log);
        return convertToResponse(saved);
    }

    @Transactional
    public WorkoutLogResponse updateLog(Long logId, WorkoutLogRequest request) {
        WorkoutLog log = workoutLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("운동기록이 존재하지 않습니다."));

        if (request.getPhotoUrls() != null && request.getPhotoUrls().size() > 5) {
            throw new IllegalArgumentException("사진은 최대 5장까지 업로드할 수 있습니다.");
        }
        log.setDate(request.getDate());
        log.setMemo(request.getMemo());
        log.setPhotoUrls(request.getPhotoUrls());

        // 기존 운동내역 삭제 후 새로 생성
        log.getItems().clear();
        List<WorkoutItem> newItems = request.getWorkouts().stream()
                .map(itemReq -> WorkoutItem.builder()
                        .workoutLog(log)
                        .workoutName(itemReq.getWorkoutName())
                        .status(itemReq.getStatus())
                        .score(itemReq.getStatus().getScore())
                        .build())
                .collect(Collectors.toList());
        log.getItems().addAll(newItems);

        return convertToResponse(log);
    }

    @Transactional
    public void removeLog(Long logId) {
        workoutLogRepository.deleteById(logId);
    }

    @Transactional
    public WorkoutLogResponse getDailyLog(LocalDate date) {
        WorkoutLog log = workoutLogRepository.findByDate(date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 기록이 없습니다."));
        return convertToResponse(log);
    }

    @Transactional
    public WorkoutLogResponse addPhotos(Long logId, List<String> uploadedUrls) {
        WorkoutLog log = workoutLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("운동기록이 존재하지 않습니다."));
        if (log.getPhotoUrls().size() + uploadedUrls.size() > 5) {
            throw new IllegalArgumentException("사진은 최대 5장까지 업로드할 수 있습니다.");
        }
        log.getPhotoUrls().addAll(uploadedUrls);
        return convertToResponse(log);
    }


    @Transactional
    public List<WorkoutLogResponse> getLogsBetween(LocalDate startDate, LocalDate endDate) {
        List<WorkoutLog> logs = workoutLogRepository.findByDateBetweenOrderByDateAsc(startDate, endDate);
        return logs.stream().map(this::convertToResponse).collect(Collectors.toList());
    }



    // 엔티티 → DTO 변환 메서드
    /*private WorkoutLogResponse convertToResponse(WorkoutLog log) {
        return WorkoutLogResponse.builder()
                .id(log.getId())
                .date(log.getDate())
                .memo(log.getMemo())
                .photoUrls(log.getPhotoUrls())
                .workouts(
                        log.getItems().stream()
                                .map(item ->
                                        WorkoutItemResponse.builder()
                                                .workoutName(item.getWorkoutName())
                                                .status(item.getStatus())
                                                .statusSymbol(item.getStatus().getSymbol())
                                                .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }
     */
    private WorkoutLogResponse convertToResponse(WorkoutLog log) {
        // 트랜잭션 내에서 컬렉션 미리 초기화
        List<String> photoUrls = new ArrayList<>(log.getPhotoUrls());
        List<WorkoutItemResponse> workouts = log.getItems().stream()
                .map(item -> WorkoutItemResponse.builder()
                        .workoutName(item.getWorkoutName())
                        .status(item.getStatus())
                        .statusSymbol(item.getStatus().getSymbol())
                        .build())
                .collect(Collectors.toList());

        return WorkoutLogResponse.builder()
                .id(log.getId())
                .date(log.getDate())
                .memo(log.getMemo())
                .photoUrls(photoUrls)
                .workouts(workouts)
                .build();
    }

}
