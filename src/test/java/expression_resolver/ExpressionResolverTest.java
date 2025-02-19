package expression_resolver;

import org.junit.jupiter.api.Test;
import ru.matytsin.expression_resolver.ExpressionResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ExpressionResolverTest {

    @Test
    void resolveExpression() {

        List<String> expressions = new ArrayList<>();
        Map<String, Double> var = new HashMap<>();

        var.put("x", 10.5);
        var.put("y", 20.1);

        expressions.add("12 + 34 * 9 - 100 + (34 - 78) / 81");  // ~217
        expressions.add("");                                    // =0
        expressions.add("Hello!");                              // =0
        expressions.add("12 + 34 * 9 - 128 + )34 - 78( / 81");  // =0
        expressions.add("          2 + 3");                     // =5
        expressions.add("126  + 100                - 923 / 4"); // ~-4
        expressions.add("x - 46 * (y + 4) / 2");                // ~-543

        int[] right_answers = { 217, 0, 0, 0, 5, -4, -543 };
        int[] answers = new int[7];

        int i = 0;
        for (String expr : expressions) {
            answers[i++] = (int) ExpressionResolver.resolveExpression(expr, var);
        }

        assertArrayEquals(right_answers, answers);
    }
}