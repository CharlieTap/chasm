package io.github.charlietap.sweet.plugin

import javax.inject.Inject
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

open class WasmTestSuiteGenPluginExtension @Inject constructor(
    layout: ProjectLayout,
    objects: ObjectFactory,
) {
    val wabtVersion: Property<String> = objects.property(String::class.java)

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

    val proposals: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    val excludes: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    private companion object {
        const val DIR_TEST_SUITE = "wasm-testsuite"
        const val DIR_TEST_SUITE_INTERMEDIATE = "wasm-testsuite-intermediates"
        const val DIR_TEST_SUITE_TESTS = "wasm-testsuite-tests"
    }
}
