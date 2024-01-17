package net.sparkminds.library.jwt;

import java.io.IOException;
import java.time.Instant;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.error.ErrorAPIResponse;
import net.sparkminds.library.service.impl.UserDetailsServiceImpl;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtFilter extends OncePerRequestFilter {

	private final UserDetailsServiceImpl userDetailsService;
	private final JwtUtil jwtUtil;
	private final MessageSource messageSource;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;
		String message = null;

		try {
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				token = authHeader.substring(7);
				username = jwtUtil.extractUsername(token);
			}   
			
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				if (jwtUtil.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}

			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			message = messageSource.getMessage("token.token-isExpired", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			
			ErrorAPIResponse errorAPIResponse = ErrorAPIResponse.builder()
					.timestamp(Instant.now().toString())
					.status(HttpStatus.UNAUTHORIZED.value())
					.error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
					.message(message)
					.messageCode("token.token-isExpired").build();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write(convertObjectToJson(errorAPIResponse));
			response.getWriter().flush();
		}
	}
	
	public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
