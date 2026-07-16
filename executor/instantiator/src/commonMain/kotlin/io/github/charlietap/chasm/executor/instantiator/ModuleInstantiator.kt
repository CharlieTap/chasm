package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocationJournal
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.type.TypeAllocator
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.invoker.drop.ModuleInstanceDropper
import io.github.charlietap.chasm.executor.invoker.drop.ModuleInstanceRollback
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.store.instanceLifetimes
import io.github.charlietap.chasm.ast.module.Module as ASTModule

typealias ModuleInstantiator = (RuntimeConfig, Store, ASTModule, List<Import>) -> Result<ModuleInstance, ModuleTrapError>
typealias CompiledModuleInstantiator = (RuntimeConfig, Store, CompiledModule, List<Import>) -> Result<ModuleInstance, ModuleTrapError>

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
        compiler = ::ModuleCompiler,
        instantiator = ::ModuleInstantiator,
    )

fun ModuleInstantiator(
    config: RuntimeConfig,
    store: Store,
    module: CompiledModule,
    imports: List<Import>,
): Result<ModuleInstance, ModuleTrapError> =
    ModuleInstantiator(
        config = config,
        store = store,
        module = module,
        imports = imports,
        partialAllocator = ::PartialModuleAllocator,
        allocator = ::ModuleAllocator,
        typeAllocator = ::TypeAllocator,
        invoker = ::FunctionInvoker,
        constantExpressionEvaluator = ::ConstantExpressionEvaluator,
        tableInitializer = ::TableInitializer,
        memoryInitializer = ::MemoryInitializer,
        rollback = ::ModuleInstanceRollback,
    )

internal inline fun ModuleInstantiator(
    config: RuntimeConfig,
    store: Store,
    module: ASTModule,
    imports: List<Import>,
    crossinline compiler: ModuleCompiler,
    crossinline instantiator: CompiledModuleInstantiator,
): Result<ModuleInstance, ModuleTrapError> = binding {
    val compiled = compiler(config, module).bind()
    instantiator(config, store, compiled, imports).bind()
}

internal inline fun ModuleInstantiator(
    config: RuntimeConfig,
    store: Store,
    module: CompiledModule,
    imports: List<Import>,
    crossinline partialAllocator: PartialModuleAllocator,
    crossinline allocator: ModuleAllocator,
    crossinline typeAllocator: TypeAllocator,
    crossinline invoker: FunctionInvoker,
    crossinline constantExpressionEvaluator: ConstantExpressionEvaluator,
    crossinline tableInitializer: TableInitializer,
    crossinline memoryInitializer: MemoryInitializer,
    crossinline rollback: ModuleInstanceDropper,
): Result<ModuleInstance, ModuleTrapError> {
    val irModule = module.module
    val runtimeTypes = typeAllocator(irModule, store)

    val context = InstantiationContext(config, store, irModule, runtimeTypes)
    val instance = ModuleInstance(runtimeTypes)
    val journal = ModuleAllocationJournal(instance)
    var allocationMayHaveEscaped = false
    context.instance = instance

    val result: Result<ModuleInstance, ModuleTrapError> = binding {
        val partialInstance = partialAllocator(context, instance, imports, journal).bind()

        val tableInitValues = LongArray(irModule.tables.size) { tableIndex ->
            val table = irModule.tables[tableIndex]
            constantExpressionEvaluator(store, partialInstance, table.initExpression).bind()
        }

        allocator(context, partialInstance, tableInitValues).bind()

        val lifetimes = store.instanceLifetimes()
        val allocation = journal.allocation(lifetimes.providers(instance))
        lifetimes.register(instance, allocation)

        tableInitializer(context, instance) { tableIndex ->
            if (journal.isImported(tableIndex)) allocationMayHaveEscaped = true
        }.bind()
        memoryInitializer(context, instance).bind()

        irModule.startFunction?.let { function ->
            if (imports.any { import -> import.externalValue !is ExternalValue.Memory }) {
                allocationMayHaveEscaped = true
            }
            val address = instance.functionAddresses[function.idx.idx]
            invoker(config, store, instance, address, emptyList()).bind()
        }

        lifetimes.publish(instance, allocationMayHaveEscaped)
        instance
    }

    if (result.isErr && instance.runtimeInstanceId != null) {
        val lifetimes = store.instanceLifetimes()
        if (instance.allocation == null) {
            val allocation = journal.allocation(lifetimes.providers(instance))
            lifetimes.register(instance, allocation)
        }
        if (allocationMayHaveEscaped) {
            lifetimes.abandon(instance)
        } else {
            val rollbackResult = rollback(store, instance)
            if (rollbackResult.isErr) return rollbackResult.map { instance }
        }
    }

    return result
}
