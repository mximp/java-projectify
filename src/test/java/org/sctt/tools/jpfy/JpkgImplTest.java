package org.sctt.tools.jpfy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link JpkgImpl}.
 */
@DisplayName("JpkgImpl tests")
class JpkgImplTest {

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with non-null package name")
        void shouldCreateInstanceWithValidPackage() {
            // given
            String packageName = "org.example.test";

            // when
            JpkgImpl jpkg = new JpkgImpl(packageName);

            // then
            assertNotNull(jpkg);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "a", "org.sctt.tools.jpfy", "com.example.deep.nested"})
        @DisplayName("Should handle various package name formats")
        void shouldHandleVariousPackageFormats(String packageName) {
            // when
            JpkgImpl jpkg = new JpkgImpl(packageName);

            // then
            assertNotNull(jpkg);
        }
    }

    @Nested
    @DisplayName("asPath tests")
    class AsPathTests {

        @Test
        @DisplayName("Should convert simple package to path")
        void shouldConvertSimplePackageToPath() {
            // given
            String packageName = "com.example";
            JpkgImpl jpkg = new JpkgImpl(packageName);

            // when
            Path result = jpkg.asPath();

            // then
            assertEquals("com/example", result.toString());
        }

        @Test
        @DisplayName("Should convert nested package to path")
        void shouldConvertNestedPackageToPath() {
            // given
            String packageName = "org.sctt.tools.jpfy";
            JpkgImpl jpkg = new JpkgImpl(packageName);

            // when
            Path result = jpkg.asPath();

            // then
            assertEquals("org/sctt/tools/jpfy", result.toString());
        }

        @Test
        @DisplayName("Should handle empty package name")
        void shouldHandleEmptyPackageName() {
            // given
            JpkgImpl jpkg = new JpkgImpl("");

            // when
            Path result = jpkg.asPath();

            // then
            assertEquals("", result.toString());
        }

        @Test
        @DisplayName("Should handle single segment package")
        void shouldHandleSingleSegmentPackage() {
            // given
            JpkgImpl jpkg = new JpkgImpl("test");

            // when
            Path result = jpkg.asPath();

            // then
            assertEquals("test", result.toString());
        }

        @Test
        @DisplayName("Should preserve case in package name")
        void shouldPreserveCaseInPackageName() {
            // given
            JpkgImpl jpkg = new JpkgImpl("org.EXAMPLE.App");

            // when
            Path result = jpkg.asPath();

            // then
            assertEquals("org/EXAMPLE/App", result.toString());
        }
    }
}