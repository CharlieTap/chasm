package io.github.charlietap.chasm.gradle

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME

class ChasmPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val extension = project.extensions.create<ChasmExtension>("chasm")

        project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val mpp = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

            extension.modules.configureEach {
                val task = registerCodegenTask(project, this, "commonMain")
                mpp.sourceSets.getByName("commonMain").kotlin.srcDir(task.flatMap { it.outputDirectory })
            }
        }

        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            val jvmExtension = project.extensions.getByType(KotlinJvmProjectExtension::class.java)
            val mainCompilation = jvmExtension.target.compilations.getByName(MAIN_COMPILATION_NAME)

            extension.modules.configureEach {
                val task = registerCodegenTask(project, this, MAIN_COMPILATION_NAME)
                mainCompilation.defaultSourceSet {
                    kotlin.srcDir(task.flatMap { it.outputDirectory })
                }
            }
        }

        project.plugins.withId("com.android.base") {
            val androidExtension = project.extensions.getByType(BaseExtension::class.java)
            val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
            androidComponents.onVariants { variant: Variant ->
                extension.modules.configureEach {
                    val task = registerCodegenTask(project, this, variant.name)
                    variant.sources.java?.addGeneratedSourceDirectory(task, CodegenTask::outputDirectory)
                    androidExtension.sourceSets.getByName(variant.name).kotlin.srcDir(project.layout.buildDirectory.dir("generated/java/${task.name}"))
                }
            }
        }

        project.gradle.projectsEvaluated {
            if (System.getProperty("idea.sync.active")?.toBoolean() == true) {
                val tasksToTrigger = project.tasks.withType(CodegenTask::class.java).map { it.name }
                val sp = project.gradle.startParameter
                sp.setTaskNames(sp.taskNames + tasksToTrigger)
            }
        }
    }

    private fun registerCodegenTask(
        project: Project,
        module: WasmModule,
        sourceSetName: String,
    ): TaskProvider<CodegenTask> {
        val capitalizedSourceName = sourceSetName.replaceFirstChar { it.uppercase() }
        return project.tasks.register<CodegenTask>("codegenModule$capitalizedSourceName${module.name}") {
            group = "chasm"
            description = "Generates a typesafe Kotlin interface from a wasm binary"
            binary.set(module.binary)
            config.set(module.codegenConfig)
            interfaceName.set(module.name)
            packageName.set(module.packageName)
            outputDirectory.set(project.layout.buildDirectory.dir("generated/kotlin/$sourceSetName"))
        }
    }
}
