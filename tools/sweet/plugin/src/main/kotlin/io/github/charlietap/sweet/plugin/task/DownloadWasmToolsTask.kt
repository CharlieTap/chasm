package io.github.charlietap.sweet.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import java.net.URL
import javax.inject.Inject

@CacheableTask
abstract class DownloadWasmToolsTask : DefaultTask() {

    @get:Input
    abstract val wasmToolsVersion: Property<String>

    @get:Input
    abstract val operatingSystem: Property<String>

    @get:Input
    abstract val architecture: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Internal
    val executable: Provider<RegularFile> = outputDirectory.zip(wasmToolsVersion) { directory, version ->
        directory.dir(version).file("wasm-tools")
    }

    @get:Inject
    abstract val archive: ArchiveOperations

    @get:Inject
    abstract val fs: FileSystemOperations

    @get:Inject
    abstract val cli: ExecOperations

    @TaskAction
    fun download() {

        val version = wasmToolsVersion.get()
        val os = operatingSystem.get()
        val arch = architecture.get()
        val extension = if (os == OS_WINDOWS) FILE_EXTENSION_ZIP else FILE_EXTENSION_TAR

        val filename = filename(version, arch, os)
        val url = "${RELEASE_URL}v$version/${filename}$extension"

        val compressed = outputDirectory.file("${version}$extension").get().asFile

        wipeDirectory()
        downloadRelease(URI(url).toURL(), compressed)

        val tree = if (os == OS_WINDOWS) {
            archive.zipTree(compressed)
        } else {
            archive.tarTree(archive.gzip(compressed))
        }

        fs.copy {
            from(tree) {
                include("**/wasm-tools*")
                eachFile {
                    path = path
                        .replace(filename, version)
                        .replace(".exe", "")
                    includeEmptyDirs = false
                }
            }
            into(outputDirectory)
        }

        val extractedDirectory = outputDirectory.dir(filename).get()
        extractedDirectory.asFile.deleteRecursively()
        compressed.delete()

        verify(executable.get().asFile, version)
    }

    private fun verify(wasmTools: File, version: String) {
        if (!wasmTools.exists()) {
            throw GradleException("wasm-tools file does not exist: ${wasmTools.absolutePath}")
        }

        val versionBytes = ByteArrayOutputStream()
        cli.exec {
            commandLine = listOf(wasmTools.absolutePath, "--version")
            standardOutput = versionBytes
        }

        if (!versionBytes.toString().trim().contains(version)) {
            throw GradleException("wasm-tools does not support version $version")
        }
    }

    private fun downloadRelease(url: URL, destination: File) {
        url.openStream().use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun wipeDirectory() {
        outputDirectory.get().asFile.listFiles()?.forEach {
            it.deleteRecursively()
        }
    }

    companion object {
        private const val RELEASE_URL = "https://github.com/bytecodealliance/wasm-tools/releases/download/"

        private const val ARCH_X8664 = "x86_64"
        private const val ARCH_AMD64 = "amd64"
        private const val ARCH_ARM = "aarch64"

        private const val OS_LINUX = "linux"
        private const val OS_MAC = "macos"
        private const val OS_WINDOWS = "windows"

        private const val FILE_EXTENSION_TAR = ".tar.gz"
        private const val FILE_EXTENSION_ZIP = ".zip"

        private val x86 = arrayOf(ARCH_AMD64, ARCH_X8664)

        private fun filename(version: String, arch: String, os: String) = "wasm-tools-$version-$arch-$os"

        fun operatingSystem(osName: String): String {
            val name = osName.lowercase()
            return when {
                name.contains("mac") -> OS_MAC
                name.contains("nux") -> OS_LINUX
                name.contains("win") -> OS_WINDOWS
                else -> throw GradleException("Unsupported OS: $osName")
            }
        }

        fun architecture(osArch: String): String {
            val arch = osArch.lowercase()
            return when {
                arch.contains(ARCH_ARM) -> ARCH_ARM
                x86.contains(arch) -> ARCH_X8664
                else -> throw GradleException("Unsupported cpu architecture: $osArch")
            }
        }
    }
}
