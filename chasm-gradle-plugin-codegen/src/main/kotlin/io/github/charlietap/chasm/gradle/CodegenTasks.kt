package io.github.charlietap.chasm.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskProvider
import java.io.File

fun registerCodegenTask(
    project: Project,
    module: WasmModule,
    sourceSetName: String,
    classpath: Configuration,
    moduleBinary: File? = null,
): TaskProvider<CodegenTask> {
    val capitalizedSourceName = sourceSetName.replaceFirstChar { it.uppercase() }
    return project.tasks.register("codegenModule$capitalizedSourceName${module.name}", CodegenTask::class.java) { task ->
        task.group = "chasm"
        task.description = "Generates a typesafe Kotlin interface from a wasm binary"

        task.workerClasspath.from(classpath)

        moduleBinary?.let {
            task.binary.set(moduleBinary)
        } ?: task.binary.set(module.binary)
        task.allocator.set(module.allocator)
        task.config.set(module.codegenConfig)
        task.interfaceName.set(module.name)
        task.packageName.set(module.packageName)
        task.interfaceVisibility.set(module.interfaceVisibility)
        task.implementationVisibility.set(module.implementationVisibility)
        task.initializers.set(module.initializers)
        task.functions.set(module.functions)
        task.ignoredExports.set(module.ignoredExports)
        task.outputDirectory.set(project.layout.buildDirectory.dir("generated/kotlin/$sourceSetName"))
    }
}
