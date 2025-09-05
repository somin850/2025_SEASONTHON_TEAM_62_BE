package com.kbsw.seasonthon.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Configuration
@Slf4j
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("🔧 Swagger 설정 - 개발 모드: 인증 없이 모든 API 테스트 가능");
        return new OpenAPI()
                .info(new Info()
                        .title("SeasonThon API")
                        .description("SeasonThon 크루 매칭 서비스 API 문서 (개발 모드: 인증 불필요)")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("SeasonThon Team")
                                .email("seasonthon@example.com")))
                .servers(List.of(
                        new Server()
                                .url("http://seasonthon-alb-272154529.ap-northeast-2.elb.amazonaws.com")
                                .description("AWS 배포 서버"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 개발 서버")
                ));
                // 인증 관련 설정 제거 - 모든 API를 인증 없이 테스트 가능
    }
}


