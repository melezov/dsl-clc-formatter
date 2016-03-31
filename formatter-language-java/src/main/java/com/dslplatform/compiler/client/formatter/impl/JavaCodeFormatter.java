package com.dslplatform.compiler.client.formatter.impl;

import com.dslplatform.compiler.client.formatter.Formatter;
import com.dslplatform.compiler.client.formatter.Logger;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import java.util.Properties;

public class JavaCodeFormatter implements Formatter {
    private final Logger logger;
    private final CodeFormatter codeFormatter;
    private final String newline;

    public JavaCodeFormatter(final Logger logger, final Properties properties, final String newline) {
        this.logger = logger;
        this.codeFormatter = ToolFactory.createCodeFormatter(properties, ToolFactory.M_FORMAT_EXISTING);
        this.newline = newline;
    }

    @Override
    public String format(final String context, final String code) {
        logger.info("Formatting Java source: {}", context);

        final TextEdit edits = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS, code, 0, code.length(), 0, newline);
        if (edits == null || !edits.hasChildren()) {
            logger.debug("No need for format: {}", context);
            return code;
        }

        final Document document = new Document(code);
        try {
            edits.apply(document);
        } catch (final BadLocationException e) {
            logger.error("Could not apply edits to {}: {}", context, e);
            return code;
        }

        final String formatted = document.get();
        return code.equals(formatted) ? code : formatted;
    }
}
