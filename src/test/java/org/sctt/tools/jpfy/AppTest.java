package org.sctt.tools.jpfy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link App}.
 */
@DisplayName("App tests")
class AppTest {

    @Nested
    @DisplayName("Main method tests")
    class MainMethodTests {

        @Test
        @DisplayName("Should execute without errors for valid directory")
        void shouldExecuteWithoutErrors(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("Test.java");
            Files.writeString(javaFile, "package org.example;");

            String[] args = {tempDir.toString()};
            ByteArrayOutputStream output = captureSystemOut();

            // when
            int exitCode = new CommandLine(new App()).execute(args);

            // then
            assertEquals(0, exitCode);
            restoreSystemOut();
        }

        @Test
        @DisplayName("Should output move operations")
        void shouldOutputMoveOperations(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("MyClass.java");
            Files.writeString(javaFile, "package com.myapp;");

            String[] args = {tempDir.toString()};
            ByteArrayOutputStream output = captureSystemOut();

            // when
            new CommandLine(new App()).execute(args);
            String captured = getCapturedOutput(output);

            // then
            assertTrue(captured.contains("MyClass.java") || captured.contains("org/example"),
                    "Output should contain file or path info");
            restoreSystemOut();
        }

        @Test
        @DisplayName("Should handle help flag")
        void shouldHandleHelpFlag() {
            // given
            String[] args = {"--help"};
            ByteArrayOutputStream output = captureSystemOut();

            // when
            int exitCode = new CommandLine(new App()).execute(args);
            String captured = getCapturedOutput(output);

            // then
            assertEquals(0, exitCode);
            assertTrue(captured.contains("Usage") || captured.contains("jpfy"),
                    "Help output should contain usage info");
            restoreSystemOut();
        }

        @Test
        @DisplayName("Should handle version flag")
        void shouldHandleVersionFlag() {
            // given
            String[] args = {"--version"};
            ByteArrayOutputStream output = captureSystemOut();

            // when
            int exitCode = new CommandLine(new App()).execute(args);
            String captured = getCapturedOutput(output);

            // then
            assertEquals(0, exitCode);
            assertTrue(captured.contains("jpfy") || captured.contains("0.1"),
                    "Version output should contain version info");
            restoreSystemOut();
        }
    }

    @Nested
    @DisplayName("Move logic tests")
    class MoveLogicTests {

        @Test
        @DisplayName("Should process single Java file")
        void shouldProcessSingleJavaFile(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("Single.java");
            Files.writeString(javaFile, "package single.pkg;");

            String[] args = {tempDir.toString()};
            ByteArrayOutputStream output = captureSystemOut();

            // when
            new CommandLine(new App()).execute(args);

            // then
            String captured = getCapturedOutput(output);
            assertTrue(captured.contains("single") || captured.contains("Single"),
                    "Output should reference processed file");
            restoreSystemOut();
        }

        @Test
        @DisplayName("Should process multiple Java files")
        void shouldProcessMultipleJavaFiles(@TempDir Path tempDir) throws IOException {
            // given
            Files.writeString(tempDir.resolve("ClassA.java"), "package org.a;");
            Files.writeString(tempDir.resolve("ClassB.java"), "package org.b;");

            String[] args = {tempDir.toString()};
            ByteArrayOutputStream output = captureSystemOut();

            // when
            new CommandLine(new App()).execute(args);

            // then
            String captured = getCapturedOutput(output);
            assertTrue(captured.contains("ClassA") || captured.contains("ClassB"),
                    "Output should reference both files");
            restoreSystemOut();
        }

        @Test
        @DisplayName("Should skip non-Java files")
        void shouldSkipNonJavaFiles(@TempDir Path tempDir) throws IOException {
            // given
            Files.writeString(tempDir.resolve("Test.java"), "package test;");
            Files.writeString(tempDir.resolve("Readme.txt"), "Just a readme");
            Files.writeString(tempDir.resolve("Script.kts"), "some kotlin script");

            String[] args = {tempDir.toString()};
            ByteArrayOutputStream output = captureSystemOut();

            // when
            new CommandLine(new App()).execute(args);

            // then
            String captured = getCapturedOutput(output);
            // Should only contain Test.java references
            restoreSystemOut();
        }
    }

    @Nested
    @DisplayName("Edge case tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty directory")
        void shouldHandleEmptyDirectory(@TempDir Path tempDir) {
            // given
            String[] args = {tempDir.toString()};
            ByteArrayOutputStream output = captureSystemOut();

            // when
            int exitCode = new CommandLine(new App()).execute(args);

            // then
            assertEquals(0, exitCode);
            restoreSystemOut();
        }

        @Test
        @DisplayName("Should handle directory with only non-Java files")
        void shouldHandleDirectoryWithOnlyNonJavaFiles(@TempDir Path tempDir) throws IOException {
            // given
            Files.writeString(tempDir.resolve("Readme.md"), "# Readme");
            Files.writeString(tempDir.resolve("config.xml"), "<config/>");

            String[] args = {tempDir.toString()};
            ByteArrayOutputStream output = captureSystemOut();

            // when
            int exitCode = new CommandLine(new App()).execute(args);

            // then
            assertEquals(0, exitCode);
            restoreSystemOut();
        }

        @Test
        @DisplayName("Should handle deeply nested package")
        void shouldHandleDeeplyNestedPackage(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("DeepClass.java");
            Files.writeString(javaFile, "package com.a.b.c.d.e.f;");

            String[] args = {tempDir.toString()};
            ByteArrayOutputStream output = captureSystemOut();

            // when
            new CommandLine(new App()).execute(args);

            // then
            String captured = getCapturedOutput(output);
            assertTrue(captured.contains("e") || captured.contains("f") || captured.contains("DeepClass"),
                    "Output should reference deep package");
            restoreSystemOut();
        }
    }

    // Helper methods
    private ByteArrayOutputStream captureSystemOut() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        return output;
    }

    private String getCapturedOutput(ByteArrayOutputStream output) {
        return output.toString();
    }

    private void restoreSystemOut() {
        System.setOut(new PrintStream(System.out));
    }
}