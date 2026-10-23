package ru.urabov.shopping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Покупка – основная таблица PRODUCTS.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Наименование товара
     */
    @Column(nullable = false, length = 60)
    private String name;

    /**
     * Категория товара
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;

    /**
     * Цена за единицу, руб.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Количество
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Дата покупки
     */
    @Column(nullable = false)
    private LocalDate purchaseDate;

    /**
     * Магазин, в котором сделана покупка
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    /**
     * Пользователь, который добавил покупку
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * Стоимость покупки: цена × количество
     */
    public BigDecimal getCost() {
        return price == null || quantity == null ? BigDecimal.ZERO : price.multiply(BigDecimal.valueOf(quantity));
    }
}
