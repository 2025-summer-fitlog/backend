package com.comwith.fitlog.log.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 200)
    private String memo; // 하루 전체 메모

    @ElementCollection
    @CollectionTable(name = "workout_log_photos", joinColumns = @JoinColumn(name = "workout_log_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();

    @OneToMany(mappedBy = "workoutLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutItem> items = new ArrayList<>();
}
