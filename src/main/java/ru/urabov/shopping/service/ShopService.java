package ru.urabov.shopping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urabov.shopping.dto.ShopForm;
import ru.urabov.shopping.entity.Shop;
import ru.urabov.shopping.entity.User;
import ru.urabov.shopping.repository.ProductRepository;
import ru.urabov.shopping.repository.ShopRepository;
import ru.urabov.shopping.security.CurrentUser;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final CurrentUser currentUser;
    private final ActionLogService actionLog;

    // ADMIN и READ_ONLY видят все магазины, USER – только свои
    @Transactional(readOnly = true)
    public List<Shop> findVisible() {
        return currentUser.seesAll()
                ? shopRepository.findAllByOrderByNameAsc()
                : shopRepository.findByOwnerOrderByNameAsc(currentUser.get());
    }

    @Transactional(readOnly = true)
    public Shop getForEdit(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Магазин с id = " + id + " не найден"));
        checkCanModify(shop);
        return shop;
    }

    @Transactional(readOnly = true)
    public Shop getForUse(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Магазин с id = " + id + " не найден"));
        if (!currentUser.seesAll() && !isOwner(shop, currentUser.get())) {
            throw new AccessDeniedException("Нет доступа к магазину " + id);
        }
        return shop;
    }

    @Transactional
    public Shop save(ShopForm form) {
        User user = currentUser.get();
        Shop shop;
        if (form.getId() == null) {
            shop = new Shop();
            shop.setOwner(user);
        } else {
            shop = getForEdit(form.getId());
        }
        shop.setName(form.getName().trim());
        shop.setCity(form.getCity().trim());
        shop.setAddress(form.getAddress() == null ? null : form.getAddress().trim());
        Shop saved = shopRepository.save(shop);
        actionLog.info(user.getUsername(), (form.getId() == null ? "добавил" : "изменил")
                + " магазин id = " + saved.getId() + ": " + saved.getName());
        return saved;
    }

    // Возвращает сообщение об ошибке или null, если магазин удалён
    @Transactional
    public String delete(Long id) {
        Shop shop = getForEdit(id);
        long products = productRepository.countByShop(shop);
        if (products > 0) {
            actionLog.warn(currentUser.username().orElse("?"),
                    "пытался удалить магазин " + shop.getName() + ", в котором " + products + " покупок");
            return "Нельзя удалить магазин «" + shop.getName() + "»: в нём " + products + " покупок";
        }
        shopRepository.delete(shop);
        actionLog.info(currentUser.username().orElse("?"), "удалил магазин id = " + id + ": " + shop.getName());
        return null;
    }

    public boolean canModify(Shop shop) {
        return currentUser.isAdmin() || (currentUser.canEdit() && isOwner(shop, currentUser.get()));
    }

    private void checkCanModify(Shop shop) {
        if (!canModify(shop)) {
            throw new AccessDeniedException("Нет прав на изменение магазина " + shop.getId());
        }
    }

    private static boolean isOwner(Shop shop, User user) {
        return shop.getOwner().getId().equals(user.getId());
    }
}
