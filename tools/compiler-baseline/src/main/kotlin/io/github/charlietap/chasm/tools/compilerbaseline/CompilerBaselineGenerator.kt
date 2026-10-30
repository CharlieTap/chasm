package io.github.charlietap.chasm.tools.compilerbaseline

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.ast.module.FunctionNameSubsection
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.chasm.FunctionLayout
import io.github.charlietap.chasm.compiler.backend.chasm.ModuleLinker
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
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
        code: CirModule,
        imports: List<Import>,
    ): Result<ModuleInstance, ModuleTrapError>
}

class ChasmModuleInstantiator : CompilerModuleInstantiator {
    override fun instantiate(
        config: RuntimeConfig,
        store: Store,
        module: Module,
        code: CirModule,
        imports: List<Import>,
    ): Result<ModuleInstance, ModuleTrapError> =
        ModuleInstantiator(
            config = config,
            store = store,
            module = ModuleDeclarationsFactory(module),
            imports = imports,
            codeInstaller = { linkStore, instance, _, _ -> Ok(ModuleLinker(linkStore, instance, module, code)) },
        )
}

fun interface CompilerBaselineGenerator {
    fun generate(fixture: CompilerBaselineFixture): CompilerBaseline
}

class ModuleCompilerBaselineGenerator(
    private val importResolver: CompilerBaselineImportResolver,
    private val moduleDecoder: CompilerModuleDecoder,
    private val moduleInstantiator: CompilerModuleInstantiator,
    private val runtimeConfig: RuntimeConfig = RuntimeConfig(),
    private val stores: List<CompilerBaselineStore> = listOf(CompilerBaselineStore.DEFAULT),
) : CompilerBaselineGenerator {

    override fun generate(fixture: CompilerBaselineFixture): CompilerBaseline {
        val module = moduleDecoder.decode(ModuleConfig(decodeNameSection = true), fixture.bytes).unwrap()
        val cir = ModuleCompiler(module).unwrap()
        return CompilerBaseline(
            schemaVersion = SCHEMA_VERSION,
            modules = stores.map { configuration ->
                val store = Store(config = configuration.config)
                store.fuel.remaining = Long.MAX_VALUE
                val imports = importResolver.resolve(module, store)
                val instance = moduleInstantiator.instantiate(runtimeConfig, store, module, cir, imports).unwrap()
                CompilerBaselineModule(
                    name = configuration.suffix?.let { suffix -> "${fixture.name}.$suffix" } ?: fixture.name,
                    functions = functions(module, cir, instance, store),
                )
            },
        )
    }

    private fun functions(
        module: Module,
        cir: CirModule,
        instance: ModuleInstance,
        store: Store,
    ): List<CompilerBaselineFunction> {
        val functionNames = functionNames(module)
        val renderer = DispatcherRenderer(ModuleSymbols(instance, store))
        val program = store.program

        return cir.functions.map { function ->
            val functionIndex = function.index.index
            val strategy = (store.function(instance.functionAddresses[functionIndex]) as FunctionInstance.WasmFunction).callStrategy
            val entryIp = strategy.entryIp
            val layout = FunctionLayout(function, store)
            check(entryIp + layout.size <= program.size) { "function $functionIndex runs past the end of the program" }
            val positions = (0 until layout.size).map(layout::position)
            val unlinkedCheckpoints = function.instructions.indices - positions.toSet()
            val exceptions = program.exceptionTable(entryIp)?.takeIf { table -> table.entryIp == entryIp }
            CompilerBaselineFunction(
                index = functionIndex,
                name = functionNames[functionIndex] ?: "\$func$functionIndex",
                frameSlots = strategy.frameSlots,
                activationHeaderSlot = strategy.interfaceSlotCount,
                localInitialization = renderer.localInitialization(strategy.localInitialization),
                unlinkedCheckpoints = unlinkedCheckpoints.takeIf { it.isNotEmpty() },
                exceptions = exceptions?.let(renderer::exceptions),
                instructions = positions.mapIndexed { offset, position ->
                    val dispatchable = program.instructions[entryIp + offset]
                    CompilerBaselineInstruction(
                        offset = offset,
                        position = position,
                        dispatcher = renderer.dispatcher(dispatchable),
                        variant = renderer.variant(dispatchable),
                        fields = renderer.fields(dispatchable, entryIp),
                    )
                },
            )
        }
    }

    private companion object {
        const val SCHEMA_VERSION = 3
    }
}

internal fun functionNames(
    module: Module,
): Map<Int, String> = module.customs
    .filterIsInstance<NameData>()
    .firstOrNull()
    ?.subsections
    ?.filterIsInstance<FunctionNameSubsection>()
    ?.firstOrNull()
    ?.nameMap
    ?.associate { association -> association.idx.toInt() to association.name.name }
    .orEmpty()
