package ru.urabov.shopping.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.urabov.shopping.entity.Category;
import ru.urabov.shopping.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Данные формы покупки. Владелец в форму не попадает: его задаёт сервер.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProductForm {

    private Long id;

    @NotBlank(message = "Укажите наименование")
    @Size(max = 60, message = "Не больше 60 символов")
    private String name;

    @NotNull(message = "Выберите категорию")
    private Category category;

    @NotNull(message = "Укажите цену")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше нуля")
    @Digits(integer = 8, fraction = 2, message = "Не больше 8 цифр до запятой и 2 после")
    private BigDecimal price;

    @NotNull(message = "Укажите количество")
    @Min(value = 1, message = "Количество от 1 до 10 000")
    @Max(value = 10000, message = "Количество от 1 до 10 000")
    private Integer quantity;

    @NotNull(message = "Укажите дату покупки")
    @PastOrPresent(message = "Дата покупки не может быть в будущем")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate purchaseDate;

    @NotNull(message = "Выберите магазин")
    private Long shopId;

    public static ProductForm from(Product product) {
        ProductForm form = new ProductForm();
        form.setId(product.getId());
        form.setName(product.getName());
        form.setCategory(product.getCategory());
        form.setPrice(product.getPrice());
        form.setQuantity(product.getQuantity());
        form.setPurchaseDate(product.getPurchaseDate());
        form.setShopId(product.getShop().getId());
        return form;
    }
}
