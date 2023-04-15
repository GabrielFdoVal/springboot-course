package pt.com.gabriel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.com.gabriel.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

}
