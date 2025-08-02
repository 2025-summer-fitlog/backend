package fitlog.backend.Service;

import fitlog.backend.Entity.*;
import fitlog.backend.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class FitLogService {
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final MusicRepository musicRepository;
    private final MusictagRepository musictagRepository;
    private final PlaceRepository placeRepository;
    private final ExerciseinfoRepository exerciseinfoRepository;

    // 장소 추천
    public List<PlaceEntity> getAllPlaces(){
        return placeRepository.findAll();
    }

    //장소 선택 후 운동 정보 선택
    public List<ExerciseinfoEntity> getExercisesByPlace(Long placeId) {
        return exerciseinfoRepository.findByPlaceId(placeId);
    }

    //운동 영상 선택(3개정도)
    public List<RecommendationEntity> recommendVideosByKeywords(List<String> keywords) {
        return recommendationRepository.
                findTop3ByKeywordsIn(keywords, PageRequest.of(0, 3));
    }

    //운동추천 영상 전체 조회
    public List<RecommendationEntity> getRecommendations(){
        return recommendationRepository.findAll();
    }

    //특정 운동추천 영상 조회
    public Optional<RecommendationEntity> getRecommendationById(Long id){
        return recommendationRepository.findById(id);
    }

    //사용자 저장 영상 목록 조회
    public List<RecommendationEntity> getUserSavedVideos(Long userId){
        return userRepository.findById(userId)
                .map(UserEntity::getSavedVideos)
                .orElse(List.of());
    }

    //사용자 저장 영상 추가
    public void saveUserRecommendation(Long userId, Long recommendationId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId: " + userId));

        RecommendationEntity recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new IllegalArgumentException("추천 영상을 찾을 수 없습니다. recommendationId: " + recommendationId));

        // 이미 저장된 영상인지 확인
        if (!user.getSavedVideos().contains(recommendation)) {
            user.getSavedVideos().add(recommendation);
        }
        userRepository.save(user);
    }

    @Transactional(readOnly = true) // 읽기 전용
    public List<RecommendationEntity> getUserSavedRecommendations(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId: " + userId));
        return user.getSavedVideos();
    }

    @Transactional(readOnly = true)
    public MusicEntity getRandomSongByTag(Long tagId) {
        List<MusicEntity> songs = MusicRepository.findByMusictagEntityId(tagId);
        if (songs.isEmpty()) return null;
        int idx = new Random().nextInt(songs.size());
        return songs.get(idx);
    }

    public List<RecommendationEntity> getUserSavedVideosByType(Long userId, String type) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId: " + userId));
        return user.getSavedVideos().stream()
                .filter(video -> type.equals(video.getType()))
                .toList();
    }

    public List<ExerciseinfoEntity> getExercisesByPlaceId(Long placeId) {
        return exerciseinfoRepository.findByPlaceId(placeId);
    }

    public Optional<PlaceEntity> getPlaceById(Long placeId) {
        return placeRepository.findById(placeId);
    }

}