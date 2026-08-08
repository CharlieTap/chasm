package io.github.charlietap.sweet.plugin

import io.github.charlietap.sweet.lib.SemanticPhase
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.io.Serializable
import javax.inject.Inject

data class PhaseLimit(
    val patterns: Set<String>,
    val phaseSupport: SemanticPhase,
) : Serializable

open class SuiteSource
    @Inject
    constructor(
        private val sourceName: String,
        objects: ObjectFactory,
    ) : Named {

        override fun getName(): String = sourceName

        val repositoryUrl: Property<String> = objects.property(String::class.java)

        val revision: Property<String> = objects.property(String::class.java)

        val testDirectory: Property<String> = objects.property(String::class.java).convention(".")

        val includes: ListProperty<String> = objects.listProperty(String::class.java).convention(listOf("**/*.wast"))

        val excludes: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

        val phaseSupport: Property<SemanticPhase> = objects.property(SemanticPhase::class.java)
            .convention(SemanticPhase.EXECUTION)

        val phaseLimits: ListProperty<PhaseLimit> = objects.listProperty(PhaseLimit::class.java).convention(emptyList())
    }

open class WasmTestSuiteGenPluginExtension
    @Inject
    constructor(
        layout: ProjectLayout,
        objects: ObjectFactory,
    ) {
        val wasmToolsVersion: Property<String> = objects.property(String::class.java)

        val scriptRunner: Property<String> = objects.property(String::class.java)

        val testPackageName: Property<String> = objects.property(String::class.java)

        val repositoriesDirectory: DirectoryProperty = objects.directoryProperty().convention(
            layout.buildDirectory.dir(DIR_REPOSITORIES),
        )

        val intermediatesDirectory: DirectoryProperty = objects.directoryProperty().convention(
            layout.buildDirectory.dir(DIR_INTERMEDIATES),
        )

        val testsDirectory: DirectoryProperty = objects.directoryProperty().convention(
            layout.buildDirectory.dir(DIR_TESTS),
        )

        val sources: NamedDomainObjectContainer<SuiteSource> = objects.domainObjectContainer(SuiteSource::class.java)

        fun sources(action: Action<NamedDomainObjectContainer<SuiteSource>>) {
            action.execute(sources)
        }

        private companion object {
            const val DIR_REPOSITORIES = "sweet/repositories"
            const val DIR_INTERMEDIATES = "sweet/intermediates"
            const val DIR_TESTS = "generated/sweet"
        }
    }
