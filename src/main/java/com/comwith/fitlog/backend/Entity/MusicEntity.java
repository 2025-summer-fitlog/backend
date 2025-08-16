package com.comwith.fitlog.backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MusicEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String url; // new - sql dump 파일에 있던 내용 추가

    //태그랑 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private MusictagEntity tag;

}
