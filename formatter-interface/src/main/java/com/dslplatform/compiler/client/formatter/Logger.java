package com.dslplatform.compiler.client.formatter;

public interface Logger {
    void debug(final String format, final Object... params);
    void info(final String format, final Object... params);
    void error(final String format, final Object... params);
}
