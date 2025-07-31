package com.comwith.fitlog.log.service;

import com.comwith.fitlog.log.dto.WorkoutRecordRequest;
import com.comwith.fitlog.log.dto.WorkoutRecordResponse;
import com.comwith.fitlog.log.dto.WorkoutStatsResponse;
import com.comwith.fitlog.log.entity.WorkoutRecord;
import com.comwith.fitlog.log.repository.WorkoutRecordRepository;
// import com.comwith.fitlog.log.exception.ValidationException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkoutRecordService {

    private final WorkoutRecordRepository repository;
    private final FileStorageService fileStorageService;


    public WorkoutRecordService(WorkoutRecordRepository repository, FileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    // 1. 운동 기록 생성
    public WorkoutRecordResponse createWorkoutRecord(WorkoutRecordRequest request) {
        WorkoutRecord record = WorkoutRecord.builder()
                .date(request.getDate())
                .workoutName(request.getWorkoutName())
                .status(request.getStatus())
                .score(request.getStatus().getScore())
                .memo(request.getMemo())
                .photoUrls(new ArrayList<>())
                .build();

        WorkoutRecord saved = repository.save(record);
        return WorkoutRecordResponse.from(saved);
    }

    // 2. 운동 기록 수정
    public WorkoutRecordResponse updateWorkoutRecord(Long recordId, WorkoutRecordRequest request) {
        WorkoutRecord record = repository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("운동 기록을 찾을 수 없습니다."));

        record.setWorkoutName(request.getWorkoutName());
        record.setStatus(request.getStatus());
        record.setScore(request.getStatus().getScore());
        record.setMemo(request.getMemo());

        WorkoutRecord updated = repository.save(record);

        // Hibernate 지연 로딩(lazy loading) 초기화. / size() 호출은 프록시 깨우는 것.
        // 트랜잭션 내에서 미리 프록시를 실제 데이터로 바꿔놓으면, 세션 종료 후 안전한 접근 가능.
        updated.getPhotoUrls().size(); // lazy loading 트리거?

        return WorkoutRecordResponse.from(updated);
    }

    // 3. 일간 기록 조회
    public WorkoutStatsResponse getDailyStats(LocalDate date) {
        List<WorkoutRecord> records = repository.findByDateOrderByCreatedAtDesc(date);

        if (records.isEmpty()) {
            return WorkoutStatsResponse.builder()
                    .date(date)
                    .averageScore(0)
                    .totalWorkouts(0)
                    .records(new ArrayList<>())
                    .build();
        }

        // photoUrls 초기화 추가
        for (WorkoutRecord record : records) {
            record.getPhotoUrls().size(); // lazy loading 트리거
        }

        int totalScore = records.stream().mapToInt(WorkoutRecord::getScore).sum();
        int averageScore = Math.round((float) totalScore / records.size());

        List<WorkoutRecordResponse> recordResponses = records.stream()
                .map(WorkoutRecordResponse::from)
                .collect(Collectors.toList());

        return WorkoutStatsResponse.builder()
                .date(date)
                .averageScore(averageScore)
                .totalWorkouts(records.size())
                .records(recordResponses)
                .build();
    }

    // 4. 주간 기록 조회 (일요일 시작)
    public List<WorkoutStatsResponse> getWeeklyStats(LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<Map<String, Object>> dailyStats = repository.findDailyStatsInPeriod(startOfWeek, endOfWeek);

        return buildWeeklyResponse(startOfWeek, endOfWeek, dailyStats);
    }

    // 5. 월간 기록 조회
    public List<WorkoutStatsResponse> getMonthlyStats(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        List<Map<String, Object>> dailyStats = repository.findDailyStatsInPeriod(startOfMonth, endOfMonth);

        return buildMonthlyResponse(startOfMonth, endOfMonth, dailyStats);
    }

    // 6. 사진 업로드 (최대 5장)
    public WorkoutRecordResponse uploadPhotos(Long recordId, MultipartFile[] files) {
        if (files.length > 5) {
            throw new ValidationException("사진은 최대 5장까지 업로드 가능합니다.");
        }

        WorkoutRecord record = repository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("운동 기록을 찾을 수 없습니다."));

        // 기존 사진 + 새 사진이 5장 초과하는지 확인
        if (record.getPhotoUrls().size() + files.length > 5) {
            throw new ValidationException("총 사진 개수가 5장을 초과할 수 없습니다.");
        }

        for (MultipartFile file : files) {
            validateImageFile(file);
            String photoUrl = fileStorageService.storeFile(file);
            record.addPhotoUrl(photoUrl);
        }

        WorkoutRecord updated = repository.save(record);
        return WorkoutRecordResponse.from(updated);
    }

    // 7. 기록 삭제
    public void deleteWorkoutRecord(Long recordId) {
        WorkoutRecord record = repository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("운동 기록을 찾을 수 없습니다."));

        // 관련 사진 파일들도 삭제
        record.getPhotoUrls().forEach(fileStorageService::deleteFile);
        repository.delete(record);
    }

    // 8. 전체 기록 삭제
    public void clearAllRecords() {
        repository.deleteAll();
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException("파일이 비어있습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException("이미지 파일만 업로드 가능합니다.");
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new ValidationException("파일 크기는 10MB를 초과할 수 없습니다.");
        }
    }


    // buildWeeklyResponse 메서드 추가
    private List<WorkoutStatsResponse> buildWeeklyResponse(LocalDate startOfWeek,
                                                           LocalDate endOfWeek,
                                                           List<Map<String, Object>> dailyStats) {
        List<WorkoutStatsResponse> weeklyResponse = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startOfWeek.plusDays(i);

            // 해당 날짜의 통계 찾기
            Map<String, Object> dayStats = dailyStats.stream()
                    .filter(stats -> stats.get("date").equals(currentDate))
                    .findFirst()
                    .orElse(null);

            if (dayStats != null) {
                weeklyResponse.add(WorkoutStatsResponse.builder()
                        .date(currentDate)
                        .averageScore((Integer) dayStats.get("averageScore"))
                        .totalWorkouts((Integer) dayStats.get("totalWorkouts"))
                        .records(new ArrayList<>())  // 주간 조회에서는 상세 기록 제외
                        .build());
            } else {
                weeklyResponse.add(WorkoutStatsResponse.builder()
                        .date(currentDate)
                        .averageScore(0)
                        .totalWorkouts(0)
                        .records(new ArrayList<>())
                        .build());
            }
        }

        return weeklyResponse;
    }

    // buildMonthlyResponse 메서드 추가
    private List<WorkoutStatsResponse> buildMonthlyResponse(LocalDate startOfMonth,
                                                            LocalDate endOfMonth,
                                                            List<Map<String, Object>> dailyStats) {
        List<WorkoutStatsResponse> monthlyResponse = new ArrayList<>();

        LocalDate currentDate = startOfMonth;
        while (!currentDate.isAfter(endOfMonth)) {
            final LocalDate finalCurrentDate = currentDate;

            // 해당 날짜의 통계 찾기
            Map<String, Object> dayStats = dailyStats.stream()
                    .filter(stats -> stats.get("date").equals(finalCurrentDate))
                    .findFirst()
                    .orElse(null);

            if (dayStats != null) {
                monthlyResponse.add(WorkoutStatsResponse.builder()
                        .date(finalCurrentDate)
                        .averageScore((Integer) dayStats.get("averageScore"))
                        .totalWorkouts((Integer) dayStats.get("totalWorkouts"))
                        .records(new ArrayList<>())  // 월간 조회에서는 상세 기록 제외
                        .build());
            } else {
                monthlyResponse.add(WorkoutStatsResponse.builder()
                        .date(finalCurrentDate)
                        .averageScore(0)
                        .totalWorkouts(0)
                        .records(new ArrayList<>())
                        .build());
            }

            currentDate = currentDate.plusDays(1);
        }

        return monthlyResponse;
    }

}
