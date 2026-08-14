package io.github.charlietap.chasm.coroutines

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.coroutines.internal.DefaultCoroutineParallelTaskExecutor
import io.github.charlietap.chasm.embedding.error.ChasmError.DecodeError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.internal._module as internalModule

/**
 * Decodes a Wasm module with potential parallelism.
 *
 * This function uses a heuristic to decide whether decoding should run
 * serially or in parallel. It estimates the work from the number and encoded
 * sizes of the module's function bodies. When parallel decoding is expected to
 * repay its coordination cost, those bodies are balanced across a bounded
 * number of workers on [kotlinx.coroutines.Dispatchers.Default], while the
 * remaining sections are decoded serially.
 *
 * The heuristic cannot predict the exact cost of every module. In particular,
 * total module size is not a reliable measure of parallel decoding work. Data,
 * custom, and other non-code sections can increase the size of a module without
 * increasing the function-body work that can be parallelised.
 *
 * When the calling context can suspend, this function is generally the
 * preferred API. If the amount of function code in a module is sufficiently
 * small, it may be worth benchmarking it against the non-suspending
 * [io.github.charlietap.chasm.embedding.module] function to ascertain which
 * is faster.
 */
@OptIn(InternalChasmApi::class)
suspend fun module(
    bytes: ByteArray,
    config: ModuleConfig = ModuleConfig(),
): ChasmResult<Module, DecodeError> =
    internalModule(
        bytes = bytes,
        config = config,
        taskExecutor = DefaultCoroutineParallelTaskExecutor,
    )
