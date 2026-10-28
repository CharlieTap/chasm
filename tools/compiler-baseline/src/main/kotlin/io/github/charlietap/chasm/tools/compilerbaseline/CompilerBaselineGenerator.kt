package io.github.charlietap.chasm.tools.compilerbaseline

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.ast.module.FunctionNameSubsection
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.ModuleCompiler
import io.github.charlietap.chasm.compiler.diagnostic.CompilerDiagnostics
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import kotlinx.serialization.json.JsonObject

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
    ): Result<ModuleInstance, ModuleTrapError> =
        ModuleInstantiator(
            config = config,
            store = store,
            module = module,
            imports = imports,
            codeInstaller = { compileStore, instance, runtimeTypes, types ->
                ModuleCompiler(compileStore, module, instance, runtimeTypes, types, diagnostics)
            },
            startInvoker = { invokeConfig, invokeStore, instance, address ->
                FunctionInvoker(invokeConfig, invokeStore, instance, address, emptyList()).map { }
            },
        )
}

fun interface ProgramInstructionCollectorFactory {
    fun create(): ProgramInstructionCollector
}

fun interface CompilerBaselineGenerator {
    fun generate(fixture: CompilerBaselineFixture): CompilerBaseline
}

class ModuleCompilerBaselineGenerator(
    private val importResolver: CompilerBaselineImportResolver,
    private val moduleDecoder: CompilerModuleDecoder,
    private val moduleInstantiator: CompilerModuleInstantiator,
    private val instructionCollectorFactory: ProgramInstructionCollectorFactory,
    private val tagTranslator: CompilerInstructionTagTranslator,
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

        val renderer = InstructionRenderer(ModuleSymbols(instance, store))
        val supplements = DispatchSupplements()
        val functions = module.functions.map { function ->
            val functionIndex = function.idx.toInt()
            val address = instance.functionAddresses[functionIndex]
            functionIndex to (store.function(address) as FunctionInstance.WasmFunction).callStrategy
        }

        return functions.mapIndexed { index, (functionIndex, strategy) ->
            val entryIp = strategy.entryIp
            val endIp = functions.getOrNull(index + 1)?.second?.entryIp ?: store.program.size
            val instructions = instructionCollector.instructions(store.program, entryIp, endIp)
            val exceptions = store.program.exceptionTable(entryIp)?.takeIf { table -> table.entryIp == entryIp }
            CompilerBaselineFunction(
                index = functionIndex,
                name = functionNames[functionIndex] ?: "\$func$functionIndex",
                frameSlots = strategy.frameSlots,
                activationHeaderSlot = strategy.interfaceSlotCount,
                localInitialization = renderer.localInitialization(strategy.localInitialization),
                exceptions = exceptions?.let(renderer::exceptions),
                instructions = instructions.mapIndexed { offset, observed ->
                    val immediates = renderer.immediates(observed.instruction, entryIp) +
                        supplements.immediates(observed.instruction, observed.dispatchable)
                    CompilerBaselineInstruction(
                        offset = offset,
                        tag = tagTranslator.translate(observed.instruction),
                        immediates = immediates.takeIf { it.isNotEmpty() }?.let(::JsonObject),
                    )
                },
            )
        }
    }

    private companion object {
        const val SCHEMA_VERSION = 2
    }
}
