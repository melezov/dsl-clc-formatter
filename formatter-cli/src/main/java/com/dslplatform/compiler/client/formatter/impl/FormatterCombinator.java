package com.dslplatform.compiler.client.formatter.impl;

import com.dslplatform.compiler.client.formatter.Formatter;
import com.dslplatform.compiler.client.formatter.Logger;

public class FormatterCombinator implements Formatter {
    private final Logger logger;
    private final Formatter[] formatters;

    public FormatterCombinator(final Logger logger, final Formatter... formatters) {
        this.logger = logger;
        this.formatters = formatters;
    }

    @Override
    public String format(final String context, final String body) {
        logger.debug("Applying {} formatters on {} ...", formatters.length, context);

        String work = body;
        for (final Formatter formatter : formatters) {
            try {
                work = formatter.format(context, work);
            } catch (final Exception e) {
                logger.error("Caught exception thrown by {}: {}, skipping ...", formatter, e);
            }
        }

        logger.debug("Finished applying formatters on {}", context);
        return work;
    }
}
