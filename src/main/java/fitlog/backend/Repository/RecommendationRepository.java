package fitlog.backend.Repository;

import fitlog.backend.Entity.RecommendationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<RecommendationEntity, Long> {
    @Query("SELECT DISTINCT r FROM RecommendationEntity r JOIN r.keywords k WHERE k IN :keywords")
    List<RecommendationEntity> findTop3ByKeywordsIn(@Param("keywords") List<String> keywords, Pageable pageable);
}

