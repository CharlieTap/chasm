package io.github.charlietap.corpus.plugin.task

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class PrepareCorpusResourcesTask : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val corpusDirectory: DirectoryProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val fixturesIndex: RegularFileProperty

    @get:Input
    abstract val targets: ListProperty<String>

    @get:Input
    abstract val excludedTargets: ListProperty<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    private val json = Json { prettyPrint = true }

    @TaskAction
    fun prepare() {
        val output = outputDirectory.get().asFile
        output.deleteRecursively()

        val fixtures = Json.parseToJsonElement(fixturesIndex.get().asFile.readText())
            .jsonArray
            .map { element -> element as JsonObject }
            .filter(::targetMatches)
            .filterNot(::targetExcluded)
        val corpusRoot = output.resolve(CORPUS_ROOT)
        corpusRoot.mkdirs()
        corpusRoot.resolve(FIXTURE_INDEX).writeText(json.encodeToString(JsonArray(fixtures)))

        fixtures.forEach { fixture ->
            val version = fixture.string("version")
            val binaryPath = fixture.string("path")
            val fixturePath = binaryPath.removeSuffix(".wasm") + ".json"
            copyResource(version, binaryPath, corpusRoot)
            copyResource(version, fixturePath, corpusRoot)
        }
    }

    private fun copyResource(
        version: String,
        path: String,
        output: java.io.File,
    ) {
        val source = corpusDirectory.file("$version/$path").get().asFile
        val destination = output.resolve(version).resolve(path)
        destination.parentFile.mkdirs()
        source.copyTo(destination)
    }

    private fun targetMatches(fixture: JsonObject): Boolean {
        val selected = targets.get()
        return selected.isEmpty() || selected.any { target -> fixture.matches(target) }
    }

    private fun targetExcluded(fixture: JsonObject): Boolean = excludedTargets.get().any { target ->
        fixture.matches(target)
    }

    private fun JsonObject.matches(target: String): Boolean {
        val name = string("name")
        val path = string("path")
        val stem = path.substringAfterLast('/').substringBeforeLast('.')
        return target == name || target == path || target == stem
    }

    private fun JsonObject.string(name: String): String = getValue(name).jsonPrimitive.content

    private companion object {
        const val CORPUS_ROOT = "corpus"
        const val FIXTURE_INDEX = "fixtures.json"
    }
}
