package pt.com.gabriel.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.com.gabriel.controllers.BookController;
import pt.com.gabriel.data.vo.v1.BookVO;
import pt.com.gabriel.exceptions.RequiredObjectIsNullException;
import pt.com.gabriel.exceptions.ResourseNotFoundException;
import pt.com.gabriel.mapper.DozerMapper;
import pt.com.gabriel.model.Book;
import pt.com.gabriel.repositories.BookRepository;

@Service
public class BookServices {

	private Logger logger = Logger.getLogger(BookServices.class.getName().toString());
	
	@Autowired
	BookRepository repository;
	
	public BookVO findById(Long id) {
		logger.info("Finding one book");
		
		Book entity = repository.findById(id).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		BookVO vo = DozerMapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public List<BookVO> findAll() {
		logger.info("Finding all books");
		
		List<BookVO> books = DozerMapper.parseListObjects(repository.findAll(), BookVO.class);
		books.stream().forEach(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));
		return books;
	}
	
	public BookVO create(BookVO book) {
		if(book == null) throw new RequiredObjectIsNullException();
		logger.info("Creating a book");		
		Book entity = DozerMapper.parseObject(book, Book.class);		
		BookVO vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public BookVO update(BookVO book) {
		if(book == null) throw new RequiredObjectIsNullException();
		logger.info("Updating a book");
		
		Book entity = repository.findById(book.getKey()).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		
		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());
		
		BookVO vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting a book");
		Book entity = repository.findById(id).orElseThrow(() -> new ResourseNotFoundException("No records found for this ID"));
		repository.delete(entity);
	}
}
