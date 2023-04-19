package pt.com.gabriel.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pt.com.gabriel.repositories.UserRepository;

@Service
public class UserServices implements UserDetailsService {

	private Logger logger = Logger.getLogger(UserServices.class.getName().toString());
	
	@Autowired
	UserRepository repository;
	
	public UserServices(UserRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Finding one user by name " +username+ "!");
		var user = repository.findByUserName(username);
		if (user != null) {
			// Possível erro
			return (UserDetails) user;
		} else {
			throw new UsernameNotFoundException("Username: " + username + " not found!");
		}
	}
}
