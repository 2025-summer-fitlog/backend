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
    private String warmupText; //준비 운동

    @Lob //주의사항
    private String cautionText;

    @Lob //보조기구 추천
    private String equipmentText;

    @Lob //운동 효과
    private String effectText;

    //ex.하체,상체
private  String bodyPart;
}
