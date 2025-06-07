package io.github.charlietap.chasm.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class ChasmPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val extension = project.extensions.create<ChasmExtension>("chasm")

        project.afterEvaluate {
            when (extension.mode.get()) {
                Mode.CONSUMER -> {
                    registerCodegenTask()
                }
//                Mode.PRODUCER -> {
//                    project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
//                    val linkTask = project.tasks.named("linkReleaseExecutableWasm")
////                    registerGenerateTask(project, linkTask.flatMap { it.outputs.files.singleFile })
//                    project.tasks.named("generateChasmInterface").configure {
////                        it.dependsOn(linkTask)
//                    }
//                    project.extensions.configure<KotlinMultiplatformExtension> {
//                        sourceSets.getByName("commonMain").kotlin.srcDir(
//                            project.layout.buildDirectory.dir("generated/sources/chasm")
//                        )
//                    }
//                }
                else -> throw IllegalArgumentException("Unknown mode: ${extension.mode.get()}")
            }
        }
    }

    private fun Project.registerCodegenTask() {
        tasks.register<CodegenTask>("codegen") {
            group = "chasm"
            description = "Generates a typesafe Kotlin interface from a wasm binary"
            binary.set(layout.buildDirectory.file("wasm/test.wasm"))
            outputDirectory.set(layout.buildDirectory.dir("generated/sources/chasm"))
        }
    }
}
