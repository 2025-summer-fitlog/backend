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

    // private String name;
    @Column(name = "exercise_name")
    private String exerciseName;

    //장소 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @JsonIgnore
    private PlaceEntity place;

    }
