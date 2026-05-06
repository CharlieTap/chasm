package io.github.charlietap.corpus.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class CleanCorpusTestsTask : DefaultTask() {

    @get:OutputDirectory
    abstract val corpusFixtureDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val corpusTestsDirectory: DirectoryProperty

    @TaskAction
    fun clean() {
        corpusFixtureDirectory.get().asFile.deleteRecursively()
        corpusTestsDirectory.get().asFile.deleteRecursively()
    }
}
