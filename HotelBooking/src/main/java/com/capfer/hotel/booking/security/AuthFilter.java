package com.capfer.hotel.booking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    private final JwtUtilsService jwtUtilsService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> optionalToken = getTokenFromRequest(request);

        if (optionalToken.isPresent()) {
            String token = optionalToken.get();
            try {
                String email = jwtUtilsService.getEmailFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(email);

                if (StringUtils.hasText(email) && jwtUtilsService.isValidToken(token, userDetails)) {
                    var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // Set the details of the authentication token using the request information
                    // This is important for Spring Security to properly handle the authentication context
                    // Build the details from the request and set it to the authentication token
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                log.error("Failed to authenticate user with token: {}", token, e);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error during filter chain execution", e);
            throw e; // Rethrow the exception to ensure proper error handling by Spring Security
        }
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }
}
