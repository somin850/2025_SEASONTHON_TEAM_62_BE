package com.kbsw.seasonthon.favorite.service;

import com.kbsw.seasonthon.favorite.dto.request.FavoriteCreateRequest;
import com.kbsw.seasonthon.favorite.dto.response.FavoriteListResponse;
import com.kbsw.seasonthon.favorite.dto.response.FavoriteResponse;
import com.kbsw.seasonthon.favorite.entity.Favorite;
import com.kbsw.seasonthon.favorite.repository.FavoriteRepository;
import com.kbsw.seasonthon.global.base.response.exception.BusinessException;
import com.kbsw.seasonthon.global.base.response.exception.ExceptionType;
import com.kbsw.seasonthon.user.entity.User;
import com.kbsw.seasonthon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    @Transactional
    public FavoriteResponse createFavorite(FavoriteCreateRequest request) {
        log.info("즐겨찾기 생성 요청 - userId: {}, name: {}", request.getUserId(), request.getName());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        // waypoints를 String 리스트로 변환
        List<String> waypointStrings = request.getWaypoints() != null 
            ? request.getWaypoints().stream()
                .map(point -> point.get(0) + "," + point.get(1))
                .collect(Collectors.toList())
            : List.of();

        Favorite favorite = Favorite.builder()
                .user(user)
                .name(request.getName())
                .waypoints(waypointStrings)
                .savedPolyline(request.getSavedPolyline())
                .distanceM(request.getDistanceM())
                .durationS(request.getDurationS())
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);
        log.info("즐겨찾기 생성 완료 - id: {}", savedFavorite.getId());

        return FavoriteResponse.from(savedFavorite);
    }

    public List<FavoriteListResponse> getFavoritesByUserId(Long userId) {
        log.info("즐겨찾기 목록 조회 요청 - userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        List<Favorite> favorites = favoriteRepository.findByUserOrderByCreatedAtDesc(user);
        
        return favorites.stream()
                .map(FavoriteListResponse::from)
                .collect(Collectors.toList());
    }

    public FavoriteResponse getFavoriteDetail(Long favoriteId, Long userId) {
        log.info("즐겨찾기 상세 조회 요청 - favoriteId: {}, userId: {}", favoriteId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        Favorite favorite = favoriteRepository.findByIdAndUser(favoriteId, user)
                .orElseThrow(() -> new BusinessException(ExceptionType.FAVORITE_NOT_FOUND));

        return FavoriteResponse.from(favorite);
    }

    @Transactional
    public void deleteFavorite(Long favoriteId, Long userId) {
        log.info("즐겨찾기 삭제 요청 - favoriteId: {}, userId: {}", favoriteId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        Favorite favorite = favoriteRepository.findByIdAndUser(favoriteId, user)
                .orElseThrow(() -> new BusinessException(ExceptionType.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(favorite);
        log.info("즐겨찾기 삭제 완료 - favoriteId: {}", favoriteId);
    }
}
