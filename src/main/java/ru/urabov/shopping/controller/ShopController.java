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
import ru.urabov.shopping.dto.ShopForm;
import ru.urabov.shopping.entity.Shop;
import ru.urabov.shopping.service.ShopService;

import java.util.List;

@Controller
@RequestMapping("/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public String list(Model model) {
        List<Shop> shops = shopService.findVisible();
        model.addAttribute("shops", shops);
        model.addAttribute("editableIds", shops.stream().filter(shopService::canModify).map(Shop::getId).toList());
        return "shops";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new ShopForm());
        return "shop-form";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam Long id, Model model) {
        model.addAttribute("form", ShopForm.from(shopService.getForEdit(id)));
        return "shop-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("form") ShopForm form, BindingResult result,
                       RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "shop-form";
        }
        shopService.save(form);
        redirect.addFlashAttribute("message", "Магазин «" + form.getName() + "» сохранён");
        return "redirect:/shops";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirect) {
        String error = shopService.delete(id);
        if (error == null) {
            redirect.addFlashAttribute("message", "Магазин удалён");
        } else {
            redirect.addFlashAttribute("error", error);
        }
        return "redirect:/shops";
    }
}
