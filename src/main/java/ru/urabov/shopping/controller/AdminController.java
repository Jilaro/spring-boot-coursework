package ru.urabov.shopping.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.urabov.shopping.security.CurrentUser;
import ru.urabov.shopping.service.UserService;

/**
 * Форма управления ролями, доступна только ADMIN (правило /admin/** в WebSecurityConfig).
 */
@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CurrentUser currentUser;

    @GetMapping
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin-users";
    }

    // Флажок без галочки в запрос не попадает, поэтому по умолчанию false
    @PostMapping("/roles")
    public String updateRoles(@RequestParam Long userId,
                              @RequestParam(defaultValue = "false") boolean userRole,
                              @RequestParam(defaultValue = "false") boolean adminRole,
                              RedirectAttributes redirect) {
        String admin = currentUser.username().orElseThrow();
        try {
            userService.updateRoles(userId, userRole, adminRole, admin);
            redirect.addFlashAttribute("message", "Роли сохранены");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
