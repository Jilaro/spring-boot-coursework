package ru.urabov.shopping.service;

import org.springframework.stereotype.Service;
import ru.urabov.shopping.dto.CalculationRequest;
import ru.urabov.shopping.dto.CalculationResult;
import ru.urabov.shopping.entity.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Расчёт расходов за период. Класс не обращается к базе: получает список покупок
 * и параметры расчёта, поэтому его легко проверить unit-тестами.
 */
@Service
public class ExpenseCalculationService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public CalculationResult calculate(List<Product> products, CalculationRequest request) {
        if (request.getDateTo().isBefore(request.getDateFrom())) {
            throw new IllegalArgumentException("Конец периода раньше начала");
        }

        // Покупки за период и, если выбран, в одном магазине
        List<Product> selected = products.stream()
                .filter(p -> !p.getPurchaseDate().isBefore(request.getDateFrom()))
                .filter(p -> !p.getPurchaseDate().isAfter(request.getDateTo()))
                .filter(p -> request.getShopId() == null || p.getShop().getId().equals(request.getShopId()))
                .toList();

        BigDecimal total = selected.stream().map(Product::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        int items = selected.stream().mapToInt(Product::getQuantity).sum();
        // Обе границы включаются: с 1 по 31 число – 31 день
        long days = ChronoUnit.DAYS.between(request.getDateFrom(), request.getDateTo()) + 1;

        BigDecimal averagePurchase = selected.isEmpty()
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(selected.size()), 2, RoundingMode.HALF_UP);
        BigDecimal averagePerDay = total.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);

        Product mostExpensive = selected.stream().max(Comparator.comparing(Product::getCost)).orElse(null);

        BigDecimal budget = request.getBudget();
        BigDecimal budgetLeft = budget == null ? null : budget.subtract(total);
        BigDecimal usedPercent = budget == null
                ? null
                : total.multiply(HUNDRED).divide(budget, 1, RoundingMode.HALF_UP);

        return new CalculationResult(selected.size(), items, total, averagePurchase, averagePerDay, days,
                mostExpensive,
                sumBy(selected, p -> p.getShop().getName()),
                sumBy(selected, p -> p.getCategory().getTitle()),
                budget, budgetLeft, usedPercent);
    }

    // Сумма расходов по группам, по убыванию суммы
    private static Map<String, BigDecimal> sumBy(List<Product> products, Function<Product, String> key) {
        return products.stream()
                .collect(Collectors.groupingBy(key, Collectors.reducing(BigDecimal.ZERO, Product::getCost, BigDecimal::add)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }
}
