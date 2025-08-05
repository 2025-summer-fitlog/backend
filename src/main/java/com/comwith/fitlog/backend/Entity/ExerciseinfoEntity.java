package com.comwith.fitlog.backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

//운동정보
public class ExerciseinfoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // private String name; (exercise_name에서 name으로 수정)
    // sql dump 파일과 달라서 수정함.
    @Column(name = "name")
    private String name;

    //장소 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @JsonIgnore
    private PlaceEntity place;


    }
