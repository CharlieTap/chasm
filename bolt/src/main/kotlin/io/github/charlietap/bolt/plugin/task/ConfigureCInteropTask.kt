package io.github.charlietap.bolt.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class ConfigureCInteropTask : DefaultTask() {

    @get:Input
    abstract val library: Property<String>

    @get:Input
    abstract val targets: SetProperty<String>

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputDirectory
    abstract val artifactsDir: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    private val packageName by lazy {
        library.get()
    }

    private val headers by lazy {
        library.get() + ".h"
    }

    private val staticLibraries by lazy {
        library.get() + ".a"
    }

    private val compilerOpts by lazy {
        "-I" + artifactsDir.get().asFile.absolutePath
    }

    @TaskAction
    fun configure() {

        val targets = targets.get()
        val defFile = outputFile.get().asFile

        defFile.delete()
        defFile.appendText(
            """
                package = $packageName
                headers = $headers
                staticLibraries = $staticLibraries
                compilerOpts = $compilerOpts
            """.trimIndent() + "\n"
        )

        targets.forEach { target ->

            val (ext, llvm) = libraryPaths(target)
            val libraryPaths = "libraryPaths.$ext = ${artifactsDir.get().asFile.absolutePath}/$llvm\n"

            defFile.appendText(libraryPaths)
        }
    }

    private fun libraryPaths(target: String) = when (target) {
        "iosArm64" -> "ios_arm64" to "aarch64-apple-ios"
        "iosX64" -> "ios_x64" to "x86_64-apple-ios"
        "iosSimulatorArm64" -> "ios_simulator_arm64" to "aarch64-apple-ios-sim"
        "macosArm64" -> "macos_arm64" to "aarch64-apple-darwin"
        "macosX64" -> "macos_x64" to "x86_64-apple-darwin"
        "linuxArm64" -> "linux_arm64" to "aarch64-unknown-linux-gnu"
        "linuxX64" -> "linux_x64" to "x86_64-unknown-linux-gnu"
        "mingwX64" -> "mingw_x64" to "x86_64-pc-windows-gnu"
        else -> throw GradleException("Unsupported Kotlin Multiplatform Target: $target")
    }
}
