package ru.urabov.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urabov.shopping.entity.Product;
import ru.urabov.shopping.entity.Shop;
import ru.urabov.shopping.entity.User;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByOrderByPurchaseDateDescIdDesc();

    List<Product> findByOwnerOrderByPurchaseDateDescIdDesc(User owner);

    long countByShop(Shop shop);
}
