# Учёт покупок

Курсовой проект по дисциплине «Технологии программирования», вариант 2: список покупок (Product) + список магазинов, стоимость продуктов.
Выполнил: Урабов Николай Михайлович, группа РИЗ-250213у.

Spring Boot 4.1.2, Java 25, Spring MVC, Spring Security, Spring Data JPA (Hibernate), H2, Thymeleaf, Bootstrap 5, Lombok, Actuator, Maven.

## Запуск

Запустить `ShoppingListApplication`, открыть http://localhost:8080/.
Таблицы `users`, `roles`, `users_roles`, `shops`, `products` Hibernate создаёт сам.
При первом запуске создаётся администратор **admin / admin123** (задаётся в `application.properties`).

Логи пишутся в консоль и в файл `logs/shopping.log`.

## Роли

| Роль | Права |
|------|-------|
| READ_ONLY | просмотр всех записей и расчёт; выдаётся при регистрации |
| USER | добавление, изменение и удаление своих записей; видит только свои записи |
| ADMIN | полный доступ ко всем записям, назначение ролей, Actuator |

## Страницы

| URL | Форма |
|-----|-------|
| `/` | главная |
| `/login`, `/register` | вход и регистрация |
| `/products`, `/products/new` | список и форма покупок |
| `/shops`, `/shops/new` | список и форма магазинов |
| `/calculate` | расчёт расходов за период |
| `/admin/users` | назначение ролей (ADMIN) |
| `/about` | о приложении |
| `/actuator/health`, `/actuator/metrics`, `/actuator/logfile` … | мониторинг (ADMIN) |

## Тесты

`ExpenseCalculationServiceTest` – unit-тесты расчёта расходов.
