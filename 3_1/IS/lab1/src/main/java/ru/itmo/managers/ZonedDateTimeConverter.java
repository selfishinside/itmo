package ru.itmo.managers;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JSF-конвертер для отображения `ZonedDateTime` в удобочитаемом формате
 * и обратного преобразования из строки.
 * Регистрируется под именем "zonedDateTimeConverter".
 */
@FacesConverter("zonedDateTimeConverter")
public class ZonedDateTimeConverter implements Converter<ZonedDateTime> {

    // Формат вывода/ввода
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    /**
     * Преобразует строку в `ZonedDateTime`.
     * @param context контекст Faces
     * @param component компонент UI
     * @param value строковое представление даты
     * @return `ZonedDateTime` или null
     */
    @Override
    public ZonedDateTime getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return ZonedDateTime.parse(value, FORMATTER);
    }

    /**
     * Преобразует `ZonedDateTime` в строку согласно формату.
     * @param context контекст Faces
     * @param component компонент UI
     * @param value объект даты
     * @return форматированная строка или пустая строка
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, ZonedDateTime value) {
        if (value == null) {
            return "";
        }
        return value.format(FORMATTER);
    }
}
