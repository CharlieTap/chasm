package io.github.charlietap.corpus.plugin.task

import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusSettings
import io.github.charlietap.corpus.plugin.spec.GeneratedFixture
import io.github.charlietap.corpus.plugin.spec.corpusTestFileSpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateCorpusTestsTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val fixturesIndex: RegularFileProperty

    @get:Input
    abstract val corpusRunner: Property<String>

    @get:Input
    abstract val testPackageName: Property<String>

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

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {

        val fixtures = Json.parseToJsonElement(fixturesIndex.get().asFile.readText()).jsonArray.map { element ->
            val descriptor = element.jsonObject
            GeneratedFixture(
                name = descriptor.getValue("name").jsonPrimitive.content,
                version = descriptor.getValue("version").jsonPrimitive.content,
                path = descriptor.getValue("path").jsonPrimitive.content,
            )
        }
        val settings = CorpusSettings(phase.get(), engines.get(), warmups.get(), rounds.get(), pin.get())

        outputDirectory.get().asFile.deleteRecursively()
        corpusTestFileSpec(testPackageName.get(), corpusRunner.get(), settings, fixtures)
            .writeTo(outputDirectory.get().asFile)
    }
}
