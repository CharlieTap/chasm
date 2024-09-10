package io.github.charlietap.sweet.plugin.task

import java.io.File
import java.net.URI
import java.net.URL
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class DownloadWasmToolsTask : DefaultTask() {

    @get:Input
    abstract val wasmToolsVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val archive: ArchiveOperations

    @get:Inject
    abstract val fs: FileSystemOperations

    @TaskAction
    fun download() {

        val version = wasmToolsVersion.get()
        val osArch = System.getProperty("os.arch").lowercase()
        val osName = System.getProperty("os.name").lowercase()


        val (os, extension) = when {
            osName.contains("mac") -> OS_MAC to FILE_EXTENSION_TAR
            osName.contains("nux") -> OS_LINUX to FILE_EXTENSION_TAR
            osName.contains("win") -> OS_WINDOWS to FILE_EXTENSION_ZIP
            else -> throw GradleException("Unsupported OS: $osName")
        }
        val arch = when {
            osArch.contains(ARCH_ARM) -> ARCH_ARM
            x86.contains(osArch) -> ARCH_X8664
            else -> throw GradleException("Unsupported cpu architecture: $osArch")
        }

        val filename = filename(version, arch, os)
        val url = "${RELEASE_URL}v${version}/${filename}${extension}"

        val compressed = if(os == OS_WINDOWS) {
            outputDirectory.file("${version}${FILE_EXTENSION_ZIP}").get().asFile
        } else {
            outputDirectory.file("${version}${FILE_EXTENSION_TAR}").get().asFile
        }

        wipeDirectory()
        downloadRelease(URI(url).toURL(), compressed)

        val tree = if(os == OS_WINDOWS) {
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

    private companion object {
        const val RELEASE_URL = "https://github.com/bytecodealliance/wasm-tools/releases/download/"

        const val ARCH_X8664 = "x86_64"
        const val ARCH_AMD64 = "amd64"
        const val ARCH_ARM = "aarch64"

        const val OS_LINUX = "linux"
        const val OS_MAC = "macos"
        const val OS_WINDOWS = "windows"

        const val FILE_EXTENSION_TAR = ".tar.gz"
        const val FILE_EXTENSION_ZIP = ".zip"

        val x86 = arrayOf(ARCH_AMD64, ARCH_X8664)

        fun filename(version: String, arch: String, os: String) = "wasm-tools-$version-$arch-$os"
    }
}
