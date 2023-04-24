package pt.com.gabriel.integrationtests.controller.withJson;

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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import pt.com.gabriel.configs.TestConfigs;
import pt.com.gabriel.configs.integrationtests.vo.AccountCredentialsVO;
import pt.com.gabriel.configs.integrationtests.vo.PersonVO;
import pt.com.gabriel.configs.integrationtests.vo.TokenVO;
import pt.com.gabriel.configs.integrationtests.vo.wrappers.WrapperPersonVO;
import pt.com.gabriel.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	
	private static PersonVO person;
	
	@BeforeAll
	public static void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		person = new PersonVO();
		
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var accessToken = given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.body(user)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class)
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
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.body(person)
					.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
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
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.body(person)
					.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
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

	private void mockPerson() {
		person.setFirstName("Gabriel");
		person.setLastName("do Val");
		person.setAddress("Oeiras, Lisbon - Portugal");
		person.setGender("Male");
		person.setEnabled(true);
	}

	@Test
	@Order(3)
	public void testDisableById() throws JsonMappingException, JsonProcessingException {
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.pathParam("id", person.getId())
					.when()
					.patch("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
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
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.pathParam("id", person.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
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
		
		given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.pathParam("id", person.getId())
					.when()
					.delete("{id}")
				.then()
					.statusCode(204);
	}
	
	@Test
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.queryParams("page",3,"size",10,"direction","asc")
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.asString();
		
		WrapperPersonVO wrapper  = objectMapper.readValue(content, WrapperPersonVO.class);
		var people = wrapper.getEmbedded().getPersons();
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
	public void testFindPersonsByName() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("firstName", "orv")
				.queryParams("page",0,"size",6,"direction","asc")
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.when()
					.get("/findPersonsByName/{firstName}")
				.then()
					.statusCode(200)
				.extract()
					.body()
					.asString();
		
		WrapperPersonVO wrapper  = objectMapper.readValue(content, WrapperPersonVO.class);
		var people = wrapper.getEmbedded().getPersons();
		PersonVO foundPerson1 = people.get(0);
		
		System.out.println(foundPerson1);
		
		assertEquals(25, foundPerson1.getId());
		assertNotNull(foundPerson1.getId());
		assertNotNull(foundPerson1.getFirstName());
		assertNotNull(foundPerson1.getLastName());
		assertNotNull(foundPerson1.getAddress());
		assertNotNull(foundPerson1.getGender());
		assertTrue(foundPerson1.getEnabled());	
		

		assertEquals("Orv", foundPerson1.getFirstName());
		assertEquals("MacShirrie", foundPerson1.getLastName());
		assertEquals("61446 Farragut Way", foundPerson1.getAddress());
		assertEquals("Male", foundPerson1.getGender());
	}
	
	@Test
	@Order(8)
	public void testFindAllWithOutToken() throws JsonMappingException, JsonProcessingException {
		
		 RequestSpecification testFindAllWithOutToken = new RequestSpecBuilder()
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		given()
				.spec(testFindAllWithOutToken)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.when()
					.get()
				.then()
					.statusCode(403);
	}
	
	@Test
	@Order(9)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.queryParams("page",0,"size",10,"direction","asc")
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
					.asString();
		
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/701\"}}}"));
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/730\"}}}"));
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/380\"}}}"));
		
		assertTrue(content.contains("{\"first\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\"}"));
		assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/person/v1?page=0&size=10&direction=asc\"}"));
		assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=1&size=10&sort=firstName,asc\"}"));
		assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc\"}}"));
		
	}
}
