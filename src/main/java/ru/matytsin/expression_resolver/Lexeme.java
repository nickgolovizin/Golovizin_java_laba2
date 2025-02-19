package ru.matytsin.expression_resolver;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Лексема пердставляет собой отдельную значимую единицу выражения
 */
@Getter
@ToString
@AllArgsConstructor
public class Lexeme {
    private LexemeType type;
    private String value;

    public Lexeme (LexemeType type, char value) {
        this.type = type;
        this.value = Character.toString(value);
    }

}

