package com.comwith.fitlog.init.entity;

import com.comwith.fitlog.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_workout_frequency")
@Getter @Setter
@NoArgsConstructor
public class UserWorkoutFrequency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String frequency; // 운동 횟수 (예: 1회, 2~3회, 4~5회, 6회이상)

    public UserWorkoutFrequency(User user, String frequency) {
        this.user = user;
        this.frequency = frequency;
    }
}