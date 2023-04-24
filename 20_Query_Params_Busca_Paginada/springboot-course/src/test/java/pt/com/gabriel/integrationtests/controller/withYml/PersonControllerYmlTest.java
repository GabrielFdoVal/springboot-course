package pt.com.gabriel.integrationtests.controller.withYml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import pt.com.gabriel.configs.TestConfigs;
import pt.com.gabriel.configs.integrationtests.vo.AccountCredentialsVO;
import pt.com.gabriel.configs.integrationtests.vo.PersonVO;
import pt.com.gabriel.configs.integrationtests.vo.TokenVO;
import pt.com.gabriel.configs.integrationtests.vo.pagedmodels.PagedModelPerson;
import pt.com.gabriel.integrationtests.controller.withYml.mapper.YMLMapper;
import pt.com.gabriel.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerYmlTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification;
	private static YMLMapper objectMapper;
	
	private static PersonVO person;
	
	@BeforeAll
	public static void setUp() {
		objectMapper = new YMLMapper();
		person = new PersonVO();
		
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var accessToken = given().config(
				RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
					.body(user, objectMapper)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class, objectMapper)
							.getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var persistedPerson = given().config(
				RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.body(person, objectMapper)
					.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(PersonVO.class, objectMapper);
		
		person = persistedPerson;
		
		assertTrue(persistedPerson.getId() > 0);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());	
		

		assertEquals("Gabriel", persistedPerson.getFirstName());
		assertEquals("do Val", persistedPerson.getLastName());
		assertEquals("Oeiras, Lisbon - Portugal", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		person.setLastName("Ferreira");
		
		var persistedPerson = given().config(
				RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.body(person, objectMapper)
					.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(PersonVO.class, objectMapper);
		
		person = persistedPerson;
		
		assertEquals(persistedPerson.getId(), person.getId());
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());	
		

		assertEquals("Gabriel", persistedPerson.getFirstName());
		assertEquals("Ferreira", persistedPerson.getLastName());
		assertEquals("Oeiras, Lisbon - Portugal", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}
	
	@Test
	@Order(3)
	public void testDisableById() throws JsonMappingException, JsonProcessingException {
		
		var persistedPerson = given()
				.spec(specification)
				.config(
						RestAssuredConfig.config().encoderConfig(
								EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
							)
					)
						.contentType(TestConfigs.CONTENT_TYPE_YML)
					.pathParam("id", person.getId())
					.when()
					.patch("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(PersonVO.class, objectMapper);
		
		person = persistedPerson;

		assertEquals(persistedPerson.getId(), person.getId());
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());	

		assertEquals("Gabriel", persistedPerson.getFirstName());
		assertEquals("Ferreira", persistedPerson.getLastName());
		assertEquals("Oeiras, Lisbon - Portugal", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	private void mockPerson() {
		person.setFirstName("Gabriel");
		person.setLastName("do Val");
		person.setAddress("Oeiras, Lisbon - Portugal");
		person.setGender("Male");
		person.setEnabled(true);
	}

	@Test
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var persistedPerson = given().config(
				RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.pathParam("id", person.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(PersonVO.class, objectMapper);
		
		person = persistedPerson;

		assertEquals(persistedPerson.getId(), person.getId());
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());	
		

		assertEquals("Gabriel", persistedPerson.getFirstName());
		assertEquals("Ferreira", persistedPerson.getLastName());
		assertEquals("Oeiras, Lisbon - Portugal", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(5)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		
		given().config(
				RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.pathParam("id", person.getId())
					.when()
					.delete("{id}")
				.then()
					.statusCode(204);
	}
	
	@Test
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var wrapper = given().config(
				RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.queryParams("page",3,"size",10,"direction","asc")
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.as(PagedModelPerson.class, objectMapper);
		
		var people = wrapper.getContent();
		PersonVO foundPerson1 = people.get(0);
		
		assertEquals(677, foundPerson1.getId());
		assertNotNull(foundPerson1.getId());
		assertNotNull(foundPerson1.getFirstName());
		assertNotNull(foundPerson1.getLastName());
		assertNotNull(foundPerson1.getAddress());
		assertNotNull(foundPerson1.getGender());
		assertTrue(foundPerson1.getEnabled());	
		

		assertEquals("Alic", foundPerson1.getFirstName());
		assertEquals("Terbrug", foundPerson1.getLastName());
		assertEquals("3 Eagle Crest Court", foundPerson1.getAddress());
		assertEquals("Male", foundPerson1.getGender());
		
		PersonVO foundPerson6 = people.get(5);
		
		assertEquals(911, foundPerson6.getId());
		assertNotNull(foundPerson6.getId());
		assertNotNull(foundPerson6.getFirstName());
		assertNotNull(foundPerson6.getLastName());
		assertNotNull(foundPerson6.getAddress());
		assertNotNull(foundPerson6.getGender());
		assertTrue(foundPerson6.getEnabled());	
		

		assertEquals("Allegra", foundPerson6.getFirstName());
		assertEquals("Terbrug", foundPerson1.getLastName());
		assertEquals("3 Eagle Crest Court", foundPerson1.getAddress());
		assertEquals("Male", foundPerson1.getGender());
	}
	
	@Test
	@Order(7)
	public void testFindAllWithOutToken() throws JsonMappingException, JsonProcessingException {
		
		 RequestSpecification testFindAllWithOutToken = new RequestSpecBuilder()
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		given()
		.spec(testFindAllWithOutToken).config(
				RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.when()
					.get()
				.then()
					.statusCode(403);
	}
	
	@Test
	@Order(8)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var content = given().config(
				RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
					)
			)
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.queryParams("page",0,"size",10,"direction","asc")
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.asString();
		
		assertTrue(content.contains("- rel: \"first\"\n"
				+ "  href: \"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("- rel: \"self\"\n"
				+ "    href: \"http://localhost:8888/api/person/v1/701\""));
		assertTrue(content.contains("- rel: \"self\"\n"
				+ "    href: \"http://localhost:8888/api/person/v1/730\""));
		assertTrue(content.contains("- rel: \"self\"\n"
				+ "    href: \"http://localhost:8888/api/person/v1/380\""));
		
		assertTrue(content.contains("- rel: \"self\"\n"
				+ "  href: \"http://localhost:8888/api/person/v1?page=0&size=10&direction=asc\""));
		assertTrue(content.contains("- rel: \"next\"\n"
				+ "  href: \"http://localhost:8888/api/person/v1?direction=asc&page=1&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("- rel: \"last\"\n"
				+ "  href: \"http://localhost:8888/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc\""));
		
		assertTrue(content.contains("page:\n"
				+ "  size: 10\n"
				+ "  totalElements: 1008\n"
				+ "  totalPages: 101\n"
				+ "  number: 0"));
	}
}
