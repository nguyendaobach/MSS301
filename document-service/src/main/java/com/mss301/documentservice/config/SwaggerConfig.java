package com.mss301.documentservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Document Service API",
                version = "1.0",
                description = "API for managing document"
        )
)
public class SwaggerConfig {
}
