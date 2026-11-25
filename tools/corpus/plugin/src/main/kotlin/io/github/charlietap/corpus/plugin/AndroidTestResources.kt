package io.github.charlietap.corpus.plugin

import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import io.github.charlietap.corpus.plugin.task.PrepareCorpusResourcesTask
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidTestResources(
    private val project: Project,
    private val fixtures: RegisteredFixtures,
) {

    fun register() {

        val components = project.extensions.getByType<KotlinMultiplatformAndroidComponentsExtension>()

        components.onVariants { variant ->
            val tests = variant.hostTests.values + variant.deviceTests.values
            tests.forEach { test ->
                val resources = checkNotNull(test.sources.resources) { "${test.name} has no Java resources" }
                resources.addGeneratedSourceDirectory(fixtures.prepareResources, PrepareCorpusResourcesTask::outputDirectory)
            }
        }
    }
}
