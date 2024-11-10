package io.github.charlietap.sweet.plugin.task

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class TestMatrixTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val testFiles: ConfigurableFileTree

    @TaskAction
    fun matrix() {

        val references = testFiles.mapNotNull { file ->

            val packageName = file.useLines {
                lines -> lines.drop(1).firstOrNull()
            }?.replace("package ", "")?.replace("`", "")

            if(packageName == null) {
                logger.warn("Skipping matrix gen for file $file")
            }

            packageName?.let {
                packageName + "." + file.nameWithoutExtension
            }
        }

        val size = references.size
        var remainder = size % MAX_PARALLELISM
        val taskLists = if(size <= MAX_PARALLELISM) {
            references.chunked(1)
        } else {
            val chunkSize = references.size / MAX_PARALLELISM

            val lists = references.drop(remainder).chunked(chunkSize)
            lists.mapIndexed { idx, list ->
                if(idx <= remainder - 1) {
                    list + listOf(references[idx])
                } else list
            }
        }

        val commands = taskLists.map { list ->
            list.joinToString(separator = "\" --tests \"", prefix = "--tests \"", postfix = "\"")
        }

        println(Json.encodeToString(ListSerializer(String.serializer()), commands))
    }

    private companion object {
        const val MAX_PARALLELISM = 3
    }
}
