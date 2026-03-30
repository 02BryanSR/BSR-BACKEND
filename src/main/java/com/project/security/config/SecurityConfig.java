package com.project.security.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import com.project.security.handler.RestAccessDeniedHandler;
import com.project.security.handler.RestAuthenticationEntryPoint;
import com.project.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtFilter;
	private final RestAuthenticationEntryPoint entryPoint;
	private final RestAccessDeniedHandler deniedHandler;

	@Value("${app.security.cors.allowed-origins:http://localhost:4200}")
	private String allowedOrigins;

	public SecurityConfig(JwtAuthenticationFilter jwtFilter, RestAuthenticationEntryPoint entryPoint,
			RestAccessDeniedHandler deniedHandler) {
		this.jwtFilter = jwtFilter;
		this.entryPoint = entryPoint;
		this.deniedHandler = deniedHandler;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint).accessDeniedHandler(deniedHandler))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**", "/uploads/**").permitAll()
						.anyRequest().authenticated());

		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		config.setExposedHeaders(List.of("Authorization"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}