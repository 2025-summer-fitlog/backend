package com.comwith.fitlog.log.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workout_records")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 100)
    private String workoutName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkoutStatus status;  // ← WorkoutStatus enum 사용

    @Column(nullable = false)
    private Integer score;

    @Column(length = 200)
    private String memo;

    @ElementCollection
    @CollectionTable(name = "workout_photos",
            joinColumns = @JoinColumn(name = "workout_record_id"))
    @Column(name = "photo_url")
    @Builder.Default
    private List<String> photoUrls = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    // 생성자, getter, setter 메서드들
    public WorkoutRecord(LocalDate date, String workoutName, WorkoutStatus status, String memo) {
        this.date = date;
        this.workoutName = workoutName;
        this.status = status;
        this.score = status.getScore(); // enum에서 점수 자동 설정
        this.memo = memo;
        this.photoUrls = new ArrayList<>();
    }

    // 사진 추가 메서드
    public void addPhotoUrl(String photoUrl) {
        if (this.photoUrls.size() >= 5) {
            throw new IllegalStateException("사진은 최대 5장까지 업로드 가능합니다.");
        }
        this.photoUrls.add(photoUrl);
    }

    // Getter, Setter 메서드들
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getWorkoutName() { return workoutName; }
    public void setWorkoutName(String workoutName) { this.workoutName = workoutName; }

    public WorkoutStatus getStatus() { return status; }
    public void setStatus(WorkoutStatus status) {
        this.status = status;
        this.score = status.getScore(); // 상태 변경 시 점수도 자동 업데이트
    }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

