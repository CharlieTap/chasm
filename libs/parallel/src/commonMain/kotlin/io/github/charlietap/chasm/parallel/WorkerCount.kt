package io.github.charlietap.chasm.parallel

/**
 * How many workers [totalWork] is worth, where one means it is not worth sharing. The
 * [largestItem] occupies one worker whatever the others do, so only the rest justifies more.
 */
fun selectWorkerCount(
    totalWork: Long,
    largestItem: Long,
    itemCount: Int,
    policy: ParallelismPolicy,
    availableProcessors: Int = availableParallelProcessors(),
): Int {
    if (totalWork < policy.minimumWork) return 1

    val workerLimit = minOf(availableProcessors, policy.maximumWorkers, itemCount)
    val additionalWorkers = (totalWork - largestItem) / policy.workPerWorker
    val workerCount = if (additionalWorkers >= workerLimit) workerLimit else additionalWorkers.toInt() + 1

    return if (workerCount < maxOf(policy.minimumWorkers, 2)) 1 else workerCount
}
