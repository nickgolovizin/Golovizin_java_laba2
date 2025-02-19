package ru.matytsin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * В выражении обнаружен неизвестный символ
 */
@AllArgsConstructor
@Getter
public class UnexpectedSymbol extends Throwable {
    private String message;
}
