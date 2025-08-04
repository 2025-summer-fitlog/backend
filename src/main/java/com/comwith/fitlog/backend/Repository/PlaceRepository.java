package com.comwith.fitlog.backend.Repository;

import com.comwith.fitlog.backend.Entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity,Long> {}
