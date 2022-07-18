package com.yiban.javaBase.dev.IO.net_io.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public enum Calculator {
    Instance;
    private final static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");

    public static void main(String[] args) {
        try {
            System.out.println(Calculator.Instance.cal("1+2"));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public Object cal(String expression) throws ScriptException {
        return jse.eval(expression);
    }
}
