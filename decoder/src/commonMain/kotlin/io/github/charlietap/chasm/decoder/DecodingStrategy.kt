package io.github.charlietap.chasm.decoder

import io.github.charlietap.chasm.decoder.layout.CodeBodyRanges
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import io.github.charlietap.chasm.parallel.WorkBatches
import io.github.charlietap.chasm.parallel.availableParallelProcessors
import io.github.charlietap.chasm.parallel.selectWorkerCount
import io.github.charlietap.chasm.parallel.singleBatch
import io.github.charlietap.chasm.parallel.workBatches

internal fun shouldScanParallelLayout(
    moduleSize: Int,
    mode: DecodingMode,
    policy: ParallelismPolicy,
): Boolean = mode == DecodingMode.PARALLEL || moduleSize >= policy.minimumWork

internal fun selectDecodingWorkers(
    bodies: CodeBodyRanges,
    mode: DecodingMode,
    policy: ParallelismPolicy,
    availableProcessors: Int = availableParallelProcessors(),
): Int = when (mode) {
    DecodingMode.SERIAL -> 1
    DecodingMode.PARALLEL -> minOf(bodies.size, availableProcessors, policy.maximumWorkers).coerceAtLeast(1)
    DecodingMode.AUTO -> {
        var bodyBytes = 0L
        var largestBody = 0
        for (size in bodies.sizes) {
            bodyBytes += size
            if (size > largestBody) largestBody = size
        }
        selectWorkerCount(bodyBytes, largestBody.toLong(), bodies.size, policy, availableProcessors)
    }
}

internal fun batchBodies(
    bodies: CodeBodyRanges,
    workerCount: Int,
): WorkBatches = if (workerCount == 1) {
    singleBatch(bodies.size)
} else {
    workBatches(bodies.size, BATCH_BYTES) { bodyIndex -> bodies.sizes[bodyIndex] }
}

internal enum class DecodingMode {
    AUTO,
    SERIAL,
    PARALLEL,
}

private const val BATCH_BYTES = 8 * 1024
