package com.dslplatform.compiler.client.formatter.impl;

import com.dslplatform.compiler.client.formatter.Formatter;
import com.dslplatform.compiler.client.formatter.Logger;

public class CSharpCodeFormatter implements Formatter {
    private final Logger logger;

    public CSharpCodeFormatter(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public String format(final String context, final String code) {
        logger.info("Formatting C# source: {}", context);
        return code;
    }
}
