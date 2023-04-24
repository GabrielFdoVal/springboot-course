package pt.com.gabriel.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pt.com.gabriel.controllers.PersonController;
import pt.com.gabriel.data.vo.v1.PersonVO;
import pt.com.gabriel.exceptions.RequiredObjectIsNullException;
import pt.com.gabriel.exceptions.ResourseNotFoundException;
import pt.com.gabriel.mapper.DozerMapper;
import pt.com.gabriel.model.Person;
import pt.com.gabriel.repositories.PersonRepository;

@Service
public class PersonServices {

	private Logger logger = Logger.getLogger(PersonServices.class.getName().toString());
	
	@Autowired
	PersonRepository repository;
	
	@Autowired
	PagedResourcesAssembler<PersonVO> assembler;
	
	public PersonVO findById(Long id) {
		logger.info("Finding one person");
		
		Person entity = repository.findById(id).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public PagedModel<EntityModel<PersonVO>> findPersonsByName(String firstname, Pageable pageable) {
		logger.info("Finding all people");
		
		var personPage = repository.findPersonsByName(firstname, pageable);
		
		var personsVoPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		personsVoPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		
		Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
		return assembler.toModel(personsVoPage, link);
	}
	
	public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
		logger.info("Finding all people");
		
		var personPage = repository.findAll(pageable);
		
		var personsVoPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		personsVoPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		
		Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
		return assembler.toModel(personsVoPage, link);
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
	
	@Transactional
	public PersonVO disablePerson(Long id) {
		logger.info("Disabling one person");
		repository.disablePerson(id);
		Person entity = repository.findById(id).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting a person");
		Person entity = repository.findById(id).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		repository.delete(entity);
	}
}
