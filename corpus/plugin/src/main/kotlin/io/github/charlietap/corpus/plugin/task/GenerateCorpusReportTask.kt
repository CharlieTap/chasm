package io.github.charlietap.corpus.plugin.task

import io.github.charlietap.corpus.lib.report.CorpusConfiguration
import io.github.charlietap.corpus.lib.report.CorpusEnvironment
import io.github.charlietap.corpus.lib.report.CorpusFixtureResult
import io.github.charlietap.corpus.lib.report.CorpusGradleRuntime
import io.github.charlietap.corpus.lib.report.CorpusJavaRuntime
import io.github.charlietap.corpus.lib.report.CorpusMachine
import io.github.charlietap.corpus.lib.report.CorpusReport
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateCorpusReportTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val resultFiles: ConfigurableFileCollection

    @get:Input
    abstract val machine: Property<String>

    @get:Input
    abstract val repositoryUrl: Property<String>

    @get:Input
    abstract val revision: Property<String>

    @get:Input
    abstract val phase: Property<String>

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

    @get:Input
    abstract val gradleVersion: Property<String>

    @get:Input
    abstract val gradleJavaLanguageVersion: Property<String>

    @get:Input
    abstract val gradleJavaRuntimeVersion: Property<String>

    @get:Input
    abstract val gradleJvmVersion: Property<String>

    @get:Input
    abstract val gradleJavaVendor: Property<String>

    @get:Input
    abstract val testJavaLanguageVersion: Property<String>

    @get:Input
    abstract val testJavaRuntimeVersion: Property<String>

    @get:Input
    abstract val testJvmVersion: Property<String>

    @get:Input
    abstract val testJavaVendor: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    private val inputJson = Json { ignoreUnknownKeys = true }
    private val outputJson = Json { prettyPrint = true }

    @TaskAction
    fun generate() {
        val fixtureResults = resultFiles.files
            .asSequence()
            .map { file -> inputJson.decodeFromString<CorpusFixtureResult>(file.readText()) }
            .sortedWith(compareBy(CorpusFixtureResult::version, CorpusFixtureResult::fixture))
            .toList()

        val report = CorpusReport(
            schemaVersion = SCHEMA_VERSION,
            environment = CorpusEnvironment(
                machine = inputJson.decodeFromString<CorpusMachine>(machine.get()),
                gradle = CorpusGradleRuntime(
                    version = gradleVersion.get(),
                    jvm = CorpusJavaRuntime(
                        languageVersion = gradleJavaLanguageVersion.get(),
                        runtimeVersion = gradleJavaRuntimeVersion.get(),
                        jvmVersion = gradleJvmVersion.get(),
                        vendor = gradleJavaVendor.get(),
                    ),
                ),
                testJvm = CorpusJavaRuntime(
                    languageVersion = testJavaLanguageVersion.get(),
                    runtimeVersion = testJavaRuntimeVersion.get(),
                    jvmVersion = testJvmVersion.get(),
                    vendor = testJavaVendor.get(),
                ),
            ),
            configuration = CorpusConfiguration(
                repositoryUrl = repositoryUrl.get(),
                revision = revision.get(),
                phase = phase.get(),
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
            ),
            fixtures = fixtureResults,
        )

        val reportFile = outputFile.get().asFile
        reportFile.parentFile.mkdirs()
        reportFile.writeText(outputJson.encodeToString(report))
        logger.lifecycle("Corpus report: ${reportFile.absolutePath} (${fixtureResults.size} fixtures)")
    }

    private companion object {
        const val SCHEMA_VERSION = 1
    }
}
