package com.dslplatform.compiler.client.formatter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private final Logger logger;
    private final CodeIO codeIO;
    private final FormatterFactory formatterFactory;

    public Main(final Logger logger) {
        this.logger = logger;
        this.codeIO = new CodeIO(logger, Charset.forName("UTF-8"));
        this.formatterFactory = new FormatterFactory(logger);
    }

    private void process(final Formatter formatter, final File file) throws IOException {
        final Code code = codeIO.read(file);
        final String formatted = formatter.format(file.getAbsolutePath(), code.body);
        codeIO.write(code, formatted);
    }

    private void format(final File file) {
        final Pattern extensionMatch = Pattern.compile("^.*?\\.([^.]+)$");
        final Matcher extensionMatcher = extensionMatch.matcher(file.getName());

        if (extensionMatcher.find()) {
            final String extension = extensionMatcher.group(1).toLowerCase(Locale.ENGLISH);
            final Formatter formatter = formatterFactory.getFormatter(extension);

            if (formatter != null) {
                try {
                    process(formatter, file);
                    return;
                } catch (final Exception e) {
                    logger.error("Caught exception when formatting {}: {}", file, e);
                }
            }
        }
    }

    public static void main(final String[] args) {
        final Pattern sourcePattern = Pattern.compile(".*\\.(cs|java|php|scala|sql)");
        final Main main = new Main(new LoggerImpl());

        for (final String arg : args) {
            final File file = new File(arg);
            if (file.isDirectory()) {
                for (final File current : new FileIterator(file, sourcePattern)) {
                    main.format(current);
                }
            } else if (file.isFile()) {
                main.format(file);
            }
        }
    }
}
