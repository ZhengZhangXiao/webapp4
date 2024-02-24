package dev.eviez.helloworld;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration   {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //System.out.println("in the security filter");
        http
                // Disable CSRF for API endpoints or when using token-based authentication
                .csrf(csrf -> csrf.disable())
                // Configure request authorization
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/users/{id}").authenticated() // Require authentication for secret_endpoint
                        .anyRequest().permitAll() // Allow all other requests without authentication
                )
                // Enable HTTP Basic authentication
                .httpBasic(withDefaults());

        return http.build();
    }

}
