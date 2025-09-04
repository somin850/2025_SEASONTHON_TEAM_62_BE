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
                .cors(withDefaults())
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
                                "/favicon.ico",
                                "*"
                        ).permitAll()

                        // 그 외 공개/인증 정책은 프로젝트에 맞게 조정
                        //.requestMatchers("/api/auth/**", "/public/**").permitAll()
                        //.anyRequest().authenticated()
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
}