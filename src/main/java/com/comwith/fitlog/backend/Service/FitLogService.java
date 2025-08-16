package com.comwith.fitlog.backend.Service;

import com.comwith.fitlog.backend.Entity.*;
import com.comwith.fitlog.backend.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FitLogService {
    private final RecommendationRepository recommendationRepository;
    // private final UserRepository userRepository;
    @Qualifier("backendUserRepository")
    private final com.comwith.fitlog.backend.Repository.UserRepository userRepository;
    private final MusicRepository musicRepository;
    private final MusictagRepository musictagRepository;
    private final PlaceRepository placeRepository;
    private final ExerciseinfoRepository exerciseinfoRepository;

    // 전체 장소 조회
    public List<PlaceEntity> getAllPlaces(){
        return placeRepository.findAll();
    }

    // 장소별 운동 정보 조회
    public List<ExerciseinfoEntity> getExercisesByPlaceId(Long placeId) {
        return exerciseinfoRepository.findByPlaceId(placeId);
    }

    // 키워드 기반 운동 영상 추천 (최대 3개)
    public List<RecommendationEntity> recommendVideosByKeywords(List<String> keywords) {
        return recommendationRepository.findTop3ByKeywordsIn(keywords, PageRequest.of(0, 3));
    }

    // 특정 운동 추천 영상 조회
    public Optional<RecommendationEntity> getRecommendationById(Long id){
        return recommendationRepository.findById(id);
    }

    // 사용자 저장 영상 추가 (중복 저장 방지)
    public void saveUserRecommendation(Long userId, Long recommendationId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId: " + userId));

        RecommendationEntity recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new IllegalArgumentException("추천 영상을 찾을 수 없습니다. recommendationId: " + recommendationId));

        if (!user.getSavedVideos().contains(recommendation)) {
            user.getSavedVideos().add(recommendation);
        }
        userRepository.save(user);
    }

    // 사용자 저장 영상 전체 조회 (사용자 없으면 예외 발생)
    @Transactional(readOnly = true)
    public List<RecommendationEntity> getUserSavedRecommendations(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId: " + userId));
        return user.getSavedVideos();
    }

    // 사용자 저장 영상 중 특정 type 필터링 조회
    @Transactional(readOnly = true)
    public List<RecommendationEntity> getUserSavedVideosByType(Long userId, String type) {
        List<RecommendationEntity> savedVideos = getUserSavedRecommendations(userId);
        return savedVideos.stream()
                .filter(video -> video.getType() != null && video.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    // 사용자 저장 영상 type별 그룹핑 조회
    @Transactional(readOnly = true)
    public Map<String, List<RecommendationEntity>> getUserSavedVideosGroupedByType(Long userId) {
        List<RecommendationEntity> videos = getUserSavedRecommendations(userId);
        return videos.stream()
                .collect(Collectors.groupingBy(RecommendationEntity::getType));
    }

    // 태그 기반 랜덤 음악 추천
    @Transactional(readOnly = true)
    public MusicEntity getRandomSongByTag(Long tagId) {
        List<MusicEntity> songs = musicRepository.findByTag_Id(tagId);
        if (songs.isEmpty()) return null;
        int idx = new Random().nextInt(songs.size());
        return songs.get(idx);
    }

    // 특정 장소 조회
    public Optional<PlaceEntity> getPlaceById(Long placeId) {
        return placeRepository.findById(placeId);
    }
}