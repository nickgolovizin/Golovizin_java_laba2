package ru.matytsin.expression_resolver;

import ru.matytsin.exception.ExpressionCannotBeResolved;
import ru.matytsin.exception.UnexpectedSymbol;
import ru.matytsin.exception.UnknownVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @autor Matytsin Alexander
 *
 * Класс, позволяющий решать простейщие математические выражение, записанные в виде строки.
 * Поддерживает операции: +, -, /, *, (, ).
 * Присутсвует поддрежка переменных в выражении
 */
public class ExpressionResolver {

    private String expression;
    private Map<String, Double> variables;
    private List<Lexeme> lexemes;
    private LexemeBuffer lb;

    /**
     * Конструктор с параметрами
     *
     * @param expression математическое выражение, записанное в виде строки
     * @param variables переменные, используемые в выражении. Записаны в хэш-таблицу в виде пары имя_переменной-ее_значение
     */
    public ExpressionResolver(String expression, Map<String, Double> variables) {
        this.expression = expression;
        this.variables = variables;
    }

    /**
     * Сокрытый метод, парсящий строку с выражение
     *
     * @param expression выражение
     * @param variables переменные
     * @return Список лексем в выражении
     * @throws UnexpectedSymbol
     * @throws UnknownVariable
     */
    private static List<Lexeme> parseExpression(String expression, Map<String, Double> variables)
            throws UnexpectedSymbol, UnknownVariable {
        List<Lexeme> lexemes = new ArrayList<>();
        int i = 0;

        while (i < expression.length()) {
            char symbol = expression.charAt(i);

            switch (symbol) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, symbol));
                    ++i;
                    break;

                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, symbol));
                    ++i;
                    break;

                case '+':
                    lexemes.add(new Lexeme(LexemeType.PLUS, symbol));
                    ++i;
                    break;

                case '-':
                    lexemes.add(new Lexeme(LexemeType.MINUS, symbol));
                    ++i;
                    break;

                case '*':
                    lexemes.add(new Lexeme(LexemeType.MULT, symbol));
                    ++i;
                    break;

                case '/':
                    lexemes.add(new Lexeme(LexemeType.DIV, symbol));
                    ++i;
                    break;

                default: {
                    if (symbol >= '0' && symbol <= '9') {
                        String number = "";

                        do {
                            number += symbol;
                            ++i;

                            if (i >= expression.length())
                                break;

                            symbol = expression.charAt(i);
                        } while (symbol >= '0' && symbol <= '9');

                        lexemes.add(new Lexeme(LexemeType.NUMBER, number));
                    }
                    else if (symbol >= 'a' && symbol <= 'z') {
                        String var = "";

                        do {
                            var += symbol;
                            ++i;

                            if (i >= expression.length())
                                break;

                            symbol = expression.charAt(i);
                        } while (symbol >= 'a' && symbol <= 'b');

                        if (!variables.containsKey(var))
                            throw new UnknownVariable("Unknown variable: " + var);

                        lexemes.add(new Lexeme(LexemeType.NUMBER, variables.get(var).toString()));
                    }
                    else {
                        if (symbol != ' ')
                            throw new UnexpectedSymbol("Unexpected symbol: " + symbol);

                        ++i;
                    }
                }
            }
        }

        lexemes.add(new Lexeme(LexemeType.EOE, ""));

        return lexemes;
    }


    /**
     * Вспомогательный внутренний класс.
     * Предназначен для итерации по лексемам
     */
    private static class LexemeBuffer {
        private List<Lexeme> lexemes;
        private int pos;

        public LexemeBuffer(List<Lexeme> lexemes) {
            this.lexemes = lexemes;
            pos = 0;
        }

        public Lexeme next() {
            return lexemes.get(pos++);
        }

        public void back() {
            pos--;
        }

        public int getPos() {
            return pos;
        }
    }

    private static double expr(LexemeBuffer lexemes) throws ExpressionCannotBeResolved {
        Lexeme lexeme = lexemes.next();
        if (lexeme.getType() == LexemeType.EOE) {
            return 0;
        }
        else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }

    private static double plusminus(LexemeBuffer lexemes) throws ExpressionCannotBeResolved {
        double value = multdiv(lexemes);

        while (true) {
            Lexeme lexeme = lexemes.next();

            switch (lexeme.getType()) {
                case PLUS:
                    value += multdiv(lexemes);
                    break;

                case MINUS:
                    value -= multdiv(lexemes);
                    break;

                case EOE:
                case RIGHT_BRACKET:
                    lexemes.back();
                    return value;

                default:
                    throw new ExpressionCannotBeResolved("This Expression cannot be resolved");
            }
        }
    }

    private static double multdiv(LexemeBuffer lexemes) throws ExpressionCannotBeResolved {
        double value = factor(lexemes);

        while (true) {
            Lexeme lexeme = lexemes.next();

            switch (lexeme.getType()) {
                case MULT:
                    value *= factor(lexemes);
                    break;

                case DIV:
                    value /= factor(lexemes);
                    break;

                case EOE:
                case RIGHT_BRACKET:
                case PLUS:
                case MINUS:
                    lexemes.back();
                    return value;

                default:
                    throw new ExpressionCannotBeResolved("This Expression cannot be resolved");
            }
        }
    }

    private static double factor(LexemeBuffer lexemes) throws ExpressionCannotBeResolved {
        Lexeme lexeme = lexemes.next();

        switch (lexeme.getType()) {
            case NUMBER:
                return Double.parseDouble(lexeme.getValue());

            case LEFT_BRACKET:
                double value = plusminus(lexemes);
                lexeme = lexemes.next();

                if (lexeme.getType() != LexemeType.RIGHT_BRACKET)
                    throw new ExpressionCannotBeResolved("This Expression cannot be resolved");
                return value;

            default:
                throw new ExpressionCannotBeResolved("This Expression cannot be resolved");
        }
    }


    /**
     * Вычисляет математическое выражение согласно алгоритму, учитывая переменные
     * и перехватывая исключения
     *
     * @return результат вычисления выражения
     */
    public static double resolveExpression(String expression, Map<String, Double> variables) {
        try {
            List<Lexeme> lexemes = parseExpression(expression, variables);
            LexemeBuffer lb = new LexemeBuffer(lexemes);

            try {
                double answer = expr(lb);
                return answer;
            } catch (ExpressionCannotBeResolved e) {
                System.out.println(e.getMessage());
            }
        } catch (UnexpectedSymbol e) {
            System.out.println(e.getMessage());
        } catch (UnknownVariable e) {
            System.out.println(e.getMessage());
        }

        return 0D;
    }
}


