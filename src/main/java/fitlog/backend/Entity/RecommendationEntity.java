package fitlog.backend.Entity;

import java.util.List;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "recommendations")


public class RecommendationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String title;
    private String videoUrl;

    @ElementCollection
    @CollectionTable(name = "recommendation_keywords",
            joinColumns = @JoinColumn(name = "recommendation_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    @Lob
    private String warmupText;

    @Lob
    private String cautionText;

    @Lob
    private String equipmentText;

    @Lob
    private String effectText;

private  String bodyPart;
}
