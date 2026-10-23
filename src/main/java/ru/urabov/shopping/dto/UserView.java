package ru.urabov.shopping.dto;

import java.time.LocalDateTime;

/**
 * Строка формы управления ролями.
 */
public record UserView(Long id, String username, LocalDateTime registeredAt,
                       boolean user, boolean admin, String rolesTitle) {
}
