package fitlog.backend.Repository;

import fitlog.backend.Entity.ExerciseinfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseinfoRepository extends JpaRepository<ExerciseinfoEntity,Long>{
    // 장소랑 연결
    List<ExerciseinfoEntity> findByPlaceEntityId(Long placeId);

    List<ExerciseinfoEntity> findByPlaceId(Long placeId);
}
