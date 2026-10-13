package io.github.charlietap.chasm.validator

import io.github.charlietap.chasm.parallel.ParallelPlatform
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import io.github.charlietap.chasm.parallel.parallelPlatform

/**
 * How many instructions and locals make validating on several threads worthwhile.
 *
 * Android is slow to bring idle cores up to speed, so only a large module is worth sharing
 * there and a handful of workers is slower than one thread.
 */
internal fun validationParallelism(): ParallelismPolicy = when (parallelPlatform) {
    ParallelPlatform.JVM -> JVM
    ParallelPlatform.ANDROID -> ANDROID
    ParallelPlatform.NATIVE -> NATIVE
}

private val JVM = ParallelismPolicy(minimumWork = 20_000, workPerWorker = 5_000)
private val ANDROID = ParallelismPolicy(minimumWork = 250_000, workPerWorker = 28_000, minimumWorkers = 4)
private val NATIVE = ParallelismPolicy(minimumWork = 24_000, workPerWorker = 12_000)
