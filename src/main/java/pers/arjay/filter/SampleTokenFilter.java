package pers.arjay.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

public class SampleTokenFilter extends OncePerRequestFilter {

	@Autowired
	private UserDetailsService userDetailsService;

	private final static String tokenHeader = "Sample-header";

	private final static String tokenHead = "accessToken";

	// @formatter:off
	@Override
	protected void doFilterInternal(
		HttpServletRequest request, 
		HttpServletResponse response, 
		FilterChain chain) throws ServletException, IOException {
	// @formatter:on

		final String authHeader = request.getHeader(tokenHeader);
		if (authHeader != null && authHeader.startsWith(tokenHead)) {
			final String authToken = authHeader.substring(tokenHead.length());
			logger.info("access otp token :" + authToken.trim());

			final UserDetails userDetails = userDetailsService.loadUserByUsername(authToken.trim());
			final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			
			
			if (userDetails != null && authentication == null) {
				UsernamePasswordAuthenticationToken upAuthentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				upAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				logger.info("authenticated user " + userDetails.getUsername() + ", setting security context");
				
				SecurityContextHolder.getContext().setAuthentication(upAuthentication);
			}
		}

		chain.doFilter(request, response);
	}

}
