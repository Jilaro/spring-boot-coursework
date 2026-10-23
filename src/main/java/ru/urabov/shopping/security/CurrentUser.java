package ru.urabov.shopping.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.urabov.shopping.entity.RoleName;
import ru.urabov.shopping.entity.User;
import ru.urabov.shopping.repository.UserRepository;

import java.util.Optional;

/**
 * Текущий пользователь и его права на записи.
 * <p>
 * ADMIN видит и изменяет все записи, USER – только свои, READ_ONLY видит все записи, но ничего не изменяет.
 */
@Component
@RequiredArgsConstructor
public class CurrentUser {

    private final UserRepository userRepository;

    public Optional<String> username() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return Optional.of(auth.getName());
    }

    public User get() {
        String name = username().orElseThrow(() -> new IllegalStateException("Пользователь не вошёл в систему"));
        return userRepository.findByUsername(name)
                .orElseThrow(() -> new IllegalStateException("Пользователь " + name + " не найден"));
    }

    public boolean has(RoleName role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role.name()));
    }

    public boolean isAdmin() {
        return has(RoleName.ROLE_ADMIN);
    }

    // Может ли пользователь добавлять и изменять записи
    public boolean canEdit() {
        return has(RoleName.ROLE_ADMIN) || has(RoleName.ROLE_USER);
    }

    // Видит ли пользователь записи всех пользователей
    public boolean seesAll() {
        return isAdmin() || !has(RoleName.ROLE_USER);
    }

    // Главная роль для вывода в меню
    public String roleTitle() {
        if (isAdmin()) {
            return RoleName.ROLE_ADMIN.getTitle();
        }
        return has(RoleName.ROLE_USER) ? RoleName.ROLE_USER.getTitle() : RoleName.ROLE_READ_ONLY.getTitle();
    }
}
