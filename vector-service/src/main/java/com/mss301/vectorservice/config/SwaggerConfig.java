package com.mss301.vectorservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Vector Service API",
                version = "1.0",
                description = "API for managing vector data"
        )
)
public class SwaggerConfig {
}
