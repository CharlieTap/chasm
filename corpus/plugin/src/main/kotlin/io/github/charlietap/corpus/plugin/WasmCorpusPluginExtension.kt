package io.github.charlietap.corpus.plugin

import io.github.charlietap.corpus.lib.CorpusPhase
import javax.inject.Inject
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

open class WasmCorpusPluginExtension @Inject constructor(
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

    val requiredFeatures: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    val excludedFeatures: ListProperty<String> = objects.listProperty(String::class.java)
        .convention(listOf("memory64", "simd", "relaxed-simd"))

    val tags: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

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

    private companion object {
        const val DIR_CORPUS = "wasm-corpus"
        const val DIR_CORPUS_FIXTURES = "wasm-corpus-fixtures"
        const val DIR_CORPUS_TESTS = "wasm-corpus-tests"
    }
}
