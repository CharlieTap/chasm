package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.diagnostic.CompilerDiagnostics
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
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
import io.github.charlietap.chasm.ast.module.Module as ASTModule

typealias ModuleInstantiator = (RuntimeConfig, Store, ASTModule, List<Import>) -> Result<ModuleInstance, ModuleTrapError>

fun ModuleInstantiator(
    config: RuntimeConfig,
    store: Store,
    module: ASTModule,
    imports: List<Import>,
): Result<ModuleInstance, ModuleTrapError> =
    ModuleInstantiator(
        config = config,
        store = store,
        module = module,
        imports = imports,
        compilerDiagnostics = null,
        compatibilityChecker = ::CompatibilityChecker,
        partialAllocator = ::PartialModuleAllocator,
        allocator = ::ModuleAllocator,
        typeAllocator = ::TypeAllocator,
        invoker = ::FunctionInvoker,
        constantExpressionEvaluator = ::ConstantExpressionEvaluator,
        tableInitializer = ::TableInitializer,
        memoryInitializer = ::MemoryInitializer,
    )

fun ModuleInstantiator(
    config: RuntimeConfig,
    store: Store,
    module: ASTModule,
    imports: List<Import>,
    compilerDiagnostics: CompilerDiagnostics,
): Result<ModuleInstance, ModuleTrapError> =
    ModuleInstantiator(
        config = config,
        store = store,
        module = module,
        imports = imports,
        compilerDiagnostics = compilerDiagnostics,
        compatibilityChecker = ::CompatibilityChecker,
        partialAllocator = ::PartialModuleAllocator,
        allocator = ::ModuleAllocator,
        typeAllocator = ::TypeAllocator,
        invoker = ::FunctionInvoker,
        constantExpressionEvaluator = ::ConstantExpressionEvaluator,
        tableInitializer = ::TableInitializer,
        memoryInitializer = ::MemoryInitializer,
    )

internal inline fun ModuleInstantiator(
    config: RuntimeConfig,
    store: Store,
    module: ASTModule,
    imports: List<Import>,
    compilerDiagnostics: CompilerDiagnostics?,
    crossinline compatibilityChecker: CompatibilityChecker,
    crossinline partialAllocator: PartialModuleAllocator,
    crossinline allocator: ModuleAllocator,
    crossinline typeAllocator: TypeAllocator,
    crossinline invoker: FunctionInvoker,
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
        constantExpressionEvaluator = constantExpressionEvaluator,
    ).bind()
    val instance = allocator(
        preparation.context,
        preparation.partialInstance,
        preparation.tableInitValues,
        compilerDiagnostics,
    ).bind()
    CompleteModuleInstantiation(
        preparation.context,
        instance,
        invoker,
        tableInitializer,
        memoryInitializer,
    ).bind()
    instance
}

internal inline fun PrepareModuleInstantiation(
    config: RuntimeConfig,
    store: Store,
    module: ASTModule,
    imports: List<Import>,
    crossinline compatibilityChecker: CompatibilityChecker,
    crossinline partialAllocator: PartialModuleAllocator,
    crossinline typeAllocator: TypeAllocator,
    crossinline constantExpressionEvaluator: ConstantExpressionEvaluator,
): Result<ModuleInstantiationPreparation, ModuleTrapError> = binding {

    compatibilityChecker(module).bind()

    val runtimeTypes = typeAllocator(module, store)

    val context = InstantiationContext(config, store, module, runtimeTypes)
    val partialInstance = partialAllocator(context, imports).bind()

    val tableInitValues = LongArray(module.tables.size) { tableIndex ->
        val table = module.tables[tableIndex]
        constantExpressionEvaluator(store, partialInstance, context.types, table.initExpression).bind()
    }

    ModuleInstantiationPreparation(context, partialInstance, tableInitValues)
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

    context.module.startFunction?.let { function ->
        val address = instance.functionAddresses[function.idx.toInt()]
        invoker(context.config, context.store, instance, address, emptyList()).bind()
    }
}

internal class ModuleInstantiationPreparation(
    val context: InstantiationContext,
    val partialInstance: ModuleInstance,
    val tableInitValues: LongArray,
)
