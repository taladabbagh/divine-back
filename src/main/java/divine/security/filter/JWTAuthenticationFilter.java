package divine.security.filter;

import divine.service.JWTService;
import divine.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JWTService jwtService;
    private final UserService userService;

    @Autowired
    public JWTAuthenticationFilter(JWTService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length());
            System.out.println("Extracted token: " + token);  // Log the token

            try {
                String userId = jwtService.getUserIdFromToken(token);
                System.out.println("User ID from token: " + userId);  // Log the user ID

                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    var userDetails = userService.loadUserById(Long.valueOf(userId));

                    if (jwtService.isValidToken(token, userId)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("Authentication successful for User ID: " + userId);  // Log success
                    } else {
                        System.out.println("Invalid token for User ID: " + userId);  // Log invalid token
                    }
                }
            } catch (Exception ex) {
                System.out.println("JWT Authentication failed: " + ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

}
