package io.microservice.security;

import javax.sql.DataSource;

import io.microservice.security.filters.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

//	finding h2 database
	@Autowired
	DataSource dataSource;

//	service for load userDetails with jpa from mysql 
	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	
	/**
	 * Authentication
	 * whenever comes a request to our APIs, SecurityConfiguration class will check the authentication by using configure method
	 * AuthenticationManagerBuilder checks if any authentication it has. it gets an interface called userDetailsService to check 
	 * the user details. we should implement this class and override the loadUserByUsername method. when a request comes with user and pass
	 * it will accept that user and check if we have it in database and everything is right, if yes it allows to go to the next level. 
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		// connecting to database (h2 embeded) just for test
//		auth.jdbcAuthentication()
//		 		.dataSource(dataSource)
//		 		.withDefaultSchema()
//		 		.withUser(User.withUsername("user")
//		 				.password("pass")
//		 				.roles("USER"))
//		 		.withUser(User.withUsername("admin")
//		 				.password("pass")
//		 				.roles("ADMIN"));

//		---------------------------------------------
//		connecting to mysql database using jpa
		auth.userDetailsService(userDetailsService);

	}

//	Authorization
	@Override
	protected void configure(HttpSecurity http) throws Exception {

//		disabling csrf for specific routes(POST,PUT,DELETE)
//		http.csrf().ignoringAntMatchers("/**");

		//specifying roles for our endpoint. but notice that these should be passed before .anyRequest().authenticated()
		// if we use them here. they do not use jwt and use just a basic authentication
//		http.authorizeRequests()
//				.antMatchers(HttpMethod.GET, "/hello").hasAnyRole("ADMIN","USER")
////				.antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
////                .antMatchers("/**").permitAll()
//				.and().formLogin().permitAll().and().logout().and().httpBasic();
//-----------------------------------------------------------------------------------------
		/**
		 * we should permit all user to have authenticated for /authenticate and any other request should have authenticated
		 */
		http.csrf().disable().authorizeRequests().antMatchers("/authenticate").permitAll()
		// we can specify any role for every endpoint 
		.antMatchers(HttpMethod.GET, "/hello").hasAnyRole("ADMIN","USER")
		.anyRequest().authenticated()
		// make sure we use stateless session; session won't be used to store user's state.
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); 

		// adding jwtRequestFilter before UsernamePasswordAuthenticationFilter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}

//	in order to use AuthenticationManager in JwtAuthenticationController, we should create a Bean to be able to autowired it
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public PasswordEncoder getPasswordEncoder() {
//		not a good encoder just for test, it does not do any hashing on our password
		return NoOpPasswordEncoder.getInstance();
		
//		this is an better encoder. try to use it in development 
//		return new BCryptPasswordEncoder();
	}
	
}
