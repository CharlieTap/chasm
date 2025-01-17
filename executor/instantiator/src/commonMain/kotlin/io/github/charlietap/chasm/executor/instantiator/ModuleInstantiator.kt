package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.instantiator.predecoding.ExpressionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.Import
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.executor.runtime.function.Expression as RuntimeExpression

typealias ModuleInstantiator = (RuntimeConfig, Store, Module, List<Import>) -> Result<ModuleInstance, ModuleTrapError>

fun ModuleInstantiator(
    config: RuntimeConfig,
    store: Store,
    module: Module,
    imports: List<Import>,
): Result<ModuleInstance, ModuleTrapError> =
    ModuleInstantiator(
        config = config,
        store = store,
        module = module,
        imports = imports,
        partialAllocator = ::PartialModuleAllocator,
        allocator = ::ModuleAllocator,
        invoker = ::FunctionInvoker,
        evaluator = ::ExpressionEvaluator,
        tableInitializer = ::TableInitializer,
        memoryInitializer = ::MemoryInitializer,
        expressionPredecoder = ::ExpressionPredecoder,
    )

internal inline fun ModuleInstantiator(
    config: RuntimeConfig,
    store: Store,
    module: Module,
    imports: List<Import>,
    crossinline partialAllocator: PartialModuleAllocator,
    crossinline allocator: ModuleAllocator,
    crossinline invoker: FunctionInvoker,
    crossinline evaluator: ExpressionEvaluator,
    crossinline tableInitializer: TableInitializer,
    crossinline memoryInitializer: MemoryInitializer,
    crossinline expressionPredecoder: Predecoder<Expression, RuntimeExpression>,
): Result<ModuleInstance, ModuleTrapError> = binding {

    val context = InstantiationContext(config, store, module)
    val partialInstance = partialAllocator(context, imports).bind()

    val tableInitValues = module.tables.map { table ->
        val initExpression = expressionPredecoder(context, table.initExpression).bind()
        evaluator(config, store, partialInstance, initExpression, Arity.Return(1)).bind() as ReferenceValue
    }

    val instance = allocator(context, partialInstance, tableInitValues).bind()

    tableInitializer(context, instance).bind()
    memoryInitializer(context, instance).bind()

    module.startFunction?.let { function ->
        val address = instance.functionAddresses[function.idx.idx.toInt()]
        invoker(config, store, address, emptyList()).bind()
    }

    instance
}
