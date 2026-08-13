package io.github.charlietap.chasm.coroutines

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.internal._instance as internalInstance

/**
 * Instantiates a Wasm module with potential parallelism.
 *
 * This function can parallelise the compilation stage of instantiation by
 * compiling independent Wasm functions on
 * [kotlinx.coroutines.Dispatchers.Default]. Allocation, linking, and
 * initialisation remain serial, and compilation errors are reported in module
 * order.
 *
 * The compiler estimates work from function and instruction counts, balances
 * the largest functions across a bounded number of workers, and compiles in
 * parallel only when its estimated saving is greater than the coordination
 * cost. This is a heuristic and cannot account perfectly for the cost of every
 * instruction or the current load on the host.
 *
 * Small modules generally contain too little compilation work to repay the
 * cost of coroutine scheduling, so the automatic policy keeps their
 * compilation serial. As a rough rule of thumb, modules smaller than 4 KiB may
 * be faster to instantiate with the non-suspending
 * [io.github.charlietap.chasm.embedding.instance] function.
 *
 * Module size is only an approximation of compilation work. Data segments,
 * types, and other non-code sections can increase the size of a module without
 * increasing the function compilation work that this function parallelises.
 */
@OptIn(InternalChasmApi::class)
suspend fun instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    config: RuntimeConfig = RuntimeConfig(),
): ChasmResult<Instance, ChasmError.ExecutionError> {
    return internalInstance(
        store = store,
        module = module,
        imports = imports,
        config = config,
        taskExecutor = CoroutineParallelTaskExecutor,
    )
}
