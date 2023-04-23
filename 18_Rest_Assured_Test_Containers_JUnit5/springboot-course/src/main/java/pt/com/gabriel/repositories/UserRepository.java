package pt.com.gabriel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pt.com.gabriel.model.Person;
import pt.com.gabriel.model.User;

@Repository
public interface UserRepository extends JpaRepository<Person, Long>{
	@Query("SELECT u FROM User u WHERE u.userName =:userName")
	User findByUserName(@Param("userName") String userName);
}
