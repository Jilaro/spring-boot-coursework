package ru.urabov.shopping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urabov.shopping.dto.RegistrationForm;
import ru.urabov.shopping.dto.UserView;
import ru.urabov.shopping.entity.Role;
import ru.urabov.shopping.entity.RoleName;
import ru.urabov.shopping.entity.User;
import ru.urabov.shopping.repository.RoleRepository;
import ru.urabov.shopping.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActionLogService actionLog;

    @Transactional(readOnly = true)
    public boolean exists(String username) {
        return userRepository.existsByUsername(normalize(username));
    }

    // Регистрация: по умолчанию у нового пользователя только роль READ_ONLY
    @Transactional
    public User register(RegistrationForm form) {
        User user = create(form.getUsername(), form.getPassword(), RoleName.ROLE_READ_ONLY);
        actionLog.info(user.getUsername(), "зарегистрировался, выдана роль READ_ONLY");
        return user;
    }

    @Transactional
    public User create(String username, String rawPassword, RoleName... roles) {
        User user = new User();
        user.setUsername(normalize(username));
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRegisteredAt(LocalDateTime.now());
        for (RoleName role : roles) {
            user.getRoles().add(role(role));
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserView> findAll() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getRegisteredAt))
                .map(UserService::toView)
                .toList();
    }

    // Форма ролей: READ_ONLY есть всегда, USER и ADMIN добавляются или снимаются флажками
    @Transactional
    public void updateRoles(Long userId, boolean userRole, boolean adminRole, String changedBy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id = " + userId + " не найден"));
        if (user.getUsername().equals(changedBy) && !adminRole) {
            throw new IllegalArgumentException("Нельзя снять роль ADMIN с самого себя");
        }
        user.getRoles().clear();
        user.getRoles().add(role(RoleName.ROLE_READ_ONLY));
        if (userRole) {
            user.getRoles().add(role(RoleName.ROLE_USER));
        }
        if (adminRole) {
            user.getRoles().add(role(RoleName.ROLE_ADMIN));
        }
        actionLog.info(changedBy, "изменил роли пользователя " + user.getUsername()
                + ": USER = " + userRole + ", ADMIN = " + adminRole);
    }

    // Роль создаётся при первом обращении
    @Transactional
    public Role role(RoleName name) {
        return roleRepository.findByName(name).orElseGet(() -> roleRepository.save(new Role(name)));
    }

    private static UserView toView(User user) {
        String titles = user.getRoles().stream()
                .map(Role::getName)
                .sorted()
                .map(RoleName::getTitle)
                .collect(Collectors.joining(", "));
        return new UserView(user.getId(), user.getUsername(), user.getRegisteredAt(),
                user.hasRole(RoleName.ROLE_USER), user.hasRole(RoleName.ROLE_ADMIN), titles);
    }

    private static String normalize(String username) {
        return username.trim().toLowerCase();
    }
}
