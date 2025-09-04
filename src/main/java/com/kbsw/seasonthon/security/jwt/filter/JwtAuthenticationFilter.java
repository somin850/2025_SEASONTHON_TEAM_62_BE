package com.kbsw.seasonthon.security.jwt.filter;

import com.kbsw.seasonthon.security.jwt.service.CustomUserDetailsService;
import com.kbsw.seasonthon.security.jwt.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    // 필요시 특정 경로는 아예 JWT 검사 스킵
    private static final AntPathMatcher PM = new AntPathMatcher();
    private static final List<String> SKIP_PATTERNS = List.of(
            "/error", "/favicon.ico",
            "/oauth2/**", "/login/oauth2/**",
            "/public/**", "/health"
    );

    // dev 전용: 토큰 없을 때 게스트 주입을 켤지 여부
    private static final boolean DEV_GUEST_ENABLED = false; // 필요시 true로

    private boolean shouldSkip(HttpServletRequest req) {
        String uri = req.getRequestURI();
        for (String p : SKIP_PATTERNS) {
            if (PM.match(p, uri)) return true;
        }
        return "OPTIONS".equalsIgnoreCase(req.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // 이미 인증이 세팅되어 있으면 건드리지 않음
        var current = SecurityContextHolder.getContext().getAuthentication();
        if (current != null && current.isAuthenticated() && !(current instanceof AnonymousAuthenticationToken)) {
            chain.doFilter(request, response);
            return;
        }

        if (shouldSkip(request)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
                String token = bearer.substring(7);

                if (jwtTokenProvider.isValidToken(token)) {
                    // subject에 userId(Long) 저장했다고 가정
                    Long userId = jwtTokenProvider.extractId(token);

                    UserDetails ud = customUserDetailsService.loadUserById(userId);
                    var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    chain.doFilter(request, response);
                    return;
                } else {
                    log.debug("Invalid JWT token");
                }
            }

            // 토큰이 없거나 무효 → 예외 없이 패스
            if (DEV_GUEST_ENABLED) {
                // dev 시연/편의용: 게스트 강제 주입 (운영 금지)
                var guest = org.springframework.security.core.userdetails.User
                        .withUsername("dev-guest")
                        .password("") // 사용 안 함
                        .authorities("ROLE_GUEST")
                        .build();
                var guestAuth = new UsernamePasswordAuthenticationToken(guest, null, guest.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(guestAuth);
            }

        } catch (Exception e) {
            log.warn("JWT filter error: {}", e.getMessage());
            // 어떤 에러든 컨텍스트 비우고 계속 진행(401/500로 터지지 않게)
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }


    //    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        String token = JwtTokenProvider.extractToken(request.getHeader("Authorization"));  // JWT 토큰 추출
//
//        log.debug("[JwtAuthenticationFilter] token: {}", token);
//
//        // 토큰이 존재하고 유효한 경우
//        if (token != null && jwtTokenProvider.isValidToken(token)) {
//            Long id =jwtTokenProvider.extractId(token);  // JWT에서 사용자명 추출
//
//            log.debug("[JwtAuthenticationFilter] userId from token: {}", id);
//
//            // UserDetailsService 를 통해 사용자 정보 로드
//            PrincipalDetails userDetails = (PrincipalDetails) customUserDetailsService.loadUserById(id);
//
//            // 인증 객체 생성 및 SecurityContext에 설정
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//
//        chain.doFilter(request, response);
//    }
}