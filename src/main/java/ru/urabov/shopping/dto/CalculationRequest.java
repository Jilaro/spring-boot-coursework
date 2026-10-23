package ru.urabov.shopping.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Параметры расчёта расходов: период, магазин (необязательно) и бюджет на период (необязательно).
 */
@Getter
@Setter
@NoArgsConstructor
public class CalculationRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private Long shopId;

    private BigDecimal budget;

    public CalculationRequest(LocalDate dateFrom, LocalDate dateTo, Long shopId, BigDecimal budget) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.shopId = shopId;
        this.budget = budget;
    }
}
