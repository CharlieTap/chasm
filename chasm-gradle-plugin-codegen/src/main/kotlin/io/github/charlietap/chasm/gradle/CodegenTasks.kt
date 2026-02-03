package io.github.charlietap.chasm.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File

fun registerCodegenTask(
    project: Project,
    module: WasmModule,
    sourceSetName: String,
    classpath: Configuration,
    moduleBinary: File? = null,
): TaskProvider<CodegenTask> {
    val capitalizedSourceName = sourceSetName.replaceFirstChar { it.uppercase() }
    return project.tasks.register<CodegenTask>("codegenModule$capitalizedSourceName${module.name}") {
        group = "chasm"
        description = "Generates a typesafe Kotlin interface from a wasm binary"

        workerClasspath.from(classpath)

        moduleBinary?.let {
            binary.set(moduleBinary)
        } ?: binary.set(module.binary)
        allocator.set(module.allocator)
        config.set(module.codegenConfig)
        interfaceName.set(module.name)
        packageName.set(module.packageName)
        interfaceVisibility.set(module.interfaceVisibility)
        implementationVisibility.set(module.implementationVisibility)
        initializers.set(module.initializers)
        functions.set(module.functions)
        ignoredExports.set(module.ignoredExports)
        outputDirectory.set(project.layout.buildDirectory.dir("generated/kotlin/$sourceSetName"))
    }
}
