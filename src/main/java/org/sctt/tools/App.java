package org.sctt.tools;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Entry point class for the app.
 */
@Command(name = "jpfy", mixinStandardHelpOptions = true, version = "jpfy 0.1",
        description = "Turns a bunch of java files into proper project")
public final class App implements Callable<Integer> {

    /**
     * Source root folder.
     */
    @Parameters(description = "Base folder of the files (source root).")
    private File file;

    @Override
    public Integer call() {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Not a source folder");
        }

        Arrays.stream(this.file.list((f, s) -> s.endsWith(".java"))).forEach(
                System.out::println
        );

        return 0;
    }

    /**
     * Main method.
     * @param args Arguments
     */
    public static void main(final String... args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
