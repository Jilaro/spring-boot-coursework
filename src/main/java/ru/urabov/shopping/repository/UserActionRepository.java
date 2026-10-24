package ru.urabov.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urabov.shopping.entity.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    List<UserAction> findAllByOrderByDateActionsDescIdDesc();
}
