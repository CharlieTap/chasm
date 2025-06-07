package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.moduleInfo
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.map
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
import javax.inject.Inject

@CacheableTask
abstract class CodegenTask
    @Inject
    constructor() : DefaultTask() {

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

            val logger: (String) -> Unit = {
                logger.warn(it)
            }
            val data = factory(config.get(), info, logger)
            val specs = generator(interfaceName.get(), packageName.get(), data)

            val outputDir = outputDirectory.get().asFile
            specs.forEach { spec ->
                spec.writeTo(outputDir)
            }
        }
    }
