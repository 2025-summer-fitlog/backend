package fitlog.backend.Entity;

import jakarta.persistence.*;
import lombok.*;

//징소 선택(집,야외,헬스장)
@Entity
@Table(name = "place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}