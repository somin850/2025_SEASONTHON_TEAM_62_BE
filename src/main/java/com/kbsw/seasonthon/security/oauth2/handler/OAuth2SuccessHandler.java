// OAuth2SuccessHandler.java (전체 교체)
package com.kbsw.seasonthon.security.oauth2.handler;

import com.kbsw.seasonthon.security.jwt.dto.response.TokenResponseDto;
import com.kbsw.seasonthon.security.jwt.service.AuthService;
import com.kbsw.seasonthon.security.oauth2.principal.PrincipalDetails;
import com.kbsw.seasonthon.user.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth 로그인 성공 시:
 * - 정식 access/refresh 즉시 발급
 * - access: JS가 읽을 수 있도록 일반 쿠키 (SameSite=Lax)
 * - refresh: HttpOnly 쿠키 (SameSite=Lax)
 * - 이후 프론트 경로로 리다이렉트 (쿼리에 토큰 싣지 않음)
 */
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${oauth2.url.base}")
    private String BASE_URL;

    @Value("${oauth2.url.path.auth}")
    private String AUTH_PATH;

    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        User user = principal.getUser();

        // 프론트가 보내주면 기기 식별 저장(없어도 동작)
        String deviceId = request.getHeader("Device-Id");

        // ✅ 바로 정식 토큰 발급
        TokenResponseDto tokens = authService.issueTokensFor(user, deviceId);

        // ✅ 쿠키로 내려주기
        // dev 환경(localhost): Secure=false, SameSite=Lax 사용 권장
        addCookie(response, "access", tokens.getAccess(), 60 * 60, false, true);      // 1h
        addCookie(response, "refresh", tokens.getRefresh(), 14 * 24 * 60 * 60, true, true); // 14d HttpOnly

        // ✅ 프론트로 깔끔 리다이렉트 (쿼리에 토큰 미포함)
        String redirectUrl = BASE_URL + AUTH_PATH; // 예: http://localhost:3000/oauth/authenticated
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    /**
     * SameSite=Lax 지정 쿠키 헬퍼
     * - httpOnly: refresh=true, access=false 권장
     * - secure: 로컬 http면 false, https 환경에선 true로 바꿔주세요.
     */
    private void addCookie(HttpServletResponse res, String name, String value,
                           int maxAgeSeconds, boolean httpOnly, boolean devLocal) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(!devLocal ? true : false); // devLocal=true면 Secure=false, 운영에선 true 권장
        // SameSite 지정 (Servlet Cookie API에 표준 속성은 없어 헤더로 한 번 더 세팅)
        res.addCookie(cookie);
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