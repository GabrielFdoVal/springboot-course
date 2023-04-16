package pt.com.gabriel.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.com.gabriel.exceptions.ResourseNotFoundException;
import pt.com.gabriel.mapper.DozerMapper;
import pt.com.gabriel.model.Person;
import pt.com.gabriel.data.vo.v1.PersonVO;
import pt.com.gabriel.repositories.PersonRepository;

@Service
public class PersonService {

	private Logger logger = Logger.getLogger(PersonService.class.getName().toString());
	
	@Autowired
	PersonRepository repository;
	
	public PersonVO findById(Long id) {
		logger.info("Finding one person");
		
		Person entity = repository.findById(id).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		return DozerMapper.parseObject(entity, PersonVO.class);
	}
	
	public List<PersonVO> findAll() {
		logger.info("Finding all people");
		
		return DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
	}
	
	public PersonVO create(PersonVO person) {
		logger.info("Creating a person");		
		Person entity = DozerMapper.parseObject(person, Person.class);		
		PersonVO vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		return vo;
	}
	
	public PersonVO update(PersonVO person) {
		logger.info("Updating a person");
		
		Person entity = repository.findById(person.getId()).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		PersonVO vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting a person");
		Person entity = repository.findById(id).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		repository.delete(entity);
	}
}
