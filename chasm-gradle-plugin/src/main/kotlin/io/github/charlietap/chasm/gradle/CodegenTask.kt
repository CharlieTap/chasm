package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.moduleInfo
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.map
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
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

        @get:Input
        abstract val interfaceVisibility: Property<TypeVisibility>

        @get:Input
        abstract val implementationVisibility: Property<TypeVisibility>

        @get:Input
        abstract val initializers: SetProperty<String>

        @get:Input
        abstract val functions: ListProperty<WasmFunction>

        @get:OutputDirectory
        abstract val outputDirectory: DirectoryProperty

        @TaskAction
        fun generate() {

            val moduleConfig = ModuleConfig(
                decodeNameSection = true,
            )
            val info = module(binary.get().asFile.readBytes(), moduleConfig)
                .map { module ->
                    moduleInfo(module)
                }.expect("Failed to find module at path: ${binary.get().asFile.absolutePath}")

            val factory = WasmInterfaceFactory()
            val generator = WasmInterfaceGenerator()

            val logger: (String) -> Unit = {
                logger.lifecycle(it)
            }
            val data = factory(
                interfaceName = interfaceName.get(),
                packageName = packageName.get(),
                config = config.get(),
                info = info,
                initializers = initializers.get(),
                wasmFunctions = functions.get(),
                logger = logger,
            )
            val specs = generator(interfaceVisibility.get(), implementationVisibility.get(), data)

            val outputDir = outputDirectory.get().asFile
            specs.forEach { spec ->
                spec.writeTo(outputDir)
            }
        }
    }
