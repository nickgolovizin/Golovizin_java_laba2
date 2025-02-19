package ru.matytsin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Исключение, выбрасываемое в случае синтаксической ошибки в выражении
 */
@AllArgsConstructor
@Getter
public class ExpressionCannotBeResolved extends Throwable {
    private String message;
}
