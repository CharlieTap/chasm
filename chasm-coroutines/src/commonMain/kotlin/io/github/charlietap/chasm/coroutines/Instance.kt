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
 * This function uses a heuristic to decide whether the compilation stage of
 * instantiation should run serially or in parallel. It estimates the work from
 * the module's function and instruction counts. When parallel compilation is
 * expected to repay its coordination cost, the largest functions are balanced
 * across a bounded number of workers on
 * [kotlinx.coroutines.Dispatchers.Default]. Allocation, linking, and
 * initialisation remain serial, and compilation errors are reported in module
 * order.
 *
 * The heuristic cannot predict the exact cost of every module or instruction.
 * In particular, total module size is not a reliable measure of compilation
 * work. Data segments, types, and other non-code sections can increase the size
 * of a module without increasing the function compilation work that can be
 * parallelised.
 *
 * When the calling context can suspend, this function is generally the
 * preferred API. If the amount of function code in a module is sufficiently
 * small, it may be worth benchmarking it against the non-suspending
 * [io.github.charlietap.chasm.embedding.instance] function to ascertain which
 * is faster.
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
