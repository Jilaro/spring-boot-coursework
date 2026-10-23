package ru.urabov.shopping;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

// Отдельная база в памяти и без файла логов, чтобы тест не трогал рабочие данные
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "logging.file.name="})
class ShoppingListApplicationTests {

    @Test
    void contextLoads() {
    }
}
