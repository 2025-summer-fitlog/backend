package fitlog.backend.Controller;

import fitlog.backend.Entity.ExerciseinfoEntity;
import fitlog.backend.Entity.MusicEntity;
import fitlog.backend.Entity.PlaceEntity;
import fitlog.backend.Entity.RecommendationEntity;
import fitlog.backend.Service.FitLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fitlog")
@RequiredArgsConstructor

public class FitLogController {

    private final FitLogService fitLogService;

    //장소
    @GetMapping("/places")
    public List<PlaceEntity> getPlaces() {
        return fitLogService.getAllPlaces();
    }

    //운동정보
    @GetMapping ("/exercises")
    public List<ExerciseinfoEntity> getExercises(@RequestParam Long placeId) {
        return fitLogService.getExercisesByPlace(placeId);
    }

    //운동 추천 영상
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
                                                        @RequestParam Long recommendationId) {
        fitLogService.saveUserRecommendation(userId, recommendationId);
        return ResponseEntity.ok("추천 영상 저장 완료!");
    }

    //사용자 저장 영상 목록 조회
    @GetMapping("/users/{userId}/saved-videos")
    public ResponseEntity<List<RecommendationEntity>> getUserSavedVideos(@PathVariable Long userId){
        return ResponseEntity.ok(fitLogService.getUserSavedVideos(userId));
    }

    @GetMapping("/tags/{tagId}/music")
    public MusicEntity getRandomMusicByTag(@PathVariable Long tagId){
        return fitLogService.getRandomSongByTag(tagId);
    }

}
