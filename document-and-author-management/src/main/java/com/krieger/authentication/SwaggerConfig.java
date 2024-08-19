package com.krieger.authentication;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * To load Authorize button configuration in swagger page to enter basic authentication details to authenticate to APIs.
 */
@Configuration
public class SwaggerConfig {

	/**
	 * To enable basic authentication details to authenticate to APIs.
	 *
	 * @return updated configuration swagger UI.
	 */
	@Bean
	public OpenAPI customSwaggerOpenAPI() {

		final String securitySchemeName = "basicAuth";

		return new OpenAPI()
				.info(new Info()
						.title("Document and Author Management")
						.description("Document and Author Management Web Application")
						.version("1.0.0")
				)
				.addSecurityItem(new SecurityRequirement()
						.addList(securitySchemeName))
				.components(new Components()
						.addSecuritySchemes(
								securitySchemeName,
								new SecurityScheme()
										.name(securitySchemeName)
										.type(SecurityScheme.Type.HTTP)
										.scheme("basic")
						)
				);
	}
}