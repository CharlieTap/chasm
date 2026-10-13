@file:OptIn(ExperimentalAtomicApi::class)

package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.decoder.layout.ModuleLayoutScanner
import io.github.charlietap.chasm.decoder.parallel.BodyWorker
import io.github.charlietap.chasm.decoder.parallel.ObserverState
import io.github.charlietap.chasm.decoder.parallel.ParallelDecoderContext
import io.github.charlietap.chasm.decoder.parallel.decodeSections
import io.github.charlietap.chasm.decoder.parallel.replayBatch
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import io.github.charlietap.chasm.parallel.WorkCursor
import io.github.charlietap.chasm.parallel.availableParallelProcessors
import io.github.charlietap.chasm.parallel.claimEach
import io.github.charlietap.chasm.parallel.executeWorkers
import kotlin.concurrent.atomics.ExperimentalAtomicApi

suspend fun ParallelWasmModuleDecoder(
    config: ModuleConfig,
    bytes: ByteArray,
    taskExecutor: ParallelTaskExecutor,
): Result<Module, ModuleDecoderError> =
    ParallelWasmModuleDecoder(config, bytes, taskExecutor, DecodingMode.AUTO)

/**
 * Decodes [bytes] while [observer] follows along. The observer is not called at all for a
 * binary whose section framing cannot be read up front, or which lacks a function type for
 * one of its bodies.
 */
suspend fun ParallelWasmModuleDecoder(
    config: ModuleConfig,
    bytes: ByteArray,
    taskExecutor: ParallelTaskExecutor,
    observer: ModuleDecodeObserver,
): Result<Module, ModuleDecoderError> =
    ParallelWasmModuleDecoder(config, bytes, taskExecutor, DecodingMode.AUTO, observer = observer)

internal suspend fun ParallelWasmModuleDecoder(
    config: ModuleConfig,
    bytes: ByteArray,
    taskExecutor: ParallelTaskExecutor,
    mode: DecodingMode,
    availableProcessors: Int = availableParallelProcessors(),
    observer: ModuleDecodeObserver? = null,
    policy: ParallelismPolicy = decodingParallelism(observed = observer != null),
): Result<Module, ModuleDecoderError> {
    if (observer == null && !shouldScanParallelLayout(bytes.size, mode, policy)) return WasmModuleDecoder(config, bytes)
    val ranges = ModuleLayoutScanner(bytes) ?: return WasmModuleDecoder(config, bytes)
    val workerCount = selectDecodingWorkers(ranges, mode, policy, availableProcessors)
    if (workerCount == 1 && observer == null) return WasmModuleDecoder(config, bytes)

    val context = ParallelDecoderContext(config, bytes, ranges, batchBodies(ranges, workerCount), observer)
    val results = taskExecutor.executeWorkers(workerCount) { decodeModule(context) }
    context.failures.error?.let { error -> return Err(error) }

    val unobserved = results.flatMap(DecodeTaskResult::unobserved)
    if (unobserved.isNotEmpty() && observer != null && context.observerState.load() == ObserverState.FOLLOWING) {
        val cursor = WorkCursor(unobserved.size)
        taskExecutor.executeWorkers(minOf(workerCount, unobserved.size)) {
            val bodyObserver = observer.bodyObserver()
            claimEach(cursor) { claimed -> replayBatch(context, unobserved[claimed], bodyObserver) }
        }
    }

    context.builder.functionBodies(List(ranges.size) { index -> checkNotNull(context.decodedBodies[index]) })
    return context.builder.build(results.any(DecodeTaskResult::requiresDataCount))
}

private class DecodeTaskResult(
    val requiresDataCount: Boolean,
    // batches decoded before the observer was ready that the worker never got back to
    val unobserved: List<Int>,
)

private fun ParallelTaskScope.decodeModule(context: ParallelDecoderContext): DecodeTaskResult {
    val decodedSections = context.sectionsClaimed.compareAndSet(expectedValue = false, newValue = true)
    if (decodedSections) decodeSections(context)

    val worker = BodyWorker(context)
    claimEach(context.cursor) { batch -> worker.decode(batch) }

    return DecodeTaskResult(
        requiresDataCount = worker.requiresDataCount || (decodedSections && context.sectionContext.requiresDataCount),
        unobserved = worker.finish(),
    )
}
