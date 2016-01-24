package com.dslplatform.compiler.client.formatter.util;

import com.dslplatform.compiler.client.formatter.Formatter;

public class FormatterCombinator implements Formatter {
    private final Formatter[] formatters;

    public FormatterCombinator(final Formatter... formatters) {
        this.formatters = formatters;
    }

    @Override
    public String format(final String body) {
        String work = body;
        for (final Formatter formatter : formatters) {
            try {
                work = formatter.format(work);
            }
            catch (final Exception e) {
                System.err.println(e);
            }
        }
        return work;
    }
}
