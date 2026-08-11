package io.github.charlietap.corpus.plugin.task

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class CorpusMatrixTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val fixturesIndex: RegularFileProperty

    @TaskAction
    fun printMatrix() {
        val fixtures = Json.parseToJsonElement(fixturesIndex.get().asFile.readText()).jsonArray
        val byVersion = fixtures.groupingBy { fixture ->
            fixture.jsonObject["version"]?.jsonPrimitive?.content ?: "unknown"
        }.eachCount()

        logger.lifecycle("wasm-corpus fixtures: ${fixtures.size}")
        byVersion.toSortedMap().forEach { (version, count) ->
            logger.lifecycle("  $version: $count")
        }
    }
}
