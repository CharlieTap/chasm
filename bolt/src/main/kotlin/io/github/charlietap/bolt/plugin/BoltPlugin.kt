package io.github.charlietap.bolt.plugin

import io.github.charlietap.bolt.plugin.task.ConfigureCInteropTask
import io.github.charlietap.bolt.plugin.task.DownloadArchivesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.declarative.dsl.schema.FqName.Empty.packageName
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class BoltPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val extension = project.extensions.create<BoltExtension>("bolt")
        val kotlinExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

        project.afterEvaluate {
            val enabledKmpTargets = kotlinExtension.targets
                .mapTo(mutableSetOf()) { it.name }.apply {
                    remove("jvm")
                    remove("metadata")
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
                targets.set(enabledKmpTargets)
                outputFile.set(extension.defFile)
            }

            enabledKmpTargets.forEach { targetName ->
                kotlinExtension.targets.findByName(targetName)?.let { target ->
                    if (target is KotlinNativeTarget) {
                        val cinterop = target.compilations.getByName("main").cinterops.create(packageName) {
                            defFile(configureCinteropTask.flatMap { it.outputFile })
                        }

                        project.tasks.named(cinterop.interopProcessingTaskName).configure {
                            dependsOn(configureCinteropTask)
                        }
                    }
                } ?: println("Target $targetName not found or is not a KotlinNativeTarget")
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
