package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.parallel.ParallelPlatform
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import io.github.charlietap.chasm.parallel.parallelPlatform

internal fun loweringParallelism(): ParallelismPolicy {
    return when (parallelPlatform) {
        ParallelPlatform.ANDROID -> ANDROID
        ParallelPlatform.JVM, ParallelPlatform.NATIVE -> JVM
    }
}

private val ANDROID = ParallelismPolicy(minimumWork = 32_000, workPerWorker = 16_000, maximumWorkers = 6)

private val JVM = ParallelismPolicy(minimumWork = 16_000, workPerWorker = 8_000)
