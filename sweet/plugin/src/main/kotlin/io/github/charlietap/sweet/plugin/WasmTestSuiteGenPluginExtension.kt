package io.github.charlietap.sweet.plugin

import io.github.charlietap.sweet.lib.SemanticPhase
import javax.inject.Inject
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.io.Serializable

data class Proposal(
    val name: String,
    val phaseSupport: SemanticPhase = SemanticPhase.EXECUTION,
): Serializable

data class LimitedSupport(
    val patterns: Set<String>,
    val phaseSupport: SemanticPhase,
): Serializable

open class WasmTestSuiteGenPluginExtension @Inject constructor(
    layout: ProjectLayout,
    objects: ObjectFactory,
) {
    val wasmToolsVersion: Property<String> = objects.property(String::class.java)

    val testSuiteCommit: Property<String> = objects.property(String::class.java)

    val scriptRunner: Property<String> = objects.property(String::class.java)

    val testPackageName: Property<String> = objects.property(String::class.java)

    val testSuiteDirectory: DirectoryProperty = objects.directoryProperty().convention(layout.buildDirectory.dir(
        DIR_TEST_SUITE
    ))

    val testSuiteIntermediateDirectory: DirectoryProperty = objects.directoryProperty().convention(layout.buildDirectory.dir(
        DIR_TEST_SUITE_INTERMEDIATE
    ))

    val testSuiteTestsDirectory: DirectoryProperty = objects.directoryProperty().convention(layout.buildDirectory.dir(
        DIR_TEST_SUITE_TESTS
    ))

    val proposals: ListProperty<Proposal> = objects.listProperty(Proposal::class.java).convention(emptyList())

    val limitedSupport: ListProperty<LimitedSupport> = objects.listProperty(LimitedSupport::class.java).convention(emptyList())

    val excludes: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    private companion object {
        const val DIR_TEST_SUITE = "wasm-testsuite"
        const val DIR_TEST_SUITE_INTERMEDIATE = "wasm-testsuite-intermediates"
        const val DIR_TEST_SUITE_TESTS = "wasm-testsuite-tests"
    }
}
