package pt.com.gabriel.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.com.gabriel.exceptions.ResourseNotFoundException;
import pt.com.gabriel.model.Person;
import pt.com.gabriel.repositories.PersonRepository;

@Service
public class PersonService {

	private Logger logger = Logger.getLogger(PersonService.class.getName().toString());
	
	@Autowired
	PersonRepository repository;
	
	public Person findById(Long id) {
		logger.info("Finding one person");
		
		return repository.findById(id).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
	}
	
	public List<Person> findAll() {
		logger.info("Finding all people");
		
		return repository.findAll();
	}
	
	public Person create(Person person) {
		logger.info("Creating a person");
		return repository.save(person);
	}
	
	public Person update(Person person) {
		logger.info("Updating a person");
		
		Person entity = repository.findById(person.getId()).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		return repository.save(person);
	}
	
	public void delete(Long id) {
		logger.info("Deleting a person");
		Person entity = repository.findById(id).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		repository.delete(entity);
	}
}
