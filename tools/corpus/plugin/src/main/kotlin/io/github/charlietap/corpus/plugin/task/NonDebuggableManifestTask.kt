package io.github.charlietap.corpus.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class NonDebuggableManifestTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val mergedManifest: RegularFileProperty

    @get:OutputFile
    abstract val updatedManifest: RegularFileProperty

    @TaskAction
    fun rewrite() {

        val manifest = mergedManifest.get().asFile.readText()
        check(DEBUGGABLE in manifest) { "the merged manifest does not declare $DEBUGGABLE" }

        updatedManifest.get().asFile.writeText(manifest.replace(DEBUGGABLE, NOT_DEBUGGABLE))
    }

    private companion object {
        const val DEBUGGABLE = "android:debuggable=\"true\""
        const val NOT_DEBUGGABLE = "android:debuggable=\"false\""
    }
}
