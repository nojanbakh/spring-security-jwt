package io.microservice.security.repository;

import java.util.Optional;

import io.microservice.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * 
 * providing the definition of methods and Jpa provide others automatically
 */
public interface UserRepository extends JpaRepository<User, Integer> {
//	creating a functionality to look up a user by passing user name in database by Jpa
	Optional<User> findByUserName(String userName);

}
