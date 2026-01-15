package br.com.vr.miniautorizador.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // desabilita CSRF para simplificar chamadas REST
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/cartoes/**", "/transacoes/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(); // habilita autenticação Basic
                //.httpBasic("user", "password");

        return http.build();
    }
}

