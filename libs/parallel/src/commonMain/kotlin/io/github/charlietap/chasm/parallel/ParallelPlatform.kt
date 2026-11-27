package io.github.charlietap.chasm.parallel

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
expect fun availableParallelProcessors(): Int

@InternalChasmApi
enum class ParallelPlatform {
    JVM,
    ANDROID,
    NATIVE,
}

@InternalChasmApi
expect val parallelPlatform: ParallelPlatform
