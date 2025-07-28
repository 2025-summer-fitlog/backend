// 사용자의 신체정보 관련 엔티티
package com.comwith.fitlog.init.entity;

import com.comwith.fitlog.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_physical_info")
@Getter @Setter
@NoArgsConstructor
public class UserPhysicalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // User 엔티티와 1:1 관계 설정
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer height;

    @Column(nullable = false)
    private Integer weight;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String gender; // ENUM으로 변경 고려 (MALE, FEMALE, OTHER)

    @Column(nullable = false)
    private String workoutExperience; // ENUM으로 변경 고려 (BEGINNER, INTERMEDIATE, ADVANCED)

    public UserPhysicalInfo(User user, Integer height, Integer weight, Integer age, String gender, String workoutExperience) {
        this.user = user;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.gender = gender;
        this.workoutExperience = workoutExperience;
    }
}