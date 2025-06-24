package org.sctt.tools.jpfy;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Package backed by a string.
 */
public final class JpkgImpl implements Jpkg {

    /**
     * Package string.
     */
    private final String pkgName;

    /**
     * Ctor.
     *
     * @param pkg Dot-separated package name e.g., "com.example.myapp".
     */
    public JpkgImpl(final String pkg) {
        this.pkgName = pkg;
    }

    @Override
    public Path asPath() {
        return Paths.get(pkgName.replace('.', '/'));
    }
}
