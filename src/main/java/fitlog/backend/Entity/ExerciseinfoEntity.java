package fitlog.backend.Entity;

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
    private String name;

    //장소 연결
    @ManyToOne(fetch = FetchType.LAZY)
    private PlaceEntity place;


}
