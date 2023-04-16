package pt.com.gabriel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.com.gabriel.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long>{

}
