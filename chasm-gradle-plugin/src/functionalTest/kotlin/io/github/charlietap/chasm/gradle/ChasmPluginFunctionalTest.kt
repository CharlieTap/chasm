package io.github.charlietap.chasm.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChasmPluginFunctionalTest {

    @Test
    fun `plugin metadata declares compatibility without runtime dependencies`() {
        assertTrue(
            Files.isRegularFile(pluginModuleMetadata),
            "Missing Gradle module metadata at $pluginModuleMetadata",
        )
        val moduleContent = Files.readString(pluginModuleMetadata)
        assertContains(
            moduleContent,
            "\"org.gradle.plugin.api-version\": \"$minimumGradleVersion\"",
        )
        assertFalse(
            moduleContent.contains("\"dependencies\""),
            "Gradle module metadata must not publish codegen dependencies",
        )
        assertTrue(Files.isRegularFile(pluginPom), "Missing Maven POM at $pluginPom")
        assertFalse(
            Files.readString(pluginPom).contains("<dependencies>"),
            "The Gradle plugin must not publish codegen dependencies on its runtime classpath",
        )
    }

    @Test
    fun `plugin loads without Kotlin or Android on supported Gradle versions`() {
        testedGradleVersions.forEach { gradleVersion ->
            val project = project(
                build = """
                    plugins {
                        id("$pluginId")
                    }
                """,
            )

            project.build("help", gradleVersion = gradleVersion)
        }
    }

    @Test
    fun `JVM and multiplatform tasks are registered when Chasm is applied first`() {
        val jvmProject = project(
            build = """
                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.jvm")
                }

                chasm {
                    modules.create("JvmService") {
                        packageName.set("test.chasm")
                    }
                    modules.create("OtherJvmService") {
                        packageName.set("test.chasm")
                    }
                }
            """,
        )
        val jvmResult = jvmProject.build("compileKotlin", "--configuration-cache")
        assertContains(jvmResult.output, "codegenModuleMainJvmService")
        assertContains(jvmResult.output, "codegenModuleMainOtherJvmService")
        jvmProject.assertGenerated("main", "JvmService")
        jvmProject.assertGenerated("main", "OtherJvmService")

        val multiplatformProject = project(
            build = """
                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.multiplatform")
                }

                kotlin {
                    jvm()
                }

                chasm {
                    modules.create("CommonService") {
                        packageName.set("test.chasm")
                    }
                }
            """,
            warningMode = WarningMode.KNOWN_KMP_DEPRECATION,
        )
        val multiplatformResult = multiplatformProject.build("tasks", "--group=chasm")
        assertContains(multiplatformResult.output, "codegenModuleCommonMainCommonService")
    }

    @Test
    fun `runtime dependency selection remains lazy`() {
        RuntimeDependencyConfiguration.entries.forEach { selection ->
            val configurationName = selection.name.lowercase()
            val project = project(
                build = """
                    import io.github.charlietap.chasm.gradle.RuntimeDependencyConfiguration

                    plugins {
                        id("$pluginId")
                        id("org.jetbrains.kotlin.jvm")
                    }

                    chasm {
                        runtimeDependencyConfiguration.set(RuntimeDependencyConfiguration.${selection.name})
                    }
                """,
            )

            val result = project.build("dependencies", "--configuration=$configurationName")
            assertContains(result.output, "io.github.charlietap.chasm:vm-jvm:")
        }
    }

    @Test
    fun `configuration cache is reused with configured modules`() {
        val project = project(
            build = """
                plugins {
                    id("org.jetbrains.kotlin.jvm")
                    id("$pluginId")
                }

                chasm {
                    modules.create("CachedService") {
                        packageName.set("test.chasm")
                    }
                }
            """,
        )

        project.build("compileKotlin", "--configuration-cache")
        project.deleteGenerated("main", "CachedService")
        val reused = project.build("compileKotlin", "--configuration-cache")

        assertContains(reused.output, "Configuration cache entry reused.")
        assertContains(reused.output, "codegenModuleMainCachedService")
        project.assertGenerated("main", "CachedService")
    }

    @Test
    fun `plugin is compatible with isolated projects`() {
        val project = project(
            build = """
                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.jvm")
                }

                chasm {
                    modules.create("IsolatedService") {
                        packageName.set("test.chasm")
                    }
                }
            """,
        )

        project.build(
            "help",
            "--isolated-projects",
            "-Dorg.gradle.isolated-projects.diagnostics=true",
        )
    }

    @Test
    fun `current AGP 9 registers generated Kotlin sources on supported Gradle versions`() {
        testedGradleVersions.forEach { gradleVersion ->
            val project = androidProject(
                androidPluginId = "com.android.library",
                moduleName = "AndroidService",
                compileSdk = currentCompileSdk,
            )

            val result = project.build("tasks", "--group=chasm", gradleVersion = gradleVersion)
            assertContains(result.output, "codegenModuleDebugAndroidService")
            assertContains(result.output, "codegenModuleReleaseAndroidService")
        }
    }

    @Test
    fun `AGP 9 application library and test plugins are supported`() {
        listOf(
            "com.android.application" to "ApplicationService",
            "com.android.library" to "LibraryService",
        ).forEach { (androidPluginId, moduleName) ->
            val project = androidProject(
                androidPluginId = androidPluginId,
                moduleName = moduleName,
                compileSdk = currentCompileSdk,
            )

            val result = project.build("tasks", "--group=chasm")
            assertContains(result.output, "codegenModuleDebug$moduleName")
        }

        val testProject = androidTestProject()
        val testResult = testProject.build(":test:tasks", "--group=chasm")
        assertContains(testResult.output, "codegenModuleDebugTestService")
    }

    @Test
    fun `minimum AGP 9 API remains supported`() {
        val project = androidProject(
            androidPluginId = "com.android.library",
            moduleName = "MinimumAgpService",
            compileSdk = minimumAgpCompileSdk,
            minimumAgp = true,
        )

        val result = project.build(
            "tasks",
            "--group=chasm",
            gradleVersion = minimumAgpGradleVersion,
        )
        assertContains(result.output, "codegenModuleDebugMinimumAgpService")
        assertContains(result.output, "codegenModuleReleaseMinimumAgpService")
    }

    @Test
    fun `AGP 9 Kotlin multiplatform library is supported`() {
        val project = project(
            build = """
                plugins {
                    id("$pluginId")
                    id("org.jetbrains.kotlin.multiplatform")
                    id("com.android.kotlin.multiplatform.library")
                }

                kotlin {
                    jvm()
                    androidLibrary {
                        namespace = "test.chasm"
                        compileSdk = $currentCompileSdk
                        minSdk = $currentMinSdk
                    }
                }

                chasm {
                    modules.create("KmpAndroidService") {
                        packageName.set("test.chasm")
                    }
                }
            """,
            gradleProperties = """
                android.builtInKotlin=true
                android.newDsl=true
            """,
            warningMode = WarningMode.KNOWN_KMP_DEPRECATION,
        )

        val result = project.build("tasks", "--group=chasm")
        assertContains(result.output, "codegenModuleCommonMainKmpAndroidService")
    }

    private fun project(
        build: String,
        gradleProperties: String? = null,
        minimumAgp: Boolean = false,
        warningMode: WarningMode = WarningMode.FAIL,
    ): FunctionalProject {
        val directory = createTempDirectory("chasm-gradle-plugin-test")
        directory.write("settings.gradle.kts", settings(minimumAgp = minimumAgp))
        directory.write("build.gradle.kts", build)
        Files.createDirectories(directory.resolve("src/main/wasm"))
        Files.write(directory.resolve("src/main/wasm/module.wasm"), MINIMAL_WASM_MODULE)
        gradleProperties?.let { directory.write("gradle.properties", it) }
        return FunctionalProject(
            directory = directory,
            warningMode = warningMode,
        )
    }

    private fun androidProject(
        androidPluginId: String,
        moduleName: String,
        compileSdk: Int,
        minimumAgp: Boolean = false,
    ): FunctionalProject {
        return project(
            build = """
                plugins {
                    id("$pluginId")
                    id("$androidPluginId")
                }

                android {
                    namespace = "test.chasm"
                    compileSdk = $compileSdk
                }

                chasm {
                    modules.create("$moduleName") {
                        packageName.set("test.chasm")
                    }
                }
            """,
            gradleProperties = """
                android.builtInKotlin=true
                android.newDsl=true
            """,
            minimumAgp = minimumAgp,
        )
    }

    private fun androidTestProject(): FunctionalProject {
        val directory = createTempDirectory("chasm-gradle-plugin-test")
        directory.write("settings.gradle.kts", settings(includedProjects = listOf(":app", ":test")))
        directory.write("build.gradle.kts", "")
        Files.createDirectories(directory.resolve("app"))
        directory.write(
            "app/build.gradle.kts",
            """
                plugins {
                    id("com.android.application")
                }

                android {
                    namespace = "test.chasm.app"
                    compileSdk = $currentCompileSdk
                }
            """,
        )
        Files.createDirectories(directory.resolve("test"))
        directory.write(
            "test/build.gradle.kts",
            """
                plugins {
                    id("$pluginId")
                    id("com.android.test")
                }

                android {
                    namespace = "test.chasm.test"
                    compileSdk = $currentCompileSdk
                    targetProjectPath = ":app"
                }

                chasm {
                    modules.create("TestService") {
                        packageName.set("test.chasm")
                    }
                }
            """,
        )
        directory.write(
            "gradle.properties",
            """
                android.builtInKotlin=true
                android.newDsl=true
            """,
        )
        return FunctionalProject(
            directory = directory,
            warningMode = WarningMode.FAIL,
        )
    }

    private fun settings(
        minimumAgp: Boolean = false,
        includedProjects: List<String> = emptyList(),
    ): String {
        val agpVersion = if (minimumAgp) minimumAgpPluginVersion else androidPluginVersion
        return """
            import org.gradle.api.initialization.resolve.RepositoriesMode
            import org.gradle.util.GradleVersion

            pluginManagement {
                repositories {
                    maven {
                        url = uri("$pluginRepository")
                        metadataSources {
                            mavenPom()
                            artifact()
                        }
                    }
                    gradlePluginPortal()
                    google()
                    mavenCentral()
                }
                plugins {
                    id("$pluginId") version "$pluginVersion"
                    id("org.jetbrains.kotlin.jvm") version "$kotlinPluginVersion"
                    id("org.jetbrains.kotlin.multiplatform") version "$kotlinPluginVersion"
                    id("com.android.application") version "$agpVersion"
                    id("com.android.library") version "$agpVersion"
                    id("com.android.test") version "$agpVersion"
                    id("com.android.kotlin.multiplatform.library") version "$agpVersion"
                }
            }

            dependencyResolutionManagement {
                repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                repositories {
                    maven {
                        url = uri("$functionalTestRepository")
                        metadataSources {
                            mavenPom()
                            artifact()
                        }
                    }
                    google()
                    mavenCentral()
                }
            }

            if (GradleVersion.current() >= GradleVersion.version("9.6")) {
                enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")
            }

            rootProject.name = "chasm-plugin-test"
            ${includedProjects.joinToString("\n") { projectPath -> "include(\"$projectPath\")" }}
        """
    }

    private class FunctionalProject(
        private val directory: Path,
        private val warningMode: WarningMode,
    ) {
        fun build(
            vararg arguments: String,
            gradleVersion: String? = null,
        ): BuildResult {
            val runner = GradleRunner.create()
                .withProjectDir(directory.toFile())
                .withArguments(
                    *arguments,
                    "--stacktrace",
                    warningMode.argument,
                )

            gradleVersion?.let(runner::withGradleVersion)
            return runner.build().also { result ->
                if (warningMode == WarningMode.KNOWN_KMP_DEPRECATION) {
                    assertContains(result.output, "getTaskDependencyFromProjectDependency")
                }
            }
        }

        fun assertGenerated(
            sourceSetName: String,
            moduleName: String,
        ) {
            val outputDirectory = directory.resolve("build/generated/kotlin/$sourceSetName/$moduleName")
            assertTrue(Files.isDirectory(outputDirectory), "Missing generated output directory $outputDirectory")
            Files.walk(outputDirectory).use { files ->
                assertTrue(
                    files.anyMatch(Files::isRegularFile),
                    "Missing generated source in $outputDirectory",
                )
            }
        }

        fun deleteGenerated(
            sourceSetName: String,
            moduleName: String,
        ) {
            val outputDirectory = directory.resolve("build/generated/kotlin/$sourceSetName/$moduleName")
            Files.walk(outputDirectory).use { files ->
                files.sorted(Comparator.reverseOrder()).forEach(Files::delete)
            }
        }
    }

    private enum class WarningMode(val argument: String) {
        FAIL("--warning-mode=fail"),
        KNOWN_KMP_DEPRECATION("--warning-mode=all"),
    }

    private companion object {
        val MINIMAL_WASM_MODULE = byteArrayOf(0, 97, 115, 109, 1, 0, 0, 0)
        val functionalTestRepository = requiredSystemProperty("chasm.functionalTest.repository")
        val pluginRepository = requiredSystemProperty("chasm.functionalTest.pluginRepository")
        val pluginPom = Path.of(requiredSystemProperty("chasm.functionalTest.pluginPom"))
        val pluginModuleMetadata = Path.of(
            requiredSystemProperty("chasm.functionalTest.pluginModuleMetadata"),
        )
        val pluginId = requiredSystemProperty("chasm.functionalTest.pluginId")
        val pluginVersion = requiredSystemProperty("chasm.functionalTest.pluginVersion")
        val kotlinPluginVersion = requiredSystemProperty("chasm.functionalTest.kotlinPluginVersion")
        val androidPluginVersion = requiredSystemProperty("chasm.functionalTest.androidPluginVersion")
        val minimumAgpPluginVersion = requiredSystemProperty("chasm.functionalTest.minimumAgpPluginVersion")
        val minimumGradleVersion = requiredSystemProperty("chasm.functionalTest.minimumGradleVersion")
        val minimumAgpGradleVersion = requiredSystemProperty("chasm.functionalTest.minimumAgpGradleVersion")
        val testedGradleVersions = requiredSystemProperty("chasm.functionalTest.testedGradleVersions").split(',')
        val currentCompileSdk = requiredSystemProperty("chasm.functionalTest.compileSdk").toInt()
        val minimumAgpCompileSdk = requiredSystemProperty("chasm.functionalTest.minimumAgpCompileSdk").toInt()
        val currentMinSdk = requiredSystemProperty("chasm.functionalTest.minSdk").toInt()

        fun requiredSystemProperty(name: String): String {
            return requireNotNull(System.getProperty(name)) { "Missing system property $name" }
        }
    }
}

private fun Path.write(
    relativePath: String,
    content: String,
) {
    Files.writeString(resolve(relativePath), content.trimIndent())
}
