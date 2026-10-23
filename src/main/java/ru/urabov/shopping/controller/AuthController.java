package ru.urabov.shopping.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.urabov.shopping.dto.RegistrationForm;
import ru.urabov.shopping.service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult result) {
        if (form.getPassword() != null && !form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Пароли не совпадают");
        }
        if (form.getUsername() != null && !form.getUsername().isBlank() && userService.exists(form.getUsername())) {
            result.rejectValue("username", "duplicate", "Пользователь с таким логином уже есть");
        }
        if (result.hasErrors()) {
            return "register";
        }
        userService.register(form);
        return "redirect:/login?registered";
    }
}
