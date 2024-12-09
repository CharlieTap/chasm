package io.github.charlietap.bolt.plugin.task

import java.io.File
import java.net.URI
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class DownloadArchivesTask : DefaultTask() {

    @get:Input
    abstract val library: Property<String>

    @get:Input
    abstract val url: Property<String>

    @get:Input
    abstract val archiveFormat: Property<String>

    @get:Input
    abstract val targets: SetProperty<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val archive: ArchiveOperations

    @get:Inject
    abstract val fs: FileSystemOperations

    @TaskAction
    fun download() {

        val library = library.get()

        val headerFileName = "$library.h"
        val headerFile = outputDirectory.file(headerFileName).get().asFile

        wipeDirectory()
        downloadArtifact(headerFileName, headerFile)

        targets.get().forEach { target ->

            val llvmTriple = lookup[target] ?: throw GradleException("Kotlin Multiplatform Target $target not found")
            val artifactFileName = "$library-$llvmTriple.a." + archive()
            val artifactFile = outputDirectory.file(artifactFileName).get().asFile

            downloadArtifact(artifactFileName, artifactFile)

            val tree = archive.zipTree(artifactFile)
            fs.copy {
                from(tree)
                into(outputDirectory.dir(llvmTriple))
            }
            artifactFile.delete()
        }
    }

    private fun archive(): String = when(val archive = archiveFormat.get()){
        "zip" -> archive
        "tar" -> "tar.gz"
        else -> throw GradleException("Unexpected archive format, must be zip or tar")
    }

    private fun downloadArtifact(fileName: String, destination: File) {
        val url = if(url.get().last() == '/') {
            url.get() + fileName
        } else {
            url.get() + "/" + fileName
        }.let {
            URI(it).toURL()
        }
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

        private const val KTARGET_MACOS_ARM64 = "macosArm64"
        private const val KTARGET_MACOS_X64 = "macosX64"
        private const val KTARGET_IOS_ARM64 = "iosArm64"
        private const val KTARGET_IOS_SIMULATOR_ARM64 = "iosSimulatorArm64"
        private const val KTARGET_IOS_X64 = "iosX64"
        private const val KTARGET_LINUX_ARM64 = "linuxArm64"
        private const val KTARGET_LINUX_X64 = "linuxX64"
        private const val KTARGET_MINGW_X64 = "mingwX64"

        private const val LLVM_MACOS_ARM64 = "aarch64-apple-darwin"
        private const val LLVM_MACOS_X64 = "x86_64-apple-darwin"
        private const val LLVM_IOS_ARM64 = "aarch64-apple-ios"
        private const val LLVM_IOS_SIMULATOR_ARM64 = "aarch64-apple-ios-sim"
        private const val LLVM_IOS_X64 = "x86_64-apple-ios"
        private const val LLVM_LINUX_ARM64 = "aarch64-unknown-linux-gnu"
        private const val LLVM_LINUX_X64 = "x86_64-unknown-linux-gnu"
        private const val LLVM_MINGW_X64 = "x86_64-pc-windows-gnu"

        val lookup = mapOf(
            KTARGET_MACOS_ARM64 to LLVM_MACOS_ARM64,
            KTARGET_MACOS_X64 to LLVM_MACOS_X64,
            KTARGET_IOS_ARM64 to LLVM_IOS_ARM64,
            KTARGET_IOS_SIMULATOR_ARM64 to LLVM_IOS_SIMULATOR_ARM64,
            KTARGET_IOS_X64 to LLVM_IOS_X64,
            KTARGET_LINUX_ARM64 to LLVM_LINUX_ARM64,
            KTARGET_LINUX_X64 to LLVM_LINUX_X64,
            KTARGET_MINGW_X64 to LLVM_MINGW_X64
        )
    }
}
