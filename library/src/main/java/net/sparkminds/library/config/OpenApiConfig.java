package net.sparkminds.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Library management API definition",
        description = "All API definitions",
        version = "1.0.0"))
public class OpenApiConfig {
	@Bean
	 OpenAPI customizeOpenAPI() {
	    final String securitySchemeName = "bearerAuth";
	    return new OpenAPI()  
	    		.addSecurityItem(new SecurityRequirement()
	    		.addList(securitySchemeName))
	    		.components(new Components()
	    		.addSecuritySchemes(securitySchemeName, new SecurityScheme()
	    		.name(securitySchemeName)
	            .type(SecurityScheme.Type.HTTP)
	            .scheme("bearer")
	            .bearerFormat("JWT")));
	    }
}