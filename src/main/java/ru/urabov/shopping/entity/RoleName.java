package ru.urabov.shopping.entity;

/**
 * Роли приложения. Названия с префиксом ROLE_, как принято в Spring Security.
 */
public enum RoleName {

    ROLE_READ_ONLY("Только чтение"),
    ROLE_USER("Пользователь"),
    ROLE_ADMIN("Администратор");

    private final String title;

    RoleName(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
