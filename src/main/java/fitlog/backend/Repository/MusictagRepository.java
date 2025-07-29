package fitlog.backend.Repository;

import fitlog.backend.Entity.MusictagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusictagRepository extends JpaRepository<MusictagEntity,Long> {}