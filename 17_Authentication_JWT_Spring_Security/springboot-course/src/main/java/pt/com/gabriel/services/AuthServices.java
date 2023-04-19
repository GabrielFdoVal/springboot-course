package pt.com.gabriel.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pt.com.gabriel.data.vo.v1.security.AccountCredentialsVO;
import pt.com.gabriel.data.vo.v1.security.TokenVO;
import pt.com.gabriel.repositories.UserRepository;
import pt.com.gabriel.security.jwt.JwtTokenProvider;

@Service
public class AuthServices {

	@Autowired
	private AuthenticationManager authenticationManager;	

	@Autowired
	private JwtTokenProvider tokenProvider;	

	@Autowired
	private UserRepository repository;
	
	@SuppressWarnings({ "rawtypes" })
	public ResponseEntity signin(AccountCredentialsVO data) {
		try {
			var username = data.getUserName();
			var password = data.getPassword();
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			
			var user = repository.findByUserName(username);
			var tokenResponse = new TokenVO();
			if(user != null) {
				tokenResponse = tokenProvider.createAccessToken(username, user.getRoles());
			} else {
				throw new UsernameNotFoundException("Username "+username+" not found!");
			}
			
			return ResponseEntity.ok(tokenResponse);
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid username/password supplied!");
		}
	}
}
