package pt.com.gabriel.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import pt.com.gabriel.exceptions.RequiredObjectIsNullException;
import pt.com.gabriel.exceptions.ResourseNotFoundException;
import pt.com.gabriel.mapper.DozerMapper;
import pt.com.gabriel.model.Person;
import pt.com.gabriel.controllers.PersonController;
import pt.com.gabriel.data.vo.v1.PersonVO;
import pt.com.gabriel.repositories.PersonRepository;

@Service
public class PersonServices {

	private Logger logger = Logger.getLogger(PersonServices.class.getName().toString());
	
	@Autowired
	PersonRepository repository;
	
	public PersonVO findById(Long id) {
		logger.info("Finding one person");
		
		Person entity = repository.findById(id).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public List<PersonVO> findAll() {
		logger.info("Finding all people");
		
		List<PersonVO> persons = DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
		persons.stream().forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		return persons;
	}
	
	public PersonVO create(PersonVO person) {
		if(person == null) throw new RequiredObjectIsNullException();
		logger.info("Creating a person");		
		Person entity = DozerMapper.parseObject(person, Person.class);		
		PersonVO vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public PersonVO update(PersonVO person) {
		if(person == null) throw new RequiredObjectIsNullException();
		logger.info("Updating a person");
		
		Person entity = repository.findById(person.getKey()).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		PersonVO vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting a person");
		Person entity = repository.findById(id).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		repository.delete(entity);
	}
}
