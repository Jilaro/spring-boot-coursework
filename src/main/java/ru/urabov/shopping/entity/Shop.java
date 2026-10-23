package ru.urabov.shopping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/**
 * Магазин – таблица SHOPS, характеристика основной таблицы PRODUCTS.
 */
@Entity
@Table(name = "shops")
@Getter
@Setter
@NoArgsConstructor
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название магазина
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * Город
     */
    @Column(nullable = false, length = 40)
    private String city;

    /**
     * Адрес: улица и дом
     */
    @Column(length = 100)
    private String address;

    /**
     * Пользователь, который добавил магазин
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public String getFullAddress() {
        return address == null || address.isBlank() ? city : city + ", " + address;
    }
}
