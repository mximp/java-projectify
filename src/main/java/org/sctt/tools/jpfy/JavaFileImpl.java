package org.sctt.tools.jpfy;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Java file implementation.
 */
public final class JavaFileImpl implements JavaFile {

    /**
     * Source path.
     */
    private final Path src;

    /**
     * File source path.
     *
     * @param source path to a source file.
     */
    public JavaFileImpl(final Path source) {
        this.src = source;
    }

    @Override
    public Jpkg pkg() {
        String pkg = "";
        try (BufferedReader reader =
                     Files.newBufferedReader(this.src, StandardCharsets.UTF_8)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("package")) {
                    String[] parts = line.split("\\s+|;");
                    pkg = parts[parts.length > 1 ? 1 : 0].trim();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read source file", e);
        }
        return new JpkgImpl(pkg);
    }
}
