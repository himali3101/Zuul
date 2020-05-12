package com.cg.realestate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class JwtRequestFilter extends OncePerRequestFilter{
	@Autowired
	private MyUserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String authorizationHeader = request.getHeader("Authorization"); // Extracting AUthorization Header
		
		String username = null;
        String jwt = null;
        String role = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {  // if the header starts with Bearer then it's a JWT based Authorization
            jwt = authorizationHeader.substring(7); // remove the first 7 characters which are "Bearer ", which leaves you with the jwt token
            username = jwtUtil.extractUsername(jwt); // it's extracting username
            role = jwtUtil.extractRoles(jwt);
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // if there's actually a user in my db
            																				// this is again not a microservice appoach
            																				// I will just comment it out at the 2 hrs meetings

            if (jwtUtil.validateToken(jwt, userDetails)) { // if the jwt is not expired, and if there is actually a user with the same name in the db. Stupid Appraoch ://
            	
            	// Generating a  new authority
            	List<GrantedAuthority> roleList = new ArrayList<GrantedAuthority>();
        		roleList.add(new SimpleGrantedAuthority(role));
            	
        		
        		// User will be authenticated without credentials with proper Authorities
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, roleList);
                
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response); // let this code block be copied and pasted for working conditions

	}

}