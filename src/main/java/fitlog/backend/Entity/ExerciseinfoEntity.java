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

public class ExerciseinfoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private PlaceEntity place;

    @ElementCollection
    @CollectionTable(name = "exercise_keywords", joinColumns = @JoinColumn(name="exercise_id"))
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();
}
