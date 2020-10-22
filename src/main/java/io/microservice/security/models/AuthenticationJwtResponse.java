package io.microservice.security.models;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * as a response of /authenticate we just need a jwt string
 */
@Getter
@AllArgsConstructor
public class AuthenticationJwtResponse {

	private final String jwt;

}