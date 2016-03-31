package com.dslplatform.compiler.client.formatter.impl;

import com.dslplatform.compiler.client.formatter.Formatter;
import com.dslplatform.compiler.client.formatter.Logger;

public class SQLCodeFormatter implements Formatter {
    private final Logger logger;

    public SQLCodeFormatter(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public String format(final String context, final String code) {
        logger.info("Formatting SQL script: {}", context);
        return code;
    }
}
