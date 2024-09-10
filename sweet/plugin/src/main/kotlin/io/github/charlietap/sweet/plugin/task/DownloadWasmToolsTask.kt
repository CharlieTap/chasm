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
        val tarFile = outputDirectory.file("${version}.tar.gz").get().asFile

        val os = when {
            osName.contains("mac") -> OS_MAC
            osName.contains("nux") -> OS_LINUX
            osName.contains("win") -> OS_WINDOWS
            else -> throw GradleException("Unsupported OS: $osName")
        }
        val arch = when {
            osArch.contains(ARCH_ARM) -> ARCH_ARM
            osArch.contains(ARCH_X86) -> ARCH_X86
            else -> throw GradleException("Unsupported cpu architecture: $osArch")
        }

        val filename = filename(version, arch, os)
        val url = "${RELEASE_URL}v${version}/${filename}${FILE_EXTENSION}"

        wipeDirectory()
        downloadRelease(URI(url).toURL(), tarFile)

        fs.copy {
            from(archive.tarTree(archive.gzip(tarFile))) {
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
        tarFile.delete()
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

        const val ARCH_X86 = "x86_64"
        const val ARCH_ARM = "aarch64"

        const val OS_LINUX = "linux"
        const val OS_MAC = "macos"
        const val OS_WINDOWS = "windows"

        const val FILE_EXTENSION = ".tar.gz"

        fun filename(version: String, arch: String, os: String) = "wasm-tools-$version-$arch-$os"
    }
}
