package ru.urabov.shopping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Данные формы регистрации.
 */
@Getter
@Setter
@NoArgsConstructor
public class RegistrationForm {

    @NotBlank(message = "Укажите логин")
    @Size(min = 3, max = 50, message = "Логин от 3 до 50 символов")
    @Pattern(regexp = "[A-Za-z0-9._-]*", message = "Латинские буквы, цифры, точка, дефис и подчёркивание")
    private String username;

    @NotBlank(message = "Укажите пароль")
    @Size(min = 6, max = 64, message = "Пароль от 6 до 64 символов")
    private String password;

    @NotBlank(message = "Повторите пароль")
    private String confirmPassword;

}
