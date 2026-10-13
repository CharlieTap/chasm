package io.github.charlietap.chasm.decoder

import io.github.charlietap.chasm.parallel.ParallelPlatform
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import io.github.charlietap.chasm.parallel.parallelPlatform

/**
 * How many bytes of function body make decoding on several threads worthwhile. Each byte
 * costs more when [observed], that is when a [ModuleDecodeObserver] follows the bodies.
 *
 * Android is slow to bring idle cores up to speed, which costs around ten milliseconds
 * however many workers take part. Only a large module is worth sharing there, and a handful
 * of workers is slower than one thread. Kotlin/Native stops getting faster beyond about
 * eight workers.
 */
internal fun decodingParallelism(observed: Boolean): ParallelismPolicy = when (parallelPlatform) {
    ParallelPlatform.JVM -> if (observed) JVM_OBSERVED else JVM
    ParallelPlatform.ANDROID -> if (observed) ANDROID_OBSERVED else ANDROID
    ParallelPlatform.NATIVE -> NATIVE
}

private val JVM = ParallelismPolicy(minimumWork = 64 * 1024L, workPerWorker = 16 * 1024L)
private val JVM_OBSERVED = ParallelismPolicy(minimumWork = 24 * 1024L, workPerWorker = 8 * 1024L)
private val ANDROID = ParallelismPolicy(minimumWork = 560 * 1024L, workPerWorker = 64 * 1024L, minimumWorkers = 4)
private val ANDROID_OBSERVED = ParallelismPolicy(minimumWork = 400 * 1024L, workPerWorker = 48 * 1024L, minimumWorkers = 4)
private val NATIVE = ParallelismPolicy(minimumWork = 64 * 1024L, workPerWorker = 24 * 1024L, maximumWorkers = 8)
