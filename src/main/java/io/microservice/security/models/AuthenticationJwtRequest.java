package io.microservice.security.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * 
 * the class for requesting the authentication with user and pass to receive jwt as a response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationJwtRequest {

	private String username;
	private String password;
}
