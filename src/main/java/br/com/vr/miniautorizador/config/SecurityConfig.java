package br.com.vr.miniautorizador.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // desabilita CSRF para simplificar chamadas REST
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/cartoes/**", "/transacoes/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults()) // habilita autenticação Basic
                // Disable form login
                .formLogin(form -> form.disable())
                // Disable logout since we're not using it
                .logout(logout -> logout.disable())
                // Add headers for API documentation (if using SpringDoc/Swagger)
                .headers(headers -> headers
                        .frameOptions().disable()
                );

        return http.build();
    }
}

