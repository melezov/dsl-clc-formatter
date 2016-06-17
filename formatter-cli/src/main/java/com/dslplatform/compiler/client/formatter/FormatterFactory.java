package com.dslplatform.compiler.client.formatter;

import com.dslplatform.compiler.client.formatter.impl.FormatterCombinator;
import com.dslplatform.compiler.client.formatter.impl.NewlineTrimFormatter;
import com.dslplatform.compiler.client.formatter.impl.NoopFormatter;
import com.dslplatform.compiler.client.formatter.impl.PatternFormatter;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FormatterFactory {
    private final Logger logger;

    public FormatterFactory(final Logger logger) {
        this.logger = logger;
    }

    private InputStream readResource(final String name) throws IOException {
        final String override = System.getProperty(name);
        if (override != null) {
            logger.debug("Reading from \"{}\" override: {}", name, override);
            return new FileInputStream(override);
        }
        return FormatterFactory.class.getResourceAsStream("defaults/" + name);
    }

    private Properties readProperties(final String name) throws IOException {
        final Properties properties = new Properties();
        properties.load(readResource(name));
        return properties;
    }

    private static final String SCALA_VERSION = "2.11.8";

    private static final Pattern repositoryPattern = Pattern.compile("\"([^\"]+)\" +at +\"([^\"]+)\"");
    private static final Pattern artifactPattern = Pattern.compile("\"([^\"]+)\" +% +\"([^\"]+)\" +% +\"([^\"]+)\" +// +(\\d+) bytes, SHA-1: ([a-f0-9]{40})");

    public Dependencies readDependencies(final String name) throws IOException {
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(readResource(name), "UTF-8"));

            final String repositoryLine = br.readLine();
            final Matcher repositoryMatcher = repositoryPattern.matcher(repositoryLine);
            if (!repositoryMatcher.find()) {
                throw new IOException("Could not parse repository: " + repositoryLine);
            }
            final Repository repository = new Repository(repositoryMatcher.group(1), new URI(repositoryMatcher.group(2)));

            final List<ArtifactDownload> artifacts = new ArrayList<ArtifactDownload>();
            while (true) {
                final String artifactLine = br.readLine();
                if (artifactLine == null) break;
                if (artifactLine.isEmpty()) continue;

                final Matcher artifactMatcher = artifactPattern.matcher(artifactLine);
                if (!artifactMatcher.find()) {
                    throw new IOException("Could not parse artifact: " + artifactLine);
                }
                artifacts.add(new ArtifactDownload(
                        logger,
                        repository,
                        artifactMatcher.group(1),
                        artifactMatcher.group(2),
                        artifactMatcher.group(3),
                        Integer.parseInt(artifactMatcher.group(4)),
                        artifactMatcher.group(5)));
            }
            return new Dependencies(logger, artifacts);
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private final Map<String, Formatter> formatters = new HashMap<String, Formatter>();

    public Formatter getFormatter(final String language) {
        final String lowerLang = language.toLowerCase(Locale.ENGLISH);

        synchronized (formatters) {
            final Formatter cachedFormatter = formatters.get(lowerLang);
            if (cachedFormatter != null) return cachedFormatter;

            final Formatter formatter = createFormatter(lowerLang);
            formatters.put(lowerLang, formatter);

            return formatter;
        }
    }

    @SuppressWarnings("unchecked")
    private Formatter createFormatter(final String language) {
        try {
            logger.debug("Creating formatter for language: {}", language);
            if (language.equals("cs")) {
                final Class<Formatter> clazz = (Class<Formatter>) readDependencies("csharp-dependencies.sbt").getClassLoader()
                        .loadClass("com.dslplatform.compiler.client.formatter.impl.CSharpCodeFormatter");

                final Formatter csharpCodeFormatter = clazz.getConstructor(Logger.class).newInstance(logger);

                return new FormatterCombinator(logger,
                        PatternFormatter.fromInputStream(logger, readResource("csharp-clean.regex")),
                        csharpCodeFormatter,
                        PatternFormatter.fromInputStream(logger, readResource("csharp-post.regex")),
                        new NewlineTrimFormatter(logger, "\r\n"));
            }

            if (language.equals("java")) {
                final Class<Formatter> clazz = (Class<Formatter>) readDependencies("java-dependencies.sbt").getClassLoader()
                        .loadClass("com.dslplatform.compiler.client.formatter.impl.JavaCodeFormatter");

                final Formatter javaCodeFormatter = clazz.getConstructor(Logger.class, Properties.class, String.class)
                        .newInstance(logger, readProperties("java-format.properties"), "\n");

                return new FormatterCombinator(logger,
                        PatternFormatter.fromInputStream(logger, readResource("java-clean.regex")),
                        javaCodeFormatter,
                        PatternFormatter.fromInputStream(logger, readResource("java-post.regex")),
                        new NewlineTrimFormatter(logger, "\n"));
            }

            if (language.equals("php")) {
                final Class<Formatter> clazz = (Class<Formatter>) readDependencies("php-dependencies.sbt").getClassLoader()
                        .loadClass("com.dslplatform.compiler.client.formatter.impl.PHPCodeFormatter");

                final Formatter phpCodeFormatter = clazz.getConstructor(Logger.class).newInstance(logger);

                return new FormatterCombinator(logger,
                        PatternFormatter.fromInputStream(logger, readResource("php-clean.regex")),
                        phpCodeFormatter,
                        PatternFormatter.fromInputStream(logger, readResource("php-post.regex")),
                        new NewlineTrimFormatter(logger, "\n"));
            }

            if (language.equals("scala")) {
                final Class<Formatter> clazz = (Class<Formatter>) readDependencies("scala-dependencies.sbt").getClassLoader()
                        .loadClass("com.dslplatform.compiler.client.formatter.impl.ScalaCodeFormatter");

                final Formatter scalaCodeFormatter = clazz.getConstructor(Logger.class, Properties.class, String.class, String.class)
                        .newInstance(logger, readProperties("scala-format.properties"), "\n", SCALA_VERSION);

                return new FormatterCombinator(logger,
                        PatternFormatter.fromInputStream(logger, readResource("scala-clean.regex")),
                        scalaCodeFormatter,
                        PatternFormatter.fromInputStream(logger, readResource("scala-post.regex")),
                        new NewlineTrimFormatter(logger, "\n"));
            }

            if (language.equals("sql")) {
                final Class<Formatter> clazz = (Class<Formatter>) readDependencies("sql-dependencies.sbt").getClassLoader()
                        .loadClass("com.dslplatform.compiler.client.formatter.impl.SQLCodeFormatter");

                final Formatter sqlCodeFormatter = clazz.getConstructor(Logger.class).newInstance(logger);
                return new FormatterCombinator(logger,
                        PatternFormatter.fromInputStream(logger, readResource("sql-clean.regex")),
                        sqlCodeFormatter,
                        PatternFormatter.fromInputStream(logger, readResource("sql-post.regex")),
                        new NewlineTrimFormatter(logger, "\n"));
            }

            logger.error("Language {} is not supported!", language);
            return NoopFormatter.INSTANCE;
        } catch (final Exception e) {
            logger.error("Could not create formatter for language {}: {}", language, e);
            return NoopFormatter.INSTANCE;
        }
    }
}
