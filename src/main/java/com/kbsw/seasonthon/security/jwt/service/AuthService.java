package com.kbsw.seasonthon.security.jwt.service;

import com.kbsw.seasonthon.global.base.response.exception.BusinessException;
import com.kbsw.seasonthon.global.base.response.exception.ExceptionType;
import com.kbsw.seasonthon.security.jwt.dto.request.UsernameLoginRequestDto;
import com.kbsw.seasonthon.security.jwt.dto.response.TokenResponseDto;
import com.kbsw.seasonthon.security.jwt.entity.RefreshToken;
import com.kbsw.seasonthon.security.jwt.repository.RefreshTokenRepository;
import com.kbsw.seasonthon.security.jwt.util.JwtTokenProvider;
import com.kbsw.seasonthon.user.entity.User;
import com.kbsw.seasonthon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인: 항상 정식 access/refresh 즉시 발급
     */
    public TokenResponseDto usernameLogin(UsernameLoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ExceptionType.PASSWORD_NOT_MATCHED);
        }

        // ✅ 임시 토큰 시나리오 제거: 항상 정식 쌍 발급
        return issueTokensFor(user, request.getDeviceId());
    }

    /**
     * 특정 유저 & 디바이스 기준 access/refresh (재)발급 + refresh 저장/치환
     */
    public TokenResponseDto issueTokensFor(User user, String deviceId) {
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        LocalDateTime expiresAt = jwtTokenProvider.extractExpiration(newRefreshToken);

        refreshTokenRepository.findByUserIdAndDeviceId(user.getId(), deviceId)
                .map(existing -> {
                    existing.update(newRefreshToken, expiresAt); // 더티체킹
                    return existing;
                })
                .orElseGet(() -> {
                    RefreshToken rt = RefreshToken.builder()
                            .token(newRefreshToken)
                            .user(user)
                            .expiresAt(expiresAt)
                            .deviceId(deviceId) // null 허용: null 키 1개만 유지됨
                            .build();
                    return refreshTokenRepository.save(rt);
                });

        return TokenResponseDto.builder()
                .access(newAccessToken)
                .refresh(newRefreshToken)
                .build();
    }

    /**
     * RTR: Authorization 헤더(=Bearer refresh) 기반 재발급
     */
    public TokenResponseDto reissueTokens(String bearerToken) {
        String refreshToken = com.kbsw.seasonthon.security.jwt.util.JwtTokenProvider.extractToken(bearerToken);
        return reissueTokensUsingRawRefresh(refreshToken);
    }

    /**
     * RTR: Raw refresh 문자열(쿠키 등) 기반 재발급
     */
    public TokenResponseDto reissueTokensUsingRawRefresh(String refreshToken) {
        RefreshToken token = validateAndGetRefreshToken(refreshToken);

        User user = token.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        LocalDateTime newExpiry = jwtTokenProvider.extractExpiration(newRefreshToken);

        token.update(newRefreshToken, newExpiry);
        refreshTokenRepository.save(token);

        return TokenResponseDto.builder()
                .access(newAccessToken)
                .refresh(newRefreshToken)
                .build();
    }

    /**
     * 단일 로그아웃
     */
    public void logout(String bearerToken) {
        String refreshToken = com.kbsw.seasonthon.security.jwt.util.JwtTokenProvider.extractToken(bearerToken);
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    /**
     * 전체 로그아웃
     */
    public void logoutAll(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));
        refreshTokenRepository.deleteAllByUserId(user.getId());
    }

    /**
     * refresh 유효성(서명/만료) + DB 존재 검증
     */
    private RefreshToken validateAndGetRefreshToken(String refreshToken) {
        if (!jwtTokenProvider.isValidToken(refreshToken)) {
            throw new BusinessException(ExceptionType.INVALID_REFRESH_TOKEN);
        }
        return refreshTokenRepository.findByToken(refreshToken)
                .filter(rt -> !rt.isExpired())
                .orElseThrow(() -> new BusinessException(ExceptionType.REFRESH_TOKEN_EXPIRED));
    }
}