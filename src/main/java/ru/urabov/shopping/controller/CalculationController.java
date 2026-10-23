package ru.urabov.shopping.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.urabov.shopping.dto.CalculationRequest;
import ru.urabov.shopping.dto.CalculationResult;
import ru.urabov.shopping.security.CurrentUser;
import ru.urabov.shopping.service.ExpenseCalculationService;
import ru.urabov.shopping.service.ProductService;
import ru.urabov.shopping.service.ShopService;

import java.time.LocalDate;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CalculationController {

    private final ExpenseCalculationService calculationService;
    private final ProductService productService;
    private final ShopService shopService;
    private final CurrentUser currentUser;

    // Форма отправляется GET-запросом: параметры расчёта видны в адресе, страницу можно обновить
    @GetMapping("/calculate")
    public String calculate(@ModelAttribute("form") CalculationRequest form, BindingResult result,
                            Model model) {
        model.addAttribute("shops", shopService.findVisible());
        boolean submitted = form.getDateFrom() != null || form.getDateTo() != null;
        if (!submitted) {
            // Первое открытие: по умолчанию текущий месяц
            LocalDate today = LocalDate.now();
            form.setDateFrom(today.withDayOfMonth(1));
            form.setDateTo(today);
            return "calculate";
        }
        // Проверка параметров вручную: при первом открытии формы ошибок быть не должно
        if (form.getDateFrom() == null) {
            result.rejectValue("dateFrom", "required", "Укажите начало периода");
        }
        if (form.getDateTo() == null) {
            result.rejectValue("dateTo", "required", "Укажите конец периода");
        } else if (form.getDateFrom() != null && form.getDateTo().isBefore(form.getDateFrom())) {
            result.rejectValue("dateTo", "range", "Конец периода раньше начала");
        }
        if (form.getBudget() != null && form.getBudget().signum() <= 0) {
            result.rejectValue("budget", "positive", "Бюджет должен быть больше нуля");
        }
        if (result.hasErrors()) {
            return "calculate";
        }
        CalculationResult calc = calculationService.calculate(productService.findVisible(), form);
        log.info("{} выполнил расчёт за {} – {}: {} покупок на {} руб.", currentUser.username().orElse("?"),
                form.getDateFrom(), form.getDateTo(), calc.purchaseCount(), calc.totalCost());
        model.addAttribute("result", calc);
        return "calculate";
    }
}
