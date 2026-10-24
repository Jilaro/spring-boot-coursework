package ru.urabov.shopping.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import ru.urabov.shopping.service.ActionLogService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Публикует события входа и неудачного входа – их записывает ActionLogService
    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher publisher) {
        return new DefaultAuthenticationEventPublisher(publisher);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ActionLogService actionLog) throws Exception {
        // При отказе в доступе действие записывается в журнал, затем показывается страница 403
        AccessDeniedHandlerImpl deniedPage = new AccessDeniedHandlerImpl();
        deniedPage.setErrorPage("/access-denied");
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
                .exceptionHandling(ex -> ex.accessDeniedHandler((request, response, e) -> {
                    String url = request.getRequestURI()
                            + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
                    String user = request.getRemoteUser() == null ? "anonymous" : request.getRemoteUser();
                    actionLog.warn(user, "получил отказ в доступе: " + url);
                    deniedPage.handle(request, response, e);
                }));
        return http.build();
    }
}
