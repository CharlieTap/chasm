package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

typealias ModuleInstantiator = (Store, Module, List<ExternalValue>) -> Result<ModuleInstance, ModuleTrapError>

fun ModuleInstantiator(
    store: Store,
    module: Module,
    imports: List<ExternalValue>,
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
    imports: List<ExternalValue>,
    partialAllocator: PartialModuleAllocator,
    allocator: ModuleAllocator,
    invoker: FunctionInvoker,
    evaluator: ExpressionEvaluator,
    tableInitializer: TableInitializer,
    memoryInitializer: MemoryInitializer,
): Result<ModuleInstance, ModuleTrapError> = binding {

    if (module.imports.size != imports.size) {
        Err(InstantiationError.MissingImport).bind<ModuleInstance>()
    }

    val partialInstance = partialAllocator(store, module, imports).bind()

    val globalInitValues = module.globals.mapNotNull { global ->
        evaluator(store, partialInstance, global.initExpression, Arity.Return(1)).bind()
    }

    val tableInitValues = module.tables.map { table ->
        evaluator(store, partialInstance, table.initExpression, Arity.Return(1)).bind() as ReferenceValue
    }

    val elementSegmentReferences = module.elementSegments.map { segment ->
        segment.initExpressions.map { initExpression ->
            evaluator(store, partialInstance, initExpression, Arity.Return(1)).bind() as ReferenceValue
        }
    }

    val instance = allocator(store, module, partialInstance, globalInitValues, tableInitValues, elementSegmentReferences).bind()

    tableInitializer(store, instance, module).bind()
    memoryInitializer(store, instance, module).bind()

    module.startFunction?.let { function ->
        val address = instance.functionAddresses[function.idx.idx.toInt()]
        invoker(store, address, emptyList()).bind()
    }

    instance
}
