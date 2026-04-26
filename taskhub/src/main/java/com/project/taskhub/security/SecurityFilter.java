package com.project.taskhub.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenConfiguration tokenConfiguration;

    public SecurityFilter(TokenConfiguration tokenConfiguration) {
	this.tokenConfiguration = tokenConfiguration;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
	String autho = request.getHeader("Authorization");

	if (autho != null && autho.startsWith("Bearer ")) {
	    String token = autho.substring(7);
	    var optionUser = tokenConfiguration.validateToken(token);

	    if (optionUser.isPresent()) {
		var jwtuserdata = optionUser.get();
		var auth = new UsernamePasswordAuthenticationToken(jwtuserdata, null, Collections.emptyList());
		SecurityContextHolder.getContext().setAuthentication(auth);
	    }
	}

	filterChain.doFilter(request, response);
    }

}
