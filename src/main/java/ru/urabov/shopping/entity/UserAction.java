package ru.urabov.shopping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Действие пользователя – таблица USER_ACTIONS (журнал действий).
 */
@Entity
@Table(name = "user_actions")
@Getter
@Setter
@NoArgsConstructor
public class UserAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата и время действия
     */
    @Column(name = "date_actions", nullable = false)
    private LocalDateTime dateActions;

    /**
     * Кто выполнил действие
     */
    @Column(nullable = false, length = 50)
    private String username;

    /**
     * Уровень: INFO – обычное действие, WARN – отказ или ошибка
     */
    @Column(nullable = false, length = 5)
    private String level;

    /**
     * Описание действия
     */
    @Column(nullable = false, length = 255)
    private String description;

    public UserAction(String username, String level, String description) {
        this.dateActions = LocalDateTime.now();
        this.username = username;
        this.level = level;
        this.description = description.length() > 255 ? description.substring(0, 255) : description;
    }
}
