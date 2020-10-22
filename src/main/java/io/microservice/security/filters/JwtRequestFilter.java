package io.microservice.security.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.microservice.security.MyUserDetailsService;
import io.microservice.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

/**
 * 
 * this class intercept every request only once
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private MyUserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * this method examine the incoming request for the jwt in the header. if it
	 * finds a valid jwt, it's gonna get the UserDetails out of UserDetailsService
	 * and save it in the security context. at the end it continues the chain.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestAuthorizationHeader = request.getHeader("Authorization");
		String username = null;
		String jwtToken = null;

		/*
		 * JWT Token is in the form "Bearer token". Remove Bearer word and get only the
		 * Token
		 */
		if (requestAuthorizationHeader != null && requestAuthorizationHeader.startsWith("Bearer ")) {
			jwtToken = requestAuthorizationHeader.substring(7); // beginIndex = 7

			try {
				username = jwtUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				logger.info("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				logger.info("JWT Token has expired");
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
		}

		/*
		 * Once we get the token validate it and get the userDetails using
		 * userDetailsService by passing username
		 */
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

			/*
			 * if token is valid and is not expired, configure Spring Security to manually
			 * set authentication
			 */
			if (jwtUtil.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				/*
				 * After setting the Authentication in the context, we specify that the current
				 * user is authenticated. So it passes the Spring Security Configurations
				 * successfully.
				 */
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		// do filter and continue the chain
		chain.doFilter(request, response);

	}

}