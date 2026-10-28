package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.parallel.ParallelPlatform
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import io.github.charlietap.chasm.parallel.parallelPlatform

internal fun compilationParallelism(): ParallelismPolicy = when (parallelPlatform) {
    ParallelPlatform.JVM -> JVM
    ParallelPlatform.ANDROID -> ANDROID
    ParallelPlatform.NATIVE -> NATIVE
}

private val JVM = ParallelismPolicy(minimumWork = 8_000, workPerWorker = 2_000)
private val ANDROID = ParallelismPolicy(minimumWork = 64_000, workPerWorker = 4_000)
private val NATIVE = ParallelismPolicy(minimumWork = 8_000, workPerWorker = 2_000)
