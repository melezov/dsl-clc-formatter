package com.dslplatform.compiler.client.formatter.impl;

import com.dslplatform.compiler.client.formatter.Formatter;

public class NoopFormatter implements Formatter {
    protected NoopFormatter() {}

    public static final NoopFormatter INSTANCE = new NoopFormatter();

    @Override
    public String format(final String context, final String body) {
        return body;
    }
}
