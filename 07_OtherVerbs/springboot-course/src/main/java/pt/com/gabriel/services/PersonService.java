package pt.com.gabriel.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import pt.com.gabriel.model.Person;

@Service
public class PersonService {


	private final AtomicLong counter = new AtomicLong();
	private Logger logger = Logger.getLogger(PersonService.class.getName().toString());
	
	public Person findById(String id) {
		logger.info("Finding one person");
		Person person = new Person(counter.incrementAndGet(), "Gabriel", "do Val", "Oerias - Portugal", "Male");
		
		return person;
	}
	
	public List<Person> findAll() {
		logger.info("Finding all people");
		List<Person> persons = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			Person person = mockPerson(i);
			persons.add(person);
		}
		return persons;
	}
	
	public Person create(Person person) {
		logger.info("Creating a person");
		return person;
	}
	
	public Person update(Person person) {
		logger.info("Updating a person");
		return person;
	}
	
	public void delete(String id) {
		logger.info("Deleting a person");
	}

	private Person mockPerson(int i) {
		return new Person(counter.incrementAndGet(), "First Name " + i, "Last Name " + i, "Address " + i, "Gender " + i);
	}
}
