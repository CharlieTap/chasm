package io.github.charlietap.corpus.plugin.task

import com.android.tools.r8.ClassFileConsumer
import com.android.tools.r8.CompilationMode
import com.android.tools.r8.R8
import com.android.tools.r8.R8Command
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@CacheableTask
abstract class ReleaseR8ClassesTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val jars: ListProperty<RegularFile>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val directories: ListProperty<Directory>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val bootClasspath: ListProperty<RegularFile>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val keepRules: ConfigurableFileCollection

    @get:Input
    abstract val r8Version: Property<String>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun shrink() {

        val archives = directories.get().mapIndexed { index, directory ->
            archive(directory.asFile, temporaryDir.resolve("directory-$index.jar"))
        }
        val programs = jars.get().map { jar -> jar.asFile } + archives

        val command = R8Command.builder()
            .setMode(CompilationMode.RELEASE)
            .setProgramConsumer(ClassFileConsumer.ArchiveConsumer(output.get().asFile.toPath()))
            .addLibraryFiles(bootClasspath.get().map { file -> file.asFile.toPath() })
            .addProgramFiles(programs.map { file -> file.toPath() })
            .addProguardConfigurationFiles(keepRules.files.map { file -> file.toPath() })
            .build()

        R8.run(command)
    }

    private fun archive(
        directory: File,
        jar: File,
    ): File {

        ZipOutputStream(jar.outputStream()).use { zip ->
            directory.walkTopDown().filter { file -> file.isFile }.forEach { file ->
                zip.putNextEntry(ZipEntry(file.relativeTo(directory).invariantSeparatorsPath))
                file.inputStream().use { input -> input.copyTo(zip) }
                zip.closeEntry()
            }
        }

        return jar
    }
}
