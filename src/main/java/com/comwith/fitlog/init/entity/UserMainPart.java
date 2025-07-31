// 희망하는 운동 주요 부위 관련 엔티티
package com.comwith.fitlog.init.entity;

import com.comwith.fitlog.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_main_part")
@Getter @Setter
@NoArgsConstructor
public class UserMainPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 한 사용자가 여러 주요 부위를 가질 수 있으므로 ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String partType; // 주요 부위 (예: UPPER_BODY, LOWER_BODY, CORE, FULL_BODY)

    public UserMainPart(User user, String partType) {
        this.user = user;
        this.partType = partType;
    }
}