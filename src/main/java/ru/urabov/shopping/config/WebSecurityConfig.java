package ru.urabov.shopping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Открыто всем
                        .requestMatchers("/", "/index", "/about", "/login", "/register", "/error",
                                "/actuator/health", "/actuator/info").permitAll()
                        // Только администратору
                        .requestMatchers("/admin/**", "/actuator/**").hasRole("ADMIN")
                        // Изменение данных: USER и ADMIN, у READ_ONLY доступа нет
                        .requestMatchers("/products/new", "/products/edit", "/products/save", "/products/delete",
                                "/shops/new", "/shops/edit", "/shops/save", "/shops/delete")
                        .hasAnyRole("USER", "ADMIN")
                        // Просмотр списков и расчёт – любому вошедшему пользователю
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/products", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));
        return http.build();
    }
}
