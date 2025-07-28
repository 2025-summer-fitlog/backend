// 운동 목적 조회 엔티티
package com.comwith.fitlog.init.entity;

import com.comwith.fitlog.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_goal")
@Getter @Setter
@NoArgsConstructor
public class UserGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 한 사용자가 여러 목표를 가질 수 있으므로 ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String goalType; // 운동 목적 (예: REHABILITATION, STRENGTH, DIET, VITALITY, OTHER)

    public UserGoal(User user, String goalType) {
        this.user = user;
        this.goalType = goalType;
    }
}