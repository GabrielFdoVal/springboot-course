package pt.com.gabriel.integrationtests.repositories;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import pt.com.gabriel.integrationtests.testcontainers.AbstractIntegrationTest;
import pt.com.gabriel.model.Person;
import pt.com.gabriel.repositories.PersonRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {
	
	@Autowired
	public PersonRepository repository;
	
	private static Person person;
	
	@BeforeAll
	public static void setup() {
		person = new Person();
	}
	
	@Test
	@Order(0)
	public void testFindPersonsByName() throws JsonMappingException, JsonProcessingException {
		Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "firstName"));
		
		person = repository.findPersonsByName("orv", pageable).getContent().get(0);
		
		
		assertEquals(25, person.getId());
		assertNotNull(person.getId());
		assertNotNull(person.getFirstName());
		assertNotNull(person.getLastName());
		assertNotNull(person.getAddress());
		assertNotNull(person.getGender());
		assertTrue(person.getEnabled());	
		

		assertEquals("Orv", person.getFirstName());
		assertEquals("MacShirrie", person.getLastName());
		assertEquals("61446 Farragut Way", person.getAddress());
		assertEquals("Male", person.getGender());
	}
	
	@Test
	@Order(1)
	public void testDisablePerson() throws JsonMappingException, JsonProcessingException {
		
		repository.disablePerson(person.getId());
		Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "firstName"));
		
		person = repository.findPersonsByName("orv", pageable).getContent().get(0);
		
		
		assertEquals(25, person.getId());
		assertNotNull(person.getId());
		assertNotNull(person.getFirstName());
		assertNotNull(person.getLastName());
		assertNotNull(person.getAddress());
		assertNotNull(person.getGender());
		assertFalse(person.getEnabled());	
		

		assertEquals("Orv", person.getFirstName());
		assertEquals("MacShirrie", person.getLastName());
		assertEquals("61446 Farragut Way", person.getAddress());
		assertEquals("Male", person.getGender());
	}
}
