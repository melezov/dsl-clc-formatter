package com.dslplatform.compiler.client.formatter;

import com.dslplatform.compiler.client.formatter.util.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

enum FormatterFactory {
    INSTANCE;

    private static InputStream readResource(final String name) throws IOException {
        return FormatterFactory.class.getResourceAsStream(name);
    }

    private static Properties readProperties(final String name) throws IOException {
        final Properties properties = new Properties();
        properties.load(readResource(name));
        return properties;
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

    private static final String DSL_PLATFORM_NEXUS = "https://dsl-platform.com/nexus/content/groups/public/";

    private static final Dependencies csharpDependencies = new Dependencies(
            new ArtifactDownload(DSL_PLATFORM_NEXUS, "com.dslplatform.formatter", "dsl-clc-formatter-language-csharp", "0.2.1", 1524, "00175ef67a7959cc171583c2feb6f46926071981")
    );

    private static final Dependencies javaDependencies = new Dependencies(
            new ArtifactDownload(DSL_PLATFORM_NEXUS, "com.dslplatform.formatter", "dsl-clc-formatter-language-java", "0.2.1",                     2125, "bcf7789748a8f187453b0f90011a3e7d41e4ef11"),
            new ArtifactDownload(DSL_PLATFORM_NEXUS, "org.eclipse.equinox",       "org.eclipse.equinox.common",      "3.6.0.v20100503",         101957, "13c4a5fde7a4b976fe4c5621964881108d23b297"),
            new ArtifactDownload(DSL_PLATFORM_NEXUS, "org.eclipse.jdt",           "core",                            "3.10.0.v20140902-0626",  5565845, "647e19b28c106a63a14401c0f5956289792adf2f"),
            new ArtifactDownload(DSL_PLATFORM_NEXUS, "org.eclipse",               "text",                            "3.5.300.v20130515-1451",  249432, "53576e81d4ea46d7803c1b9fad43a43b5e24b025"),
            new ArtifactDownload(DSL_PLATFORM_NEXUS, "org.eclipse",               "jface",                           "3.10.1.v20140813-1009",  1159354, "cf5197a4a4015c3afe31b265c6aa552dacda6b35")
    );

    private static final Dependencies phpDependencies = new Dependencies(
            new ArtifactDownload(DSL_PLATFORM_NEXUS, "com.dslplatform.formatter", "dsl-clc-formatter-language-php", "0.2.1", 1515, "c4b2da4073e6f9525ea3fa18cb359f0ecaf779d4")
    );

    private static final Dependencies scala211Dependencies = new Dependencies(
            new ArtifactDownload(DSL_PLATFORM_NEXUS, "org.scala-lang",            "scala-library",                         "2.11.7", 5745606, "f75e7acabd57b213d6f61483240286c07213ec0e"),
            new ArtifactDownload(DSL_PLATFORM_NEXUS, "com.dslplatform.formatter", "dsl-clc-formatter-language-scala_2.11", "0.2.1",     2062, "6385182322cb40a969aa822e4115d44fa61a72f9"),
            new ArtifactDownload(DSL_PLATFORM_NEXUS, "org.scalariform",           "scalariform_2.11",                      "0.1.7",  1934192, "7e542ae64556a0908fd04c0edceb4fe95cbccea4"),
            new ArtifactDownload(DSL_PLATFORM_NEXUS, "org.scala-lang.modules",    "scala-xml_2.11",                        "1.0.5",   671138, "77ac9be4033768cf03cc04fbd1fc5e5711de2459")
    );

    private static final Dependencies sqlDependencies = new Dependencies(
            new ArtifactDownload(DSL_PLATFORM_NEXUS, "com.dslplatform.formatter", "dsl-clc-formatter-language-sql", "0.2.1", 1514, "a8cc83b87e9a349139fbded7cf029d57b714c33a")
    );

    @SuppressWarnings("unchecked")
    private Formatter createFormatter(final String language) {
        try {
            if (language.equals("cs")) {
                final Class<Formatter> clazz = (Class<Formatter>) csharpDependencies.getClassLoader()
                        .loadClass("com.dslplatform.compiler.client.formatter.util.CSharpCodeFormatter");

                final Formatter csharpCodeFormatter = clazz.getConstructor().newInstance();

                return new FormatterCombinator(
                        PatternFormatter.fromInputStream(readResource("csharp-clean.regex")),
                        csharpCodeFormatter,
                        new NewlineTrimFormatter("\r\n"));
            }

            if (language.equals("java")) {
                final Class<Formatter> clazz = (Class<Formatter>) javaDependencies.getClassLoader()
                        .loadClass("com.dslplatform.compiler.client.formatter.util.JavaCodeFormatter");

                final Formatter javaCodeFormatter = clazz.getConstructor(Properties.class, String.class)
                        .newInstance(readProperties("java-format.properties"), "\n");

                return new FormatterCombinator(
                        PatternFormatter.fromInputStream(readResource("java-clean.regex")),
                        javaCodeFormatter,
                        new NewlineTrimFormatter("\n"));
            }

            if (language.equals("php")) {
                final Class<Formatter> clazz = (Class<Formatter>) phpDependencies.getClassLoader()
                        .loadClass("com.dslplatform.compiler.client.formatter.util.PHPCodeFormatter");

                final Formatter phpCodeFormatter = clazz.getConstructor().newInstance();

                return new FormatterCombinator(
                        PatternFormatter.fromInputStream(readResource("php-clean.regex")),
                        phpCodeFormatter,
                        new NewlineTrimFormatter("\n"));
            }

            if (language.equals("scala")) {
                final Class<Formatter> clazz = (Class<Formatter>) scala211Dependencies.getClassLoader()
                        .loadClass("com.dslplatform.compiler.client.formatter.util.ScalaCodeFormatter");

                final Formatter scalaCodeFormatter = clazz.getConstructor(Properties.class, String.class, String.class)
                        .newInstance(readProperties("scala-format.properties"), "\n", "2.11.5");

                return new FormatterCombinator(
                        PatternFormatter.fromInputStream(readResource("scala-clean.regex")),
                        scalaCodeFormatter,
                        new NewlineTrimFormatter("\n"));
            }

            if (language.equals("sql")) {
                final Class<Formatter> clazz = (Class<Formatter>) sqlDependencies.getClassLoader()
                        .loadClass("com.dslplatform.compiler.client.formatter.util.SQLCodeFormatter");

                final Formatter sqlCodeFormatter = clazz.getConstructor().newInstance();

                return new FormatterCombinator(
                        PatternFormatter.fromInputStream(readResource("sql-clean.regex")),
                        sqlCodeFormatter,
                        new NewlineTrimFormatter("\n"));
            }

            return null;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
