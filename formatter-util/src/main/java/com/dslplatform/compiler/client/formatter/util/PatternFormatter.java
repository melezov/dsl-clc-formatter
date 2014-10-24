package com.dslplatform.compiler.client.formatter.util;

import com.dslplatform.compiler.client.formatter.Formatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternFormatter implements Formatter {
    private final Pattern[] patterns;
    private final String[] replacements;

    private PatternFormatter(final Pattern[] patterns, final String[] replacements) {
        this.patterns = patterns;
        this.replacements = replacements;
    }

    public static PatternFormatter fromEntries(final List<Map.Entry<Pattern, String>> pairs) {
        final int count = pairs.size();

        final Pattern[] patterns = new Pattern[count];
        final String[] replacements = new String[count];

        int index = 0;
        for (final Map.Entry<Pattern, String> entry : pairs) {
            patterns[index] = entry.getKey();
            replacements[index] = entry.getValue();
            index++;
        }

        return new PatternFormatter(patterns, replacements);
    }

    private static Map.Entry<Pattern, String> parsePair(final String line) {
        final String trimmed = line.trim();
        if (trimmed.isEmpty()) return null;

        final int trimLen = trimmed.length();
        if (trimLen < 8) { // 'x'=>''
            throw new IllegalArgumentException("Invalid regex-replacement pair: " + line);
        }

        final String[] pair = trimmed.substring(1, trimLen - 1).split("'\\s*=>\\s*'", -1);
        if (pair.length != 2) { throw new IllegalArgumentException("Invalid regex-replacement pair: " + line); }

        final Pattern pattern = Pattern.compile(pair[0]);
        return new AbstractMap.SimpleEntry<Pattern, String>(pattern, pair[1]);
    }

    public static PatternFormatter fromInputStream(final InputStream propsStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(propsStream, "UTF-8"));
        final ArrayList<Map.Entry<Pattern, String>> pairs = new ArrayList<Map.Entry<Pattern, String>>();

        while (true) {
            final String line = br.readLine();
            if (line == null) return fromEntries(pairs);

            final Map.Entry<Pattern, String> pair = parsePair(line);
            if (pair != null) pairs.add(pair);
        }
    }

    @Override
    public String format(final String text) {
        StringBuffer sb = null;
        String work = text;

        for (int i = 0; i < patterns.length; i++) {
            final String replacement = replacements[i];
            final Matcher m = patterns[i].matcher(work);
            if (m.find()) {
                if (sb == null) {
                    sb = new StringBuffer();
                } else {
                    sb.setLength(0);
                }

                do {
                    m.appendReplacement(sb, replacement);
                } while (m.find());
                work = m.appendTail(sb).toString();
            }
        }

        return work;
    }
}
