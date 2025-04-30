package org.sctt.tools;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Command(name = "jpfy", mixinStandardHelpOptions = true, version = "jpfy 0.1",
        description = "Turns a bunch of java files into proper project")
class App implements Callable<Integer> {

    /**
     * Source root folder.
     */
    @Parameters(description = "Base folder of the files (source root).")
    private File file;

    @Override
    public Integer call() {
        System.out.println("Succeed in doing nothing for " + this.file);
        return 0;
    }

    @Command()
    public void list() {
        if (this.file.isDirectory()) {
            System.out.println(Arrays.toString(this.file.list()));
        } else {
            System.out.println("File: " + this.file.getAbsolutePath());
        }

    }

    public static void main(final String... args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
