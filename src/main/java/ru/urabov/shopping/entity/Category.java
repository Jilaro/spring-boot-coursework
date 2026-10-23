package ru.urabov.shopping.entity;

/**
 * Категория покупки.
 */
public enum Category {

    FOOD("Продукты питания"),
    HOUSEHOLD("Бытовые товары"),
    ELECTRONICS("Электроника"),
    CLOTHES("Одежда и обувь"),
    HEALTH("Здоровье и красота"),
    OTHER("Прочее");

    private final String title;

    Category(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
