package io.github.charlietap.corpus.plugin.task

import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

@CacheableTask
abstract class ResolveCorpusFixturesTask : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val corpusDirectory: DirectoryProperty

    @get:Input
    abstract val versions: ListProperty<String>

    @get:Input
    abstract val requiredFeatures: ListProperty<String>

    @get:Input
    abstract val excludedFeatures: ListProperty<String>

    @get:Input
    abstract val tags: ListProperty<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Inject
    abstract val cli: ExecOperations

    private val json = Json { prettyPrint = true }

    @TaskAction
    fun resolve() {
        val corpusRoot = corpusDirectory.get().asFile
        val corpusScript = corpusRoot.resolve("corpus")
        if (!corpusScript.isFile) {
            throw GradleException("Missing wasm-corpus script at ${corpusScript.absolutePath}")
        }

        val fixtures = versions.get().flatMap { version ->
            resolveVersion(corpusScript.absolutePath, version)
        }

        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText(json.encodeToString(JsonArray.serializer(), JsonArray(fixtures)))
        }
    }

    private fun resolveVersion(
        corpusScript: String,
        version: String,
    ): List<JsonObject> {
        val stdout = ByteArrayOutputStream()
        val args = buildList {
            add(corpusScript)
            add("--version")
            add(version)
            requiredFeatures.get().forEach { feature ->
                add("--feature")
                add(feature)
            }
            excludedFeatures.get().forEach { feature ->
                add("--exclude-feature")
                add(feature)
            }
            tags.get().forEach { tag ->
                add("--tag")
                add(tag)
            }
            add("--compact")
        }

        cli.exec {
            workingDir = corpusDirectory.get().asFile
            commandLine(args)
            standardOutput = stdout
        }

        return Json.parseToJsonElement(stdout.toString()).jsonArray.map { element ->
            JsonObject(element.jsonObject + ("version" to JsonPrimitive(version)))
        }
    }
}
