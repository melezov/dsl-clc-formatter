package com.dslplatform.compiler.client.formatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

public class FileIterator implements Iterable<File> {
    private static File resolveFile(final File file) {
        try {
            return file.getCanonicalFile();
        } catch (final IOException e) {
            return file.getAbsoluteFile();
        }
    }

    private final File root;
    private final Pattern pattern;

    public FileIterator(final File root, final Pattern pattern) {
        this.root = root;
        this.pattern = pattern;
    }

    public Iterator<File> iterator() {
        return new Iterator<File>() {
            public void remove() {}

            private final ArrayDeque<File> stack = new ArrayDeque<File>(); {
                stack.add(resolveFile(root));
            }

            private boolean needsLocate = true;
            private File nextFile = null;

            private void locateNext() {
                while (true) {
                    final File file = stack.poll();
                    if (file == null) {
                        nextFile = null;
                        return;
                    }

                    if (file.isFile()) {
                        if (!pattern.matcher(file.getPath()).matches()) continue;
                        nextFile = file;
                        return;
                    }

                    if (file.isDirectory()) {
                        final File[] files = file.listFiles();
                        if (files != null) stack.addAll(Arrays.asList(files));
                    }
                }
            }

            @Override
            public boolean hasNext() {
                if (needsLocate) {
                    locateNext();
                    needsLocate = false;
                }
                return nextFile != null;
            }

            @Override
            public File next() {
                if (needsLocate) {
                    locateNext();
                } else {
                    needsLocate = true;
                }
                return nextFile;
            }
        };
    }
}
