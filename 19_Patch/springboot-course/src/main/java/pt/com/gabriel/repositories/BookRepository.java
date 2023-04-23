package pt.com.gabriel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.com.gabriel.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

}
