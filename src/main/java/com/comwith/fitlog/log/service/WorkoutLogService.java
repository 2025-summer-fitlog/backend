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
import org.springframework.web.multipart.MultipartFile;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutLogService {
    private final WorkoutLogRepository workoutLogRepository;
    private final FileStorageService fileStorageService;

    // 기록 + 사진 동시 업로드 로직 새롭게 추가
    @Transactional
    public WorkoutLogResponse createLogWithPhotos(WorkoutLogRequest request, List<MultipartFile> photos) {
        // 중복 날짜 검증
        if (workoutLogRepository.findByDate(request.getDate()).isPresent()) {
            throw new IllegalStateException("이미 해당 날짜에 운동기록이 존재합니다.");
        }

        // 사진 파일 업로드 처리
        List<String> photoUrls = new ArrayList<>();
        if (photos != null && !photos.isEmpty()) {
            photoUrls = photos.stream()
                    .map(fileStorageService::storeFile)
                    .collect(Collectors.toList());
        }

        // 기존 로직 + 사진 URL 함께 저장
        WorkoutLog log = WorkoutLog.builder()
                .date(request.getDate())
                .memo(request.getMemo())
                .photoUrls(photoUrls) // 여기서 한 번에 저장
                .build();

        // WorkoutItem 생성 로직 (기존과 동일)
        List<WorkoutItem> items = request.getWorkouts().stream()
                .map(itemReq -> WorkoutItem.builder()
                        .workoutLog(log)
                        .workoutName(itemReq.getWorkoutName())
                        .status(itemReq.getStatus())
                        .score(itemReq.getStatus().getScore())
                        .build())
                .collect(Collectors.toList());

        log.setItems(items);
        WorkoutLog saved = workoutLogRepository.save(log);
        return convertToResponse(saved);
    }


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

    // 오늘 기록 조회 메서드
    @Transactional(readOnly = true)
    public Optional<WorkoutLogResponse> getTodayLog() {
        LocalDate today = LocalDate.now();
        Optional<WorkoutLog> log = workoutLogRepository.findByDate(today);
        return log.map(this::convertToResponse);
    }



    // 주간 기록 조회 메서드
    @Transactional(readOnly = true)
    public List<WorkoutLogResponse> getWeeklyLogs(LocalDate date) {
        // 해당 날짜가 포함된 주의 월요일~일요일 계산
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);

        return getLogsBetween(startOfWeek, endOfWeek);
    }

    // 월간 기록 조회 메서드
    @Transactional(readOnly = true)
    public List<WorkoutLogResponse> getMonthlyLogs(LocalDate date) {
        // 해당 날짜가 포함된 월의 1일~마지막일 계산
        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        return getLogsBetween(startOfMonth, endOfMonth);
    }




    private WorkoutLogResponse convertToResponse(WorkoutLog log) {
        // 트랜잭션 내에서 컬렉션 미리 초기화
        List<String> photoUrls = new ArrayList<>(log.getPhotoUrls());
        List<WorkoutItemResponse> workouts = log.getItems().stream()
                .map(item -> WorkoutItemResponse.builder()
                        .workoutName(item.getWorkoutName())
                        .status(item.getStatus())
                        .statusSymbol(item.getStatus().getSymbol())
                        .score(item.getScore())
                        .build())
                .collect(Collectors.toList());

        // 총점 계산
        Integer averageScore = null;
        if (!log.getItems().isEmpty()) {
            double avg = log.getItems().stream()
                    .mapToInt(item -> item.getStatus().getScore()) // 100, 50, 0
                    .average()
                    .orElse(0.0);
            averageScore = (int) Math.round(avg); // 정수형으로 반올림
        }

        return WorkoutLogResponse.builder()
                .id(log.getId())
                .date(log.getDate())
                .memo(log.getMemo())
                .photoUrls(photoUrls)
                .workouts(workouts)
                .averageScore(averageScore)
                .build();
    }

}
