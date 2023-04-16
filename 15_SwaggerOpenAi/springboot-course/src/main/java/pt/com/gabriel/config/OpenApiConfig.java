package pt.com.gabriel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
	
	@Bean
	public OpenAPI customOpenApi() {
		return new OpenAPI()
			.info(new Info()
					.title("RESTful API with Java and Spring Boot 3")
					.version("v1")
					.description("Hire me!")
					.termsOfService("https://github.com/GabrielFdoVal/springboot-course")
					.license(new License().name("Apache 2.0")
							.url("https://github.com/GabrielFdoVal/springboot-course")));
	}
}
