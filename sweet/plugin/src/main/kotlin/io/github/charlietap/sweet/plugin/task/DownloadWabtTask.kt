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
abstract class DownloadWabtTask : DefaultTask() {

    @get:Input
    abstract val wabtVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val archive: ArchiveOperations

    @get:Inject
    abstract val fs: FileSystemOperations

    @TaskAction
    fun downloadAndExtract() {
        val osName = System.getProperty("os.name").lowercase()
        val osArch = System.getProperty("os.arch").lowercase()
        val tarFile = outputDirectory.file("${wabtVersion.get()}.tar.gz").get().asFile

        val releaseUrl = when {
            osName.contains("mac") -> {
                when (osArch) {
                    "x86_64" -> "https://github.com/WebAssembly/wabt/releases/download/${wabtVersion.get()}/wabt-${wabtVersion.get()}-macos-12.tar.gz"
                    "aarch64" -> "https://github.com/WebAssembly/wabt/releases/download/${wabtVersion.get()}/wabt-${wabtVersion.get()}-macos-14.tar.gz"
                    else -> throw GradleException("Unsupported macOS architecture: $osArch")
                }
            }
            osName.contains("nux") -> {
                "https://github.com/WebAssembly/wabt/releases/download/${wabtVersion.get()}/wabt-${wabtVersion.get()}-ubuntu-20.04.tar.gz"
            }
            osName.contains("win") -> {
                "https://github.com/WebAssembly/wabt/releases/download/${wabtVersion.get()}/wabt-${wabtVersion.get()}-windows.tar.gz"
            }
            else -> throw GradleException("Unsupported OS: ${System.getProperty("os.name")}")
        }

        wipeDirectory()
        downloadRelease(URI(releaseUrl).toURL(), tarFile)

        fs.copy {
            from(archive.tarTree(archive.gzip(tarFile))) {
                include("*/bin/**")
                eachFile {
                    path = path
                        .replace("wabt-${wabtVersion.get()}", wabtVersion.get())
                        .replace("bin", "")
                    includeEmptyDirs = false
                }
            }
            into(outputDirectory)
        }

        val extractedDirectory = outputDirectory.dir("wabt-" + wabtVersion.get()).get()
        val extractedBinDirectory = outputDirectory.dir(wabtVersion.get()).get().dir("bin")
        extractedDirectory.asFile.deleteRecursively()
        extractedBinDirectory.asFile.deleteRecursively()
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
}
