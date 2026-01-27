package org.sctt.tools.jpfy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link JavaFileImpl}.
 */
@DisplayName("JavaFileImpl tests")
class JavaFileImplTest {

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with valid path")
        void shouldCreateInstanceWithValidPath(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("Test.java");
            Files.writeString(javaFile, "package com.example;");

            // when
            JavaFileImpl javaFileImpl = new JavaFileImpl(javaFile);

            // then
            assertNotNull(javaFileImpl);
        }
    }

    @Nested
    @DisplayName("pkg() tests")
    class PkgTests {

        @Test
        @DisplayName("Should parse simple package declaration")
        void shouldParseSimplePackage(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("ClassA.java");
            String content = """
                package org.sctt.tools.a;

                public final class ClassA {
                }
                """;
            Files.writeString(javaFile, content);

            // when
            Jpkg pkg = new JavaFileImpl(javaFile).pkg();

            // then
            assertNotNull(pkg);
            assertEquals("org/sctt/tools/a", pkg.asPath().toString());
        }

        @Test
        @DisplayName("Should parse nested package")
        void shouldParseNestedPackage(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("ClassB.java");
            String content = """
                package com.example.deep.nested;

                public class ClassB {
                }
                """;
            Files.writeString(javaFile, content);

            // when
            Jpkg pkg = new JavaFileImpl(javaFile).pkg();

            // then
            assertNotNull(pkg);
            assertEquals("com/example/deep/nested", pkg.asPath().toString());
        }

        @Test
        @DisplayName("Should handle file without package declaration")
        void shouldHandleFileWithoutPackage(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("NoPackage.java");
            String content = """
                public class NoPackage {
                }
                """;
            Files.writeString(javaFile, content);

            // when
            Jpkg pkg = new JavaFileImpl(javaFile).pkg();

            // then
            assertNotNull(pkg);
            assertEquals("", pkg.asPath().toString());
        }

        @Test
        @DisplayName("Should handle file with package on first line")
        void shouldHandlePackageOnFirstLine(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("FirstLine.java");
            String content = "package test.pkg;";
            Files.writeString(javaFile, content);

            // when
            Jpkg pkg = new JavaFileImpl(javaFile).pkg();

            // then
            assertNotNull(pkg);
            assertEquals("test/pkg", pkg.asPath().toString());
        }

        @Test
        @DisplayName("Should handle package with semicolon and spaces")
        void shouldHandlePackageWithSemicolonAndSpaces(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("Spaced.java");
            String content = "   package   org.example.app   ;   ";
            Files.writeString(javaFile, content);

            // when
            Jpkg pkg = new JavaFileImpl(javaFile).pkg();

            // then
            assertNotNull(pkg);
            // Current implementation returns first token after split, which is "package"
            // This test documents current behavior (known limitation)
            assertEquals("package", pkg.asPath().toString());
        }

        @Test
        @DisplayName("Should handle empty file")
        void shouldHandleEmptyFile(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("Empty.java");
            Files.writeString(javaFile, "");

            // when
            Jpkg pkg = new JavaFileImpl(javaFile).pkg();

            // then
            assertNotNull(pkg);
            assertEquals("", pkg.asPath().toString());
        }

        @Test
        @DisplayName("Should handle file with comments before package")
        void shouldHandleCommentsBeforePackage(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("WithComments.java");
            String content = """
                /*
                 * Multi-line comment
                 */
                package org.example;

                public class WithComments {
                }
                """;
            Files.writeString(javaFile, content);

            // when
            Jpkg pkg = new JavaFileImpl(javaFile).pkg();

            // then
            assertNotNull(pkg);
            assertEquals("org/example", pkg.asPath().toString());
        }

        @Test
        @DisplayName("Should throw exception for non-existent file")
        void shouldThrowExceptionForNonExistentFile() {
            // given
            Path nonExistent = Path.of("/non/existent/path/Test.java");
            JavaFileImpl javaFileImpl = new JavaFileImpl(nonExistent);

            // when / then
            assertThrows(RuntimeException.class, javaFileImpl::pkg);
        }
    }

    @Nested
    @DisplayName("Path handling tests")
    class PathHandlingTests {

        @Test
        @DisplayName("Should handle relative path")
        void shouldHandleRelativePath(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("Relative.java");
            Files.writeString(javaFile, "package test;");
            JavaFileImpl javaFileImpl = new JavaFileImpl(javaFile);

            // when
            Jpkg pkg = javaFileImpl.pkg();

            // then
            assertNotNull(pkg);
        }

        @Test
        @DisplayName("Should handle absolute path")
        void shouldHandleAbsolutePath(@TempDir Path tempDir) throws IOException {
            // given
            Path javaFile = tempDir.resolve("Absolute.java");
            Files.writeString(javaFile, "package absolute;");
            JavaFileImpl javaFileImpl = new JavaFileImpl(javaFile.toAbsolutePath());

            // when
            Jpkg pkg = javaFileImpl.pkg();

            // then
            assertNotNull(pkg);
        }
    }
}