package io.github.charlietap.corpus.plugin

import io.github.charlietap.corpus.lib.CorpusPhase
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

open class WasmCorpusPluginExtension
    @Inject
    constructor(
        layout: ProjectLayout,
        objects: ObjectFactory,
    ) {
        val corpusRepositoryUrl: Property<String> = objects.property(String::class.java)
            .convention("https://github.com/CharlieTap/wasm-corpus.git")

        val corpusRef: Property<String> = objects.property(String::class.java).convention("main")

        val corpusRunner: Property<String> = objects.property(String::class.java)

        val testPackageName: Property<String> = objects.property(String::class.java)

        val phase: Property<CorpusPhase> = objects.property(CorpusPhase::class.java).convention(CorpusPhase.INVOCATION)

        val versions: ListProperty<String> = objects.listProperty(String::class.java).convention(listOf("1.0"))

        val languages: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

        val requiredFeatures: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

        val excludedFeatures: ListProperty<String> = objects.listProperty(String::class.java)
            .convention(listOf("memory64", "simd", "relaxed-simd"))

        val tags: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

        val excludedTags: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

        val size: Property<String> = objects.property(String::class.java)

        val duration: Property<String> = objects.property(String::class.java)

        val targets: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

        val excludedTargets: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

        val corpusDirectory: DirectoryProperty = objects.directoryProperty().convention(
            layout.buildDirectory.dir(DIR_CORPUS),
        )

        val corpusFixtureDirectory: DirectoryProperty = objects.directoryProperty().convention(
            layout.buildDirectory.dir(DIR_CORPUS_FIXTURES),
        )

        val corpusTestsDirectory: DirectoryProperty = objects.directoryProperty().convention(
            layout.buildDirectory.dir(DIR_CORPUS_TESTS),
        )

        val corpusResourcesDirectory: DirectoryProperty = objects.directoryProperty().convention(
            layout.buildDirectory.dir(DIR_CORPUS_RESOURCES),
        )

        val corpusResultsDirectory: DirectoryProperty = objects.directoryProperty().convention(
            layout.buildDirectory.dir(DIR_CORPUS_RESULTS),
        )

        val corpusReportFile: RegularFileProperty = objects.fileProperty().convention(
            layout.buildDirectory.file(FILE_CORPUS_REPORT),
        )

        val corpusBaselineDirectory: DirectoryProperty = objects.directoryProperty().convention(
            layout.settingsDirectory.dir(DIR_CORPUS_BASELINE),
        )

        private companion object {
            const val DIR_CORPUS = "wasm-corpus"
            const val DIR_CORPUS_FIXTURES = "wasm-corpus-fixtures"
            const val DIR_CORPUS_TESTS = "wasm-corpus-tests"
            const val DIR_CORPUS_RESOURCES = "wasm-corpus-resources"
            const val DIR_CORPUS_RESULTS = "wasm-corpus-results"
            const val DIR_CORPUS_BASELINE = "baselines/corpus"
            const val FILE_CORPUS_REPORT = "reports/corpus/results.json"
        }
    }
