package com.dslplatform.compiler.client.formatter;

import java.net.URI;

public class Repository {
    public final String name;
    public final URI uri;

    public Repository(
            final String name,
            final URI uri) {
        this.name = name;
        this.uri = uri;
    }
}
