package ru.matytsin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Обнаружена неизвестная переменная
 */
@AllArgsConstructor
@Getter
public class UnknownVariable extends Throwable {
    private String message;
}
