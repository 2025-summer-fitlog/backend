package fitlog.backend.Repository;

import fitlog.backend.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {}

