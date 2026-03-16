package com.taskmagnet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TaskMagnet API")
                        .description("REST API for TaskMagnet - Task Management Application")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TaskMagnet")
                                .email("support@taskmagnet.com")));
    }
}
