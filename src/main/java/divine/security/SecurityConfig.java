package divine.security;

import divine.security.filter.JWTAuthenticationFilter;
import divine.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final UserService userService;
    private final JWTAuthenticationFilter jwtFilter;
    private final CustomCorsConfiguration customCorsConfiguration;

    public SecurityConfig(UserService userService, JWTAuthenticationFilter jwtFilter, CustomCorsConfiguration customCorsConfiguration) {
        this.userService = userService;
        this.jwtFilter = jwtFilter;
        this.customCorsConfiguration = customCorsConfiguration;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(customCorsConfiguration))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/auth/login", "/signup").permitAll();
                    auth.requestMatchers("/api/products", "/categories", "/categories/**", "/subcategories",
                            "api/products/**", "/subcategories/**").permitAll();
                    auth.requestMatchers("/cart", "/cart/**", "/wish/**", "/wish",
                            "/orders", "/orders/**").authenticated();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
