package com.kbsw.seasonthon.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SeasonThon API")
                        .description("SeasonThon 크루 매칭 서비스 API 문서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("SeasonThon Team")
                                .email("seasonthon@example.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발 서버")
                ));
    }
}
