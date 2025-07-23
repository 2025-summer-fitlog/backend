package com.comwith.fitlog.init.entity;

import com.comwith.fitlog.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_preferred_time")
@Getter @Setter
@NoArgsConstructor
public class UserPreferredTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer hours;

    @Column(nullable = false)
    private Integer minutes;

    public UserPreferredTime(User user, Integer hours, Integer minutes) {
        this.user = user;
        this.hours = hours;
        this.minutes = minutes;
    }
}