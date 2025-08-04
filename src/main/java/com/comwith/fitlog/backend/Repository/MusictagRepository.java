package com.comwith.fitlog.backend.Repository;

import com.comwith.fitlog.backend.Entity.MusictagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusictagRepository extends JpaRepository<MusictagEntity,Long> {}