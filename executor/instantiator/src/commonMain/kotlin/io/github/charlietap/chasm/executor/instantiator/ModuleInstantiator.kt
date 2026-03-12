package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.compiler.Compiler
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.compat.CompatibilityChecker
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.ir.factory.ModuleFactory
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
        compatibilityChecker = ::CompatibilityChecker,
        moduleFactory = ::ModuleFactory,
        compiler = ::Compiler,
        partialAllocator = ::PartialModuleAllocator,
        allocator = ::ModuleAllocator,
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
    crossinline compatibilityChecker: CompatibilityChecker,
    crossinline moduleFactory: ModuleFactory,
    crossinline compiler: Compiler,
    crossinline partialAllocator: PartialModuleAllocator,
    crossinline allocator: ModuleAllocator,
    crossinline invoker: FunctionInvoker,
    crossinline constantExpressionEvaluator: ConstantExpressionEvaluator,
    crossinline tableInitializer: TableInitializer,
    crossinline memoryInitializer: MemoryInitializer,
): Result<ModuleInstance, ModuleTrapError> = binding {

    compatibilityChecker(module).bind()

    val irModule = compiler(config, moduleFactory(module))
    val context = InstantiationContext(config, store, irModule)
    val partialInstance = partialAllocator(context, imports).bind()

    val tableInitValues = LongArray(irModule.tables.size) { tableIndex ->
        val table = irModule.tables[tableIndex]
        constantExpressionEvaluator(store, partialInstance, table.initExpression).bind()
    }

    val instance = allocator(context, partialInstance, tableInitValues).bind()

    tableInitializer(context, instance).bind()
    memoryInitializer(context, instance).bind()

    irModule.startFunction?.let { function ->
        val address = instance.functionAddresses[function.idx.idx]
        invoker(config, store, address, emptyList()).bind()
    }

    instance
}
