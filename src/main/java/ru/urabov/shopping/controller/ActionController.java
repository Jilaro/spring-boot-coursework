package ru.urabov.shopping.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.urabov.shopping.service.ActionLogService;

/**
 * Форма просмотра логов – журнал действий пользователей, доступна только ADMIN.
 */
@Controller
@RequiredArgsConstructor
public class ActionController {

    private final ActionLogService actionLogService;

    @GetMapping("/admin/actions")
    public String actions(Model model) {
        model.addAttribute("actions", actionLogService.findAll());
        return "actions";
    }
}
