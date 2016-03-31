package com.dslplatform.compiler.client.formatter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Dependencies {
    private final Logger logger;
    private final List<ArtifactDownload> artifacts;

    public Dependencies(
            final Logger logger,
            final List<ArtifactDownload> artifacts) {
        this.logger = logger;
        this.artifacts = artifacts;
    }

    public URLClassLoader getClassLoader() throws IOException {
        final ExecutorService executorService = Executors.newFixedThreadPool(4);

        final List<Future<File>> downloads = new ArrayList<Future<File>>();
        for (final ArtifactDownload artifact : artifacts) {
            downloads.add(executorService.submit(artifact.getDownload()));
        }

        try {
            final List<URL> localCache = new ArrayList<URL>();
            for (final Future<File> download : downloads) {
                localCache.add(download.get().toURI().toURL());
            }

            return new URLClassLoader(
                    localCache.toArray(new URL[localCache.size()]),
                    Dependencies.class.getClassLoader());
        } catch (final InterruptedException e) {
            throw new IOException(e);
        } catch (final ExecutionException e) {
            throw new IOException(e);
        } finally {
            executorService.shutdown();
        }
    }
}
