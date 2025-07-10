package io.github.charlietap.chasm.gradle

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import java.io.File
import kotlin.jvm.java

class ChasmPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val extension = project.extensions.create<ChasmExtension>("chasm")

        project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val mpp = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

            project.afterEvaluate {
                extension.modules.configureEach {
                    when (extension.mode.get()) {
                        Mode.CONSUMER -> {
                            val task = registerCodegenTask(project, this, "commonMain")
                            mpp.sourceSets.getByName("commonMain").kotlin.srcDir(task.flatMap { it.outputDirectory })
                        }
                        Mode.PRODUCER -> {
                            val wasmTargets = mpp.targets.filter { target ->
                                target.name.startsWith("wasmJs") || target.name.startsWith("wasmWasi")
                            }
                            val nonWasmSourceSets = mpp.sourceSets.filter {
                                it.name.lowercase().contains("main") &&
                                    !it.name.lowercase().contains("common") &&
                                    !it.name.lowercase().contains("wasm")
                            }

                            if (wasmTargets.isEmpty()) {
                                project.logger.warn("Producer mode requires at least one WASM target (wasmJs or wasmWasi)")
                                return@configureEach
                            }

                            wasmTargets.forEach { target ->
                                val executable = (target as KotlinJsIrTarget)
                                val mjsFile = executable.binaries.first().mainFile.get().asFile
                                val binary = File(mjsFile.parentFile, mjsFile.nameWithoutExtension + ".wasm")

                                val task = registerCodegenTask(project, this, target.name, binary)
                                task.dependsOn(executable.binaries.first().linkTask)

                                nonWasmSourceSets.forEach { nonWasmSourceSet ->
                                    nonWasmSourceSet.kotlin.srcDir(task.flatMap { it.outputDirectory })

                                    val targetName = nonWasmSourceSet.name.removeSuffix("Main")
                                    val taskName = "${targetName}ProcessResources"
                                    project.tasks.named(taskName, Copy::class.java).configure {
                                        dependsOn(executable.binaries.first().linkTask)
                                        inputs.file(binary)
                                        from({ binary }) {
                                            rename { _ -> "producer.wasm" }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            val jvmExtension = project.extensions.getByType(KotlinJvmProjectExtension::class.java)
            val mainCompilation = jvmExtension.target.compilations.getByName(MAIN_COMPILATION_NAME)

            extension.modules.configureEach {
                if (extension.mode.get() == Mode.PRODUCER) {
                    project.logger.error("Producer mode is only supported for Kotlin Multiplatform projects with WASM targets")
                    return@configureEach
                }

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
                    if (extension.mode.get() == Mode.PRODUCER) {
                        project.logger.error("Producer mode is only supported for Kotlin Multiplatform projects with WASM targets")
                        return@configureEach
                    }

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
        moduleBinary: File? = null,
    ): TaskProvider<CodegenTask> {
        val capitalizedSourceName = sourceSetName.replaceFirstChar { it.uppercase() }
        return project.tasks.register<CodegenTask>("codegenModule$capitalizedSourceName${module.name}") {
            group = "chasm"
            description = "Generates a typesafe Kotlin interface from a wasm binary"
            moduleBinary?.let {
                binary.set(moduleBinary)
            } ?: binary.set(module.binary)
            config.set(module.codegenConfig)
            interfaceName.set(module.name)
            packageName.set(module.packageName)
            initializers.set(module.initializers)
            functions.set(module.functions)
            outputDirectory.set(project.layout.buildDirectory.dir("generated/kotlin/$sourceSetName"))
        }
    }
}
