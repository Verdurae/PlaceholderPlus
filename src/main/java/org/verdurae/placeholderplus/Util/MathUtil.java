package org.verdurae.placeholderplus.Util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MathUtil {
    public static <T extends Number> Number calculate(String formula) {
        if (formula == null || formula.isEmpty()) {
            throw new IllegalArgumentException("空算式");
        }

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        try {
            formula = formula.replaceAll("\\s+", "");
            Object result = engine.eval(formula);
            if (result instanceof Number) {
                return (Number) result;
            } else {
                throw new IllegalArgumentException("算式结果不合法 反馈报错: " + result);
            }
        } catch (ScriptException e) {
            throw new IllegalArgumentException("无效算式 反馈报错: " + formula, e);
        }
    }


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
        return v;
    }
}
