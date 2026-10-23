package ru.urabov.shopping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.urabov.shopping.entity.Shop;

/**
 * Данные формы магазина.
 */
@Getter
@Setter
@NoArgsConstructor
public class ShopForm {

    private Long id;

    @NotBlank(message = "Укажите название магазина")
    @Size(max = 50, message = "Не больше 50 символов")
    private String name;

    @NotBlank(message = "Укажите город")
    @Size(max = 40, message = "Не больше 40 символов")
    private String city;

    @Size(max = 100, message = "Не больше 100 символов")
    private String address;

    public static ShopForm from(Shop shop) {
        ShopForm form = new ShopForm();
        form.setId(shop.getId());
        form.setName(shop.getName());
        form.setCity(shop.getCity());
        form.setAddress(shop.getAddress());
        return form;
    }
}
