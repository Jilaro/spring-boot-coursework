package ru.urabov.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urabov.shopping.entity.Shop;
import ru.urabov.shopping.entity.User;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    List<Shop> findAllByOrderByNameAsc();

    List<Shop> findByOwnerOrderByNameAsc(User owner);
}
