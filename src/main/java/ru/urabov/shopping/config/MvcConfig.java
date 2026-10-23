package ru.urabov.shopping.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.urabov.shopping.security.CurrentUser;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    private final CurrentUser currentUser;

    // Страницы без логики
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/about").setViewName("about");
        registry.addViewController("/access-denied").setViewName("access-denied");
    }

    // В каждую страницу передаются данные о текущем пользователе – по ним строится меню
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response,
                                   Object handler, ModelAndView mav) {
                if (mav == null || (mav.getViewName() != null && mav.getViewName().startsWith("redirect:"))) {
                    return;
                }
                boolean loggedIn = currentUser.username().isPresent();
                mav.addObject("currentUser", currentUser.username().orElse(null));
                mav.addObject("roleTitle", loggedIn ? currentUser.roleTitle() : null);
                mav.addObject("isAdmin", loggedIn && currentUser.isAdmin());
                mav.addObject("canEdit", loggedIn && currentUser.canEdit());
            }
        });
    }
}
