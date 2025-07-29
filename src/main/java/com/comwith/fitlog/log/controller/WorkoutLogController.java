package com.comwith.fitlog.log.controller;

import com.comwith.fitlog.log.dto.WorkoutRecordRequest;
import com.comwith.fitlog.log.dto.WorkoutRecordResponse;
import com.comwith.fitlog.log.dto.WorkoutStatsResponse;
import com.comwith.fitlog.log.dto.AIFeedbackResponse;
import com.comwith.fitlog.log.service.WorkoutRecordService;
import com.comwith.fitlog.log.service.AIFeedbackService;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/log")
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
