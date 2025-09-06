package com.kbsw.seasonthon.security.jwt.config;

import com.kbsw.seasonthon.security.jwt.filter.JwtAuthenticationFilter;
import com.kbsw.seasonthon.security.jwt.handler.CustomAccessDeniedHandler;
import com.kbsw.seasonthon.security.jwt.handler.CustomAuthenticationEntryPoint;
import com.kbsw.seasonthon.security.jwt.service.CustomUserDetailsService;
import com.kbsw.seasonthon.security.jwt.util.JwtTokenProvider;
import com.kbsw.seasonthon.security.oauth2.handler.OAuth2FailureHandler;
import com.kbsw.seasonthon.security.oauth2.handler.OAuth2SuccessHandler;
import com.kbsw.seasonthon.security.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable())

                // OAuth2 로그인 과정에서 인가 요청/인증 정보를 저장할 수 있도록
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // OAuth2 인가 시작점 & 콜백은 반드시 허용
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/error",
                                "/favicon.ico"
                        ).permitAll()

                        // Swagger UI 허용
                        .requestMatchers(
                                "/swagger-ui/**", 
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // 인증 관련 API 허용
                        .requestMatchers("/api/auth/**").permitAll()

                        // 신고 API 중 전체 목록과 상세 조회는 공개
                        .requestMatchers(HttpMethod.GET, "/api/hazards/all", "/api/hazards/{id}").permitAll()
                        
                        // 나머지 신고 API는 인증 필요
                        .requestMatchers("/api/hazards/**").authenticated()

                        // 기타 API들도 인증 필요로 설정 (필요에 따라 조정)
                        .requestMatchers("/api/**").authenticated()
                        
                        // 나머지는 허용
                        .anyRequest().permitAll()
                )

                .oauth2Login(oauth -> oauth
                        // (선택) 커스텀 로그인 페이지로 인가 시작점 고정
                        // 기본 콜백은 /login/oauth2/code/{registrationId}
                        //.redirectionEndpoint(r -> r.baseUri("/login/oauth2/code/*")) // 기본값이라 생략 가능
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )

                // JWT 필터: UsernamePasswordAuthenticationFilter 이전
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // 모든 origin 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키/인증 포함 허용
        configuration.setMaxAge(3600L); // preflight 요청 캐시 시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}