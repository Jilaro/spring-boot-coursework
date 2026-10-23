package ru.urabov.shopping.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.urabov.shopping.dto.CalculationRequest;
import ru.urabov.shopping.dto.CalculationResult;
import ru.urabov.shopping.entity.Category;
import ru.urabov.shopping.entity.Product;
import ru.urabov.shopping.entity.Shop;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpenseCalculationServiceTest {

    private final ExpenseCalculationService service = new ExpenseCalculationService();

    private Shop magnit;
    private Shop dns;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        magnit = shop(1L, "Магнит");
        dns = shop(2L, "DNS");
        products = List.of(
                product("Молоко", Category.FOOD, "89.90", 2, "2026-10-01", magnit),       // 179.80
                product("Хлеб", Category.FOOD, "45.00", 1, "2026-10-05", magnit),         //  45.00
                product("Наушники", Category.ELECTRONICS, "2490.00", 1, "2026-10-10", dns), // 2490.00
                product("Кабель USB", Category.ELECTRONICS, "350.00", 2, "2026-09-28", dns)); // 700.00, вне периода
    }

    @Test
    @DisplayName("Сумма, количество и средние значения за период")
    void calculateTotals() {
        CalculationResult r = service.calculate(products, request("2026-10-01", "2026-10-31", null, null));

        assertThat(r.purchaseCount()).isEqualTo(3);
        assertThat(r.itemCount()).isEqualTo(4);
        assertThat(r.totalCost()).isEqualByComparingTo("2714.80");
        assertThat(r.averagePurchase()).isEqualByComparingTo("904.93");   // 2714.80 / 3
        assertThat(r.days()).isEqualTo(31);
        assertThat(r.averagePerDay()).isEqualByComparingTo("87.57");      // 2714.80 / 31
        assertThat(r.mostExpensive().getName()).isEqualTo("Наушники");
    }

    @Test
    @DisplayName("Границы периода включаются")
    void periodBoundsIncluded() {
        CalculationResult r = service.calculate(products, request("2026-10-01", "2026-10-01", null, null));

        assertThat(r.purchaseCount()).isEqualTo(1);
        assertThat(r.totalCost()).isEqualByComparingTo("179.80");
        assertThat(r.days()).isEqualTo(1);
    }

    @Test
    @DisplayName("Фильтр по магазину")
    void filterByShop() {
        CalculationResult r = service.calculate(products, request("2026-09-01", "2026-10-31", magnit.getId(), null));

        assertThat(r.purchaseCount()).isEqualTo(2);
        assertThat(r.totalCost()).isEqualByComparingTo("224.80");
        assertThat(r.byShop()).containsOnlyKeys("Магнит");
    }

    @Test
    @DisplayName("Разбивка по магазинам и категориям по убыванию суммы")
    void groupsSortedDescending() {
        CalculationResult r = service.calculate(products, request("2026-09-01", "2026-10-31", null, null));

        assertThat(r.byShop().keySet()).containsExactly("DNS", "Магнит");
        assertThat(r.byShop().get("DNS")).isEqualByComparingTo("3190.00");
        assertThat(r.byCategory().keySet()).containsExactly("Электроника", "Продукты питания");
        assertThat(r.byCategory().get("Продукты питания")).isEqualByComparingTo("224.80");
    }

    @Test
    @DisplayName("Остаток бюджета и доля расходов")
    void budgetLeft() {
        CalculationResult r = service.calculate(products, request("2026-10-01", "2026-10-31", null, "5000"));

        assertThat(r.budgetLeft()).isEqualByComparingTo("2285.20");
        assertThat(r.budgetUsedPercent()).isEqualByComparingTo("54.3");
        assertThat(r.overBudget()).isFalse();
    }

    @Test
    @DisplayName("Перерасход бюджета")
    void overBudget() {
        CalculationResult r = service.calculate(products, request("2026-10-01", "2026-10-31", null, "2000"));

        assertThat(r.budgetLeft()).isEqualByComparingTo("-714.80");
        assertThat(r.budgetUsedPercent()).isEqualByComparingTo("135.7");
        assertThat(r.overBudget()).isTrue();
        assertThat(r.budgetBarPercent()).isEqualByComparingTo("100");
    }

    @Test
    @DisplayName("Пустой период: нули и нет самой дорогой покупки")
    void emptyPeriod() {
        CalculationResult r = service.calculate(products, request("2026-01-01", "2026-01-31", null, null));

        assertThat(r.purchaseCount()).isZero();
        assertThat(r.totalCost()).isEqualByComparingTo("0");
        assertThat(r.averagePurchase()).isEqualByComparingTo("0");
        assertThat(r.mostExpensive()).isNull();
        assertThat(r.byShop()).isEmpty();
    }

    @Test
    @DisplayName("Конец периода раньше начала – исключение")
    void invalidPeriod() {
        assertThatThrownBy(() -> service.calculate(products, request("2026-10-31", "2026-10-01", null, null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static CalculationRequest request(String from, String to, Long shopId, String budget) {
        return new CalculationRequest(LocalDate.parse(from), LocalDate.parse(to), shopId,
                budget == null ? null : new BigDecimal(budget));
    }

    private static Shop shop(Long id, String name) {
        Shop shop = new Shop();
        shop.setId(id);
        shop.setName(name);
        shop.setCity("Екатеринбург");
        return shop;
    }

    private static Product product(String name, Category category, String price, int qty, String date, Shop shop) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setPrice(new BigDecimal(price));
        p.setQuantity(qty);
        p.setPurchaseDate(LocalDate.parse(date));
        p.setShop(shop);
        return p;
    }
}
