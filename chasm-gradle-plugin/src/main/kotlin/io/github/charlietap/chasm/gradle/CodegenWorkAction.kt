package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.moduleInfo
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.map
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logging
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters

internal interface CodegenWorkParameters : WorkParameters {
    val binary: RegularFileProperty
    val outputDirectory: DirectoryProperty
    val interfaceName: Property<String>
    val packageName: Property<String>
    val interfaceVisibility: Property<TypeVisibility>
    val implementationVisibility: Property<TypeVisibility>
    val config: Property<CodegenConfig>
    val allocator: Property<ExportedAllocator>
    val initializers: SetProperty<String>
    val functions: ListProperty<WasmFunction>
    val ignoredExports: SetProperty<String>
}

internal abstract class CodegenWorkAction : WorkAction<CodegenWorkParameters> {

    private val logger = Logging.getLogger(CodegenWorkAction::class.java)

    override fun execute() {
        val params = parameters
        val binaryFile = params.binary.get().asFile
        val config = params.config.get()
        val info = module(
            binaryFile.readBytes(),
            ModuleConfig(decodeNameSection = true),
        ).map { wasmModule ->
            moduleInfo(wasmModule)
        }.expect("Failed to find module at path: ${binaryFile.absolutePath}")

        val data = WasmInterfaceFactory()(
            interfaceName = params.interfaceName.get(),
            packageName = params.packageName.get(),
            config = config,
            info = info,
            allocator = params.allocator.orNull,
            initializers = params.initializers.get(),
            wasmFunctions = params.functions.get(),
            ignoredExports = params.ignoredExports.get(),
            logger = PluginLogger(logger),
        )

        val specs = WasmInterfaceGenerator()(
            interfaceVisibility = params.interfaceVisibility.get(),
            implementationVisibility = params.implementationVisibility.get(),
            wasmInterface = data,
            config = config,
        )

        val outputDirectory = params.outputDirectory.get().asFile
        specs.forEach { spec ->
            spec.writeTo(outputDirectory)
        }
    }
}
