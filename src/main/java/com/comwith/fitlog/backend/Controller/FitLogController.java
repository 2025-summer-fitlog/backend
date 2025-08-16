package com.comwith.fitlog.backend.Controller;

import com.comwith.fitlog.backend.Entity.ExerciseinfoEntity;
import com.comwith.fitlog.backend.Entity.MusicEntity;
import com.comwith.fitlog.backend.Entity.PlaceEntity;
import com.comwith.fitlog.backend.Entity.RecommendationEntity;
import com.comwith.fitlog.backend.Repository.ExerciseinfoRepository;
import com.comwith.fitlog.backend.Service.FitLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fitlog")
@RequiredArgsConstructor

public class FitLogController {

    @Autowired
    private ExerciseinfoRepository exerciseinfoRepository;

    private final FitLogService fitLogService;

    //장소
    @GetMapping("/places")
    public List<PlaceEntity> getPlaces() {
        return fitLogService.getAllPlaces();
    }

    @GetMapping("/places/{placeId}")
    public ResponseEntity<PlaceEntity> getPlaceById(@PathVariable Long placeId) {
        return fitLogService.getPlaceById(placeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 장소에 따른 운동 정보
    @GetMapping("/exercises/{placeId}")
    public ResponseEntity<List<ExerciseinfoEntity>> getExercisesByPlace(@PathVariable Long placeId) {
        List<ExerciseinfoEntity> exercises = fitLogService.getExercisesByPlaceId(placeId);
        if (exercises.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exercises);
    }

    //운동 추천 영상, recommendations?keywords=달리기
    @GetMapping("/recommendations")
    public List<RecommendationEntity> getRecommendedVideos(@RequestParam List<String> keywords) {
        return fitLogService.recommendVideosByKeywords(keywords);
    }

    // 특정 추천 영상 상세 조회
    @GetMapping("/recommendations/{id}")
    public ResponseEntity<RecommendationEntity> getRecommendation(@PathVariable Long id){
        return fitLogService.getRecommendationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //사용자 저장 영상 추가
    @PostMapping("/users/{userId}/saved-video/{recommendationId}")
    public ResponseEntity<String> saveUserRecommendation(@PathVariable Long userId,
                                                         @PathVariable Long recommendationId) {
        fitLogService.saveUserRecommendation(userId, recommendationId);
        return ResponseEntity.ok("추천 영상 저장 완료!");
    }

    //사용자 저장 영상 목록 조회
    @GetMapping("/users/{userId}/saved-videos-by-type")
    public ResponseEntity<List<RecommendationEntity>> getUserSavedVideosByType(
            @PathVariable Long userId,
            @RequestParam String type) {
        List<RecommendationEntity> videos = fitLogService.getUserSavedVideosByType(userId, type);
        return ResponseEntity.ok(videos);
    }


    //음악 태그
    @GetMapping("/tags/{tagId}/music")
    public MusicEntity getRandomMusicByTag(@PathVariable Long tagId){
        return fitLogService.getRandomSongByTag(tagId);
    }

}