package io.github.charlietap.bolt.plugin

import io.github.charlietap.bolt.plugin.task.ConfigureCInteropTask
import io.github.charlietap.bolt.plugin.task.DownloadArchivesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class BoltPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val extension = project.extensions.create<BoltExtension>("bolt")
        val kotlinExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

        val enabledKmpTargets = kotlinExtension.targets.elements.map { targets ->
            targets.filterIsInstance<KotlinNativeTarget>().mapTo(mutableSetOf()) { it.name }
        }

        val downloadArtifactsTask = project.tasks.register<DownloadArchivesTask>(
            TASK_NAME_DOWNLOAD_ARTIFACTS,
        ) {
            description = TASK_DESCRIPTION_DOWNLOAD_ARTIFACTS
            group = GROUP

            library.set(extension.library)
            url.set(extension.url)
            archiveFormat.set(extension.archiveFormat)
            targets.set(enabledKmpTargets)
            outputDirectory.set(extension.artifactsDir)
        }

        val configureCinteropTask = project.tasks.register<ConfigureCInteropTask>(
            TASK_NAME_CONFIGURE_CINTEROP,
        ) {
            description = TASK_DESCRIPTION_CONFIGURE_CINTEROP
            group = GROUP

            library.set(extension.library)

            artifactsDir.set(downloadArtifactsTask.flatMap { it.outputDirectory })
            linkerOptions.set(extension.linkerOptions)
            targets.set(enabledKmpTargets)
            outputFile.set(extension.defFile)
        }

        kotlinExtension.targets.withType(KotlinNativeTarget::class.java).configureEach {
            val cinterop = compilations.getByName(MAIN_COMPILATION_NAME).cinterops.create(GROUP) {
                defFile(configureCinteropTask.flatMap { it.outputFile })
            }

            project.tasks.named(cinterop.interopProcessingTaskName).configure {
                dependsOn(configureCinteropTask)
            }
        }
    }

    private companion object {

        const val GROUP = "bolt"

        const val TASK_NAME_DOWNLOAD_ARTIFACTS = "downloadArtifacts"
        const val TASK_NAME_CONFIGURE_CINTEROP = "configureCinterop"

        const val TASK_DESCRIPTION_DOWNLOAD_ARTIFACTS = "Downloads static library and header artifacts from url"
        const val TASK_DESCRIPTION_CONFIGURE_CINTEROP = "Configures Kotlin Multiplatform cinterop to use downloaded artifacts"
    }
}
