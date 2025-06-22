package org.sctt.tools.jpfy;

import java.nio.file.Path;

/**
 * Java file implementation.
 */
public final class JavaFileImpl implements JavaFile {

    /**
     * Source path.
     */
    private final Path source;

    /**
     * File source path.
     *
     * @param source path to a source file.
     */
    public JavaFileImpl(final Path source) {
        this.source = source;
    }

    @Override
    public Jpkg pkg() {
        return null;
    }
}
