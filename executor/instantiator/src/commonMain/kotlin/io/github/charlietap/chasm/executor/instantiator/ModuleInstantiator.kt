package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.checkStoreLimits
import io.github.charlietap.chasm.executor.instantiator.allocation.data.DataPayloadPreparer
import io.github.charlietap.chasm.executor.instantiator.allocation.type.TypeAllocator
import io.github.charlietap.chasm.executor.instantiator.compat.CompatibilityChecker
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store

typealias ModuleInstantiator = (
    RuntimeConfig,
    Store,
    ModuleDeclarations,
    List<Import>,
    ModuleCodeInstaller,
) -> Result<ModuleInstance, ModuleTrapError>

fun ModuleInstantiator(
    config: RuntimeConfig,
    store: Store,
    module: ModuleDeclarations,
    imports: List<Import>,
    codeInstaller: ModuleCodeInstaller,
): Result<ModuleInstance, ModuleTrapError> =
    ModuleInstantiator(
        config = config,
        store = store,
        module = module,
        imports = imports,
        codeInstaller = codeInstaller,
        invoker = ::FunctionInvoker,
        compatibilityChecker = ::CompatibilityChecker,
        partialAllocator = ::PartialModuleAllocator,
        allocator = ::ModuleAllocator,
        typeAllocator = ::TypeAllocator,
        dataPayloadPreparer = ::DataPayloadPreparer,
        constantExpressionEvaluator = ::ConstantExpressionEvaluator,
        tableInitializer = ::TableInitializer,
        memoryInitializer = ::MemoryInitializer,
    )

internal inline fun ModuleInstantiator(
    config: RuntimeConfig,
    store: Store,
    module: ModuleDeclarations,
    imports: List<Import>,
    noinline codeInstaller: ModuleCodeInstaller,
    crossinline invoker: FunctionInvoker,
    crossinline compatibilityChecker: CompatibilityChecker,
    crossinline partialAllocator: PartialModuleAllocator,
    crossinline allocator: ModuleAllocator,
    crossinline typeAllocator: TypeAllocator,
    crossinline dataPayloadPreparer: DataPayloadPreparer,
    crossinline constantExpressionEvaluator: ConstantExpressionEvaluator,
    crossinline tableInitializer: TableInitializer,
    crossinline memoryInitializer: MemoryInitializer,
): Result<ModuleInstance, ModuleTrapError> = binding {
    val preparation = PrepareModuleInstantiation(
        config = config,
        store = store,
        module = module,
        imports = imports,
        compatibilityChecker = compatibilityChecker,
        partialAllocator = partialAllocator,
        typeAllocator = typeAllocator,
        dataPayloadPreparer = dataPayloadPreparer,
        constantExpressionEvaluator = constantExpressionEvaluator,
    ).bind()
    try {
        val instance = allocator(
            preparation.context,
            preparation.partialInstance,
            preparation.tableInitValues,
            codeInstaller,
        ).bind()
        CompleteModuleInstantiation(
            preparation.context,
            instance,
            invoker,
            tableInitializer,
            memoryInitializer,
        ).bind()
        instance
    } finally {
        preparation.context.data.close()
    }
}

internal inline fun PrepareModuleInstantiation(
    config: RuntimeConfig,
    store: Store,
    module: ModuleDeclarations,
    imports: List<Import>,
    crossinline compatibilityChecker: CompatibilityChecker,
    crossinline partialAllocator: PartialModuleAllocator,
    crossinline typeAllocator: TypeAllocator,
    crossinline dataPayloadPreparer: DataPayloadPreparer,
    crossinline constantExpressionEvaluator: ConstantExpressionEvaluator,
): Result<ModuleInstantiationPreparation, ModuleTrapError> = binding {

    compatibilityChecker(module).bind()
    checkStoreLimits(store, module).bind()
    val data = dataPayloadPreparer(module).bind()

    try {
        val runtimeTypes = typeAllocator(module, store)

        val context = InstantiationContext(config, store, module, runtimeTypes, data)
        val partialInstance = partialAllocator(context, imports).bind()

        val tableInitValues = LongArray(module.tables.size) { tableIndex ->
            val table = module.tables[tableIndex]
            constantExpressionEvaluator(store, partialInstance, context.types, table.initExpression).bind()
        }

        ModuleInstantiationPreparation(context, partialInstance, tableInitValues)
    } catch (failure: Throwable) {
        data.close()
        throw failure
    }
}

internal inline fun CompleteModuleInstantiation(
    context: InstantiationContext,
    instance: ModuleInstance,
    crossinline invoker: FunctionInvoker,
    crossinline tableInitializer: TableInitializer,
    crossinline memoryInitializer: MemoryInitializer,
): Result<Unit, ModuleTrapError> = binding {
    tableInitializer(context, instance).bind()
    memoryInitializer(context, instance).bind()
    context.data.close()

    context.module.startFunction?.let { function ->
        val address = instance.functionAddresses[function.toInt()]
        invoker(context.config, context.store, instance, address, emptyList()).bind()
    }
}

internal class ModuleInstantiationPreparation(
    val context: InstantiationContext,
    val partialInstance: ModuleInstance,
    val tableInitValues: LongArray,
)
