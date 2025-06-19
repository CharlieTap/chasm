package io.github.charlietap.chasm.gradle

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.BaseExtension
import kotlin.jvm.java
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.IdeaExtPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

class ChasmPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val extension = project.extensions.create<ChasmExtension>("chasm")

        project.rootProject.pluginManager.apply(IdeaExtPlugin::class.java)

        project.plugins.withType(KotlinBasePlugin::class.java) {
            val multiplatform = project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")

            val kotlinSourceSet = if(multiplatform) {
                project.extensions
                    .getByType(org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension::class.java)
                    .sourceSets
                    .named("commonMain")
            } else {
                project.extensions
                    .getByType(org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension::class.java)
                    .sourceSets
                    .named("main")
            }

            kotlinSourceSet.configure {
                val tasks = project.configureCodegenTasks(extension, this.name)
                tasks.forEach { task ->
                    this.kotlin.srcDir(task.flatMap { it.outputDirectory })
                }
            }
        }

        project.plugins.withId("com.android.base") {
            val androidExtension = project.extensions.getByType(BaseExtension::class.java)
            val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
            androidComponents.onVariants { variant: Variant ->
                val tasks = project.configureCodegenTasks(extension, variant.name)
                tasks.forEach { task ->
                    variant.sources.java?.addGeneratedSourceDirectory(task, CodegenTask::outputDirectory)
                    androidExtension.sourceSets.getByName(variant.name).kotlin.srcDir(project.layout.buildDirectory.dir("generated/java/${task.name}"))
                }
            }
        }

        project.gradle.projectsEvaluated {
            val tasksToTrigger = project.tasks.withType(CodegenTask::class.java).toTypedArray()
            val ideaModel = project.rootProject.extensions.getByType(IdeaModel::class.java)
            ideaModel.project.settings {
                taskTriggers.afterSync(*tasksToTrigger)
            }
        }
    }

    private fun Project.configureCodegenTasks(
        extension: ChasmExtension,
        sourceSetName: String,
    ): List<TaskProvider<CodegenTask>> {
        return extension.modules.map { module ->
            val capitalizedSourceName = sourceSetName.replaceFirstChar { it.uppercase() }
            tasks.register<CodegenTask>("codegenModule$capitalizedSourceName${module.name}") {
                group = "chasm"
                description = "Generates a typesafe Kotlin interface from a wasm binary"
                binary.set(module.binary)
                config.set(module.codegenConfig)
                interfaceName.set(module.name)
                packageName.set(module.packageName)
                outputDirectory.set(layout.buildDirectory.dir("generated/kotlin/$sourceSetName"))
            }
        }
    }
}
