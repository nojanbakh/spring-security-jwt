package io.microservice.security.controller;

import io.microservice.security.MyUserDetailsService;
import io.microservice.security.jwt.JwtUtil;
import io.microservice.security.models.AuthenticationJwtRequest;
import io.microservice.security.models.AuthenticationJwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MyUserDetailsService userDetailsService;

	/**
	 * we create a method which creates authenticationToken based on
	 * AuthenticationJwtRequest which contains username and password we use
	 * AuthenticationManager in order to authenticate of username and password that
	 * is passed in, if does not authenticate, we throw an Exception if it does
	 * authenticate, we should just generate a token using jwtUtil, which needs
	 * userDetails in order to create jwt so we call userDetailService and
	 * loadUserByUsername because we have just userName in this point and we get an
	 * instance of userDetails and we path it to jwtUtil and send it back in
	 * ResponseEntity
	 * 
	 * NOTE: we should tell Spring when a person call authenticate api do not expect user to have authenticated
	 * and we need specify it in SecurityConfiguration.class
	 * 
	 * so we should send a post request with body:
	 * {"username":"foo","password":"foo"}
	 * in order to receive a jwt token to call API. this jwt should be passed as a header Authorization : "Bearer $jwtToken"
	 * 
	 * @param authenticationRequest
	 * @return authenticationResponse
	 * @throws Exception
	 */

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationJwtRequest authenticationRequest)
			throws Exception {

		// authenticate the user and pass
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		// get the userDetails in order to give it to jwtUtil
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

		//create a jwt with given userDetails
		final String jwtToken = jwtUtil.generateToken(userDetails);

		return ResponseEntity.ok(new AuthenticationJwtResponse(jwtToken));
	}

	/**
	 * it authenticates the userName and password by passing them to authenticationManager.
	 * 
	 * @param username
	 * @param password
	 * @throws Exception
	 */
	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}

	}

}