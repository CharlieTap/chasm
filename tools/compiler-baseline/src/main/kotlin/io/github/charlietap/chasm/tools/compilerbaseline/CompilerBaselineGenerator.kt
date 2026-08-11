package io.github.charlietap.chasm.tools.compilerbaseline

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.ast.module.FunctionNameSubsection
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.diagnostic.CompilerDiagnostics
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store

fun interface CompilerModuleDecoder {
    fun decode(config: ModuleConfig, bytes: ByteArray): Result<Module, ModuleDecoderError>
}

class ChasmModuleDecoder : CompilerModuleDecoder {
    override fun decode(config: ModuleConfig, bytes: ByteArray): Result<Module, ModuleDecoderError> =
        WasmModuleDecoder(config, bytes)
}

fun interface CompilerModuleInstantiator {
    fun instantiate(
        config: RuntimeConfig,
        store: Store,
        module: Module,
        imports: List<Import>,
        diagnostics: CompilerDiagnostics,
    ): Result<ModuleInstance, ModuleTrapError>
}

class ChasmModuleInstantiator : CompilerModuleInstantiator {
    override fun instantiate(
        config: RuntimeConfig,
        store: Store,
        module: Module,
        imports: List<Import>,
        diagnostics: CompilerDiagnostics,
    ): Result<ModuleInstance, ModuleTrapError> = ModuleInstantiator(config, store, module, imports, diagnostics)
}

fun interface ProgramInstructionCollectorFactory {
    fun create(): ProgramInstructionCollector
}

class DefaultProgramInstructionCollectorFactory(
    private val tagTranslator: CompilerInstructionTagTranslator,
) : ProgramInstructionCollectorFactory {
    override fun create(): ProgramInstructionCollector = IdentityProgramInstructionCollector(tagTranslator)
}

fun interface CompilerBaselineGenerator {
    fun generate(fixture: CompilerBaselineFixture): CompilerBaseline
}

class ModuleCompilerBaselineGenerator(
    private val importResolver: CompilerBaselineImportResolver,
    private val moduleDecoder: CompilerModuleDecoder,
    private val moduleInstantiator: CompilerModuleInstantiator,
    private val instructionCollectorFactory: ProgramInstructionCollectorFactory,
    private val runtimeConfig: RuntimeConfig = RuntimeConfig(),
) : CompilerBaselineGenerator {

    override fun generate(fixture: CompilerBaselineFixture): CompilerBaseline {
        val module = moduleDecoder.decode(ModuleConfig(decodeNameSection = true), fixture.bytes).unwrap()
        val instructionCollector = instructionCollectorFactory.create()
        val store = Store()
        val imports = importResolver.resolve(module, store)
        val instance = moduleInstantiator.instantiate(
            config = runtimeConfig,
            store = store,
            module = module,
            imports = imports,
            diagnostics = CompilerDiagnostics(instructionObserver = instructionCollector),
        ).unwrap()

        return CompilerBaseline(
            schemaVersion = SCHEMA_VERSION,
            modules = listOf(
                CompilerBaselineModule(
                    name = fixture.name,
                    functions = functions(module, instance, store, instructionCollector),
                ),
            ),
        )
    }

    private fun functions(
        module: Module,
        instance: ModuleInstance,
        store: Store,
        instructionCollector: ProgramInstructionCollector,
    ): List<CompilerBaselineFunction> {
        val functionNames = module.customs
            .filterIsInstance<NameData>()
            .firstOrNull()
            ?.subsections
            ?.filterIsInstance<FunctionNameSubsection>()
            ?.firstOrNull()
            ?.nameMap
            ?.associate { association -> association.idx.toInt() to association.name.name }
            .orEmpty()

        val functions = module.functions.map { function ->
            val functionIndex = function.idx.toInt()
            val address = instance.functionAddresses[functionIndex]
            val functionInstance = store.function(address) as FunctionInstance.WasmFunction
            functionIndex to functionInstance.callPlan.entryIp
        }

        return functions.mapIndexed { index, (functionIndex, entryIp) ->
            val endIp = functions.getOrNull(index + 1)?.second ?: store.program.size
            CompilerBaselineFunction(
                index = functionIndex,
                name = functionNames[functionIndex] ?: "\$func$functionIndex",
                instructions = instructionCollector.instructions(store.program, entryIp, endIp),
            )
        }
    }

    private companion object {
        const val SCHEMA_VERSION = 1
    }
}
