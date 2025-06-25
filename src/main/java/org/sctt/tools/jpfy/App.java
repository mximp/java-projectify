package org.sctt.tools.jpfy;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

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
    private Path src;

    @Override
    public Integer call() {
        final Map<Path, Path> move = new HashMap<>();
        try (Stream<Path> paths = Files.walk(this.src)) {
            paths.filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().endsWith(".java"))
                .forEach(f ->
                    move.put(
                        f,
                        this.src.resolve(new JavaFileImpl(f).pkg().asPath())
                            .resolve(f.getFileName())
                    )
                );
        } catch (Exception e) {
            e.printStackTrace();
        }

        move.entrySet().forEach(System.out::println);

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
