package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.compat.CompatibilityChecker
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.asPredecodingContext
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.ir.factory.ModuleFactory
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.optimiser.Optimiser
import io.github.charlietap.chasm.predecoder.ExpressionPredecoder
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.runtime.Arity
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.ast.module.Module as ASTModule
import io.github.charlietap.chasm.runtime.function.Expression as RuntimeExpression

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
        optimiser = ::Optimiser,
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
    module: ASTModule,
    imports: List<Import>,
    crossinline compatibilityChecker: CompatibilityChecker,
    crossinline moduleFactory: ModuleFactory,
    crossinline optimiser: Optimiser,
    crossinline partialAllocator: PartialModuleAllocator,
    crossinline allocator: ModuleAllocator,
    crossinline invoker: FunctionInvoker,
    crossinline evaluator: ExpressionEvaluator,
    crossinline tableInitializer: TableInitializer,
    crossinline memoryInitializer: MemoryInitializer,
    crossinline expressionPredecoder: Predecoder<Expression, RuntimeExpression>,
): Result<ModuleInstance, ModuleTrapError> = binding {

    compatibilityChecker(module).bind()

    val irModule = optimiser(config, moduleFactory(module))
    val context = InstantiationContext(config, store, irModule)
    val partialInstance = partialAllocator(context, imports).bind()

    val tableInitValues = LongArray(irModule.tables.size) { tableIndex ->
        val table = irModule.tables[tableIndex]
        val initExpression = expressionPredecoder(context.asPredecodingContext(), table.initExpression).bind()
        evaluator(config, store, partialInstance, initExpression, Arity.Return(1)).bind() ?: 0L
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
