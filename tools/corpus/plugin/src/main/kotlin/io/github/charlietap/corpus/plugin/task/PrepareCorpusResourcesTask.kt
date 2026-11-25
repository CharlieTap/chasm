package io.github.charlietap.corpus.plugin.task

import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.report.CorpusConfiguration
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
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
    abstract val resolvedFixtures: RegularFileProperty

    @get:Input
    abstract val repositoryUrl: Property<String>

    @get:Input
    abstract val revision: Property<String>

    @get:Input
    abstract val phase: Property<CorpusPhase>

    @get:Input
    abstract val engines: SetProperty<String>

    @get:Input
    abstract val warmups: Property<Int>

    @get:Input
    abstract val rounds: Property<Int>

    @get:Input
    abstract val pin: Property<Boolean>

    @get:Input
    abstract val versions: ListProperty<String>

    @get:Input
    abstract val languages: ListProperty<String>

    @get:Input
    abstract val requiredFeatures: ListProperty<String>

    @get:Input
    abstract val excludedFeatures: ListProperty<String>

    @get:Input
    abstract val tags: ListProperty<String>

    @get:Input
    abstract val excludedTags: ListProperty<String>

    @get:Input
    @get:Optional
    abstract val size: Property<String>

    @get:Input
    @get:Optional
    abstract val duration: Property<String>

    @get:Input
    abstract val targets: ListProperty<String>

    @get:Input
    abstract val excludedTargets: ListProperty<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Internal
    val fixturesIndex: Provider<RegularFile>
        get() {
            return outputDirectory.file("$CORPUS_ROOT/$FIXTURES_INDEX")
        }

    @get:Internal
    val runFile: Provider<RegularFile>
        get() {
            return outputDirectory.file("$CORPUS_ROOT/$RUN_FILE")
        }

    private val json = Json { prettyPrint = true }

    @TaskAction
    fun prepare() {

        val output = outputDirectory.get().asFile
        output.deleteRecursively()

        val fixtures = Json.parseToJsonElement(resolvedFixtures.get().asFile.readText())
            .jsonArray
            .map { element -> element as JsonObject }
            .filter(::targetMatches)
            .filterNot(::targetExcluded)
        val corpusRoot = output.resolve(CORPUS_ROOT)
        corpusRoot.mkdirs()
        corpusRoot.resolve(FIXTURES_INDEX).writeText(json.encodeToString(JsonArray(fixtures)))
        corpusRoot.resolve(RUN_FILE).writeText(json.encodeToString(CorpusConfiguration.serializer(), configuration()))

        fixtures.forEach { fixture ->
            val version = fixture.string("version")
            val binaryPath = fixture.string("path")
            val fixturePath = binaryPath.removeSuffix(".wasm") + ".json"
            copyResource(version, binaryPath, corpusRoot)
            copyResource(version, fixturePath, corpusRoot)
        }
    }

    private fun configuration(): CorpusConfiguration {
        return CorpusConfiguration(
            repositoryUrl = repositoryUrl.get(),
            revision = revision.get(),
            phase = phase.get().name.lowercase(),
            engines = engines.get().toList(),
            warmups = warmups.get(),
            rounds = rounds.get(),
            pin = pin.get(),
            versions = versions.get(),
            languages = languages.get(),
            requiredFeatures = requiredFeatures.get(),
            excludedFeatures = excludedFeatures.get(),
            tags = tags.get(),
            excludedTags = excludedTags.get(),
            size = size.orNull,
            duration = duration.orNull,
            targets = targets.get(),
            excludedTargets = excludedTargets.get(),
        )
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

    private fun targetExcluded(fixture: JsonObject): Boolean {
        return excludedTargets.get().any { target -> fixture.matches(target) }
    }

    private fun JsonObject.matches(target: String): Boolean {

        val name = string("name")
        val path = string("path")
        val stem = path.substringAfterLast('/').substringBeforeLast('.')

        return target == name || target == path || target == stem
    }

    private fun JsonObject.string(name: String): String {
        return getValue(name).jsonPrimitive.content
    }

    private companion object {
        const val CORPUS_ROOT = "corpus"
        const val FIXTURES_INDEX = "fixtures.json"
        const val RUN_FILE = "run.json"
    }
}
