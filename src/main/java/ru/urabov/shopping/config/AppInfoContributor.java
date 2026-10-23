package ru.urabov.shopping.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Сведения о приложении для /actuator/info. Заданы в коде, а не в application.properties,
 * так как properties-файл читается в кодировке ISO-8859-1 и кириллица в нём искажается.
 */
@Component
public class AppInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("app", Map.of(
                "name", "Учёт покупок",
                "description", "Курсовой проект по дисциплине «Технологии программирования»",
                "author", "Урабов Н.М., РИЗ-250213у",
                "version", "1.0"));
    }
}
