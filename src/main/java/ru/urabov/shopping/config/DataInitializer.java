package ru.urabov.shopping.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.urabov.shopping.entity.RoleName;
import ru.urabov.shopping.service.UserService;

/**
 * Начальные данные: три роли и учётная запись администратора.
 * Все зарегистрированные пользователи получают только READ_ONLY,
 * поэтому первого администратора нужно создать при запуске.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (RoleName role : RoleName.values()) {
            userService.role(role);
        }
        if (!userService.exists(adminUsername)) {
            userService.create(adminUsername, adminPassword,
                    RoleName.ROLE_READ_ONLY, RoleName.ROLE_USER, RoleName.ROLE_ADMIN);
            log.info("Создан администратор {}", adminUsername);
        }
    }
}
