package com.kbsw.seasonthon.favorite.controller;

import com.kbsw.seasonthon.favorite.dto.request.FavoriteCreateRequest;
import com.kbsw.seasonthon.favorite.dto.response.FavoriteListResponse;
import com.kbsw.seasonthon.favorite.dto.response.FavoriteResponse;
import com.kbsw.seasonthon.favorite.service.FavoriteService;
import com.kbsw.seasonthon.global.base.response.ResponseUtil;
import com.kbsw.seasonthon.global.base.response.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Favorite", description = "즐겨찾기 API")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    @Operation(summary = "즐겨찾기 생성", description = "새로운 즐겨찾기를 생성합니다.")
    public ResponseEntity<ResponseBody<FavoriteResponse>> createFavorite(
            @Valid @RequestBody FavoriteCreateRequest request) {
        
        log.info("즐겨찾기 생성 API 호출 - userId: {}, name: {}", request.getUserId(), request.getName());
        
        FavoriteResponse response = favoriteService.createFavorite(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.createSuccessResponse(response));
    }

    @GetMapping
    @Operation(summary = "즐겨찾기 목록 조회", description = "사용자의 즐겨찾기 목록을 조회합니다.")
    public ResponseEntity<ResponseBody<List<FavoriteListResponse>>> getFavorites(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam("userId") Long userId) {
        
        log.info("즐겨찾기 목록 조회 API 호출 - userId: {}", userId);
        
        List<FavoriteListResponse> response = favoriteService.getFavoritesByUserId(userId);
        
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "즐겨찾기 상세 조회", description = "특정 즐겨찾기의 상세 정보를 조회합니다.")
    public ResponseEntity<ResponseBody<FavoriteResponse>> getFavoriteDetail(
            @Parameter(description = "즐겨찾기 ID", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam("userId") Long userId) {
        
        log.info("즐겨찾기 상세 조회 API 호출 - favoriteId: {}, userId: {}", id, userId);
        
        FavoriteResponse response = favoriteService.getFavoriteDetail(id, userId);
        
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "즐겨찾기 삭제", description = "특정 즐겨찾기를 삭제합니다.")
    public ResponseEntity<Void> deleteFavorite(
            @Parameter(description = "즐겨찾기 ID", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam("userId") Long userId) {
        
        log.info("즐겨찾기 삭제 API 호출 - favoriteId: {}, userId: {}", id, userId);
        
        favoriteService.deleteFavorite(id, userId);
        
        return ResponseEntity.noContent().build();
    }
}
