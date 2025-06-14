package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.moduleInfo
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.map
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor

@CacheableTask
abstract class CodegenTask @Inject constructor(
    private val workerExecutor: WorkerExecutor,
) : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val binary: RegularFileProperty

    @get:Input
    abstract val config: Property<CodegenConfig>

    @get:Input
    abstract val interfaceName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {

        val info = module(binary.get().asFile.readBytes())
        .map { module ->
            moduleInfo(module)
        }.expect("Failed to find module at path: ${binary.get().asFile.absolutePath}")

        val factory = WasmInterfaceFactory()
        val generator = WasmInterfaceGenerator()

        val instance = factory(config.get(), info)
        val interfaceSpec = generator(interfaceName.get(), packageName.get(), instance)

        val outputDir = outputDirectory.get().asFile
        interfaceSpec.writeTo(outputDir.toPath())
    }
}
