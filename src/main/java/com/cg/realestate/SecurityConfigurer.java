package com.cg.realestate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter{


	@Autowired
	JwtRequestFilter jwtRequestFilter;
	   
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.csrf().disable().cors().disable()
		.authorizeRequests()
		.antMatchers(HttpMethod.GET, "/login/authenticate").hasAuthority("ADMIN")
		.antMatchers("/login/authenticate").permitAll()
		.antMatchers("/login/signup").permitAll()
		.antMatchers("/property/**").permitAll()
		.antMatchers("/buyorsell/add").permitAll()
		.antMatchers("/profile/**").permitAll()
		.and()
		.sessionManagement() 
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}
}