package io.microservice.security;

import java.util.Optional;

import io.microservice.security.models.MyUserDetails;
import io.microservice.security.models.User;
import io.microservice.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
	
//	calling repository interface that is created using Jpa to call the User table in database
	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

//		spring security takes a userName and call loadUserByUsername(userName), this class will give it to repository
//		and gives a User.class. this object should be converted to a instance of UserDetails in order to return.
		
		Optional <User> user = userRepository.findByUserName(userName);
		user.orElseThrow(() -> new UsernameNotFoundException("Not found: "+ userName));
		return user.map(MyUserDetails::new).get();
	}

}
