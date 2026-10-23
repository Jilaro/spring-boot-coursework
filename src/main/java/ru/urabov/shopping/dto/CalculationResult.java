package ru.urabov.shopping.dto;

import ru.urabov.shopping.entity.Product;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Результат расчёта расходов за период.
 *
 * @param purchaseCount   количество покупок
 * @param itemCount       количество единиц товара
 * @param totalCost       общая сумма расходов
 * @param averagePurchase средняя стоимость покупки
 * @param averagePerDay   средний расход в день
 * @param days            длина периода в днях
 * @param mostExpensive   самая дорогая покупка или null
 * @param byShop          сумма по магазинам, по убыванию
 * @param byCategory      сумма по категориям, по убыванию
 * @param budget          бюджет или null
 * @param budgetLeft      остаток бюджета (отрицательный – перерасход) или null
 * @param budgetUsedPercent доля бюджета в процентах или null
 */
public record CalculationResult(
        int purchaseCount,
        int itemCount,
        BigDecimal totalCost,
        BigDecimal averagePurchase,
        BigDecimal averagePerDay,
        long days,
        Product mostExpensive,
        Map<String, BigDecimal> byShop,
        Map<String, BigDecimal> byCategory,
        BigDecimal budget,
        BigDecimal budgetLeft,
        BigDecimal budgetUsedPercent) {

    // Ширина полосы бюджета на странице: не больше 100 %
    public BigDecimal budgetBarPercent() {
        return budgetUsedPercent == null ? BigDecimal.ZERO : budgetUsedPercent.min(BigDecimal.valueOf(100));
    }

    public boolean overBudget() {
        return budgetLeft != null && budgetLeft.signum() < 0;
    }
}
