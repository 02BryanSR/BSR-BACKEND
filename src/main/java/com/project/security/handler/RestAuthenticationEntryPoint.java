package com.project.security.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.*;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.getWriter().write("{\"error\":\"Unauthorized\"}");
	}
}
