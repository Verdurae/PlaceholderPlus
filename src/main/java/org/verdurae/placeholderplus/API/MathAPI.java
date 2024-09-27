package org.verdurae.placeholderplus.API;

public class MathAPI {
    public static <T extends Number> Number calculate(T number, String formula) {
        double v = number.doubleValue();
        if (formula.startsWith("+")) {
            return v + Double.parseDouble(formula.substring(1));
        } else if (formula.startsWith("-")) {
            return v - Double.parseDouble(formula.substring(1));
        } else if (formula.startsWith("*")) {
            return v * Double.parseDouble(formula.substring(1));
        } else if (formula.startsWith("/")) {
            return v / Double.parseDouble(formula.substring(1));
        }
        return null;
    }
}
