package pt.com.gabriel.integrationtests.controller.withYml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import pt.com.gabriel.configs.TestConfigs;
import pt.com.gabriel.configs.integrationtests.vo.AccountCredentialsVO;
import pt.com.gabriel.configs.integrationtests.vo.TokenVO;
import pt.com.gabriel.integrationtests.controller.withYml.mapper.YMLMapper;
import pt.com.gabriel.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerYmlTest  extends AbstractIntegrationTest {

	private static TokenVO tokenVO;
	private static YMLMapper objMapper;
	
	@BeforeAll
	public static void setup() {
		objMapper = new YMLMapper();
	}
	
	@Test
	@Order(1)
	public void testSignin() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		tokenVO = given().config(
					RestAssuredConfig.config().encoderConfig(
								EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
							)
					)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_YML)
				.body(user, objMapper)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class, objMapper);
		
		assertNotNull(tokenVO.getAccessToken());
		assertNotNull(tokenVO.getRefreshToken());
	}
	
	@Test
	@Order(2)
	public void testRefresh() throws JsonMappingException, JsonProcessingException {
		
		var newTokenVO = given().config(
							RestAssuredConfig.config().encoderConfig(
									EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
								)
						)
				.basePath("/auth/refresh")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("username", tokenVO.getUserName())
				.header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
					.when()
				.put("{username}")
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class, objMapper);
		
		assertNotNull(newTokenVO.getAccessToken());
		assertNotNull(newTokenVO.getRefreshToken());
	}
}
