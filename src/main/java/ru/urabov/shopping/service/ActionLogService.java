package ru.urabov.shopping.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.urabov.shopping.entity.UserAction;
import ru.urabov.shopping.repository.UserActionRepository;

import java.util.List;

/**
 * Журнал действий пользователей.
 * Каждое действие пишется в таблицу USER_ACTIONS и в лог-файл logs/shopping.log.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActionLogService {

    private final UserActionRepository actionRepository;

    // REQUIRES_NEW: запись сохраняется, даже если основная транзакция откатится (например, при отказе в доступе)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void info(String username, String description) {
        log.info("{} {}", username, description);
        actionRepository.save(new UserAction(username, "INFO", description));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void warn(String username, String description) {
        log.warn("{} {}", username, description);
        actionRepository.save(new UserAction(username, "WARN", description));
    }

    @Transactional(readOnly = true)
    public List<UserAction> findAll() {
        return actionRepository.findAllByOrderByDateActionsDescIdDesc();
    }

    // События Spring Security: вход, неудачный вход и выход
    @EventListener
    public void onLogin(AuthenticationSuccessEvent event) {
        info(event.getAuthentication().getName(), "вошёл в систему");
    }

    @EventListener
    public void onLoginFailure(AbstractAuthenticationFailureEvent event) {
        warn(event.getAuthentication().getName(), "неудачная попытка входа");
    }

    @EventListener
    public void onLogout(LogoutSuccessEvent event) {
        info(event.getAuthentication().getName(), "вышел из системы");
    }
}
