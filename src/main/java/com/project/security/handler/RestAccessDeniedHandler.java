package com.project.security.handler;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.*;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler { // cuando intentas entrar a algo sin permiso

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json");
		response.getWriter().write("{\"error\":\"Forbidden\"}");
	}
}
