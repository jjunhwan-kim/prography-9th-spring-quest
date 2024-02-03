package com.prography.game.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Prography Spring Quest API Document",
                description = "API Document",
                version = "v1.0",
                termsOfService = "https://prography.org/",
                license = @License(
                        name = "Apache License Version 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0"
                ),
                contact = @Contact(
                        name = "JunHwan KIm",
                        email = "jjunhwan-kim@naver.com"
                )
        ),
        tags = {
                @Tag(name = "공통", description = "공통 기능 API"),
                @Tag(name = "유저", description = "유저 API"),
                @Tag(name = "방", description = "방 API")
        }
)
@Configuration
public class SpringDocConfig {
}
