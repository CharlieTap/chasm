package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.Import
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

typealias ModuleInstantiator = (Store, Module, List<Import>) -> Result<ModuleInstance, ModuleTrapError>

fun ModuleInstantiator(
    store: Store,
    module: Module,
    imports: List<Import>,
): Result<ModuleInstance, ModuleTrapError> =
    ModuleInstantiator(
        store = store,
        module = module,
        imports = imports,
        partialAllocator = ::PartialModuleAllocator,
        allocator = ::ModuleAllocator,
        invoker = ::FunctionInvoker,
        evaluator = ::ExpressionEvaluator,
        tableInitializer = ::TableInitializer,
        memoryInitializer = ::MemoryInitializer,
    )

internal fun ModuleInstantiator(
    store: Store,
    module: Module,
    imports: List<Import>,
    partialAllocator: PartialModuleAllocator,
    allocator: ModuleAllocator,
    invoker: FunctionInvoker,
    evaluator: ExpressionEvaluator,
    tableInitializer: TableInitializer,
    memoryInitializer: MemoryInitializer,
): Result<ModuleInstance, ModuleTrapError> = binding {

    val context = InstantiationContext(store, module)

    val partialInstance = partialAllocator(context, imports).bind()

    val tableInitValues = module.tables.map { table ->
        evaluator(store, partialInstance, table.initExpression, Arity.Return(1)).bind() as ReferenceValue
    }

    val instance = allocator(context, partialInstance, tableInitValues).bind()

    tableInitializer(store, instance, module).bind()
    memoryInitializer(store, instance, module).bind()

    module.startFunction?.let { function ->
        val address = instance.functionAddresses[function.idx.idx.toInt()]
        invoker(store, address, emptyList()).bind()
    }

    instance
}
