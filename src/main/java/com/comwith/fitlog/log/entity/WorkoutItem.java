package com.comwith.fitlog.log.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workout_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_log_id")
    private WorkoutLog workoutLog;

    @Column(nullable = false, length = 100)
    private String workoutName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutStatus status;

    @Column(nullable = false)
    private Integer score;
}
