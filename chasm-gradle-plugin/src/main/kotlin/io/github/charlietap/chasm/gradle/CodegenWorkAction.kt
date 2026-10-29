package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.aot.inspectModule
import io.github.charlietap.chasm.bindgen.BindingGenerator
import io.github.charlietap.chasm.bindgen.BindingOptions
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.shapes.expect
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logging
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import io.github.charlietap.chasm.bindgen.CodegenConfig as BindgenCodegenConfig
import io.github.charlietap.chasm.bindgen.CodegenRuntime as BindgenCodegenRuntime
import io.github.charlietap.chasm.bindgen.ExportedAllocator as BindgenExportedAllocator
import io.github.charlietap.chasm.bindgen.FactoryVisibility as BindgenFactoryVisibility
import io.github.charlietap.chasm.bindgen.FunctionParameterDefinition as BindgenFunctionParameterDefinition
import io.github.charlietap.chasm.bindgen.ImplementationVisibility as BindgenImplementationVisibility
import io.github.charlietap.chasm.bindgen.InterfaceVisibility as BindgenInterfaceVisibility
import io.github.charlietap.chasm.bindgen.ReturnTypeDefinition as BindgenReturnTypeDefinition
import io.github.charlietap.chasm.bindgen.Scalar as BindgenScalar
import io.github.charlietap.chasm.bindgen.StringAllocationStrategy as BindgenStringAllocationStrategy
import io.github.charlietap.chasm.bindgen.StringEncodingStrategy as BindgenStringEncodingStrategy
import io.github.charlietap.chasm.bindgen.Type as BindgenType
import io.github.charlietap.chasm.bindgen.WasiLinking as BindgenWasiLinking
import io.github.charlietap.chasm.bindgen.WasmFunction as BindgenWasmFunction

internal interface CodegenWorkParameters : WorkParameters {
    val binary: RegularFileProperty
    val outputDirectory: DirectoryProperty
    val interfaceName: Property<String>
    val packageName: Property<String>
    val interfaceVisibility: Property<InterfaceVisibility>
    val implementationVisibility: Property<ImplementationVisibility>
    val factoryVisibility: Property<FactoryVisibility>
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
        val info = inspectModule(
            binaryFile.readBytes(),
            ModuleConfig(decodeNameSection = true),
        ).expect("Failed to find module at path: ${binaryFile.absolutePath}")

        val options = BindingOptions(
            interfaceName = params.interfaceName.get(),
            packageName = params.packageName.get(),
            config = params.config.get().toBindgen(),
            interfaceVisibility = BindgenInterfaceVisibility.valueOf(params.interfaceVisibility.get().name),
            implementationVisibility = BindgenImplementationVisibility.valueOf(params.implementationVisibility.get().name),
            factoryVisibility = BindgenFactoryVisibility.valueOf(params.factoryVisibility.get().name),
            allocator = params.allocator.orNull?.let { allocator ->
                BindgenExportedAllocator(allocator.allocationFunction, allocator.deallocationFunction)
            },
            initializers = params.initializers.get(),
            functions = params.functions.get().map { function -> function.toBindgen() },
            ignoredExports = params.ignoredExports.get(),
        )
        val specs = BindingGenerator(PluginLogger(logger)).generate(info, options)

        val outputDirectory = params.outputDirectory.get().asFile
        outputDirectory.listFiles()?.forEach { stale ->
            check(stale.deleteRecursively()) { "Failed to delete stale generated source: ${stale.absolutePath}" }
        }
        specs.forEach { spec ->
            spec.writeTo(outputDirectory)
        }
    }
}

private fun CodegenConfig.toBindgen() = BindgenCodegenConfig(
    generateTypesafeGlobalProperties = generateTypesafeGlobalProperties,
    generateTypesafeMemoryProperties = generateTypesafeMemoryProperties,
    generateSuspendingFactories = generateSuspendingFactories,
    runtime = BindgenCodegenRuntime.valueOf(runtime.name),
    wasi = BindgenWasiLinking.valueOf(wasi.name),
    compiledCode = compiledCode,
)

private fun WasmFunction.toBindgen() = BindgenWasmFunction(
    name = name,
    parameters = parameters.map { parameter ->
        BindgenFunctionParameterDefinition(
            name = parameter.name,
            type = parameter.type.toBindgen(),
            stringEncodingStrategy = parameter.stringEncodingStrategy?.toBindgen(),
            stringAllocationStrategy = parameter.stringAllocationStrategy?.let { strategy ->
                BindgenStringAllocationStrategy(strategy.freeAfterCall)
            },
        )
    },
    returnType = BindgenReturnTypeDefinition(
        type = returnType.type.toBindgen(),
        stringEncodingStrategy = returnType.stringEncodingStrategy?.toBindgen(),
    ),
)

private fun Type.toBindgen(): BindgenType = when (this) {
    is Scalar -> BindgenScalar.valueOf(name)
    else -> UnknownType(this)
}

private class UnknownType(private val type: Type) : BindgenType {
    override fun toString(): String = type.toString()
}

private fun StringEncodingStrategy.toBindgen() = BindgenStringEncodingStrategy.valueOf(name)
