package com.kbsw.seasonthon.security.jwt.controller;

import com.kbsw.seasonthon.global.base.response.ResponseBody;
import com.kbsw.seasonthon.global.base.response.ResponseUtil;
import com.kbsw.seasonthon.global.base.response.exception.BusinessException;
import com.kbsw.seasonthon.global.base.response.exception.ExceptionType;
import com.kbsw.seasonthon.global.config.CookieUtils;
import com.kbsw.seasonthon.security.jwt.dto.request.UsernameLoginRequestDto;
import com.kbsw.seasonthon.security.jwt.dto.response.TokenResponseDto;
import com.kbsw.seasonthon.security.jwt.service.AuthService;
import com.kbsw.seasonthon.security.oauth2.principal.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API", description = "Access Token / Refresh Token 기반 인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "자체 로그인",
            description = "username, password를 이용해 로그인을 수행하며, 즉시 access / refresh 토큰을 발급합니다."
    )
    @PostMapping("/login")
    public ResponseEntity<ResponseBody<TokenResponseDto>> usernameLogin(
            @RequestBody UsernameLoginRequestDto request
    ) {
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(authService.usernameLogin(request)));
    }

    @Operation(
            summary = "Refresh token을 이용한 토큰 재발급",
            description = "Authorization 헤더의 Bearer refresh_token 또는 HttpOnly 쿠키 'refresh'를 이용해 access/refresh를 갱신합니다."
    )
    @PostMapping("/refresh")
    public ResponseEntity<ResponseBody<TokenResponseDto>> reissueTokens(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            HttpServletRequest request
    ) {
        TokenResponseDto tokenDto;
        if (bearerToken != null && !bearerToken.isBlank()) {
            tokenDto = authService.reissueTokens(bearerToken);
        } else {
            String cookieRefresh = CookieUtils.getCookie(request, "refresh")
                    .orElseThrow(() -> new BusinessException(ExceptionType.INVALID_REFRESH_TOKEN));
            tokenDto = authService.reissueTokensUsingRawRefresh(cookieRefresh);
        }
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(tokenDto));
    }

    @Operation(
            summary = "전용 쿠키 기반 재발급",
            description = "HttpOnly 쿠키 'refresh'만 읽어 RTR 수행. 응답 본문으로 새 토큰을 반환하며, 필요 시 새 쿠키도 재세팅합니다."
    )
    @PostMapping("/refresh-cookie")
    public ResponseEntity<ResponseBody<TokenResponseDto>> refreshFromCookie(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String cookieRefresh = CookieUtils.getCookie(request, "refresh")
                .orElseThrow(() -> new BusinessException(ExceptionType.INVALID_REFRESH_TOKEN));

        TokenResponseDto tokenDto = authService.reissueTokensUsingRawRefresh(cookieRefresh);

        // (선택) 새 refresh를 쿠키로 갱신 — 운영환경에 맞춰 secure/devLocal 플래그 조정
        addCookie(response, "refresh", tokenDto.getRefresh(), 14 * 24 * 60 * 60, true, true);

        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(tokenDto));
    }

    @Operation(
            summary = "단일 로그아웃",
            description = "특정 refresh token을 만료시킵니다.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @DeleteMapping("/logout")
    public ResponseEntity<ResponseBody<Void>> logout(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String bearerToken
    ) {
        authService.logout(bearerToken);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    @Operation(
            summary = "전체 로그아웃",
            description = "특정 유저에 대해 등록된 모든 refresh token을 만료시킵니다.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @DeleteMapping("/logout/all")
    public ResponseEntity<ResponseBody<Void>> logoutAll(
            @Parameter(hidden = true)
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        authService.logoutAll(principal.getUser().getId());
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    /* ────────────── 내부 헬퍼(쿠키 세팅) ────────────── */
    private void addCookie(HttpServletResponse res, String name, String value,
                           int maxAgeSeconds, boolean httpOnly, boolean devLocal) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(!devLocal ? true : false); // devLocal=true면 Secure=false (로컬)
        res.addCookie(cookie);
        // SameSite=Lax 지정 (서블릿 표준 속성 부재로 헤더 추가)
        res.addHeader("Set-Cookie",
                String.format("%s=%s; Path=/; Max-Age=%d; %s%sSameSite=Lax",
                        name,
                        value,
                        maxAgeSeconds,
                        httpOnly ? "HttpOnly; " : "",
                        (!devLocal ? "Secure; " : "")
                )
        );
    }
}