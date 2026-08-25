package com.app.security.filter;

import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.security.service.CustomUserDetailsService;
import com.app.security.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

//			final String authHeader = request.getHeader("Authorization");
//			// No JWT found
//			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//				filterChain.doFilter(request, response);
//				return;
//			}
//	
//			String jwt = authHeader.substring(7);
//	
//			String username;
//	
//			try {
//				username = jwtService.extractUsername(jwt);
//			} catch (Exception ex) {
//				filterChain.doFilter(request, response);
//				return;
//			}
//	
//			// Authenticate only if not already authenticated
//	
//			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//	
//				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//	
//				if (jwtService.isTokenValid(jwt, userDetails)) {
//	
//					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//							userDetails, null, userDetails.getAuthorities());
//	
//					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//	
//					SecurityContextHolder.getContext().setAuthentication(authentication);
//				}
//			}
//	
//			filterChain.doFilter(request, response);

		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			System.out.println("No Bearer token found");
			filterChain.doFilter(request, response);
			return;
		}

		String jwt = authHeader.substring(7);
		// System.out.println("JWT: " + jwt);

		try {
			String username = jwtService.extractUsername(jwt);

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			boolean valid = jwtService.isTokenValid(jwt, userDetails);

			if (valid) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		filterChain.doFilter(request, response);
	}
}