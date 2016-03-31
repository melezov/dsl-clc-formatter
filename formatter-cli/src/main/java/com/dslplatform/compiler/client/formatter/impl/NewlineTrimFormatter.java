package com.dslplatform.compiler.client.formatter.impl;

import com.dslplatform.compiler.client.formatter.Formatter;
import com.dslplatform.compiler.client.formatter.Logger;

public class NewlineTrimFormatter implements Formatter {
    private final Logger logger;
    private final String newline;

    public NewlineTrimFormatter(final Logger logger, final String newline) {
        this.logger = logger;
        this.newline = newline;
    }

    @Override
    public String format(final String context, final String body) {
        logger.debug("Trimming {} ...", context);

        final int nlen = newline.length();
        final int blen = body.length();
        if (nlen == 0 || blen == 0) {
            return body;
        }

        int start = 0;
        while (body.startsWith(newline, start)) {
            start += nlen;
        }

        if (start == blen) {
            return start == nlen ? body : newline;
        }

        int end = blen;
        while (end >= nlen && body.startsWith(newline, end - nlen)) {
            end -= nlen;
        }

        return end == blen - nlen
                ? body.substring(start)
                : end == blen
                        ? body.substring(start) + newline
                        : body.substring(start, end + nlen);
    }
}
