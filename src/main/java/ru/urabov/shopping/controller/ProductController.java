package ru.urabov.shopping.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.urabov.shopping.dto.ProductForm;
import ru.urabov.shopping.entity.Category;
import ru.urabov.shopping.entity.Product;
import ru.urabov.shopping.service.ProductService;
import ru.urabov.shopping.service.ShopService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ShopService shopService;

    @GetMapping
    public String list(Model model) {
        List<Product> products = productService.findVisible();
        model.addAttribute("products", products);
        model.addAttribute("total", products.stream().map(Product::getCost).reduce(BigDecimal.ZERO, BigDecimal::add));
        // id записей, которые текущий пользователь может изменить
        model.addAttribute("editableIds", products.stream().filter(productService::canModify).map(Product::getId).toList());
        return "products";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        ProductForm form = new ProductForm();
        form.setPurchaseDate(LocalDate.now());
        form.setQuantity(1);
        return form(model, form);
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam Long id, Model model) {
        return form(model, ProductForm.from(productService.getForEdit(id)));
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("form") ProductForm form, BindingResult result, Model model,
                       RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return form(model, form);
        }
        productService.save(form);
        redirect.addFlashAttribute("message", "Покупка «" + form.getName() + "» сохранена");
        return "redirect:/products";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirect) {
        productService.delete(id);
        redirect.addFlashAttribute("message", "Покупка удалена");
        return "redirect:/products";
    }

    private String form(Model model, ProductForm form) {
        model.addAttribute("form", form);
        model.addAttribute("categories", Category.values());
        model.addAttribute("shops", shopService.findVisible());
        return "product-form";
    }
}
