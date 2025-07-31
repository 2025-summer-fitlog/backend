package com.comwith.fitlog.log.controller;

import com.comwith.fitlog.log.dto.*;
import com.comwith.fitlog.log.service.FileStorageService;
import com.comwith.fitlog.log.service.WorkoutLogService;
import com.comwith.fitlog.log.service.WorkoutRecordService;
import com.comwith.fitlog.log.service.AIFeedbackService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/*
@RestController
@RequestMapping("/api/log")
@Validated
@CrossOrigin(origins = "*") // 프론트엔드 연동용
public class WorkoutLogController {

    private final WorkoutRecordService workoutRecordService;
    private final AIFeedbackService aiFeedbackService;

    // 생성자
    public WorkoutLogController(WorkoutRecordService workoutRecordService,
                                AIFeedbackService aiFeedbackService) {
        this.workoutRecordService = workoutRecordService;
        this.aiFeedbackService = aiFeedbackService;
    }

    // 1. 운동 기록 생성
    @PostMapping("/records")
    public ResponseEntity<WorkoutRecordResponse> createRecord(
            @Valid @RequestBody WorkoutRecordRequest request) {
        WorkoutRecordResponse response = workoutRecordService.createWorkoutRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. 운동 기록 수정
    @PutMapping("/records/{recordId}")
    public ResponseEntity<WorkoutRecordResponse> updateRecord(
            @PathVariable Long recordId,
            @Valid @RequestBody WorkoutRecordRequest request) {
        WorkoutRecordResponse response = workoutRecordService.updateWorkoutRecord(recordId, request);
        return ResponseEntity.ok(response);
    }

    // 3. 운동 기록 삭제
    @DeleteMapping("/records/{recordId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long recordId) {
        workoutRecordService.deleteWorkoutRecord(recordId);
        return ResponseEntity.noContent().build();
    }

    // 4. 일간 기록 조회
    @GetMapping("/daily")
    public ResponseEntity<WorkoutStatsResponse> getDailyStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        WorkoutStatsResponse response = workoutRecordService.getDailyStats(date);
        return ResponseEntity.ok(response);
    }

    // 5. 주간 기록 조회 (차트용)
    @GetMapping("/weekly")
    public ResponseEntity<List<WorkoutStatsResponse>> getWeeklyStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<WorkoutStatsResponse> response = workoutRecordService.getWeeklyStats(date);
        return ResponseEntity.ok(response);
    }

    // 6. 월간 기록 조회 (히트맵용)
    @GetMapping("/monthly")
    public ResponseEntity<List<WorkoutStatsResponse>> getMonthlyStats(
            @RequestParam int year, @RequestParam int month) {
        List<WorkoutStatsResponse> response = workoutRecordService.getMonthlyStats(year, month);
        return ResponseEntity.ok(response);
    }

    // 7. 사진 업로드 (최대 5장)
    @PostMapping("/records/{recordId}/photos")
    public ResponseEntity<WorkoutRecordResponse> uploadPhotos(
            @PathVariable Long recordId,
            @RequestParam("files") MultipartFile[] files) {
        WorkoutRecordResponse response = workoutRecordService.uploadPhotos(recordId, files);
        return ResponseEntity.ok(response);
    }

    // 8. AI 피드백 생성 (당일 기준)
    @PostMapping("/feedback")
    public ResponseEntity<AIFeedbackResponse> generateFeedback(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AIFeedbackResponse response = aiFeedbackService.generateDailyFeedback(date);
        return ResponseEntity.ok(response);
    }

    // 9. 전체 기록 삭제
    @DeleteMapping("/records/all")
    public ResponseEntity<Void> clearAllRecords() {
        workoutRecordService.clearAllRecords();
        return ResponseEntity.noContent().build();
    }
}

 */

@RestController
@RequestMapping("/api/log/daily")
@RequiredArgsConstructor
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;
    private final AIFeedbackService aiFeedbackService;
    private final FileStorageService fileStorageService;

    // 1) 날짜별 운동기록 묶음 생성
    @PostMapping
    public ResponseEntity<WorkoutLogResponse> createLog(@RequestBody WorkoutLogRequest request) {
        WorkoutLogResponse response = workoutLogService.createLog(request);
        return ResponseEntity.ok(response);
    }

    // 2) 날짜별 운동기록 묶음 수정
    @PutMapping("/{logId}")
    public ResponseEntity<WorkoutLogResponse> updateLog(@PathVariable Long logId, @RequestBody WorkoutLogRequest request) {
        WorkoutLogResponse response = workoutLogService.updateLog(logId, request);
        return ResponseEntity.ok(response);
    }

    // 3) 날짜별 운동기록 묶음 조회 (쿼리 파라미터로 날짜 지정)
    @GetMapping
    public ResponseEntity<WorkoutLogResponse> getDailyLog(@RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        WorkoutLogResponse response = workoutLogService.getDailyLog(date);
        return ResponseEntity.ok(response);
    }

    // 4) 일간 ~ 월간 운동기록 범위 조회
    @GetMapping("/range")
    public ResponseEntity<List<WorkoutLogResponse>> getLogsInRange(
            @RequestParam("start") String startDateStr,
            @RequestParam("end") String endDateStr) {
        LocalDate start = LocalDate.parse(startDateStr);
        LocalDate end = LocalDate.parse(endDateStr);
        List<WorkoutLogResponse> responses = workoutLogService.getLogsBetween(start, end);
        return ResponseEntity.ok(responses);
    }

    // 5) 운동기록 묶음에 사진 추가 (파일 업로드)
    @PostMapping("/{logId}/photos")
    public ResponseEntity<WorkoutLogResponse> addPhotos(
            @PathVariable Long logId,
            @RequestParam("files") List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        // 실제 파일 저장 처리: 기존 FileStorageService 활용
        List<String> uploadedUrls = files.stream()
                .map(fileStorageService::storeFile)
                .collect(Collectors.toList());
        WorkoutLogResponse response = workoutLogService.addPhotos(logId, uploadedUrls);
        return ResponseEntity.ok(response);
    }


    // 6) 날짜별 운동기록 묶음 삭제
    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long logId) {
        workoutLogService.removeLog(logId);
        return ResponseEntity.noContent().build();
    }

    // 7) AI 피드백 요청 (일간)
    @GetMapping("/feedback/daily")
    public ResponseEntity<AIFeedbackResponse> getDailyFeedback(@RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        AIFeedbackResponse feedback = aiFeedbackService.generateDailyFeedback(date);
        return ResponseEntity.ok(feedback);
    }

    // 8) AI 피드백 요청 (주간, 월간 등 필요 시 추가 가능)
    // 예:
    // @GetMapping("/feedback/range")
    // public ResponseEntity<AIFeedbackResponse> getRangeFeedback(@RequestParam("start") String startStr, @RequestParam("end") String endStr) { ... }
}