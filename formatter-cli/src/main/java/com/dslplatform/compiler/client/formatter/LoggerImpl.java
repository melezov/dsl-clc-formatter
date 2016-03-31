package com.dslplatform.compiler.client.formatter;

public class LoggerImpl implements Logger {
    @Override
    public void debug(String format, Object... params) {
//        System.out.println(String.format("DEBUG: " + format.replace("{}", "%s"), params));
    }

    @Override
    public void info(String format, Object... params) {
        System.out.println(String.format("INFO: " + format.replace("{}", "%s"), params));
    }

    @Override
    public void error(String format, Object... params) {
        System.out.println(String.format("ERROR: " + format.replace("{}", "%s"), params));

        for (final Object param : params) {
            if (param instanceof Exception) {
                ((Exception) param).printStackTrace();
            }
        }
    }
}
