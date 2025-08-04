package com.comwith.fitlog.backend.Repository;

import com.comwith.fitlog.backend.Entity.MusicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<MusicEntity, Long> {
    static List<MusicEntity> findByMusictagEntityId(Long tagId) {
        return null;
    }

    List<MusicEntity> findByTag_Id(Long tagId);
}
