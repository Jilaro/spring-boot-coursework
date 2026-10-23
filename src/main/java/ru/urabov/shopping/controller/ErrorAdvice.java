package ru.urabov.shopping.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Запись не найдена или передан неверный параметр – показывается страница с сообщением.
 * AccessDeniedException здесь не перехватывается: её обрабатывает Spring Security.
 */
@Slf4j
@ControllerAdvice
public class ErrorAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView notFound(IllegalArgumentException e) {
        log.warn("Ошибка запроса: {}", e.getMessage());
        return new ModelAndView("not-found").addObject("message", e.getMessage());
    }
}
