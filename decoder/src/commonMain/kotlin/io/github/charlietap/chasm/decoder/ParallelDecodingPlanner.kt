package io.github.charlietap.chasm.decoder

import io.github.charlietap.chasm.decoder.layout.CodeBodyRanges
import io.github.charlietap.chasm.parallel.availableParallelProcessors

internal fun shouldScanParallelLayout(
    moduleSize: Int,
    mode: DecodingMode,
): Boolean = mode == DecodingMode.PARALLEL || moduleSize >= MINIMUM_PARALLEL_MODULE_SIZE

internal fun ParallelDecodingPlanner(
    moduleSize: Int,
    bodies: CodeBodyRanges,
    mode: DecodingMode,
    availableProcessors: Int = availableParallelProcessors(),
): DecodingPlan {
    val workerLimit = minOf(
        bodies.size,
        maxOf(availableProcessors - 1, 1),
        DECODER_WORKER_LIMIT,
    )
    if (
        mode == DecodingMode.SERIAL ||
        bodies.size < MINIMUM_PARALLEL_BODY_COUNT ||
        workerLimit < MINIMUM_PARALLEL_WORKER_COUNT
    ) {
        return DecodingPlan.Serial
    }
    var bodyBytes = 0L
    val scheduledBodies = Array(bodies.size) { bodyIndex ->
        val size = bodies.sizes[bodyIndex].toLong()
        bodyBytes += size
        ScheduledBody(bodyIndex, size + BODY_DECODING_COST)
    }
    if (
        mode == DecodingMode.AUTO &&
        (moduleSize < MINIMUM_PARALLEL_MODULE_SIZE || bodyBytes < MINIMUM_PARALLEL_BODY_BYTES)
    ) {
        return DecodingPlan.Serial
    }
    scheduledBodies.sortWith(
        compareByDescending<ScheduledBody>(ScheduledBody::estimatedCost)
            .thenBy(ScheduledBody::bodyIndex),
    )

    val workerCount = if (mode == DecodingMode.PARALLEL) {
        workerLimit
    } else {
        minOf(
            workerLimit,
            maxOf(MINIMUM_PARALLEL_WORKER_COUNT, (bodyBytes / BODY_BYTES_PER_WORKER).toInt() + 1),
        )
    }
    val assignments = scheduleBodies(scheduledBodies, workerCount)
    return DecodingPlan.Parallel(
        Array(assignments.size) { index ->
            assignments[index].toIntArray().also(IntArray::sort)
        },
    )
}

internal enum class DecodingMode {
    AUTO,
    SERIAL,
    PARALLEL,
}

internal sealed interface DecodingPlan {
    data object Serial : DecodingPlan

    class Parallel(val assignments: Array<IntArray>) : DecodingPlan
}

private fun scheduleBodies(
    bodies: Array<ScheduledBody>,
    workerCount: Int,
): Array<BodyAssignment> {
    val initialCapacity = maxOf((bodies.size + workerCount - 1) / workerCount, 1)
    val assignments = Array(workerCount) { BodyAssignment(initialCapacity) }
    for (body in bodies) {
        var lightestWorker = 0
        for (workerIndex in 1 until assignments.size) {
            if (assignments[workerIndex].estimatedCost < assignments[lightestWorker].estimatedCost) {
                lightestWorker = workerIndex
            }
        }
        assignments[lightestWorker].add(body)
    }
    return assignments
}

private class BodyAssignment(initialCapacity: Int) {

    private var bodyIndices = IntArray(initialCapacity)
    private var size = 0
    var estimatedCost = 0L
        private set

    fun add(body: ScheduledBody) {
        if (size == bodyIndices.size) bodyIndices = bodyIndices.copyOf(bodyIndices.size * 2)
        bodyIndices[size++] = body.bodyIndex
        estimatedCost += body.estimatedCost
    }

    fun toIntArray(): IntArray = bodyIndices.copyOf(size)
}

private class ScheduledBody(
    val bodyIndex: Int,
    val estimatedCost: Long,
)

private const val BODY_DECODING_COST = 64L
private const val BODY_BYTES_PER_WORKER = 64L * 1024L
private const val MINIMUM_PARALLEL_MODULE_SIZE = 100 * 1024
private const val MINIMUM_PARALLEL_BODY_BYTES = 64 * 1024
private const val MINIMUM_PARALLEL_BODY_COUNT = 2
private const val MINIMUM_PARALLEL_WORKER_COUNT = 2
private const val DECODER_WORKER_LIMIT = 6
