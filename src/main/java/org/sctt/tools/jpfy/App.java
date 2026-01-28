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
@Command(name = "jpfy", mixinStandardHelpOptions = true, version = "jpfy 0.0.2-SNAPSHOT",
        description = "Turns a bunch of java files into proper project")
public final class App implements Callable<Integer> {

    /**
     * Source root folder.
     */
    @Parameters(description = "Base folder of the files (source root).")
    private Path src;

    /**
     * Whether to copy non-Java files alongside Java files in their respective packages.
     */
    @CommandLine.Option(names = {"--copy-resources"}, description = "Copy non-Java files to their corresponding package directories")
    private boolean copyResources = false;

    @Override
    public Integer call() {
        final Map<Path, Path> move = new HashMap<>();
        try (Stream<Path> paths = Files.walk(this.src)) {
            paths.filter(Files::isRegularFile)
                .forEach(f -> {
                    if (f.getFileName().toString().endsWith(".java")) {
                        // Handle Java files - organize by package
                        move.put(
                            f,
                            this.src.resolve(new JavaFileImpl(f).pkg().asPath())
                                .resolve(f.getFileName())
                        );
                    } else if (copyResources) {
                        // Handle non-Java files if --copy-resources is enabled
                        // Try to find corresponding Java file in same directory
                        String fileNameWithoutExt = f.getFileName().toString().replaceAll("\\.([^.]*)$", "");
                        Path correspondingJavaFile = f.getParent().resolve(fileNameWithoutExt + ".java");

                        if (Files.exists(correspondingJavaFile)) {
                            // If there's a corresponding Java file, place it in the same package directory
                            Path javaDestination = this.src.resolve(new JavaFileImpl(correspondingJavaFile).pkg().asPath());
                            move.put(f, javaDestination.resolve(f.getFileName()));
                        } else {
                            // If no corresponding Java file, check if it's in a package directory
                            try (Stream<Path> javaFiles = Files.walk(this.src, 1).filter(p -> p.toString().endsWith(".java"))) {
                                javaFiles.findFirst().ifPresent(jf -> {
                                    // Use the package of the first Java file we find
                                    Path javaDestination = this.src.resolve(new JavaFileImpl(jf).pkg().asPath());
                                    move.put(f, javaDestination.resolve(f.getFileName()));
                                });
                            } catch (Exception e) {
                                // If we can't process, skip this file
                                System.err.println("Warning: Could not determine destination for resource file: " + f);
                            }
                        }
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        if (move.isEmpty()) {
            System.err.println("No files found to process in: " + src);
            return 1;
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
