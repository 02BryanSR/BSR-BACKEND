package com.project.security.jwt;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.project.security.user.CustomerUserDetailsService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { // esta clase sirve para ver si existe el token o no y saber quien eres

	private final JwtService jwtService;
	private final CustomerUserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, CustomerUserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);
		if (!jwtService.isTokenValid(token)) {
			filterChain.doFilter(request, response);
			return;
		}

		String email = jwtService.extractEmail(token);

		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			var userDetails = userDetailsService.loadUserByUsername(email);
			var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		filterChain.doFilter(request, response);
	}
}
