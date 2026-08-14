package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.asErr
import com.github.michaelbull.result.get
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.allocation.ParallelModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.type.TypeAllocator
import io.github.charlietap.chasm.executor.instantiator.compat.CompatibilityChecker
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.ast.module.Module as ASTModule

suspend fun ParallelModuleInstantiator(
    config: RuntimeConfig,
    store: Store,
    module: ASTModule,
    imports: List<Import>,
    taskExecutor: ParallelTaskExecutor,
): Result<ModuleInstance, ModuleTrapError> {
    val preparation = PrepareModuleInstantiation(
        config = config,
        store = store,
        module = module,
        imports = imports,
        compatibilityChecker = ::CompatibilityChecker,
        partialAllocator = ::PartialModuleAllocator,
        typeAllocator = ::TypeAllocator,
        constantExpressionEvaluator = ::ConstantExpressionEvaluator,
    )
    if (preparation.isErr) return preparation.asErr()
    val prepared = checkNotNull(preparation.get())
    val instance = ParallelModuleAllocator(
        prepared.context,
        prepared.partialInstance,
        prepared.tableInitValues,
        taskExecutor,
    )
    if (instance.isErr) return instance.asErr()
    val instantiated = checkNotNull(instance.get())
    return CompleteModuleInstantiation(
        prepared.context,
        instantiated,
        ::FunctionInvoker,
        ::TableInitializer,
        ::MemoryInitializer,
    ).map { instantiated }
}
