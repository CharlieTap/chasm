package io.github.charlietap.chasm.coroutines

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.ChasmError.ValidationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.internal._validate as internalValidate

/**
 * Validates a Wasm module with potential parallelism.
 *
 * This function uses a heuristic to decide whether function validation should
 * run serially or in parallel. It estimates the work from the module's function
 * instruction and local counts. When parallel validation is expected to repay
 * its coordination cost, the largest functions are balanced across a bounded
 * number of workers on [kotlinx.coroutines.Dispatchers.Default]. Non-function
 * module sections remain serial, and validation errors are reported in module
 * order.
 *
 * The heuristic cannot predict the exact cost of every module or instruction.
 * In particular, total module size is not a reliable measure of function
 * validation work. Data segments, types, and other non-code sections can
 * increase the size of a module without increasing the work that can be
 * parallelised.
 *
 * When the calling context can suspend, this function is generally the
 * preferred API. If the amount of function code in a module is sufficiently
 * small, it may be worth benchmarking it against the non-suspending
 * [io.github.charlietap.chasm.embedding.validate] function to ascertain which
 * is faster.
 */
@OptIn(InternalChasmApi::class)
suspend fun validate(
    module: Module,
): ChasmResult<Module, ValidationError> =
    internalValidate(
        module = module,
        taskExecutor = CoroutineParallelTaskExecutor,
    )
