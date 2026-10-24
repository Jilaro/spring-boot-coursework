package ru.urabov.shopping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urabov.shopping.dto.ProductForm;
import ru.urabov.shopping.entity.Product;
import ru.urabov.shopping.entity.User;
import ru.urabov.shopping.repository.ProductRepository;
import ru.urabov.shopping.security.CurrentUser;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopService shopService;
    private final CurrentUser currentUser;
    private final ActionLogService actionLog;

    // ADMIN и READ_ONLY видят все покупки, USER – только свои
    @Transactional(readOnly = true)
    public List<Product> findVisible() {
        return currentUser.seesAll()
                ? productRepository.findAllByOrderByPurchaseDateDescIdDesc()
                : productRepository.findByOwnerOrderByPurchaseDateDescIdDesc(currentUser.get());
    }

    @Transactional(readOnly = true)
    public Product getForEdit(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Покупка с id = " + id + " не найдена"));
        if (!canModify(product)) {
            throw new AccessDeniedException("Нет прав на изменение покупки " + id);
        }
        return product;
    }

    @Transactional
    public Product save(ProductForm form) {
        User user = currentUser.get();
        Product product;
        if (form.getId() == null) {
            product = new Product();
            product.setOwner(user);
        } else {
            product = getForEdit(form.getId());
        }
        product.setName(form.getName().trim());
        product.setCategory(form.getCategory());
        product.setPrice(form.getPrice());
        product.setQuantity(form.getQuantity());
        product.setPurchaseDate(form.getPurchaseDate());
        // Магазин проверяется: USER может выбрать только свой магазин
        product.setShop(shopService.getForUse(form.getShopId()));
        Product saved = productRepository.save(product);
        actionLog.info(user.getUsername(), (form.getId() == null ? "добавил" : "изменил")
                + " покупку id = " + saved.getId() + ": " + saved.getName() + " × " + saved.getQuantity()
                + " по " + saved.getPrice() + " руб. в «" + saved.getShop().getName() + "»");
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Product product = getForEdit(id);
        productRepository.delete(product);
        actionLog.info(currentUser.username().orElse("?"), "удалил покупку id = " + id + ": " + product.getName());
    }

    public boolean canModify(Product product) {
        return currentUser.isAdmin()
                || (currentUser.canEdit() && product.getOwner().getId().equals(currentUser.get().getId()));
    }
}
